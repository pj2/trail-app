package uk.co.prenderj.trail.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import uk.co.prenderj.trail.LocationTracker;
import uk.co.prenderj.trail.db.DataSource;
import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trail.net.CommentResponse;
import uk.co.prenderj.trail.net.WebClient;
import uk.co.prenderj.trail.ui.MapController;
import uk.co.prenderj.trail.ui.marker.CommentMarker;
import uk.co.prenderj.trail.util.Callback;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import uk.co.prenderj.trail.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * The main map activity.
 * @author Joshua Prendergast
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private MapController map;
    private LocationTracker tracker;
    private WebClient client;
    private DataSource dataSource;

    protected void checkPlayServices() {
        // TODO Display prompt to download Services
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(TAG, "GPlayServices: status = " + status + ", success = " + (status == ConnectionResult.SUCCESS));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPlayServices();

        // Enable fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        
        // Start all the services and managers
        dataSource = new DataSource();
        tracker = new LocationTracker(this);

        GoogleMap gmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map = new MapController(gmap, getResources());

        try {
            client = new WebClient(new URL("http://prenderj.co.uk:8080"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        // TODO Shutdown database
    }

    /**
     * Attempts to share a comment and place it on the map.
     */
    public void onComment() {
        // TODO Move this to separate manager class
        // Store the comment with the server before adding it to the local map
        String body = new Date().toString(); // TODO This is a temporary test value
        new Callback<CommentResponse>(client.registerComment(tracker.getLastLatLng(), body), client.getDefaultTimeoutMillis(), TimeUnit.MILLISECONDS) {
            @Override
            public void onGet(CommentResponse resp) throws IllegalStateException, IOException {
                if (resp.isSuccess()) {
                    Comment comment = resp.getSingleComment();
                    dataSource.insertComments(comment);
                    map.addMarker(new CommentMarker(comment)); // TODO Move to UI thread
                }
                Toast.makeText(getApplication(), resp.getStatusMessageResource(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception e) {
                // Something really bad happened before even sending
                Toast.makeText(getApplication(), R.string.action_comment_fail, Toast.LENGTH_LONG).show();
            }
        }.start();
    }
}
