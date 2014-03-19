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

public class ImageFile extends AttachmentFile {
    private static final String FILE_SUFFIX = ".jpg";
    
    public ImageFile(File source) {
        super(AttachmentFile.ATTACHMENT_IMAGE, source);
    }
    
    @Override
    public File createCompressed(File directory) throws IOException {
        FileInputStream is = null;
        FileOutputStream out = null;
        try {
            File compressed = File.createTempFile(getSourceFile().getName(), null, directory);
            
            is = new FileInputStream(getSourceFile());
            out = new FileOutputStream(compressed);
            BitmapDrawable bitmap = new BitmapDrawable(is);
            
            if (!bitmap.getBitmap().compress(CompressFormat.JPEG, 75, out)) 
                throw new IOException();
            
            return compressed;
        } finally {
            if (is != null)
                is.close();
            if (out != null)
                out.close();
        }
    }
    
    public static File createSourceFile() throws IOException {
        // Create a unique image file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        File out = File.createTempFile(timestamp, FILE_SUFFIX, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        return out;
    }
    
    @Override
    public ContentType getContentType() {
        return ContentType.create("image/jpeg");
    }
    
    @Override
    public String getFileSuffix() {
        return FILE_SUFFIX;
    }
}
