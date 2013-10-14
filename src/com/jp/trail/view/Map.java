package com.jp.trail.view;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Map {
	private static final LatLngBounds LANCASTER_UNIVERSITY = new LatLngBounds(new LatLng(54.00497d, -2.79156d), new LatLng(54.01412d, -2.78070d));
	private GoogleMap gmap;
	
	// Listeners
	private OnCameraChangeListener onCameraChangeListener = new OnCameraChangeListener() {
		private boolean positionSet;
		
		@Override
		public void onCameraChange(CameraPosition pos) {
			if (!positionSet) {
				// Set the initial position after the GoogleMap has done layout
				gmap.moveCamera(CameraUpdateFactory.newLatLngBounds(LANCASTER_UNIVERSITY, 15));
				positionSet = true;
			}
		}
	};
	
	public Map(GoogleMap gmap) {
		this.gmap = gmap;
		setupMap();
	}
	
	/**
     * Sets the MapView's initial properties.
     */
	protected void setupMap() {
        if (gmap != null) {
        	gmap.setOnCameraChangeListener(onCameraChangeListener);
        	gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            
            UiSettings ui = gmap.getUiSettings();
            ui.setAllGesturesEnabled(false); // Disable user interaction
            ui.setMyLocationButtonEnabled(false);
            ui.setZoomControlsEnabled(false);
            
            ui.setCompassEnabled(true);
        }
	}
	
	/**
	 * Ensures that a LatLng is contained within the given bounds.
	 * @param latLng
	 * 		the LatLng
	 * @param bounds
	 * 		the clamp bounds
	 * @return a new adjusted LatLng if position was altered. Otherwise, the supplied LatLng instance
	 */
	protected LatLng clampPosition(LatLng latLng, LatLngBounds bounds) {
		if (!bounds.contains(latLng)) {
			// Clamp within bounds, see http://developer.android.com/reference/com/google/android/gms/maps/model/LatLngBounds.html constructor notes
			double latitude = clamp(latLng.latitude, bounds.northeast.latitude, bounds.southwest.latitude);
			double longitude;
			if (bounds.southwest.longitude <= bounds.northeast.longitude) {
				longitude = clamp(latLng.longitude, bounds.northeast.longitude, bounds.southwest.longitude);
			} else {
				longitude = clamp(latLng.longitude, bounds.southwest.longitude, bounds.northeast.longitude);
			}
			return new LatLng(latitude, longitude);
		} else {
			return latLng;
		}
	}
	
	private double clamp(double d, double min, double max) {
		return Math.max(min, Math.min(d, max));
	}
}
