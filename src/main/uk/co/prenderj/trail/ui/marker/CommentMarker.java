package uk.co.prenderj.trail.ui.marker;

import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.model.Comment;
import android.graphics.Point;
import android.graphics.PointF;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
        int image = R.drawable.ic_chat_bubble;
        PointF anchor = findAnchor(image);
        
        return opt.position(comment.location)
                .title(comment.title)
                .snippet(comment.body)
                .anchor(anchor.x, anchor.y)
                .icon(BitmapDescriptorFactory.fromResource(image));
    }
    
    public PointF findAnchor(int resource) {
        switch (resource) {
        case R.drawable.ic_chat_bubble:
            return new PointF(0.917f, 0.861f);
        case R.drawable.ic_camera:
            // TODO
        default:
            return new PointF(0.5f, 0.5f);
        }
    }
}
