package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
//import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.okhttp.OkHttpUtil;

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
 * @author Qile
 */
public class Cs1369 extends Spider {

    private final String siteUrl = "https://www.cs1369.com";

    private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:102.0) Gecko/20100101 Firefox/102.0";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        return header;
    }

    private String req(String url, Map<String, String> header) {
//        return OkHttp.string(url, header);
        return OkHttpUtil.string(url, header);
    }

    private String find(Pattern pattern, String html) {
        Matcher m = pattern.matcher(html);
        return m.find() ? m.group(1).trim() : "";
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        List<String> typeIds = Arrays.asList("1", "2", "3", "4");
        List<String> typeNames = Arrays.asList("电影", "电视剧", "动漫", "爽文短剧");
        for (int i = 0; i < typeIds.size(); i++) {
            JSONObject c = new JSONObject();
            c.put("type_id", typeIds.get(i));
            c.put("type_name", typeNames.get(i));
            classes.put(c);
        }
        String f = "{\"1\": [{\"key\": \"cateId\", \"name\": \"类型\", \"value\": [{\"n\": \"全部\", \"v\": \"1\"}, {\"n\": \"动作\", \"v\": \"6\"}, {\"n\": \"喜剧\", \"v\": \"7\"}, {\"n\": \"爱情\", \"v\": \"8\"}, {\"n\": \"科幻\", \"v\": \"9\"}, {\"n\": \"恐怖\", \"v\": \"10\"}, {\"n\": \"剧情\", \"v\": \"11\"}, {\"n\": \"战争\", \"v\": \"12\"}, {\"n\": \"动画\", \"v\": \"13\"}, {\"n\": \"记录\", \"v\": \"14\"}]}, {\"key\": \"class\", \"name\": \"剧情\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"喜剧\", \"v\": \"class/喜剧\"}, {\"n\": \"爱情\", \"v\": \"class/爱情\"}, {\"n\": \"恐怖\", \"v\": \"恐怖\"}, {\"n\": \"动作\", \"v\": \"class/动作\"}, {\"n\": \"科幻\", \"v\": \"class/科幻\"}, {\"n\": \"剧情\", \"v\": \"class/剧情\"}, {\"n\": \"战争\", \"v\": \"class/战争\"}, {\"n\": \"警匪\", \"v\": \"class/警匪\"}, {\"n\": \"犯罪\", \"v\": \"class/犯罪\"}, {\"n\": \"动画\", \"v\": \"class/动画\"}, {\"n\": \"奇幻\", \"v\": \"class/奇幻\"}, {\"n\": \"武侠\", \"v\": \"class/武侠\"}, {\"n\": \"冒险\", \"v\": \"class/冒险\"}, {\"n\": \"枪战\", \"v\": \"class/枪战\"}, {\"n\": \"恐怖\", \"v\": \"class/恐怖\"}, {\"n\": \"悬疑\", \"v\": \"class/悬疑\"}, {\"n\": \"惊悚\", \"v\": \"class/惊悚\"}, {\"n\": \"经典\", \"v\": \"class/经典\"}, {\"n\": \"青春\", \"v\": \"class/青春\"}, {\"n\": \"文艺\", \"v\": \"class/文艺\"}, {\"n\": \"微电影\", \"v\": \"class/微电影\"}, {\"n\": \"古装\", \"v\": \"class/古装\"}, {\"n\": \"历史\", \"v\": \"class/历史\"}, {\"n\": \"运动\", \"v\": \"class/运动\"}, {\"n\": \"农村\", \"v\": \"class/农村\"}, {\"n\": \"儿童\", \"v\": \"class/儿童\"}, {\"n\": \"网络电影\", \"v\": \"class/网络电影\"}]}, {\"key\": \"area\", \"name\": \"按地区\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"大陆\", \"v\": \"area/中国大陆\"}, {\"n\": \"香港\", \"v\": \"area/中国香港\"}, {\"n\": \"台湾\", \"v\": \"area/中国台湾\"}, {\"n\": \"美国\", \"v\": \"area/美国\"}, {\"n\": \"日本\", \"v\": \"area/日本\"}, {\"n\": \"韩国\", \"v\": \"area/韩国\"}, {\"n\": \"其他\", \"v\": \"area/其他\"}]}, {\"key\": \"year\", \"name\": \"年份\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"year/2024\"}, {\"n\": \"2023\", \"v\": \"year/2023\"}, {\"n\": \"2022\", \"v\": \"year/2022\"}, {\"n\": \"2021\", \"v\": \"year/2021\"}, {\"n\": \"2020\", \"v\": \"year/2020\"}, {\"n\": \"2019\", \"v\": \"year/2019\"}, {\"n\": \"2018\", \"v\": \"year/2018\"}, {\"n\": \"2017\", \"v\": \"year/2017\"}, {\"n\": \"2016\", \"v\": \"year/2016\"}, {\"n\": \"2015\", \"v\": \"year/2015\"}, {\"n\": \"2014\", \"v\": \"year/2014\"}, {\"n\": \"2013\", \"v\": \"year/2013\"}, {\"n\": \"2012\", \"v\": \"year/2012\"}, {\"n\": \"2011\", \"v\": \"year/2011\"}, {\"n\": \"2010\", \"v\": \"year/2010\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"时间\", \"v\": \"by/time\"}, {\"n\": \"人气\", \"v\": \"by/hits\"}, {\"n\": \"评分\", \"v\": \"by/score\"}]}], \"2\": [{\"key\": \"class\", \"name\": \"剧情\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"古装\", \"v\": \"class/古装\"}, {\"n\": \"战争\", \"v\": \"class/战争\"}, {\"n\": \"青春偶像\", \"v\": \"class/青春偶像\"}, {\"n\": \"喜剧\", \"v\": \"class/喜剧\"}, {\"n\": \"家庭\", \"v\": \"class/家庭\"}, {\"n\": \"犯罪\", \"v\": \"class/犯罪\"}, {\"n\": \"动作\", \"v\": \"class/动作\"}, {\"n\": \"奇幻\", \"v\": \"class/奇幻\"}, {\"n\": \"剧情\", \"v\": \"class/剧情\"}, {\"n\": \"历史\", \"v\": \"class/历史\"}, {\"n\": \"经典\", \"v\": \"class/经典\"}, {\"n\": \"乡村\", \"v\": \"class/乡村\"}, {\"n\": \"情景\", \"v\": \"class/情景\"}, {\"n\": \"商战\", \"v\": \"class/商战\"}, {\"n\": \"网剧\", \"v\": \"class/网剧\"}, {\"n\": \"其他\", \"v\": \"class/其他\"}]}, {\"key\": \"area\", \"name\": \"按地区\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"大陆\", \"v\": \"area/中国大陆\"}, {\"n\": \"香港\", \"v\": \"area/中国香港\"}, {\"n\": \"台湾\", \"v\": \"area/中国台湾\"}, {\"n\": \"美国\", \"v\": \"area/美国\"}, {\"n\": \"日本\", \"v\": \"area/日本\"}, {\"n\": \"韩国\", \"v\": \"area/韩国\"}, {\"n\": \"其他\", \"v\": \"area/其他\"}]}, {\"key\": \"year\", \"name\": \"年份\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"year/2024\"}, {\"n\": \"2023\", \"v\": \"year/2023\"}, {\"n\": \"2022\", \"v\": \"year/2022\"}, {\"n\": \"2021\", \"v\": \"year/2021\"}, {\"n\": \"2020\", \"v\": \"year/2020\"}, {\"n\": \"2019\", \"v\": \"year/2019\"}, {\"n\": \"2018\", \"v\": \"year/2018\"}, {\"n\": \"2017\", \"v\": \"year/2017\"}, {\"n\": \"2016\", \"v\": \"year/2016\"}, {\"n\": \"2015\", \"v\": \"year/2015\"}, {\"n\": \"2014\", \"v\": \"year/2014\"}, {\"n\": \"2013\", \"v\": \"year/2013\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"时间\", \"v\": \"by/time\"}, {\"n\": \"人气\", \"v\": \"by/hits\"}, {\"n\": \"评分\", \"v\": \"by/score\"}]}], \"3\": [{\"key\": \"cateId\", \"name\": \"类型\", \"value\": [{\"n\": \"全部\", \"v\": \"3\"}, {\"n\": \"国产动漫\", \"v\": \"25\"}, {\"n\": \"日韩动漫\", \"v\": \"26\"}, {\"n\": \"欧美动漫\", \"v\": \"27\"}, {\"n\": \"其他\", \"v\": \"28\"}]}, {\"key\": \"class\", \"name\": \"剧情\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"情感\", \"v\": \"class/情感\"}, {\"n\": \"科幻\", \"v\": \"class/科幻\"}, {\"n\": \"热血\", \"v\": \"class/热血\"}, {\"n\": \"推理\", \"v\": \"class/推理\"}, {\"n\": \"搞笑\", \"v\": \"class/搞笑\"}, {\"n\": \"冒险\", \"v\": \"class/冒险\"}, {\"n\": \"萝莉\", \"v\": \"class/萝莉\"}, {\"n\": \"校园\", \"v\": \"class/校园\"}, {\"n\": \"动作\", \"v\": \"class/动作\"}, {\"n\": \"机战\", \"v\": \"class/机战\"}, {\"n\": \"运动\", \"v\": \"class/运动\"}, {\"n\": \"战争\", \"v\": \"class/战争\"}, {\"n\": \"少年\", \"v\": \"class/少年\"}, {\"n\": \"少女\", \"v\": \"class/少女\"}, {\"n\": \"社会\", \"v\": \"class/社会\"}, {\"n\": \"原创\", \"v\": \"class/原创\"}, {\"n\": \"亲子\", \"v\": \"class/亲子\"}, {\"n\": \"益智\", \"v\": \"class/益智\"}, {\"n\": \"励志\", \"v\": \"class/励志\"}, {\"n\": \"其他\", \"v\": \"class/其他\"}]}, {\"key\": \"area\", \"name\": \"按地区\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"大陆\", \"v\": \"area/中国大陆\"}, {\"n\": \"香港\", \"v\": \"area/中国香港\"}, {\"n\": \"台湾\", \"v\": \"area/中国台湾\"}, {\"n\": \"美国\", \"v\": \"area/美国\"}, {\"n\": \"日本\", \"v\": \"area/日本\"}, {\"n\": \"韩国\", \"v\": \"area/韩国\"}, {\"n\": \"其他\", \"v\": \"area/其他\"}]}, {\"key\": \"year\", \"name\": \"年份\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"year/2024\"}, {\"n\": \"2023\", \"v\": \"year/2023\"}, {\"n\": \"2022\", \"v\": \"year/2022\"}, {\"n\": \"2021\", \"v\": \"year/2021\"}, {\"n\": \"2020\", \"v\": \"year/2020\"}, {\"n\": \"2019\", \"v\": \"year/2019\"}, {\"n\": \"2018\", \"v\": \"year/2018\"}, {\"n\": \"2017\", \"v\": \"year/2017\"}, {\"n\": \"2016\", \"v\": \"year/2016\"}, {\"n\": \"2015\", \"v\": \"year/2015\"}, {\"n\": \"2014\", \"v\": \"year/2014\"}, {\"n\": \"2013\", \"v\": \"year/2013\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"时间\", \"v\": \"by/time\"}, {\"n\": \"人气\", \"v\": \"by/hits\"}, {\"n\": \"评分\", \"v\": \"by/score\"}]}]}";
        JSONObject filterConfig = new JSONObject(f);
        Document doc = Jsoup.parse(req(siteUrl, getHeader()));
        JSONArray videos = new JSONArray();
        Elements items = doc.select(".stui-vodlist.clearfix").eq(0).select(".stui-vodlist__box");
        for (Element li : items) {
            String vid = siteUrl + li.select("a").attr("href");
            String name = li.select("a").attr("title");
//            String pic = li.select("a").attr("data-original") + "@Referer=https://api.douban.com/@User-Agent="+userAgent;
            String pic = li.select("a").attr("data-original");
            String remark = li.select("a").select(".pic-text.text-right").text();

            JSONObject vod = new JSONObject();
            vod.put("vod_id", vid);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", filterConfig);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cateId = extend.get("cateId") == null ? tid : extend.get("cateId");
        String area = extend.get("area") == null ? "" : extend.get("area");
        String year = extend.get("year") == null ? "" : extend.get("year");
        String by = extend.get("by") == null ? "" : extend.get("by");
        String classType = extend.get("class") == null ? "" : extend.get("class");
        String cateUrl = siteUrl + String.format("/show/%s/%s/%s/id/%s/%s/page/%s.html", area, by, classType, cateId, year, pg);
        Document doc = Jsoup.parse(req(cateUrl, getHeader()));
        JSONArray videos = new JSONArray();
        for (Element li : doc.select(".stui-vodlist__box")) {
            String vid = siteUrl + li.select("a").attr("href");
            String name = li.select("a").attr("title");
//            String pic = li.select("a").attr("data-original") + "@Referer=https://api.douban.com/@User-Agent=" + userAgent;
            String pic = li.select("a").attr("data-original");
            String remark = li.select("a").select(".pic-text.text-right").text();

            JSONObject vod = new JSONObject();
            vod.put("vod_id", vid);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }
        int page = Integer.parseInt(pg), count = Integer.MAX_VALUE, limit = videos.length(), total = Integer.MAX_VALUE;
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
        Document doc = Jsoup.parse(req(ids.get(0), getHeader()));
        List<String> vodItems = new ArrayList<>();
        Elements sourceList = doc.select(".stui-content__playlist.clearfix li a");
        for (Element a : sourceList) {
            String episodeUrl = siteUrl + a.attr("href");
            String episodeName = a.text();
            vodItems.add(episodeName + "$" + episodeUrl);
        }
        Map<String, String> playMap = new LinkedHashMap<>();
        if (vodItems.size() > 0) playMap.put("Qile", String.join("#", vodItems));

        String title = doc.select("h1.title").first().ownText();
        String pic = doc.select("img.lazyload").attr("data-original") + "@Referer=https://api.douban.com/@User-Agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36";
        String remark = doc.select(".data.hidden-sm").text();
        String brief = doc.select("p.col-pd").text();
        Elements elements = doc.select("p.data");
        String classifyName = "";
        String actor = "";
        String director = "";
        for (Element element : elements) {
            String text = element.text();
            if (text.startsWith("类型：")) {
                classifyName = element.select("a").text();
            } else if (text.startsWith("导演：")) {
                director = element.select("a").text();
            } else if (text.startsWith("主演：")) {
                actor = element.select("a").text();
            }
        }

        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", title); // 影片名称
        vod.put("vod_pic", pic); // 图片
        vod.put("type_name", classifyName); // 影片类型 选填
//        vod.put("vod_year", year); // 年份 选填
//        vod.put("vod_area", area); // 地区 选填
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
        String searchUrl = siteUrl + "/search.html?wd=" + URLEncoder.encode(key);
        Document doc = Jsoup.parse(req(searchUrl, getHeader()));
        JSONArray videos = new JSONArray();
        for (Element li : doc.select("div.thumb")) {
            String vid = siteUrl + li.select("a").attr("href");
            String name = li.select("a").attr("title");
            String pic = li.select("a").attr("data-original") + "@Referer=https://api.douban.com/@User-Agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36";
            String remark = li.select(".pic-text.text-right").text();

            JSONObject vod = new JSONObject();
            vod.put("vod_id", vid);
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
        String content = req(id, getHeader());
        String json = find(Pattern.compile("player_aaaa=(.*?)</script>"), content);
        JSONObject player = new JSONObject(json);
//        String realUrl = URLDecoder.decode(new String(Base64.decode(player.getString("url").getBytes(), 0)));
        String realUrl = player.getString("url");

        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("header", getHeader().toString());
        result.put("playUrl", "");
        result.put("url", realUrl);
        return result.toString();
    }
}