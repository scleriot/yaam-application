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
import java.io.FileWriter;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;


public class LoginActivity extends Activity{
	ProgressDialog progressDialog;
	String terminal="phone";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        
        setContentView(R.layout.loginscreen);

        // Ensure /sdcard/.yaam directory is present
        Tools.createYAAMDir();
        
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);  
        if(pref.getBoolean("connected1", false))
        {
        	Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            this.finish();
        }
        
        RadioButton radio1=(RadioButton) findViewById(R.id.RadioPhone);
        radio1.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				terminal="phone";
			}
        });
        RadioButton radio2=(RadioButton) findViewById(R.id.RadioTablet);
        radio2.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				terminal="tablet";
			}
        });
        RadioButton radio3=(RadioButton) findViewById(R.id.RadioGoogleTV);
        radio3.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				terminal="gtv";
			}
        });
        
        
        Button loginButton = (Button) findViewById(R.id.ButtonLogin);
        
        loginButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				progressDialog = ProgressDialog.show(LoginActivity.this,getText(R.string.logging).toString(), getText(R.string.contactingserver).toString(), true);
				
				String username, password;
				
				EditText editUsername = (EditText) findViewById(R.id.EditTextUsername);
				EditText editPassword = (EditText) findViewById(R.id.EditTextPassword);
				username=editUsername.getText().toString();
				password=editPassword.getText().toString();
				
				try {
					Tools.queryWeb(Functions.getHost(getApplicationContext())+"/account/login.php?password="+URLEncoder.encode(Tools.sha1("yaamprotection"+password+"echoyaamemee"),"UTF-8")+"&username="+URLEncoder.encode(username)+"&ypass="+Functions.getPassword(getApplicationContext()),connectedHandler);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        	
        });
        
        

        Button registerButton = (Button) findViewById(R.id.ButtonRegister);
        
        registerButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
			}
        });
    }
    
    private Handler connectedHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		progressDialog.dismiss();
    		String content=msg.getData().getString("content");
    		
    		if(content.equals("ok"))
    		{    			
    			EditText editUsername = (EditText) findViewById(R.id.EditTextUsername);
    			EditText editPassword = (EditText) findViewById(R.id.EditTextPassword);
    			SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(LoginActivity.this.getApplicationContext());
  				
    			
  				Editor editor=pref.edit();
  				editor.putBoolean("connected1", true);
  				editor.putString("username", editUsername.getText().toString());
  				editor.putString("password", Tools.sha1("yaamprotection"+editPassword.getText().toString()+"echoyaamemee"));
  				editor.putString("terminal", terminal);
  				editor.commit();
    			
  				
  				File file=new File(Environment.getExternalStorageDirectory().toString()+"/.yaam/user");
  	            file.delete();

  	            try {
  	            	String username=Tools.sha1(pref.getString("username", "").toUpperCase()+"YAAMISTHEBEST");
  	            	
  		        	FileWriter writer = new FileWriter(file);
  		        	
  		        	writer.append(username);
  		            writer.flush();
  		            writer.close();
  	            } 
  	            catch (Exception e) { 
  	            	e.printStackTrace();
  	            } 
  	            
  	            
  	            file=new File(Environment.getExternalStorageDirectory().toString()+"/.yaam/password");
  	            file.delete();

  	            try {
  	            	String pass=Tools.sha1("yaamprotection"+editPassword.getText().toString()+"echoyaamemee");
  	            	
  		        	FileWriter writer = new FileWriter(file);
  		        	
  		        	writer.append(pass);
  		            writer.flush();
  		            writer.close();
  	            } 
  	            catch (Exception e) { 
  	            	e.printStackTrace();
  	            } 
  				
  				
  				
  				Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
    		}
    		else
    		{
    			AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
				builder.setMessage(R.string.errorconnection)
				       .setCancelable(false)
				       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                
				           }
				       });
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
    		}
    	}
    };
    
}
