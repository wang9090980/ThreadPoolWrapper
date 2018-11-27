package com.wl.thread.wrapper.demo;

import java.util.concurrent.Callable;

/**
 * 需要返回值的异步任务。
 */
public class CallableAnsyTask implements Callable<Long> {

    private int[] _arr;
    
    public CallableAnsyTask(int[] arr) {
        _arr = arr;
    }
    
    @Override
    public Long call() throws Exception {
        long result = 0;
        for (int i = 0; i < _arr.length; i++) {
            result += _arr[i];
        }
        
        return result;
    }

}
