package uk.co.prenderj.trail.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.Lists;

import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.activity.MainActivity;
import uk.co.prenderj.trail.adapter.CSVToComment;
import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trail.net.CSVObjectReader;
import uk.co.prenderj.trail.net.CommentResponse;
import uk.co.prenderj.trail.net.WebClient;
import uk.co.prenderj.trail.ui.MapController;
import uk.co.prenderj.trail.ui.marker.CommentMarker;

public class LoadNearbyCommentsTask extends BaseTask<LatLng, Void, List<Comment>> {
    private WebClient client;
    private MapController map;
    
    public LoadNearbyCommentsTask(CommentTasks manager, WebClient client, MapController map) {
        super(manager);
        this.client = client;
        this.map = map;
    }
    
    @Override
    public List<Comment> call(LatLng... pos) throws Exception {
        publishProgress(null);
        
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
    public void postExecute(List<Comment> comments) throws Exception {
        // Add to map
        for (Comment c : comments) {
            map.addMarkable(new CommentMarker(c));
        }
    }
    
    @Override
    public void onException(Exception thrown) {
        Toast.makeText(MainActivity.instance(), R.string.http_fail_generic, Toast.LENGTH_LONG).show();
    }
}
