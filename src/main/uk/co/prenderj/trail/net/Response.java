package uk.co.prenderj.trail.net;

import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.params.HttpParams;

import uk.co.prenderj.trail.R;

/**
 * Wrapper class for a HttpResponse.
 * @author Joshua Prendergast
 */
public class Response {
    private HttpResponse httpResp;

    public Response(HttpResponse resp) {
        this.httpResp = resp;
    }
    
    public boolean isSuccess() {
        return getStatusCode() >= 200 && getStatusCode() < 300;
    }
    
    public int getStatusMessageResource() {
        if (isSuccess()) {
            return R.string.http_success;
        } else {
            switch (getStatusCode()) {
            case 400:
                return R.string.http_fail_400; // This should be validated client-side
            case 404:
                return R.string.http_fail_404;
            default:
                return R.string.http_fail_generic;
            }
        }
    }
    
    public int getStatusCode() {
        return getStatusLine().getStatusCode();
    }
    
    protected HttpResponse getHandle() {
        return httpResp;
    }
    
    // Delegates
    public boolean containsHeader(String name) {
        return httpResp.containsHeader(name);
    }

    public Header[] getAllHeaders() {
        return httpResp.getAllHeaders();
    }

    public HttpEntity getEntity() {
        return httpResp.getEntity();
    }

    public Header getFirstHeader(String name) {
        return httpResp.getFirstHeader(name);
    }

    public Header[] getHeaders(String name) {
        return httpResp.getHeaders(name);
    }

    public Header getLastHeader(String name) {
        return httpResp.getLastHeader(name);
    }

    public Locale getLocale() {
        return httpResp.getLocale();
    }

    public HttpParams getParams() {
        return httpResp.getParams();
    }

    public ProtocolVersion getProtocolVersion() {
        return httpResp.getProtocolVersion();
    }

    public StatusLine getStatusLine() {
        return httpResp.getStatusLine();
    }

    public HeaderIterator headerIterator() {
        return httpResp.headerIterator();
    }

    public HeaderIterator headerIterator(String name) {
        return httpResp.headerIterator(name);
    }
}
