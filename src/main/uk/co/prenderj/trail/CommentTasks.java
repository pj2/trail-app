package uk.co.prenderj.trail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.Lists;

import uk.co.prenderj.trail.activity.MainActivity;
import uk.co.prenderj.trail.adapter.CSVToComment;
import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trail.net.CSVObjectReader;
import uk.co.prenderj.trail.net.CommentResponse;
import uk.co.prenderj.trail.net.WebClient;
import uk.co.prenderj.trail.ui.MapController;
import uk.co.prenderj.trail.ui.marker.CommentMarker;
import uk.co.prenderj.trail.util.CheckedAsyncTask;

/**
 * Provides a top-layer to call for comment operations. All relevant subsystems are updated by calls to this class.
 * @author Joshua Prendergast
 */
public class CommentTasks {
    private static final String TAG = "CommentManager";
    private MapController map;
    private WebClient client;
    
    public CommentTasks(MapController map, WebClient client) {
        this.map = map;
        this.client = client;
    }
    
    public AsyncTask<CommentParams, Void, Comment> addComment(CommentParams params) {
        return new CheckedAsyncTask<CommentParams, Void, Comment>() {
            @Override
            public Comment call(CommentParams... params) throws Exception {
                Future<CommentResponse> future = client.registerComment(params[0]);
                CommentResponse resp = future.get();
                
                return resp.getSingleComment();
            }
            
            @Override
            public void finish(Comment comment) throws Exception {
                map.addMarkable(new CommentMarker(comment));
                Toast.makeText(MainActivity.instance(), R.string.comment_add_success, Toast.LENGTH_LONG).show();
            }
            
            @Override
            public void onException(Exception thrown) {
                Log.e(TAG, "addComment", thrown);
                Toast.makeText(MainActivity.instance(), R.string.action_comment_fail, Toast.LENGTH_LONG).show();
                
                cancel(true);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }
    
    public AsyncTask<LatLng, Void, List<Comment>> loadNearbyComments(LatLng pos) {
        return new CheckedAsyncTask<LatLng, Void, List<Comment>>() {
            @Override
            public List<Comment> call(LatLng... pos) throws Exception {
                Future<CommentResponse> future = client.loadNearbyComments(pos[0]);
                CommentResponse resp = future.get();
                
                CSVObjectReader<Comment> reader = null;
                try {
                    reader = resp.getCommentReader();
                    Comment comment;
                    CSVToComment adapter = new CSVToComment();
                    ArrayList<Comment> comments = Lists.newArrayList();
                    while ((comment = reader.readObject(adapter)) != null) {
                        comments.add(comment);
                    }
                    return comments;
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
            
            @Override
            public void finish(List<Comment> comments) throws Exception {
                // TODO Check if not already added
                for (Comment c : comments) {
                    map.addMarkable(new CommentMarker(c));
                }
            }
            
            @Override
            public void onException(Exception thrown) {
                Log.e(TAG, "loadNearbyComments", thrown);
                Toast.makeText(MainActivity.instance(), R.string.http_fail_generic, Toast.LENGTH_LONG).show();
                
                cancel(true);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pos);
    }
}
