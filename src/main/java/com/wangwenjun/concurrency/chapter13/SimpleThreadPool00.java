package com.wangwenjun.concurrency.chapter13;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/***************************************
 * 自定义线程池
 * 之前只知道线程池中有线程，当有任务submit到线程池后就会被线程池中的一个线程执行，只是有一种感性的认识。
 * 通过这个实例程序（虽然简单），但是知道了线程池中线程是这样被放进去的：当向线程池中（其实一个List）放线程的时候，首先调用线程的start方法让线程启动，然后将这个线程添加的线程池中。
 * 当线程池中的线程执行任务的时候，是在线程的run方法中进行控制（判断任务队列中是否有任务等）从而实现线程池对任务的处理
 ***************************************/
public class SimpleThreadPool00 extends Thread {

    private final int size;

    private final int queueSize;

    private final static int DEFAULT_SIZE = 10;

    private final static int DEFAULT_TASK_QUEUE_SIZE = 2000;

    private static volatile int seq = 0;

    private final String THREAD_PREFIX = "SIMPLE_THREAD_POOOL";

    private final static ThreadGroup GROUP = new ThreadGroup("pool-group-");

    private final static LinkedList<Runnable> TASK_QUEUE = new LinkedList<>();

    private final static List<WorkTask> THREAD_QUEUE = new ArrayList<>();

    public final DiscardPolicy discardPolicy;

    public final static DiscardPolicy DEFAULT_DISCARD_POLICY = () -> {
      throw new DiscardException("Discard this task");
    };

    public SimpleThreadPool00() {
        this(DEFAULT_SIZE, DEFAULT_TASK_QUEUE_SIZE, DEFAULT_DISCARD_POLICY);
    }

    public SimpleThreadPool00(int size, int queueSize, DiscardPolicy discardPolicy) {
        this.size = size;
        this.queueSize = queueSize;
        this.discardPolicy = discardPolicy;
        init();
    }

    private void init() {

        for (int i = 0; i < size; i++) {
            createWorkTask();
        }
    }

    public void submit(Runnable runnable) {
        synchronized (TASK_QUEUE) {
            if (TASK_QUEUE.size() >= queueSize) {
                discardPolicy.discard();
            }
            TASK_QUEUE.addLast(runnable);
            TASK_QUEUE.notifyAll();
        }
    }

    private void createWorkTask() {
        WorkTask task = new WorkTask(GROUP, THREAD_PREFIX + seq++);
        task.start();
        THREAD_QUEUE.add(task);
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
                            e.printStackTrace();
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

    public static void main(String[] args) {

        SimpleThreadPool00 simpleThreadPool = new SimpleThreadPool00(6, 10, SimpleThreadPool00.DEFAULT_DISCARD_POLICY);

        IntStream.rangeClosed(0, 40)
                .forEach(i -> simpleThreadPool.submit(
                        () -> {
                            System.out.println("The runnable " + i + " be serviced by " + Thread.currentThread().getName() + " START");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("The runnable " + i + " be serviced by " + Thread.currentThread().getName() + " FINISHED");
                        }
                ));

    }
}
