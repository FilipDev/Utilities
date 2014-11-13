package me.pauzen.jlib.http.request;

import me.pauzen.jlib.http.cookie.Cookie;
import me.pauzen.jlib.http.headers.Header;
import me.pauzen.jlib.http.headers.UserAgent;
import me.pauzen.jlib.http.result.Result;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class HttpRequest {

    private UserAgent   userAgent;
    private Set<Cookie> cookies;

    public UserAgent getUserAgent() {
        return this.userAgent;
    }

    /**
     * Makes a UserAgent object then sets the connection's user agent.
     *
     * @param userAgent The String
     * @return
     */
    public HttpRequest setUserAgent(String userAgent) {
        this.userAgent = new UserAgent(userAgent);
        return this;
    }

    /**
     * Sets the connection's user agent.
     *
     * @param userAgent The UserAgent value to set the UserAgent to.
     * @return The HttpRequest object for further building.
     */
    public HttpRequest setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * Universal send method.
     *
     * @return The HttpRequest object for further building.
     */
    public abstract HttpRequest send();

    public abstract HttpRequest applyHeader(Header header);

    /**
     * Returns the result object.
     *
     * @return Result object.
     * @throws IOException
     */
    public abstract Result getResult() throws IOException;

    /**
     * Closes the connection.
     *
     * @return The HttpRequest object for further building.
     * @throws IOException
     */
    public abstract HttpRequest closeConnection() throws IOException;

    /**
     * Gets the connection.
     *
     * @return The connection.
     */
    protected abstract HttpURLConnection getConnection();

    /**
     * Gets Cookie value from the request.
     *
     * @return A map of the Cookies.
     */
    public Map<String, Cookie> getCookies() {
        Map<String, Cookie> cookiesMap = new HashMap<>();
        for (String cookies : getConnection().getHeaderFields().get("Set-Cookie")) {
            Cookie cookie = new Cookie(cookies);
            cookiesMap.put(cookie.getName(), cookie);
        }
        return cookiesMap;
    }

    /**
     * Applies Cookies to the connection.
     *
     * @param cookies The cookies to apply to the connection.
     * @return The HttpRequest object for further building.
     */
    public HttpRequest applyCookies(Cookie... cookies) {
        StringBuilder cookieStringBuilder = new StringBuilder();
        for (int cookie = 0; cookie < cookies.length; cookie++) {
            cookieStringBuilder.append(cookies[cookie].toString());
            if (cookie != cookies.length - 1) cookieStringBuilder.append("; ");
        }
        getConnection().setRequestProperty("Cookie", cookieStringBuilder.toString());
        return this;
    }
}
