package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Demo for spider
 * <p>
 * Author: CatVod
 */
public class WoGG extends Spider {
    private static final String siteUrl = "https://wogg.xyz/";
    private static final String siteHost = "wogg.xyz";

    private Pattern regexCategory = Pattern.compile("/index.php/vodtype/(\\w+).html");
    private Pattern regexVid = Pattern.compile("/index.php/voddetail/(\\d+).html");
    private Pattern regexPlay = Pattern.compile("/index.php/vodplay/(\\d+)-\\S+.html");

    @Override
    public void init(Context context) {
        super.init(context);
    }

    /**
     * 爬虫headers
     *
     * @param url
     * @return
     */
    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        headers.put("Host", siteHost);
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("DNT", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return headers;
    }

    /**
     * 获取分类数据 + 首页最近更新视频列表数据
     *
     * @param filter 是否开启筛选 关联的是 软件设置中 首页数据源里的筛选开关
     * @return
     */
    @Override
    public String homeContent(boolean filter) {
        try {
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl, getHeaders(siteUrl)));
            // 分类节点
            Elements elements = doc.select("ul.nav-menu-items > .nav-menu-item a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                Matcher mather = regexCategory.matcher(ele.attr("href"));
                if (!mather.find())
                    continue;
                // 把分类的id和名称取出来加到列表里
                String id = mather.group(1).trim();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type_id", id);
                jsonObject.put("type_name", name);
                classes.put(jsonObject);
            }
            JSONObject result = new JSONObject();
            result.put("class", classes);
            JSONObject filterConfig = new JSONObject("{\"1\":[{\"key\":\"3\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"爱情\",\"v\":\"爱情\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"}]},{\"key\":\"11\",\"name\":\"年代\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"}]},{\"key\":\"1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"中国香港\",\"v\":\"香港\"},{\"n\":\"中国台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"其他\",\"v\":\"其他\"}]}],\"37\":[{\"key\":\"3\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"爱情\",\"v\":\"爱情\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"}]},{\"key\":\"11\",\"name\":\"年代\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"}]},{\"key\":\"1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"中国香港\",\"v\":\"香港\"},{\"n\":\"中国台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"其他\",\"v\":\"其他\"}]}],\"38\":[{\"key\":\"1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"中国香港\",\"v\":\"香港\"},{\"n\":\"中国台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"11\",\"name\":\"年代\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"}]}],\"20\":[{\"key\":\"11\",\"name\":\"年代\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"}]},{\"key\":\"1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"中国香港\",\"v\":\"香港\"},{\"n\":\"中国台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"其他\",\"v\":\"其他\"}]}],\"28\":[{\"key\":\"1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"内地\",\"v\":\"内地\"},{\"n\":\"日韩\",\"v\":\"日韩\"},{\"n\":\"欧美\",\"v\":\"欧美\"}]}],\"24\":[{\"key\":\"11\",\"name\":\"年代\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"}]},{\"key\":\"1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"欧美\",\"v\":\"欧美\"},{\"n\":\"其他\",\"v\":\"其他\"}]}]}");
            if (filter) result.put("filters", filterConfig);
            try {
                JSONArray videos = new JSONArray();
                // 取首页推荐视频列表
                Elements moduleList = doc.select("div.module-lines-list");
                for (Element element : moduleList) {
                    Elements list = element.select("div.module-item");
                    for (Element vod : list) {
                        Element itemLink = vod.selectFirst("div.module-item-pic>a");
                        String title = itemLink.attr("title");
                        String cover = vod.select("img.lazyloaded").attr("data-src");
                        if (!cover.startsWith("http")) cover = siteUrl + cover;
                        String remark = vod.select("div.module-item-text").text();
                        Matcher matcher = regexVid.matcher(itemLink.attr("href"));
                        if (!matcher.find())
                            continue;
                        String id = matcher.group(1);
                        JSONObject v = new JSONObject();
                        v.put("vod_id", id);
                        v.put("vod_name", title);
                        v.put("vod_pic", cover);
                        v.put("vod_remarks", remark);
                        videos.put(v);
                    }
                }
                result.put("list", videos);
            } catch (Exception e) {
                SpiderDebug.log(e);
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 获取分类信息数据
     *
     * @param tid    分类id
     * @param pg     页数
     * @param filter 同homeContent方法中的filter
     * @param extend 筛选参数{k:v, k1:v1}
     * @return
     */
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            String[] urlParams = new String[]{"", "", "", "", "", "", "", "", "", "", "", ""};
            urlParams[0] = tid;
            urlParams[8] = pg;
            if (extend != null && extend.size() > 0) {
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    urlParams[Integer.parseInt(key)] = URLEncoder.encode(value);
                }
            }
            // 获取分类数据的url
            String url = siteUrl + "index.php/vodshow/" + TextUtils.join("-", urlParams) + ".html";
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 0;
            int limit = 0;
            // 取页码相关信息
            Element footer = doc.selectFirst("div.module-footer");
            if (footer != null) {
                Element pageLink = footer.select("a").last();
                if (pageLink != null) {
                    Matcher matcherPage = Pattern.compile("-(\\d+)-").matcher(pageLink.attr("href"));
                    if (matcherPage.find()) {
                        pageCount = Integer.parseInt(matcherPage.group(1));
                    }
                }
            }
            JSONArray videos = new JSONArray();
            Elements list = doc.select("div.module-item");
            for (Element vod : list) {
                Element vodLink = vod.selectFirst("div.module-item-pic>a");
                String title = vodLink.attr("title");
                String cover = vod.selectFirst("img.lazyloaded").attr("data-src");
                if (!cover.startsWith("http")) cover = siteUrl + cover;
                String remark = vod.selectFirst("div.module-item-text").text();
                Matcher matcher = regexVid.matcher(vodLink.attr("href"));
                if (!matcher.find()) continue;
                JSONObject v = new JSONObject();
                v.put("vod_id", matcher.group(1));
                v.put("vod_name", title);
                v.put("vod_pic", cover);
                v.put("vod_remarks", remark);
                videos.put(v);
            }
            limit = videos.length();
            if (limit > 0) {
                limit = 72;
            }
            result.put("page", pg);
            result.put("pagecount", pageCount);
            result.put("limit", limit);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * limit);
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 视频详情信息
     *
     * @param ids 视频id
     * @return
     */
    @Override
    public String detailContent(List<String> ids) throws Exception {
        String url = siteUrl + "index.php/voddetail/" + ids.get(0) + ".html";
        Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
        Elements items = doc.select("div.module-row-info > a");
        if (items.size() == 0) return "";
        List<String> ids_ = new ArrayList<>();
        for (Element item : items) {
            ids_.add(item.attr("data-clipboard-text"));
        }
        return super.detailContent(ids_);
    }


    /**
     * 获取视频播放信息
     *
     * @param flag     播放源
     * @param id       视频id
     * @param vipFlags 所有可能需要vip解析的源
     * @return
     */
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        return super.playerContent(flag, id, vipFlags);
    }

    /**
     * 搜索
     *
     * @param key
     * @param quick 是否播放页的快捷搜索
     * @return
     */
    @Override
    public String searchContent(String key, boolean quick) {
        try {
            if (quick)
                return "";
            String url = siteUrl + "index.php/vodsearch/-------------.html?wd=" + URLEncoder.encode(key, "UTF-8");
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            Elements list = doc.select("div.module-search-item");
            for (Element vod : list) {
                String cover = vod.select("div.module-item-pic img").attr("data-src");
                if (!cover.startsWith("http")) cover = siteUrl + cover;
                String title = vod.select("div.module-item-pic img").attr("alt");
                Matcher matcher = regexPlay.matcher(vod.select("div.module-item-pic>a").get(0).attr("href"));
                if (!matcher.find())
                    continue;
                String id = matcher.group(1);
                JSONObject v = new JSONObject();
                v.put("vod_id", id);
                v.put("vod_name", title);
                v.put("vod_pic", cover);
                v.put("vod_remarks", "");
                videos.put(v);
            }
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}