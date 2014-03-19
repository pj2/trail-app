package uk.co.prenderj.trail.tasks;

import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.Trail;
import uk.co.prenderj.trail.storage.DataStore;
import uk.co.prenderj.trail.ui.MapController;
import uk.co.prenderj.trail.ui.marker.CommentMarker;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.widget.Toast;

import com.google.common.base.Function;

public class LoadStoredCommentsTask extends BaseTask<Void, Void, Void> {
    public LoadStoredCommentsTask(TaskManager manager, Context caller) {
        super(manager, caller);
    }
    
    @Override
    public Void call(Void... params) throws Exception {
        publishProgress();
        
        // Add everything to the map
        final MapController map = getManager().getMap();
        final Handler handler = getManager().getUiHandler();
        Trail.getDataStore().processAllComments(new Function<Cursor, Void>() {
            @Override
            public Void apply(final Cursor cursor) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        map.addMarkable(new CommentMarker(DataStore.createComment(cursor)));
                    }
                });
                return null;
            }
        });
        return null;
    }
    
    @Override
    public void postExecute(Void result) throws Exception {
        // UI processing is done via a handler instead
    }
    
    @Override
    public void onException(Exception thrown) {
        super.onException(thrown);
        Toast.makeText(getCaller(), R.string.http_fail_generic, Toast.LENGTH_SHORT).show();
    }
}
