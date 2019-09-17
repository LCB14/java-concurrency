package com.wangwenjun.concurrency.chapter13;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/***************************************
 * 自定义线程池
 * 之前只知道线程池中有线程，当有任务submit到线程池后就会被线程池中的一个线程执行，只是有一种感性的认识。
 * 通过这个实例程序（虽然简单），但是知道了线程池中线程是这样被放进去的：当向线程池中（其实一个List）放线程的时候，首先调用线程的start方法让线程启动，然后将这个线程添加的线程池中。
 * 当线程池中的线程执行任务的时候，是在线程的run方法中进行控制（判断任务队列中是否有任务等）从而实现线程池对任务的处理
 ***************************************/
public class SimpleThreadPool01 extends Thread {

    private int size;

    private final int queueSize;

    private final static int DEFAULT_SIZE = 10;

    private final static int DEFAULT_TASK_QUEUE_SIZE = 2000;

    private static volatile int seq = 0;

    private int min;

    private int max;

    private int active;

    private final String THREAD_PREFIX = "SIMPLE_THREAD_POOOL";

    private final static ThreadGroup GROUP = new ThreadGroup("pool-group-");

    private final static LinkedList<Runnable> TASK_QUEUE = new LinkedList<>();

    private final static List<WorkTask> THREAD_QUEUE = new ArrayList<>();

    public final DiscardPolicy discardPolicy;

    public final static DiscardPolicy DEFAULT_DISCARD_POLICY = () -> {
        throw new DiscardException("Discard this task");
    };

    public static boolean destroy = false;

    public SimpleThreadPool01() {
        this(4, 8, 12, DEFAULT_TASK_QUEUE_SIZE, DEFAULT_DISCARD_POLICY);
    }

    public SimpleThreadPool01(int min, int active, int max, int queueSize, DiscardPolicy discardPolicy) {
        this.min = min;
        this.max = max;
        this.active = active;
        this.size = size;
        this.queueSize = queueSize;
        this.discardPolicy = discardPolicy;
        init();
    }

    private void init() {

        for (int i = 0; i < this.min; i++) {
            createWorkTask();
        }
        this.size = min;
        this.start();
    }

    public void submit(Runnable runnable) {
        if (destroy) {
            throw new IllegalStateException("thread pool is shutdown, not allow to submit task to it ");
        }

        synchronized (TASK_QUEUE) {
            if (TASK_QUEUE.size() >= queueSize) {
                discardPolicy.discard();
            }
            TASK_QUEUE.addLast(runnable);
            TASK_QUEUE.notifyAll();
        }
    }

    @Override
    public void run() {
        while (!destroy) {
            System.out.printf("Pool#Min:%d, #Max:%d, #Active:%d, Current:%d, QueueSize:%d\n",
                    this.min, this.max, this.active, this.size, TASK_QUEUE.size());

            try {
                Thread.sleep(1_000);
                if (TASK_QUEUE.size() > active && size < active) {
                    for (int i = size; i < active; i++) {
                        createWorkTask();
                    }
                    System.out.println("The pool incremented.");
                    size = active;
                } else if (TASK_QUEUE.size() > max && size < max) {
                    for (int i = size; i < max; i++) {
                        createWorkTask();
                    }
                    System.out.println("The pool incremented to max.");
                    size = max;
                }

                // 当调用线程池的shutdown方法时，是对THREAD_QUEUE进行修改，而这里是对THREAD_QUEUE的遍历，因此会抛出异常
                // 因此需要对THREAD_QUEUE加锁进行操作
                synchronized (THREAD_QUEUE) {
                    if (TASK_QUEUE.isEmpty() && size > active) {
                        System.out.println("=============Reduce===============");
                        int releaseSize = size - active;

                        for (Iterator<WorkTask> iterator = THREAD_QUEUE.iterator(); iterator.hasNext(); ) {
                            if (releaseSize <= 0) {
                                break;
                            }
                            WorkTask task = iterator.next();
                            task.close();
                            task.interrupt();
                            iterator.remove();
                            releaseSize--;
                        }

                        size = active;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createWorkTask() {
        WorkTask task = new WorkTask(GROUP, THREAD_PREFIX + seq++);
        task.start();
        THREAD_QUEUE.add(task);
    }

    // 关闭掉线程池，即，让线程池中的线程均运行完；若没有关闭线程池的话，就算任务队列TASK_QUEUE中已经
    // 没有任务了，程序还是处于运行的状态
    public void shutDown() throws InterruptedException {
        while (!TASK_QUEUE.isEmpty()) {
            Thread.sleep(50);
        }

        int initVal = THREAD_QUEUE.size();
        while (initVal > 0) {
            for (WorkTask task : THREAD_QUEUE) {
                if (task.getTaskState() == TaskState.BLOCK) {
                    task.interrupt();
                    task.close();
                    initVal--;
                } else {
                    Thread.sleep(10);
                }
            }
        }

        this.destroy = true;
        System.out.println("thread pool is shutdown");

    }

    public int getSize() {
        return size;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public static boolean getDestroy() {
        return destroy;
    }

    private enum TaskState {
        FREE, RUNNING, BLOCK, DEAD
    }

    public static class DiscardException extends RuntimeException {
        public DiscardException(String message) {
            super(message);
        }
    }

    public interface DiscardPolicy {

        void discard() throws DiscardException;

    }

    private static class WorkTask extends Thread {

        private volatile TaskState taskState = TaskState.FREE;

        public TaskState getTaskState() {
            return this.taskState;
        }

        public WorkTask(ThreadGroup group, String name) {
            super(group, name);
        }

        @Override
        public void run() {
            OUTER:
            while (this.taskState != TaskState.DEAD) {
                Runnable runnable;
                synchronized (TASK_QUEUE) {
                    while (TASK_QUEUE.isEmpty()) {
                        try {
                            taskState = taskState.BLOCK;
                            TASK_QUEUE.wait();
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                            System.out.println("Close");
                            break OUTER;
                        }

                    }

                    runnable = TASK_QUEUE.removeFirst();
                }
                if (runnable != null) {
                    taskState = TaskState.RUNNING;
                    runnable.run();
                    taskState = TaskState.FREE;
                }
            }

        }

        public void close() {
            this.taskState = TaskState.DEAD;
        }

    }

    public static void main(String[] args) throws InterruptedException {

//        SimpleThreadPool00 simpleThreadPool = new SimpleThreadPool00(6, 10, SimpleThreadPool00.DEFAULT_DISCARD_POLICY);

        SimpleThreadPool01 simpleThreadPool = new SimpleThreadPool01();

        IntStream.rangeClosed(0, 40)
                .forEach(i -> simpleThreadPool.submit(
                        () -> {
                            System.out.println("The runnable " + i + " be serviced by " + Thread.currentThread().getName() + " START");
                            try {
                                // 目前程序遗留一个问题：当缩减线程池中的线程时，如果线程池中的被缩减的线程没有wait的话，而是要缩减的线程，调用了要执行的任务
                                // 的run方法时进行了sleep，此时调用WorkTask.interrupt方法时，在这里会抛出异常:
                                // java.lang.InterruptedException: sleep interrupted
                                //	at java.lang.Thread.sleep(Native Method)
                                //	at com.wangwenjun.concurrency.chapter13.SimpleThreadPool.lambda$main$1(SimpleThreadPool.java:242)
                                //	at com.wangwenjun.concurrency.chapter13.SimpleThreadPool$WorkerTask.run(SimpleThreadPool.java:225)
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("The runnable " + i + " be serviced by " + Thread.currentThread().getName() + " FINISHED");
                        }
                ));


        Thread.sleep(10_000);
        simpleThreadPool.shutDown();
////        simpleThreadPool.submit(() -> System.out.println("=================="));

    }
}
