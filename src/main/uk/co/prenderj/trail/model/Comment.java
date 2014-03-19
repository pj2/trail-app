package uk.co.prenderj.trail.model;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

/**
 * A user's comment.
 * @author Joshua Prendergast
 */
public class Comment implements Serializable {
    public final long id;
    public final LatLng location;
    public final String title;
    public final String body;
    public final long attachmentId;
    
    public Comment(long id, LatLng location, String title, String body, long attachmentId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.location = location;
        this.attachmentId = attachmentId;
    }
    
    public Comment(long id, LatLng location, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.location = location;
        this.attachmentId = -1;
    }
}
