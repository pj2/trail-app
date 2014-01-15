package uk.co.prenderj.trail.net;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import uk.co.prenderj.trail.CommentParams;
import uk.co.prenderj.trail.util.Pair;
import android.net.http.AndroidHttpClient;

import com.google.android.gms.maps.model.LatLng;

/**
 * Helper for HTTP communication with the server. This class is
 * thread safe (tasks run sequentially).
 * @author Joshua Prendergast
 */
public class WebClient {
    private URL hostname;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private HttpClient http = AndroidHttpClient.newInstance("Mozilla/5.0");

    public WebClient(URL hostname) {
        this.hostname = hostname;
    }

    /**
     * Sends a HTTP POST to /comment/add.
     * @param params the new comment parameters
     * @return a Future containing the server's response
     */
    public Future<CommentResponse> registerComment(final CommentParams params) {
        return executor.submit(new Callable<CommentResponse>() {
            @Override
            public CommentResponse call() throws Exception {
                HttpPost post = newPostData(new URL(hostname, "/comments/"),
                        new Pair<Double>("lat", params.position.latitude),
                        new Pair<Double>("lng", params.position.longitude),
                        new Pair<String>("title", params.title),
                        new Pair<String>("body", params.body));
                CommentResponse resp = new CommentResponse(http.execute(post));
                if (!resp.isSuccess()) {
                	throw new HttpResponseException(resp.getStatusCode(), null);
                } else {
                	return resp;
                }
            }
        });
    }
    
    public Future<CommentResponse> loadNearbyComments(final LatLng position) {
        return executor.submit(new Callable<CommentResponse>() {
            @Override
            public CommentResponse call() throws Exception {
                URI target = new URL(hostname, String.format("/nearby/%f/%f", position.latitude, position.longitude)).toURI();
                CommentResponse resp = new CommentResponse(http.execute(new HttpGet(target)));
                if (!resp.isSuccess()) {
                	throw new HttpResponseException(resp.getStatusCode(), null);
                } else {
                	return resp;
                }
            }
        });
    }
    
    public static HttpPost newPostData(URL target, Pair<?>... pairs) {
        try {
            HttpPost post = new HttpPost(target.toURI());
            post.setEntity(new UrlEncodedFormEntity(Arrays.asList(pairs)));
            return post;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e); // This shouldn't happen
        } catch (URISyntaxException e) {
        	throw new RuntimeException(e); // This shouldn't happen either
		}
    }
}
