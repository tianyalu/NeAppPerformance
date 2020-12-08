[TOC]

## 四、线程优化

### 4.1 `Android`线程调度机制

#### 4.1.1 线程调度机制

* **分时调度模型**：所有的线程轮流获得`cpu`使用权，平均分配每个线程占用的`cpu`时间；
* **抢占式调度模型**：优先让可运行池中的优先级高的线程占用`cpu`，优先级相同的随机选择一个线程。

#### 4.1.2 线程优先级

`Android`可以通过`android.os.Process.setThreadPriority(int)`设置线程优先级，参数范围`-20~24`,数值越小优先级越高，默认优先级为0.

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/thread_priority1.png)

默认情况下，新创建的线程的优先级默认与母线程一致。

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/thread_priority2.png)

**线程分组**：`Android`系统会根据当前运行的可见的程序和不可见的后台程序对线程进行归类：

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/thread_group.png)

### 4.2 `Android`异步方式

#### 4.2.1 异步方式

* **`Thread`**：直接创建一个线程；

* **`AsyncTask`**：为`UI`线程与工作线程之间进行快速地切换提供一种简单便捷的机制，适用于当下立即需要启动，但是异步执行的生命周期短暂的使用场景；

  ①默认线程调度执行，会阻塞后续任务②可以指定线程池并发调度：

  ![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/async_task.png)

* **`HandlerThread`**：为某些回调方法或者等待某些任务的执行设置一个专属的线程，并提供线程任务的调度机制，适用场景：图片采集、视频采集等；

* **`ThreadPool`**：把任务分解成不同的单元，分发到各个不同的线程上，进行同步并发处理；

* **`IntentService`**：适合于执行由`UI`触发的后台`Service`任务，并可以把后台任务执行的情况通过一定的机制反馈给`UI`；

  继承自普通`service`，内部使用了`HandlerThread`，在`onHandlerIntent`的回调里面处理任务，也不受页面生命周期影响：

![image](https://github.com/tianyalu/NeAppPerformance/raw/master/show/intent_service.png)

* **`Loaders`**：`Android`系统提供了`LoaderManager`；

### 4.3 `Android`线程优化

* 线程指定线程名，对线程分组便于问题排查；
* 合理指定线程优先级；
* 采用线程池复用线程；
* 合适的场景选择合适的异步方式；
* 控制整个应用总线程数量，应用各个模块复用线程；
* 不再使用的`HandlerThread`需要退出；
* 不用直接使用创建线程，应该使用线程池