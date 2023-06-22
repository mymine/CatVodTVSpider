package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Paper extends Ali {

    @Override
    public void init(Context context, String extend) {
        super.init(context, extend);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("User-Agent", "Mozilla/5.0(Windows NT 10.0; Win64;x64)");
        linkedHashMap.put("Origin", "https://u.gitcafe.net/");
        linkedHashMap.put("Referer", "https://u.gitcafe.net/");

        LinkedHashMap<String,String>  linkedHashMap2 = new LinkedHashMap<>();
        linkedHashMap2.put("action", "search");
        linkedHashMap2.put("from", "web");
        linkedHashMap2.put("token", "5b91e46fed2ef7a3eac2ed2d8665adb176c2d9");
        linkedHashMap2.put("keyword", key);

        String poststr = OkHttp.post("https://gitcafe.net/tool/alipaper/", linkedHashMap2, linkedHashMap);
        SpiderDebug.log(poststr);
        JSONArray jsonArray = (poststr.startsWith("[") && poststr.endsWith("]")) ? new JSONArray(poststr) : new JSONObject(poststr).getJSONArray("data");
        SpiderDebug.log(jsonArray.get(0).toString());
        ArrayList<Vod> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Vod vod = new Vod();
            vod.setVodId("https://www.aliyundrive.com/s/" + jsonArray.getJSONObject(i).getString("key"));
            vod.setVodName(jsonArray.getJSONObject(i).getString("title"));
            vod.setVodPic("https://pic.rmb.bdstatic.com/bjh/1d0b02d0f57f0a42201f92caba5107ed.jpeg");
            vod.setVodRemarks(jsonArray.getJSONObject(i).getString("alititle"));
            arrayList.add(vod);

        }
        return Result.string(arrayList);

    }




}
