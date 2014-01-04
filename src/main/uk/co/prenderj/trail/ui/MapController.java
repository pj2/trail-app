package uk.co.prenderj.trail.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
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
import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.ui.marker.Markable;

import com.google.android.gms.maps.model.Marker;

/**
 * Maintains and controls the main MapView.
 * @author Joshua Prendergast
 */
public class MapController {
    private static final String TAG = "MapController";

    private final GoogleMap gmap;
    private final MapStyle style;
    private RouteOverlay overlay;
    private CameraListener camera = new CameraListener(this);

    /**
     * Creates a new controller using the default settings in resources.
     * @param gmap
     * the GoogleMap to control
     * @param res
     * the application resources
     */
    public MapController(GoogleMap gmap, Resources res) {
        if (gmap == null) {
            throw new IllegalStateException("Null GoogleMap"); // May happen if GServices are missing
        }

        this.gmap = gmap;
        this.style = new MapStyle(res);
        try {
            this.overlay = new RouteOverlay(res.getXml(R.xml.test_route_1));
        } catch (IOException e) {
            Log.e(TAG, "Failed to load default route", e);
        }
        setupMap();
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
        ui.setCompassEnabled(true);

        // Add overlays
        gmap.addPolygon(createBoundary(getMapBounds()));
        overlay.attach(gmap);
        
        // Center on Lancaster
        moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(54.0103d, 2.78613d), 12));

        gmap.setOnMyLocationChangeListener(camera);
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
        opt.fillColor(style.outOfBoundsFill);
        opt.strokeWidth(0.0f);
        return opt;
    }

    /**
     * Places a marker on the map.
     * @param markable the markable object
     */
    public Marker addMarker(Markable markable) {
        MarkerOptions opt = new MarkerOptions();
        return gmap.addMarker(markable.mark(opt)); // Allow object to adjust marker
    }

    public int getStartZoom() {
        return 14;
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
