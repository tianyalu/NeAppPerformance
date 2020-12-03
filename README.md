# Android性能优化

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
* 热启动：将`Activity`带到前台（如果应用的所有`Activity`都还驻留在内存中，则应用无需重复对象初始化、布局扩充和呈现）；
* 温启动：涵盖在冷启动期间发生的操作的一些子集，同时它的开销比热启动多。

**冷启动流程：**

> 1. 加载并启动`APP`；
> 2. 启动后立即为该`APP`显示一个空白启动窗口；
> 3. 创建`APP`进程（创建应用程序对象）；
> 4. 启动主线程，创建主`Activity`；
> 5. 加载布局，绘制。

### 1.2.3 黑白屏优化

可以为应用的加载设置主题背景，从而使应用的启动屏幕在主题背景上与应用的后续效果保持一致，二不是采用系统主题。

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

### 1.3.1 测量方式

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

​	显示结果如下：

```bash
GGGdeMac-mini:NeAppPerformance tian$ adb shell am start -W com.sty.ne.appperformance/.MainActivity
Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.sty.ne.appperformance/.MainActivity }
Status: ok
Activity: com.sty.ne.appperformance/.MainActivity
ThisTime: 337  （当前Activity启动耗时）
TotalTime: 337  （所有Activity启动耗时）
WaitTime: 359  （AMS启动Activity的总耗时）
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

### 1.3.2 方法耗时统计

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
> 3. 需要注意异步后程序能否正常执行。

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

## 二、布局优化

### 2.1 `Android`绘制原理

渲染操作通常依赖于两个核心组件：`CPU`与`GPU`。`CPU`负责包括`Measure`,`Layout`,`Record`,`Execute`等操作，`GPU`负责`Rasterization`(栅格化)操作。

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/android_draw_cpu_gpu_function.png)

### 2.1.1 为什么是`60fps`

* 人眼与大脑之间的协作无法感知超过`60fps`的画面更新；
* `12fps`：手动快速翻动书籍的帧率；
* `24fps`：电影使用的频率；
* `30fps`：实时音视频的帧率。

```java
public class FpsUtil {
    private static long startTime;
    private static int count = 0;
    private static final long INTERVAL = 160 * 1000 * 1000; //160ms

    private static Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if(startTime == 0) {
                startTime = frameTimeNanos; //纳秒
            }
            long interval = frameTimeNanos - startTime;
            if(interval > INTERVAL) {
                double fps = (((double) (count * 1000 * 1000)) / interval) * 1000L; // fps/1000L = (count * 1000 * 1000) / INTERVAL
                LogUtil.d("FPS", "fps=" + fps);
                startTime = 0;
                count = 0;
            }else {
                count++;
            }
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    public static void getFps() {
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    public static void stopGetFps() {
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }
}
```

#### 2.1.2 `VSYNC`

* `Android`系统每隔`16ms`发出`VSYNC`信号，触发对`UI`进行渲染；
* `Refresh Rate`：代表了屏幕在`1S`内刷新屏幕的次数，这取决于硬件的固定参数，如`60HZ`；
* `Frame Rate`：代表了`GPU`在`1S`内绘制操作的帧数，例如`30fps`,`60fps`。

帧率 > 刷新率：画面撕裂（上半部分渲染的是前一帧，下半部分渲染的是后一帧）

刷新率 > 帧率：两个刷新周期渲染了同一帧，丢帧、画面卡顿 <-- **大部分情况**

### 2.2 布局加载原理

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/view_create_process.png)

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  installCustomFactory();
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);
}

private void installCustomFactory() {
  LayoutInflaterCompat.setFactory2(getLayoutInflater(), new LayoutInflater.Factory2() {
    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
      //handle
      LogUtil.e("onCreateView", "name=" + name);
      return getDelegate().createView(parent, name, context, attrs);
      // E/onCreateView: 2/name=LinearLayout
      // E/onCreateView: 2/name=ViewStub
      // E/onCreateView: 2/name=FrameLayout
      // E/onCreateView: 2/name=androidx.appcompat.widget.ActionBarOverlayLayout
      // E/onCreateView: 2/name=androidx.appcompat.widget.ContentFrameLayout
      // E/onCreateView: 2/name=androidx.appcompat.widget.ActionBarContainer
      // E/onCreateView: 2/name=androidx.appcompat.widget.Toolbar
      // E/onCreateView: 2/name=androidx.appcompat.widget.ActionBarContextView
      // E/onCreateView: 2/name=androidx.constraintlayout.widget.ConstraintLayout
      // E/onCreateView: 2/name=TextView
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
      return null;
    }
  });
}
```

### 2.3 布局优化方式

#### 2.3.1 过渡绘制

`Overdraw`(过渡绘制)描述的是屏幕上某个像素在同一帧的时间内被绘制了多次。在多层次的`UI`结构里面，如果不可见的`UI`也在做绘制的操作，这就会导致某些像素区域被绘制了多次，这就浪费大量的`CPU`以及`GPU`资源。

优化方式：

* 移除`Window`默认的`background`；
* 移除`XML`布局文件中的非必须的`background`；
* 按需显示占位图片。

* 对于复杂的自定义`view`，`Android`系统无法监控并自动优化；
* 采用`canvas.clipRect`来帮助系统识别那些可见区域；
* 采用`canvas.quickreject`来判断是否相交。

```java
private void drawDroidCard(Canvas canvas, List<DroidCard> mDroidCards, int i) {
  DroidCard card = mDroidCards.get(i);
  canvas.save(); //画布保存
  canvas.clipRect(card.x, 0f, (mDroidCards.get(i + 1).x), card.height); //关键计算画布大小
  canvas.drawBitmap(card.bitmap, card.x, 0f, paint);
  canvas.restore(); //画布裁剪
}
```

#### 2.3.2 标签优化

* **`include`标签的使用**：抽取公用`xml`，使用时包含进来；

* **`merge`标签的使用**：`merge`标签主要用于辅助`include`标签,在使用`include`后可能导致布局嵌套过多,多余的`layout`节点或导致解析变慢(可通过`hierarchy viewer`工具查看布局的嵌套情况)；官方文档说明:`merge`用于消除视图层次结构中的冗余视图；

  `merge`标签常用场景:
  根布局是`FrameLayout`且不需要设置`background`或`padding`等属性,可以用`merge`代替,因为`Activity`的`ContentView`父元素就是`FrameLayout`,所以可以用`merge`消除只剩一个.
  某布局作为子布局被其他布局`include`时,使用`merge`当作该布局的顶节点,这样在被引入时顶结点会自动被忽略,而将其子节点全部合并到主布局中.
  自定义`View`如果继承`LinearLayout(ViewGroup)`,建议让自定义View的布局文件根布局设置成`merge`,这样能少一层结点.

* **`ViewStub`标签的使用**：最大的优点是当你需要时才会加载,使用它并不会影响UI初始化时的性能.各种不常用的布局像进度条、显示错误消息等可以使用`ViewStub`标签,以减少内存使用量,加快渲染速度.`ViewStub`是一个不可见的,实际上是把宽高设置为0的`View`.效果有点类似普通的`view.setVisible()`,但性能体验提高不少。

#### 2.3.3 布局的选择

* 减少嵌套层级
* 优先使用线性布局
* `ConstraintLayout`终极大招

### 2.4 布局加载优化方式

#### 2.4.1 采用`Java`代码加载

* `xml`由于有`IO`操作与反射操作导致加载耗时；
* 采用`Java`方式构造布局，代码量大，不容易实现，不容易维护；

**解决方法**：`github`第三方库`X2C`，其地址为 [https://github.com/TomasYu/X2C](https://github.com/TomasYu/X2C) ，其核心思路是吧`xml`翻译成`Java`文件，减少系统里有`LayoutInflate`去解析`xml`的过程。

**使用方式**：

```java
annotationProcessor 'com.zhangyue.we:x2c-apt:1.1.2'
implementation 'com.zhangyue.we:x2c-lib:1.0.6'
  
@Xml(layout="activity_main")
  
this.setContentView(R.layout.activity_main);
-->X2C.setContentView(this, R.layout.activity_main);

LayoutInflater.from(this).inflate(R.layout.activity_main,null); 
--> X2C.inflate(this,R.layout.activity_main,null);
```

**原理分析**：

①什么时候解析`xml`？

```groovy
annotationProcessor 'com.zhangyue.we:x2c-apt:1.1.2'
implementation 'com.zhangyue.we:x2c-lib:1.0.6'
```

这里指定了`annotationProcessor`，也就是注解编译处理器，即`APT`技术；`Javac`编译的时候回调用这个处理器，传入注解，解析`xml`（`X2C`通过`scanLayouts`方法扫码项目的`res/layout`中的一系列文件来找到`xml`文件）。

②找到`xml`文件后如何解析？

在`com.zhangyue.we.view.View#translate(...)`方法中通过拼接字符串的方式解析，然后统计`javapoet`技术生成`Java`代码文件。

参考：[源码解析：解析掌阅X2C 框架](https://www.cnblogs.com/caoxinyu/p/10568500.html)

#### 2.4.2 异步加载

使用`Androidx`的库`asynclayoutinflater`:

**注意：**不能有依赖于主线程的操作。

```groovy
implementation 'androidx.asynclayoutinflater:asynclayoutinflater:1.0.0'
```

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        installCustomFactory();
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //异步加载
        new AsyncLayoutInflater(this).inflate(R.layout.activity_main, null,
                new AsyncLayoutInflater.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(@NonNull View view, int resid, @Nullable ViewGroup parent) {
              	setContentView(view);
                afterSetView();
            }
        });
        //afterSetView();
    }
```

