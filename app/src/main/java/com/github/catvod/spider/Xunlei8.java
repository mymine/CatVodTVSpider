package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
//import com.github.catvod.net.OkHttp;
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
 * 迅雷电影天堂、迅雷吧
 */
public class Xunlei8 extends Spider {
    private final String siteUrl = "https://xunlei8.top";

    private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        header.put("Referer", siteUrl + "/");
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
        return new String(bytes, "UTF-8");
    }

    private OkHttpClient okClient() {
        //return OkHttp.client();
        return OkHttpUtil.defaultClient();
    }

    private String find(Pattern pattern, String html) {
        Matcher m = pattern.matcher(html);
        return m.find() ? m.group(1).trim() : "";
    }

    private JSONArray parseVodListFromDoc(String html) throws Exception {
        JSONArray videos = new JSONArray();
        Document doc = Jsoup.parse(html);
        Elements items = doc.select(".b876dd567bb .b33c0");
        for (Element it : items) {
            String vodId = it.select("a:eq(0)").attr("href");
            //String name = it.select("a:eq(0)").attr("title").split(" ")[0];
            String name = it.select("a:eq(0)").attr("title");
            String pic = it.select("a:eq(0) img:eq(0)").attr("src");
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

    private String fixVodInfo(Element e) {
        StringBuilder sb = new StringBuilder();
        for (Element a : e.select("a")) sb.append(a.text()).append("/");
        return sb.toString();
    }

    private String removeHtmlTag(String str) {
        return str.replaceAll("</?[^>]+>", "");
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        List<String> typeIds = Arrays.asList("list", "tv");
        List<String> typeNames = Arrays.asList("电影", "电视剧");
        for (int i = 0; i < typeIds.size(); i++) {
            JSONObject c = new JSONObject();
            c.put("type_id", typeIds.get(i));
            c.put("type_name", typeNames.get(i));
            classes.put(c);
        }
        String f = "{\"list\": [{\"name\": \"类型\", \"key\": \"cateId\", \"value\": [{\"n\": \"全部\", \"v\": \"0\"}, {\"n\": \"科幻\", \"v\": \"科幻\"}, {\"n\": \"悬疑\", \"v\": \"悬疑\"}, {\"n\": \"情色\", \"v\": \"情色\"}, {\"n\": \"恐怖\", \"v\": \"恐怖\"}, {\"n\": \"奇幻\", \"v\": \"奇幻\"}, {\"n\": \"喜剧\", \"v\": \"喜剧\"}, {\"n\": \"战争\", \"v\": \"战争\"}, {\"n\": \"动作\", \"v\": \"动作\"}, {\"n\": \"动画\", \"v\": \"动画\"}, {\"n\": \"冒险\", \"v\": \"冒险\"}, {\"n\": \"爱情\", \"v\": \"爱情\"}, {\"n\": \"武侠\", \"v\": \"武侠\"}, {\"n\": \"犯罪\", \"v\": \"犯罪\"}, {\"n\": \"惊悚\", \"v\": \"惊悚\"}, {\"n\": \"剧情\", \"v\": \"剧情\"}, {\"n\": \"纪录片\", \"v\": \"纪录片\"}, {\"n\": \"运动\", \"v\": \"运动\"}, {\"n\": \"历史\", \"v\": \"历史\"}, {\"n\": \"西部\", \"v\": \"西部\"}, {\"n\": \"家庭\", \"v\": \"家庭\"}, {\"n\": \"音乐\", \"v\": \"音乐\"}, {\"n\": \"同性\", \"v\": \"同性\"}]}, {\"name\": \"年份\", \"key\": \"year\", \"value\": [{\"n\": \"全部\", \"v\": \"0\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}, {\"n\": \"2014\", \"v\": \"2014\"}, {\"n\": \"2013\", \"v\": \"2013\"}, {\"n\": \"2012\", \"v\": \"2012\"}, {\"n\": \"2011\", \"v\": \"2011\"}, {\"n\": \"2010\", \"v\": \"2010\"}, {\"n\": \"2009\", \"v\": \"2009\"}, {\"n\": \"2008\", \"v\": \"2008\"}, {\"n\": \"更早\", \"v\": \"更早\"}]}, {\"name\": \"地区\", \"key\": \"area\", \"value\": [{\"n\": \"全部\", \"v\": \"0\"}, {\"n\": \"美国\", \"v\": \"美国\"}, {\"n\": \"中国大陆\", \"v\": \"中国大陆\"}, {\"n\": \"韩国\", \"v\": \"韩国\"}, {\"n\": \"日本\", \"v\": \"日本\"}, {\"n\": \"英国\", \"v\": \"英国\"}, {\"n\": \"印度\", \"v\": \"印度\"}, {\"n\": \"法国\", \"v\": \"法国\"}, {\"n\": \"俄罗斯\", \"v\": \"俄罗斯\"}, {\"n\": \"加拿大\", \"v\": \"加拿大\"}, {\"n\": \"德国\", \"v\": \"德国\"}, {\"n\": \"泰国\", \"v\": \"泰国\"}, {\"n\": \"西班牙\", \"v\": \"西班牙\"}, {\"n\": \"澳大利亚\", \"v\": \"澳大利亚\"}, {\"n\": \"意大利\", \"v\": \"意大利\"}, {\"n\": \"比利时\", \"v\": \"比利时\"}, {\"n\": \"中国台湾\", \"v\": \"中国台湾\"}, {\"n\": \"中国香港\", \"v\": \"中国香港\"}]}, {\"name\": \"排序\", \"key\": \"sort\", \"value\": [{\"n\": \"最近更新(默认)\", \"v\": \"date\"}, {\"n\": \"精彩热播\", \"v\": \"hot\"}, {\"n\": \"高分好评\", \"v\": \"rating\"}]}], \"tv\": [{\"name\": \"类型\", \"key\": \"cateId\", \"value\": [{\"n\": \"全部\", \"v\": \"0\"}, {\"n\": \"科幻\", \"v\": \"科幻\"}, {\"n\": \"悬疑\", \"v\": \"悬疑\"}, {\"n\": \"情色\", \"v\": \"情色\"}, {\"n\": \"恐怖\", \"v\": \"恐怖\"}, {\"n\": \"奇幻\", \"v\": \"奇幻\"}, {\"n\": \"喜剧\", \"v\": \"喜剧\"}, {\"n\": \"战争\", \"v\": \"战争\"}, {\"n\": \"动作\", \"v\": \"动作\"}, {\"n\": \"动画\", \"v\": \"动画\"}, {\"n\": \"冒险\", \"v\": \"冒险\"}, {\"n\": \"爱情\", \"v\": \"爱情\"}, {\"n\": \"武侠\", \"v\": \"武侠\"}, {\"n\": \"犯罪\", \"v\": \"犯罪\"}, {\"n\": \"惊悚\", \"v\": \"惊悚\"}, {\"n\": \"剧情\", \"v\": \"剧情\"}, {\"n\": \"纪录片\", \"v\": \"纪录片\"}, {\"n\": \"运动\", \"v\": \"运动\"}, {\"n\": \"历史\", \"v\": \"历史\"}, {\"n\": \"西部\", \"v\": \"西部\"}, {\"n\": \"家庭\", \"v\": \"家庭\"}, {\"n\": \"音乐\", \"v\": \"音乐\"}, {\"n\": \"同性\", \"v\": \"同性\"}]}, {\"name\": \"年份\", \"key\": \"year\", \"value\": [{\"n\": \"全部\", \"v\": \"0\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}, {\"n\": \"2014\", \"v\": \"2014\"}, {\"n\": \"2013\", \"v\": \"2013\"}, {\"n\": \"2012\", \"v\": \"2012\"}, {\"n\": \"2011\", \"v\": \"2011\"}, {\"n\": \"2010\", \"v\": \"2010\"}, {\"n\": \"2009\", \"v\": \"2009\"}, {\"n\": \"2008\", \"v\": \"2008\"}, {\"n\": \"更早\", \"v\": \"更早\"}]}, {\"name\": \"地区\", \"key\": \"area\", \"value\": [{\"n\": \"全部\", \"v\": \"0\"}, {\"n\": \"美国\", \"v\": \"美国\"}, {\"n\": \"中国大陆\", \"v\": \"中国大陆\"}, {\"n\": \"韩国\", \"v\": \"韩国\"}, {\"n\": \"日本\", \"v\": \"日本\"}, {\"n\": \"英国\", \"v\": \"英国\"}, {\"n\": \"印度\", \"v\": \"印度\"}, {\"n\": \"法国\", \"v\": \"法国\"}, {\"n\": \"俄罗斯\", \"v\": \"俄罗斯\"}, {\"n\": \"加拿大\", \"v\": \"加拿大\"}, {\"n\": \"德国\", \"v\": \"德国\"}, {\"n\": \"泰国\", \"v\": \"泰国\"}, {\"n\": \"西班牙\", \"v\": \"西班牙\"}, {\"n\": \"澳大利亚\", \"v\": \"澳大利亚\"}, {\"n\": \"意大利\", \"v\": \"意大利\"}, {\"n\": \"比利时\", \"v\": \"比利时\"}, {\"n\": \"中国台湾\", \"v\": \"中国台湾\"}, {\"n\": \"中国香港\", \"v\": \"中国香港\"}]}, {\"name\": \"排序\", \"key\": \"sort\", \"value\": [{\"n\": \"最近更新(默认)\", \"v\": \"date\"}, {\"n\": \"精彩热播\", \"v\": \"hot\"}, {\"n\": \"高分好评\", \"v\": \"rating\"}]}]}";
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
        JSONArray videos = parseVodListFromDoc(html);
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        // https://xunlei8.top/list-科幻-2024-美国-date-1-30.html
        String cateId = extend.get("cateId") == null ? "0" : extend.get("cateId");
        String year = extend.get("year") == null ? "0" : extend.get("year");
        String area = extend.get("area") == null ? "0" : extend.get("area");
        String sort = extend.get("sort") == null ? "date" : extend.get("sort");
        String cateUrl = siteUrl + "/" + tid + "-" + cateId + "-" + year + "-" + area + "-" + sort + "-" + pg + "-30.html";
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
        String link = siteUrl + ids.get(0);
        String html = req(link, getHeader());

        String pic = "";
        String typeName = "";
        String year = "";
        String area = "";
        String remark = "";
        String actor = "";
        String director = "";
        String brief = "";
        Document doc = Jsoup.parse(html);
        pic = doc.select(".bd800a7092 > img").attr("src");
        brief = doc.select(".b1f40f7888").text();
        Elements elements = doc.select(".be998a > p");
        for (Element e : elements) {
            String text = e.text();
            if (text.startsWith("类型")) typeName = fixVodInfo(e);
            if (text.startsWith("上映")) year = text.replace("上映：", "");
            if (text.startsWith("地区")) area = text.replace("地区：", "");
            if (text.startsWith("片长")) remark = text;
            if (text.startsWith("主演")) actor = fixVodInfo(e);
            if (text.startsWith("导演")) director = fixVodInfo(e);
        }
        typeName += " 地区:" + area;
        area = "";
        typeName += " 年份:" + year;
        remark += " 年份:" + year;
        year = "";

        List<String> magnetList = new ArrayList<>();
        List<String> ed2kList = new ArrayList<>();
        List<String> vodItems = new ArrayList<>();
        Elements aList = doc.select("a.copylink");
        for (int i = 0; i < aList.size(); i++) {
            Element a = aList.get(i);
            String episodeUrl = a.attr("alt");
            String episodeName = (i + 1) + "";
            String episode = episodeName + "$" + episodeUrl;
            if (episodeUrl.startsWith("magnet")) magnetList.add(episode);
            if (episodeUrl.startsWith("ed2k")) ed2kList.add(episode);
            if (episodeUrl.startsWith("thunder://")) vodItems.add(episode);
        }
        Map<String, String> playMap = new LinkedHashMap<>();
        if (magnetList.size() > 0) playMap.put("磁力", String.join("#", magnetList));
        if (ed2kList.size() > 0) playMap.put("电驴", String.join("#", ed2kList));
        if (vodItems.size() > 0) playMap.put("边下边播", String.join("#", vodItems));

        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", doc.select("h1").text()); // 影片名称
        vod.put("vod_pic", pic); // 图片
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
        if (!"1".equals(pg)) return "";
        String searchUrl = siteUrl + "/s/" + URLEncoder.encode(key) + ".html";
        String html = req(searchUrl, getHeader());
        JSONArray videos = new JSONArray();
        Document doc = Jsoup.parse(html);
        for (Element it : doc.select(".b007")) {
            String vodId = it.select("a:eq(0)").attr("href");
            //String name = it.select("h2 > a:eq(0)").text().split(" ")[0];
            String name = it.select("h2 > a:eq(0)").text();
            String pic = it.select("a:eq(0) > img").attr("src");
            String remark = "";

            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodId);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
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
