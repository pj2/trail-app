package uk.co.prenderj.trail.adapter;

import uk.co.prenderj.trail.model.Comment;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Function;

/**
 * Converts a CSV string into a comment.
 * @author Joshua Prendergast
 */
public class CSVToComment implements Function<String[], Comment> {
    @Override
    public Comment apply(String[] value) {
        return new Comment(Long.parseLong(value[0]), new LatLng(Double.parseDouble(value[1]), Double.parseDouble(value[2])), value[3], value[4]);
    }
}
