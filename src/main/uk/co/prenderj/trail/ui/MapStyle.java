package uk.co.prenderj.trail.ui;

import uk.co.prenderj.trail.R;

import android.content.res.Resources;

/**
 * A simple loader / wrapper for map display options such as colors.
 * @author Joshua Prendergast
 */
public class MapStyle {
    public final int outOfBoundsFill;
    public final int routeColor;

    public MapStyle(Resources res) {
        this.outOfBoundsFill = res.getColor(R.color.out_of_bounds_fill);
        this.routeColor = res.getColor(R.color.route_color);
    }

    public MapStyle(int outOfBoundsColor, int routeColor) {
        this.outOfBoundsFill = outOfBoundsColor;
        this.routeColor = routeColor;
    }
}
