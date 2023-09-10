package com.github.catvod.spider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.github.catvod.BuildConfig;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;

import java.io.File;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;



public class Smb4nas extends Spider {
    //    static {
//        Security.addProvider(new BouncyCastleProvider());
//    }
//static {
//    Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
//}
    private static final Map<String, CIFSContext> CIFS_CONTEXT_CACHE = new HashMap<>();

    private static final String DIR_PIC = "https://img.tukuppt.com/png_preview/00/42/50/3ySGW7mvyY.jpg!/fw/260";
    private static final String FILE_PIC = "https://img.tukuppt.com/png_preview/00/18/23/GBmBU6fHo7.jpg!/fw/260";

    public static NtlmPasswordAuthenticator auth = null;
    public static CIFSContext cifsContext = null;

    public Smb4nas() {
    }

    static {
        Properties properties = new Properties();
        properties.putAll(System.getProperties());
        properties.put("jcifs.smb.client.responseTimeout", "15000");
        properties.put("jcifs.smb.client.rcv_buf_size", "262144");
        properties.put("jcifs.smb.client.enableSMB2", "true");
        properties.put("jcifs.smb.client.maxVersion", "SMB311");
        properties.put("jcifs.smb.client.minVersion", "SMB202");
        // note that connectivity with smbV1 will not be working
        properties.put("jcifs.smb.client.useSMB2Negotiation", "true");
        // disable dfs makes win10 shares with ms account work
        properties.put("jcifs.smb.client.dfs.disabled", "true");

        // get around https://github.com/AgNO3/jcifs-ng/issues/40 and this is required for guest login on win10 smb2
        properties.put("jcifs.smb.client.ipcSigningEnforced", "false");

        // allow plaintext password fallback
        properties.put("jcifs.smb.client.disablePlainTextPasswords", "false");
        // resolve in this order to avoid netbios name being also a foreign DNS entry resulting in bad resolution
        // BCAST,DNS order makes WD devices happy but results in wrong IP decision for some https://github.com/AgNO3/jcifs-ng/issues/258
        properties.put("jcifs.resolveOrder", "BCAST,DNS");
        properties.put("jcifs.traceResources", "true");

        try {
            SingletonContext.init(properties);
        } catch (CIFSException e) {
            e.printStackTrace();
        }
        SingletonContext.registerSmbURLHandler();
    }
    public static void getContext(String url) {
        URI uri = URI.create(url);//Administrator

        String userInfo = uri.getUserInfo();

        if (userInfo != null) {
            try {
                auth = new DumbNtlmPasswordAuthenticator(userInfo);
            } catch (Exception ex) {
                ex.printStackTrace();
                SpiderDebug.log(ex);
            }
            cifsContext = CIFS_CONTEXT_CACHE.get(userInfo);
        }
        if (cifsContext == null) {

            SingletonContext baseContext = SingletonContext.getInstance();


            if (auth != null) {
                cifsContext = baseContext.withCredentials(auth);
            } else {
                cifsContext = baseContext.withGuestCrendentials();
            }
            CIFS_CONTEXT_CACHE.put(userInfo, cifsContext);
        }

    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> hashMap) {


        JSONArray videos = new JSONArray();
        String smbUrl = tid;
        if (tid != null && tid.startsWith("http://")) {
            smbUrl = tid.replace("http://", "smb://");

        }
        if (smbUrl.startsWith("smb://")) {
            try {
                smbUrl = URLDecoder.decode(smbUrl, "UTF-8");
                getContext(smbUrl);

                SmbFile file = new SmbFile(smbUrl, cifsContext);
                file.setReadTimeout(5000);
                file.setConnectTimeout(5000);
                if (file.canRead() && file.exists()) {
                    if (file.isDirectory()) {

                        //   videos.put(addSmbFile(file, "上一级:" + file.getName()));
                        try {
                            SmbFile[] list = file.listFiles();
                            for (SmbFile sf : list) {

                                videos.put(addSmbFile(sf, sf.getName()));
                            }
                        } catch (SmbException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (file.isFile()) {
                        videos.put(addSmbFile(file, file.getName()));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                SpiderDebug.log(ex);
            }

        } else {
            File file = new File(tid);
            File[] list = file.listFiles();

            // videos.put(addFile(file, "上一级:" + file.getName()));
            for (File f : list) {
//                String filename = f.getName();
//                if (filename.indexOf('.') == 0) {
//                    continue;
//                }
                videos.put(addFile(f));
            }
        }
        try {
            JSONObject result = new JSONObject();
            result.put("page", 1);
            result.put("pagecount", 1);
            result.put("limit", videos.length());
            result.put("total", videos.length());
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            Log.e("LocalFile", "category load error", e);
            e.printStackTrace();
        }
        return "";
    }

    public JSONObject addFile(File file) {
        JSONObject up = new JSONObject();
        try {
            up.put("vod_id", file.getAbsolutePath());
            up.put("vod_name", file.getName());
            up.put("vod_pic", file.isDirectory() ? DIR_PIC : FILE_PIC);
            // 当vod_tag为folder时会点击该item会把当前vod_id当成新的类型ID重新进
            up.put("vod_tag", file.isDirectory() ? "folder" : "file");
            //fileTime(file.lastModified(), "yyyy/MM/dd aHH:mm:ss")
            up.put("vod_remarks", "");
        } catch (Exception ex) {
        }
        return up;

    }


    public JSONObject addSmbFile(SmbFile file, String name) {
        JSONObject up = new JSONObject();

        try {
            up.put("vod_id", file.getParent() + URLEncoder.encode(name, "UTF-8"));
            up.put("vod_name", name);
            up.put("vod_pic", file.isDirectory() ? DIR_PIC : FILE_PIC);
            // 当vod_tag为folder时会点击该item会把当前vod_id当成新的类型ID重新进
            up.put("vod_tag", file.isDirectory() ? "folder" : "file");
            up.put("vod_remarks","");// fileTime(file.lastModified(), "yyyy/MM/dd aHH:mm:ss")
        } catch (Exception ex) {
        }
        return up;
    }

    @Override
    public String detailContent(List<String> ids) {
        try {
            JSONObject vod = new JSONObject();
            String filename = ids.get(0);


            File f = new File(filename);
            String name = URLDecoder.decode(f.getName(), "UTF-8");
            vod.put("vod_id", filename);
            vod.put("vod_name", name);
            vod.put("vod_pic", "");
            vod.put("type_name", "");
            if (name.toLowerCase().endsWith(".apk")) {
                vod.put("vod_play_from", "安装");
            } else {
                vod.put("vod_play_from", "播放");
            }
            vod.put("vod_play_url", name + "$" + filename);
            vod.put("vod_content", URLDecoder.decode(filename, "UTF-8"));

            JSONObject result = new JSONObject();
            JSONArray list = new JSONArray();
            list.put(vod);

            result.put("list", list);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }


    String fileTime(long time, String fmt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    @Override
    public String homeContent(boolean filter) {
        try {
            JSONArray classes = new JSONArray();

            if (!TextUtils.isEmpty(siteUrl)) {
                JSONObject newCls = new JSONObject();
                newCls.put("type_id", siteUrl);
                newCls.put("type_name", "SMB共享文件");
                newCls.put("type_flag", "1");
                classes.put(newCls);
            }
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (root != null) {
                JSONObject newCls = new JSONObject();
                newCls.put("type_id", root);
                newCls.put("type_name", "本地文件");
                // type_flag 是扩展字段，目前可取值0、1、2，该字段不存在时表示正常模式
                newCls.put("type_flag", "1"); // 1、列表形式的文件夹 2、缩略图 0或者不存在表示正常模式

                classes.put(newCls);
            }

            JSONObject result = new JSONObject();
            result.put("class", classes);
            if (filter) {
                result.put("filters", new JSONObject("{}"));
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //smb://tv:123456@192.168.3.3
    private String siteUrl = null;

    @Override
    public void init(Context context, String extend) {
        if (!TextUtils.isEmpty(extend)) {
            Log.d("LocalFile", "url:" + extend);
            this.siteUrl = extend;
        }
//      try {
//          Class.forName("fi.iki.elonen.NanoHTTPD");
//          Init.show("Yes NanoHTTPD!");
//      }catch (Exception ex){
//          Init.show("No NanoHTTPD!");
//      }


    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {

            if(id.startsWith("smb://")) {
                JSONObject result = new JSONObject();
                result.put("parse", "0");
                result.put("playUrl", "");
                result.put("url", "http://127.0.0.1:9977/smbproxy?url=" + id);
                return result.toString();
            }else {


                openFile(chmod(new File(id)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private static Uri getShareUri(File file) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? Uri.fromFile(file) : androidx.core.content.FileProvider.getUriForFile(Init.context(), Init.context().getPackageName() + ".provider", file);
    }

    public static void openFile(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(getShareUri(file), getMimeType(file.getName()));
            Init.context().startActivity(intent);
        }catch (Exception ex){
            SpiderDebug.log(ex);
        }
    }
    private static String getMimeType(String fileName) {
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        return TextUtils.isEmpty(mimeType) ? "*/*" : mimeType;
    }

    public static File chmod(File file) {
        try {
            Process process = Runtime.getRuntime().exec("chmod 777 " + file);
            process.waitFor();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return file;
        }
    }

    public static void installAPK(Context mContext, File apkFile) {
        try {
            if (!apkFile.exists()) {
                return;
            }
            //Android 7.0及以上
            if (Build.VERSION.SDK_INT >= 24) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    boolean hasInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
                    if (!hasInstallPermission) {
                        //请求安装未知应用来源的权限
                       // androidx.core.app.ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 6666);
                        Uri packageURI =  Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                        //注意这个是8.0新API
                        Intent intent =new  Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);

//                        Init.getActivity().startActivityForResult(intent,10086);
//                        Init.getActivity().onActivityResult();
                    }
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String packageName = mContext.getApplicationContext().getPackageName();
                String authority = new StringBuilder(packageName).append(".fileprovider").toString();
                Uri apkUri = androidx.core.content.FileProvider.getUriForFile(mContext, authority, apkFile);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                mContext.startActivity(intent);
            } else {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
          //  android.os.Process.killProcess(android.os.Process.myPid());//安装完之后会提示”完成” “打开”。

        }
    }

    @Override
    public String searchContent(String keyword, boolean quick) {
        try {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static class DumbNtlmPasswordAuthenticator extends NtlmPasswordAuthenticator {
        public DumbNtlmPasswordAuthenticator(String userInfo) {
            // Call a protected constructor in NtlmPasswordAuthenticator because the exposed
            // one tries to be "too" smart and parse a domain from the username.
            // This ends up breaking functionality for usernames with `@` in them.
            super(userInfo, null, null, null, null);
        }
    }


}