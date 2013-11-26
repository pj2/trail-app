package uk.co.prenderj.trail.net;

import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import uk.co.prenderj.trail.util.Pair;

import com.google.android.gms.maps.model.LatLng;

/**
 * Helper for HTTP communication with the server. This class is
 * thread safe (tasks run sequentially).
 * @author Joshua Prendergast
 */
public class WebClient {
    private URL hostname;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private HttpClient http = new DefaultHttpClient();

    public WebClient(URL hostname) {
        this.hostname = hostname;
    }

    /**
     * Sends a HTTP POST to /comment/add.
     * @param position the comment location
     * @param position the comment text
     * @return a Future containing the server's response
     */
    public Future<CommentResponse> registerComment(final LatLng position, final String body) {
        return executor.submit(new Callable<CommentResponse>() {
            @SuppressWarnings("unchecked")
            @Override
            public CommentResponse call() throws Exception {
                HttpPost post = new PostHelper(hostname.getPath(), new Pair<String>("lat", String.valueOf(position.latitude)), new Pair<String>(
                        "lng", String.valueOf(position.longitude)), new Pair<String>("body", body));
                return new CommentResponse(http.execute(post));
            }
        });
    }

    public long getDefaultTimeoutMillis() {
        return 10000;
    }
}
