package uk.co.prenderj.trail.adapter;

import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trailshared.function.Transformer;

import com.google.android.gms.maps.model.LatLng;

/**
 * Converts a CSV string into a comment.
 * @author Joshua Prendergast
 */
public class CSVToComment implements Transformer<String[], Comment> {
    @Override
    public Comment call(String[] value) throws RuntimeException {
        long id = Long.parseLong(value[0]);
        LatLng location = new LatLng(Double.parseDouble(value[1]), Double.parseDouble(value[2]));
        String body = value[3];
        return new Comment(id, location, body);
    }
}
