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

import greendroid.widget.AsyncImageView;
import greendroid.widget.SegmentedAdapter;
import greendroid.widget.SegmentedHost;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalAdvancedPayment;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalInvoiceItem;
import com.paypal.android.MEP.PayPalReceiverDetails;

public class ShowAppActivity extends BaseActivity {
	private String idApp="",name="",size="",packagename="",versionName="",widget="",price="",screens="",rating="",icon="",description="", devname="", devpaypal="", dlCount="";
	private String url;
	private String isUpdate;
	
	private float fees=0f;
	
	private String discount="0";
	
	private ProgressDialog progressDialog, mProgress;
	
	private Dialog paymentDialog;
	
	private View mView,mView2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setActionBarContentView(R.layout.showappscreen);
		//getActionBar().setTitle(getText(R.string.yaammarket));
		
		super.onCreate(savedInstanceState);		
		
		/*if (PayPal.getInstance().isLibraryInitialized()) 
        	ppObj = PayPal.initWithAppID(this.getBaseContext(), "APP-9VH00888N66449701", PayPal.ENV_LIVE);
        else
        	ppObj=PayPal.getInstance();

        ppObj.setShippingEnabled(false);*/
		
		 //ppObj = PayPal.initWithAppID(this.getBaseContext(), "APP-80W284485P519543T", PayPal.ENV_NONE);
		
		
		
		
		
		/*TabHost mTabHost = (TabHost) this.findViewById(R.id.tabhost);
		mTabHost.setup();
		
        TabSpec tab1=mTabHost.newTabSpec("tab_infos").setIndicator(getText(R.string.app_infos).toString()).setContent(R.id.LinearLayoutInfos);
        mTabHost.addTab(tab1);
        
        TabSpec tab2=mTabHost.newTabSpec("tab_comments").setIndicator(getText(R.string.app_comments).toString()).setContent(R.id.LinearLayoutComments);
        mTabHost.addTab(tab2);
        
        mTabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 40;
        mTabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 40;*/
        
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		mView=inflater.inflate(R.layout.showapp_infos, null, false);
        mView2=inflater.inflate(R.layout.showapp_comments, null, false);
		
		SegmentedHost segmentedHost = (SegmentedHost) findViewById(R.id.segmentedHost);
		 
		AppSegmentedAdapter mAdapter = new AppSegmentedAdapter();
        segmentedHost.setAdapter(mAdapter);

       

        
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth(); 

    	Button buttonBuy = (Button) findViewById(R.id.ButtonBuy);
		buttonBuy.setWidth(width);
		buttonBuy.setVisibility(View.GONE);
	    buttonBuy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(isAppBought())
            	{
            		Functions functions=new Functions();
                	functions.install(idApp, ShowAppActivity.this, getApplicationContext().getResources().getConfiguration());
            	}
            	else
            	{
            		ShowChoosePaymentDial();
               	}
            }
	    });
		
		Button buttonDL = (Button) mView.findViewById(R.id.ButtonDownload);
		buttonDL.setWidth(width);
		buttonDL.setVisibility(View.GONE);
	    buttonDL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Functions functions=new Functions();
            	functions.install(idApp, ShowAppActivity.this, getApplicationContext().getResources().getConfiguration());
            }
	     }); 
	    
	    Button buttonUninstall = (Button) mView.findViewById(R.id.ButtonUninstall);
	    buttonUninstall.setWidth(width/2);
	    buttonUninstall.setVisibility(View.GONE);
	    buttonUninstall.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
	    buttonUninstall.setOnClickListener(new View.OnClickListener() {
	                    public void onClick(View v) {
	                    	Uri uninstallUri = Uri.fromParts("package", packagename, null);  
	                    	Intent intent = new Intent(Intent.ACTION_DELETE, uninstallUri);
	                    	startActivityForResult(intent,2);  
	                    }
	            	});
	    
	    Button buttonOpen = (Button) mView.findViewById(R.id.ButtonOpen);
	    buttonOpen.setWidth(width/2);
	    buttonOpen.setVisibility(View.GONE);
	    buttonOpen.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_more, 0, 0, 0);
	    buttonOpen.setOnClickListener(new View.OnClickListener() {
	                    public void onClick(View v) {
	                    	Intent i = new Intent(Intent.ACTION_MAIN);
	                    	
	                    	PackageManager pm=getPackageManager();
	                    	
	                    	List<ResolveInfo> launchables=pm.queryIntentActivities(i, 0);
	                    	
	                    	for(int j=0;j<launchables.size();j++)
	                    	{
		                		ActivityInfo activity=launchables.get(j).activityInfo;
		                		if(activity.applicationInfo.packageName.equals(packagename))
		                		{
			                		ComponentName name=new ComponentName(activity.applicationInfo.packageName, activity.name);
			                    	
			                		Intent intent=new Intent(Intent.ACTION_MAIN);
			                		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			                								Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			                		intent.setComponent(name);
		
			                    	startActivity(intent);
			                    	
			                    	j=launchables.size();
		                		}
	                    	}
	                    }
	            	});
    	
	    
	    
	    Button buttonCommentAndRate = (Button) mView2.findViewById(R.id.ButtonComment);
	    buttonCommentAndRate.setWidth(width);
	    buttonCommentAndRate.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_edit, 0, 0, 0);
	    buttonCommentAndRate.setOnClickListener(new View.OnClickListener() {
	                    public void onClick(View v) {
	                    	Intent intent = new Intent(ShowAppActivity.this,CommentActivity.class);
	                    	Bundle objetbunble = new Bundle();
	                        objetbunble.putInt("id",Integer.valueOf(idApp));
	                        intent.putExtras(objetbunble );
	                    	startActivityForResult(intent,3);
	                    }
	            	});
        
        
        
        
        
        
    	if(this.getIntent().getExtras().getInt("id")!=0)
    	{
    		String lang=getApplicationContext().getResources().getConfiguration().locale.getISO3Language();
    		idApp=String.valueOf(getIntent().getExtras().getInt("id"));

        	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(ShowAppActivity.this);  
    		String username=pref.getString("username", "");
        	try {
				url = Functions.getHost(getApplicationContext())+"/apps/application.php?username="+URLEncoder.encode(username,"UTF-8")+"&appid="+idApp+"&lang="+lang+"&ypass="+Functions.getPassword(getApplicationContext());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	
    		//LoadInfos();
    	}
    	
    	else if(this.getIntent().getExtras().getString("package")!=null)
    	{
    		try {
        		String packagename2=this.getIntent().getExtras().getString("package");
				Tools.queryWeb(Functions.getHost(getApplicationContext())+"/apps/idFromPackage.php?package="+URLEncoder.encode(packagename2,"UTF-8")+"&ypass="+Functions.getPassword(getApplicationContext()),gotId);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	}

    	
	}
	
	@Override
	protected void onResume()
	{
		LoadInfos();
		
		super.onResume();
	}
	
	
	private class AppSegmentedAdapter extends SegmentedAdapter {
		 
        public boolean mReverse = false;
 
        @Override
        public View getView(int position, ViewGroup parent) {            
            if(position==0)
            {
            	return mView;
            }
            else
            {
            	return mView2;
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
                    return "Informations";
                case 1:
                    return "Comments";
            }
 
            return null;
        }
    }
	
	
	
	public void LoadInfos()
	{
		mProgress = ProgressDialog.show(this, this.getText(R.string.loading),
                this.getText(R.string.loadingtext), true, false);
		
		try {
			Tools.queryWeb(url, parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Handler gotId=new Handler()
    {
    	public void handleMessage(Message msg) {
    		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(ShowAppActivity.this);  
    		String lang=getApplicationContext().getResources().getConfiguration().locale.getISO3Language();
    		idApp=msg.getData().getString("content");
    		String username=pref.getString("username", "");
        	try {
				url = Functions.getHost(getApplicationContext())+"/apps/application.php?username="+URLEncoder.encode(username,"UTF-8")+"&appid="+idApp+"&lang="+lang+"&ypass="+Functions.getPassword(getApplicationContext());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

    		//LoadInfos();
    	}
    };
	
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
			
			for(int i=0; i<liste.getLength(); i++){
				  Element E1= (Element) liste.item(i);				  

				  name=Functions.getDataFromXML(E1,"name");
				  description=Functions.getDataFromXML(E1,"description");
				  size=Functions.getDataFromXML(E1,"size");
				  price=Functions.getDataFromXML(E1,"price");
				  versionName=Functions.getDataFromXML(E1,"version");
				  dlCount=Functions.getDataFromXML(E1,"dlCount");
				  screens=Functions.getDataFromXML(E1,"screens");
				  icon=Functions.getDataFromXML(E1,"icon");
				  idApp=Functions.getDataFromXML(E1,"id");
				  rating=Functions.getDataFromXML(E1,"rating");
				  //TODO : youtube=Functions.getDataFromXML(E1,"youtube");
				  packagename=Functions.getDataFromXML(E1,"packagename");
				  devname=Functions.getDataFromXML(E1,"devname");
				  widget=Functions.getDataFromXML(E1,"widget");
				  devpaypal=Functions.getDataFromXML(E1,"devpaypal");
				  
				  isUpdate=Functions.getDataFromXML(E1,"update");
				  
				  fees=Float.valueOf(Functions.getDataFromXML(E1,"fees"));
			  }
			
			description+="<br /><br /><i>"+dlCount+" "+getBaseContext().getText(R.string.downloads)+". Version "+versionName+"</i><br />" +
					"Size : "+size;
			  
			description+="<br /><br />";
			String[] screensList=screens.split(";");
			for(int y=0;y<screensList.length;y++)
			{
				if(!screensList[y].equals(""))
					description+="<a href='screens://"+screensList[y]+"'><img src='"+screensList[y]+"' width='100px' /></a> ";
			}
			
			  if(widget.equals("1"))
			  {
				  description+="<br /><br /><b>"+getBaseContext().getText(R.string.applicationcontainswidget)+"</b>";
			  }
			
			  //TextView nameApp=(TextView) findViewById(R.id.nameApp);
        	  TextView authorApp=(TextView) findViewById(R.id.authorApp);
			  WebView descriptionApp=(WebView) mView.findViewById(R.id.descriptionApp);
			  AsyncImageView iconApp=(AsyncImageView) findViewById(R.id.IconApp);
			  
			  Button buttonDL=(Button) mView.findViewById(R.id.ButtonDownload);
			  Button buttonUninstall=(Button) mView.findViewById(R.id.ButtonUninstall);
			  Button buttonBuy=(Button) mView.findViewById(R.id.ButtonBuy);
			  Button buttonOpen=(Button) mView.findViewById(R.id.ButtonOpen);
			  
			  buttonDL.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_add, 0, 0, 0);
			  buttonBuy.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_add, 0, 0, 0);
			
			  //nameApp.setText("");
			  getActionBar().setTitle(name);
			  authorApp.setText(getBaseContext().getText(R.string.by)+" "+devname);
			  descriptionApp.loadDataWithBaseURL(null,description,"text/html", "UTF-8","about:blank");
			  descriptionApp.getSettings().setPluginsEnabled(true);
			  
			  descriptionApp.setWebViewClient(new WebViewClientScreens());
			  descriptionApp.setBackgroundColor(Color.parseColor("#fbf8f4"));
			  
			  iconApp.setDefaultImageResource(R.drawable.defaultappicon);
			  if(icon!="")
	  	      {
				  iconApp.setUrl(icon);
	  	      }
			  
			  
			float rate=Float.valueOf(rating);
			if(rate==0)
				 ((RatingBar) findViewById(R.id.ratingbar)).setVisibility(View.INVISIBLE);
			 else
				 ((RatingBar) findViewById(R.id.ratingbar)).setRating(rate);
	        	
	        	
			
	        	

	    		
	    		
	    		
	    		
	    		
	    		NodeList nodeComments = racine.getElementsByTagName("comments");
	    		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	    		
				for(int i1=0; i1<nodeComments.getLength(); i1++){
					  Element E1= (Element) nodeComments.item(i1);
					  String name="",comment="",phone="";
					  
					  name=Functions.getDataFromXML(E1,"username");
					  
					  comment=Functions.getDataFromXML(E1,"comment");
					  
					  phone=Functions.getDataFromXML(E1,"phone");
					  
					  HashMap<String, String> map = new HashMap<String, String>();
					  map.put("name", name+" ("+phone+")");
					  map.put("comment", comment);
					  mylist.add(map);
				}
			
			
				ListCommentsAdapter adapterComments = new ListCommentsAdapter(getBaseContext(), mylist, R.layout.commentslistlayout,
						new String[] {"name", "comment"},new int[] {R.id.nameCommentsList, R.id.commentCommentsList});
				ListView listComments=(ListView) mView2.findViewById(R.id.listViewComments);
				listComments.setAdapter(adapterComments);
			
			
			
	        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			int width = display.getWidth(); 
			
	        Button buttonCommentAndRate = (Button) mView2.findViewById(R.id.ButtonComment);
	        buttonDL.setWidth(width);
	        
			if(isAppInstalled())
	    	{
	    		if(isUpdate.equals("1"))
	    		{
	    			buttonOpen.setVisibility(View.GONE);
	    			buttonUninstall.setVisibility(View.VISIBLE);
	    			buttonBuy.setVisibility(View.GONE);
	    			buttonDL.setVisibility(View.VISIBLE);
	    			buttonDL.setText(R.string.update);
	    			buttonDL.setWidth(width/2);
	    		}
	    		else
	    		{
					buttonOpen.setVisibility(View.VISIBLE);
	    			buttonUninstall.setVisibility(View.VISIBLE);
	    			buttonBuy.setVisibility(View.GONE);
	    			buttonDL.setVisibility(View.GONE);
	    		}
	    			
	    		buttonCommentAndRate.setVisibility(View.VISIBLE);
	    	}
	    	else
	    	{
	    		if(Float.valueOf(price)>0)
	    		{
	    			if(isAppBought())
	    			{
	    				buttonOpen.setVisibility(View.GONE);
		    			buttonUninstall.setVisibility(View.GONE);
		    			buttonBuy.setVisibility(View.GONE);
		    			buttonDL.setVisibility(View.VISIBLE);
		    			buttonDL.setText(R.string.download);
	    			}
	    			else
	    			{
	    				buttonOpen.setVisibility(View.GONE);
		    			buttonUninstall.setVisibility(View.GONE);
		    			buttonBuy.setVisibility(View.VISIBLE);
		    			buttonBuy.setText(R.string.buy);
		    			buttonDL.setVisibility(View.GONE);
	    			}
	    		}
	    		else
	    		{
	    			buttonOpen.setVisibility(View.GONE);
	    			buttonUninstall.setVisibility(View.GONE);
	    			buttonBuy.setVisibility(View.GONE);
	    			buttonDL.setVisibility(View.VISIBLE);
	    			buttonDL.setText(R.string.download);
	    		}
	    		
	    		buttonCommentAndRate.setVisibility(View.GONE);
	    	}
			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		
    		mProgress.dismiss();
    	}
    };
	
	
	
	
	
	
	
	
	
	/*boolean isUpdate()
    {
    	PackageManager pm=this.getApplicationContext().getPackageManager();
		
		try {
			PackageInfo infos=pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
						
			if(infos!=null)
			{
				if(versionName.equals(infos.versionName))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
				return false;
		} catch (NameNotFoundException e) {
			return false;
		}
    }*/
	
	public boolean isAppInstalled()
    {
			PackageManager pm=this.getApplicationContext().getPackageManager();
			
			try {
				pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
			} catch (NameNotFoundException e) {
				return false;
			}
			
			return true;
    }
    
    public boolean isAppBought()
    {
    	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	String name=pref.getString("username", "");
    	
    	String txt="";
		try {
			txt = Tools.queryWeb(Functions.getHost(getApplicationContext())+"/apps/isAppBought.php?username="+URLEncoder.encode(name,"UTF-8")+"&appid="+idApp+"&ypass="+Functions.getPassword(getApplicationContext()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
		if(txt.equals("bought"))
		{
			return true;
		}
		else
			return false;
    }

    
    //Load screens
    private class WebViewClientScreens extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	if(url.startsWith("screens://"))
        	{
        		Intent i = new Intent(ShowAppActivity.this, WatchScreensActivity.class);
            	
            	Bundle objetbunble = new Bundle();
                objetbunble.putString("url",url.replace("screens://", ""));
                i.putExtras(objetbunble );

                startActivity(i);
        	}
        	else
        	{
        		Intent viewIntent = new Intent("android.intent.action.VIEW",Uri.parse(url));  
        		startActivity(viewIntent);
        	}
        	
            return true;
        }
    }
    
    public Handler boughtHandler=new Handler()
    {
    	public void handleMessage(Message msg) {
    		progressDialog.dismiss();
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(ShowAppActivity.this);
        	if(isAppBought())
        	{
    			builder.setMessage(R.string.successbought)
    			       .setCancelable(false)
    			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			        	   //LoadInfos();
    			           }
    			       });
        	}
        	else
        	{
        		builder.setMessage(R.string.errorbought)
    		       .setCancelable(false)
    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		        	   //LoadInfos();
    		           }
    		       });
        	}
    		
    		AlertDialog alertDialog = builder.create();
    		alertDialog.show();
    	}
    };
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
    	if(requestCode==5) //Paypal Payment
    	{
    		progressDialog.dismiss();
    		paymentDialog.dismiss();
    		
    		switch(resultCode) {
    		case Activity.RESULT_OK:
    			/*		try {
    						int iDiscount=Integer.valueOf(discount);
    						float toPay=(float) (Float.valueOf(price)-(Float.valueOf(price)*iDiscount/100));
    						SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    				    	String name=pref.getString("username", "");
    						String url2 = Functions.getHost(getApplicationContext())+"/apps/buy.php?price="+(toPay*(100-fees)/100)+"&ypass="+Functions.getPassword(getApplicationContext())+"&username="+URLEncoder.encode(name,"UTF-8")+"&id="+idApp;
    						Tools.queryWeb(url2);
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    			
    			//LoadInfos();
    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.successbought)
				       .setCancelable(false)
				       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                // put your code here
				           }
				       });
				
				AlertDialog alertDialog = builder.create();
				alertDialog.show();*/
    			
    			progressDialog = ProgressDialog.show(ShowAppActivity.this,"", "Buying Application on YAAM...", true);
         		progressDialog.show();
         		
         		
         	    
         	   try {
					int iDiscount=Integer.valueOf(discount);
					float toPay=(float) (Float.valueOf(price)-(Float.valueOf(price)*iDiscount/100));
					SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			    	String name=pref.getString("username", "");
					String url2 = Functions.getHost(getApplicationContext())+"/apps/buy.php?price="+(toPay*(100-fees)/100)+"&ypass="+Functions.getPassword(getApplicationContext())+"&username="+URLEncoder.encode(name,"UTF-8")+"&id="+idApp;
					Tools.queryWeb(url2,boughtHandler);
				} catch (Exception e) {
					e.printStackTrace();
				}
         	    
         		
    			break;
    		}
    		
    		
    	}
    	else if(requestCode==2) //Uninstalled App
    	{
    		if(isAppInstalled()==false)
    		{
    			SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		    	String name=pref.getString("username", "");
    			try {
					Tools.queryWeb(Functions.getHost(getApplicationContext())+"/apps/uninstall.php?ypass="+Functions.getPassword(getApplicationContext())+"&username="+URLEncoder.encode(name,"UTF-8")+"&appid="+idApp);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
    		}
    	}
    	else if(requestCode==3) //Comment App
    	{
    		
    	}
    	
    	//LoadInfos();
    }
    
    
    
    
    public void ShowChoosePaymentDial()
    {
    	paymentDialog = new Dialog(this);

    	paymentDialog.setContentView(R.layout.choosepaymentdialog);
    	paymentDialog.setTitle("");
    	paymentDialog.setCancelable(true);
    	
    	progressDialog = ProgressDialog.show(this,"", "Initializing Paypal...", true);
		progressDialog.show();

		Thread libraryPaypalInitializationThread = new Thread() {
			public void run() {
				initPaypalLibrary();
			}
		};
		libraryPaypalInitializationThread.start();
    	
         Button refundButton = (Button) paymentDialog.findViewById(R.id.ButtonDiscount);
         refundButton.setVisibility(View.GONE);
         refundButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
        		//TODO : ShowRefundDial();
             }
     	}); 
    }
    
    
    
    
    
    
    
    
    
    
    private void initPaypalLibrary() {
		PayPal pp = PayPal.getInstance();
		if(pp == null) {
			Log.d("YAAM","Initializing Paypal...");
			pp = PayPal.initWithAppID(this, "APP-9VH00888N66449701", PayPal.ENV_LIVE); //TODO:
        	//pp.setLanguage("en_US"); // Sets the language for the library.

        	pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER); 
        	pp.setShippingEnabled(false);
		}
		
		if (PayPal.getInstance().isLibraryInitialized()) {	    	
	    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    	params.bottomMargin = 10;
	    	
	        LinearLayout paypalButton = pp.getCheckoutButton(this,
	        		 PayPal.BUTTON_278x43, CheckoutButton.TEXT_PAY);
	        paypalButton.setLayoutParams(params);
	         ((LinearLayout)paymentDialog.findViewById(R.id.layout_root)).addView(paypalButton);
	         
	         
	         paypalButton.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	progressDialog = ProgressDialog.show(ShowAppActivity.this,"", "Loading Paypal...", true);
	         		progressDialog.show();
	            	 
	         		Thread thread = new Thread() {
	        			public void run() {
			            	int iDiscount=Integer.valueOf(discount);
			            	float toPay=(float) (Float.valueOf(price)-(Float.valueOf(price)*iDiscount/100));

			            	/*PayPalPayment newPayment = new PayPalPayment();
			         		newPayment.setSubtotal((new BigDecimal(toPay)).round(MathContext.DECIMAL32));
			         		newPayment.setCurrencyType("EUR");
			         		newPayment.setRecipient("paypal@yaam.mobi");
			         		newPayment.setDescription("YAAM application : "+name+" ("+price+"€)");
			         		newPayment.setMerchantName("YAAM Market");
			         		newPayment.setPaymentType(PayPal.PAYMENT_TYPE_SERVICE);

			         		Intent checkoutIntent = PayPal.getInstance().checkout(newPayment, ShowAppActivity.this);
			         				
			         		startActivityForResult(checkoutIntent, 5);*/
			            	
			            	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    				    	String name=pref.getString("username", "");
    				    	
			            	PayPalAdvancedPayment payment = new PayPalAdvancedPayment();
			         		payment.setCurrencyType("EUR");
			         		//payment.setMerchantName("YAAM Market");
			         		try {
								payment.setIpnUrl(Functions.getHost(getApplicationContext())+"/apps/buy_ipn.php");
							} catch (Exception e) {
								e.printStackTrace();
							}
			         		payment.setMemo("YAAM application : "+name+" ("+price+"€)");
			         		
			         		/*PayPalReceiverDetails receiverYAAM = new PayPalReceiverDetails();
			         		receiverYAAM.setRecipient("paypal@yaam.mobi");
			         		receiverYAAM.setSubtotal((new BigDecimal(toPay*fees/100)).round(MathContext.DECIMAL32));
			         		//receiverYAAM.setSubtotal((new BigDecimal("10.0")).round(MathContext.DECIMAL32));
			         		receiverYAAM.setIsPrimary(false);
			         		receiverYAAM.setPaymentType(PayPal.PAYMENT_TYPE_SERVICE);
			         		receiverYAAM.setMerchantName("YAAM Market");
			         		receiverYAAM.setDescription("YAAM fees ("+fees+"%)");
			         		receiverYAAM.setPaymentSubtype(PayPal.PAYMENT_SUBTYPE_NONE);
		 
			        		PayPalInvoiceData invoice1 = new PayPalInvoiceData();	
			        		PayPalInvoiceItem item1 = new PayPalInvoiceItem();
			            	item1.setName("YAAM fees (15%)");
			            	item1.setTotalPrice((new BigDecimal(toPay*fees/100)).round(MathContext.DECIMAL32));
			           
			            	invoice1.getInvoiceItems().add(item1);
			         		
			            	receiverYAAM.setInvoiceData(invoice1);
			        		payment.getReceivers().add(receiverYAAM);*/
		
			        		
			        		
			        		
		
			        		PayPalReceiverDetails receiverDev = new PayPalReceiverDetails();
			        		receiverDev.setRecipient(devpaypal);
			        		receiverDev.setSubtotal((new BigDecimal(String.valueOf(toPay*(100-fees)/100))).round(MathContext.DECIMAL32));
			        		receiverDev.setIsPrimary(false);
			        		receiverDev.setPaymentType(PayPal.PAYMENT_TYPE_SERVICE);
			        		receiverDev.setMerchantName("Developer "+devname);
			        		receiverDev.setDescription("Developer benefits ("+(100-fees)+"%)");
			        		receiverDev.setPaymentSubtype(PayPal.PAYMENT_SUBTYPE_NONE);
			        		
			        		PayPalInvoiceData invoice2 = new PayPalInvoiceData();	
			        		PayPalInvoiceItem item2 = new PayPalInvoiceItem();
			            	item2.setName("Developer benefits ("+(100-fees)+"%)");
			            	item2.setTotalPrice((new BigDecimal(String.valueOf(toPay*(100-fees)/100))).round(MathContext.DECIMAL32));
			            	invoice2.getInvoiceItems().add(item2);
			         		
			            	receiverDev.setInvoiceData(invoice2);
			        		payment.getReceivers().add(receiverDev);
		
			        		
			        		Intent paypalIntent = PayPal.getInstance().checkout(payment, ShowAppActivity.this);
			        		startActivityForResult(paypalIntent, 5);
	        			}
	         		};
	         	
	        		thread.run();
	         	}
	     	}); 
		}
		else {
			Log.d("YAAM", "Error while initializing paypal!");
		}
		
		progressDialog.dismiss();
		
		showPaymentDial.sendEmptyMessage(0);
		
	}
    public Handler showPaymentDial=new Handler()
    {
    	public void handleMessage(Message msg) {
    		paymentDialog.show();
    	}
    };
    
    
    
    public class ListCommentsAdapter  extends SimpleAdapter{
  	  //le constructeur
  	 public ListCommentsAdapter (Context context,
  	            ArrayList<HashMap<String, String>> data, 
  	            int resource,
  	            String[] from,
  	            int[] to) { 
  	                super(context, data, resource, from, to); 
  	        } 
  	 
  	 public View getView(int position, View convertView, ViewGroup parent) { 
  	    	View v = super.getView(position, convertView, parent); 
  	 
  	        return v; 
  	    }
  	}
}
