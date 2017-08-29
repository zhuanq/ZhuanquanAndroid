package net.xiguo.test.plugin;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONObject;

import net.xiguo.test.BaseApplication;
import net.xiguo.test.X5Activity;
import net.xiguo.test.utils.LogUtil;
import net.xiguo.test.web.MyCookies;

/**
 * Created by army8735 on 2017/8/29.
 */

public class SetCookiePlugin extends H5Plugin {

    public SetCookiePlugin(X5Activity activity) {
        super(activity);
    }
    @Override
    public void handle(JSONObject data) {
        LogUtil.i("SetCookiePlugin: " + data.toJSONString());
        String clientId = data.getString("clientId");
        JSONObject param = data.getJSONObject("param");
        if(param != null) {
            String key = param.getString("key");
            String value = param.getString("value");
            SharedPreferences.Editor editor = BaseApplication.getContext().getSharedPreferences("cookie", Context.MODE_PRIVATE).edit();
            LogUtil.i("SetCookiePlugin isNull: " + (value == null));
            if(value == null) {
                editor.remove(key);
            }
            else {
                editor.putString(key, value);
                MyCookies.add(key, value);
            }
            editor.apply();
            activity.getWebView().loadUrl("javascript: ZhuanQuanJSBridge._invokeJS('" + clientId + "',true);");
        }
    }
}
