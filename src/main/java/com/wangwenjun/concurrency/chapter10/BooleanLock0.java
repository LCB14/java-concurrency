package com.wangwenjun.concurrency.chapter10;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/***************************************
 * @author:Alex Wang
 * @Date:2017/2/22 QQ:532500648
 * QQ交流群:286081824
 ***************************************/
public class BooleanLock0 implements Lock {

    //The initValue is true indicated the lock have be get.
    //The initValue is false indicated the lock is free (other thread can get this.)
    private boolean initValue;

    private Collection<Thread> blockedThreadCollection = new ArrayList<>();

    private Thread currentThread;

    public BooleanLock0() {
        this.initValue = false;
    }

    @Override
    public synchronized void lock() throws InterruptedException {
        while (initValue) {
            blockedThreadCollection.add(Thread.currentThread());
            this.wait();
        }

        blockedThreadCollection.remove(Thread.currentThread());
        this.initValue = true;
        currentThread = Thread.currentThread();
    }

    @Override
    public synchronized void lock(long mills) throws InterruptedException, TimeOutException {
        
    }

    @Override
    public synchronized void unlock() {
        if(currentThread == Thread.currentThread()){
            initValue = false;
            Optional.of(Thread.currentThread().getName() + " release the lock monitor.")
                        .ifPresent(System.out::println);
            this.notifyAll();
        }
    
    }

    @Override
    public Collection<Thread> getBlockedThread() {
        return Collections.unmodifiableCollection(blockedThreadCollection);
    }

    @Override
    public int getBlockedSize() {
        return blockedThreadCollection.size();
    }
}
