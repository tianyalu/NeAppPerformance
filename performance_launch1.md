[TOC]

## 一、`APP`启动优化

### 1.1 为什么要启动优化？

用户希望应用能够及时响应并快速加载，启动时间过长的应用不能满足这个期望，并且可能使用户失望。

**启动太慢的结果**：

* 体验效果差
* 用户放弃使用你的应用
* 时间越长用户流失越高
* 产品死掉

### 1.2 启动优化流程及分类

#### 1.2.1 开机启动流程

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/app_start_process.png)

#### 1.2.2 启动分类

* 冷启动：应用从头开始启动（应用自设备启动后或系统终止应用后首次启动）；
* 热启动：将`Activity`带到前台（如果应用的所有`Activity`都还驻留在内存中，则应用无需重复对象初始化、布局扩充和呈现。需要注意的是，如果程序的某些内存被系统清除，比如调用了`onTrimMemory`方法，则需要重新创建这些对象以响应热启动事件）；
* 温启动：涵盖在冷启动期间发生的操作的一些子集，同时它的开销比热启动多（它与热启动最大的区别在于，必须通过调用`onCreate`方法开始重新创建活动，也可以从传递给`onCreate`方法中保存的实例状态中获得某些对象的恢复）。

**冷启动流程：**

> 1. 加载并启动`APP`；
> 2. 启动后立即为该`APP`显示一个空白启动窗口；
> 3. 创建`APP`进程（创建应用程序对象）；
> 4. 启动主线程，创建主`Activity`；
> 5. 加载布局，绘制。

**启动总结**：

`App`从被系统调用，再到第一个页面渲染到手机屏幕，我们通常只需要关注`Application`中的`onCreate`方法，第一个`Activity`中`onCreate`、`onStart`、`onResume`方法。

注意：如果在`App`启动第一个`Activity`时，该`Activity`不但有自己的逻辑，还在`onCreate`、`onStart`或者`onResume`方法中直接有跳转到了其它`Activity`页面，那么跳转后的`Activity`的这三个方法也需要进行优化。

#### 1.2.3 黑白屏优化

在系统加载并启动`App`时，需要耗费相应的时间，即使时间不到1S，用户也会感觉到当点击`App`图标时会有“延迟”现象，为了解决这一个问题，`Google`的做法是在`App`创建的过程中，先展示一个空白的页面，让用户体会到点击图标之后立马就有响应，而这个空白页面的颜色则是根据我们在`Manifest`文件中配置的主题颜色 来决定的，现在一般默认为白色。

可以为应用的加载设置主题背景，从而使应用的启动屏幕在主题背景上与应用的后续效果保持一致，而不是采用系统主题。

**方案一**：设置`LauncherTheme`

在`LauncherTheme`中，设置系统“取消预览（空白窗体）”为`true`，或者设置空白窗体为透明，这样用户从视觉上就无法看出黑白屏的存在：

```xml
<style name="AppTheme.LauncherTheme">
	<!--设置系统的取消预览（空白窗口）为true-->
  <item name="android:windowDisablePreview">true</item>
  <!--设置背景为透明-->
  <item name="android:windowIsTranslucent">true</item>
</style>
```

**方案二**：自定义`Theme`主题

> 1. 自定义继承自`AppTheme`的主题；
> 2. 将启动`Activity`的`Theme`设置为自定义主题；
> 3. 在启动`Activity`的`onCreate`方法中，在`super.onCreate`和`setContentView`方法之前调用`setTheme`方法，将主题设置为最初的`AppTheme`。

① 自定义主题

```xml
<style name="AppTheme.LaunchTheme1">
	<item name="android:windowBackground">@mipmap/ic_launcher</item>
</style>
```

② 设置启动`Activity`主题

```xml
<activity android:name=".MainActivity"
          android:theme="@style/AppTheme.LaunchTheme1">
	<intent-filter>
  	<action android:name="android.intent.action.MAIN"/>
    <category android:name="android.intent.category.LAUNCHER"/>
  </intent-filter>
</activity>
```

③ 在代码中将主题设置回来

```java
protected void onCreate(Bundle savedInstanceState) {
  	setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState);
}
```



### 1.3 测量启动时间

#### 1.3.1 测量方式

* 系统日志输出：在`Android4.4`及更高的版本中，`logcat`包括一个输出行，其中包含命令为`Displayed`的值，此值代表从**启动进程**到**在屏幕上完成对应用`Activity`绘制**所经过的时间(`MI6`测试，并没有)；

  > 1. 启动进程；
  > 2. 初始化对象；
  > 3. 创建并初始化`Activity`:`ActivityManager:displayed com.sty.ne.appperformance/.MainActivity: +550ms`；
  > 4. 扩充布局；
  > 5. 首次绘制应用。

* `adb`命令：`adb shell Activity Manager`:

```bash
adb [ -d | -e | -s <serialNumber>] shell am start -S -W
com.sty.ne.appperformance/.MainActivity
-c android.intent.category.LAUNCHER
-a android.intent.action.MAIN

adb shell am start -W com.sty.ne.appperformance/.MainActivity
```

	显示结果如下：

```bash
GGGdeMac-mini:NeAppPerformance tian$ adb shell am start -W com.sty.ne.appperformance/.activity.SplashActivity
Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.sty.ne.appperformance/.activity.SplashActivity }
Status: ok
Activity: com.sty.ne.appperformance/.MainActivity
ThisTime: 186  （最后一个Activity启动耗时）
TotalTime: 395  （所有Activity启动耗时）
WaitTime: 417  （AMS启动Activity的总耗时）
Complete
```

* 手动获取：手动打印日志计算启动时间，只能记录应用内耗时。

```java
private void findViews() {
  final View viewRoot = findViewById(R.id.root);
  viewRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
    @Override
    public boolean onPreDraw() {
      viewRoot.getViewTreeObserver().removeOnPreDrawListener(this);
      LauncherTimer.logEnd("tag3");
      return false;
    }
  });
}

@Override
protected void onResume() {
  super.onResume();
  LauncherTimer.logEnd("tag1");
}

@Override
public void onWindowFocusChanged(boolean hasFocus) {
  super.onWindowFocusChanged(hasFocus);
  LauncherTimer.logEnd("tag2");
}
// D/Time: 2/tag1 launcher time=101
// D/Time: 2/tag3 launcher time=139
// D/Time: 2/tag2 launcher time=146
```

#### 1.3.2 方法耗时统计

* **`traceview`统计**：可以用代码统计，也可以用`Android Studio`自带的`cup profiler`来统计；**缺点**是代码侵入性强，会拖慢程序运行。

  ①`Debug Trace`:

  ```java
  @Override
  public void onCreate() {
    super.onCreate();
    Debug.startMethodTracing("Launcher");
  
    coreSize = Runtime.getRuntime().availableProcessors();
    executorService = Executors.newFixedThreadPool(Math.max(2, Math.min(coreSize - 1, 4)));
    application = this;
    context = this.getApplicationContext();
    AppProfile.context = context;
    ScreenUtil.init(context);
    initLog();
    AppForegroundWatcher.init(context);
    CrashReport.initCrashReport(getApplicationContext(), "e9bf59bd43", false);
  
    Debug.stopMethodTracing();
    //sdcard/Android/data/com.sty.ne.appperformance/files/Launcher.trace  --> save as 导出来，用Profiler打开
  }
  ```

  `sdcard/Android/data/com.sty.ne.appperformance/files/Launcher.trace`  --> `save as` 导出来，用`Profiler`打开，如下图所示：

  ![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/debug_trace.png)

  缺点：只能记录应用内程序执行时间。

  ②`CPU Profiler`: 

  不需要侵入代码（无需写`Debug.startMethodTracing("Launcher"`），但是需要做如下配置：

  > 1. `run` -> `edit configurations`；
  > 2. 勾选`start recording a method trace on startup`；
  > 3. 从菜单中选择`cpu`记录配置（`profiling`菜单下勾选两个复选框）；
  > 4. `apply` --> `profile`模式部署。

* **`systrace`统计**

  在代码中添加命令：

  ```java
  @Override
  public void onCreate() {
    super.onCreate();
    //systemtrace方式
    Trace.beginSection("Launcher");
  
    coreSize = Runtime.getRuntime().availableProcessors();
    executorService = Executors.newFixedThreadPool(Math.max(2, Math.min(coreSize - 1, 4)));
    application = this;
    context = this.getApplicationContext();
    AppProfile.context = context;
    ScreenUtil.init(context);
    initLog();
    AppForegroundWatcher.init(context);
    CrashReport.initCrashReport(getApplicationContext(), "e9bf59bd43", false);
  
    Trace.endSection();
  }
  ```

  命令行终端进入如下目录：`/Users/tian/Library/Android/sdk/platform-tools/systrace`

  输入如下命令进入监听状态：

  ```bash
  python systrace.py -o mynewtrace.html sched freq idle am wm gfx view binder_driver hal dalvik camera input res
  ```

  ![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/system_trace1.png)

  此时运行代码，完成之后在命令行窗口按`Enter`键结束监听，然后会生成目标文件`mynewtrace.html`:

  ![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/system_trace1.png)

  分析目标文件：

  ![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/system_trace3.png)

  参考：[https://developer.android.google.cn/topic/performance/tracing/command-line](https://developer.android.google.cn/topic/performance/tracing/command-line)

* **`aop`方式统计**

### 1.4 优化方式

#### 1.4.1 异步优化

异步优化主要是采用子线程来进行线程初始化，并行执行，减少执行时间。

```java
@Override
public void onCreate() {
  super.onCreate();

  coreSize = Runtime.getRuntime().availableProcessors();
  executorService = Executors.newFixedThreadPool(Math.max(2, Math.min(coreSize - 1, 4)));
  application = this;
  context = this.getApplicationContext();
  AppProfile.context = context;
  ScreenUtil.init(context);
  async(new Runnable() {
    @Override
    public void run() {
      initLog();
    }
  });
  async(new Runnable() {
    @Override
    public void run() {
      AppForegroundWatcher.init(context);
    }
  });
  async(new Runnable() {
    @Override
    public void run() {
      CrashReport.initCrashReport(getApplicationContext(), "e9bf59bd43", false);
    }
  });
}
```

异步优化需要关注的点：

> 1. 确定能不能异步优化；
> 2. 执行的方法是否有先后顺序；
> 3. 需要注意异步后程序能否正常执行；
> 4. 异步线程中使用的`api`不能创建`Handler`；
> 5. 不能有`UI`操作。

#### 1.4.2 延迟初始化

仅初始化立即需要的对象，不要创建全局静态对象，而是移动到单例模式，其中应用仅在第一次访问对象时初始化它们。

#### 1.4.3 空闲时初始化

可以监听应用空闲时间，在空闲时间进行初始化。

```java
public class DelayInit {
    private Queue<Runnable> delayQueue = new LinkedList<>();

    public void add(Runnable runnable) {
        delayQueue.add(runnable);
    }

    public void start() {
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                Runnable poll = delayQueue.poll();
                if(poll != null) {
                    poll.run();
                }
                return !delayQueue.isEmpty();
            }
        });
    }
}
```