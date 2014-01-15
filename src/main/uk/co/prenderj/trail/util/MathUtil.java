package uk.co.prenderj.trail.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MathUtil {
    /**
     * Ensures that a LatLng is contained within the given bounds.
     * @param latLng the LatLng
     * @param bounds the clamp bounds
     * @param margin the minimum distance to be kept from a horizontal or vertical edge
     * @return a new adjusted LatLng if position was altered. Otherwise, the supplied LatLng instance
     */
    public static LatLng clampPosition(LatLng latLng, LatLngBounds bounds, double margin) {
        if (!bounds.contains(latLng)) {
            // Clamp within bounds, see http://developer.android.com/reference/com/google/android/gms/maps/model/LatLngBounds.html constructor notes
            double latitude = clamp(latLng.latitude, bounds.southwest.latitude + margin, bounds.northeast.latitude - margin);
            double longitude = latLng.longitude;
            if (bounds.southwest.longitude <= bounds.northeast.longitude) {
                longitude = clamp(latLng.longitude, bounds.southwest.longitude + margin, bounds.northeast.longitude - margin);
            } else {
                if (latLng.longitude > bounds.southwest.longitude && latLng.longitude <= 180.0f) {
                    longitude = clamp(latLng.longitude, bounds.southwest.longitude + margin, 180.0f - margin);
                } else {
                    longitude = clamp(latLng.longitude, -180.0f + margin, bounds.northeast.longitude - margin);
                }
            }
            return new LatLng(latitude, longitude);
        } else {
            return latLng;
        }
    }

    /**
     * Ensures that a LatLng is contained within the given bounds.
     * @param latLng the LatLng
     * @param bounds the clamp bounds
     * @return a new adjusted LatLng if position was altered. Otherwise, the supplied LatLng instance
     */
    public static LatLng clampPosition(LatLng latLng, LatLngBounds bounds) {
        return clampPosition(latLng, bounds, 0.0d);
    }

    public static double clamp(double d, double min, double max) {
        return Math.max(min, Math.min(d, max));
    }
    
    /**
     * Calculates the distance between two points of longitude and latitude.
     * @param x the first LatLng
     * @param y the second LatLng
     * @return the distance in kilometers
     */
    public static double haversineDistance(LatLng x, LatLng y) {
    	// Pretty much copied from http://stackoverflow.com/questions/27928/how-do-i-calculate-distance-between-two-latitude-longitude-points
    	double r = 6371; // Radius of Earth in km
    	double dLat = Math.toRadians(y.latitude - x.latitude);
    	double dLng = Math.toRadians(y.longitude - x.latitude);
    	
    	double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + 
    	        Math.cos(Math.toRadians(x.latitude)) * Math.cos(Math.toRadians(y.latitude)) *
    	        Math.sin(dLng / 2) * Math.sin(dLng / 2);
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    	return r * c;
    }
}
