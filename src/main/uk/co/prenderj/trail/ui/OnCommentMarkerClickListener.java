package uk.co.prenderj.trail.ui;

import uk.co.prenderj.trail.model.Comment;

import com.google.android.gms.maps.model.Marker;

public interface OnCommentMarkerClickListener {
    public void onCommentClick(Marker m, Comment c);
}
