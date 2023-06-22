package com.github.catvod.spider;

import static java.lang.System.in;
import static java.lang.System.out;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OKCallBack;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkHttpUtil;
import com.github.catvod.ui.ScrollTextView;
import com.github.catvod.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Notice extends Spider {

    private static final String SPACE = "                                        ";
    private ScrollTextView view;

    @Override
    public void init(Context context, String extend) {
        super.init(context, extend);
        String[] splits = extend.split(";");
        String text = splits[0];
        int duration = splits.length > 1 ? Integer.parseInt(splits[1]) : 30;
        Init.run(() -> createView(text, duration));
    }

    private void createView(String text, int duration) {
        createText(text, duration);
        createLayout();
        updateColor();
        hide();
    }

    private void createLayout() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        Utils.addView(view, params);
    }

    private void createText(String text, int duration) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2; i++) sb.append(SPACE).append(text);
        view = new ScrollTextView(Init.context());
        view.setTextSize(20);
        view.setDuration(duration);
        view.setText(sb.toString());
        view.setTypeface(null, Typeface.BOLD);
        view.setPadding(0, Utils.dp2px(10), 0, Utils.dp2px(10));
        view.setBackgroundColor(Color.argb(200, 255, 255, 255));
  //      view.setBackgroundColor( Color.TRANSPARENT);
        view.startScroll();
    }

    private void hide() {
        Init.run(() -> Utils.removeView(view), 30 * 1000);
    }

    private void updateColor() {
        Init.run(runnable, 500);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Random random = new Random();
            view.setTextColor(Color.argb(255, random.nextInt(128), random.nextInt(128), random.nextInt(128)));
            updateColor();
        }
    };

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }    //将所有验证的结果都设为true
    };


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public static String GetResult(String siteurl) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(siteurl);
            HttpURLConnection httpconn;
            HttpsURLConnection httpsconn;
            httpsconn = (HttpsURLConnection) url.openConnection();

            if (url.getProtocol().toLowerCase().equals("https")) {
                httpsconn.setHostnameVerifier(DO_NOT_VERIFY);
                httpconn = httpsconn;
            } else {	//判断是https请求还是http请求
                httpconn = (HttpURLConnection) url.openConnection();
            }

            httpconn.setRequestMethod("GET");
            httpconn.setDoOutput(false);
            httpconn.setDoInput(true);
            httpconn.setConnectTimeout(30000);
            httpconn.setReadTimeout(30000);
            httpconn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");
            httpconn.connect();
            in = new BufferedReader(new InputStreamReader(httpconn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        String Toresult = ascii2native(result.toString());
   //     out.println(Toresult);
        return Toresult;
    }

    //将返回的ASCII码转为汉字（若有）
    public static String ascii2native(String asciicode) {
        String[] asciis = asciicode.split("\\\\u");
        String nativeValue = asciis[0];
        try {
            for (int i = 1; i < asciis.length; i++) {
                String code = asciis[i];
                nativeValue += (char) Integer.parseInt(code.substring(0, 4), 16);
                if (code.length() > 4) {
                    nativeValue += code.substring(4, code.length());
                }
            }
        } catch (NumberFormatException e) {
            return asciicode;
        }
        return nativeValue;
    }


}










