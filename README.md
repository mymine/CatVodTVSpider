# 说明

### 1.基于这些开源的项目。
>  [CatVodTVSpider项目](https://github.com/CatVodTVOfficial/CatVodTVSpider)
、 [TvJar-1项目](https://github.com/q215613905/TvJar-1)、[tvjar_test](https://github.com/asters1/tvjar_test)、[CatVodSpider](https://github.com/FongMi/CatVodSpider)、[TvJar](https://github.com/takagen99/TvJar)

- 在此感谢这些开源项目，感谢 CatVodTVOfficial、q215613905、asters1、FongMi、takagen99

- 初次接触的话建议查看原来的 [README.md](./README(原).md)，里面有关于一些参数说明。

### 2.编写spider说明：
> 当前项目的dev分支无需启动虚拟机即可调试相关的内容(缺点是无法编写和调试与Android系统相关的内容，如果需要编写和调试与Android系统相关的内容，那么就需要用当前项目的main分支)，dev分支里面 /app/src/main/java/com/github/catvod/demo 下有多个测试的例子，给对应的 spider 类调试时，多打几个断点，仔细观察各个变量里面存储的是什么值就明白各个变量的作用了。

### 3.编译和构建 jar：
> 当调试好spider类后，可以将对应包下面的代码拷贝至main分支对应的包下，然后执行 buildAndGenJar.bat，即可构建jar，如果你是macOS / Linux 系统，那么可以利用 GitHub 的 Actions 去构建，当前项目的 Actions 里面有 【Spider Jar Gen CI】、【Spider构建】，建议使用 【Spider构建】，这个构建好 jar 后，存在 action 的 Artifacts 里，不会将jar直接提交到当前项目的 jar 目录下。在执行完毕的 action 里面的 Artifacts 可以找到和下载打包好的 Spider.zip ，解压后即可得到 jar


### 4.配置接口
> FongMi的影视APP / TVBox 配置地址: https://raw.githubusercontent.com/zhixc/CatVodTVSpider/main/config.json
>
> FongMi的影视APP / TVBox 配置加速地址1: https://mirror.ghproxy.com/https://raw.githubusercontent.com/zhixc/CatVodTVSpider/main/config.json
>
> FongMi的影视APP / TVBox 配置加速地址2: https://github.moeyy.xyz/https://raw.githubusercontent.com/zhixc/CatVodTVSpider/main/config.json



### 5.推荐的软件
> 强烈推荐使用 FongMi 的影视，支持自动换源，非常强大，作者持续更新维护。其仓库地址：https://github.com/FongMi/TV
> 
> 俊版TVBox、takagen99版TVBox，安装包发布仓库：https://github.com/o0HalfLife0o/TVBoxOSC
> 
> 俊版仓库地址：https://github.com/q215613905/TVBoxOS
> 
> takagen99版仓库地址 https://github.com/takagen99/Box

### 6.关于直播(爬虫版)使用说明

- 第一种写法，这种是早期写的，比较难阅读和编写
```text
{"key":"Live2Vod","name":"电视直播","type":3,"api":"csp_Live2Vod","searchable":1,"ext":"南风$https://agit.ai/Yoursmile7/TVBox/raw/branch/master/live.txt#饭太硬$https://agit.ai/fantaiying/fty/raw/branch/master/live.txt#影视范$https://agit.ai/fantaiying/fmm/raw/branch/main/tv/m3u/global.m3u"},
```
- 第二种写法，这种从配置文件里面读取会比较好些
```text
{"key":"Live2Vod","name":"电视直播(远程配置版)","type":3,"api":"csp_Live2Vod","searchable":1,"ext":"https://ghproxy.com/https://raw.githubusercontent.com/zhixc/CatVodTVSpider/main/json/live.json"},
```
- 第二种写法对应的配置文件说明：
```json
[
  {
    "name": "txt直播名称1",
    "url": "http://abc.txt"
  },
  {
    "name": "txt直播名称2",
    "url": "http://def.txt&&&http://hij.png"  // 直播链接与自定义图片用 &&& 隔开 
  },
  {
    "name": "txt直播名称3",
    "url": "http://def.txt&&&http://hij.png",
    "circuit": 1   // 分组后各个组里面的直播按照名称分线路
  },
  {
    "name": "m3u直播名称1",
    "url": "https://lmn.m3u"
  },
  {
    "name": "m3u直播名称2",
    "url": "https://lmn.m3u&&&https://opq.jpg" // 同样的，带图片链接
  },
  {
    "name": "m3u直播名称3(分组)",
    "url": "https://rst.m3u&&&http://hij.png", // m3u分组的最好带上图片，不然没有图片
    "group": 1  // 要分组的话，group 值为 1，其他情况不分组
  }
]
```
- m3u 格式的文件，里面一般带有图片，如果在json配置文件里面写了图片链接，那么就以json配置文件的为主。
- 一些直播源来自：Yoursmile7 的 [TVBox](https://agit.ai/Yoursmile7/TVBox)、Ftindy 的 [IPTV-URL](https://github.com/Ftindy/IPTV-URL)、范明明的 [live](https://github.com/fanmingming/live) 等项目，非常感谢 Yoursmile7、Ftindy、范明明的分享。

