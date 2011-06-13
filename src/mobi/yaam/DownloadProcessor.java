package mobi.yaam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class DownloadProcessor extends Thread 
{
        public static final String TAG = "DownloadProcessor";

        private Download mDownload;

        private HttpGet mMethod = null;
        private DownloadHandler mHandler;

        protected volatile boolean mStopped = false;
        
        private Object lock = new Object();

        public DownloadProcessor(Download dl, DownloadHandler handler)
        {
                mDownload = dl;
                mHandler = handler;
        }

        public Download getDownload()
        {
                return mDownload;
        }

        private HttpClient getClient()
        {
                /* Set the connection timeout to 10s for our test. */
                HttpParams params = new BasicHttpParams();

                /* Avoid registering the https scheme, and thus initializing
                 * SSLSocketFactory on Android.  Seems to be very heavy for some
                 * reason. */
                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("http",
                  PlainSocketFactory.getSocketFactory(), 80));

                SingleClientConnManager cm =
                  new SingleClientConnManager(params, schemeRegistry);

                return new DefaultHttpClient(cm, params);
        }

        public void run()
        {
                Log.d(TAG, "Connecting...");

                HttpClient cli = getClient();
                HttpGet method;
                
                method = new HttpGet(mDownload.url.toString());
                
                /* It's important that we pause here to check if we've been stopped
                 * already.  Otherwise, we would happily progress, seemingly ignoring
                 * the stop request. */
                if (mStopped == true)
                        return;

                synchronized(lock) {
                        mMethod = method;
                }

                HttpEntity ent = null;
                InputStream in = null;
                OutputStream out = null;

                try {
                        HttpResponse resp = cli.execute(mMethod);

                        if (mStopped == true)
                                return;

                        StatusLine status = resp.getStatusLine();

                        if (status.getStatusCode() != HttpStatus.SC_OK)
                                throw new Exception("HTTP GET failed: " + status);

                        if ((ent = resp.getEntity()) != null)
                        {
                                long len;
                                if ((len = ent.getContentLength()) >= 0)
                                        mHandler.sendSetLength(len);

                                in = ent.getContent();
                                out = new FileOutputStream(mDownload.getDestination());

                                byte[] b = new byte[2048];
                                int n;
                                long bytes = 0;

                                /* Note that for most applications, sending a handler message
                                 * after each read() would be unnecessary.  Instead, a timed
                                 * approach should be utilized to send a message at most every
                                 * x seconds. */
                                while ((n = in.read(b)) >= 0)
                                {
                                        bytes += n;
                                        mHandler.sendOnRecv(bytes);     
                                        out.write(b, 0, n);
                                }
                        }

                        if (mStopped == false)
                                mHandler.sendFinished();
                } catch (Exception e) {
                        /* We expect a SocketException on cancellation.  Any other type of
                         * exception that occurs during cancellation is ignored regardless
                         * as there would be no need to handle it. */
                        if (mStopped == false)
                        {
                                mHandler.sendError(e.toString());
                                Log.e(TAG, "Unexpected error", e);
                        }
                } finally {
                        if (in != null)
                                try { in.close(); } catch (IOException e) {}

                        synchronized(lock) {
                                mMethod = null;
                        }

                        /* Close the socket (if it's still open) and cleanup. */
                        cli.getConnectionManager().shutdown();

                        if (out != null)
                        {
                                try { 
                                        out.close();
                                } catch (IOException e) {
                                        mHandler.sendError("Error writing output: " + e.toString());
                                        return;
                                } finally {
                                        if (mStopped == true)
                                                mDownload.abortCleanup();
                                }
                        }
                }
        }

        /**
         * This method is to be called from a separate thread.  That is, not the
         * one executing run().  When it exits, the download thread should be on
         * its way out (failing a connect or read call and cleaning up).
         */
        public void stopDownload()
        {
                /* As we've written this method, calling it from multiple threads would
                 * be problematic. */
                if (mStopped == true)
                        return;

                /* Too late! */
                if (isAlive() == false)
                        return;

                Log.d(TAG, "Stopping download...");

                /* Flag to instruct the downloading thread to halt at the next
                 * opportunity. */
                mStopped = true;

                /* Interrupt the blocking thread.  This won't break out of a blocking
                 * I/O request, but will break out of a wait or sleep call.  While in
                 * this case we know that no such condition is possible, it is always a
                 * good idea to include an interrupt to avoid assumptions about the
                 * thread in question. */
                interrupt();

                /* A synchronized lock is necessary to avoid catching mMethod in
                 * an uncommitted state from the download thread. */
                synchronized(lock)
                {
                        /* This closes the socket handling our blocking I/O, which will
                         * interrupt the request immediately.  This is not the same as
                         * closing the InputStream yieled by HttpEntity#getContent, as the
                         * stream is synchronized in such a way that would starve our main
                         * thread. */
                        if (mMethod != null)
                                mMethod.abort();
                }

                mHandler.sendAborted();

                Log.d(TAG, "Download stopped.");
        }

        public void stopDownloadThenJoin()
        {
                stopDownload();

                while (true)
                {
                        try {
                                join();
                                break;
                        } catch (InterruptedException e) {}
                }
        }

    public static class Download
    {
        public URL url;
        public boolean directory;
        public File dst;
        public String name;
        public long length;

        public Download(URL url, File dst)
        {
                this.url = url;
                this.dst = dst;

                /* Figure out the filename to save to from the URL.  Note that
                 * it would be better to override once the HTTP server responds,
                 * since a better name will have been provided, possibly after
                 * redirect.  But I don't care right now. */
                if ((directory = dst.isDirectory()) == true)
                {
                        String[] paths = url.getPath().split("/");

                        int n = paths.length;
                        
                        if (n > 0)
                                n--;

                        if (paths[n].length() > 0)
                                name = paths[n];
                        else
                                name = "index.html";
                }
        }
        
        public File getDestination()
        {
                File f;
                
                if (directory == true)
                        f = new File(dst.getAbsolutePath() + File.separator + name);
                else
                        f = dst;
                
                return f;
        }

        /**
         * Delete the destination file, if it exists.
         */
        public void abortCleanup()
        {
                getDestination().delete();
        }
    }   
}
