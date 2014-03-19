package uk.co.prenderj.trail.activity;

import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.model.Comment;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        View v = inflater.inflate(R.layout.comment_fragment, container, false);
        
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView body = (TextView) v.findViewById(R.id.body);
        Button viewAttachment = (Button) v.findViewById(R.id.view_attachment);
        
        title.setText(comment.title);
        body.setText(comment.body);
        
        viewAttachment.setVisibility(comment.attachmentId != -1 ? View.VISIBLE : View.GONE);
        
        return v;
    }
    
    public void viewAttachment(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.server_host) + "/attachments/" + comment.attachmentId)));
    }
    
    public static CommentFragment newInstance(Comment comment) {
        CommentFragment frag = new CommentFragment();
        
        Bundle bundle = new Bundle();
        bundle.putSerializable("comment", comment);
        frag.setArguments(bundle);
        
        return frag;
    }
}
