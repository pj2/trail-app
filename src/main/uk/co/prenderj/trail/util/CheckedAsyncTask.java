package uk.co.prenderj.trail.util;

import android.os.AsyncTask;

public abstract class CheckedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    public abstract Result call(Params... params) throws Exception;
    
    public abstract void finish(Result result) throws Exception;
    
    @Override
    protected Result doInBackground(Params... params) {
        try {
            return call(params);
        } catch (Exception e) {
            onException(e);
            return null;
        }
    }
    
    @Override
    protected void onPostExecute(Result result) {
        try {
            finish(result);
        } catch (Exception e) {
            onException(e);
        }
    }
    
    public void onException(Exception thrown) {
        cancel(true);
    }
}
