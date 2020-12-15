[TOC]

## 二、布局优化

### 2.1 `Android`绘制原理

#### 2.1.1 `CPU`和`GPU`结构

渲染操作通常依赖于两个核心组件：`CPU`与`GPU`，其结构如下图所示：

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/android_draw_cpu_gpu_structure.png)

> 1. 蓝色的`Control`为控制器，用于协调控制整个`CPU`的运行，包括取出指令、控制其他模块的运行等；
> 2. 绿色的`ALU(Arithmetic Logic Unit)`为算术逻辑单元，用于数学以及逻辑运算；
> 3. 橙色的`Cache`和`DRAM`分别为缓存和`RAM`，用户存储信息；

`CPU`控制器比较复杂，`ALU`数量较少，因此`CPU`擅长各种复杂的逻辑运算，但不擅长数学尤其是浮点运算。

#### 2.1.2 `CPU`和`GPU`功能

`CPU`负责包括`Measure`,`Layout`,`Record`,`Execute`等操作，`GPU`负责`Rasterization`(栅格化)操作。

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/android_draw_cpu_gpu_function.png)

#### 2.1.3 `XML`布局显示到屏幕的流程

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/layout_draw_screen_process.png)

#### 2.1.4 为什么是`60fps`

* 人眼与大脑之间的协作无法感知超过`60fps`的画面更新；
* `12fps`：手动快速翻动书籍的帧率；
* `24fps`：电影使用的频率；
* `30fps`：实时音视频的帧率；
* `60fps`：手机交互过程中，需要触摸和反馈，需要60帧才能到达不卡顿的效果。

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

#### 2.1.5 `VSYNC`

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
* 按需显示占位图片;

```java
if(chat.getAuthor().getAvatarId() == 0) {
  Picasso.with(getContext()).load(android.R.color.transparent).into(char_author_avatar);
  chat_author_avatar.setBackgroundColor(chat.getAuthor().getColor());
}else {
  Picasso.with(getContext()).load(chat.getAuthor().getAvatarId()).into(chat_author_avatar);
  chat_author_avatar.setBackgroundColor(Color.TRANSPARENT);
}
```

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