package com.example.gb.forcemultiplier;

import com.loopj.android.http.*;

public class ForceApiUtil {
        private static final String BASE_URL = "http://192.168.2.32:3000/api/";

        private static AsyncHttpClient client = new AsyncHttpClient();


        public static void setHeader(String header,String value){
            client.addHeader(header,value);
        }

        public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }

        public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.post(getAbsoluteUrl(url), params, responseHandler);
        }

        public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(url, params, responseHandler);
        }

        public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.post(url, params, responseHandler);
        }

        private static String getAbsoluteUrl(String relativeUrl) {
            return BASE_URL + relativeUrl;
        }
}