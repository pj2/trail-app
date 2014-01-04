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
}
