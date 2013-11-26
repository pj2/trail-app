package uk.co.prenderj.trail.net;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import uk.co.prenderj.trail.adapter.CSVToComment;
import uk.co.prenderj.trail.model.Comment;

public class CommentResponse extends Response {
    private boolean exhausted;

    public CommentResponse(HttpResponse resp) {
        super(resp);
    }

    /**
     * Reads a single comment from the response's content.
     * @return the comment
     * @throws IllegalStateException if the content is non-repeatable and has already been consumed
     * @throws IOException if an IO error occurs or the content is malformed
     */
    public Comment getSingleComment() throws IllegalStateException, IOException {
        CSVObjectReader<Comment> reader = null;
        try {
            reader = getCommentReader();
            return reader.readObject(new CSVToComment());
        } finally {
            if (reader != null) {
                reader.close();
                // getHandle().getEntity.consumeContent();
            }

        }
    }

    /**
     * Gets a reader which will treat this response's content as CSV comments.
     * @return the comment reader
     * @throws IllegalStateException if the content is non-repeatable and has already been consumed
     * @throws IOException if an IO error occurs or the content is malformed
     */
    public CSVObjectReader<Comment> getCommentReader() throws IllegalStateException, IOException {
        HttpEntity entity = getHandle().getEntity();
        if (exhausted) {
            throw new IllegalStateException("Content already used and not repeatable");
        }
        String encoding = entity.getContentEncoding() == null ? "UTF-8" : entity.getContentEncoding().getValue();
        CSVObjectReader<Comment> out = new CSVObjectReader<Comment>(new InputStreamReader(entity.getContent(), encoding));
        exhausted = !entity.isRepeatable();
        return out;
    }
}
