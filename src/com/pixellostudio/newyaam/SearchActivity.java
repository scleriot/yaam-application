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

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;




public class SearchActivity extends BaseActivity {
	int PAID=0;
	int FREE=1;
	
	List<Integer> appIdsFree=new ArrayList<Integer>();
	List<Integer> appIdsPaid=new ArrayList<Integer>();
	String order="top";
	String query;
	
	TabHost mTabHost;
	
	private ProgressDialog mProgress;
	
	int pageFree=0;
	int pagePaid=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setActionBarContentView(R.layout.categoryscreen);
		
		super.onCreate(savedInstanceState);
				
		mTabHost = (TabHost) this.findViewById(R.id.tabhost);
		mTabHost.setup();
		
		TabSpec tab1=mTabHost.newTabSpec("tab_paid").setIndicator(getText(R.string.categories_paid).toString()).setContent(R.id.LinearLayoutPaid);
        mTabHost.addTab(tab1);
        
        TabSpec tab2=mTabHost.newTabSpec("tab_free").setIndicator(getText(R.string.categories_free).toString()).setContent(R.id.LinearLayoutFree);
        mTabHost.addTab(tab2);
		
        mTabHost.getTabWidget().getChildAt(PAID).getLayoutParams().height = 40;
        mTabHost.getTabWidget().getChildAt(FREE).getLayoutParams().height = 40;
		
        getActionBar().setTitle(getText(R.string.search_results)+" ("+getText(R.string.top)+")".toString());
		
		
		Intent intent = getIntent();

	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);
			//Log.d("YAAM", query);
			LoadInfos();
	    }
		
	    Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth(); 
		
		/////FREE BUTTONS//////
		Button buttonPrevFree = (Button) findViewById(R.id.ButtonPrevFree);
		//buttonPrevFree.setBackgroundDrawable(this.getResources().getDrawable(android.R.drawable.));
		buttonPrevFree.setWidth(width/2);
		buttonPrevFree.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	pageFree--;
            	
            	updateButtons();
            	
        		LoadInfos();
            }
	     }); 
		
		Button buttonNextFree = (Button) findViewById(R.id.ButtonNextFree);
		buttonNextFree.setWidth(width/2);
		buttonNextFree.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	pageFree++;
            	
            	updateButtons();
            	
        		LoadInfos();
            }
	     }); 
		
		
		
	/////PAID BUTTONS//////
		Button buttonPrevPaid = (Button) findViewById(R.id.ButtonPrevPaid);
		buttonPrevPaid.setWidth(width/2);
		buttonPrevPaid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	pagePaid--;
            	
            	updateButtons();
            	
        		LoadInfos();
            }
	     }); 
		
		Button buttonNextPaid = (Button) findViewById(R.id.ButtonNextPaid);
		buttonNextPaid.setWidth(width/2);
		buttonNextPaid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	pagePaid++;
            	
            	updateButtons();
            	
        		LoadInfos();
            }
	     }); 
		
		
		updateButtons();
	}
	
	public void updateButtons()
	{
		///FREE BUTTONS///
		Button buttonPrevFree = (Button) findViewById(R.id.ButtonPrevFree);
		if(pageFree<=0)
			buttonPrevFree.setEnabled(false);
		else
			buttonPrevFree.setEnabled(true);
		
		
		///PAID BUTTONS///
		Button buttonPrevPaid = (Button) findViewById(R.id.ButtonPrevPaid);
		if(pagePaid<=0)
			buttonPrevPaid.setEnabled(false);
		else
			buttonPrevPaid.setEnabled(true);
	}
	
	public void LoadInfos()
	{
		mProgress = ProgressDialog.show(this, this.getText(R.string.loading),
                this.getText(R.string.loadingtext), true, false);
		
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);  
		String terminal=pref.getString("terminal", "phone");
		String sdk=Build.VERSION.SDK;
		String lang=getApplicationContext().getResources().getConfiguration().locale.getISO3Language();
		try {
			Tools.queryWeb(Functions.getHost(getApplicationContext())+"/apps/search.php?page="+pageFree+"&order="+order+"&query="+URLEncoder.encode(query,"UTF-8")+"&lang="+lang+"&sdk="+URLEncoder.encode(sdk,"UTF-8")+"&paid=0&terminal="+URLEncoder.encode(terminal,"UTF-8")+"&ypass="+Functions.getPassword(getApplicationContext()), parserFree);
			Tools.queryWeb(Functions.getHost(getApplicationContext())+"/apps/search.php?page="+pagePaid+"&order="+order+"&query="+URLEncoder.encode(query,"UTF-8")+"&lang="+lang+"&sdk="+URLEncoder.encode(sdk,"UTF-8")+"&paid=1&terminal="+URLEncoder.encode(terminal,"UTF-8")+"&ypass="+Functions.getPassword(getApplicationContext()), parserPaid);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public Handler parserFree=new Handler()
    {
    	public void handleMessage(Message msg) {
    		appIdsFree.clear();
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
				  appIdsFree.add(Integer.valueOf(id));
				  icons.add(icon);
				  prices.add(Float.valueOf(price));
				  ratings.add(Float.valueOf(rating));
			  }
			
			ListView listRecommended = (ListView) findViewById(R.id.ListViewAppsFree);
			listRecommended.setAdapter(new AppsListAdapter(SearchActivity.this,names,icons,ratings,prices));
			
			listRecommended.setOnItemClickListener(new OnItemClickListener() {
			    @SuppressWarnings("rawtypes")
				public void onItemClick(AdapterView parent, View v, int position, long id)
			    {
			    	Intent i = new Intent(SearchActivity.this, ShowAppActivity.class);
                	
                	Bundle objetbunble = new Bundle();
                    objetbunble.putInt("id",appIdsFree.get(position));
                    i.putExtras(objetbunble );

                    startActivity(i);
			    }
			});
			
			
			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		TextView title = (TextView) mTabHost.getTabWidget().getChildAt(FREE).findViewById(android.R.id.title);
    		if(appIdsFree.size()<10)
    			title.setText(SearchActivity.this.getText(R.string.categories_free)+" ("+appIdsFree.size()+")");
    		else
    			title.setText(SearchActivity.this.getText(R.string.categories_free)+" ("+appIdsFree.size()+"+)");
    		
    		mProgress.dismiss();
    	}
    };

	public Handler parserPaid=new Handler()
    {
    	public void handleMessage(Message msg) {
    		appIdsPaid.clear();
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
				  
				  appIdsPaid.add(Integer.valueOf(id));
				  
				  icons.add(icon);
				  prices.add(Float.valueOf(price));
				  ratings.add(Float.valueOf(rating));
			  }
			
			ListView listRecommended = (ListView) findViewById(R.id.ListViewAppsPaid);
			listRecommended.setAdapter(new AppsListAdapter(SearchActivity.this,names,icons,ratings,prices));
			
			listRecommended.setOnItemClickListener(new OnItemClickListener() {
			    @SuppressWarnings("rawtypes")
				public void onItemClick(AdapterView parent, View v, int position, long id)
			    {
			    	Intent i = new Intent(SearchActivity.this, ShowAppActivity.class);
                	
                	Bundle objetbunble = new Bundle();
                    objetbunble.putInt("id",appIdsPaid.get(position));
                    i.putExtras(objetbunble );

                    startActivity(i);
			    }
			});
			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		
    		TextView title = (TextView) mTabHost.getTabWidget().getChildAt(PAID).findViewById(android.R.id.title);
    		if(appIdsPaid.size()<10)
    			title.setText(SearchActivity.this.getText(R.string.categories_paid)+" ("+appIdsPaid.size()+")");
    		else
    			title.setText(SearchActivity.this.getText(R.string.categories_paid)+" ("+appIdsPaid.size()+"+)");
    		
    		mProgress.dismiss();
    	}
    };
    
    
    
    
    
    
    
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
        	getActionBar().setTitle(getText(R.string.search_results)+" ("+getText(R.string.top)+")".toString());
    		pageFree=0;
    		pagePaid=0;
    		updateButtons();
    		LoadInfos();
            return true;
        case 2: //Last
        	order="last";
        	getActionBar().setTitle(getText(R.string.search_results)+" ("+getText(R.string.last).toString()+")");
    		pageFree=0;
    		pagePaid=0;
    		updateButtons();
    		LoadInfos();
            return true;
        case 3: //Search
        	this.startSearch("", false, null, false);
        	return true;
        case 4: //disconnect
        	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(SearchActivity.this.getApplicationContext());
			
			Editor editor=pref.edit();
			editor.putBoolean("connected", false);
			editor.putString("username", "");
			editor.putString("terminal", "");
			editor.commit();
			
			Intent i = new Intent(SearchActivity.this, LoginActivity.class);
            startActivity(i);
			this.finish();
			
			return true;
        }
        return false;
    }
}
