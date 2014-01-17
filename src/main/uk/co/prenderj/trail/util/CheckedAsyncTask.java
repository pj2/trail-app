package uk.co.prenderj.trail.util;

import android.os.AsyncTask;

public abstract class CheckedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private Exception thrown;
    
    public abstract Result call(Params... params) throws Exception;
    
    public abstract void postExecute(Result result) throws Exception;
    
    public void finish() {
        // Override me
    }
    
    @Override
    protected Result doInBackground(Params... params) {
        try {
            return call(params);
        } catch (Exception e) {
            thrown = e;
            return null;
        }
    }
    
    @Override
    protected void onPostExecute(Result result) {
        if (thrown != null) {
            onException(thrown);
        }
        try {
            postExecute(result);
        } catch (Exception e) {
            onException(e);
        } finally {
            finish();
        }
    }
    
    public void onException(Exception thrown) {
        cancel(true);
    }
}
