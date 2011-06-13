/*******************************************************************************
 * Copyright (c) 2011 Cleriot Simon <malgon33@gmail.com>.
 * 
 *    This file is part of YAAM.
 * 
 *     YAAM is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     YAAM is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with YAAM.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package mobi.yaam;

import greendroid.widget.AsyncImageView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class AppsListAdapter extends BaseAdapter{
	List<String> appName=new ArrayList<String>();
	List<String> iconUrl=new ArrayList<String>();
	List<Float> ratings=new ArrayList<Float>();
	List<Float> prices=new ArrayList<Float>();
	
	Map<Integer,AsyncImageView> iconsViews=new HashMap<Integer,AsyncImageView>();
	
	private LayoutInflater mInflater;

	//DrawableManager manager;
	//ImageLoader imageLoader;
	
	 public AppsListAdapter(Context context,List<String> names,List<String> icons,List<Float> listRatings,List<Float> listPrices) {
		 mInflater = LayoutInflater.from(context);
		 appName=names;
		 iconUrl=icons;
		 ratings=listRatings;
		 prices=listPrices;
		 
		 //manager=new DrawableManager();
		 //imageLoader=new ImageLoader(context);
	 }
	
	public int getCount() {
		return appName.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		 ViewHolder holder;
		 if (convertView == null) {
		 convertView = mInflater.inflate(R.layout.appslist, null);
		 holder = new ViewHolder();
		 holder.textAppName = (TextView) convertView.findViewById(R.id.TextViewName);
		 holder.iconView = (AsyncImageView) convertView.findViewById(R.id.ImageViewIcon);
		 
		 
		 
		 convertView.setTag(holder);
		 } else {
		 holder = (ViewHolder) convertView.getTag();
		 }
		 
		 String price="";
		 if(prices.get(position)>0)
			 price="("+prices.get(position)+"Û)";
		 else
			 price="(Free)";
		 
		 holder.textAppName.setText(appName.get(position)+" "+price);
		 
		 //holder.iconView.setTag(iconUrl.get(position));
		 holder.iconView.setDefaultImageResource(R.drawable.defaultappicon);
		 holder.iconView.setUrl(iconUrl.get(position));
		 //imageLoader.DisplayImage(iconUrl.get(position), holder.iconView);
		 
		 
		 
		 float rating=ratings.get(position);
		 if(rating==0)
			 ((RatingBar) convertView.findViewById(R.id.ratingbar)).setVisibility(View.GONE);
		 else
		 {
			 ((RatingBar) convertView.findViewById(R.id.ratingbar)).setVisibility(View.VISIBLE);
			 ((RatingBar) convertView.findViewById(R.id.ratingbar)).setRating(rating);
		 }
		 
		 return convertView;
	}

	static class ViewHolder {
		 TextView textAppName;
		 AsyncImageView iconView;
		 }

	
	
	public static class Utils {
	    public static void CopyStream(InputStream is, OutputStream os)
	    {
	        final int buffer_size=1024;
	        try
	        {
	            byte[] bytes=new byte[buffer_size];
	            for(;;)
	            {
	              int count=is.read(bytes, 0, buffer_size);
	              if(count==-1)
	                  break;
	              os.write(bytes, 0, count);
	            }
	        }
	        catch(Exception ex){}
	    }
	}
	
	/*public class ImageLoader {
	    
	    //the simplest in-memory cache implementation. This should be replaced with something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
	    private HashMap<String, Bitmap> cache=new HashMap<String, Bitmap>();
	    
	    private File cacheDir;
	    
	    public ImageLoader(Context context){
	        //Make the background thead low priority. This way it will not affect the UI performance
	        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
	        
	        //Find the dir to save cached images
	        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
	            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
	        else
	            cacheDir=context.getCacheDir();
	        if(!cacheDir.exists())
	            cacheDir.mkdirs();
	    }
	    
	    final int stub_id=R.drawable.defaultappicon;
	    public void DisplayImage(String url, ImageView imageView)
	    {
	        if(cache.containsKey(url))
	            imageView.setImageBitmap(cache.get(url));
	        else
	        {
	            queuePhoto(url, imageView);
	            imageView.setImageResource(stub_id);
	        }    
	    }
	        
	    private void queuePhoto(String url, ImageView imageView)
	    {
	        //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them. 
	        photosQueue.Clean(imageView);
	        PhotoToLoad p=new PhotoToLoad(url, imageView);
	        synchronized(photosQueue.photosToLoad){
	            photosQueue.photosToLoad.push(p);
	            photosQueue.photosToLoad.notifyAll();
	        }
	        
	        //start thread if it's not started yet
	        if(photoLoaderThread.getState()==Thread.State.NEW)
	            photoLoaderThread.start();
	    }
	    
	    private Bitmap getBitmap(String url) 
	    {
	        //I identify images by hashcode. Not a perfect solution, good for the demo.
	        String filename=String.valueOf(url.hashCode());
	        File f=new File(cacheDir, filename);
	        
	        //from SD cache
	        Bitmap b = decodeFile(f);
	        if(b!=null)
	            return b;
	        
	        //from web
	        try {
	            Bitmap bitmap=null;
	            InputStream is=new URL(url).openStream();
	            OutputStream os = new FileOutputStream(f);
	            Utils.CopyStream(is, os);
	            os.close();
	            bitmap = decodeFile(f);
	            return bitmap;
	        } catch (Exception ex){
	           ex.printStackTrace();
	           return null;
	        }
	    }

	    //decodes image and scales it to reduce memory consumption
	    private Bitmap decodeFile(File f){
	        try {
	            //decode image size
	            BitmapFactory.Options o = new BitmapFactory.Options();
	            o.inJustDecodeBounds = true;
	            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
	            
	            //Find the correct scale value. It should be the power of 2.
	            final int REQUIRED_SIZE=70;
	            int width_tmp=o.outWidth, height_tmp=o.outHeight;
	            int scale=1;
	            while(true){
	                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	                    break;
	                width_tmp/=2;
	                height_tmp/=2;
	                scale++;
	            }
	            
	            //decode with inSampleSize
	            BitmapFactory.Options o2 = new BitmapFactory.Options();
	            o2.inSampleSize=scale;
	            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	        } catch (FileNotFoundException e) {}
	        return null;
	    }
	    
	    //Task for the queue
	    private class PhotoToLoad
	    {
	        public String url;
	        public ImageView imageView;
	        public PhotoToLoad(String u, ImageView i){
	            url=u; 
	            imageView=i;
	        }
	    }
	    
	    PhotosQueue photosQueue=new PhotosQueue();
	    
	    public void stopThread()
	    {
	        photoLoaderThread.interrupt();
	    }
	    
	    //stores list of photos to download
	    class PhotosQueue
	    {
	        private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();
	        
	        //removes all instances of this ImageView
	        public void Clean(ImageView image)
	        {
	            for(int j=0 ;j<photosToLoad.size();){
	                if(photosToLoad.get(j).imageView==image)
	                    photosToLoad.remove(j);
	                else
	                    ++j;
	            }
	        }
	    }
	    
	    class PhotosLoader extends Thread {
	        public void run() {
	            try {
	                while(true)
	                {
	                    //thread waits until there are any images to load in the queue
	                    if(photosQueue.photosToLoad.size()==0)
	                        synchronized(photosQueue.photosToLoad){
	                            photosQueue.photosToLoad.wait();
	                        }
	                    if(photosQueue.photosToLoad.size()!=0)
	                    {
	                        PhotoToLoad photoToLoad;
	                        synchronized(photosQueue.photosToLoad){
	                            photoToLoad=photosQueue.photosToLoad.pop();
	                        }
	                        Bitmap bmp=getBitmap(photoToLoad.url);
	                        cache.put(photoToLoad.url, bmp);
	                        if(((String)photoToLoad.imageView.getTag()).equals(photoToLoad.url)){
	                            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad.imageView);
	                            Activity a=(Activity)photoToLoad.imageView.getContext();
	                            a.runOnUiThread(bd);
	                        }
	                    }
	                    if(Thread.interrupted())
	                        break;
	                }
	            } catch (InterruptedException e) {
	                //allow thread to exit
	            }
	        }
	    }
	    
	    PhotosLoader photoLoaderThread=new PhotosLoader();
	    
	    //Used to display bitmap in the UI thread
	    class BitmapDisplayer implements Runnable
	    {
	        Bitmap bitmap;
	        ImageView imageView;
	        public BitmapDisplayer(Bitmap b, ImageView i){bitmap=b;imageView=i;}
	        public void run()
	        {
	            if(bitmap!=null)
	                imageView.setImageBitmap(bitmap);
	            else
	                imageView.setImageResource(stub_id);
	        }
	    }

	    public void clearCache() {
	        //clear memory cache
	        cache.clear();
	        
	        //clear SD cache
	        File[] files=cacheDir.listFiles();
	        for(File f:files)
	            f.delete();
	    }

	}*/

}
