package uk.co.prenderj.trail.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import uk.co.prenderj.trail.model.CommentParams;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

/**
 * Helper for HTTP communication with the server. This class is thread safe.
 * @author Joshua Prendergast
 */
public class WebClient {
    private static final String TAG = "WebClient";
    
    private URL hostname;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private HttpClient http = new DefaultHttpClient(); // TODO Use async client
    private HashFunction hashFunction = Hashing.crc32();
    
    public void close() {
        executor.shutdown();
    }
    
    public <T> Future<T> queueTask(Callable<T> callable) {
        Preconditions.checkNotNull(hostname, "Hostname not set");
        return executor.submit(callable);
    }
    
    /**
     * Creates a comment on the server.
     * @param params the new comment parameters
     * @return a Future containing the server's response
     */
    public Future<CommentResponse> uploadComment(final CommentParams params, final File cacheDirectory) {
        return queueTask(new Callable<CommentResponse>() {
            @Override
            public CommentResponse call() throws Exception {
                HashCode boundary = hashFunction.hashLong(new Random().nextLong());
                
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setBoundary(boundary.toString())
                    .setCharset(Charsets.UTF_8)
                    .setMode(HttpMultipartMode.RFC6532)
                    .addTextBody("lat", String.valueOf(params.position.latitude), ContentType.TEXT_PLAIN)
                    .addTextBody("lng", String.valueOf(params.position.longitude), ContentType.TEXT_PLAIN)
                    .addTextBody("title", params.title, ContentType.TEXT_PLAIN)
                    .addTextBody("body", params.body, ContentType.TEXT_PLAIN);
                
                // Add the compressed attachment if available
                File compressed = null;
                try {
                    if (params.attachmentFile != null) {
                        compressed = params.attachmentFile.createCompressed(cacheDirectory);
                        builder.addBinaryBody("attachment", compressed, params.attachmentFile.getContentType(), "attachment" + params.attachmentFile.getFileSuffix());
                    }
                    
                    // Build and send
                    HttpPost post = new HttpPost(new URL(hostname, "/comments").toURI());
                    post.setEntity(builder.build());
                    
                    CommentResponse resp = new CommentResponse(http.execute(post));
                    if (resp.isSuccess()) {
                        return resp;
                    } else {
                        throw new HttpResponseException(resp.getStatusCode(), null);
                    }
                } finally {
                    if (compressed != null && !compressed.delete())
                        Log.e(TAG, "Failed to delete cached file");
                }
            }
        });
    }
    
    /**
     * Downloads all nearby comments from the server.
     * @param position the origin (i.e. the phone's location)
     * @return a Future containing the server's response
     */
    public Future<CommentResponse> downloadNearbyComments(final LatLng position) {
        return executor.submit(new Callable<CommentResponse>() {
            @Override
            public CommentResponse call() throws Exception {
                // Contact hostname/nearby/lat/lng
                HttpGet get = new HttpGet(new URL(hostname, String.format("/nearby/%f/%f", position.latitude, position.longitude)).toURI());
                CommentResponse resp = new CommentResponse(http.execute(get));
                if (resp.isSuccess()) {
                    return resp;
                } else {
                    throw new HttpResponseException(resp.getStatusCode(), null);
                }
            }
        });
    }

    public URL getHostname() {
        return hostname;
    }

    public void setHostname(URL hostname) {
        this.hostname = hostname;
    }
}
