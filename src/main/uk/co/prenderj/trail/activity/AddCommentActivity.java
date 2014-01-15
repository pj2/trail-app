package uk.co.prenderj.trail.activity;

import uk.co.prenderj.trail.CommentParams;
import uk.co.prenderj.trail.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class AddCommentActivity extends Activity {
	private EditText title;
    private EditText body;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_add_comment);
        
        // Show the Up button in the action bar.
        setupActionBar();
        
        title = (EditText) findViewById(R.id.comment_title_field);
        body = (EditText) findViewById(R.id.comment_body_field);
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_comment, menu);
        return true;
    }
    
    public void submit(View view) {
        CommentParams params = new CommentParams(MainActivity.instance().getLocationTracker().getLastLatLng(),
                title.getText().toString(),
                body.getText().toString());
        MainActivity.instance().getCommentManager().addComment(params);
        finish();
    }
}
