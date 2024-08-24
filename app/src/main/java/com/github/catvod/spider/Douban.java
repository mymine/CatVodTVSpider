package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;
//import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Douban extends Spider {

    private final String siteUrl = "https://frodo.douban.com/api/v2";
    private final String apikey = "?apikey=0ac44ae016490db2204ce0a042db2916";
    private String extend;
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36";

    private String req(String url, Map<String, String> headerMap) {
//        return OkHttp.string(url, headerMap);
        return OkHttpUtil.string(url, headerMap);
    }

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Host", "frodo.douban.com");
        header.put("Connection", "Keep-Alive");
        header.put("Referer", "https://servicewechat.com/wx2f9b06c1de1ccfca/84/page-frame.html");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat");
        return header;
    }

    private JSONArray parseVodListFromJSONArray(JSONArray items) throws Exception {
        JSONArray videos = new JSONArray();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String vodId = "msearch:" + item.optString("id");
            String name = item.optString("title");
            String pic = getPic(item);
            String remark = getRating(item);
            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodId);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }
        return videos;
    }

    private String getRating(JSONObject item) {
        try {
            return "评分：" + item.getJSONObject("rating").optString("value");
        } catch (Exception e) {
            return "";
        }
    }

    private String getPic(JSONObject item) {
        try {
            return item.getJSONObject("pic").optString("normal") + "@Referer=https://api.douban.com/@User-Agent=" + userAgent;
        } catch (Exception e) {
            return "";
        }
    }

    private String getTags(HashMap<String, String> extend) {
        try {
            StringBuilder tags = new StringBuilder();
            for (String key : extend.keySet()) if (!key.equals("sort")) tags.append(extend.get(key)).append(",");
            return substring(tags.toString());
        } catch (Exception e) {
            return "";
        }
    }

    public static String substring(String text) {
        return substring(text, 1);
    }

    public static String substring(String text, int num) {
        if (text != null && text.length() > num) {
            return text.substring(0, text.length() - num);
        } else {
            return text;
        }
    }

    @Override
    public void init(Context context, String extend) throws Exception {
        this.extend = extend;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        List<String> typeIds = Arrays.asList("hot_gaia", "tv_hot", "show_hot", "movie", "tv", "rank_list_movie", "rank_list_tv");
        List<String> typeNames = Arrays.asList("热门电影", "热播剧集", "热播综艺", "电影筛选", "电视筛选", "电影榜单", "电视剧榜单");
        for (int i = 0; i < typeIds.size(); i++) {
            JSONObject c = new JSONObject();
            c.put("type_id", typeIds.get(i));
            c.put("type_name", typeNames.get(i));
            classes.put(c);
        }
        String recommendUrl = "http://api.douban.com/api/v2/subject_collection/subject_real_time_hotest/items" + apikey;
        JSONObject jsonObject = new JSONObject(req(recommendUrl, getHeader()));
        JSONArray items = jsonObject.optJSONArray("subject_collection_items");
        JSONArray videos = parseVodListFromJSONArray(items);
        String f = req(extend, null);
        JSONObject filterConfig = new JSONObject(f);
        JSONObject result = new JSONObject();
        result.put("class", classes);
        if (filter) result.put("filters", filterConfig);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String sort = extend.get("sort") == null ? "T" : extend.get("sort");
        String tags = URLEncoder.encode(getTags(extend));
        int start = (Integer.parseInt(pg) - 1) * 20;
        String cateUrl;
        String itemKey = "items";
        switch (tid) {
            case "hot_gaia":
                sort = extend.get("sort") == null ? "recommend" : extend.get("sort");
                String area = extend.get("area") == null ? "全部" : extend.get("area");
                sort = sort + "&area=" + URLEncoder.encode(area);
                cateUrl = siteUrl + "/movie/hot_gaia" + apikey + "&sort=" + sort + "&start=" + start + "&count=20";
                break;
            case "tv_hot":
                String type = extend.get("type") == null ? "tv_hot" : extend.get("type");
                cateUrl = siteUrl + "/subject_collection/" + type + "/items" + apikey + "&start=" + start + "&count=20";
                itemKey = "subject_collection_items";
                break;
            case "show_hot":
                String showType = extend.get("type") == null ? "show_hot" : extend.get("type");
                cateUrl = siteUrl + "/subject_collection/" + showType + "/items" + apikey + "&start=" + start + "&count=20";
                itemKey = "subject_collection_items";
                break;
            case "tv":
                cateUrl = siteUrl + "/tv/recommend" + apikey + "&sort=" + sort + "&tags=" + tags + "&start=" + start + "&count=20";
                break;
            case "rank_list_movie":
                String rankMovieType = extend.get("榜单") == null ? "movie_real_time_hotest" : extend.get("榜单");
                cateUrl = siteUrl + "/subject_collection/" + rankMovieType + "/items" + apikey + "&start=" + start + "&count=20";
                itemKey = "subject_collection_items";
                break;
            case "rank_list_tv":
                String rankTVType = extend.get("榜单") == null ? "tv_real_time_hotest" : extend.get("榜单");
                cateUrl = siteUrl + "/subject_collection/" + rankTVType + "/items" + apikey + "&start=" + start + "&count=20";
                itemKey = "subject_collection_items";
                break;
            default:
                cateUrl = siteUrl + "/movie/recommend" + apikey + "&sort=" + sort + "&tags=" + tags + "&start=" + start + "&count=20";
                break;
        }
        JSONObject object = new JSONObject(req(cateUrl, getHeader()));
        JSONArray array = object.getJSONArray(itemKey);
        JSONArray videos = parseVodListFromJSONArray(array);
        int page = Integer.parseInt(pg), count = Integer.MAX_VALUE, limit = 20, total = Integer.MAX_VALUE;
        JSONObject result = new JSONObject();
        result.put("page", page);
        result.put("pagecount", count);
        result.put("limit", limit);
        result.put("total", total);
        result.put("list", videos);
        return result.toString();
    }
}