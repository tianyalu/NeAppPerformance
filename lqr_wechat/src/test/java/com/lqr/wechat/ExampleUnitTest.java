package com.lqr.wechat;

import com.lqr.wechat.utils.SortUtils;

import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);


    }
    @Test
    public void testWeakReference() throws InterruptedException {
        ReferenceQueue<Object> referenceQueuee=new ReferenceQueue<>();
        Object weakObject=new Object();
        //弱引用
        WeakReference weakReference=new WeakReference(weakObject,referenceQueuee);
        System.out.println("WeakReference:"+weakReference.get());
        System.out.println("referenceQueuee:"+referenceQueuee.poll());

        weakObject=null;
        System.gc();
        Thread.sleep(2000);
        System.out.println("WeakReference:"+weakReference.get());
        System.out.println("referenceQueuee:"+referenceQueuee.poll());
    }



    @Test
    public void testPhantomReference() throws InterruptedException {
        //虚引用：功能，不会影响到对象的生命周期的，
        // 但是能让程序员知道该对象什么时候被 回收了
        ReferenceQueue<Object> referenceQueuee=new ReferenceQueue<>();
        Object phantomObject=new Object();
        PhantomReference phantomReference=new PhantomReference(phantomObject,referenceQueuee);
        phantomObject=null;
        System.out.println("phantomObject:"+phantomObject);//null
        System.out.println("phantomReference"+referenceQueuee.poll());//null
        System.gc();
        Thread.sleep(2000);
        System.out.println("referenceQueuee:"+referenceQueuee.poll());
    }

}








