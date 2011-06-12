package com.pixellostudio.newyaam;

import greendroid.app.GDApplication;

public class YAAMApplication extends GDApplication {

    @Override
    public Class<?> getHomeActivityClass() {
        return HomeActivity.class;
    }
    
    /*@Override
    public Intent getMainApplicationIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url)));
    }*/

}
