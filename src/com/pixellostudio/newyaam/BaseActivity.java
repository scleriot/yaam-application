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

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Tools.createYAAMDir();
		
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);  
        if(!pref.getBoolean("connected1", false))
        {
        	Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }
		
       
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,1);
        Intent intent = new Intent(this, CheckUpdatesBroadcast.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
        
        Tools.queryWeb(Functions.getHost(getApplicationContext())+"/yaamupdate.php", yaamUpdateHandler);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
	}
	
	
	public Handler yaamUpdateHandler=new Handler()
    {
    	public void handleMessage(Message msg) {
    		String content=msg.getData().getString("content");
    		String updateId=content.split("/")[0];
    		//TODO:String changelog=content.split("/")[1];
    		
    		try {
				if(Integer.valueOf(updateId)>getPackageManager().getPackageInfo("com.pixellostudio.newyaam",PackageManager.GET_ACTIVITIES|PackageManager.GET_GIDS|PackageManager.GET_CONFIGURATIONS|PackageManager.GET_INSTRUMENTATION|PackageManager.GET_PERMISSIONS|PackageManager.GET_PROVIDERS|PackageManager.GET_RECEIVERS|PackageManager.GET_SERVICES|PackageManager.GET_SIGNATURES).versionCode)
				{
					int notificationID = 11;
					NotificationManager notificationManager = (NotificationManager) BaseActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
					Notification notification = new Notification(R.drawable.icon, BaseActivity.this.getText(R.string.yaamupdate), System.currentTimeMillis());
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					Intent intent = new Intent(BaseActivity.this, YAAMUpdate.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent pendingIntent = PendingIntent.getActivity(BaseActivity.this, 0, intent, 0);
					notification.setLatestEventInfo(BaseActivity.this, BaseActivity.this.getText(R.string.newyaamupdate), BaseActivity.this.getText(R.string.yaamupdate), pendingIntent);
					notificationManager.notify(notificationID, notification);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    };
	
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 3, 0, R.string.search_menu).setIcon(android.R.drawable.ic_menu_search);
    	menu.add(0, 1, 0, R.string.updates_menu).setIcon(android.R.drawable.ic_menu_rotate);
    	//TODO : menu.add(0, 2, 0, R.string.settings_menu).setIcon(android.R.drawable.ic_menu_preferences);
    	menu.add(0, 4, 0, R.string.disconnect_menu).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) { 	
    	Intent i;
        switch (item.getItemId()) {
        case 1: //Updates
        	i = new Intent(this, UpdatesActivity.class);
            startActivity(i);
            return true;
        case 3: //Search
        	this.startSearch("", false, null, false);
        	return true;
        case 2: //Settings
        	//i = new Intent(BaseActivity.this, Settings.class);

            //startActivity(i);
        	return true;
        case 4: //disconnect
        	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(BaseActivity.this.getApplicationContext());
			
			Editor editor=pref.edit();
			editor.putBoolean("connected1", false);
			editor.putString("username", "");
			editor.putString("terminal", "");
			editor.commit();
			
			i = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(i);
			this.finish();
			
			return true;
        }
        return false;
    }

    @Override 
    public void onConfigurationChanged(Configuration newConfig) 
    {
           super.onConfigurationChanged(newConfig);
    }
}
