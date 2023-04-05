package com.lb.chatgpt;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import ren.yale.android.cachewebviewlib.CacheType;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;


public class ChatGPTWebViewActivity extends FragmentActivity {
    public static final String TITLE = "title";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_protocal);
        TextView titleTv = findViewById(R.id.title_text);
        Intent intent = getIntent();
        if (intent != null){
            String title = intent.getStringExtra(TITLE);
            if (!TextUtils.isEmpty(title)){
                titleTv.setText(title);
            }
        }

        String url = getIntent().getStringExtra("url");
        //获得控件
        WebView webView = (WebView) findViewById(R.id.wv_webview);
        setWebView(webView);
        //访问网页
        webView.loadUrl("https://chat.openai.com/chat");
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }

        });

        clickToBack();
    }

    private void clickToBack() {
        findViewById(R.id.icon_back).setOnClickListener(v -> finish());
    }

    private void setWebView(WebView webView) {
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setDomStorageEnabled(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT); // 默认使用缓存
        webSetting.setAppCacheMaxSize(100 * 1024 * 1024); //缓存最多可以有8M
        String path = getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
//设置缓存路径
        webSetting.setAppCachePath(path);

        //开启缓存
        webSetting.setAppCacheEnabled(true);

        webSetting.setAllowFileAccess(true); // 可以读取文件缓存(manifest生效)
        webSetting.setAllowFileAccessFromFileURLs(true);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDefaultTextEncodingName("gb2312");
        webView.getSettings().setSupportMultipleWindows(false);

        String cacheDirPath = getFilesDir().getAbsolutePath()+"cache/";
// 设置缓存路径
        webSetting.setDatabasePath(cacheDirPath);
        webSetting.setDatabaseEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(0);
        }

        WebViewCacheInterceptor.Builder builder =  new WebViewCacheInterceptor.Builder(this);

        builder.setCachePath(new File(this.getCacheDir(),"cache_path_name"))//设置缓存路径，默认getCacheDir，名称CacheWebViewCache
                .setDynamicCachePath(new File(this.getCacheDir(),"dynamic_webview_cache"))
                .setCacheSize(1024*1024*100)//设置缓存大小，默认100M
                .setConnectTimeoutSecond(20)//设置http请求链接超时，默认20秒
                .setReadTimeoutSecond(20)//设置http请求链接读取超时，默认20秒
                .setCacheType(CacheType.NORMAL);//设置缓存为正常模式，默认模式为强制缓存静态资源
        builder.setAssetsDir("static");
        WebViewCacheInterceptorInst.getInstance().init(builder);
        CacheExtensionConfig extension = new CacheExtensionConfig();
        extension.addExtension("json").removeExtension("swf");//添加删除缓存后缀
        builder.setCacheExtensionConfig(extension);

        WebViewCacheInterceptorInst.getInstance().init(builder);


        webView.setWebViewClient(new WebViewClient(){


            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                WebViewCacheInterceptorInst.getInstance().loadUrl(webView,request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebViewCacheInterceptorInst.getInstance().loadUrl(webView,url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return  WebViewCacheInterceptorInst.getInstance().interceptRequest(request);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return  WebViewCacheInterceptorInst.getInstance().interceptRequest(url);
            }
        });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
