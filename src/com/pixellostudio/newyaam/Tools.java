package com.pixellostudio.newyaam;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.ByteArrayBuffer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Tools
{
	public static void queryWeb(final String url, final Handler handler)
	{		
	    class LoadURL extends AsyncTask<Object, Object, Integer> {
	    	
	    	private final int NETWORK_ERROR = -1, ERROR = 0, SUCCESS = 1;
	    	String txt="";
	    	private int number;
	    	
	    	@Override
			protected Integer doInBackground(Object... params) {    
	    		number=0;
	    		txt="";
	            try {  
	                URL updateURL = new URL(url);  
	                URLConnection conn = updateURL.openConnection();  
	                InputStream is = conn.getInputStream();  
	                BufferedInputStream bis = new BufferedInputStream(is);  
	                ByteArrayBuffer baf = new ByteArrayBuffer(50);  
	  
	                int current = 0;  
	                while((current = bis.read()) != -1){  
	                    baf.append((byte)current);  
	                }  
	                
	                /* Convert the Bytes read to a String. */  
	                txt = new String(baf.toByteArray());
	                
	            } catch (IOException e) {
	            	return NETWORK_ERROR;
	            	
	            } catch (Exception e) {  
	            	e.printStackTrace();
	            	return ERROR;
	            }
	    		
	    		return SUCCESS;
			}
	    	
	    	@Override
	    	protected void onCancelled()
	    	{
	    		this.execute();
	    	}
	    	
	    	@Override
	    	protected void onPostExecute(Integer result) {
	    		switch (result) {
	    			case NETWORK_ERROR:
	    				if(number<10)
	    				{
		    				Log.i("YAAM", "Network unavailable");
		    				this.cancel(true);
	    				}
	    				number++;
	    				break;
	    			case ERROR:
	    				
	    				
	    				break;
	    			case SUCCESS:
		                Message msg=new Message();
		                Bundle data=new Bundle();
		                data.putString("content", txt);
		                msg.setData(data);
		                
		                handler.sendMessage(msg); 
	    				break;
	    		}
	    	}
	    } 
	    
	    new LoadURL().execute();
	}
	
	public static String queryWeb(final String url)
	{		
	        	String txt="";
	            try {  
	                URL updateURL = new URL(url);  
	                URLConnection conn = updateURL.openConnection();  
	                InputStream is = conn.getInputStream();  
	                BufferedInputStream bis = new BufferedInputStream(is);  
	                ByteArrayBuffer baf = new ByteArrayBuffer(50);  
	  
	                int current = 0;  
	                while((current = bis.read()) != -1){  
	                    baf.append((byte)current);  
	                }  
	  
	                /* Convert the Bytes read to a String. */  
	                txt = new String(baf.toByteArray());
	                return txt;
	            } catch (Exception e) {  
	            	e.printStackTrace();
	            	return "";
	            }  
	}
	
	public static AlertDialog.Builder dialog(Context context,String title,String txt,String buttonOk)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(txt)
		       .setCancelable(false)
		       .setPositiveButton(buttonOk, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           };
		       });
		builder.setTitle(title);
		return builder;
	}
	public static AlertDialog.Builder dialog(Context context,String title,int txt,String buttonOk)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getResources().getString(txt))
		       .setCancelable(false)
		       .setPositiveButton(buttonOk, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           };
		       });
		builder.setTitle(title);
		return builder;
	}
	
	public static Bitmap loadImageFromUrl(String url)
	{
		URL aURL;
		try {
			aURL = new URL(url);
			
			URLConnection conn = aURL.openConnection(); 
            conn.connect(); 
            InputStream is = conn.getInputStream(); 
            BufferedInputStream bis = new BufferedInputStream(is); 
            Bitmap bm = BitmapFactory.decodeStream(bis); 
            bis.close(); 
            is.close();
            
	        return bm;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public static String md5(String s) {  
	    try {  
	        // Create MD5 Hash  
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");  
	        digest.update(s.getBytes());  
	        byte messageDigest[] = digest.digest();  
	          
	        // Create Hex String  
	        StringBuffer hexString = new StringBuffer();  
	        for (int i=0; i<messageDigest.length; i++)  
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));  
	        return "0"+hexString.toString();  
	          
	    } catch (NoSuchAlgorithmException e) {  
	        e.printStackTrace();  
	    }  
	    return "";  
	}
	
	private static String convertToHex(byte[] data)
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++)
        {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do
            {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            }
            while(two_halfs++ < 1);
        }
        return buf.toString();
    }

	
	public static String sha1(String s){  
		MessageDigest md;
        try {
        	md = MessageDigest.getInstance("SHA-1");
        	byte[] sha1hash = new byte[40];
			md.update(s.getBytes("iso-8859-1"), 0, s.length());
			sha1hash = md.digest();
	        return convertToHex(sha1hash);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
    public static class HTMLEntities {
     
      private static Map<String, Character> HTML_ENTITIES;
     
      private static Map<Character, String> UTF8_CHARS;
     
      private static void initEntities() {
        HTML_ENTITIES = new HashMap<String, Character>();
        HTML_ENTITIES.put("&acute;", '\u00B4');
        HTML_ENTITIES.put("&quot;", '\"');
        HTML_ENTITIES.put("&amp;", '\u0026');
        HTML_ENTITIES.put("&lt;", '\u003C');
        HTML_ENTITIES.put("&gt;", '\u003E');
        HTML_ENTITIES.put("&nbsp;", '\u00A0');
        HTML_ENTITIES.put("&iexcl;", '\u00A1');
        HTML_ENTITIES.put("&cent;", '\u00A2');
        HTML_ENTITIES.put("&pound;", '\u00A3');
        HTML_ENTITIES.put("&curren;", '\u00A4');
        HTML_ENTITIES.put("&yen;", '\u00A5');
        HTML_ENTITIES.put("&brvbar;", '\u00A6');
        HTML_ENTITIES.put("&sect;", '\u00A7');
        HTML_ENTITIES.put("&uml;", '\u00A8');
        HTML_ENTITIES.put("&copy;", '\u00A9');
        HTML_ENTITIES.put("&ordf;", '\u00AA');
        HTML_ENTITIES.put("&laquo;", '\u00AB');
        HTML_ENTITIES.put("&not;", '\u00AC');
        HTML_ENTITIES.put("&shy;", '\u00AD');
        HTML_ENTITIES.put("&reg;", '\u00AE');
        HTML_ENTITIES.put("&macr;", '\u00AF');
        HTML_ENTITIES.put("&deg;", '\u00B0');
        HTML_ENTITIES.put("&plusmn;", '\u00B1');
        HTML_ENTITIES.put("&sup2;", '\u00B2');
        HTML_ENTITIES.put("&sup3;", '\u00B3');
        HTML_ENTITIES.put("&acute;", '\u00B4');
        HTML_ENTITIES.put("&micro;", '\u00B5');
        HTML_ENTITIES.put("&para;", '\u00B6');
        HTML_ENTITIES.put("&middot;", '\u00B7');
        HTML_ENTITIES.put("&cedil;", '\u00B8');
        HTML_ENTITIES.put("&sup1;", '\u00B9');
        HTML_ENTITIES.put("&ordm;", '\u00BA');
        HTML_ENTITIES.put("&raquo;", '\u00BB');
        HTML_ENTITIES.put("&frac14;", '\u00BC');
        HTML_ENTITIES.put("&frac12;", '\u00BD');
        HTML_ENTITIES.put("&frac34;", '\u00BE');
        HTML_ENTITIES.put("&iquest;", '\u00BF');
        HTML_ENTITIES.put("&Agrave;", '\u00C0');
        HTML_ENTITIES.put("&Aacute;", '\u00C1');
        HTML_ENTITIES.put("&Acirc;", '\u00C2');
        HTML_ENTITIES.put("&Atilde;", '\u00C3');
        HTML_ENTITIES.put("&Auml;", '\u00C4');
        HTML_ENTITIES.put("&Aring;", '\u00C5');
        HTML_ENTITIES.put("&AElig;", '\u00C6');
        HTML_ENTITIES.put("&Ccedil;", '\u00C7');
        HTML_ENTITIES.put("&Egrave;", '\u00C8');
        HTML_ENTITIES.put("&Eacute;", '\u00C9');
        HTML_ENTITIES.put("&Ecirc;", '\u00CA');
        HTML_ENTITIES.put("&Euml;", '\u00CB');
        HTML_ENTITIES.put("&Igrave;", '\u00CC');
        HTML_ENTITIES.put("&Iacute;", '\u00CD');
        HTML_ENTITIES.put("&Icirc;", '\u00CE');
        HTML_ENTITIES.put("&Iuml;", '\u00CF');
        HTML_ENTITIES.put("&ETH;", '\u00D0');
        HTML_ENTITIES.put("&Ntilde;", '\u00D1');
        HTML_ENTITIES.put("&Ograve;", '\u00D2');
        HTML_ENTITIES.put("&Oacute;", '\u00D3');
        HTML_ENTITIES.put("&Ocirc;", '\u00D4');
        HTML_ENTITIES.put("&Otilde;", '\u00D5');
        HTML_ENTITIES.put("&Ouml;", '\u00D6');
        HTML_ENTITIES.put("&times;", '\u00D7');
        HTML_ENTITIES.put("&Oslash;", '\u00D8');
        HTML_ENTITIES.put("&Ugrave;", '\u00D9');
        HTML_ENTITIES.put("&Uacute;", '\u00DA');
        HTML_ENTITIES.put("&Ucirc;", '\u00DB');
        HTML_ENTITIES.put("&Uuml;", '\u00DC');
        HTML_ENTITIES.put("&Yacute;", '\u00DD');
        HTML_ENTITIES.put("&THORN;", '\u00DE');
        HTML_ENTITIES.put("&szlig;", '\u00DF');
        HTML_ENTITIES.put("&agrave;", '\u00E0');
        HTML_ENTITIES.put("&aacute;", '\u00E1');
        HTML_ENTITIES.put("&acirc;", '\u00E2');
        HTML_ENTITIES.put("&atilde;", '\u00E3');
        HTML_ENTITIES.put("&auml;", '\u00E4');
        HTML_ENTITIES.put("&aring;", '\u00E5');
        HTML_ENTITIES.put("&aelig;", '\u00E6');
        HTML_ENTITIES.put("&ccedil;", '\u00E7');
        HTML_ENTITIES.put("&egrave;", '\u00E8');
        HTML_ENTITIES.put("&eacute;", '\u00E9');
        HTML_ENTITIES.put("&ecirc;", '\u00EA');
        HTML_ENTITIES.put("&euml;", '\u00EB');
        HTML_ENTITIES.put("&igrave;", '\u00EC');
        HTML_ENTITIES.put("&iacute;", '\u00ED');
        HTML_ENTITIES.put("&icirc;", '\u00EE');
        HTML_ENTITIES.put("&iuml;", '\u00EF');
        HTML_ENTITIES.put("&eth;", '\u00F0');
        HTML_ENTITIES.put("&ntilde;", '\u00F1');
        HTML_ENTITIES.put("&ograve;", '\u00F2');
        HTML_ENTITIES.put("&oacute;", '\u00F3');
        HTML_ENTITIES.put("&ocirc;", '\u00F4');
        HTML_ENTITIES.put("&otilde;", '\u00F5');
        HTML_ENTITIES.put("&ouml;", '\u00F6');
        HTML_ENTITIES.put("&divide;", '\u00F7');
        HTML_ENTITIES.put("&oslash;", '\u00F8');
        HTML_ENTITIES.put("&ugrave;", '\u00F9');
        HTML_ENTITIES.put("&uacute;", '\u00FA');
        HTML_ENTITIES.put("&ucirc;", '\u00FB');
        HTML_ENTITIES.put("&uuml;", '\u00FC');
        HTML_ENTITIES.put("&yacute;", '\u00FD');
        HTML_ENTITIES.put("&thorn;", '\u00FE');
        HTML_ENTITIES.put("&yuml;", '\u00FF');
        HTML_ENTITIES.put("&OElig;", '\u008C');
        HTML_ENTITIES.put("&oelig;", '\u009C');
        HTML_ENTITIES.put("&euro;", '\u20AC');
             
        UTF8_CHARS = new HashMap<Character, String>();
        for (String key : HTML_ENTITIES.keySet()) {
          UTF8_CHARS.put(HTML_ENTITIES.get(key), key);
        }
      }
     
      public static String encode(String s) {
        if ((UTF8_CHARS == null) && (HTML_ENTITIES == null)) {
          HTMLEntities.initEntities();
        }
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
          char c = s.charAt(i);
          String htmlEntity = UTF8_CHARS.get(c);
          if (htmlEntity != null) {
            result.append(htmlEntity);
          } else {
            result.append(c);
          }
        }
        return (result.toString());
      }
     
      public static String decode(String s) {
        if ((UTF8_CHARS == null) && (HTML_ENTITIES == null)) {
          HTMLEntities.initEntities();
        }
        StringBuffer result = new StringBuffer();
        int start = 0;
        Pattern p = Pattern.compile("&[a-zA-Z]+;");
        Matcher m = p.matcher(s);
        while (m.find(start)) {
          Character utf8Char = HTML_ENTITIES.get(s.substring(m.start(), m.end()));
          result.append(s.substring(start, m.start()));
          if (utf8Char != null) {
            result.append(utf8Char);
          } else {
            result.append(s.substring(m.start(), m.end()));
          }
          start = m.end();
        }
        
        String txt = (result.append(s.substring(start)).toString());
        
        txt=txt.replaceAll("<br />", "\n");
		txt=txt.replaceAll("<br/>", "\n");
		txt=txt.replaceAll("&#039;", "'");
		txt=txt.replaceAll("&#39;", "\'");
		txt=txt.replaceAll("&#37;","%");
		txt=txt.replaceAll("&#36;","$");
		txt=txt.replaceAll("&#167;","§");
		txt=txt.replace("&#339;","oe");
		txt=txt.replace("&#338;","OE");
        
        return txt;
      }
    }
}
