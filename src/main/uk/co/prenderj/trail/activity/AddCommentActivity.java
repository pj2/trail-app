package uk.co.prenderj.trail.activity;

import java.io.File;
import java.io.IOException;

import uk.co.prenderj.trail.R;
import uk.co.prenderj.trail.Trail;
import uk.co.prenderj.trail.model.CommentParams;
import uk.co.prenderj.trail.net.attachment.AttachmentFile;
import uk.co.prenderj.trail.net.attachment.AudioFile;
import uk.co.prenderj.trail.net.attachment.ImageFile;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
    
    private File attachmentSource;
    private int attachmentType = -1;
    
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        
        if (requestCode == REQUEST_AUDIO_CAPTURE) {
            attachmentSource = new File(getPath(data.getData()));
            attachmentType = AttachmentFile.ATTACHMENT_AUDIO;
        } else {
            attachmentType = AttachmentFile.ATTACHMENT_IMAGE;
        }
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
                attachmentSource = ImageFile.createSourceFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(attachmentSource));
            } else {
                request = REQUEST_AUDIO_CAPTURE;
                intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                intent.putExtra(MediaStore.Audio.Media.EXTRA_MAX_BYTES, 1024 * 512); // TODO
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
            Intent returnIntent = new Intent();
            returnIntent.putExtra("title", title);
            returnIntent.putExtra("body", body);
            
            // Check if the attachment was successfully created
            if (attachmentType != -1) {
                returnIntent.putExtra("attachment", attachmentSource.getAbsolutePath());
                returnIntent.putExtra("attachmentType", attachmentType);
            }
            
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }
    
    protected boolean canTakePhotos() {
        // Check we can actually take pictures
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    
    private String getPath(Uri uri) {
        String[] projection = { MediaStore.Audio.Media.DATA };
        CursorLoader loader = new CursorLoader(this, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        
        int column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column);
    }
}
