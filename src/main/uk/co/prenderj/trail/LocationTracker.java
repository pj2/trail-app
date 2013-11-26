package uk.co.prenderj.trail;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

/**
 * Gathers location data at least every 10 seconds.
 * @author Joshua Prendergast
 */
public class LocationTracker implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
    private LocationClient client;
    private Location lastLocation;
    private long lastUpdate;

    public LocationTracker(LocationClient client) {
        initialize(client);
    }

    public LocationTracker(Context ctx) {
        initialize(new LocationClient(ctx, this, this));
    }

    private void initialize(LocationClient client) {
        this.client = client;
        connect();
    }

    protected LocationRequest createLocationRequest() {
        return new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).setFastestInterval(500).setInterval(10000);
    }

    public void connect() {
        client.connect();
    }

    public void disconnect() {
        client.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        setCurrentLocation(client.getLastLocation());
        client.requestLocationUpdates(createLocationRequest(), LocationTracker.this, Looper.getMainLooper()); // Start listening to updates
    }

    @Override
    public void onDisconnected() {
        client.removeLocationUpdates(LocationTracker.this); // Halt updates
    }

    @Override
    public void onLocationChanged(Location loc) {
        setCurrentLocation(loc);
    }

    @Override
    public void onConnectionFailed(ConnectionResult res) {
        // TODO Handle
    }

    public void setCurrentLocation(Location loc) {
        lastLocation = loc;
        lastUpdate = SystemClock.uptimeMillis();
    }

    public LocationClient getLocationClient() {
        return client;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public LatLng getLastLatLng() {
        Location loc = getLastLocation();
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
}
