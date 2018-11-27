package com.wl.threadpool.lib.callback;


/**
 * 当队列满，异步任务无法提交给线程池执行时，丢弃此任务并不做任何记录。
 */
public class DiscardFailCallback<T> implements FailCallback<T> {

    /**
     * 处理无法提交线程池执行的异步任务。
     *
     * @param task 无法提交线程池执行的异步任务
     * @return null
     */
    @Override
    public void execute(T task) {
        // nothing
    }

}
