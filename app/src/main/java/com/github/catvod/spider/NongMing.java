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
 * 农民影视
 */
public class NongMing extends Spider {

    private final String siteUrl = "https://m.xiangdao.me";
    private final String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Mobile/15E148 Safari/604.1";

    private String req(String url, Map<String, String> header) {
//        return OkHttp.string(url, header);
        return OkHttpUtil.string(url, header);
    }

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", userAgent);
        return header;
    }

    private String getRemark(String html) {
        return find(Pattern.compile("状态:&nbsp;(.*?)</div"), html)
                .replaceAll("</?[^>]+>", "");
    }

    private String getDirector(String html) {
        return find(Pattern.compile("导演:&nbsp;(.*?)</div"), html)
                .replaceAll("</?[^>]+>", "")
                .replaceAll("&nbsp;&nbsp;", "")
                .replaceAll("&nbsp;", ",");
    }

    private String getActor(String html) {
        return find(Pattern.compile("主演:&nbsp;(.*?)</div"), html)
                .replaceAll("</?[^>]+>", "")
                .replaceAll("&nbsp;&nbsp;", "")
                .replaceAll("&nbsp;", ",");
    }

    private String find(Pattern pattern, String html) {
        Matcher matcher = pattern.matcher(html);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String getYear(String html) {
        return find(Pattern.compile("年代:.*?<a.*?>(.*?)</a>"), html);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        List<String> typeIds = Arrays.asList("1", "2", "3", "4");
        List<String> typeNames = Arrays.asList("电影", "电视剧", "综艺", "动漫");
        for (int i = 0; i < typeIds.size(); i++) {
            JSONObject c = new JSONObject();
            c.put("type_id", typeIds.get(i));
            c.put("type_name", typeNames.get(i));
            classes.put(c);
        }
        String f = "{\"1\": [{\"key\": \"cateId\", \"name\": \"类型\", \"value\": [{\"n\": \"全部\", \"v\": \"1\"}, {\"n\": \"动作片\", \"v\": \"5\"}, {\"n\": \"喜剧片\", \"v\": \"6\"}, {\"n\": \"爱情片\", \"v\": \"7\"}, {\"n\": \"科幻片\", \"v\": \"8\"}, {\"n\": \"恐怖片\", \"v\": \"9\"}, {\"n\": \"剧情片\", \"v\": \"10\"}, {\"n\": \"战争片\", \"v\": \"11\"}, {\"n\": \"惊悚片\", \"v\": \"16\"}, {\"n\": \"奇幻片\", \"v\": \"17\"}]}, {\"key\": \"area\", \"name\": \"地区\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"大陆\", \"v\": \"大陆\"}, {\"n\": \"香港\", \"v\": \"香港\"}, {\"n\": \"台湾\", \"v\": \"台湾\"}, {\"n\": \"美国\", \"v\": \"美国\"}, {\"n\": \"韩国\", \"v\": \"韩国\"}, {\"n\": \"日本\", \"v\": \"日本\"}, {\"n\": \"泰国\", \"v\": \"泰国\"}, {\"n\": \"新加坡\", \"v\": \"新加坡\"}, {\"n\": \"马来西亚\", \"v\": \"马来西亚\"}, {\"n\": \"印度\", \"v\": \"印度\"}, {\"n\": \"英国\", \"v\": \"英国\"}, {\"n\": \"法国\", \"v\": \"法国\"}, {\"n\": \"加拿大\", \"v\": \"加拿大\"}, {\"n\": \"西班牙\", \"v\": \"西班牙\"}, {\"n\": \"俄罗斯\", \"v\": \"俄罗斯\"}, {\"n\": \"其它\", \"v\": \"其它\"}]}, {\"key\": \"year\", \"name\": \"年代\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}, {\"n\": \"2014\", \"v\": \"2014\"}, {\"n\": \"2013\", \"v\": \"2013\"}, {\"n\": \"2012\", \"v\": \"2012\"}, {\"n\": \"2011\", \"v\": \"2011\"}, {\"n\": \"2010\", \"v\": \"2010\"}, {\"n\": \"2009\", \"v\": \"2009\"}, {\"n\": \"2008\", \"v\": \"2008\"}, {\"n\": \"2007\", \"v\": \"2007\"}, {\"n\": \"2006\", \"v\": \"2006\"}, {\"n\": \"2005\", \"v\": \"2005\"}, {\"n\": \"2004\", \"v\": \"2004\"}, {\"n\": \"2003\", \"v\": \"2003\"}, {\"n\": \"2002\", \"v\": \"2002\"}, {\"n\": \"2001\", \"v\": \"2001\"}, {\"n\": \"2000\", \"v\": \"2000\"}, {\"n\": \"1999\", \"v\": \"1999\"}, {\"n\": \"1998\", \"v\": \"1998\"}, {\"n\": \"1997\", \"v\": \"1997\"}, {\"n\": \"1996\", \"v\": \"1996\"}, {\"n\": \"1995\", \"v\": \"1995\"}, {\"n\": \"1994\", \"v\": \"1994\"}, {\"n\": \"1993\", \"v\": \"1993\"}, {\"n\": \"1992\", \"v\": \"1992\"}, {\"n\": \"1991\", \"v\": \"1991\"}, {\"n\": \"1990\", \"v\": \"1990\"}, {\"n\": \"1989\", \"v\": \"1989\"}, {\"n\": \"1988\", \"v\": \"1988\"}, {\"n\": \"1987\", \"v\": \"1987\"}, {\"n\": \"1986\", \"v\": \"1986\"}, {\"n\": \"1985\", \"v\": \"1985\"}, {\"n\": \"1984\", \"v\": \"1984\"}, {\"n\": \"1983\", \"v\": \"1983\"}, {\"n\": \"1982\", \"v\": \"1982\"}, {\"n\": \"1981\", \"v\": \"1981\"}, {\"n\": \"1980\", \"v\": \"1980\"}, {\"n\": \"1979\", \"v\": \"1979\"}, {\"n\": \"1978\", \"v\": \"1978\"}, {\"n\": \"1977\", \"v\": \"1977\"}, {\"n\": \"1976\", \"v\": \"1976\"}, {\"n\": \"1975\", \"v\": \"1975\"}, {\"n\": \"1974\", \"v\": \"1974\"}, {\"n\": \"1973\", \"v\": \"1973\"}, {\"n\": \"1972\", \"v\": \"1972\"}, {\"n\": \"1971\", \"v\": \"1971\"}, {\"n\": \"1970\", \"v\": \"1970\"}, {\"n\": \"1969\", \"v\": \"1969\"}, {\"n\": \"1968\", \"v\": \"1968\"}, {\"n\": \"1967\", \"v\": \"1967\"}, {\"n\": \"1966\", \"v\": \"1966\"}, {\"n\": \"1965\", \"v\": \"1965\"}, {\"n\": \"1964\", \"v\": \"1964\"}, {\"n\": \"1963\", \"v\": \"1963\"}, {\"n\": \"1962\", \"v\": \"1962\"}, {\"n\": \"1961\", \"v\": \"1961\"}, {\"n\": \"1960\", \"v\": \"1960\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"时间\", \"v\": \"time\"}, {\"n\": \"人气\", \"v\": \"hits\"}, {\"n\": \"评分\", \"v\": \"score\"}]}], \"2\": [{\"key\": \"cateId\", \"name\": \"类型\", \"value\": [{\"n\": \"全部\", \"v\": \"2\"}, {\"n\": \"国产剧\", \"v\": \"12\"}, {\"n\": \"港台泰\", \"v\": \"13\"}, {\"n\": \"日韩剧\", \"v\": \"14\"}, {\"n\": \"欧美剧\", \"v\": \"15\"}]}, {\"key\": \"area\", \"name\": \"地区\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"大陆\", \"v\": \"大陆\"}, {\"n\": \"香港\", \"v\": \"香港\"}, {\"n\": \"台湾\", \"v\": \"台湾\"}, {\"n\": \"美国\", \"v\": \"美国\"}, {\"n\": \"韩国\", \"v\": \"韩国\"}, {\"n\": \"日本\", \"v\": \"日本\"}, {\"n\": \"泰国\", \"v\": \"泰国\"}, {\"n\": \"新加坡\", \"v\": \"新加坡\"}, {\"n\": \"马来西亚\", \"v\": \"马来西亚\"}, {\"n\": \"印度\", \"v\": \"印度\"}, {\"n\": \"英国\", \"v\": \"英国\"}, {\"n\": \"法国\", \"v\": \"法国\"}, {\"n\": \"加拿大\", \"v\": \"加拿大\"}, {\"n\": \"西班牙\", \"v\": \"西班牙\"}, {\"n\": \"俄罗斯\", \"v\": \"俄罗斯\"}, {\"n\": \"其它\", \"v\": \"其它\"}]}, {\"key\": \"year\", \"name\": \"年代\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}, {\"n\": \"2014\", \"v\": \"2014\"}, {\"n\": \"2013\", \"v\": \"2013\"}, {\"n\": \"2012\", \"v\": \"2012\"}, {\"n\": \"2011\", \"v\": \"2011\"}, {\"n\": \"2010\", \"v\": \"2010\"}, {\"n\": \"2009\", \"v\": \"2009\"}, {\"n\": \"2008\", \"v\": \"2008\"}, {\"n\": \"2007\", \"v\": \"2007\"}, {\"n\": \"2006\", \"v\": \"2006\"}, {\"n\": \"2005\", \"v\": \"2005\"}, {\"n\": \"2004\", \"v\": \"2004\"}, {\"n\": \"2003\", \"v\": \"2003\"}, {\"n\": \"2002\", \"v\": \"2002\"}, {\"n\": \"2001\", \"v\": \"2001\"}, {\"n\": \"2000\", \"v\": \"2000\"}, {\"n\": \"1999\", \"v\": \"1999\"}, {\"n\": \"1998\", \"v\": \"1998\"}, {\"n\": \"1997\", \"v\": \"1997\"}, {\"n\": \"1996\", \"v\": \"1996\"}, {\"n\": \"1995\", \"v\": \"1995\"}, {\"n\": \"1994\", \"v\": \"1994\"}, {\"n\": \"1993\", \"v\": \"1993\"}, {\"n\": \"1992\", \"v\": \"1992\"}, {\"n\": \"1991\", \"v\": \"1991\"}, {\"n\": \"1990\", \"v\": \"1990\"}, {\"n\": \"1989\", \"v\": \"1989\"}, {\"n\": \"1988\", \"v\": \"1988\"}, {\"n\": \"1987\", \"v\": \"1987\"}, {\"n\": \"1986\", \"v\": \"1986\"}, {\"n\": \"1985\", \"v\": \"1985\"}, {\"n\": \"1984\", \"v\": \"1984\"}, {\"n\": \"1983\", \"v\": \"1983\"}, {\"n\": \"1982\", \"v\": \"1982\"}, {\"n\": \"1981\", \"v\": \"1981\"}, {\"n\": \"1980\", \"v\": \"1980\"}, {\"n\": \"1979\", \"v\": \"1979\"}, {\"n\": \"1978\", \"v\": \"1978\"}, {\"n\": \"1977\", \"v\": \"1977\"}, {\"n\": \"1976\", \"v\": \"1976\"}, {\"n\": \"1975\", \"v\": \"1975\"}, {\"n\": \"1974\", \"v\": \"1974\"}, {\"n\": \"1973\", \"v\": \"1973\"}, {\"n\": \"1972\", \"v\": \"1972\"}, {\"n\": \"1971\", \"v\": \"1971\"}, {\"n\": \"1970\", \"v\": \"1970\"}, {\"n\": \"1969\", \"v\": \"1969\"}, {\"n\": \"1968\", \"v\": \"1968\"}, {\"n\": \"1967\", \"v\": \"1967\"}, {\"n\": \"1966\", \"v\": \"1966\"}, {\"n\": \"1965\", \"v\": \"1965\"}, {\"n\": \"1964\", \"v\": \"1964\"}, {\"n\": \"1963\", \"v\": \"1963\"}, {\"n\": \"1962\", \"v\": \"1962\"}, {\"n\": \"1961\", \"v\": \"1961\"}, {\"n\": \"1960\", \"v\": \"1960\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"时间\", \"v\": \"time\"}, {\"n\": \"人气\", \"v\": \"hits\"}, {\"n\": \"评分\", \"v\": \"score\"}]}], \"3\": [{\"key\": \"area\", \"name\": \"地区\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"大陆\", \"v\": \"大陆\"}, {\"n\": \"香港\", \"v\": \"香港\"}, {\"n\": \"台湾\", \"v\": \"台湾\"}, {\"n\": \"美国\", \"v\": \"美国\"}, {\"n\": \"韩国\", \"v\": \"韩国\"}, {\"n\": \"日本\", \"v\": \"日本\"}, {\"n\": \"泰国\", \"v\": \"泰国\"}, {\"n\": \"新加坡\", \"v\": \"新加坡\"}, {\"n\": \"马来西亚\", \"v\": \"马来西亚\"}, {\"n\": \"印度\", \"v\": \"印度\"}, {\"n\": \"英国\", \"v\": \"英国\"}, {\"n\": \"法国\", \"v\": \"法国\"}, {\"n\": \"加拿大\", \"v\": \"加拿大\"}, {\"n\": \"西班牙\", \"v\": \"西班牙\"}, {\"n\": \"俄罗斯\", \"v\": \"俄罗斯\"}, {\"n\": \"其它\", \"v\": \"其它\"}]}, {\"key\": \"year\", \"name\": \"年代\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}, {\"n\": \"2014\", \"v\": \"2014\"}, {\"n\": \"2013\", \"v\": \"2013\"}, {\"n\": \"2012\", \"v\": \"2012\"}, {\"n\": \"2011\", \"v\": \"2011\"}, {\"n\": \"2010\", \"v\": \"2010\"}, {\"n\": \"2009\", \"v\": \"2009\"}, {\"n\": \"2008\", \"v\": \"2008\"}, {\"n\": \"2007\", \"v\": \"2007\"}, {\"n\": \"2006\", \"v\": \"2006\"}, {\"n\": \"2005\", \"v\": \"2005\"}, {\"n\": \"2004\", \"v\": \"2004\"}, {\"n\": \"2003\", \"v\": \"2003\"}, {\"n\": \"2002\", \"v\": \"2002\"}, {\"n\": \"2001\", \"v\": \"2001\"}, {\"n\": \"2000\", \"v\": \"2000\"}, {\"n\": \"1999\", \"v\": \"1999\"}, {\"n\": \"1998\", \"v\": \"1998\"}, {\"n\": \"1997\", \"v\": \"1997\"}, {\"n\": \"1996\", \"v\": \"1996\"}, {\"n\": \"1995\", \"v\": \"1995\"}, {\"n\": \"1994\", \"v\": \"1994\"}, {\"n\": \"1993\", \"v\": \"1993\"}, {\"n\": \"1992\", \"v\": \"1992\"}, {\"n\": \"1991\", \"v\": \"1991\"}, {\"n\": \"1990\", \"v\": \"1990\"}, {\"n\": \"1989\", \"v\": \"1989\"}, {\"n\": \"1988\", \"v\": \"1988\"}, {\"n\": \"1987\", \"v\": \"1987\"}, {\"n\": \"1986\", \"v\": \"1986\"}, {\"n\": \"1985\", \"v\": \"1985\"}, {\"n\": \"1984\", \"v\": \"1984\"}, {\"n\": \"1983\", \"v\": \"1983\"}, {\"n\": \"1982\", \"v\": \"1982\"}, {\"n\": \"1981\", \"v\": \"1981\"}, {\"n\": \"1980\", \"v\": \"1980\"}, {\"n\": \"1979\", \"v\": \"1979\"}, {\"n\": \"1978\", \"v\": \"1978\"}, {\"n\": \"1977\", \"v\": \"1977\"}, {\"n\": \"1976\", \"v\": \"1976\"}, {\"n\": \"1975\", \"v\": \"1975\"}, {\"n\": \"1974\", \"v\": \"1974\"}, {\"n\": \"1973\", \"v\": \"1973\"}, {\"n\": \"1972\", \"v\": \"1972\"}, {\"n\": \"1971\", \"v\": \"1971\"}, {\"n\": \"1970\", \"v\": \"1970\"}, {\"n\": \"1969\", \"v\": \"1969\"}, {\"n\": \"1968\", \"v\": \"1968\"}, {\"n\": \"1967\", \"v\": \"1967\"}, {\"n\": \"1966\", \"v\": \"1966\"}, {\"n\": \"1965\", \"v\": \"1965\"}, {\"n\": \"1964\", \"v\": \"1964\"}, {\"n\": \"1963\", \"v\": \"1963\"}, {\"n\": \"1962\", \"v\": \"1962\"}, {\"n\": \"1961\", \"v\": \"1961\"}, {\"n\": \"1960\", \"v\": \"1960\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"时间\", \"v\": \"time\"}, {\"n\": \"人气\", \"v\": \"hits\"}, {\"n\": \"评分\", \"v\": \"score\"}]}], \"4\": [{\"key\": \"cateId\", \"name\": \"类型\", \"value\": [{\"n\": \"全部\", \"v\": \"4\"}, {\"n\": \"动漫剧\", \"v\": \"18\"}, {\"n\": \"动漫片\", \"v\": \"19\"}]}, {\"key\": \"area\", \"name\": \"地区\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"大陆\", \"v\": \"大陆\"}, {\"n\": \"香港\", \"v\": \"香港\"}, {\"n\": \"台湾\", \"v\": \"台湾\"}, {\"n\": \"美国\", \"v\": \"美国\"}, {\"n\": \"韩国\", \"v\": \"韩国\"}, {\"n\": \"日本\", \"v\": \"日本\"}, {\"n\": \"泰国\", \"v\": \"泰国\"}, {\"n\": \"新加坡\", \"v\": \"新加坡\"}, {\"n\": \"马来西亚\", \"v\": \"马来西亚\"}, {\"n\": \"印度\", \"v\": \"印度\"}, {\"n\": \"英国\", \"v\": \"英国\"}, {\"n\": \"法国\", \"v\": \"法国\"}, {\"n\": \"加拿大\", \"v\": \"加拿大\"}, {\"n\": \"西班牙\", \"v\": \"西班牙\"}, {\"n\": \"俄罗斯\", \"v\": \"俄罗斯\"}, {\"n\": \"其它\", \"v\": \"其它\"}]}, {\"key\": \"year\", \"name\": \"年代\", \"value\": [{\"n\": \"全部\", \"v\": \"\"}, {\"n\": \"2024\", \"v\": \"2024\"}, {\"n\": \"2023\", \"v\": \"2023\"}, {\"n\": \"2022\", \"v\": \"2022\"}, {\"n\": \"2021\", \"v\": \"2021\"}, {\"n\": \"2020\", \"v\": \"2020\"}, {\"n\": \"2019\", \"v\": \"2019\"}, {\"n\": \"2018\", \"v\": \"2018\"}, {\"n\": \"2017\", \"v\": \"2017\"}, {\"n\": \"2016\", \"v\": \"2016\"}, {\"n\": \"2015\", \"v\": \"2015\"}, {\"n\": \"2014\", \"v\": \"2014\"}, {\"n\": \"2013\", \"v\": \"2013\"}, {\"n\": \"2012\", \"v\": \"2012\"}, {\"n\": \"2011\", \"v\": \"2011\"}, {\"n\": \"2010\", \"v\": \"2010\"}, {\"n\": \"2009\", \"v\": \"2009\"}, {\"n\": \"2008\", \"v\": \"2008\"}, {\"n\": \"2007\", \"v\": \"2007\"}, {\"n\": \"2006\", \"v\": \"2006\"}, {\"n\": \"2005\", \"v\": \"2005\"}, {\"n\": \"2004\", \"v\": \"2004\"}, {\"n\": \"2003\", \"v\": \"2003\"}, {\"n\": \"2002\", \"v\": \"2002\"}, {\"n\": \"2001\", \"v\": \"2001\"}, {\"n\": \"2000\", \"v\": \"2000\"}, {\"n\": \"1999\", \"v\": \"1999\"}, {\"n\": \"1998\", \"v\": \"1998\"}, {\"n\": \"1997\", \"v\": \"1997\"}, {\"n\": \"1996\", \"v\": \"1996\"}, {\"n\": \"1995\", \"v\": \"1995\"}, {\"n\": \"1994\", \"v\": \"1994\"}, {\"n\": \"1993\", \"v\": \"1993\"}, {\"n\": \"1992\", \"v\": \"1992\"}, {\"n\": \"1991\", \"v\": \"1991\"}, {\"n\": \"1990\", \"v\": \"1990\"}, {\"n\": \"1989\", \"v\": \"1989\"}, {\"n\": \"1988\", \"v\": \"1988\"}, {\"n\": \"1987\", \"v\": \"1987\"}, {\"n\": \"1986\", \"v\": \"1986\"}, {\"n\": \"1985\", \"v\": \"1985\"}, {\"n\": \"1984\", \"v\": \"1984\"}, {\"n\": \"1983\", \"v\": \"1983\"}, {\"n\": \"1982\", \"v\": \"1982\"}, {\"n\": \"1981\", \"v\": \"1981\"}, {\"n\": \"1980\", \"v\": \"1980\"}, {\"n\": \"1979\", \"v\": \"1979\"}, {\"n\": \"1978\", \"v\": \"1978\"}, {\"n\": \"1977\", \"v\": \"1977\"}, {\"n\": \"1976\", \"v\": \"1976\"}, {\"n\": \"1975\", \"v\": \"1975\"}, {\"n\": \"1974\", \"v\": \"1974\"}, {\"n\": \"1973\", \"v\": \"1973\"}, {\"n\": \"1972\", \"v\": \"1972\"}, {\"n\": \"1971\", \"v\": \"1971\"}, {\"n\": \"1970\", \"v\": \"1970\"}, {\"n\": \"1969\", \"v\": \"1969\"}, {\"n\": \"1968\", \"v\": \"1968\"}, {\"n\": \"1967\", \"v\": \"1967\"}, {\"n\": \"1966\", \"v\": \"1966\"}, {\"n\": \"1965\", \"v\": \"1965\"}, {\"n\": \"1964\", \"v\": \"1964\"}, {\"n\": \"1963\", \"v\": \"1963\"}, {\"n\": \"1962\", \"v\": \"1962\"}, {\"n\": \"1961\", \"v\": \"1961\"}, {\"n\": \"1960\", \"v\": \"1960\"}]}, {\"key\": \"by\", \"name\": \"排序\", \"value\": [{\"n\": \"时间\", \"v\": \"time\"}, {\"n\": \"人气\", \"v\": \"hits\"}, {\"n\": \"评分\", \"v\": \"score\"}]}]}";
        JSONObject filterConfig = new JSONObject(f);
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", filterConfig);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String area = extend.get("area") == null ? "" : extend.get("area");
        String year = extend.get("year") == null ? "" : extend.get("year");
        String by = extend.get("by") == null ? "" : extend.get("by");
        String classType = extend.get("class") == null ? tid : extend.get("class");

        String cateUrl = siteUrl + String.format("/vod-list-id-%s-pg-%s-order--by-%s-class-0-year-%s-letter--area-%s-lang-.html", classType, pg, by, year, area);
        String html = req(cateUrl, getHeader());
        JSONArray videos = new JSONArray();
        Elements items = Jsoup.parse(html).select("[class=globalPicList] li > a");
        for (Element item : items) {
            String vodId = item.attr("href");
            String name = item.attr("title");
            String pic = item.select("img").attr("src");
            List<TextNode> textNodes = item.select(".sBottom span").textNodes();
            String remark = textNodes.size() > 0 ? textNodes.get(0).text() : "";
            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodId);
            vod.put("vod_name", name);
            vod.put("vod_pic", pic);
            vod.put("vod_remarks", remark);
            videos.put(vod);
        }
        int page = Integer.parseInt(pg), count = Integer.MAX_VALUE, limit = 30, total = Integer.MAX_VALUE;
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
        String html = req(detailUrl, getHeader());
        Document doc = Jsoup.parse(html);
        String name = doc.select(".page-hd a").attr("title");
        String pic = doc.select(".page-hd img").attr("src");
        String typeName = doc.select(".type-title").text();
        String year = getYear(html);
        String remark = getRemark(html);
        String actor = getActor(html);
        String director = getDirector(html);
        String description = doc.select(".detail-con p").text().replaceAll("简 介：", "");

        Elements sourceList = doc.select("[class=numList]");
        Elements circuits = doc.select("#leftTabBox > .hd a");
        Map<String, String> playMap = new LinkedHashMap<>();
        for (int i = 0; i < sourceList.size(); i++) {
            String circuitName = circuits.get(i).text();
            ArrayList<String> vodItems = new ArrayList<>();
            Elements aList = sourceList.get(i).select("a");
            for (int j = aList.size() - 1; j >= 0; j--) { // 逆序遍历，因为网站的集数是从大到小的
                String episodeUrl = siteUrl + aList.get(j).attr("href");
                String episodeName = aList.get(j).text();
                vodItems.add(episodeName + "$" + episodeUrl);
            }
            if (vodItems.size() > 0) playMap.put(circuitName, TextUtils.join("#", vodItems));
        }

        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", name); // 影片名称
        vod.put("vod_pic", pic); // 图片
        vod.put("type_name", typeName); // 影片类型 选填
        vod.put("vod_year", year); // 年份 选填
        vod.put("vod_area", ""); // 地区 选填
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
        String searchUrl = siteUrl + "/index.php?m=vod-search&wd=" + URLEncoder.encode(key);
        String html = req(searchUrl, getHeader());
        JSONArray videos = new JSONArray();
        Elements items = Jsoup.parse(html).select("[id=data_list] li");
        for (Element item : items) {
            Elements a = item.select(".pic > a");
            String vodId = a.attr("href");
            String name = item.select(".sTit").text();
            String pic = a.select("img").attr("data-src");
            String remark = item.select(".sStyle").text();
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
        result.put("parse", 1);// 1=嗅探  0=直连
        result.put("header", getHeader().toString());
        result.put("playUrl", "");
        result.put("url", id);
        return result.toString();
    }
}