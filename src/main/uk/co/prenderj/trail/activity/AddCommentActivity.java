package uk.co.prenderj.trail.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.prenderj.trail.CommentParams;
import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.net.Attachment;
import uk.co.prenderj.trail.net.Attachment.AttachmentType;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddCommentActivity extends Activity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_AUDIO_CAPTURE = 2;
    private static final String TAG = "AddCommentActivity";
    
    private File attachmentFile;
    private Attachment attachment;
    
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
        
        // Disable camera functions if needed
        if (!canTakePhotos()) {
            cameraButton.setEnabled(false);
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
        // XXX This can be replaced by file extension matching
        if (resultCode == RESULT_OK) {
            // Determine attachment type
            AttachmentType type;
            switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                type = AttachmentType.IMAGE;
                break;
            case REQUEST_AUDIO_CAPTURE:
                type = AttachmentType.AUDIO;
                break;
            default:
                Log.e(TAG, "Unknown activity result");
                return;
            }
            
            // TODO Reveal picture to other apps
            attachment = new Attachment(attachmentFile, type);
            Toast.makeText(this, R.string.attachment_added, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Called when the user asks to add a picture.
     * @param view the caller
     */
    public void takePicture(View view) {
        if (!canTakePhotos()) return;
        
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                attachmentFile = createImageFile();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(attachmentFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (IOException e) {
                Toast.makeText(this, R.string.unable_to_write_to_sd_card, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * Called when the user asks to add a recording.
     * @param view the caller
     */
    public void recordSound(View view) {
        // TODO
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
    
    private File createImageFile() throws IOException {
        // Create a unique image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );
        return image;
    }
}
