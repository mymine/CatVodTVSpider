package com.github.catvod.demo;

import android.app.Activity;
import android.os.Bundle;

import com.github.catvod.R;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.spider.Czsapp;
import com.github.catvod.spider.Init;
import com.github.catvod.spider.Kunyu77;
import com.github.catvod.spider.Notice;
import com.github.catvod.spider.Paper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {
    /*
     * onCreate的方法是在Activity创建时被系统调用，是一个Activity生命周期的开始。
     * getApplicationContext(): 返回应用的上下文，生命周期是整个应用，应用摧毁，它才摧毁。
     * setContentView的作用就是把自己的布局文件放在Activity中显示
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init.init(getApplicationContext());
        new Thread(() -> {
            System.out.println("可以开始调试了哦！！！！！！！！！！");
//            Notice notice = new Notice();
//            notice.init(MainActivity.this,"这是一个弹窗通知");

//            paper.setUsername("yunshuche");
//            paper.setPassword("11223344");
 //           this.Alisearch(paper, "他是谁", true);
//            Zhaozy zzy = new Zhaozy();
//            zzy.setUsername("yunshuche");
//            zzy.setPassword("11223344");
//            this.Alisearch(zzy, "他是谁", true);
//            try {
//                Czsapp spider = new Czsapp();
//                // 外接字符串，可用于自定义
//                spider.init(MainActivity.this, "https://www.czzy03.com");
//                // 筛选HashMap
//
//                // 搜索开关，1为true,0为false
//                int search_switch = 1;
//                if (search_switch == 1) {
//                    // 搜索,key为空的话，将使用默认值:==>琅琊榜
//                    List<String> res_search = null;
//                    res_search = test_search(spider, "他是谁");
//                    ArrayList<String> res_detail = test_detail(spider, res_search.get(0));
//                    test_player(spider, res_detail.get(0));
//
//                } else {
//                    Class_res_home res_home = test_home(spider);
//
//                    // res_home.array.getString(0)测试是电视剧还是电影...，可以根据index来切换
//                    String tid = res_home.array.getString(0);
//                    HashMap<String, String> extend;
//                    try {
//
//                        // 筛选
//                        ArrayList<HashMap<String, String>> filters = res_home.extend.get(tid);
//                        // filters.get(0)是测试是动作片还是喜剧片...,可以根据index来切换，
//                        extend = filters.get(1);
//                    } catch (Exception e) {
//                        extend = null;
//
//                    }
//                    List<String> res_category = test_category(spider, tid, extend);
//                    // res_category.get(1)测试是那一部视频...，可以根据index来切换
//                    ArrayList<String> res_detail = test_detail(spider, res_category.get(1));
//                    test_player(spider, res_detail.get(0));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            Czsapp app = new Czsapp();
//            try {
//                this.Testspider(app, "他是谁", "", true);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//
//
//            Kunyu77 kunyu = new Kunyu77();
//            try {
//                this.Testspider(kunyu, "他是谁", "", false);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            AppYsV2 ying = new AppYsV2();
//            try {
//                this.Testspider(ying, "长月烬明", "http://kuying.kuyouk.top:9528/api.php/app/", false);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            Kuaikan kuaikan=new Kuaikan();
//            try {
//                this.Testspider(kuaikan,"长月烬明","",false);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//               String str=kuaikan.searchContent("他是谁",true);
//               SpiderDebug.log(str);

//            SP360 sp360 =new SP360();
//            try {
//                this.Testspider(sp360,"云襄传","",false);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }


        }).start();
    }

    /*
     * 调试ALI搜索,返回搜索结果json
     * */
    public void Alisearch(Spider spider, String key, boolean quick) {
        String str;
        try {
            str = spider.searchContent(key, quick);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(spider.toString() + "返回值：" + str);

    }

    /*
     * 调试单个site,spider
     * */
    public void Testspider(Spider spider, String key, String extend, boolean quick) throws Exception {
        spider.init(MainActivity.this, extend);
        String json = spider.homeContent(true);
        SpiderDebug.log("homeContent返回值:" + json);  //
        String str = spider.searchContent(key, quick);
        System.out.println("searchContent返回值:" + str);  //
        String str1 = spider.homeVideoContent();
        System.out.println("homevideoContent返回值:" + str1);  //
        JSONObject homeContent = null;
        try {
            homeContent = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("categoryContent返回值:" + spider.categoryContent("1", "1", true, null));
        if (homeContent != null) {
            try {
                List<String> ids = new ArrayList<String>();
                JSONArray array = homeContent.getJSONArray("list");
                for (int i = 0; i < array.length() && i < 3; i++) {
                    try {
                        ids.clear();
                        ids.add(array.getJSONObject(i).getString("vod_id"));
                        System.out.println("detailContent返回值:" + spider.detailContent(ids));  //调用detailContent的输出结果
                        JSONObject detailContent = new JSONObject(spider.detailContent(ids)).getJSONArray("list").getJSONObject(0);
                        String[] playFlags = detailContent.getString("vod_play_from").split("\\$\\$\\$");
                        String[] playUrls = detailContent.getString("vod_play_url").split("\\$\\$\\$");
                        for (int j = 0; j < playFlags.length; j++) {
                            String pu = playUrls[j].split("#")[0].split("\\$")[1];
                            // System.out.println(pu);
                            System.out.println("playerContent返回值:" + spider.playerContent(playFlags[j], pu, new ArrayList<>()));  //调用playerContent返回结果
                        }
                    } catch (Throwable th) {
                    }
                }
            } catch (Throwable th) {
            }
        }
    }

    public static Class_res_home test_home(Spider spider) throws Exception {

        // 标题栏
        Class_res_home res_class = new Class_res_home();
        JSONArray res = new JSONArray();
        HashMap<String, ArrayList<HashMap<String, String>>> res_extend = new HashMap<>();
        System.out.println("==========homeContent:=======\n");
        String strhomeContent = spider.homeContent(true);
        if (strhomeContent.equals("")) {
            System.out.println("homeContent返回为空");
            System.exit(0);
        }
        try {
            JSONObject result = new JSONObject(new String(strhomeContent));
            JSONArray classes = result.getJSONArray("class");
            try {
                for (int i = 0; i < classes.length(); i++) {
                    System.out.print(classes.getJSONObject(i).get("type_name") + "["
                            + classes.getJSONObject(i).get("type_id") + "]" + "\r\n");

                    try {
                        JSONArray filters = result.getJSONObject("filters")
                                .getJSONArray(classes.getJSONObject(i).getString("type_id"));
                        for (int j = 0; j < filters.length(); j++) {

                            System.out.print(filters.getJSONObject(j).getString("name") + "  ");
                            JSONArray value = filters.getJSONObject(j).getJSONArray("value");
                            String key = filters.getJSONObject(j).getString("key");
                            // HashMap<String, ArrayList<HashMap<String, String>>> leixing = new
                            // HashMap<String, ArrayList<HashMap<String, String>>>();
                            ArrayList<HashMap<String, String>> leixing = new ArrayList<>();
                            for (int k = 0; k < value.length(); k++) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                System.out.print(value.getJSONObject(k).getString("n") + "  ");
                                hashMap.put(key, value.getJSONObject(k).getString("v"));
                                leixing.add(hashMap);

                            }
                            System.out.println("\r\n");
                            res_extend.put(classes.getJSONObject(i).getString("type_id"), leixing);
                        }

                    } catch (Exception e) {
                        System.out.println("未读取到[" + classes.getJSONObject(i).get("type_id") + "]的筛选信息\r\n");
                    }

                    res.put(classes.getJSONObject(i).get("type_id"));
                }
                System.out.println();
            } catch (Exception e) {
                System.out.println("未读取到type_name或者type_id，请检查！");
            }
        } catch (Exception e) {
            System.out.println("未读取到class，请检查！");
        }

        res_class.array = res;
        res_class.extend = res_extend;
        System.out.println(res_class.extend);
        return res_class;
    }

    public static List<String> test_category(Spider spider, String tid, HashMap<String, String> extend) throws Exception {
        System.out.println("\r\n你测试的分类为:" + tid);
        System.out.println("\r\n你测试的筛选为:" + extend);

        List<String> res = new ArrayList<>();
        // 标题栏下的视频内容
        System.out.println("\r\n==========categoryContent:=======\r\n");
        String strcategoryContent = spider.categoryContent(tid, "1", true, extend);
        if (strcategoryContent.equals("")) {
            System.out.println("categoryContent返回为空");
            System.exit(0);
        }
        try {
            JSONObject result = new JSONObject(new String(strcategoryContent));
            int page = result.getInt("page");
            System.out.println("//当前页");
            System.out.println("page:" + page);
            int pagecount = result.getInt("pagecount");
            System.out.println("// 总共几页");
            System.out.println("pagecount:" + pagecount);
            int limit = result.getInt("limit");
            System.out.println("// 每页几条数据");
            System.out.println("limit:" + limit);
            int total = result.getInt("total");
            System.out.println(" // 总共多少条数据");
            System.out.println("total:" + total);
            JSONArray list = result.getJSONArray("list");
            System.out.println(" //视频列表");
            try {
                for (int i = 0; i < list.length(); i++) {
                    System.out.print(list.getJSONObject(i).get("vod_name") + "["
                            + list.getJSONObject(i).get("vod_id") + "]" + "\r\n");
                    res.add(list.getJSONObject(i).get("vod_id").toString());
                }
            } catch (Exception e) {
                System.out.println("视频列表解析出现问题，请检查!");
            }
        } catch (Exception e) {
            System.out.println(" // 总共多少调数据");
            System.out.println("没有解析到list，请检查！！！");
        }

        return res;

    }

    public static ArrayList<String> test_detail(Spider spider, String ids0) throws Exception {
        // 视频详细信息,注意List<String> ids只有一个tid
        ArrayList<String> result_url = new ArrayList<String>();
        List<String> ids = new ArrayList<>();
        ids.add(ids0);
        System.out.println("\r\n==========detailContent:=======\r\n");
        System.out.println("你测试的链接是:" + ids0);
        JSONArray data = new JSONArray();
        String strdetailContent = spider.detailContent(ids);
        if (strdetailContent.equals("")) {
            System.out.println("detailContent返回为空");
            System.exit(0);
        }
        try {
            JSONObject result = new JSONObject(new String(strdetailContent));
            JSONArray list = result.getJSONArray("list");
            try {
                String vod_play_from = list.getJSONObject(0).getString("vod_play_from");
                for (String s : vod_play_from.split("\\$\\$\\$")) {
                    JSONObject source = new JSONObject();
                    source.put("source_name", s);
                    data.put(source);
                }
                // System.out.println(data);
            } catch (Exception e) {
                System.out.println("没有解析到vod_play_from,请检查!!!");
            }
            try {
                String vod_play_url = list.getJSONObject(0).getString("vod_play_url");
                int i = 0;
                for (String url_list : vod_play_url.split("\\$\\$\\$")) {
                    JSONArray d = new JSONArray();
                    for (String name_url : url_list.split("\\#")) {
                        JSONArray n_u = new JSONArray();
                        for (String value : name_url.split("\\$")) {
                            n_u.put(value);
                        }
                        d.put(n_u);
                    }
                    data.getJSONObject(i).put("data", d);
                    i = i + 1;
                }
            } catch (Exception e) {
                System.out.println("没有解析到vod_play_url,请检查!!!");
            }
            for (int j = 0; j < data.length(); j++) {
                System.out.println();
                for (int k = 0; k < data.getJSONObject(j).getJSONArray("data").length(); k++) {
                    System.out.println(data.getJSONObject(j).getString("source_name") + "--->"
                            + data.getJSONObject(j).getJSONArray("data").getJSONArray(k).getString(0) + "[ "
                            + data.getJSONObject(j).getJSONArray("data").getJSONArray(k).getString(1) + " ]");
                    result_url.add(data.getJSONObject(j).getJSONArray("data").getJSONArray(k).getString(1));
                }
            }
            return result_url;

            // System.out.println(list);
        } catch (Exception e) {
            System.out.println("未读取到list,请检查!!");
        }

        return null;

    }

    public static String test_player(Spider spider, String url) throws Exception {
        // 播放内容
        System.out.println("\r\n==========playerContent=======\r\n");
        System.out.println("测试地址为:" + url);
        String strplayerContent = spider.playerContent("", url, null);
        System.out.println(strplayerContent);
        System.exit(0);

        return "";
    }

    public static List<String> test_search(Spider spider, String key) throws Exception {
        List<String> res = new ArrayList<>();
        System.out.println("\r\n==========searchContent=======\r\n");
        if (key.equals("")) {
            System.out.println("关键字为空，使用默认值==>斗罗大陆\r\n");
            key = "斗罗大陆";
        } else {
            System.out.println("你测试的搜索关键字为==>" + key + "\r\n");

        }
        String strsearchContent = spider.searchContent(key, true);
        if (strsearchContent.equals("")) {
            System.out.println("searchContent返回为空");
            System.exit(0);
        }
        try {
            JSONObject result = new JSONObject(new String(strsearchContent));
            JSONArray list = result.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                try {
                    String vod_id = list.getJSONObject(i).getString("vod_id");
                    try {
                        String vod_name = list.getJSONObject(i).getString("vod_name");
                        System.out.println(vod_name + "[" + vod_id + "]");
                        res.add(vod_id);
                    } catch (Exception e) {
                        System.out.println("没有解析到vod_name");
                    }
                } catch (Exception e) {
                    System.out.println("没有解析到vod_id");
                }
            }
            return res;
        } catch (Exception e) {
            System.out.println("没有解析到list");
        }
        System.exit(0);
        return null;
    }
}

class Class_res_home {
    JSONArray array;
    HashMap<String, ArrayList<HashMap<String, String>>> extend;
}

