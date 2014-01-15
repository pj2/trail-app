package uk.co.prenderj.trail.ui;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import uk.co.prenderj.trail.event.LocationChangedEvent;
import uk.co.prenderj.trail.ui.marker.Markable;

import com.google.android.gms.maps.model.Marker;
import com.google.common.eventbus.Subscribe;

/**
 * Maintains and controls the main MapView.
 * @author Joshua Prendergast
 */
public class MapController {
    private static final String TAG = "MapController";
    private static final LatLng LANCASTER_UNIVERSITY = new LatLng(54.0100d, -2.78613d);
    
    private final GoogleMap gmap;
    private final MapOptions options;
    
    /**
     * Creates a new controller using the default settings in resources.
     * @param gmap the GoogleMap to control
     * @param res the application resources
     */
    public MapController(GoogleMap gmap, MapOptions options) {
        if (gmap == null) {
            throw new IllegalStateException("Null GoogleMap"); // May happen if GServices are missing
        }
        
        this.gmap = gmap;
        this.options = options; // TODO Move from constructor into separate method
        setupMap();
    }
    
    @Subscribe
    public void onLocationChanged(LocationChangedEvent event) {
        gmap.moveCamera(CameraUpdateFactory.newLatLng(event.asLatLng())); // Follow position
    }
    
    /**
     * Sets the MapView's initial properties.
     */
    protected void setupMap() {
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        // Disable UI components
        UiSettings ui = gmap.getUiSettings();
        ui.setAllGesturesEnabled(false);
        ui.setMyLocationButtonEnabled(false);
        ui.setZoomControlsEnabled(false);
        
        // Add overlays
        gmap.addPolygon(createBoundary(getMapBounds()));
        options.route.attach(gmap);
        
        // Center on Lancaster
        moveCamera(CameraUpdateFactory.newLatLngZoom(LANCASTER_UNIVERSITY, getStartZoom()));
    }
    
    protected PolygonOptions createBoundary(LatLngBounds bounds) {
        PolygonOptions opt = new PolygonOptions();
        
        // Cover the entire Earth
        opt.add(new LatLng(0.0d, 90.0d), new LatLng(180.0d, 90.0d), new LatLng(180.0d, -90.0d), new LatLng(0.0d, -90.0d));
        
        // Add a hole
        List<LatLng> hole = new ArrayList<LatLng>();
        hole.add(bounds.northeast);
        hole.add(new LatLng(bounds.southwest.latitude, bounds.northeast.longitude));
        hole.add(bounds.southwest);
        hole.add(new LatLng(bounds.northeast.latitude, bounds.southwest.longitude));
        opt.addHole(hole);
        
        // Set display options
        opt.fillColor(options.colorOutOfBounds);
        opt.strokeWidth(0.0f);
        return opt;
    }
    
    /**
     * Places a marker on the map.
     * @param markable the markable object
     */
    public Marker addMarkable(Markable markable) {
        MarkerOptions opt = new MarkerOptions();
        markable.mark(opt); // Allow object to adjust marker
        Log.v(TAG, String.format("Adding marker: pos = %s, title = %s", opt.getPosition(), opt.getTitle()));
        return gmap.addMarker(opt);
    }
    
    public int getStartZoom() {
        return 15;
    }
    
    public LatLngBounds getMapBounds() {
        return new LatLngBounds(new LatLng(54.00497d, -2.79156d), new LatLng(54.01412d, -2.78070d));
    }
    
    public final void animateCamera(CameraUpdate update, CancelableCallback callback) {
        gmap.animateCamera(update, callback);
    }
    
    public final void animateCamera(CameraUpdate update, int durationMs, CancelableCallback callback) {
        gmap.animateCamera(update, durationMs, callback);
    }
    
    public final void animateCamera(CameraUpdate update) {
        gmap.animateCamera(update);
    }
    
    public final CameraPosition getCameraPosition() {
        return gmap.getCameraPosition();
    }
    
    public final void moveCamera(CameraUpdate update) {
        gmap.moveCamera(update);
    }
}
