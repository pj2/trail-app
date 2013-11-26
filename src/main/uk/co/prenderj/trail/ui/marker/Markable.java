package uk.co.prenderj.trail.ui.marker;

import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Indicates that this object can be marked on the map.
 * @author Joshua Prendergast
 */
public interface Markable {
    public MarkerOptions mark(MarkerOptions opt);
}
