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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.pixellostudio.tools.Tools;

public class CheckUpdatesBroadcast extends BroadcastReceiver {
	List<Integer> appIds=new ArrayList<Integer>();
	Context _Context;
	@Override
	public void onReceive(Context context, Intent intent) {
		_Context=context;
		LoadInfos();
	}

	public void LoadInfos()
	{
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(_Context);  
		String terminal=pref.getString("terminal", "phone");
		String username=pref.getString("username", "");
		String sdk=Build.VERSION.SDK;
		String lang=_Context.getResources().getConfiguration().locale.getISO3Language();
		try {
			Tools.queryWeb(Functions.getHost(_Context)+"/apps/updates.php?order=top&username="+URLEncoder.encode(username,"UTF-8")+"&lang="+lang+"&sdk="+URLEncoder.encode(sdk,"UTF-8")+"&terminal="+URLEncoder.encode(terminal,"UTF-8")+"&ypass="+Functions.getPassword(_Context), parser);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public Handler parser=new Handler()
    {
    	public void handleMessage(Message msg) {
    		appIds.clear();
    		String content=msg.getData().getString("content");
    		try {
    		DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur = builder.newDocumentBuilder();
			Document document = constructeur.parse(new ByteArrayInputStream( content.getBytes()));
			Element racine = document.getDocumentElement();
			NodeList liste = racine.getElementsByTagName("app");
			
			for(int i=0; i<liste.getLength(); i++){
				  Element E1= (Element) liste.item(i);
				  String id;
				  id=Functions.getDataFromXML(E1,"id");
			
				  
				  if(isUpdate(Functions.getDataFromXML(E1,"package"),Functions.getDataFromXML(E1,"version")))
				  {					  
					  appIds.add(Integer.valueOf(id));
				  }
			}
			
			if(appIds.size()>0)
			{
				int notificationID = 10;
				NotificationManager notificationManager = (NotificationManager) _Context.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification(R.drawable.icon, _Context.getText(R.string.updatesavailable), System.currentTimeMillis());
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				Intent intent = new Intent(_Context, UpdatesActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pendingIntent = PendingIntent.getActivity(_Context, 0, intent, 0);
				notification.setLatestEventInfo(_Context, _Context.getText(R.string.newupdate), _Context.getText(R.string.updatesavailable), pendingIntent);
				notificationManager.notify(notificationID, notification);
			}

			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    };
    
    boolean isUpdate(String packagename,String versionName)
    {
    	PackageManager pm=_Context.getPackageManager();
		
		try {
			PackageInfo infos=pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
						
			if(infos!=null)
			{
				if(versionName.equals(infos.versionName))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return false;
		} catch (NameNotFoundException e) {
			return false;
		}
    }
}
