package uk.co.prenderj.trail;

import java.io.IOException;

import android.content.Context;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import uk.co.prenderj.trail.adapter.CSVToComment;
import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trail.net.CSVObjectReader;
import uk.co.prenderj.trail.net.CommentResponse;
import uk.co.prenderj.trail.net.WebClient;
import uk.co.prenderj.trail.ui.MapController;
import uk.co.prenderj.trail.ui.marker.CommentMarker;

/**
 * Provides a top-layer to call for comment operations. All relevant subsystems
 * are updated by calls to this class.
 * @author Joshua Prendergast
 */
public class CommentManager {
    private MapController map;
    private LocationTracker tracker;
    private WebClient client;
    
    public CommentManager(MapController map, LocationTracker tracker, WebClient client) {
         this.map = map;
         this.tracker = tracker;
         this.client = client;
    }
    
    public void addComment(final Context ctx, String body) {
        Futures.addCallback(client.registerComment(tracker.getLastLatLng(), body), new FutureCallback<CommentResponse>() {
            public void onSuccess(CommentResponse resp) {
                if (resp.isSuccess()) {
                    try {
                        Comment comment = resp.getSingleComment();
                        map.addMarker(new CommentMarker(comment)); // TODO Move to UI thread
                    } catch (IOException e) {
                        showMessage(ctx, R.string.fail_generic);
                    }
                } else {
                    showMessage(ctx, R.string.http_fail_404);
                }
            }
            
            public void onFailure(Throwable thrown) {
                showMessage(ctx, R.string.http_fail_404);
            }
        });
    }
    
    public void loadComments(final Context ctx) {
        Futures.addCallback(client.loadNearbyComments(tracker.getLastLatLng()), new FutureCallback<CommentResponse>() {
            public void onSuccess(CommentResponse resp) {
                try {
                    CSVObjectReader<Comment> reader = null;
                    if (resp.isSuccess()) {
                        try {
                            reader = resp.getCommentReader();
                            Comment comment;
                            CSVToComment adapter = new CSVToComment();
                            while ((comment = reader.readObject(adapter)) != null) {
                                map.addMarker(new CommentMarker(comment));
                            }
                        } finally {
                            if (reader != null)
                                reader.close();
                        }
                    }
                } catch (IOException e) {
                    showMessage(ctx, R.string.fail_generic);
                    // TODO Log
                }
            }
            
            public void onFailure(Throwable thrown) {
                showMessage(ctx, R.string.http_fail_404);
            }
        });
    }
    
    protected void showMessage(Context ctx, int messageId) {
        Toast.makeText(ctx, messageId, Toast.LENGTH_LONG).show();
    }
}
