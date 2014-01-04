package uk.co.prenderj.trail.net;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;

import uk.co.prenderj.trail.util.Pair;

import android.net.http.AndroidHttpClient;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Helper for HTTP communication with the server. This class is
 * thread safe (tasks run sequentially).
 * @author Joshua Prendergast
 */
public class WebClient {
    private URL hostname;
    private ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    private HttpClient http = AndroidHttpClient.newInstance("Mozilla/5.0");

    public WebClient(URL hostname) {
        this.hostname = hostname;
    }

    /**
     * Sends a HTTP POST to /comment/add.
     * @param position the comment location
     * @param position the comment text
     * @return a Future containing the server's response
     */
    public ListenableFuture<CommentResponse> registerComment(final LatLng position, final String body) {
        return executor.submit(new Callable<CommentResponse>() {
            @Override
            public CommentResponse call() throws Exception {
                HttpPost post = newPostData(hostname + "/comments/",
                        new Pair<Double>("lat", position.latitude),
                        new Pair<Double>("lng", position.longitude),
                        new Pair<String>("body", body));
                return new CommentResponse(http.execute(post));
            }
        });
    }
    
    public ListenableFuture<CommentResponse> loadNearbyComments(final LatLng position) {
        return executor.submit(new Callable<CommentResponse>() {
            @Override
            public CommentResponse call() throws Exception {
                return new CommentResponse(http.execute(new HttpGet(String.format(hostname + "/comments/nearby/%d/%d", position.latitude, position.longitude))));
            }
        });
    }
    
    public static HttpPost newPostData(String target, Pair<?>... pairs) {
        try {
            HttpPost post = new HttpPost(target);
            post.setEntity(new UrlEncodedFormEntity(Arrays.asList(pairs)));
            return post;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e); // This shouldn't happen
        }
    }
}
