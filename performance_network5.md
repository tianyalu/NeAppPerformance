[TOC]

## 五、网络优化

### 5.1 网络优化维度

* 网络优化需要从多个维度、多个方面展开，并建立合适的数据指标来反应当前的网络的各项特征；
* 多维度具体包括：流量消耗、弱网质量、网络稳定性以及应用性能等方面；
* 网络流量质量提升的同时会减少手机电量的消耗，减少公司的成本。

#### 5.1.1 流量维度

* 减少整个应用的流量消耗，比如采用更小的数据格式，压缩数据传输，选择更小的图片；
* 准确统计消耗的流量，网络类型，前后台等数据指标来减少网络流量消耗。

#### 5.1.2 质量维度

* 提高网络请求速度，提高网络请求成功率，提供网络缓存，减少网络请求提升用户体验，更小的数据格式在减少流量消耗的同时提升弱网下的体验；
* 准确统计网络请求的各个维度，提升网络质量。

### 5.2 网络优化工具

#### 5.2.1 `NetworkProfiler`

在应用启动过程中开启高级选项：

> 1. `run` --> `edit configurations`;
> 2. `profiling` --> 勾选`enable advanced profiling`；
> 3. `apply` --> `profile`模式部署。

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/network_profiler.png)

#### 5.2.2 抓包工具

* `Charles`
* `Wireshark`

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/wireshark1.png)

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/wireshark2.png)

* `Fiddler`
* `TcpDump`

#### 5.2.3 `Stetho`

* 连接`Android`与`Chrome`；
* 可以查看组件布局，网络抓包，`sp`存储，数据库存储

使用步骤：

①引入`stetho`:

```groovy
    implementation 'com.facebook.stetho:stetho:1.5.1'
    implementation 'com.facebook.stetho:stetho-okhttp3:1.5.1'
```

②在`Application`中初始化

```java
    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        Stetho.initializeWithDefaults(context);
    }
```

③`OKHttp`中添加拦截器

```java
    private OkHttp() {
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .retryOnConnectionFailure(true).build();
    }
```

④运行程序并在`Chrome`地址栏输入`chrome://inspect/#devices`中查看

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/chrome_inspect.png)

### 5.3 流量优化

#### 5.3.1 流量统计(`mi6`测试没有卵用)

##### 5.3.1.1 获取系统启动后的流量:`TrafficStats`

> 1. `TrafficStats.getUidRxBytes(int uid)`;
> 2. `TrafficStats.getUidTxBytes(int uid)`;
> 3. `TrafficStats.getTotalRxBytes()`;
> 4. `TrafficStats.getTotalTxBytes()`;

```java
    private int getUid() {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
            return ai.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    //流量统计方式一（从开机到现在）
    private void statistics() {
        TrafficStats.getUidRxBytes(getUid());
        TrafficStats.getUidTxBytes(getUid());
        TrafficStats.getTotalRxBytes();
    }
```

方式二：

```java
    //流量统计方式二（从开机到现在）
    private long[] getStat(int uid) {
        String line, line2;
        long[] stats = new long[2];
        try {
            File fileSnd = new File("/proc/uid_stat/" + uid + "tcp_snd");
            File fileRcv = new File("/proc/uid_stat/" + uid + "tcp_rcv");
            BufferedReader br1 = new BufferedReader(new FileReader(fileSnd));
            BufferedReader br2 = new BufferedReader(new FileReader(fileRcv));
            while ((line = br1.readLine()) != null && (line2 = br2.readLine()) != null) {
                stats[0] = Long.parseLong(line);
                stats[1] = Long.parseLong(line2);
            }
            br1.close();
            br2.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
```

##### 5.3.1.2 获取指定时间间隔的流量：`NetworkStatsManager`

还可以获取不同网络类型下的流量。

#### 5.3.2 流量优化

##### 5.3.2.1 减小图片大小

> 1. 选用合适的图片格式；
> 2. 不同清晰度对空间的大小影响很大；
> 3. 合适的位置展示合适大小的图片；

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/suitable_pic.png)

##### 5.3.2.2 序列化数据

> 1. 减少序列化数据大小；
> 2. `JSON`与`XML`为了提高可读性，在文件中加入了大量的符号，空格等字符，占用空间，可以采用`protocol buffers`，`nano-proto-buffer`以及`flatbuffer`等来替换。

* **`Protocol Buffers`**：强大、灵活，但是对内存的消耗会比较大，并不是移动端上的最佳选择；
* **`Nano-Proto-Buffers`**：基于`Protocol`，为移动端做了特殊的优化，代码执行效率更高，内存使用效率更佳；
* **`FlatBuffers`**：这个开源库最开始是由`Google`研发的，专注于提供更优秀的性能。

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/json_optimize.png)

> 1. 存在重复的属性名称 --> 减少重复的属性名；
> 2. `GZIP`不能进行有效的压缩 --> 使得`GZIP`的压缩效率更高；
> 3. 同样的数据类型可以批量优化。

#### 5.3.3 其它优化

* 缓存数据；
* 不要采用轮询的方式获取数据；
* 数据压缩；
* 数据增量更新；
* 请求打包，减少请求头信息。

### 5.4 质量优化

#### 5.4.1 `DNS`优化

* `DNS`解析的失败率占联网失败中很大一种，而且首次域名解析一般需要几百毫秒；
* 采用`IP`直连省去`DNS`解析过程，节省这部分时间；
* 采用`HttpDNS`，避免`Local DNS`造成的域名劫持和跨网访问问题，解决域名解析异常带来的困扰。

#### 5.4.2 网络协议优化

* 采用高版本的`HTTP`；
* 采用`quic`协议，比如`google`的`grpc`；
* 采用`socket`长连，保证数据实时收发。

#### 5.4.3 图片优化

* 选取合适的图片格式，不仅减少了流量，也提高了网络传输的成功率；
* 选取缩略图，图片越小传输速度越快，成功率越高；
* 分片上传，根据网络情况动态调整分片大小，失败重传。

#### 5.4.4 其它优化

* 打包网络请求，监控网络状态，在`WIFI`下预取；
* 区分数据重要程度，低优先级数据在`WIFI`网络下上传；
* 设置重试次数，减小服务器压力；
* 资源部署`CDN`；
* 弱网情况下不显示图片。