package uk.co.prenderj.trail.net;

import java.io.File;

/**
 * A comment attachment; an audio file or image.
 * @author Joshua Prendergast
 */
public class Attachment {
    public final File source;
    public final AttachmentType type;
    
    public enum AttachmentType {
        IMAGE, AUDIO;
    }

    public Attachment(File source, AttachmentType type) {
        this.source = source;
        this.type = type;
    }
}
