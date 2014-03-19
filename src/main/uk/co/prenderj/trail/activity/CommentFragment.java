package uk.co.prenderj.trail.activity;

import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.model.Comment;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CommentFragment extends DialogFragment {
    private Comment comment;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comment = (Comment) getArguments().getSerializable("comment");
        
        setStyle(STYLE_NO_FRAME, 0);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.comment_fragment_image, container, false);
        
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView body = (TextView) v.findViewById(R.id.body);
        
        title.setText(comment.title);
        body.setText(comment.body);
        
        return v;
    }
    
    public static CommentFragment newInstance(Comment comment) {
        CommentFragment frag = new CommentFragment();
        
        Bundle bundle = new Bundle();
        bundle.putSerializable("comment", comment);
        frag.setArguments(bundle);
        
        return frag;
    }
}
