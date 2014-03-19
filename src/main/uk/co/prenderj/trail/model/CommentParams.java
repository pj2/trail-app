package uk.co.prenderj.trail.model;

import uk.co.prenderj.trail.net.attachment.AttachmentFile;

import com.google.android.gms.maps.model.LatLng;

/**
 * A request to create a new comment.
 * @author Joshua Prendergast
 */
public class CommentParams {
    public final LatLng position;
    public final String title;
    public final String body;
    public final AttachmentFile attachmentFile;
    
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
        this.attachmentFile = null;
    }
    
    /**
     * Creates a comment request with an attachment.
     * @param position the position
     * @param title the title
     * @param body the comment content
     * @param attachmentFile the attachment
     */
    public CommentParams(LatLng position, String title, String body, AttachmentFile attachmentFile) {
        this.position = position;
        this.title = title;
        this.body = body;
        this.attachmentFile = attachmentFile;
    }
}
