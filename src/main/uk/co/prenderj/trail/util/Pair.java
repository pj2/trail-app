package uk.co.prenderj.trail.util;

import org.apache.http.NameValuePair;

/**
 * A generic key-value pair.
 * @author Joshua Prendergast
 * @param <T> the value type
 */
public class Pair<T> implements NameValuePair {
    private String name;
    private T value;
    
    public Pair(String name, T value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getValue() {
        return String.valueOf(value);
    }
    
    public T getObject() {
        return value;
    }
}
