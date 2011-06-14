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

import greendroid.widget.SegmentedAdapter;
import greendroid.widget.SegmentedHost;

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
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;

public class CategoryActivity extends BaseActivity {
	int PAID=0;
	int FREE=1;
	
	List<Integer> appIdsFree=new ArrayList<Integer>();
	List<Integer> appIdsPaid=new ArrayList<Integer>();
	String order="top";
	int pageFree=0;
	int pagePaid=0;
	
	TabHost mTabHost;
	
	private ProgressDialog mProgress;
	
	private View mViewFree,mViewPaid;
	
	//FREE
	List<String> namesFree=new ArrayList<String>();
	List<String> iconsFree=new ArrayList<String>();
	List<Float> ratingsFree=new ArrayList<Float>();
	List<Float> pricesFree=new ArrayList<Float>();
	
	ListView listViewFree;
	AppsListAdapter adapterFree;
	
	//PAID
	List<String> namesPaid=new ArrayList<String>();
	List<String> iconsPaid=new ArrayList<String>();
	List<Float> ratingsPaid=new ArrayList<Float>();
	List<Float> pricesPaid=new ArrayList<Float>();
	
	ListView listViewPaid;
	AppsListAdapter adapterPaid;
	
	boolean firstfree=true, firstpaid=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setActionBarContentView(R.layout.categoryscreen);
		
		super.onCreate(savedInstanceState);
				
        getActionBar().setTitle(getIntent().getExtras().getString("name")+" ("+getText(R.string.top)+")".toString());
		
        
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		mViewFree=inflater.inflate(R.layout.category_list, null, false);
        mViewPaid=inflater.inflate(R.layout.category_list, null, false);
		
		SegmentedHost segmentedHost = (SegmentedHost) findViewById(R.id.segmentedHost);
		
		listViewFree = (ListView) mViewFree.findViewById(R.id.ListViewApps);
        listViewPaid = (ListView) mViewPaid.findViewById(R.id.ListViewApps);
        
        adapterFree=new AppsListAdapter(CategoryActivity.this,namesFree,iconsFree,ratingsFree,pricesFree);
		listViewFree.setAdapter(adapterFree);
		
		adapterPaid=new AppsListAdapter(CategoryActivity.this,namesPaid,iconsPaid,ratingsPaid,pricesPaid);
		listViewPaid.setAdapter(adapterPaid);
		
		
		
		
		FreePaidSegmentedAdapter mAdapter = new FreePaidSegmentedAdapter();
        segmentedHost.setAdapter(mAdapter);


		LoadInfos();
	}
	
	
	private class FreePaidSegmentedAdapter extends SegmentedAdapter {
		 
        public boolean mReverse = false;
 
        @Override
        public View getView(int position, ViewGroup parent) {            
            if(position==0)
            {
            	return mViewFree;
            }
            else
            {
            	return mViewPaid;
            }
        }
 
        @Override
        public int getCount() {
            return 2;
        }
 
        @Override
        public String getSegmentTitle(int position) {
            switch (mReverse ? ((getCount() - 1) - position) : position) {
                case 0:
                    return getString(R.string.categories_free);
                case 1:
                    return getString(R.string.categories_paid);
            }
 
            return null;
        }
    }
	
	
	void resetFree()
	{
		appIdsFree.clear();

		namesFree.clear();
		iconsFree.clear();
		ratingsFree.clear();
		pricesFree.clear();
		
		adapterFree=new AppsListAdapter(CategoryActivity.this,namesFree,iconsFree,ratingsFree,pricesFree);
		listViewFree.setAdapter(adapterFree);
		
		firstfree=true;
	}
	void resetPaid()
	{
		appIdsPaid.clear();

		namesPaid.clear();
		iconsPaid.clear();
		ratingsPaid.clear();
		pricesPaid.clear();
		
		adapterPaid=new AppsListAdapter(CategoryActivity.this,namesPaid,iconsPaid,ratingsPaid,pricesPaid);
		listViewPaid.setAdapter(adapterPaid);
		
		firstpaid=true;
	}
	
	
	public void LoadInfosFree()
	{
		int catid=getIntent().getExtras().getInt("id");
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);  
		String terminal=pref.getString("terminal", "phone");
		String sdk=Build.VERSION.SDK;
		String lang=getApplicationContext().getResources().getConfiguration().locale.getISO3Language();
		try {
			Tools.queryWeb(Functions.getHost(getApplicationContext())+"/categories/applications.php?page="+pageFree+"&order="+order+"&catid="+catid+"&lang="+lang+"&sdk="+URLEncoder.encode(sdk,"UTF-8")+"&paid=0&terminal="+URLEncoder.encode(terminal,"UTF-8")+"&ypass="+Functions.getPassword(getApplicationContext()), parserFree);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	public void LoadInfosPaid()
	{
		int catid=getIntent().getExtras().getInt("id");
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);  
		String terminal=pref.getString("terminal", "phone");
		String sdk=Build.VERSION.SDK;
		String lang=getApplicationContext().getResources().getConfiguration().locale.getISO3Language();
		try {
			Tools.queryWeb(Functions.getHost(getApplicationContext())+"/categories/applications.php?page="+pagePaid+"&order="+order+"&catid="+catid+"&lang="+lang+"&sdk="+URLEncoder.encode(sdk,"UTF-8")+"&paid=1&terminal="+URLEncoder.encode(terminal,"UTF-8")+"&ypass="+Functions.getPassword(getApplicationContext()), parserPaid);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void LoadInfos()
	{
		mProgress = ProgressDialog.show(this, this.getText(R.string.loading),
                this.getText(R.string.loadingtext), true, false);
		
		LoadInfosFree();
		LoadInfosPaid();
	}
	
	
	public Handler parserFree=new Handler()
    {
    	public void handleMessage(Message msg) {
    		//appIdsFree.clear();
    		String content=msg.getData().getString("content");
    		try {
    		DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur = builder.newDocumentBuilder();
			Document document = constructeur.parse(new ByteArrayInputStream( content.getBytes()));
			Element racine = document.getDocumentElement();
			NodeList liste = racine.getElementsByTagName("app");
			
			
			for(int i=0; i<liste.getLength(); i++){
				  Element E1= (Element) liste.item(i);
				  String name="",id="",rating="",icon="",price="";
				  

				  name=Functions.getDataFromXML(E1,"name");
				  icon=Functions.getDataFromXML(E1,"icon");
				  id=Functions.getDataFromXML(E1,"id");
				  rating=Functions.getDataFromXML(E1,"rating");
				  price=Functions.getDataFromXML(E1,"price");
				  
				  namesFree.add(name);
				  appIdsFree.add(Integer.valueOf(id));
				  iconsFree.add(icon);
				  pricesFree.add(Float.valueOf(price));
				  ratingsFree.add(Float.valueOf(rating));
			  }
			
			

			if(firstfree)
			{
				listViewFree.setAdapter(adapterFree);
				firstfree=false;
			}
			else
				adapterFree.notifyDataSetChanged();
			
			
			listViewFree.setOnScrollListener(new EndlessScrollListener());
			listViewFree.setOnItemClickListener(new OnItemClickListener() {
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
    		
    		
    		
    		mProgress.dismiss();
    	}
    };

	public Handler parserPaid=new Handler()
    {
    	public void handleMessage(Message msg) {
    		//appIdsPaid.clear();
    		String content=msg.getData().getString("content");
    		try {
    		DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur = builder.newDocumentBuilder();
			Document document = constructeur.parse(new ByteArrayInputStream( content.getBytes()));
			Element racine = document.getDocumentElement();
			NodeList liste = racine.getElementsByTagName("app");
			
			
			for(int i=0; i<liste.getLength(); i++){
				  Element E1= (Element) liste.item(i);
				  String name="",id="",rating="",icon="",price="";
				  

				  name=Functions.getDataFromXML(E1,"name");
				  icon=Functions.getDataFromXML(E1,"icon");
				  id=Functions.getDataFromXML(E1,"id");
				  rating=Functions.getDataFromXML(E1,"rating");
				  price=Functions.getDataFromXML(E1,"price");
				  
				  namesPaid.add(name);
				  
				  appIdsPaid.add(Integer.valueOf(id));
				  
				  iconsPaid.add(icon);
				  pricesPaid.add(Float.valueOf(price));
				  ratingsPaid.add(Float.valueOf(rating));
			  }
			
			
			listViewPaid.setOnScrollListener(new EndlessScrollListener());
			listViewPaid.setOnItemClickListener(new OnItemClickListener() {
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
    		
    		if(firstpaid)
    		{
    			listViewPaid.setAdapter(adapterPaid);
    			firstpaid=false;
    		}
    		else
				adapterPaid.notifyDataSetChanged();
    	
    		mProgress.dismiss();
    	}
    };
    
    
    
    
    
    public class EndlessScrollListener implements OnScrollListener {
    	 
        private int visibleThreshold = 5;
        private int previousTotal = 0;
        private boolean loading = true;
     
        public EndlessScrollListener() {
        }
        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }
     
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                if(view==listViewFree)
                {
                	pageFree++;
                	LoadInfosFree();
                }
                else if(view==listViewPaid)
                {
                	pagePaid++;
                	LoadInfosPaid();
                }
                loading = true;
            }
        }
     
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
    
    
    
    
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
        	getActionBar().setTitle(getIntent().getExtras().getString("name")+" ("+getText(R.string.top)+")".toString());
    		pageFree=0;
    		pagePaid=0;
    		resetFree();
    		resetPaid();
    		LoadInfos();
            return true;
        case 2: //Last
        	order="last";
        	getActionBar().setTitle(getIntent().getExtras().getString("name")+" ("+getText(R.string.last).toString()+")");
    		pageFree=0;
    		pagePaid=0;
    		resetPaid();
    		resetFree();
    		LoadInfos();
            return true;
        case 3: //Search
        	this.startSearch("", false, null, false);
        	return true;
        
        }
        return false;
    }
}
