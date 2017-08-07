package net.xiguo.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebSettings;
//import com.tencent.smtt.sdk.WebView;
import net.xiguo.test.login.UserInfo;
import net.xiguo.test.login.oauth.Constants;
import net.xiguo.test.plugin.LoginWeiboPlugin;
import net.xiguo.test.plugin.GetPreferencePlugin;
import net.xiguo.test.plugin.SetPreferencePlugin;
import net.xiguo.test.plugin.SwipeRefreshPlugin;
import net.xiguo.test.web.WebView;

import net.xiguo.test.event.H5EventDispatcher;
import net.xiguo.test.plugin.AlertPlugin;
import net.xiguo.test.plugin.BackPlugin;
import net.xiguo.test.plugin.ConfirmPlugin;
import net.xiguo.test.plugin.H5Plugin;
import net.xiguo.test.plugin.HideBackButtonPlugin;
import net.xiguo.test.plugin.HideLoadingPlugin;
import net.xiguo.test.plugin.PopWindowPlugin;
import net.xiguo.test.plugin.PushWindowPlugin;
import net.xiguo.test.plugin.SetTitlePlugin;
import net.xiguo.test.plugin.ShowBackButtonPlugin;
import net.xiguo.test.plugin.ShowLoadingPlugin;
import net.xiguo.test.plugin.ToastPlugin;
import net.xiguo.test.plugin.UserInfoPlugin;
import net.xiguo.test.utils.LogUtil;
import net.xiguo.test.web.MyCookies;
import net.xiguo.test.web.MyWebChromeClient;
import net.xiguo.test.web.MyWebViewClient;
import net.xiguo.test.web.URLs;
import net.xiguo.test.web.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by army on 2017/3/16.
 */

public class X5Activity extends AppCompatActivity {

    private SetTitlePlugin setTitlePlugin;
    private PushWindowPlugin pushWindowPlugin;
    private PopWindowPlugin popWindowPlugin;
    private BackPlugin backPlugin;
    private ToastPlugin toastPlugin;
    private ShowLoadingPlugin showLoadingPlugin;
    private HideLoadingPlugin hideLoadingPlugin;
    private AlertPlugin alertPlugin;
    private ConfirmPlugin confirmPlugin;
    private HideBackButtonPlugin hideBackButtonPlugin;
    private ShowBackButtonPlugin showBackButtonPlugin;
    private UserInfoPlugin userInfoPlugin;
    private SwipeRefreshPlugin swipeRefreshPlugin;
    private LoginWeiboPlugin loginWeiboPlugin;
    private GetPreferencePlugin getPreferencePlugin;
    private SetPreferencePlugin setPreferencePlugin;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView back;
    private WebView webView;
    private String url;

    private boolean firstWeb;
    private boolean firstRun;

    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setFormat(PixelFormat.TRANSLUCENT);

        Intent intent = getIntent();
        // 标题栏是否为透明或者自定义几号标题栏
        int titleBar = intent.getIntExtra("titleBar", 0);
        LogUtil.i("titleBar", titleBar + "");
        switch(titleBar) {
            case 1:
                setContentView(R.layout.activity_x5);
                break;
            default:
                setContentView(R.layout.activity_x5_transparent);
                break;
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        webView = (WebView) findViewById(R.id.x5);
        back = (ImageView) findViewById(R.id.back);

        // webview背景色
        String backgroundColor = intent.getStringExtra("backgroundColor");
        LogUtil.i("backgroundColor", backgroundColor);
        if(backgroundColor != null && backgroundColor.length() > 0) {
            int color = Color.parseColor(backgroundColor);
            LogUtil.i("backgroundColor", color + "");
            webView.setBackgroundColor(color);
        }

        // 是否显示back键
        boolean showBack = intent.getBooleanExtra("showBack", false);
        LogUtil.i("showBack", showBack + "");
        if(showBack) {
            back.setVisibility(View.VISIBLE);
        }
        else {
            back.setVisibility(View.GONE);
        }

        initPlugins();
        firstRun = true;

        WebSettings webSettings = webView.getSettings();
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua + " app/ZhuanQuan");
        webSettings.setJavaScriptEnabled(true);
        // 支持缩放viewport
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        IX5WebViewExtension ix5 = webView.getX5WebViewExtension();
        webView.setDrawingCacheEnabled(true);

        webView.setSwipeRefreshLayout(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtil.i("swipeRefreshLayout onRefresh");
                webView.reload();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // 关闭小窗播放和方块滚动条
        Bundle data = new Bundle();
        data.putBoolean("supportLiteWnd", false);
        if(null != ix5) {
            ix5.invokeMiscMethod("setVideoParams", data);
            ix5.setScrollBarFadingEnabled(false);
        }

        MyWebViewClient webViewClient = new MyWebViewClient(this);
        webView.setWebViewClient(webViewClient);
        MyWebChromeClient webChromeClient = new MyWebChromeClient(this);
        webView.setWebChromeClient(webChromeClient);

        // 从上个启动活动获取需要加载的url
        url = intent.getStringExtra("url");
        LogUtil.i("url: " + url);
        // 是否第一个web
        firstWeb = intent.getBooleanExtra("firstWeb", false);
        LogUtil.i("firstWeb: " + firstWeb);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i("click back");
                webView.loadUrl("javascript: ZhuanQuanJSBridge.trigger('back');");
            }
        });

        // 离线包地址添加cookie
        if(url.startsWith(URLs.H5_DOMAIN)) {
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();

            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                LogUtil.i("CookieSyncManager sync");
                CookieSyncManager.getInstance().sync();
            } else {
                LogUtil.i("CookieManager flush");
                CookieManager.getInstance().flush();
            }

            for (String s : MyCookies.getAll()) {
                LogUtil.i("CookieManager: ", s);
                cookieManager.setCookie(URLs.H5_DOMAIN, s);
                cookieManager.setCookie(URLs.WEB_DOMAIN, s);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                LogUtil.i("CookieSyncManager sync");
                CookieSyncManager.getInstance().sync();
            } else {
                LogUtil.i("CookieManager flush");
                CookieManager.getInstance().flush();
            }
        }
        webView.loadUrl(url);
    }

    private void initPlugins() {
//        setTitlePlugin = new SetTitlePlugin(this);
//        H5EventDispatcher.addEventListener(H5Plugin.SET_TITLE, setTitlePlugin);
        pushWindowPlugin = new PushWindowPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.PUSH_WINDOW, pushWindowPlugin);

        popWindowPlugin = new PopWindowPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.POP_WINDOW, popWindowPlugin);

        backPlugin = new BackPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.BACK, backPlugin);

        toastPlugin = new ToastPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.TOAST, toastPlugin);

        showLoadingPlugin = new ShowLoadingPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.SHOW_LOADING, showLoadingPlugin);

        hideLoadingPlugin = new HideLoadingPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.HIDE_LOADING, hideLoadingPlugin);

        alertPlugin = new AlertPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.ALERT, alertPlugin);

        confirmPlugin = new ConfirmPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.CONFIRM, confirmPlugin);

        hideBackButtonPlugin = new HideBackButtonPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.HIDE_BACKBUTTON, hideBackButtonPlugin);

        showBackButtonPlugin = new ShowBackButtonPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.SHOW_BACKBUTTON, showBackButtonPlugin);

        userInfoPlugin = new UserInfoPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.USER_INFO, userInfoPlugin);

        swipeRefreshPlugin = new SwipeRefreshPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.SWIPE_REFRESH, swipeRefreshPlugin);

        loginWeiboPlugin = new LoginWeiboPlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.LOGIN_WEIBO, loginWeiboPlugin);

        getPreferencePlugin = new GetPreferencePlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.GET_PRE_FERENCE, getPreferencePlugin);

        setPreferencePlugin = new SetPreferencePlugin(this);
        H5EventDispatcher.addEventListener(H5Plugin.SET_PRE_FERENCE, setPreferencePlugin);
    }

    public void setTitle(String title) {
        LogUtil.i("setTitle: " + title);
        TextView tv = (TextView) findViewById(R.id.webViewTitle);
        // 有可能没有titleBar
        if(tv != null) {
            tv.setText(title);
        }
    }
    public void pushWindow(String url, JSONObject params) {
        LogUtil.i("pushWindow: " + url + "," + params.toJSONString());
        Intent intent = new Intent(X5Activity.this, X5Activity.class);
        intent.putExtra("url", url);
        intent.putExtra("backgroundColor", params.getString("backgroundColor"));
        intent.putExtra("titleBar", params.getIntValue("titleBar"));
        intent.putExtra("showBack", params.getBooleanValue("showBack"));
        startActivityForResult(intent, 1);
    }
    public boolean isFirstWeb() {
        return firstWeb;
    }
    public WebView getWebView() {
        return webView;
    }
    public void hideBackButton() {
        back.setVisibility(View.GONE);
    }
    public void showBackButton() {
        back.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        LogUtil.i("keyup: " + keyCode);
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            LogUtil.i("KEYCODE_BACK");
            webView.loadUrl("javascript: ZhuanQuanJSBridge.trigger('back');");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i("onActivityResult: " + requestCode + ", " + resultCode + ", " + data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            mSsoHandler = null;
        }
        else {
            switch (requestCode) {
                case 1:
                    if (resultCode == RESULT_OK) {
                        String params = data.getStringExtra("params");
                        LogUtil.i("onActivityResult: " + params);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.i("onStart: ", url);
        if(!firstRun) {
            webView.onResume();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("javascript: ZhuanQuanJSBridge.trigger('resume');");
        }
        else {
            firstRun = false;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        webView.onPause();
        webView.getSettings().setJavaScriptEnabled(false);
        LogUtil.i("onStop: ", url);
        webView.loadUrl("javascript: ZhuanQuanJSBridge.trigger('pause');");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        webView.clearHistory();
        ((ViewGroup) webView.getParent()).removeView(webView);
        webView.destroy();
        webView = null;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public void loginWeibo() {
        LogUtil.i("loginWeibo");
        WbSdk.install(this, new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE));
        mSsoHandler = new SsoHandler(this);
        mSsoHandler.authorize(new SelfWbAuthListener());
    }

    private class SelfWbAuthListener implements WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken mAccessToken) {
//            progressDialog.dismiss();
            LogUtil.i("SelfWbAuthListener onSuccess");
            final String openId = mAccessToken.getUid();
            final String token = mAccessToken.getToken();
            loginWeiboPlugin.success(openId, token);
        }

        @Override
        public void cancel() {
//            progressDialog.dismiss();
            LogUtil.i("SelfWbAuthListener cancel");
            loginWeiboPlugin.cancel();
//            Toast toast = Toast.makeText(LoginActivity.this, "取消授权", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
//            progressDialog.dismiss();
            LogUtil.i("SelfWbAuthListener onFailure", errorMessage.getErrorMessage());
            loginWeiboPlugin.failure(errorMessage.getErrorMessage());
//            Toast toast = Toast.makeText(LoginActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
        }
    }
}
