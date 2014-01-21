package uk.co.prenderj.trail.activity;

import java.io.File;
import java.io.IOException;

import uk.co.prenderj.trail.CommentParams;
import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.net.attachment.Attachment;
import uk.co.prenderj.trail.net.attachment.ImageAttachment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddCommentActivity extends Activity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_AUDIO_CAPTURE = 2;
    private static final String TAG = "AddCommentActivity";
    
    private Attachment attachment;
    private File attachmentSource;
    
    private EditText titleBox;
    private EditText bodyBox;
    private ImageButton cameraButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_add_comment);
        getActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
        
        titleBox = (EditText) findViewById(R.id.comment_title_field);
        bodyBox = (EditText) findViewById(R.id.comment_body_field);
        cameraButton = ((ImageButton) findViewById(R.id.camera_button));
        
        // Disable functions as needed
        cameraButton.setEnabled(canTakePhotos());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ((attachmentSource != null && !attachmentSource.delete())) {
            Log.e(TAG, "Failed to delete unfinished file");
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            attachment = new ImageAttachment(attachmentSource);
        } else {
            // TODO Audio attachment
        }
        attachmentSource = null; // Do not delete in onDestroy
        Toast.makeText(this, R.string.attachment_added, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Called once an attachment button is pressed (record / take photo).
     * @param view the caller
     */
    public void addAttachment(View view) {
        try {
            int request;
            Intent intent;
            if (view.getId() == R.id.camera_button) {
                if (!canTakePhotos()) return;
                
                request = REQUEST_IMAGE_CAPTURE;
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                attachmentSource = ImageAttachment.createSourceFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(attachmentSource));
            } else {
                // TODO Microphone
                throw new IllegalStateException("Microphone not yet implemented");
            }
        
            if (intent.resolveActivity(getPackageManager()) != null) { // Check there is a handling activity available
                startActivityForResult(intent, request);
            } else {
                Toast.makeText(this, R.string.fail_generic, Toast.LENGTH_SHORT).show(); // TODO More specific message
            }
        } catch (IOException e) {
            Toast.makeText(this, R.string.io_error, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Called once the submit button is pressed.
     * @param view the caller
     */
    public void submit(View view) {
        String title = titleBox.getText().toString();
        String body = bodyBox.getText().toString();
        
        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(this, R.string.missing_argument, Toast.LENGTH_SHORT).show();
        } else {
            // Ensure the attachment was written to by another activity (e.g. camera)
            CommentParams params = new CommentParams(MainActivity.instance().getLocationTracker().getLastLatLng(), title, body, attachment);
            
            MainActivity.instance().getCommentManager().addComment(params);
            finish();
        }
    }
    
    protected boolean canTakePhotos() {
        // Check we can actually take pictures
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
