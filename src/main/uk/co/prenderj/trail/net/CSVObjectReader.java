package uk.co.prenderj.trail.net;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import com.google.common.base.Function;

import au.com.bytecode.opencsv.CSVReader;

public class CSVObjectReader<T> implements Closeable {
    private CSVReader reader;
    
    public CSVObjectReader(Reader reader, int size) {
        this.reader = new CSVReader(new BufferedReader(reader, size));
    }
    
    public CSVObjectReader(Reader reader) {
        this(reader, 256);
    }
    
    public T readObject(Function<String[], T> adapter) throws IOException {
        try {
            String[] next = reader.readNext();
            return next == null ? null : adapter.apply(next);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
