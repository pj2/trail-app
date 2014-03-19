package uk.co.prenderj.trail.tasks;

import java.io.File;
import java.util.concurrent.Future;

import com.google.android.gms.maps.model.Marker;

import android.content.Context;
import android.widget.Toast;
import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.Trail;
import uk.co.prenderj.trail.activity.MainActivity;
import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trail.model.CommentParams;
import uk.co.prenderj.trail.net.CommentResponse;
import uk.co.prenderj.trail.net.WebClient;
import uk.co.prenderj.trail.ui.MapController;
import uk.co.prenderj.trail.ui.marker.CommentMarker;

public class AddCommentTask extends BaseTask<CommentParams, Void, Comment> {
    private File cacheDirectory;
    
    public AddCommentTask(TaskManager manager, Context caller, File cacheDirectory) {
        super(manager, caller);
        this.cacheDirectory = cacheDirectory;
    }

    @Override
    public Comment call(CommentParams... params) throws Exception {
        publishProgress();
        
        // Send comment to server
        Future<CommentResponse> future = Trail.getWebClient().uploadComment(params[0], cacheDirectory);
        CommentResponse resp = future.get();
        
        Comment comment = resp.getSingleComment();
        Trail.getDataStore().insertComments(comment);
        
        return comment;
    }
    
    @Override
    public void postExecute(Comment comment) throws Exception {
        // Add to map
        getManager().getMap().addMarkable(new CommentMarker(comment));
        Toast.makeText(getCaller(), R.string.comment_add_success, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onException(Exception thrown) {
        super.onException(thrown);
        Toast.makeText(getCaller(), R.string.action_comment_fail, Toast.LENGTH_SHORT).show();
    }
}
