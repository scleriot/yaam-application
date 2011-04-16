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
package com.pixellostudio.newyaam;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.pixellostudio.tools.DownloadHandler;
import com.pixellostudio.tools.DownloadProcessor;
import com.pixellostudio.tools.Tools;


public class Functions implements Runnable
{
	public static String getPassword(Context context)
	{
		return context.getText(R.string.ypass).toString();
	}
	
	public static String getHost(Context context)
	{
		return context.getText(R.string.yhost).toString();
	}
	
	public static String getDataFromXML(Element E1, String tag)
	{
		  NodeList liste=E1.getElementsByTagName(tag);
		  Element E2= (Element)liste.item(0);
		  
		  return E2.getAttribute("data");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private DownloadProcessor mThread;
    private ProgressDialog mProgress;
	Configuration config;
	Activity activity;
	Service service;
	Context context;
	String idApp;
	
	public void run()
	{
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
    	String name=pref.getString("username", "");
    	
        String phone=Build.MODEL;
        String sdk=Build.VERSION.SDK;
        String lang=config.locale.getISO3Language();
        String country=config.locale.getISO3Country();
                
		try {
			Tools.queryWeb(Functions.getHost(activity.getApplicationContext())+"/apps/addApplicationDL.php?ypass="+Functions.getPassword(activity.getApplicationContext())+"&appid="+idApp+"&username="+URLEncoder.encode(name,"UTF-8")+"&phone="+URLEncoder.encode(phone,"UTF-8")+"&lang="+URLEncoder.encode(lang,"UTF-8")+"&country="+URLEncoder.encode(country,"UTF-8")+"&sdk="+URLEncoder.encode(sdk,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
    private final DownloadHandler mHandler = new DownloadHandler()
    {
            @Override
            public void handleMessage(Message msg)
            {
                    DownloadProcessor.Download dl = mThread.getDownload();

                    switch (msg.what)
                    {
                    case MSG_FINISHED:
                            mProgress.dismiss();
                            mProgress = null;
                            mThread = null;
                            
                    		Thread addAppDl=new Thread(Functions.this);
                    		addAppDl.start();
                            
                            Intent intent=new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse("file://"+Environment.getExternalStorageDirectory().toString()+"/.yaam/"+idApp+".apk"), "application/vnd.android.package-archive"); 
                            activity.startActivityForResult(intent,1);
                            break;
                    case MSG_SET_LENGTH:
                            mProgress.setIndeterminate(false);
                            mProgress.setProgress(0);
                            dl.length = (Long)msg.obj;
                            break;
                    case MSG_ON_RECV:
                            if (dl.length >= 0)
                            {
                                    float prog =
                                      ((float)((Long)msg.obj) / (float)dl.length) * 100f;

                                    mProgress.setProgress((int)(prog * 100f));
                                    mProgress.setMessage("Received " + (int)prog + "%");
                            }
                            else
                            {
                                    mProgress.setMessage("Received " + (Long)msg.obj + " bytes");
                            }
                            break;
                    case MSG_ERROR:
                            mProgress.dismiss();
                            mProgress = null;
                            mThread = null;
                            Toast.makeText(context, "Error: " + msg.obj,
                              Toast.LENGTH_LONG).show();
                            break;
                    default:
                            super.handleMessage(msg);
                    }
            }
    };
	
	public final void install(final String _idApp,final Activity _activity,final Configuration _config)
	{
	    context=_activity.getApplicationContext();
	    idApp=_idApp;
	    config=_config;
	    activity=_activity;
	    
	    SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(_activity.getApplicationContext());
    	String name=pref.getString("username", "");
	    
	    String src="";
		try {
			src = Functions.getHost(activity.getApplicationContext())+"/apps/downloadApk.php?ypass="+Functions.getPassword(activity.getApplicationContext())+"&username="+URLEncoder.encode(name,"UTF-8")+"&id="+idApp;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        String dst = Environment.getExternalStorageDirectory().toString()+"/.yaam/"+idApp+".apk";
        
        URL srcUrl;

        try
        {
                srcUrl = new URL(src);
        }
        catch (MalformedURLException e)
        {
                Toast.makeText(activity,
                  "Invalid source URL",
                  Toast.LENGTH_SHORT).show();

                return;
        }

        mProgress = ProgressDialog.show(activity, "Downloading...",
                  "Connecting to server...", true, false);

        DownloadProcessor.Download dl = 
          new DownloadProcessor.Download(srcUrl, new File(dst));
        
        mThread = new DownloadProcessor(dl, mHandler);
        mThread.start();
	}
	
	public final void updateYAAM(final Activity _activity,final Configuration _config)
	{
	    context=_activity.getApplicationContext();
	    config=_config;
	    activity=_activity;
	    
	    String src= Functions.getHost(activity.getApplicationContext())+"/yaam.apk";
		
        String dst = Environment.getExternalStorageDirectory().toString()+"/.yaam/yaam.apk";
        idApp="yaam";
        URL srcUrl;

        try
        {
                srcUrl = new URL(src);
        }
        catch (MalformedURLException e)
        {
                Toast.makeText(activity,
                  "Invalid source URL",
                  Toast.LENGTH_SHORT).show();

                return;
        }

        mProgress = ProgressDialog.show(activity, "Downloading...",
                  "Connecting to server...", true, false);

        DownloadProcessor.Download dl = 
          new DownloadProcessor.Download(srcUrl, new File(dst));
        
        mThread = new DownloadProcessor(dl, mHandler);
        mThread.start();
	}
}
