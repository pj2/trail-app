package uk.co.prenderj.trail.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.prenderj.trail.LocationTracker;
import uk.co.prenderj.trail.net.WebClient;
import uk.co.prenderj.trail.tasks.CommentTasks;
import uk.co.prenderj.trail.ui.MapController;
import uk.co.prenderj.trail.ui.MapOptions;
import uk.co.prenderj.trail.ui.Route;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import uk.co.prenderj.trail.R;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;

/**
 * The main map activity.
 * @author Joshua Prendergast
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static MainActivity instance; // TODO Find a way to replace this
    private MapController map;
    private LocationTracker tracker;
    private WebClient http;
    private CommentTasks commentTasks;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        
        // Start all the services and managers
        tracker = new LocationTracker((LocationManager) getSystemService(LOCATION_SERVICE));
        
        if (tracker.isGpsEnabled()) {
            setContentView(R.layout.activity_main);
            
            tracker.connect();
            
            GoogleMap gmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            map = new MapController(gmap, createMapOptions());
            
            try {
                http = new WebClient(new URL(getResources().getString(R.string.server_host)));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            
            commentTasks = new CommentTasks(map, http, (ProgressBar) findViewById(R.id.progressBar));
            commentTasks.loadNearbyComments(tracker.getLastLatLng());
        } else {
            // Ask the user to activate GPS and exit
            Toast.makeText(this, R.string.activate_gps, Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            finish();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        checkPlayServices();
        
        tracker.connect();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Turn off some components to save battery
        tracker.disconnect();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        http.close();
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add_comment:
            startAddCommentActivity();
            return true;
        default:
            return super.onMenuItemSelected(featureId, item);
        }
    }
    
    /**
     * Starts the comment add form when the action is pressed.
     */
    protected void startAddCommentActivity() {
        startActivity(new Intent(this, AddCommentActivity.class));
    }
    
    protected void checkPlayServices() {
        // TODO Move
        // TODO Display prompt to download Services
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(TAG, "GPlayServices: status = " + status + ", success = " + (status == ConnectionResult.SUCCESS));
    }
    
    protected MapOptions createMapOptions() {
        try {
            return new MapOptions(R.color.out_of_bounds_fill, R.color.route_color, new Route(getResources().getXml(R.xml.test_route_1)));
        } catch (NotFoundException e) {
            Log.e(TAG, "Couldn't find default route", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load default route", e);
            throw new RuntimeException(e);
        }
    }
    
    public MapController getMap() {
        return map;
    }
    
    public LocationTracker getLocationTracker() {
        return tracker;
    }
    
    public WebClient getClient() {
        return http;
    }
    
    public CommentTasks getCommentManager() {
        return commentTasks;
    }
    
    public static MainActivity instance() {
        return instance;
    }
}
