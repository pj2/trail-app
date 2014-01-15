package uk.co.prenderj.trail.event;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

public class LocationChangedEvent {
    public final Location location;
    
    public LocationChangedEvent(Location location) {
        this.location = location;
    }
    
    public LatLng asLatLng() {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
