package uk.co.prenderj.trail.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import uk.co.prenderj.trail.CommentManager;
import uk.co.prenderj.trail.LocationTracker;
import uk.co.prenderj.trail.net.WebClient;
import uk.co.prenderj.trail.ui.MapController;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import uk.co.prenderj.trail.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.content.Intent;

/**
 * The main map activity.
 * @author Joshua Prendergast
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static MainActivity instance;
    private MapController map;
    private LocationTracker tracker;
    private WebClient client;
    private CommentManager commentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);

        checkPlayServices();

        // Enable fullscreen mode
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        
        // Start all the services and managers
        tracker = new LocationTracker(this);

        GoogleMap gmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map = new MapController(gmap, getResources());

        try {
            client = new WebClient(new URL("http://prenderj.co.uk:8080"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        
        commentManager = new CommentManager(map, tracker, client);
        commentManager.loadComments(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tracker.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tracker.disconnect(); // Disconnect to save battery
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
        // TODO Display prompt to download Services
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(TAG, "GPlayServices: status = " + status + ", success = " + (status == ConnectionResult.SUCCESS));
    }
    
    public MapController getMap() {
        return map;
    }

    public LocationTracker getTracker() {
        return tracker;
    }

    public WebClient getClient() {
        return client;
    }

    public CommentManager getCommentManager() {
        return commentManager;
    }

    public static MainActivity instance() {
        return instance;
    }
}
