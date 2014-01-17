package uk.co.prenderj.trail.ui;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import uk.co.prenderj.trail.R;

import android.content.res.XmlResourceParser;
import android.util.Log;

/**
 * An overlay which reads and displays GPX-formatted data.
 * @author Joshua Prendergast
 */
public class Route {
    private static final String TAG = "Route";
    private PolylineOptions opt;
    
    public Route(XmlResourceParser gpx) throws IOException {
        load(gpx);
    }
    
    protected PolylineOptions createOptions() {
        return new PolylineOptions().color(R.color.route_color).zIndex(1.0f);
    }
    
    protected void load(XmlResourceParser gpx) throws IOException {
        if (opt != null) return; // Load once
        try {
            opt = createOptions();
            int eventType = -1;
            while ((eventType = gpx.next()) != XmlPullParser.END_DOCUMENT) {
                // Assume there is just one route in the file
                if (eventType == XmlPullParser.START_TAG && gpx.getName().equals("trkpt")) {
                    if (gpx.getAttributeCount() == 2) {
                        float lat = gpx.getAttributeFloatValue(0, 0.0f);
                        float lon = gpx.getAttributeFloatValue(1, 0.0f);
                        opt.add(new LatLng(lat, lon));
                    } else {
                        Log.w(TAG, "trkpt has invalid attribute count, ignoring");
                    }
                }
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, "XML parser exception", e);
            throw new RuntimeException(e);
        } finally {
            gpx.close();
        }
    }
    
    public void attach(GoogleMap gmap) {
        gmap.addPolyline(opt);
    }
}
