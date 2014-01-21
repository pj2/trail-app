package uk.co.prenderj.trail.tasks;

import java.io.File;
import java.util.concurrent.Future;

import android.widget.Toast;
import uk.co.prenderj.trail.CommentParams;
import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.activity.MainActivity;
import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trail.net.CommentResponse;
import uk.co.prenderj.trail.net.WebClient;
import uk.co.prenderj.trail.ui.MapController;
import uk.co.prenderj.trail.ui.marker.CommentMarker;

public class AddCommentTask extends BaseTask<CommentParams, Void, Comment> {
    private WebClient client;
    private MapController map;
    private File cacheDirectory;
    
    public AddCommentTask(TaskManager manager, WebClient client, MapController map, File cacheDirectory) {
        super(manager);
        this.client = client;
        this.map = map;
        this.cacheDirectory = cacheDirectory;
    }

    @Override
    public Comment call(CommentParams... params) throws Exception {
        publishProgress();
        
        // Send comment to server
        Future<CommentResponse> future = client.uploadComment(params[0], cacheDirectory);
        CommentResponse resp = future.get();
        
        return resp.getSingleComment();
    }
    
    @Override
    public void postExecute(Comment comment) throws Exception {
        // Add to map
        map.addMarkable(new CommentMarker(comment));
        Toast.makeText(MainActivity.instance(), R.string.comment_add_success, Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onException(Exception thrown) {
        super.onException(thrown);
        Toast.makeText(MainActivity.instance(), R.string.action_comment_fail, Toast.LENGTH_LONG).show();
    }
}
