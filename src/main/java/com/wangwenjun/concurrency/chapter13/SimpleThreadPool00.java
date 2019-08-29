package com.wangwenjun.concurrency.chapter13;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/***************************************
 * @author:Alex Wang
 * @Date:2017/2/25 QQ:532500648
 * QQ交流群:286081824
 ***************************************/
public class SimpleThreadPool00 extends Thread {

    private final int size;

    private final static int DEFAULT_SIZE = 10;

    private static volatile int seq = 0;

    private final String THREAD_PREFIX = "SIMPLE_THREAD_POOOL";

    private final static ThreadGroup GROUP = new ThreadGroup("pool-group-");

    private final static LinkedList<Runnable> TASK_QUEUE = new LinkedList<>();

    private final static List<WorkTask> THREAD_QUEUE = new ArrayList<>();

    public SimpleThreadPool00() {
        this(DEFAULT_SIZE);
    }

    public SimpleThreadPool00(int size) {
        this.size = size;
        init();
    }

    private void init() {

        for (int i = 0; i < size; i++) {
            createWorkTask();
        }
    }

    public void submit(Runnable runnable) {
        synchronized (TASK_QUEUE) {
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

        SimpleThreadPool00 simpleThreadPool = new SimpleThreadPool00();

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
