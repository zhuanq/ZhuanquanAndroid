package net.xiguo.test.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import net.xiguo.test.LoginActivity;
import net.xiguo.test.R;
import net.xiguo.test.utils.LogUtil;
import net.xiguo.test.web.MyCookies;
import net.xiguo.test.web.URLs;
import net.xiguo.test.widget.ErrorTip;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by army on 2017/3/26.
 */

public class LoginFragment extends Fragment {
    private EditText userName;
    private EditText userPass;
    private ImageView switchShowPass;
    private TextView forgetPass;
    private boolean showPass;
    private Button login;
    private LoginActivity loginActivity;
    private ErrorTip errorTip;
    private Handler handler;
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginActivity = (LoginActivity) getActivity();
        errorTip = loginActivity.getErrorTip();
        handler = new Handler();

        userName = (EditText) view.findViewById(R.id.userName);
        userPass = (EditText) view.findViewById(R.id.userPass);
        switchShowPass = (ImageView) view.findViewById(R.id.switchShowPass);
        forgetPass = (TextView) view.findViewById(R.id.forgetPass);
        login = (Button) view.findViewById(R.id.login);

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                checkLoginButton();
            }
        });
        userPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                checkLoginButton();
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.showForgetDiv();
            }
        });

        showPass = false;
        switchShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showPass) {
                    userPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    switchShowPass.setImageResource(R.drawable.pass_invisible);
                }
                else {
                    userPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    switchShowPass.setImageResource(R.drawable.pass_visible);
                }
                userPass.setSelection(userPass.getText().toString().length());
                showPass = !showPass;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(loginActivity);
                progressDialog.setMessage("登录中");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i("LOGIN_BY_MOBILE run");
                        try {
                            OkHttpClient client = new OkHttpClient
                                    .Builder()
                                    .cookieJar(new CookieJar() {
                                        @Override
                                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                                            LogUtil.i("saveFromResponse: " + url);
                                            for(Cookie cookie : cookies) {
                                                LogUtil.i("cookie: " + cookie.toString());
                                                MyCookies.add(cookie.toString());
                                                if(cookie.name().equals("JSESSIONID")) {
                                                    LogUtil.i("cookie: ", cookie.value());
                                                    SharedPreferences.Editor editor = loginActivity.getSharedPreferences("cookie", Context.MODE_PRIVATE).edit();
                                                    editor.putString("JSESSIONID", cookie.value());
                                                    editor.apply();
                                                }
                                            }
                                        }

                                        @Override
                                        public List<Cookie> loadForRequest(HttpUrl url) {
                                            return new List<Cookie>() {
                                                @Override
                                                public int size() {
                                                    return 0;
                                                }

                                                @Override
                                                public boolean isEmpty() {
                                                    return false;
                                                }

                                                @Override
                                                public boolean contains(Object o) {
                                                    return false;
                                                }

                                                @NonNull
                                                @Override
                                                public Iterator<Cookie> iterator() {
                                                    return null;
                                                }

                                                @NonNull
                                                @Override
                                                public Object[] toArray() {
                                                    return new Object[0];
                                                }

                                                @NonNull
                                                @Override
                                                public <T> T[] toArray(@NonNull T[] a) {
                                                    return null;
                                                }

                                                @Override
                                                public boolean add(Cookie cookie) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean remove(Object o) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean containsAll(@NonNull Collection<?> c) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean addAll(@NonNull Collection<? extends Cookie> c) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean addAll(int index, @NonNull Collection<? extends Cookie> c) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean removeAll(@NonNull Collection<?> c) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean retainAll(@NonNull Collection<?> c) {
                                                    return false;
                                                }

                                                @Override
                                                public void clear() {

                                                }

                                                @Override
                                                public Cookie get(int index) {
                                                    return null;
                                                }

                                                @Override
                                                public Cookie set(int index, Cookie element) {
                                                    return null;
                                                }

                                                @Override
                                                public void add(int index, Cookie element) {

                                                }

                                                @Override
                                                public Cookie remove(int index) {
                                                    return null;
                                                }

                                                @Override
                                                public int indexOf(Object o) {
                                                    return 0;
                                                }

                                                @Override
                                                public int lastIndexOf(Object o) {
                                                    return 0;
                                                }

                                                @Override
                                                public ListIterator<Cookie> listIterator() {
                                                    return null;
                                                }

                                                @NonNull
                                                @Override
                                                public ListIterator<Cookie> listIterator(int index) {
                                                    return null;
                                                }

                                                @NonNull
                                                @Override
                                                public List<Cookie> subList(int fromIndex, int toIndex) {
                                                    return null;
                                                }
                                            };
                                        }
                                    })
                                    .build();
                            String url = URLs.LOGIN_DOMAIN + URLs.LOGIN_BY_MOBILE
                                    + "?userName=" + android.net.Uri.encode(userName.getText().toString())
                                    + "&password=" + android.net.Uri.encode(userPass.getText().toString());
                            LogUtil.i(url);
                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();
                            Response response = client.newCall(request).execute();
                            String responseBody = response.body().string();
                            LogUtil.i("LOGIN_BY_MOBILE: " + responseBody);
                            if(responseBody.isEmpty()) {
                                loginActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        Toast toast = Toast.makeText(loginActivity, "网络异常请重试", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                });
                                return;
                            }
                            final JSONObject json = JSON.parseObject(responseBody);
                            boolean success = json.getBoolean("success");
                            if(success) {
                                loginActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        progressDialog.dismiss();
                                        Toast toast = Toast.makeText(loginActivity, "登录成功", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        JSONObject data = json.getJSONObject("data");
                                        loginActivity.openUrl(data.getIntValue("regStat"));
                                    }
                                });
                            }
                            else {
                                loginActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        String message = json.getString("message");
                                        if(message == null || message.isEmpty()) {
                                            message = "网络异常请重试";
                                        }
                                        Toast toast = Toast.makeText(loginActivity, message, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            loginActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    Toast toast = Toast.makeText(loginActivity, "网络异常请重试", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    public void clearDelayShowError() {
        if(handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
    }
    private void checkLoginButton() {
        String userNameText = userName.getText().toString();
        String userPassText = userPass.getText().toString();
        // 清除上次可能的延迟校验
        clearDelayShowError();
        boolean valid = true;
        // 空则清除提示信息
        if(userNameText.equals("")) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    errorTip.showNeedUserName();
                }
            };
            handler.postDelayed(runnable, 500);
            valid = false;
        }
        else {
            // 手机格式校验
            Pattern pattern = Pattern.compile("^1[356789]\\d{9}$");
            Matcher matcher = pattern.matcher(userNameText);
            if(!matcher.matches()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        errorTip.showPhoneUnValid();
                    }
                };
                handler.postDelayed(runnable, 500);
                valid = false;
            }
            else {
                errorTip.hide();
            }
        }
        // 用户名如果正确合法，则判断密码输入状态
        if(valid) {
            // 空则清除提示信息
            if(userPassText.equals("")) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        errorTip.showNeedUserPass();
                    }
                };
                handler.postDelayed(runnable, 500);
                valid = false;
            }
            else if(userPassText.length() < 8) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        errorTip.showUserPassTooShort();
                    }
                };
                handler.postDelayed(runnable, 500);
                valid = false;
            }
            else {
                errorTip.hide();
            }
        }
        // 设置按钮禁用状态
        login.setEnabled(valid);
    }
}
