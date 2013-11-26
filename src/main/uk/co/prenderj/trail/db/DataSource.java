package uk.co.prenderj.trail.db;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import uk.co.prenderj.trail.model.Comment;
import uk.co.prenderj.trailshared.function.Processor;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * DAO for the SQLite database.
 * @author Joshua Prendergast
 */
public class DataSource {
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private DatabaseHelper helper;
    
    protected SQLiteDatabase getConnection(boolean write) {
        return write ? helper.getWritableDatabase() : helper.getReadableDatabase();
    }
    
    /**
     * Inserts the given comments into the database.
     * @param comments the comments to add
     * @return a Future returning null on success
     */
    public Future<?> insertComments(final Comment... comments) {
        return executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                SQLiteStatement st = null;
                SQLiteDatabase conn = null;
                try {
                    conn = getConnection(true);
                    st = conn.compileStatement("INSERT INTO comment (_id, lat, lng, body) VALUES(?, ?, ?, ?)");
                    for (Comment comment : comments) {
                        st.bindLong(1, comment.id);
                        st.bindDouble(2, comment.location.latitude);
                        st.bindDouble(3, comment.location.longitude);
                        st.bindString(4, comment.body);
                        if (st.executeInsert() == -1) {
                            throw new SQLException();
                        }
                    }
                    return null;
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
     * Selects all comments from the database for processing.
     * @param proc the processor. The cursor is advanced automatically.
     * @return a Future returning null when all comments are processed
     */
    public Future<?> loadComments(final Processor<Cursor> proc) {
        return executor.submit(new Callable<Object>() {
            @Override
            public Collection<?> call() throws Exception {
                SQLiteDatabase conn = null;
                Cursor cursor = null;
                try {
                    conn = getConnection(false);
                    cursor = conn.query("comment", null, null, null, null, null, null);
                    while (cursor.moveToNext()) {
                        proc.call(cursor);
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
}
