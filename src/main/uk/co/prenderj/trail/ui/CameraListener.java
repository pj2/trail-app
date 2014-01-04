package uk.co.prenderj.trail.ui;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;

/**
 * Controls the camera.
 * @author Joshua Prendergast
 */
public class CameraListener implements OnMyLocationChangeListener {
    private MapController map;

    public CameraListener(MapController map) {
        this.map = map;
    }
    
    @Override
    public void onMyLocationChange(Location loc) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 10));
    }
}
