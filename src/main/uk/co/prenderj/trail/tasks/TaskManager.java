package uk.co.prenderj.trail.tasks;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;

import uk.co.prenderj.trail.Trail;
import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trail.model.CommentParams;
import uk.co.prenderj.trail.ui.MapController;

/**
 * Provides a top-layer to call for comment operations. All relevant subsystems are updated by calls to this class.
 * @author Joshua Prendergast
 */
public class TaskManager {
    private static final String TAG = "TaskManager";
    
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private MapController map;
    private ProgressBar bar;
    private AtomicInteger runningTasks = new AtomicInteger();
    
    public AsyncTask<CommentParams, Void, Comment> addComment(Context caller, CommentParams params, File cacheDir) {
        return startTask(new AddCommentTask(this, caller, cacheDir), params);
    }
    
    public AsyncTask<LatLng, Void, List<Comment>> loadNearbyComments(Context caller, LatLng pos) {
        return startTask(new LoadNearbyCommentsTask(this, caller), pos);
    }
    
    public AsyncTask<Void, Void, Void> loadStoredComments(Context caller) {
        return startTask(new LoadStoredCommentsTask(this, caller));
    }
    
    /**
     * Shows or hides the progress bar depending on the current task activity.
     * Must be called on the UI thread.
     */
    protected void updateProgressBar() {
        if (bar != null) {
            bar.setVisibility(isBusy() ? View.VISIBLE : View.INVISIBLE);
        }
    }
    
    /**
     * Starts a new task asynchronously, registering it with the manager.
     * @param task the task
     * @param params the task parameters
     * @return the task
     */
    protected <Params, Progress, Result> BaseTask<Params, Progress, Result> startTask(BaseTask<Params, Progress, Result> task, Params... params) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        runningTasks.incrementAndGet();
        return task;
    }
    
    /**
     * Called by {@link BaseTask} on completion.
     * @param task the caller
     */
    public void taskComplete(BaseTask<?, ?, ?> task) {
        runningTasks.decrementAndGet();
        updateProgressBar();
    }
    
    /**
     * Checks if any tasks are running.
     * @return true if one or more tasks are running
     */
    public boolean isBusy() {
        return runningTasks.get() > 0;
    }

    public ProgressBar getProgressBar() {
        return bar;
    }

    public void setProgressBar(ProgressBar bar) {
        this.bar = bar;
    }

    public MapController getMap() {
        return map;
    }

    public void setMap(MapController map) {
        this.map = map;
    }
    
    public Handler getUiHandler() {
        return uiHandler;
    }
}
