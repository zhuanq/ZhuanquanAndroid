package cc.circling.web;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebViewClient;
import android.webkit.WebView;

import cc.circling.BaseApplication;
import cc.circling.X5Activity;
import cc.circling.utils.LogUtil;

import java.io.InputStream;


/**
 * Created by army on 2017/3/18.
 */

public class MyWebViewClient extends WebViewClient {
    public static boolean online = false;

    private X5Activity activity;

    public MyWebViewClient(X5Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        return shouldInterceptRequest(uri.toString());
    }
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return shouldInterceptRequest(url);
    }
    private WebResourceResponse shouldInterceptRequest(String url) {
        // 离线包地址拦截本地资源
        if(!online && url.startsWith(URLs.WEB_DOMAIN)) {
            String path = url.substring(URLs.WEB_DOMAIN.length());
            // 忽略掉search
            int i = path.indexOf('?');
            if(i > 0) {
                path = path.substring(0, i);
            }
            // 忽略掉hash
            i = path.indexOf('#');
            if(i > 0) {
                path = path.substring(0, i);
            }
            // 忽略掉开头的/
            if(path.startsWith("/")) {
                path = path.substring(1);
            }
            LogUtil.i("shouldInterceptRequest? " + url + ", " + path);
            if (path.endsWith(".html")
                    || path.endsWith(".htm")
                    || path.endsWith(".css")
                    || path.endsWith(".js")
                    || path.endsWith(".ico")
                    || path.endsWith(".png")
                    || path.endsWith(".gif")
                    || path.endsWith(".jpg")
                    || path.endsWith(".jpeg")) {
                String noSepPath = path.replaceAll("/", "__");
                LogUtil.i("shouldInterceptPath: " + path + ", " + noSepPath);
                WebResourceResponse wrr = null;
                InputStream is = null;
                try {
//                InputStream is = BaseApplication.getContext().getResources().openRawResource(R.raw.cc.circling);
//                InputStream is = BaseApplication.getContext().getAssets().open("cc.circling.html");
                    is = BaseApplication.getContext().openFileInput(noSepPath);
                    if (noSepPath.endsWith(".html")) {
                        wrr = new WebResourceResponse("text/html", "utf-8", is);
                    } else if (noSepPath.endsWith(".htm")) {
                        wrr = new WebResourceResponse("text/html", "utf-8", is);
                    } else if (noSepPath.endsWith(".css")) {
                        wrr = new WebResourceResponse("text/css", "utf-8", is);
                    } else if (noSepPath.endsWith(".js")) {
                        wrr = new WebResourceResponse("application/javascript", "utf-8", is);
                    } else if (noSepPath.endsWith(".png")) {
                        wrr = new WebResourceResponse("image/png", "utf-8", is);
                    } else if (noSepPath.endsWith(".gif")) {
                        wrr = new WebResourceResponse("image/gif", "utf-8", is);
                    } else if (noSepPath.endsWith(".jpg")) {
                        wrr = new WebResourceResponse("image/jpg", "utf-8", is);
                    } else if (noSepPath.endsWith(".jpeg")) {
                        wrr = new WebResourceResponse("image/jpeg", "utf-8", is);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return wrr;
            }
        }
        return null;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        LogUtil.i("onPageStarted: " + url + ", " + activity.getUrl());
    }
    @Override
    public void onPageFinished(WebView view, String args) {
        LogUtil.i("onPageFinished: " + args + ", " + activity.getUrl());
        view.loadUrl("javascript: " + LoadBridge.getBridgeJs());
    }
}