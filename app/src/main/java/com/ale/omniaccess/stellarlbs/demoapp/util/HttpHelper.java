/*
 * Copyright (C) 2018 Alcatel-Lucent Enterprise
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.ale.omniaccess.stellarlbs.demoapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.polestar.helpers.Log;
import com.polestar.naosdk.api.IHttpHelper;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by ASimonigh on 25/10/2016.
 */

public class HttpHelper {

    private static final String TAG = "HttpHelper";

    public static final MediaType FILE = MediaType.parse("application/octet-stream");
    public static final MediaType TXT = MediaType.parse("text/plain; charset=utf-8");

    private final OkHttpClient client;

    public HttpHelper(Context context, boolean https)
    {
        if(https)
            client = getHttpsClient(context);
        else
            client = getClient(context);
    }

    /**
     * Create HttpClient needed for all http requests
     * @param context
     * @return
     */
    private static OkHttpClient getClient(Context context){
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if(context != null){
            //use cache directory if context was set

            //Assigning a CacheDirectory
            File myCacheDir = new File(context.getCacheDir(), "OkHttpCache");
            int cacheSize = 1024 * 1024;

            clientBuilder.cache(new Cache(myCacheDir, cacheSize));
        }
        return clientBuilder.build();
    }

    private static OkHttpClient getHttpsClient(Context context){
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if(context != null){
            //use cache directory if context was set

            //Assigning a CacheDirectory
            File myCacheDir = new File(context.getCacheDir(), "OkHttpCache");
            int cacheSize = 1024 * 1024;

            clientBuilder.cache(new Cache(myCacheDir, cacheSize));
        }

        //create custom trust
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    chain[0].checkValidity();
                } catch (Exception e) {
                    throw new CertificateException("Certificate not valid or trusted.");
                }
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLSocketFactory sslSocketFactory;

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] {trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();

            return clientBuilder.sslSocketFactory(sslSocketFactory, trustManager)
                    //Allow all hostname
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }).build();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();

        }
        return null;
    }

    public Response uploadFile(String src, String dest, String... params){
        File sourceFile = new File(src);
        if(!sourceFile.exists()){
            Log.alwaysError(TAG, "File not found: " + src);
            return null;
        }

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);
        if(params.length % 2 == 0) {
            for (int i = 0; i < params.length; i += 2) {
                //Build the HTML multipart form
                bodyBuilder.addFormDataPart(params[i], params[i + 1]);
            }
        }
        //add file
        bodyBuilder.addFormDataPart("file",sourceFile.getName(), RequestBody.create(FILE, sourceFile ));

        //create request and add body
        Request request = new Request.Builder()
                .url(dest)
                .post(bodyBuilder.build())
                .build();

        try {
            Response response = client.newCall(request).execute();

            return response;

        } catch (IOException e) {
            return null;
        }
    }
    public Response downloadFile(String url, String outputPath){
        return downloadFile(url, outputPath, null);
    }

    public Response downloadFile(String url, String outputPath, String lastModifiedDate){

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url).get();

        if (lastModifiedDate != null && !lastModifiedDate.isEmpty()) {
            requestBuilder.addHeader("If-Modified-Since", lastModifiedDate);
        }

        Request request = requestBuilder.build();

        //synchronous call
        try {
            //execute request
            Response response = client.newCall(request).execute();

            if(response != null && (response.code() >= 200 && response.code() <= 300)){
                File downloadFile = new File(outputPath);
                if(!downloadFile.exists()){
                    downloadFile.createNewFile();
                }

                BufferedSink sink = Okio.buffer(Okio.sink(downloadFile));
                sink.writeAll(response.body().source());
                response.body().close();
                sink.close();

            }
            return response;

        } catch (IOException e) {
            Log.alwaysError(TAG, "Error downloading "  + url + " " + e.getMessage());
            return null;
        }
    }

    public static String postText(String url, String... params){
        String result = "";

        //init builder
        Request.Builder request = new Request.Builder()
                .url(url);

        if(params.length > 1){
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
            bodyBuilder.setType(MultipartBody.FORM);
            if(params.length % 2 == 0) {
                for (int i = 0; i < params.length; i += 2) {
                    String name = params[i];
                    String value = params[i+1];
                    bodyBuilder.addFormDataPart(name,value);
                }
            }

            request.post(bodyBuilder.build());
        } else{
            //add empty string
            request.post(RequestBody.create(TXT, ""));
        }

        try {
            Response response = getHttpsClient(null).newCall(request.build()).execute();
            if(response != null && (response.code() >= 200 && response.code() <= 300)){
                result = response.body().string();
            }

        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public Response get(String url){
        Request request = new Request.Builder()
                .url(url)
                .get().build();

        try {

            Response response = client.newCall(request).execute();

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getText(String url){
        String result = null;
        Response response = get(url);
        if(response != null){
            try {
                ResponseBody lBody = response.body();
                String lBodyString = lBody.string();
                return lBodyString;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            return null;
        }

    }

    public static boolean checkHttpResponse(Response response){
        return response != null && response.code() >= 200 && response.code() <= 300;
    }

    public static boolean isOnline(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    public class HttpHelperProxy extends IHttpHelper {

        HttpHelper m_httpHelper;

        public HttpHelperProxy()
        {
            m_httpHelper = HttpHelper.this;
        }

        @Override
        public int sendFile(String url, String outputPath) {
            Response response = m_httpHelper.uploadFile(url, outputPath);
            if(response == null)
                return 0;
            return response.code();
        }

        @Override
        public int getFile(String url, String outputPath) {
            Response response = m_httpHelper.downloadFile(url, outputPath);
            if(response == null)
                return 0;
            return response.code();
        }

        @Override
        public int getFileIfModified(String url, String outputPath, String modifiedSince) {
            Response response = m_httpHelper.downloadFile(url, outputPath, modifiedSince);
            if(response == null)
                return 0;
            return response.code();
        }

        @Override
        public String getText(String url) {
            return m_httpHelper.getText(url);
        }

        @Override
        public String postText(String url, HashMap<String, String> params) {
            String[] keyValues = new String[2*params.size()];
            int i = 0;
            for (HashMap.Entry<String, String> entry : params.entrySet())
            {
                keyValues[2*i] = entry.getKey();
                keyValues[2*i+1] = entry.getValue();
                i++;
            }

            return HttpHelper.postText(url, keyValues);
        }

        @Override
        public boolean zipAndUploadFiles(ArrayList<String> filesPath, String zipPath, String s3URL, String cloudURL, ArrayList<String> params) {

            try {
                //ZipHelper.zipFiles(filesPath, zipPath);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            String[] paramsTable = new String[params.size()];
            paramsTable = params.toArray(paramsTable);

            // Upload on S3
            Response result = m_httpHelper.uploadFile(zipPath, s3URL, paramsTable);

            String key = params.get(1);
            if(result != null && result.code() >= 200 && result.code() <= 300){
                // notify nao cloud
                int indCharacter = key.indexOf("$");
                String fileName = zipPath.substring(zipPath.lastIndexOf('/') + 1);
                String s3Key = key.substring(0, indCharacter) + fileName;
                String response = HttpHelper.postText(cloudURL, "file", s3Key);
                // delete logs on local storage
                return response.contains("success");
            } else {
                return false;
            }
        }
    }
}
