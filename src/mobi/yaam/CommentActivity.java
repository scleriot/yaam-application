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

import java.net.URLEncoder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;


public class CommentActivity extends BaseActivity implements OnRatingBarChangeListener{
	ProgressDialog progressDialog;
	String terminal="phone";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        
        setContentView(R.layout.commentscreen);
        
       
        
        Button commentButton = (Button) findViewById(R.id.ButtonComment);
        
        commentButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				progressDialog = ProgressDialog.show(CommentActivity.this,"", getText(R.string.contactingserver).toString(), true);
				
				String idApp=String.valueOf(getIntent().getExtras().getInt("id"));
		    	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(CommentActivity.this);
		    	String username=pref.getString("username", "");
		    	
				EditText editComment = (EditText) findViewById(R.id.EditTextComment);
				String comment=editComment.getText().toString();
				
				int rating=((RatingBar) findViewById(R.id.ratingbar)).getNumStars();
				
				String phone=Build.MODEL;
		        String sdk=Build.VERSION.SDK;
		        String lang=getApplicationContext().getResources().getConfiguration().locale.getISO3Language();
				
				try {
					Tools.queryWeb(Functions.getHost(getApplicationContext())+"/comments/add.php?rating="+rating+"&phone="+URLEncoder.encode(phone)+"&sdk="+URLEncoder.encode(sdk)+"&lang="+URLEncoder.encode(lang)+"&appid="+idApp+"&comment="+URLEncoder.encode(comment)+"&username="+URLEncoder.encode(username)+"&ypass="+Functions.getPassword(getApplicationContext()),commentHandler);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        	
        });
        
        
        ((RatingBar)findViewById(R.id.ratingbar)).setOnRatingBarChangeListener(this); 
    }

    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromTouch) { 
    	((EditText) findViewById(R.id.EditTextComment)).setVisibility(View.VISIBLE);
    }
    
    private Handler commentHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		progressDialog.dismiss();
    		String content=msg.getData().getString("content");
    		
    		if(content.equals("ok"))
    		{    			
                finish();
    		}
    		else
    		{
    			AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
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
