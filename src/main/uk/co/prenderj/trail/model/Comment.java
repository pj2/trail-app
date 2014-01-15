package uk.co.prenderj.trail.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * A user's comment.
 * @author Joshua Prendergast
 */
public class Comment {
    public final long id;
    public final LatLng location;
    public final String title;
    public final String body;
    
    public Comment(long id, LatLng location, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.location = location;
    }
}
