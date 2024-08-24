package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author zhixc
 * 新飘花电影网
 */
public class PiaoHua extends Spider {

    private final String siteUrl = "https://www.xpiaohua.com";

    private final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36";

    private String req(String targetUrl) throws Exception {
        Request request = new Request.Builder()
                .addHeader("User-Agent", userAgent)
                .get()
                .url(targetUrl)
                .build();
        OkHttpClient okHttpClient = OkHttpUtil.defaultClient();
        Response response = okHttpClient.newCall(request).execute();
        if (response.body() == null) return "";
        byte[] bytes = response.body().bytes();
        response.close();
        return new String(bytes, "gb2312");
    }

    private String find(Pattern pattern, String html) {
        Matcher matcher = pattern.matcher(html);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static String getEpisodeName(String episodeUrl) {
        try {
            return episodeUrl.split("&dn=")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "第1集";
    }

    private String getDescription(Pattern pattern, String html) {
        return find(pattern, html).replaceAll("</?[^>]+>", "");
    }

    private String getDirectorStr(Pattern pattern, String html) {
        return find(pattern, html)
                .replaceAll("&middot;", "·");
    }

    private String getActorStr(String html) {
        Pattern p1 = Pattern.compile("◎演　　员　(.*?)◎");
        Pattern p2 = Pattern.compile("◎主　　演　(.*?)◎");
        String actor = find(p1, html).equals("") ? find(p2, html) : "";
        return actor.replaceAll("</?[^>]+>", "")
                .replaceAll("　　　　　", "")
                .replaceAll("&middot;", "·");
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        List<String> typeIds = Arrays.asList("/dongzuo/", "/xiju/", "/aiqing/", "/kehuan/", "/juqing/", "/xuanyi/", "/zhanzheng/", "/kongbu/", "/zainan/", "/dongman/", "/jilu/");
        List<String> typeNames = Arrays.asList("动作片", "喜剧片", "爱情片", "科幻片", "剧情片", "悬疑片", "战争片", "恐怖片", "灾难片", "动漫", "纪录片");
        for (int i = 0; i < typeIds.size(); i++) {
            JSONObject obj = new JSONObject();
            obj.put("type_id", typeIds.get(i));
            obj.put("type_name", typeNames.get(i));
            classes.put(obj);
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        // 第一页
        // https://www.xpiaohua.com/column/xiju/
        // 第二页
        // https://www.xpiaohua.com/column/xiju/list_2.html
        String cateUrl = siteUrl + "/column" + tid;
        if (!pg.equals("1")) cateUrl += "/list_" + pg + ".html";
        String html = req(cateUrl);
        JSONArray videos = new JSONArray();
        Elements items = Jsoup.parse(html).select("#list dl");
        for (Element item : items) {
            String vid = item.select("strong a").attr("href");
            String name = item.select("strong").text();
            String pic = item.select("img").attr("src");


            JSONObject vod = new JSONObject();
            vod.put("vod_id", vid);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", "");
            videos.put(vod);
        }

        JSONObject result = new JSONObject();
        result.put("pagecount", 999);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String detailURL = ids.get(0);
        String html = req(detailURL);
        Document doc = Jsoup.parse(html);
        String vod_play_url = "";
        String vod_play_from = "magnet";
        Elements aList = doc.select("table").get(0).select("a");
        List<String> vodItems = new ArrayList<>();
        for (Element element : aList) {
            String episodeURL = element.attr("href");
            if (!episodeURL.startsWith("magnet")) continue;
            String episodeName = getEpisodeName(episodeURL);
            vodItems.add(episodeName + "$" + episodeURL);
        }
        if (vodItems.size() > 0) {
            vod_play_url = TextUtils.join("#", vodItems);
        }

        String name = doc.select("h3").text();
        String pic = doc.select("#showinfo img").attr("src");
        String typeName = find(Pattern.compile("◎类　　别　(.*?)<br"), html);
        String year = find(Pattern.compile("◎年　　代　(.*?)<br"), html);
        String area = find(Pattern.compile("◎产　　地　(.*?)<br"), html);
        String remark = find(Pattern.compile("◎上映日期　(.*?)<br"), html);
        String actor = getActorStr(html);
        String director = getDirectorStr(Pattern.compile("◎导　　演　(.*?)<br"), html);
        String description = getDescription(Pattern.compile("◎简　　介(.*?)◎", Pattern.CASE_INSENSITIVE | Pattern.DOTALL), html);


        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", name);
        vod.put("vod_pic", pic);
        vod.put("type_name", typeName);
        vod.put("vod_year", year);
        vod.put("vod_area", area);
        vod.put("vod_remarks", remark);
        vod.put("vod_actor", actor);
        vod.put("vod_director", director);
        vod.put("vod_content", description);
        if (vod_play_url.length() > 0) {
            vod.put("vod_play_from", vod_play_from);
            vod.put("vod_play_url", vod_play_url);
        }

        JSONArray jsonArray = new JSONArray().put(vod);
        JSONObject result = new JSONObject();
        result.put("list", jsonArray);
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String searchURL = siteUrl + "/plus/search.php?q=" + URLEncoder.encode(key, "GBK") + "&searchtype.x=0&searchtype.y=0";
        String html = req(searchURL);
        JSONArray videos = new JSONArray();
        Elements items = Jsoup.parse(html).select("#list dl");
        for (Element item : items) {
            String vid = item.select("strong a").attr("href");
            String name = item.select("strong").text();
            String pic = item.select("img").attr("src");

            JSONObject vod = new JSONObject();
            vod.put("vod_id", vid);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", "");
            videos.put(vod);
        }
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("header", "");
        result.put("playUrl", "");
        result.put("url", id);
        return result.toString();
    }
}