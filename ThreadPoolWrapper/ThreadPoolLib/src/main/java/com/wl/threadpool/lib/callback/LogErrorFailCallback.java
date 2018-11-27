package com.wl.threadpool.lib.callback;


import com.wl.threadpool.lib.util.LoggerUtils;


/**
 * 当队列满，异步任务无法提交给线程池执行时，输出一条错误日志记录处理失败的任务信息。
 */
public class LogErrorFailCallback<T> implements FailCallback<T> {


    /**
     * 处理无法提交线程池执行的异步任务。
     *
     * @param task 无法提交线程池执行的异步任务
     * @return null
     */
    @Override
    public void execute(T task) {
        LoggerUtils.e("THARED_POOL ：queue is full, a task cannot be submit to threadpool, task information:{}");
    }

}
