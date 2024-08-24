package com.github.catvod.spider;

import android.text.TextUtils;
import com.github.catvod.crawler.Spider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class PushAgent extends Spider {

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String url = ids.get(0);
        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", url);
        vod.put("vod_pic", "https://pic.rmb.bdstatic.com/bjh/1d0b02d0f57f0a42201f92caba5107ed.jpeg");
        //vod.put("type_name", "");
        //vod.put("vod_content", "推送的链接：" + url);
        vod.put("vod_content", url);
        vod.put("vod_play_from", TextUtils.join("$$$", Arrays.asList("直连", "嗅探", "解析")));
        vod.put("vod_play_url", TextUtils.join("$$$", Arrays.asList("播放$" + url, "播放$" + url, "播放$" + url)));
        JSONArray list = new JSONArray().put(vod);
        JSONObject result = new JSONObject();
        result.put("list", list);
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject result = new JSONObject();
        switch (flag) {
            case "直连":
                result.put("parse", 0);
                break;
            case "嗅探":
                result.put("parse", 1);
                break;
            case "解析":
                result.put("parse", 1).put("jx", "1");
                break;
        }
        result.put("playUrl", "");
        result.put("url", id);
        return result.toString();
    }
}
