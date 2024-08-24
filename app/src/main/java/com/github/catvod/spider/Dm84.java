package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
//import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.net.URLEncoder;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhixc
 * 动漫84（动漫巴士）
 */
public class Dm84 extends Spider {

    private final String siteUrl = "https://dm84.tv";

    private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:102.0) Gecko/20100101 Firefox/102.0";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        header.put("Referer", siteUrl + "/");
        return header;
    }

    private String req(String url) {
        //return OkHttp.string(url, getHeader());
        return OkHttpUtil.string(url, getHeader());
    }

    private String find(String regexStr, String htmlStr) {
        Pattern pattern = Pattern.compile(regexStr, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlStr);
        if (matcher.find()) return matcher.group(1).trim();
        return "";
    }

    private JSONArray parseVodList(String url) throws Exception {
        String html = req(url);
        Elements elements = Jsoup.parse(html).select("[class=v_list] li");
        JSONArray videos = new JSONArray();
        for (Element e : elements) {
            Element item = e.select("a").get(0);
            String vodId = item.attr("href");
            String name = item.attr("title").replaceAll("在线观看", "");
            String pic = item.attr("data-bg");
            String remark = e.select("[class=desc]").text();

            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodId);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }
        return videos;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        List<String> typeIds = Arrays.asList("1", "2", "3", "4");
        List<String> typeNames = Arrays.asList("国产动漫", "日本动漫", "欧美动漫", "电影");
        for (int i = 0; i < typeIds.size(); i++) {
            JSONObject c = new JSONObject();
            c.put("type_id", typeIds.get(i));
            c.put("type_name", typeNames.get(i));
            classes.put(c);
        }
        String f = "{\"1\": [{\"name\": \"年份\", \"key\": \"year\", \"value\": [{\"n\": \"全部年份\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}]}, {\"name\": \"类型\", \"key\": \"class\", \"value\": [{\"n\": \"全部类型\", \"v\": \"\"}, {\"n\": \"奇幻\", \"v\": \"奇幻\"}, {\"n\": \"战斗\", \"v\": \"战斗\"}, {\"n\": \"玄幻\", \"v\": \"玄幻\"}, {\"n\": \"穿越\", \"v\": \"穿越\"}, {\"n\": \"科幻\", \"v\": \"科幻\"}, {\"n\": \"武侠\", \"v\": \"武侠\"}, {\"n\": \"热血\", \"v\": \"热血\"}, {\"n\": \"眈美\", \"v\": \"眈美\"}, {\"n\": \"搞笑\", \"v\": \"搞笑\"}, {\"n\": \"动态漫画\", \"v\": \"动态漫画\"}]}, {\"name\": \"排序\", \"key\": \"by\", \"value\": [{\"n\": \"最新\", \"v\": \"time\"}, {\"n\": \"人气\", \"v\": \"hits\"}, {\"n\": \"评分\", \"v\": \"score\"}]}], \"2\": [{\"name\": \"年份\", \"key\": \"year\", \"value\": [{\"n\": \"全部年份\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}]}, {\"name\": \"类型\", \"key\": \"class\", \"value\": [{\"n\": \"全部类型\", \"v\": \"\"}, {\"n\": \"冒险\", \"v\": \"冒险\"}, {\"n\": \"奇幻\", \"v\": \"奇幻\"}, {\"n\": \"战斗\", \"v\": \"战斗\"}, {\"n\": \"后宫\", \"v\": \"后宫\"}, {\"n\": \"热血\", \"v\": \"热血\"}, {\"n\": \"励志\", \"v\": \"励志\"}, {\"n\": \"搞笑\", \"v\": \"搞笑\"}, {\"n\": \"校园\", \"v\": \"校园\"}, {\"n\": \"机战\", \"v\": \"机战\"}, {\"n\": \"悬疑\", \"v\": \"悬疑\"}, {\"n\": \"治愈\", \"v\": \"治愈\"}, {\"n\": \"百合\", \"v\": \"百合\"}, {\"n\": \"恐怖\", \"v\": \"恐怖\"}, {\"n\": \"泡面番\", \"v\": \"泡面番\"}, {\"n\": \"恋爱\", \"v\": \"恋爱\"}, {\"n\": \"推理\", \"v\": \"推理\"}]}, {\"name\": \"排序\", \"key\": \"by\", \"value\": [{\"n\": \"最新\", \"v\": \"time\"}, {\"n\": \"人气\", \"v\": \"hits\"}, {\"n\": \"评分\", \"v\": \"score\"}]}], \"3\": [{\"name\": \"年份\", \"key\": \"year\", \"value\": [{\"n\": \"全部年份\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}]}, {\"name\": \"类型\", \"key\": \"class\", \"value\": [{\"n\": \"全部类型\", \"v\": \"\"}, {\"n\": \"科幻\", \"v\": \"科幻\"}, {\"n\": \"冒险\", \"v\": \"冒险\"}, {\"n\": \"战斗\", \"v\": \"战斗\"}, {\"n\": \"百合\", \"v\": \"百合\"}, {\"n\": \"奇幻\", \"v\": \"奇幻\"}, {\"n\": \"热血\", \"v\": \"热血\"}, {\"n\": \"搞笑\", \"v\": \"搞笑\"}]}, {\"name\": \"排序\", \"key\": \"by\", \"value\": [{\"n\": \"最新\", \"v\": \"time\"}, {\"n\": \"人气\", \"v\": \"hits\"}, {\"n\": \"评分\", \"v\": \"score\"}]}], \"4\": [{\"name\": \"年份\", \"key\": \"year\", \"value\": [{\"n\": \"全部年份\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}]}, {\"name\": \"类型\", \"key\": \"class\", \"value\": [{\"n\": \"全部类型\", \"v\": \"\"}, {\"n\": \"搞笑\", \"v\": \"搞笑\"}, {\"n\": \"奇幻\", \"v\": \"奇幻\"}, {\"n\": \"治愈\", \"v\": \"治愈\"}, {\"n\": \"科幻\", \"v\": \"科幻\"}, {\"n\": \"喜剧\", \"v\": \"喜剧\"}, {\"n\": \"冒险\", \"v\": \"冒险\"}, {\"n\": \"动作\", \"v\": \"动作\"}, {\"n\": \"爱情\", \"v\": \"爱情\"}]}, {\"name\": \"排序\", \"key\": \"by\", \"value\": [{\"n\": \"最新\", \"v\": \"time\"}, {\"n\": \"人气\", \"v\": \"hits\"}, {\"n\": \"评分\", \"v\": \"score\"}]}]}";
        JSONObject filterConfig = new JSONObject(f);
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", filterConfig);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        // 筛选处理 start
        String year = extend.get("year") == null ? "" : extend.get("year");
        String by = extend.get("by") == null ? "" : extend.get("by");
        String classType = extend.get("class") == null ? "" : extend.get("class");
        // 筛选处理 end

        // https://dm84.tv/show-1--time-战斗--2022-.html
        String cateUrl;
        if (pg.equals("1")) {
            cateUrl = siteUrl + String.format("/show-%s--%s-%s--%s-.html", tid, by, classType, year);
        } else {
            cateUrl = siteUrl + String.format("/show-%s--%s-%s--%s-%s.html", tid, by, classType, year, pg);
        }
        JSONArray videos = parseVodList(cateUrl);
        int page = Integer.parseInt(pg), count = Integer.MAX_VALUE, limit = 36, total = Integer.MAX_VALUE;
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
        String html = req(detailUrl);
        Document doc = Jsoup.parse(html);
        String name = doc.select(".v_title > a").text();
        String pic = doc.select("#v_content > .cover > img").attr("src");
        String typeName = doc.select(".v_desc a").text();
        List<TextNode> textNodes = doc.select(".v_desc").textNodes();
        String year = "";
        String area = "";
        if (textNodes.size() >= 2) {
            year = textNodes.get(0).text();
            area = textNodes.get(1).text();
        }
        String remark = doc.select(".v_desc .desc").text();
        String director = find("导演：(.*?)</p>", html);
        String actor = find("主演：(.*?)</p>", html);
        String description = find("剧情：(.*?)</p>", html);

        Elements sourceList = doc.select(".play_list");
        Elements circuits = doc.select("[class=tab_control play_from] li");
        Map<String, String> playMap = new LinkedHashMap<>();
        for (int i = 0; i < sourceList.size(); i++) {
            String circuitName = circuits.get(i).text();
            List<String> vodItems = new ArrayList<>();
            Elements aList = sourceList.get(i).select("a");
            for (Element a : aList) {
                String episodeUrl = siteUrl + a.attr("href");
                String episodeName = "第" + a.text() + "集";
                vodItems.add(episodeName + "$" + episodeUrl);
            }
            if (vodItems.size() > 0) {
                playMap.put(circuitName, TextUtils.join("#", vodItems));
            }
        }

        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", name); // 影片名称
        vod.put("vod_pic", pic); // 图片
        vod.put("type_name", typeName); // 影片类型 选填
        vod.put("vod_year", year); // 年份 选填
        vod.put("vod_area", area); // 地区 选填
        vod.put("vod_remarks", remark); // 备注 选填
        vod.put("vod_actor", actor); // 主演 选填
        vod.put("vod_director", director); // 导演 选填
        vod.put("vod_content", description); // 简介 选填
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
        String keyWord = URLEncoder.encode(key);
        String searchUrl = siteUrl + "/s----------.html?wd=" + keyWord;
        if (!pg.equals("1")) searchUrl = siteUrl + "/s-" + keyWord + "---------" + pg + ".html";
        JSONArray videos = parseVodList(searchUrl);
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String lastUrl = id;
        String html = req(lastUrl);
        lastUrl = Jsoup.parse(html).select("iframe").attr("src");

        JSONObject result = new JSONObject();
        result.put("parse", 1);
        result.put("header", getHeader().toString());
        result.put("playUrl", "");
        result.put("url", lastUrl);
        return result.toString();
    }
}