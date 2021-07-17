package com.gupaoedu.spring.juc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockQueueTest {

    public static void main(String[] args) throws Exception {
//        test1();
        test2();
    }
    public static void test1(){
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(10);
        blockingQueue.offer("apple");
        System.out.println("1111111"+222222222);
        System.out.println(blockingQueue.poll());
    }

    public static void test2() throws InterruptedException {
        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
        linkedBlockingQueue.offer("apple");
        System.out.println(linkedBlockingQueue.take());
        System.out.println("-------------喜欢就搞,高翻天");
    }
}
