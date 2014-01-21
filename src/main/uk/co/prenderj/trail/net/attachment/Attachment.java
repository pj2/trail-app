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
public abstract class Attachment {
    private File source;
    
    public abstract File createCompressed(File directory) throws IOException;
    
    public abstract ContentType getContentType();
    
    public abstract String getFileSuffix();
    
    public Attachment(File source) {
        this.source = source;
    }
    
    public void delete() throws IOException {
        if ((source != null && !source.delete())) {
            throw new IOException();
        }
    }
    
    public File getSourceFile() {
        return source;
    }
}
