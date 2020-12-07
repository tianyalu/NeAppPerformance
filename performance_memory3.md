[TOC]

## 三、内存优化

### 3.1 `Android`内存管理机制

#### 3.1.1 `Java`虚拟机概念

`Java`虚拟机是一台“抽象的计算机”，它拥有自己的处理器，堆栈，寄存器以及相应的指令系统；`Java`虚拟机屏蔽了与具体操作系统相关的平台信息，使得`Java`程序只需要生成在该虚拟机上运行的目标代码，就可以在多平台上运行。虽然叫`Java`虚拟机，但在它之上运行的语言不仅有`Java`、`Kotlin`、`Groovy`、`Scala`等都可以运行。

`Java`虚拟机包括：类加载系统、运行时区域、执行引擎、本地方法库等。

#### 3.1.2 `Java`虚拟机执行流程

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/jvm_process.png)

#### 3.1.3 `Java`虚拟机运行时数据区域

> 1. 方法区（公有）：被`JVM`加载的类的结构信息，包括运行时常量池、字段、方法信息、静态变量等数据；
> 2. `Java`堆（公有）：存储几乎所有对象的实例
> 3. `Java`虚拟机栈（私有）：存储`Java`方法调用的状态，栈帧；
> 4. 本地方法栈（私有）：执行`native`方法；
> 5. 程序计数器（私有）：多线程中记录程序执行下一条指令的计数器。

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/jvm_runtime_data_area.png)

> * 线程独自：每个线程都会有它独立的空间，随线程生命周期而创建和销毁；
> * 线程共享：所有线程能访问这块内存数据，随虚拟机或者`GC`而创建和销毁。

#### 3.1.4 强软弱虚引用

* 强引用：当新建的对象为强引用时，垃圾回收器绝对不会回收它，宁愿抛出`OutOfMemoryError`异常，让程序异常终止也不会回收；
* 软引用：当新建的对象为软引用时，在内存不足时，回收器就会回收这些对象，如果回收后还是没有足够的内存，抛出`OutOfMemoryError`异常；
* 弱引用：当新建的对象为弱引用时，垃圾回收器不管当前内存是否足够，都会回收它的内存；
* 虚引用：虚引用跟其他引用都不同，如果一个对象仅持有虚引用，在任何时候都可能被`GC`回收，只是当它被回收时会收到一个系统通知。

#### 3.1.5 垃圾标记算法

* 引用计数算法：每个对象都有一个引用计数器，当对象每被引用一次时就加1，引用失效时就减1；当计数为0时则将该对象设置为可回收的“垃圾对象”； 缺点：循环引用不能回收；
* 可达性分析算法：将对象及其引用关系看做一个图，选定活动对象作为`GC Roots`，然后跟踪引用链条，如果一个对象和`GC Roots`之间不可达，也就是不存在引用，那么认为是可回收对象。

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/gc_roots.png)

可以作为`GC Roots`的对象：

> 1. 虚拟机栈中正在引用的对象；
> 2. 本地方法栈中正在引用的对象；
> 3. 静态属性引用的对象；
> 4. 方法区常量引用的对象；

#### 3.1.6 垃圾收集算法

* 标记-清除算法：用根搜索算法标记可被回收的对象，之后将被标记为“垃圾”的对象进行回收； --> **内存碎片**
* 复制算法(年轻代)：先把内存一分为二，每次只使用其中一个区域，垃圾收集时，将存活的对象拷贝到另一个区域，然后对之前的对象全部回收；--> **减小了内存使用空间**
* 标记-压缩算法(老年代)：在标记可回收的对象后，将所有的存活对象压缩到内存的一段，让它们排在一个，然后对边界以外的内存进行回收；
* 分代收集算法：`Java`堆中存在的对象生命周期有较大差别，大部分生命周期很短，有的很长，设置与应用程序或者`Java`虚拟机生命周期一样。因为分代算法就是根据对象的生命周期长短，将对象放到不同的区域；

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/gc_generation.png)

**堆区**：堆区分为年轻代和老年代，其空间大小理论比值为2：1；其中年轻代又会分为`Eden`区和`Survivor`区，其空间大小理论比值为8：2；`Surfivo`r区又分为`from`区和`to`区，其空间大小理论比值为1：1。
**`gc`流程**：创建对象时首先会被放入`Eden`区，该区存满时会触发`gc`，`gc`时清除可回收对象，然后把`Eden`区剩余存活对象移动到`From`区；新创建的对象会继续被放入`Eden`区，第二次`gc`时清除`Eden`区和和`From`区可回收对象，然后把`Eden`区和`From`区剩余存活对象移动到`To`区；第三次`gc`时会把`Eden`区和`To`区中剩余存活对象移动到`From`区……依次反复进行。

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/gc_process.png)

**从年轻代进入老年代的条件**：
	大对象，大对象会直接进入老年代；
	每次`gc`时会对已存活对象进行标记（每次+1），标记达到一定次数（`Java`为15次，`Android`的`CMS`垃圾回收器为6次）时该对象会从年轻代进入老年代；
	`Survivor`区中`From`或`To`区中的相同标记（相同年龄）对象大小总和大于等于`From`或`To`区的一半时，这些对象可以进入老年代。
	在`Java`环境的`bin`目录下有一个` jvisualvm `工具，该工具可以观察到程序运行过程中内存的动态情况，从而证实上述描述。

### 3.2 内存抖动

内存抖动通常指在短时间内发生了多次内存的分配和释放，主要原因是短时间内频繁地创建对象。为了应对这种情况，虚拟机会频繁地触发`GC`操作，当`GC`进行时，其它线程会被挂起等待`GC`完成，频繁`GC`会使`UI`在绘制时超过`16ms`一帧，从而导致画面卡顿。

#### 3.2.1 内存抖动测试

```java
public class ChurnActivity extends AppCompatActivity {
    private Handler mHandler;
    private Button btnChurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_churn);

        initView();
        setViewsListener();
    }

    private void initView() {
        btnChurn = findViewById(R.id.btn_churn);
        mHandler = new Handler();
    }


    private void setViewsListener() {
        btnChurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                churn(0);
            }
        });
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            allocate();
        }
    };

    private void allocate() {
        for (int i = 0; i < 1000; i++) {
            String ob[] = new String[10000];
        }
        churn(50);
    }

    private void churn(int delay) {
        mHandler.postDelayed(r, delay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(r);
    }
}
```

#### 3.2.2 内存抖动分析

使用`Profiler`查看内存抖动：

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/memory_churn.png)

### 3.3 内存泄漏及排查工具的使用

* 内存泄漏：一个不再被程序使用的对象或变量依旧存活在内存中无法被回收；
* 内存溢出：当程序申请内存时，没有足够的内存供程序使用；

比较小的内存泄漏并不会有太大的影响，但内存泄漏多了，占用的内存空间就更大，程序正常需要申请的内存则会相应减少。

内存泄漏分析工具使用`MAT`，它除了可以分析内存泄漏之外，还可以分析大对象。

#### 3.3.1 下载`MAT`

官方下载地址：[https://www.eclipse.org/mat/downloads.php](https://www.eclipse.org/mat/downloads.php) 。

#### 3.3.2 安装`MAT`

下载的是`MAC`版，安装时遇到一个问题：  

```java
The platform metadata area could not be written: /private/var/folders/9j/zj116b2n765fkk7qm1s7ctq8000
```

解决方法：在应用程序中右键`mat.app`-->显示包内容-->`Contents/Eclipse/MemoryAnalyzer.ini`，修改内容如下：  

![image](https://github.com/tianyalu/XxtJvmMemory/raw/master/show/mat_install_problem.png)  

#### 3.3.3 获取`hprof`文件

借助`Android Studio` 的`Profile`工具，在操作页面之前`dump`（截取该时间点内存中存在的对象）一份文件，操作页面（比如进入`SecondActivity` 然后再返回主页面）之后再`dump`一份文件。然后把这两份文件（`memory1.hprof，memory2.hprof`）保存到本地。

![image](https://github.com/tianyalu/XxtJvmMemory/raw/master/show/dump.png)  

#### 3.3.4 转换`hprof`文件

使用`Android SDK`环境`sdk/platform-tools/`目录下的`hprof-conv`工具将3.3.3获取的`hprof`文件转换为`MAT`可以识别的文件：  

```bash
hprof-conv -z memory1.hprof memory1_after.hprof
hprof-conv -z memory2.hprof memory2_after.hprof
```

#### 3.3.5 `Mat`分析`hprof`文件

首先用`Mat`打开（`Open Heap Dump..`）两个转换后的`hprof`文件：  

![image](https://github.com/tianyalu/XxtJvmMemory/raw/master/show/mat_open_hprof.png)  

选择直方图：  

![image](https://github.com/tianyalu/XxtJvmMemory/raw/master/show/mat_histogram.png)  

排除其他引用：  

![image](https://github.com/tianyalu/XxtJvmMemory/raw/master/show/mat_exclude_other_references.png)  

定位结果：  

![image](https://github.com/tianyalu/XxtJvmMemory/raw/master/show/mat_memory_leak_result.png)  

因为我们进入`SecondActivity`之后又退出页面了，按道理其不应该存在，但此时排除其他引用之后发现它仍然存活，由此可以判断内存泄漏。从上图可以看出这是匿名内部类持有外部类引用引起的内存泄漏，需要在页面销毁时结束动画。

参考：[`JVM`共享区深入了解及内存抖动/泄漏排查优化](https://github.com/tianyalu/XxtJvmMemory)

### 3.4 内存优化总结

#### 3.4.1 工具使用

* 使用`Memory profiler`检测内存抖动；
* 使用`MAT`检测内存泄漏；
* 使用`LeakCannary`线下监控；
* 采用`Glide`等三方库加载图片。

#### 3.4.2 优化点

* 避免在`for`循环里分配对象占用内存；
* 自定义`View`的`onDraw`方法避免执行复杂的方法与创建对象；
* 采用对象池模型解决频繁创建与销毁；
* 对`bitmap`做缩放，重用`bitmap`；
* 配置`LargeHeap`属性；
* 在`onTrimMemory`进行处理；
* 使用松散数组：`SparseArray`,`ArrayMap`。



