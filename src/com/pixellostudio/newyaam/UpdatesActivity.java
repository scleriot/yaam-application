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

import greendroid.widget.SegmentedAdapter;
import greendroid.widget.SegmentedHost;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class UpdatesActivity extends BaseActivity {
	List<Integer> appIds=new ArrayList<Integer>();
	String order="top";
	String query;
	
	private ProgressDialog mProgress;
	
	private View mView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setActionBarContentView(R.layout.categoryscreen);
		
		super.onCreate(savedInstanceState);
		
        getActionBar().setTitle(getText(R.string.updates_menu)+" ("+getText(R.string.top)+")".toString());
		
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		mView=inflater.inflate(R.layout.category_list, null, false);
		
		SegmentedHost segmentedHost = (SegmentedHost) findViewById(R.id.segmentedHost);
		 
		UpdatesSegmentedAdapter mAdapter = new UpdatesSegmentedAdapter();
        segmentedHost.setAdapter(mAdapter);
        
        
		Button buttonPrev = (Button) mView.findViewById(R.id.ButtonPrev);
		Button buttonNext = (Button) mView.findViewById(R.id.ButtonNext);
		
		buttonPrev.setVisibility(View.GONE);
		buttonNext.setVisibility(View.GONE);
		
		//LoadInfos();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR,1);
        Intent intent = new Intent(this, CheckUpdatesBroadcast.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
        
        LoadInfos();
	}
	
	
	
	private class UpdatesSegmentedAdapter extends SegmentedAdapter {
		 
        public boolean mReverse = false;
 
        @Override
        public View getView(int position, ViewGroup parent) {            
            return mView;
        }
 
        @Override
        public int getCount() {
            return 1;
        }
 
        @Override
        public String getSegmentTitle(int position) {
 
            switch (mReverse ? ((getCount() - 1) - position) : position) {
                case 0:
                    return getString(R.string.updates_menu)+" : "+appIds.size();
            }
 
            return null;
        }
    }
	
	
	
	
	public void LoadInfos()
	{
		mProgress = ProgressDialog.show(this, this.getText(R.string.loading),
                this.getText(R.string.loadingtext), true, false);
		
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);  
		String terminal=pref.getString("terminal", "phone");
		String username=pref.getString("username", "");
		String sdk=Build.VERSION.SDK;
		String lang=getApplicationContext().getResources().getConfiguration().locale.getISO3Language();
		try {
			Tools.queryWeb(Functions.getHost(getApplicationContext())+"/apps/getUpdates.php?order="+order+"&username="+URLEncoder.encode(username,"UTF-8")+"&lang="+lang+"&sdk="+URLEncoder.encode(sdk,"UTF-8")+"&terminal="+URLEncoder.encode(terminal,"UTF-8")+"&ypass="+Functions.getPassword(getApplicationContext()), parser);
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
			
			List<String> names=new ArrayList<String>();
			List<String> icons=new ArrayList<String>();
			List<Float> ratings=new ArrayList<Float>();
			List<Float> prices=new ArrayList<Float>();
			
			for(int i=0; i<liste.getLength(); i++){
				  Element E1= (Element) liste.item(i);
				  String name="",id="",rating="",icon="",price="";

				  name=Functions.getDataFromXML(E1,"name");
				  icon=Functions.getDataFromXML(E1,"icon");
				  id=Functions.getDataFromXML(E1,"id");
				  rating=Functions.getDataFromXML(E1,"rating");
				  price=Functions.getDataFromXML(E1,"price");
				  
				  if(isAppInstalled(Functions.getDataFromXML(E1,"package")))
				  {
					  names.add(name);
					  
					  appIds.add(Integer.valueOf(id));
					  
					  icons.add(icon);
					  prices.add(Float.valueOf(price));
					  ratings.add(Float.valueOf(rating));
				  }
			  }
			
			ListView listRecommended = (ListView) mView.findViewById(R.id.ListViewApps);
			listRecommended.setAdapter(new AppsListAdapter(UpdatesActivity.this,names,icons,ratings,prices));
			
			listRecommended.setOnItemClickListener(new OnItemClickListener() {
			    @SuppressWarnings("rawtypes")
				public void onItemClick(AdapterView parent, View v, int position, long id)
			    {
			    	Intent i = new Intent(UpdatesActivity.this, ShowAppActivity.class);
                	
                	Bundle objetbunble = new Bundle();
                    objetbunble.putInt("id",appIds.get(position));
                    i.putExtras(objetbunble );

                    startActivity(i);
			    }
			});
			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		mProgress.dismiss();
    		
    		SegmentedHost segmentedHost = (SegmentedHost) findViewById(R.id.segmentedHost);
    		UpdatesSegmentedAdapter mAdapter = new UpdatesSegmentedAdapter();
            segmentedHost.setAdapter(mAdapter);
    	}
    };
    
    
    public boolean isAppInstalled(String packagename)
    {
			PackageManager pm=this.getApplicationContext().getPackageManager();
			
			try {
				pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
			} catch (NameNotFoundException e) {
				return false;
			}
			
			return true;
    }
    
    
    
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 1, 0, R.string.top);
    	menu.add(0, 2, 0, R.string.last);
    	menu.add(0, 3, 0, R.string.search_menu).setIcon(android.R.drawable.ic_menu_search);
    	menu.add(0, 4, 0, R.string.disconnect_menu).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) { 	
        switch (item.getItemId()) {
        case 1: //Top
        	order="top";
        	getActionBar().setTitle(getText(R.string.updates_button)+" ("+getText(R.string.top)+")".toString());
    		LoadInfos();
            return true;
        case 2: //Last
        	order="last";
        	getActionBar().setTitle(getText(R.string.updates_button)+" ("+getText(R.string.last).toString()+")");
    		LoadInfos();
            return true;
        case 3: //Search
        	this.startSearch("", false, null, false);
        	return true;
        case 4: //disconnect
        	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(UpdatesActivity.this.getApplicationContext());
			
			Editor editor=pref.edit();
			editor.putBoolean("connected", false);
			editor.putString("username", "");
			editor.putString("terminal", "");
			editor.commit();
			
			Intent i = new Intent(UpdatesActivity.this, LoginActivity.class);
            startActivity(i);
			this.finish();
			
			return true;
        }
        return false;
    }
}
