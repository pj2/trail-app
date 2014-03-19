package uk.co.prenderj.trail.net.attachment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.entity.ContentType;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;

/**
 * A file attached to a comment.
 * @author Joshua Prendergast
 */
public abstract class AttachmentFile {
    public static final int ATTACHMENT_AUDIO = 1;
    public static final int ATTACHMENT_IMAGE = 2;
    
    private int type;
    private File source;
    
    public abstract File createCompressed(File directory) throws IOException;
    
    public abstract ContentType getContentType();
    
    public abstract String getFileSuffix();
    
    public AttachmentFile(int type, File source) {
        this.source = source;
        this.type = type;
    }
    
    public void delete() throws IOException {
        if ((source != null && !source.delete())) {
            throw new IOException();
        }
    }
    
    public static AttachmentFile newInstance(File source, int type) {
        switch (type){
        case ATTACHMENT_AUDIO:
            return new AudioFile(source);
        case ATTACHMENT_IMAGE:
            return new ImageFile(source);
        default:
            throw new AssertionError();
        }
    }
    
    public File getSourceFile() {
        return source;
    }
    
    public int getType() {
        return type;
    }
}
