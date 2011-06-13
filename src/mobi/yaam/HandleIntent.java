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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class HandleIntent extends Activity {
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = this.getIntent();
        if(i.getDataString().contains("yaam://details?id=") || i.getDataString().contains("market://details?id="))
        {
        	String packagename="";
        	if(i.getDataString().contains("yaam://details?id="))
        		packagename=i.getDataString().replace("yaam://details?id=","");
        	else if(i.getDataString().contains("market://details?id="))
        		packagename=i.getDataString().replace("market://details?id=","");
        	
        	Intent j = new Intent(HandleIntent.this, ShowAppActivity.class);

        	Bundle objetbunble = new Bundle();
            objetbunble.putString("package",packagename);
            j.putExtras(objetbunble );

            startActivity(j);
        	
            this.finish();
        }
    }
}