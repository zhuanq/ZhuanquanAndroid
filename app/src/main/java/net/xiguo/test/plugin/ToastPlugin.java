package net.xiguo.test.plugin;

import android.view.Gravity;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import net.xiguo.test.X5Activity;
import net.xiguo.test.utils.LogUtil;

/**
 * Created by army on 2017/4/29.
 */

public class ToastPlugin extends H5Plugin {

    public ToastPlugin(X5Activity activity) {
        super(activity);
    }
    @Override
    public void handle(JSONObject param) {
        String params = param.toJSONString();
        LogUtil.i("ToastPlugin: " + params);
        String s = param.getString("param");
        Toast toast = Toast.makeText(this.activity, s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
