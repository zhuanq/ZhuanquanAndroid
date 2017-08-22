package net.xiguo.test.plugin;

import android.content.Intent;
import android.net.Uri;

import com.alibaba.fastjson.JSONObject;

import net.xiguo.test.X5Activity;
import net.xiguo.test.utils.LogUtil;

/**
 * Created by army8735 on 2017/8/22.
 */

public class OpenUriPlugin extends H5Plugin {

    public OpenUriPlugin(X5Activity activity) {
        super(activity);
    }

    @Override
    public void handle(JSONObject data) {
        LogUtil.i("AlertPlugin: " + data.toJSONString());
        String param = data.getString("param");
        if(param != null && param.length() > 0) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(param);
            intent.setData(uri);
            activity.startActivity(intent);
        }
    }
}