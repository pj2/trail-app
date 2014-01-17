package uk.co.prenderj.trail.tasks;

import uk.co.prenderj.trail.util.CheckedAsyncTask;

public abstract class BaseTask<Params, Progress, Result> extends CheckedAsyncTask<Params, Progress, Result> {
    private CommentTasks manager;
    
    public BaseTask(CommentTasks manager) {
        this.manager = manager;
    }
    
    @Override
    protected void onProgressUpdate(Progress... values) {
        manager.updateProgressBar();
        super.onProgressUpdate(values);
    }
    
    @Override
    public void finish() {
        super.finish();
        manager.taskComplete(this);
    }
}
