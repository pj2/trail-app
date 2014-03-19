package uk.co.prenderj.trail.tasks;

import android.content.Context;
import uk.co.prenderj.trail.util.CheckedAsyncTask;

public abstract class BaseTask<Params, Progress, Result> extends CheckedAsyncTask<Params, Progress, Result> {
    private final TaskManager manager;
    private final Context caller;
    
    public BaseTask(TaskManager manager, Context caller) {
        this.manager = manager;
        this.caller = caller;
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

    public TaskManager getManager() {
        return manager;
    }
    
    public Context getCaller() {
        return caller;
    }
}
