package com.github.catvod.spider;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhixc
 * 电影港
 * 说明：主要在 DyGang.java 的基础上
 * 针对动漫、电视剧、综艺类型的保留原来的剧集拼接方式
 * 而电影类型的一条链接作为一个播放源，这样更适配
 * FongMi的影视，尤其是播放失败时自动换源。
 */
public class DyGang extends Spider {

    //  地址发布：https://www.dygang.me/
    //  可用的域名：
    //   http://www.dygangs.net
    //   http://www.dygangs.me
    //   https://www.dygang.tv
    private final String siteUrl = "http://www.dygangs.me";
    private String nextSearchUrlPrefix;
    private String nextSearchUrlSuffix;

    private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        header.put("Referer", siteUrl + "/");
        return header;
    }

    private Map<String, String> getSearchHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        return header;
    }

    private String req(String url, Map<String, String> header) throws Exception {
        Request.Builder builder = new Request.Builder().get().url(url);
        for (String key : header.keySet()) builder.addHeader(key, header.get(key));
        Request request = builder.build();
        return req(request);
    }

    private String req(Request request) throws Exception {
        Response response = okClient().newCall(request).execute();
        return req(response);
    }

    private String req(Response response) throws Exception {
        if (!response.isSuccessful()) return "";
        byte[] bytes = response.body().bytes();
        response.close();
        return new String(bytes, "GBK");
    }

    private OkHttpClient okClient() {
        //return OkHttp.client();
        return OkHttpUtil.defaultClient();
    }

    private String find(Pattern pattern, String html) {
        Matcher m = pattern.matcher(html);
        return m.find() ? m.group(1).trim() : "";
    }

    private JSONArray parseVodListFromDoc(String html, boolean isHotVod) throws Exception {
        JSONArray videos = new JSONArray();
        Document doc = Jsoup.parse(html);
        String itemsCssQuery = isHotVod ? "td[width=132]" : "table[width=388]";
        Elements items = doc.select(itemsCssQuery);
        for (Element it : items) {
            String vodId = it.select("a:eq(0)").attr("href");
            String name = it.select("a:eq(0) > img:eq(0)").attr("alt");
            String pic = it.select("a:eq(0) > img:eq(0)").attr("src");
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
        String actor = find(Pattern.compile("◎演　　员　(.*?)</p", Pattern.DOTALL), html);
        if ("".equals(actor)) actor = find(Pattern.compile("◎主　　演　(.*?)</p", Pattern.DOTALL), html);
        return actor.replaceAll("&middot;", "·").replaceAll("\r\n", "").replaceAll("<br />", "").replaceAll("&nbsp;", "").replaceAll("　　　　 　", " / ").replaceAll("　　　　　 ", " / ").replaceAll("　　　　　　", " / ");
    }

    private String getDirector(String html) {
        return find(Pattern.compile("◎导　　演　(.*?)<br"), html).replaceAll("&middot;", "·");
    }

    private String getBrief(String html) {
        return find(Pattern.compile("◎简　　介(.*?)<hr", Pattern.DOTALL), html).replaceAll("&middot;", "·").replaceAll("\r\n", "").replaceAll("&nbsp;", " ").replaceAll("　　　　", "");
    }

    private String removeHtmlTag(String str) {
        return str.replaceAll("</?[^>]+>", "");
    }


    private boolean isMovie(String vodId) {
        return !(vodId.startsWith("/dsj") || vodId.startsWith("/dsj1") || vodId.startsWith("/yx") || vodId.startsWith("/dmq"));
    }

    private Map<String, String> parsePlayMapFromDoc(Document doc) {
        Map<String, String> playMap = new LinkedHashMap<>();
        List<String> magnetList = new ArrayList<>();
        List<String> ed2kList = new ArrayList<>();
        Elements aList = doc.select("td[bgcolor=#ffffbb] > a");
        for (Element a : aList) {
            String episodeUrl = a.attr("href");
            String episodeName = a.text();
            String episode = episodeName + "$" + episodeUrl;
            if (episodeUrl.startsWith("magnet")) magnetList.add(episode);
            if (episodeUrl.startsWith("ed2k")) ed2kList.add(episode);
        }
        if (magnetList.size() > 0) playMap.put("磁力", String.join("#", magnetList));
        if (ed2kList.size() > 0) playMap.put("电驴", String.join("#", ed2kList));
        return playMap;
    }

    private Map<String, String> parsePlayMapForMovieFromDoc(Document doc) {
        Map<String, String> playMap = new LinkedHashMap<>();
        Elements aList = doc.select("td[bgcolor=#ffffbb] > a");
        for (int i = 0; i < aList.size(); i++) {
            Element a = aList.get(i);
            String episodeUrl = a.attr("href");
            String episodeName = a.text();
            if (episodeUrl.startsWith("magnet") || episodeUrl.startsWith("ed2k")) {
                if (playMap.containsKey(episodeName)) episodeName += i;
                playMap.put(episodeName, episodeUrl);
            }
        }
        return playMap;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        List<String> typeIds = Arrays.asList("my_dianying", "my_dianshiju", "dmq", "zy", "jilupian");
        List<String> typeNames = Arrays.asList("电影", "电视剧", "动漫", "综艺", "纪录片");
        for (int i = 0; i < typeIds.size(); i++) {
            JSONObject c = new JSONObject();
            c.put("type_id", typeIds.get(i));
            c.put("type_name", typeNames.get(i));
            classes.put(c);
        }
        String f = "{\"my_dianying\": [{\"name\": \"类型\", \"key\": \"cateId\", \"value\": [{\"n\": \"最新电影(默认)\", \"v\": \"ys\"}, {\"n\": \"经典高清\", \"v\": \"bd\"}, {\"n\": \"国配电影\", \"v\": \"gy\"}, {\"n\": \"经典港片\", \"v\": \"gp\"}]}], \"my_dianshiju\": [{\"name\": \"类型\", \"key\": \"cateId\", \"value\": [{\"n\": \"国剧(默认)\", \"v\": \"dsj\"}, {\"n\": \"日韩剧\", \"v\": \"dsj1\"}, {\"n\": \"美剧\", \"v\": \"yx\"}]}]}";
        JSONObject filterConfig = new JSONObject(f);
        JSONArray videos = new JSONArray();
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", filterConfig);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        String html = req(siteUrl, getHeader());
        JSONArray videos = parseVodListFromDoc(html, true);
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        if ("my_dianying".equals(tid)) tid = extend.get("cateId") == null ? "ys" : extend.get("cateId");
        if ("my_dianshiju".equals(tid)) tid = extend.get("cateId") == null ? "dsj" : extend.get("cateId");
        String cateUrl = siteUrl + "/" + tid;
        if (!"1".equals(pg)) cateUrl += "/index_" + pg + ".htm";
        String html = req(cateUrl, getHeader());
        JSONArray videos = parseVodListFromDoc(html, false);
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
        String link = siteUrl + vodId;
        String html = req(link, getHeader());
        String remark = "上映日期：" + removeHtmlTag(find(Pattern.compile("◎上映日期　(.*?)<br"), html));
        //String remark = find(Pattern.compile("◎片　　长　(.*?)<br"), html);
        //String remark = find(Pattern.compile("◎语　　言　(.*?)<br"), html);
        String actor = getActor(html);
        String director = getDirector(html);
        String brief = removeHtmlTag(getBrief(html)).replaceAll("　　　", "").replaceAll("　　", "");
        Document doc = Jsoup.parse(html);
        Map<String, String> playMap = isMovie(vodId) ? parsePlayMapForMovieFromDoc(doc) : parsePlayMapFromDoc(doc);

        String typeName = removeHtmlTag(find(Pattern.compile("◎类　　别　(.*?)<br"), html)).replaceAll(" / ", "/");
        String year = find(Pattern.compile("◎年　　代　(.*?)<br"), html);
        String area = removeHtmlTag(find(Pattern.compile("◎产　　地　(.*?)<br"), html));

        // 由于部分信息过长，故进行一些调整，将年份、地区等信息放到 类别、备注里面
        typeName += " 地区:" + area;
        area = "";
        typeName += " 年份:" + year;
        remark += " 年份:" + year;
        year = "";

        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", doc.select("div[class=title] > a:eq(0)").text()); // 影片名称
        vod.put("vod_pic", doc.select("img[width=120]:eq(0)").attr("src")); // 图片
        vod.put("type_name", typeName); // 影片类型 选填
        vod.put("vod_year", year); // 年份 选填
        vod.put("vod_area", area); // 地区 选填
        vod.put("vod_remarks", remark); // 备注 选填
        vod.put("vod_actor", actor); // 主演 选填
        vod.put("vod_director", director); // 导演 选填
        vod.put("vod_content", brief); // 简介 选填
        if (playMap.size() > 0) {
            vod.put("vod_play_from", String.join("$$$", playMap.keySet()));
            vod.put("vod_play_url", String.join("$$$", playMap.values()));
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
        String searchUrl = "http://www.dygangs.me/e/search/index.php";
        String html = "";
        if ("1".equals(pg)) {
            String requestBody = "tempid=1&tbname=article&keyboard=" + URLEncoder.encode(key, "GBK") + "&show=title%2Csmalltext&Submit=%CB%D1%CB%F7";
            RequestBody formBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestBody);
            Request request = new Request.Builder()
                    .url(searchUrl)
                    .post(formBody)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Origin", "http://www.dygangs.me")
                    .header("Referer", "http://www.dygangs.me/")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36")
                    .build();
            Response response = okClient().newCall(request).execute();
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
        JSONArray videos = parseVodListFromDoc(html, false);
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
