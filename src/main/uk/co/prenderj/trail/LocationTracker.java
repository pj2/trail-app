package uk.co.prenderj.trail;

import uk.co.prenderj.trail.event.LocationChangedEvent;
import uk.co.prenderj.trail.ui.MapController;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.eventbus.EventBus;

/**
 * Gathers GPS location data every 5 seconds.
 * @author Joshua Prendergast
 */
public class LocationTracker implements LocationListener {
    private static final String TAG = "LocationTracker";
    private EventBus eventBus = new EventBus(TAG);
    private LocationManager locationManager;
    private Location lastLocation;
    
    public LocationTracker(LocationManager locationManager) {
        this.locationManager = locationManager;
    }
    
    @Override
    public void onLocationChanged(Location loc) {
        lastLocation = loc;
        eventBus.post(new LocationChangedEvent(loc));
    }
    
    @Override
    public void onProviderDisabled(String provider) {
        if (provider == LocationManager.GPS_PROVIDER) {
            Log.e(TAG, "GPS disabled!");
        }
    }
    
    @Override
    public void onProviderEnabled(String provider) {
    }
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    
    public void connect() {
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
    }
    
    public void disconnect() {
        locationManager.removeUpdates(this);
    }
    
    public boolean isGpsEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    
    public void registerListener(Object o) {
        eventBus.register(o);
    }
    
    public LocationManager getLocationManager() {
        return locationManager;
    }
    
    public Location getLastLocation() {
        return lastLocation;
    }
    
    public LatLng getLastLatLng() {
        Location loc = getLastLocation();
        return loc == null ? MapController.LANCASTER_UNIVERSITY : new LatLng(loc.getLatitude(), loc.getLongitude()); // HACK
    }
}
