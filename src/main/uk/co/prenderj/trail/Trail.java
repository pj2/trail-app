package uk.co.prenderj.trail;

import android.content.Context;
import android.widget.Toast;
import uk.co.prenderj.trail.net.WebClient;
import uk.co.prenderj.trail.storage.DataStore;
import uk.co.prenderj.trail.tasks.TaskManager;

/**
 * Convenient access to shared sub-systems.
 * @author Joshua Prendergast
 */
public class Trail {
    private static final Trail INSTANCE = new Trail();
    
    private final TaskManager taskManager = new TaskManager();
    private final DataStore dataStore = new DataStore();
    private final LocationTracker tracker = new LocationTracker();
    private final WebClient http = new WebClient();
    
    public void showToast(Context ctx, int resId, int duration) {
        Toast.makeText(ctx, resId, duration).show();
    }
    
    public static TaskManager getTaskManager() {
        return INSTANCE.taskManager;
    }

    public static DataStore getDataStore() {
        return INSTANCE.dataStore;
    }
    
    public static LocationTracker getLocationTracker() {
        return INSTANCE.tracker;
    }
    
    public static WebClient getWebClient() {
        return INSTANCE.http;
    }
}
