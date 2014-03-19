package uk.co.prenderj.trail.tasks;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.common.collect.Lists;

import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.Trail;
import uk.co.prenderj.trail.adapter.CSVToComment;
import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trail.net.CSVObjectReader;
import uk.co.prenderj.trail.net.CommentResponse;
import uk.co.prenderj.trail.ui.marker.CommentMarker;

public class LoadNearbyCommentsTask extends BaseTask<LatLng, Void, List<Comment>> {
    public LoadNearbyCommentsTask(TaskManager manager, Context caller) {
        super(manager, caller);
    }
    
    @Override
    public List<Comment> call(LatLng... pos) throws Exception {
        publishProgress();
        
        Future<CommentResponse> future = Trail.getWebClient().downloadNearbyComments(pos[0]);
        CommentResponse resp = future.get();
        
        CSVObjectReader<Comment> reader = null;
        try {
            reader = resp.getCommentReader();
            Comment comment;
            CSVToComment adapter = new CSVToComment();
            List<Comment> comments = Lists.newArrayList();
            while ((comment = reader.readObject(adapter)) != null) {
                comments.add(comment);
            }
            return Trail.getDataStore().insertComments(comments.toArray(new Comment[0])).get(5, TimeUnit.SECONDS);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    @Override
    public void postExecute(List<Comment> newComments) throws Exception {
        // Add to map
        for (Comment c : newComments) {
            getManager().getMap().addMarkable(new CommentMarker(c));
        }
    }
    
    @Override
    public void onException(Exception thrown) {
        super.onException(thrown);
        Toast.makeText(getCaller(), R.string.http_fail_generic, Toast.LENGTH_SHORT).show();
    }
}
