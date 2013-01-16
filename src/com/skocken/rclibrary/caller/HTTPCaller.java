/**
 * 
 */
package com.skocken.rclibrary.caller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.os.Build;
import android.util.Log;

import com.skocken.rclibrary.RCPreference;

/**
 * @author Stan Kocken (stan.kocken@gmail.com)
 * 
 */
public class HTTPCaller {
    /** Used locally to tag Logs */
    private static final String TAG = HTTPCaller.class.getSimpleName();

    public static String loadFromUrl(String url) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                HttpParams httpParameters = new BasicHttpParams();
                HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
                HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                // Set verifier
                HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
                SchemeRegistry registry = new SchemeRegistry();
                SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
                socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                registry.register(new Scheme("https", socketFactory, 443));
                httpclient.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);

                HttpGet httpUriRequest = new HttpGet(url);
                HttpResponse httpResponse = httpclient.execute(httpUriRequest);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    InputStream ips = httpResponse.getEntity().getContent();
                    BufferedReader buf = new BufferedReader(new InputStreamReader(ips, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String s;
                    while (true) {
                        s = buf.readLine();
                        if (s == null || s.length() == 0) {
                            break;
                        }
                        sb.append(s);
                    }
                    buf.close();
                    ips.close();
                    s = sb.toString();
                    return s;
                }
            } else {
                try {
                    // do this wherever you are wanting to POST
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    // once you set the output to true, you don't really need to set the request method to post, but I'm doing it anyway
                    conn.setRequestMethod("GET");

                    // build the string to store the response text from the server
                    StringBuilder sb = new StringBuilder();

                    // start listening to the stream
                    Scanner inStream = new Scanner(conn.getInputStream());

                    // process the stream and store it in StringBuilder
                    while (inStream.hasNextLine()) {
                        sb.append(inStream.nextLine());
                    }
                    return sb.toString();

                } catch (MalformedURLException ex) {
                    if (RCPreference.sDebugMode) {
                        Log.v(TAG, "RCLibrary : MalformedURLException", ex);
                    }
                } catch (IOException ex) {
                    if (RCPreference.sDebugMode) {
                        Log.v(TAG, "RCLibrary : IOException", ex);
                    }
                }
            }
        } catch (Exception ex) {
            if (RCPreference.sDebugMode) {
                Log.v(TAG, "RCLibrary : Exception", ex);
            }
        }
        return null;
    }
}
