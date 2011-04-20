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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class HomeActivity extends BaseActivity{
	List<Integer> appIds=new ArrayList<Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.homescreen);
        		
		DisplayMetrics dm = new DisplayMetrics(); 
        getWindowManager().getDefaultDisplay().getMetrics(dm); 
        int width = dm.widthPixels;
        
		Button buttonApps = (Button) findViewById(R.id.ButtonApps);
		buttonApps.setWidth(width/3);
		buttonApps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(HomeActivity.this, CategoriesActivity.class);
            	Bundle objetbunble = new Bundle();
                objetbunble.putInt("game",0);
                i.putExtras(objetbunble );
                startActivity(i);
            }
	    });
		
		Button buttonGames = (Button) findViewById(R.id.ButtonGames);
		buttonGames.setWidth(width/3);
		buttonGames.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(HomeActivity.this, CategoriesActivity.class);
            	Bundle objetbunble = new Bundle();
                objetbunble.putInt("game",1);
                i.putExtras(objetbunble );
                startActivity(i);
            }
	    });
		
		Button buttonUpdates = (Button) findViewById(R.id.ButtonUpdates);
		buttonUpdates.setWidth(width/3);
		buttonUpdates.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
    			Intent i = new Intent(HomeActivity.this, UpdatesActivity.class);
                startActivity(i);
            }
	    });
		
		
		//Tools.queryWeb(Functions.getHost(getApplicationContext())+"/yaamUpdate.php", yaamUpdateHandler);
		
		
		
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);  
		String terminal=pref.getString("terminal", "phone");
		String sdk=Build.VERSION.SDK;
		String lang=getApplicationContext().getResources().getConfiguration().locale.getISO3Language();
		try {
			Tools.queryWeb(Functions.getHost(getApplicationContext())+"/apps/getAppsRecommended.php?lang="+lang+"&sdk="+URLEncoder.encode(sdk,"UTF-8")+"&terminal="+URLEncoder.encode(terminal,"UTF-8")+"&ypass="+Functions.getPassword(getApplicationContext()), parser);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
	    ///WELCOME DIALOG///
	    if(pref.getBoolean("welcome", true))
    	{
		    AlertDialog.Builder build = new AlertDialog.Builder(this);
			build.setMessage(R.string.welcome)
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	  
			           }
			       });
			AlertDialog dialog = build.create();
			
			dialog.show();
			
			Editor editor=pref.edit();
			editor.putBoolean("welcome", false);
			editor.commit();
    	}
		
	}
	
	public Handler parser=new Handler()
    {
    	public void handleMessage(Message msg) {
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
				  
				  names.add(name);
				  
				  appIds.add(Integer.valueOf(id));
				  
				  icons.add(icon);
				  
				  ratings.add(Float.valueOf(rating));
				  
				  prices.add(Float.valueOf(price));
			  }
			
			ListView listRecommended = (ListView) findViewById(R.id.ListViewRecommended);
			listRecommended.setAdapter(new AppsListAdapter(HomeActivity.this,names,icons,ratings,prices));
			
			listRecommended.setOnItemClickListener(new OnItemClickListener() {
			    @SuppressWarnings("rawtypes")
				public void onItemClick(AdapterView parent, View v, int position, long id)
			    {
			    	Intent i = new Intent(HomeActivity.this, ShowAppActivity.class);
                	
                	Bundle objetbunble = new Bundle();
                    objetbunble.putInt("id",appIds.get(position));
                    i.putExtras(objetbunble );

                    startActivity(i);
			    }
			});
			
			//handlerRecommended.sendEmptyMessage(0);
    		} catch (Exception e) {
    			e.printStackTrace();
    			//handlerError.sendEmptyMessage(0);
    		}
    	}
    };
}
