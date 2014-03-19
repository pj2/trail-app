package uk.co.prenderj.trail.net.attachment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.entity.ContentType;

import android.os.Environment;

public class AudioFile extends AttachmentFile {
    public AudioFile(File source) {
        super(AttachmentFile.ATTACHMENT_AUDIO, source);
    }
    
    @Override
    public File createCompressed(File directory) throws IOException {
        return getSourceFile();
    }
    
    @Override
    public ContentType getContentType() {
        return ContentType.create("audio/mpeg");
    }
    
    @Override
    public String getFileSuffix() {
        return ".mp3";
    }
}
