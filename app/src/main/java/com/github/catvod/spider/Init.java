package com.github.catvod.spider;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttpUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Init {

   /*
   在Android中，Handler被用来提供用于线程间通信，以确保线程通信安全（比如U1线程的安全）。包含四个组成部分：Message，Looper，
   MessageQueue，Handler，这四个组成部分构成了多线程中经典的“生产者消费者模型
   1、成员介绍:
   Message：主要功能是进行消息的封装，同时可以指定消息的操作形式；
   Looper：消息循环泵，用来为一个线程跑一个消息循环。每一个线程最多只可以拥有一个。
   MessageQueue：就是一个消息队列，存放消息的地方。每一个线程最多只可以拥有一个。
   Handler：消息的处理者，handler负责将需要传递的信息封装成Message，发送给Looper，继而由Looper将Message放入MessageQueue中。当Looper对象看到MessageQueue中含有Message，就将其广播出去。该handler对象收到该消息后，调用相应的handler对象的handleMessage（）方法对其进行处理。
   2、同线程各成员的关系及数量
   ①一个线程中只能有一个Looper，只能有一个MessageQueue，可以有多个Handler，多个Messge；
   ②一个Looper只能维护唯一一个MessageQueue，可以接受多个Handler发来的消息；
   ③一个Message只能属于唯——个Handler；
   ④同一个Handler只能处理自己发送给Looper的那些Message；
   * */

    private final ExecutorService executor;      //线程池对象
    private final Handler handler;      //每个handler将关联一个线程
    private Application app;   //每一个APP都会有一个Application实例，它拥有和APP一样长的生命周期，Application和APP一起“同生共死”

    private static class Loader {
        static volatile Init INSTANCE = new Init();  //实例化对象时，会调用无参构造Init()，初始化线程池
    }


    /*
     *返回当前Init类的对象
     * */
    public static Init get() {
        return Loader.INSTANCE;
    }


    /*
     * 无参构造方法
     *
     * */
    public Init() {
        this.handler = new Handler(Looper.getMainLooper());  //获取主线程的Looper对象
        this.executor = Executors.newFixedThreadPool(5);//线程池的大小为5
    }


    /*
     * 返回应用程序上下文，上下文理解为“应用程序的共享部分”
     *
     * */
    public static Application context() {
        return get().app;
    }


    /*
     * 构造方法，传入应用程序上下文
     * 些处没用this.app是因为：static方法不依赖于任何对象就可以进行访问,因此对于静态方法来说,是没有this的,
     * 因为它不依附于任何对象,既然都没有对象,就谈不上this了
     * */
    public static void init(Context context) {
        SpiderDebug.log("自定義爬蟲代碼載入成功！");
        get().app = ((Application) context);
        Notice notice = new Notice();
        String str = notice.GetResult("https://gitee.com/lekanbox/App/raw/master/ts.txt");
        notice.init(context, str+";30");

    }


    public static void execute(Runnable runnable) {
        get().executor.execute(runnable);   //提交Runnable类型的任务到线程池中
    }


    public static void run(Runnable runnable) {
        get().handler.post(runnable);     //handler的post（Runnable）操作用来发送消息给在主线程中的handler执行
    }


    public static void run(Runnable runnable, int delay) {
        get().handler.postDelayed(runnable, delay);   //delay指定时间后，调用runable对象
    }


    /*
     *Toast是一个View视图，快速的为用户显示少量的信息。Toast在应用程序上浮动显示信息给用户，它永远不会获得焦点，
     * 不影响用户的输入等操作，主要用于一些帮助/提示。
     * */
    public static void show(String msg) {
        get().handler.post(() -> Toast.makeText(context(), msg, Toast.LENGTH_LONG).show());
    }


    /*
     *取Activity全局上下文
     * */
    public static Activity getActivity() throws Exception {
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");  //按参数中指定的字符串形式的类名去搜索并加载相应的类
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null); //获取指定的方法
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities"); //获取类本身的属性成员
        activitiesField.setAccessible(true);  //设置为可访问
        Map<?, ?> activities = (Map<?, ?>) activitiesField.get(activityThread);
        for (Object activityRecord : activities.values()) {
            Class<?> activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if (!pausedField.getBoolean(activityRecord)) {
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                SpiderDebug.log(activity.getComponentName().getClassName());
                return activity;
            }
        }
        return null;
    }
}
