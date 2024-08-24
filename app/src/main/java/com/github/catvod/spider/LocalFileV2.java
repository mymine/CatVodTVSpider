package com.github.catvod.spider;

import android.content.Context;
import android.os.Environment;

import com.github.catvod.crawler.Spider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * 基于原来的 LocalFile.java
 * 和参考 FongMi 的 CatVodSpider 项目的 Local.java 修改而来
 * 支持配置文件传入 ext 参数来决定是否显示隐藏文件
 * 文件播放时获取当前目录下的媒体文件按照名称顺序播放
 */
public class LocalFileV2 extends Spider {

    private final String defaultFolderPic = "https://img.tukuppt.com/png_preview/00/18/23/GBmBU6fHo7.jpg!/fw/260";
    private final String defaultMediaPic = "https://img.tukuppt.com/png_preview/00/42/50/3ySGW7mvyY.jpg!/fw/260";

    private boolean showAllFile = true;
    private final List<String> media = Arrays.asList("mp4", "mkv", "wmv", "flv", "avi", "mp3", "aac", "flac", "m4a", "ape", "ogg", "rmvb", "ts");

    private String getFileExt(String name) {
        try {
            return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        } catch (Exception e) {
//            e.printStackTrace();
            return "";
        }
    }

    private String fileTime(long time, String fmt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);
        if (extend != null && extend.equals("showAllFile=false")) showAllFile = false;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject filterConfig = new JSONObject();
        JSONArray classes = new JSONArray();
        JSONObject newCls = new JSONObject();
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        newCls.put("type_id", root);
        newCls.put("type_name", "本地文件");
        // type_flag 是扩展字段，目前可取值0、1、2，该字段不存在时表示正常模式
        newCls.put("type_flag", "1"); // 1、列表形式的文件夹 2、缩略图 0或者不存在表示正常模式
        classes.put(newCls);

        // 补充 支持外部存储路径 start，参考了 FongMi 的 CatVodSpider 项目
        File[] files = new File("/storage").listFiles();
        if (files != null) {
            List<String> exclude = Arrays.asList("emulated", "sdcard", "self");
            for (File file : files) {
                if (exclude.contains(file.getName())) continue;
                JSONObject obj = new JSONObject();
                obj.put("type_id", file.getAbsolutePath());
                obj.put("type_name", file.getName());
                obj.put("type_flag", "1");
                classes.put(obj);
            }
        }
        // 补充 支持外部存储路径 end

        JSONObject result = new JSONObject();
        result.put("class", classes);
        if (filter) {
            result.put("filters", new JSONObject("{}"));
        }
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        File folder = new File(tid);
        File[] files = folder.listFiles();

        List<File> folderList = new ArrayList<>();
        List<File> fileList = new ArrayList<>();

        // 将文件夹和文件分别放入不同的集合
        for (File file : files) {
            if (file.isDirectory()) {
                folderList.add(file);
            } else {
                if (!media.contains(getFileExt(file.getName()))) continue;
                fileList.add(file);
            }
        }

        // 对文件夹和文件集合进行按照文件名字母排序
        Collections.sort(folderList, (f1, f2) -> f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase()));
        Collections.sort(fileList, (f1, f2) -> f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase()));

        // 将排好序的文件夹和文件放入一个新的文件数组
        File[] sortedFiles = new File[folderList.size() + fileList.size()];
        int index = 0;
        for (File file : folderList) {
            sortedFiles[index] = file;
            index++;
        }
        for (File file : fileList) {
            sortedFiles[index] = file;
            index++;
        }

        JSONArray list = new JSONArray();
        for (File f : sortedFiles) {
            String filename = f.getName();
            if (!showAllFile && filename.indexOf('.') == 0) continue; // 过滤掉隐藏文件、隐藏文件夹
            //String pic = "https://img.tukuppt.com/png_preview/00/18/23/GBmBU6fHo7.jpg!/fw/260";
            String pic = defaultFolderPic;
            if (!f.isDirectory()) {
                //pic = "https://img.tukuppt.com/png_preview/00/42/50/3ySGW7mvyY.jpg!/fw/260";
                pic = defaultMediaPic;
            }
            JSONObject vod = new JSONObject();
            vod.put("vod_id", f.getAbsolutePath());
            vod.put("vod_name", f.getName());
            vod.put("vod_pic", pic);
            // 当 vod_tag 为 folder 时会点击该 item 会把当前 vod_id 当成新的类型 ID 重新进
            vod.put("vod_tag", f.isDirectory() ? "folder" : "file");
            vod.put("vod_remarks", fileTime(f.lastModified(), "yyyy/MM/dd aHH:mm:ss"));
            list.put(vod);
        }

        JSONObject result = new JSONObject();
        result.put("page", 1);
        result.put("pagecount", 1);
        result.put("limit", list.length());
        result.put("total", list.length());
        result.put("list", list);
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String filename = ids.get(0);
        File f = new File(filename);
        StringBuilder vod_play_url = new StringBuilder();
        File parentFile = f.getParentFile();
        String name = f.getName();
        if (parentFile != null) {
            File[] files = parentFile.listFiles();
            name = parentFile.getName();
            if (files != null) {
                Arrays.sort(files);
                for (File file : files) {
                    if (file.isDirectory()) continue;
                    String fileName2 = file.getName();
                    String suffix = getFileExt(fileName2);
                    if (suffix.equals("")) continue;
                    if (!media.contains(suffix)) continue;
                    vod_play_url.append(fileName2).append("$").append(file.getAbsolutePath()).append("#");
                }
            }
        } else {
            vod_play_url.append(name).append("$").append(filename);
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", name);
        vod.put("vod_pic", defaultMediaPic);
        vod.put("type_name", "本地文件");
        vod.put("vod_content", "当前文件所在目录：" + f.getParent());
        if (vod_play_url.length() > 0) {
            vod.put("vod_play_from", "播放");
            vod.put("vod_play_url", vod_play_url);
        }
        JSONObject result = new JSONObject();
        JSONArray list = new JSONArray();
        list.put(vod);
        result.put("list", list);
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("playUrl", "");
        result.put("url", id);
        return result.toString();
    }
}
