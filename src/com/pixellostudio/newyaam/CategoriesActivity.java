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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CategoriesActivity extends BaseActivity{
	List<String> categoriesId=new ArrayList<String>();
	List<String> categoriesName=new ArrayList<String>();
	
	private ProgressDialog mProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.categoriesscreen);
		
		int game=getIntent().getExtras().getInt("game");
		String lang=getApplicationContext().getResources().getConfiguration().locale.getISO3Language();

		mProgress = ProgressDialog.show(this, this.getText(R.string.loading),
                this.getText(R.string.loadingtext), true, false);
		
		try {
			Tools.queryWeb(Functions.getHost(getApplicationContext())+"/categories/get.php?lang="+lang+"&game="+game+"&ypass="+Functions.getPassword(getApplicationContext()), parser);
		} catch (Exception e) {
			e.printStackTrace();
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
			NodeList liste = racine.getElementsByTagName("cat");
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
	   	            android.R.layout.simple_list_item_1);
			
			int game=getIntent().getExtras().getInt("game");
			if(game==0)
			{
				adapter.add(getBaseContext().getText(R.string.allapplications).toString());
				categoriesId.add("-1");
				categoriesName.add(getBaseContext().getText(R.string.allapplications).toString());
			}
			else
			{
				adapter.add(getBaseContext().getText(R.string.allgames).toString());
				categoriesId.add("-2");
				categoriesName.add(getBaseContext().getText(R.string.allgames).toString());
			}
			
			for(int i=0; i<liste.getLength(); i++){
				  Element E1= (Element) liste.item(i);
				  String name="",id="";
				  

				  name=Functions.getDataFromXML(E1,"name");
				  id=Functions.getDataFromXML(E1,"id");
				  
				  adapter.add(name);
				  categoriesId.add(id);
				  categoriesName.add(name);
			  }
			
			ListView listRecommended = (ListView) findViewById(R.id.ListViewCategories);
			listRecommended.setAdapter(adapter);
			
			listRecommended.setOnItemClickListener(new OnItemClickListener() {
			    @SuppressWarnings("rawtypes")
				public void onItemClick(AdapterView parent, View v, int position, long id)
			    {
			    	Intent i = new Intent(CategoriesActivity.this, CategoryActivity.class);
                	
                	Bundle objetbunble = new Bundle();
                    objetbunble.putInt("id",Integer.valueOf(categoriesId.get(position)));
                    objetbunble.putString("name",categoriesName.get(position));
                    i.putExtras(objetbunble );

                    startActivity(i);
			    }
			});
			
			//handlerRecommended.sendEmptyMessage(0);
    		} catch (Exception e) {
    			e.printStackTrace();
    			//handlerError.sendEmptyMessage(0);
    		}
    		
    		mProgress.dismiss();
    	}
    };
}
