package uk.co.prenderj.trail;

import com.google.android.gms.maps.model.LatLng;

public class CommentParams {
    public final LatLng position;
    public final String title;
    public final String body;
    
    public CommentParams(LatLng position, String title, String body) {
        this.position = position;
        this.title = title;
        this.body = body;
    }
}
