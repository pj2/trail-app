package uk.co.prenderj.trail.activity;

import uk.co.prenderj.trail.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class AddCommentActivity extends Activity {
    private EditText body;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        
        // Show the Up button in the action bar.
        setupActionBar();
        
        body = (EditText) findViewById(R.id.editText1);
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
        MainActivity.instance().getCommentManager().addComment(this, body.getText().toString());
        finish();
    }
}
