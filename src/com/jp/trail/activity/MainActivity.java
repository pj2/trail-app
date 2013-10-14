package com.jp.trail.activity;

import com.google.android.gms.maps.MapFragment;
import com.jp.trail.R;
import com.jp.trail.view.Map;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	private Map map;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        map = new Map(((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
