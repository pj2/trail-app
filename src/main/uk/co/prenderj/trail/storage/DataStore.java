package uk.co.prenderj.trail.storage;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import uk.co.prenderj.trail.activity.MainActivity;
import uk.co.prenderj.trail.model.Comment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * DAO for the SQLite database.
 * @author Joshua Prendergast
 */
public class DataStore {
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private DatabaseHelper helper = new DatabaseHelper(); // hack
    
    protected SQLiteDatabase getConnection(boolean write) {
        return write ? helper.getWritableDatabase() : helper.getReadableDatabase();
    }
    
    /**
     * Inserts the given comments into the database.
     * @param comments the comments to add
     * @return a Future returning the comments that were actually inserted
     */
    public Future<List<Comment>> insertComments(final Comment... comments) {
        return executor.submit(new Callable<List<Comment>>() {
            @Override
            public List<Comment> call() throws Exception {
                SQLiteStatement st = null;
                SQLiteDatabase conn = null;
                try {
                    conn = getConnection(true);
                    st = conn.compileStatement("REPLACE INTO comments (_id, lat, lng, body, attachmentId) VALUES(?, ?, ?, ?, ?)");
                    List<Comment> inserted = Lists.newArrayList();
                    for (Comment comment : comments) {
                        st.bindLong(1, comment.id);
                        st.bindDouble(2, comment.location.latitude);
                        st.bindDouble(3, comment.location.longitude);
                        st.bindString(4, comment.body);
                        st.bindLong(5, comment.attachmentId);
                        
                        if (st.executeInsert() != -1) { // Assuming non-unique primary key will force -1
                            inserted.add(comment);
                        }
                    }
                    return inserted;
                } finally {
                    if (st != null)
                        st.close();
                    if (conn != null)
                        conn.close();
                }
            }
        });
    }
    
    /**
     * Retrieves comments from the database by ID.
     * @return a Future returning the list of comments
     */
    public Future<List<Comment>> getCommentsById(final long... ids) {
        return executor.submit(new Callable<List<Comment>>() {
            @Override
            public List<Comment> call() throws Exception {
                SQLiteDatabase conn = null;
                Cursor cursor = null;
                try {
                    // Build the arguments list
                    StringBuilder params = new StringBuilder();
                    for (int i = 0; i < ids.length; i++) {
                        params.append(i);
                        if (i != ids.length - 1)
                            params.append(", ");
                    }
                    
                    conn = getConnection(false);
                    cursor = conn.query("comments", null, "_id IN (" + params.toString() + ")", null, null, null, null);
                    
                    List<Comment> out = Lists.newArrayList();
                    while (cursor.moveToNext()) {
                        out.add(createComment(cursor));
                    }
                    return out;
                } finally {
                    if (cursor != null)
                        cursor.close();
                    if (conn != null)
                        conn.close();
                }
            }
        });
    }
    
    /**
     * Selects all comments from the database for processing.
     * @param proc the processor. The cursor is advanced automatically.
     * @return a Future returning null when all comments are processed
     */
    public Future<?> processAllComments(final Function<Cursor, ?> proc) {
        return executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                SQLiteDatabase conn = null;
                Cursor cursor = null;
                try {
                    conn = getConnection(false);
                    cursor = conn.query("comments", null, null, null, null, null, null);
                    while (cursor.moveToNext()) {
                        proc.apply(cursor);
                    }
                    return null;
                } finally {
                    if (cursor != null)
                        cursor.close();
                    if (conn != null)
                        conn.close();
                }
            }
        });
    }
    
    public static Comment createComment(Cursor cursor) {
        return new Comment(cursor.getInt(0),
                new LatLng(cursor.getDouble(1), cursor.getDouble(2)),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getLong(5));
    }
}