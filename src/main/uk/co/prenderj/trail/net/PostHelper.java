package uk.co.prenderj.trail.net;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import uk.co.prenderj.trail.util.Pair;


/**
 * Helper to send POST data.
 * @author Joshua Prendergast
 */
public class PostHelper extends HttpPost {
    private List<Pair<String>> pairs = new ArrayList<Pair<String>>();

    public PostHelper(String target) {
        super(target);
    }

    public PostHelper(String target, Pair<String>... pairs) throws UnsupportedEncodingException {
        super(target);
        for (Pair<String> pair : pairs) {
            put(pair);
        }
        build();
    }

    public void put(String key, String value) {
        pairs.add(new Pair<String>(key, value));
    }

    public void put(Pair<String> pair) {
        pairs.add(pair);
    }

    public void build() throws UnsupportedEncodingException {
        setEntity(new UrlEncodedFormEntity(pairs));
    }
}
