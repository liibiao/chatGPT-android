package com.lb.chatgpt;

import android.app.Application;

import ren.yale.android.cachewebviewlib.WebViewCacheInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;

public class ChatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WebViewCacheInterceptorInst.getInstance().
                init(new WebViewCacheInterceptor.Builder(this));
    }
}
