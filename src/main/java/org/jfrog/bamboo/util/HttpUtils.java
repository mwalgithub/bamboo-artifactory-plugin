package org.jfrog.bamboo.util;

import com.google.gson.Gson;
import com.sun.syndication.io.impl.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Common HTTP related action utils
 *
 * @author Aviad Shikloshi
 */
public class HttpUtils {

    private static Gson gson = new Gson();

    /**
     * Create basic authentication header
     */
    public static Header createAuthorizationHeader(String username, String password) {
        String authRawString = username + ":" + password;
        String encoded = Base64.encode(authRawString);
        String auth = "Basic " + encoded;
        return new BasicHeader("Authorization", auth);
    }

    /**
     * Parse response to appropriate object
     */
    public static <T> T handleResponse(HttpResponse response, Class<T> toClass) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            InputStream content = response.getEntity().getContent();
            String jsonString = IOUtils.toString(content);
            return objectFromJsonString(jsonString, toClass);
        }
        throw new RuntimeException("Request to Artifactory failed.");
    }

    /**
     * Convert a String represented json and convert it to an object of type T
     */
    public static <T> T objectFromJsonString(String jsonString, Class<T> toClass) {
        return gson.fromJson(jsonString, toClass);
    }

    /**
     * Convert Object to JSON String
     */
    public static <T> String jsonStringToObject(T object) {
        return gson.toJson(object);
    }

    public static String encodePath(String string) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, CharEncoding.UTF_8).replaceAll("\\+", "%20");
    }

}
