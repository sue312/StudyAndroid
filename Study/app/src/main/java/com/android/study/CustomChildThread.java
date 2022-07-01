package com.android.study;

import android.os.Handler;

/**
 * 子线程，用于处理耗时工作
 */
public class CustomChildThread extends Thread {

    private Handler mHandler;

    public CustomChildThread(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void run() {
        //模拟耗时进度，将进度值传给主线程用于更新 ProgressBar 进度。
        for (int i = 1; i <= 100; i++) {
            try {
                //让当前执行的线程（即 CustomChildThread）睡眠 1s
                Thread.sleep(100);
                mHandler.obtainMessage(i,"modify_i").sendToTarget();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
