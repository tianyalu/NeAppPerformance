package com.sty.ne.appperformance.net.okhttp;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:06 PM
 */
public class OkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType OCTET_STREAM = MediaType.parse("application/octet-stream; charset=utf-8");

    private static final OkHttp ourInstance = new OkHttp();

    public static OkHttp getInstance() {
        return ourInstance;
    }

    private final OkHttpClient client;

    private OkHttp() {
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .retryOnConnectionFailure(true).build();
    }

    public void reset() {
        client.connectionPool().evictAll();
    }

    private OkHttpClient getClient() {
        return client;
    }

    /**
     * build request
     *
     * @param url
     * @param headers
     * @return
     */
    private Request buildRequest(String url, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeader(builder, headers);
        return builder.build();
    }

    /**
     * build request
     *
     * @param url
     * @return
     */
    private Request buildPostRequest(String url, RequestBody body) {
        return buildPostRequest(url, null, body);
    }

    /**
     * build request
     *
     * @param url
     * @param headers
     * @return
     */
    private Request buildPostRequest(String url, Map<String, String> headers, RequestBody body) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeader(builder, headers);
        builder.post(body);
        return builder.build();
    }

    /**
     * build request
     *
     * @param url
     * @param headers
     * @return
     */
    private Request buildPutRequest(String url, Map<String, String> headers, RequestBody body) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeader(builder, headers);
        builder.put(body);
        return builder.build();
    }

    /**
     * build request
     *
     * @param url
     * @param headers
     * @return
     */
    private Request buildDeleteRequest(String url, Map<String, String> headers, RequestBody body) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeader(builder, headers);
        builder.delete(body);
        return builder.build();
    }


    /**
     * add request header
     *
     * @param builder
     * @param headers
     */
    private void addHeader(Request.Builder builder, Map<String, String> headers) {
        if (headers == null) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * method == GET
     *
     * @param url
     * @param callback
     */
    public void httpGet(String url, Callback callback) {
        httpGet(url, null, callback);
    }

    /**
     * method == GET
     *
     * @param url
     * @param headers
     * @param callback
     */
    public void httpGet(String url, Map<String, String> headers, Callback callback) {
        getClient().newCall(buildRequest(url, headers)).enqueue(callback);
    }


    /**
     * method == post
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Response httpPost(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        return getClient().newCall(buildPostRequest(url, body)).execute();
    }

    /**
     * method == post
     *
     * @param url
     * @return
     * @throws IOException
     */
    public void httpPost(String url, String jsonBody, Map<String, String> header, Callback callback) {
        RequestBody body = RequestBody.create(JSON, jsonBody);
        getClient().newCall(buildPostRequest(url, header, body)).enqueue(callback);
    }

    /**
     * method == post / form
     *
     * @param url
     * @return
     * @throws IOException
     */
    public void httpPost(String url, Map<String, String> form, Map<String, String> header, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        if (form != null) {
            for (String key : form.keySet()) {
                String value = form.get(key);
                if (value != null) {
                    builder.add(key, value);
                }
            }
        }
        RequestBody body = builder.build();
        getClient().newCall(buildPostRequest(url, header, body)).enqueue(callback);
    }

    /**
     * method == put / form
     *
     * @param url
     * @return
     * @throws IOException
     */
    public void httpPut(String url, Map<String, String> form, Map<String, String> header, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        if (form != null) {
            for (String key : form.keySet()) {
                builder.add(key, form.get(key));
            }
        }
        RequestBody body = builder.build();
        getClient().newCall(buildPutRequest(url, header, body)).enqueue(callback);
    }

    /**
     * method == delete / form
     *
     * @param url
     * @return
     * @throws IOException
     */
    public void httpDelete(String url, Map<String, String> form, Map<String, String> header, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        if (form != null) {
            for (String key : form.keySet()) {
                builder.add(key, form.get(key));
            }
        }
        RequestBody body = builder.build();
        getClient().newCall(buildDeleteRequest(url, header, body)).enqueue(callback);
    }

    /**
     * method == post / Multi
     *
     * @param url
     * @param body
     * @param callback
     */
    public void httpMulti(String url, RequestBody body, Map<String, String> header, Callback callback) {
        getClient().newCall(buildPostRequest(url, header, body)).enqueue(callback);
    }


    /**
     * method == post
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Response httpPost(String url, byte[] bytes) throws IOException {
        RequestBody body = RequestBody.create(OCTET_STREAM, bytes);
        return getClient().newCall(buildPostRequest(url, body)).execute();
    }

    /**
     * method == post
     *
     * @param url
     * @return
     * @throws IOException
     */
    public void httpPost(String url, byte[] bytes, Callback callback) {
        RequestBody body = RequestBody.create(OCTET_STREAM, bytes);
        getClient().newCall(buildPostRequest(url, body)).enqueue(callback);
    }
}
