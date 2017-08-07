package net.xiguo.test.plugin;

import com.alibaba.fastjson.JSONObject;

import net.xiguo.test.X5Activity;
import net.xiguo.test.utils.LogUtil;

/**
 * Created by army on 2017/3/27.
 */

public class PushWindowPlugin extends H5Plugin {

    public PushWindowPlugin(X5Activity activity) {
        super(activity);
    }

    @Override
    public void handle(JSONObject data) {
        JSONObject param = data.getJSONObject("param");
        String url = param.getString("url");
        JSONObject params = param.getJSONObject("params");
        LogUtil.i("PushWindowPlugin: " + url + "," + params.toJSONString());
        this.activity.pushWindow(url, params);
    }
}
