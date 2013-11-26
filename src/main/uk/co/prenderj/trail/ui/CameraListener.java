package uk.co.prenderj.trail.ui;

import uk.co.prenderj.trail.util.MathUtil;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Controls the camera.
 * @author Joshua Prendergast
 */
public class CameraListener implements OnCameraChangeListener {
    private MapController map;
    private boolean initialPostionSet;
    private Handler boundsHandler;

    private Runnable boundsChecker = new Runnable() {
        @Override
        public void run() {
            checkBounds();
        }
    };

    public CameraListener(MapController map) {
        this.map = map;
    }

    @Override
    public void onCameraChange(CameraPosition pos) {
        if (!initialPostionSet) {
            // Set the initial position after the GoogleMap has done layout
            map.getGmap().moveCamera(CameraUpdateFactory.newLatLngBounds(map.getMapBounds(), map.getStartZoom()));
            initialPostionSet = true;

            // Run the bounds checker as often as possible
            boundsHandler = new Handler(Looper.getMainLooper());
            boundsHandler.post(boundsChecker);
        }
    }

    public void checkBounds() {
        if (initialPostionSet) {
            // Keep the camera within acceptable bounds
            GoogleMap gmap = map.getGmap();
            LatLng pos = gmap.getCameraPosition().target;
            LatLng adjusted = MathUtil.clampPosition(pos, map.getMapBounds(), 0.00075d);
            if (!adjusted.equals(pos)) {
                gmap.stopAnimation();
                gmap.moveCamera(CameraUpdateFactory.newLatLng(adjusted));
            }

            boundsHandler.post(boundsChecker); // HACK
        }
    }
}
