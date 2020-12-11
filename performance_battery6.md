## 六、电量优化

[TOC]

### 6.1 电量消耗场景

* 硬件消耗电量来执行任务的过程叫做超时电流消耗；
* 电量消耗的计算与统计是一件麻烦而且矛盾的事情，记录电量消耗本身也是一个费电量的事情，因此只能使用第三方检测电量的设备；

> 1. **待机状态耗电**：当设备处于待机状态时消耗的电量是极小的，比如`N5`，打开飞行模式，可以待机近一个月；
> 2. **屏幕唤醒耗电**：屏幕唤醒会出现电量使用高峰线；
> 3. **`CPU`唤醒耗电**：`CPU`唤醒会出现电量使用高峰线，后续正常消耗；
> 4. **蜂窝式无线耗电**：发送出现耗电高峰；接收出现耗电高峰；保存唤醒耗电均衡；

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/battery_cellular.png)

#### 6.1.1 消耗维度

* 平均只有30%左右的电量是被程序最核心的方法例如绘制图片、摆放布局等所使用；
* 剩余70%是被上报数据、检查位置信息、定时检索等使用掉的。

### 6.2 `BATTERY-HISTORIAN`使用

官方地址：[https://github.com/google/battery-historian](https://github.com/google/battery-historian)

#### 6.2.1 `Docker`环境安装

①安装`Docker Community Edition`；

②运行`Battery Historian`镜像；

③执行如下命令：

```bash
# 该命令为官方提供，但不翻墙好像下载不了
docker run -p 9999:9999 gcr.io/android-battery-historian/stable:3.0 --port 9999

# 采用阿里云镜像仓库拉取镜像可以成功
docker run -p 9999:9999 registry.cn-hangzhou.aliyuncs.com/xyz10/android-battery-historian:stable-3.0
```

④浏览器中访问：`http://localhost:9999`；

⑤导出电量信息：

```bash
adb shell dumpsys batterystats --reset
adb shell dumpsys batterystats --enable full-wake-history
# 操作测试应用，然后执行如下命令导出
adb bugreport bugreport.zip #(7.0) （注意：该文件是生成在电脑端的）
adb bugreport > bugreport.txt #(6.0)
```

⑥上传`bugreport`到`http://localhost:9999`;

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/battery_historian1.png)

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/battery_historian2.png)

#### 6.2.32 指标含义

* `battery_level`：电量；
* `plugged`：充电状态，充电时间；
* `screen`：屏幕是否点亮；
* `top`：当前运行`app`；
* `wake_lock`：`wake_lock`模块工作时间；
* `cpu running`：`cpu`执行状态；
* `JobScheduler`：`JobScheduler`执行状态；
* `wifi`：`wifi`模块相关状态。

### 6.3 电量记录

`Android`在进行电量统计时，并不是采用直接记录电流消耗量的方式，而是跟踪硬件模块在不同状态下的使用时间，收集一些可用信息，用来近似地计算出电池消耗量。

`frameworks/base/core/res/res/xml/power_profile.xml`中记录着各个模块单位时间的耗电量，由厂商定义。

可用通过如下命令拷贝出`framework-res.apk`：

```bash
adb pull /system/framework/framework-res.apk ~/MyDocuments
```

然后通过反编译工具反编译后，查看其`framework-res/res/xml/power_profile.xml`文件。

**`BatteryStatsHelper`**：计算耗电量的类。

### 6.4 电量优化方式

* 减少唤醒屏幕的次数与持续时间，采用`WakeLock`来处理唤醒的问题；
* 监控充电状态执行非必须的操作；
* 打包网络请求；
* 采用`JobScheduler`对任务进行定时处理；
* 减少定位获取次数，根据需求选取精度，多模块复用定位，及时注销定位监听；
* 传感器根据需要选取合适的采用率，采样率越高越耗电，在后台及时注销传感器监听；
* 后台停止动画运行，缩小动画执行范围。

