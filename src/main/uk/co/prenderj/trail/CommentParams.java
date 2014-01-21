package uk.co.prenderj.trail;

import uk.co.prenderj.trail.net.attachment.Attachment;

import com.google.android.gms.maps.model.LatLng;

/**
 * A request to create a new comment.
 * @author Joshua Prendergast
 */
public class CommentParams {
    public final LatLng position;
    public final String title;
    public final String body;
    public final Attachment attachment;
    
    /**
     * Creates a comment request without an attachment.
     * @param position the position
     * @param title the title
     * @param body the comment content
     */
    public CommentParams(LatLng position, String title, String body) {
        this.position = position;
        this.title = title;
        this.body = body;
        this.attachment = null;
    }
    
    /**
     * Creates a comment request with an attachment.
     * @param position the position
     * @param title the title
     * @param body the comment content
     * @param attachment the attachment
     */
    public CommentParams(LatLng position, String title, String body, Attachment attachment) {
        this.position = position;
        this.title = title;
        this.body = body;
        this.attachment = attachment;
    }
}
