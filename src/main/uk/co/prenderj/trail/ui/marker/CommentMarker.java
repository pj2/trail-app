package uk.co.prenderj.trail.ui.marker;

import uk.co.prenderj.trail.model.Comment;

import com.google.android.gms.maps.model.MarkerOptions;

/**
 * The view component of a comment as a marker.
 * @author Joshua Prendergast
 */
public class CommentMarker implements Markable {
    private Comment comment;
    
    public CommentMarker(Comment comment) {
        this.comment = comment;
    }
    
    @Override
    public MarkerOptions mark(MarkerOptions opt) {
        return opt.position(comment.location).title(comment.title).snippet(comment.body);
    }
}
