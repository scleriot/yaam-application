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

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.pixellostudio.tools.Tools;

public class CategoryActivity extends BaseActivity {
	List<Integer> appIdsFree=new ArrayList<Integer>();
	List<Integer> appIdsPaid=new ArrayList<Integer>();
	String order="top";
	int pageFree=0;
	int pagePaid=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.categoryscreen);
		
		TabHost mTabHost = (TabHost) this.findViewById(R.id.tabhost);
		mTabHost.setup();
		
		TabSpec tab1=mTabHost.newTabSpec("tab_free").setIndicator(getText(R.string.categories_paid).toString()).setContent(R.id.LinearLayoutPaid);
        mTabHost.addTab(tab1);
        
        TabSpec tab2=mTabHost.newTabSpec("tab_paid").setIndicator(getText(R.string.categories_free).toString()).setContent(R.id.LinearLayoutFree);
        mTabHost.addTab(tab2);
		
        mTabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 40;
        mTabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 40;
		
        TextView textCatName = (TextView) findViewById(R.id.TextViewCategoryName);
		textCatName.setText(getIntent().getExtras().getString("name")+" ("+getText(R.string.top)+")".toString());
		
		
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
		LoadInfos();
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
		int catid=getIntent().getExtras().getInt("id");
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);  
		String terminal=pref.getString("terminal", "phone");
		String sdk=Build.VERSION.SDK;
		String lang=getApplicationContext().getResources().getConfiguration().locale.getISO3Language();
		try {
			Tools.queryWeb(Functions.getHost(getApplicationContext())+"/categories/applications.php?page="+pageFree+"&order="+order+"&catid="+catid+"&lang="+lang+"&sdk="+URLEncoder.encode(sdk,"UTF-8")+"&paid=0&terminal="+URLEncoder.encode(terminal,"UTF-8")+"&ypass="+Functions.getPassword(getApplicationContext()), parserFree);
			Tools.queryWeb(Functions.getHost(getApplicationContext())+"/categories/applications.php?page="+pagePaid+"&order="+order+"&catid="+catid+"&lang="+lang+"&sdk="+URLEncoder.encode(sdk,"UTF-8")+"&paid=1&terminal="+URLEncoder.encode(terminal,"UTF-8")+"&ypass="+Functions.getPassword(getApplicationContext()), parserPaid);
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
			listRecommended.setAdapter(new AppsListAdapter(CategoryActivity.this,names,icons,ratings,prices));
			
			listRecommended.setOnItemClickListener(new OnItemClickListener() {
			    @SuppressWarnings("rawtypes")
				public void onItemClick(AdapterView parent, View v, int position, long id)
			    {
			    	Intent i = new Intent(CategoryActivity.this, ShowAppActivity.class);
                	
                	Bundle objetbunble = new Bundle();
                    objetbunble.putInt("id",appIdsFree.get(position));
                    i.putExtras(objetbunble );

                    startActivity(i);
			    }
			});
			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
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
			listRecommended.setAdapter(new AppsListAdapter(CategoryActivity.this,names,icons,ratings,prices));
			
			listRecommended.setOnItemClickListener(new OnItemClickListener() {
			    @SuppressWarnings("rawtypes")
				public void onItemClick(AdapterView parent, View v, int position, long id)
			    {
			    	Intent i = new Intent(CategoryActivity.this, ShowAppActivity.class);
                	
                	Bundle objetbunble = new Bundle();
                    objetbunble.putInt("id",appIdsPaid.get(position));
                    i.putExtras(objetbunble );

                    startActivity(i);
			    }
			});
			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    };
    
    
    
    
    
    
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 1, 0, R.string.top);
    	menu.add(0, 2, 0, R.string.last);
    	menu.add(0, 3, 0, R.string.search_menu).setIcon(android.R.drawable.ic_menu_search);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) { 	
        switch (item.getItemId()) {
        case 1: //Top
        	order="top";
        	TextView textCatName = (TextView) findViewById(R.id.TextViewCategoryName);
    		textCatName.setText(getIntent().getExtras().getString("name")+" ("+getText(R.string.top)+")".toString());
    		LoadInfos();
            return true;
        case 2: //Last
        	order="last";
        	TextView textCatName2 = (TextView) findViewById(R.id.TextViewCategoryName);
    		textCatName2.setText(getIntent().getExtras().getString("name")+" ("+getText(R.string.last).toString()+")");
    		LoadInfos();
            return true;
        case 3: //Search
        	this.startSearch("", false, null, false);
        	return true;
        
        }
        return false;
    }
}
