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

import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.pixellostudio.tools.Tools;

public class RegisterActivity extends Activity{
	ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		
		setContentView(R.layout.registerscreen);
		
		
		Button registerButton = (Button) findViewById(R.id.ButtonRegister);
        
        registerButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {				
				String username, password,password2,email;
				
				EditText editUsername = (EditText) findViewById(R.id.EditTextUsername);
				EditText editMail = (EditText) findViewById(R.id.EditTextEmail);
				EditText editPassword = (EditText) findViewById(R.id.EditTextPassword1);
				EditText editPassword2 = (EditText) findViewById(R.id.EditTextPassword2);
				username=editUsername.getText().toString();
				email=editMail.getText().toString();
				password=editPassword.getText().toString();
				password2=editPassword2.getText().toString();
				
				if(password.equals(password2))
				{
					progressDialog = ProgressDialog.show(RegisterActivity.this,getText(R.string.registering).toString(), getText(R.string.contactingserver).toString(), true);

					try {
						Tools.queryWeb(Functions.getHost(getApplicationContext())+"/account/register.php?password="+URLEncoder.encode(Tools.sha1("yaamprotection"+password+"echoyaamemee"),"UTF-8")+"&username="+URLEncoder.encode(username)+"&email="+URLEncoder.encode(email)+"&ypass="+Functions.getPassword(getApplicationContext()),registeredHandler);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
					builder.setMessage(R.string.passworddontmatch)
					       .setCancelable(false)
					       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                
					           }
					       });
					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
			}
        });
	}
	



	
	private Handler registeredHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		String content=msg.getData().getString("content");
    		progressDialog.dismiss();
    		
    		if(content.equals("ok"))
    		{    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
				builder.setMessage(R.string.needactivate)
				       .setCancelable(false)
				       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                RegisterActivity.this.finish();
				           }
				       });
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
    		}
    		else
    		{
    			AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
				builder.setMessage(R.string.erroroccured)
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
