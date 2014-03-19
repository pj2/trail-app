package uk.co.prenderj.trail.activity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.prenderj.trail.LocationTracker;
import uk.co.prenderj.trail.Trail;
import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trail.model.CommentParams;
import uk.co.prenderj.trail.net.WebClient;
import uk.co.prenderj.trail.net.attachment.AttachmentFile;
import uk.co.prenderj.trail.storage.DataStore;
import uk.co.prenderj.trail.tasks.TaskManager;
import uk.co.prenderj.trail.ui.MapController;
import uk.co.prenderj.trail.ui.MapOptions;
import uk.co.prenderj.trail.ui.OnCommentWindowClickListener;
import uk.co.prenderj.trail.util.Util;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;

import uk.co.prenderj.trail.R;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Intent;

/**
 * The main map activity.
 * @author Joshua Prendergast
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_COMMENT = 1;
    
    private MapController map;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // Setup and start components
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                setContentView(R.layout.activity_main);
                
                LocationTracker tracker = Trail.getLocationTracker();
                tracker.setLocationManager(locationManager);
                tracker.connect();
                
                Trail.getWebClient().setHostname(new URL(getResources().getString(R.string.server_host)));
                
                GoogleMap gmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                map = new MapController(gmap, new MapOptions(R.color.out_of_bounds_fill, R.color.route_color));
                
                map.setOnCommentWindowClickListener(new OnCommentWindowClickListener() {
                    @Override
                    public void onCommentWindowClick(Marker marker, long commentId) {
                        try {
                            Comment comment = Trail.getDataStore().getCommentsById(commentId).get(5, TimeUnit.SECONDS).get(0);
                            marker.hideInfoWindow();
                            showCommentDialog(comment);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                
                TaskManager taskManager = Trail.getTaskManager();
                taskManager.setProgressBar((ProgressBar) findViewById(R.id.progress));
                taskManager.setMap(map);
                
                taskManager.loadStoredComments(this);
                taskManager.loadNearbyComments(this, tracker.getLastLatLng());
            } else {
                // Ask the user to activate GPS and exit
                Toast.makeText(this, R.string.activate_gps, Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                finish();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        Util.checkPlayServices(this);
        Trail.getLocationTracker().connect();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Turn off some components to save battery
        Trail.getLocationTracker().disconnect();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Trail.getWebClient().close();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        
        if (resultCode == REQUEST_COMMENT) {
            String title = data.getStringExtra("title");
            String body = data.getStringExtra("body");
            String attachmentPath = data.getStringExtra("attachment");
            
            AttachmentFile attachmentFile = null;
            if (attachmentPath != null) {
                int attachmentType = data.getIntExtra("attachmentType", -1);
                attachmentFile = AttachmentFile.newInstance(new File(attachmentPath), attachmentType);
            }
            
            Trail.getTaskManager().addComment(this, new CommentParams(Trail.getLocationTracker().getLastLatLng(),
                    title,
                    body,
                    attachmentFile), getCacheDir());
        }
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add_comment:
            startAddCommentActivity();
            break;
        case R.id.center_on_home:
            centerOnHome();
            break;
        case R.id.refresh:
            Trail.getTaskManager().loadNearbyComments(this, Trail.getLocationTracker().getLastLatLng());
            break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Starts the comment add form when the action is pressed.
     */
    public void startAddCommentActivity() {
        startActivityForResult(new Intent(this, AddCommentActivity.class), REQUEST_COMMENT);
    }
    
    public void centerOnHome() {
        map.centerOnHome();
        Toast.makeText(this, R.string.action_home, Toast.LENGTH_SHORT).show();
    }
    
    public void showCommentDialog(Comment comment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = CommentFragment.newInstance(comment);
        newFragment.show(ft, "dialog");
    }
}
