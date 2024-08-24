package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
//import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhixc
 * 6V电影网（新版页面）
 */
public class SixV extends Spider {

    // 可用域名：
    //   https://www.6vdy.org
    //   https://www.66s6.cc
    //   https://www.xb6v.com
    private final String siteUrl = "https://www.6vdy.org";
    private String nextSearchUrlPrefix;
    private String nextSearchUrlSuffix;

    private final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36";

    private String req(String url, Map<String, String> header) {
        //return OkHttp.string(url, header);
        return OkHttpUtil.string(url, header);
    }

    private Response req(Request request) throws Exception {
        return okClient().newCall(request).execute();
    }

    private String req(Response response) throws Exception {
        if (!response.isSuccessful()) return "";
        String content = response.body().string();
        response.close();
        return content;
    }

    private OkHttpClient okClient() {
        //return OkHttp.client();
        return OkHttpUtil.defaultClient();
    }

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        header.put("Referer", siteUrl + "/");
        return header;
    }

    private Map<String, String> getDetailHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        return header;
    }

    private Map<String, String> getSearchHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        return header;
    }

    private String find(Pattern pattern, String html) {
        Matcher m = pattern.matcher(html);
        return m.find() ? m.group(1).trim() : "";
    }

    private JSONArray parseVodListFromDoc(String html) throws Exception {
        JSONArray videos = new JSONArray();
        Elements items = Jsoup.parse(html).select("#post_container [class=zoom]");
        for (Element item : items) {
            String vodId = item.attr("href");
            String name = removeHtmlTag(item.attr("title"));
            String pic = item.select("img").attr("src");
            String remark = "";

            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodId);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }
        return videos;
    }

    private String getActor(String html) {
        String actor = find(Pattern.compile("◎演　　员　(.*?)</p>"), html);
        if ("".equals(actor)) actor = find(Pattern.compile("◎主　　演　(.*?)</p>"), html);
        return clean(actor);
    }

    private String clean(String str) {
        return str.replaceAll("&amp;", "").replaceAll("middot;", "・").replaceAll("&nbsp;", "").replaceAll("<br>", "").replaceAll("　　　　　", " / ").replaceAll("　　　　 　", " / ");
    }

    private String getDirector(String html) {
        return clean(find(Pattern.compile("◎导　　演　(.*?)<br>"), html));
    }

    private String getDescription(String html) {
        return clean(find(Pattern.compile("◎简　　介(.*?)<hr", Pattern.DOTALL), html)).replaceAll("\n", "").replaceAll("　", "").replaceAll("hellip;", "").replaceAll("ldquo;", "【").replaceAll("rdquo;", "】");
    }

    private String removeHtmlTag(String str) {
        return str.replaceAll("</?[^>]+>", "");
    }

    private boolean isMovie(String vodId) {
        return !(vodId.startsWith("/donghuapian") || vodId.startsWith("/dianshiju") || vodId.startsWith("/ZongYi"));
    }

    private Map<String, String> parsePlayMapFromDoc(Elements sourceList) {
        Map<String, String> playMap = new LinkedHashMap<>();
        String vod_play_from = "磁力";
        int i = 0;
        for (Element source : sourceList) {
            i++;
            Elements aList = source.select("table a");
            List<String> vodItems = new ArrayList<>();
            for (Element a : aList) {
                String episodeUrl = a.attr("href");
                String episodeName = a.text();
                if (!episodeUrl.startsWith("magnet")) continue;
                vodItems.add(episodeName + "$" + episodeUrl);
            }
            if (vodItems.size() > 0) playMap.put(vod_play_from + i, TextUtils.join("#", vodItems));
        }
        return playMap;
    }

    private Map<String, String> parsePlayMapForMovieFromDoc(Elements sourceList) {
        Map<String, String> playMap = new LinkedHashMap<>();
        for (Element source : sourceList) {
            Elements aList = source.select("table a");
            for (int i = 0; i < aList.size(); i++) {
                Element a = aList.get(i);
                String episodeUrl = a.attr("href");
                String episodeName = a.text();
                if (!episodeUrl.startsWith("magnet")) continue;
                if (playMap.containsKey(episodeName)) episodeName += i;
                playMap.put(episodeName, episodeUrl);
            }
        }
        return playMap;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        List<String> typeIds = Arrays.asList("xijupian", "dongzuopian", "aiqingpian", "kehuanpian", "kongbupian", "juqingpian", "zhanzhengpian", "jilupian", "donghuapian", "dianshiju/guoju", "dianshiju/rihanju", "dianshiju/oumeiju");
        List<String> typeNames = Arrays.asList("喜剧片", "动作片", "爱情片", "科幻片", "恐怖片", "剧情片", "战争片", "纪录片", "动画片", "国剧", "日韩剧", "欧美剧");
        for (int i = 0; i < typeIds.size(); i++) {
            JSONObject c = new JSONObject();
            c.put("type_id", typeIds.get(i));
            c.put("type_name", typeNames.get(i));
            classes.put(c);
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        return result.toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        String html = req(siteUrl, getHeader());
        JSONArray videos = parseVodListFromDoc(html);
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cateUrl = siteUrl + "/" + tid;
        if (!pg.equals("1")) cateUrl += "/index_" + pg + ".html";
        String html = req(cateUrl, getHeader());
        JSONArray videos = parseVodListFromDoc(html);
        int page = Integer.parseInt(pg), count = 999, limit = videos.length(), total = Integer.MAX_VALUE;
        JSONObject result = new JSONObject();
        result.put("page", page);
        result.put("pagecount", count);
        result.put("limit", limit);
        result.put("total", total);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String vodId = ids.get(0);
        String detailUrl = siteUrl + vodId;
        String html = req(detailUrl, getDetailHeader());
        Document doc = Jsoup.parse(html);
        Elements sourceList = doc.select("#post_content");
        Map<String, String> playMap = isMovie(vodId) ? parsePlayMapForMovieFromDoc(sourceList) : parsePlayMapFromDoc(sourceList);

        String partHTML = doc.select(".context").html();
        String name = doc.select(".article_container > h1").text();
        String pic = doc.select("#post_content img").attr("src");
        String typeName = find(Pattern.compile("◎类　　别　(.*?)<br>"), partHTML);
        String year = find(Pattern.compile("◎年　　代　(.*?)<br>"), partHTML);
        String area = find(Pattern.compile("◎产　　地　(.*?)<br>"), partHTML);
        String remark = "上映日期：" + find(Pattern.compile("◎上映日期　(.*?)<br>"), partHTML);
        String actor = getActor(partHTML);
        String director = getDirector(partHTML);
        String description = removeHtmlTag(getDescription(partHTML));

        // 由于部分信息过长，故进行一些调整，将年份、地区等信息放到 类别、备注里面
        typeName += " 地区:" + area;
        area = "";
        typeName += " 年份:" + year;
        remark += " 年份:" + year;
        year = "";

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
        if (playMap.size() > 0) {
            vod.put("vod_play_from", TextUtils.join("$$$", playMap.keySet()));
            vod.put("vod_play_url", TextUtils.join("$$$", playMap.values()));
        }
        JSONArray jsonArray = new JSONArray().put(vod);
        JSONObject result = new JSONObject().put("list", jsonArray);
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String searchUrl = siteUrl + "/e/search/index.php";
        String html = "";
        if ("1".equals(pg)) {
            String formData = "show=title&tempid=1&tbname=article&mid=1&dopost=search&submit=&keyboard=" + URLEncoder.encode(key, "UTF-8");
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), formData);
            Request request = new Request.Builder()
                    .url(searchUrl)
                    .addHeader("User-Agent", userAgent)
                    .addHeader("Origin", siteUrl)
                    .addHeader("Referer", siteUrl + "/")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(requestBody)
                    .build();
            Response response = req(request);
            if (!response.isSuccessful()) return "";
            String[] split = String.valueOf(response.request().url()).split("\\?searchid=");
            nextSearchUrlPrefix = split[0] + "index.php?page=";
            nextSearchUrlSuffix = "&searchid=" + split[1];
            html = req(response);
        } else {
            int page = Integer.parseInt(pg) - 1;
            searchUrl = nextSearchUrlPrefix + page + nextSearchUrlSuffix;
            html = req(searchUrl, getSearchHeader());
        }
        JSONArray videos = parseVodListFromDoc(html);
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
