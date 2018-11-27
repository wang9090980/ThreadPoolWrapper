package com.wl.threadpool.lib;

import android.text.TextUtils;

import com.wl.threadpool.lib.callback.Callback;
import com.wl.threadpool.lib.callback.FailCallback;
import com.wl.threadpool.lib.config.ThreadPoolConfig;
import com.wl.threadpool.lib.config.ThreadPoolInfo;
import com.wl.threadpool.lib.factory.DefaultThreadFactory;
import com.wl.threadpool.lib.job.ThreadPoolStateJob;
import com.wl.threadpool.lib.job.ThreadStackJob;
import com.wl.threadpool.lib.job.ThreadStateJob;
import com.wl.threadpool.lib.job.status.ThreadPoolStatus;
import com.wl.threadpool.lib.util.LoggerUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 多线程池。
 */
public class ThreadPoolImpl implements Callback, ThreadPool {

    /**
     * 默认的线程池名称
     */
    private static final String DEFAULT_THREAD_POOL = "default";

    protected ThreadPoolConfig _threadPoolConfig = new ThreadPoolConfig();
    protected int _status = ThreadPoolStatus.UNINITIALIZED;

    Map<String, ExecutorService> _multiThreadPool = new HashMap<String, ExecutorService>();
    ThreadPoolStateJob _threadPoolStateJob;
    ThreadStateJob _threadStateJob;
    ThreadStackJob _threadStackJob;

    public ThreadPoolImpl() {
        // nothing
    }

    @Override
    public void init(Map<String, ThreadPoolInfo> threadPoolInfoMap) {
        if (ThreadPoolStatus.UNINITIALIZED != _status) {
            return;
        }
        try {
            initThreadPool(threadPoolInfoMap);
            startThreadPoolStateJob();
            startThreadStateJob();
            startThreadStackJob();
            _status = ThreadPoolStatus.INITIALITION_SUCCESSFUL;
        } catch (RuntimeException e) {
            _status = ThreadPoolStatus.INITIALITION_FAILED;
            throw e;
        }
    }

    /**
     * 初始化所有线程池。
     */
    private void initThreadPool(Map<String, ThreadPoolInfo> threadPoolInfoMap) {
        _threadPoolConfig.init(threadPoolInfoMap);

        Collection<ThreadPoolInfo> threadPoolInfoList = _threadPoolConfig.getThreadPoolConfig();
        for (ThreadPoolInfo threadPoolInfo : threadPoolInfoList) {
            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(threadPoolInfo.getQueueSize());
            ThreadPoolExecutor threadPool = new ThreadPoolExecutor(threadPoolInfo.getCoreSize(),
                    threadPoolInfo.getMaxSize(),
                    threadPoolInfo.getThreadKeepAliveTime(), TimeUnit.SECONDS, workQueue,
                    new DefaultThreadFactory(threadPoolInfo.getName()));
            _multiThreadPool.put(threadPoolInfo.getName(), threadPool);
            LoggerUtils.i("initialization thread pool '{}' success" + threadPoolInfo.getName());
        }
    }

    /**
     * 初始化并启动线程池状态统计Job。
     */
    private void startThreadPoolStateJob() {
        if (!_threadPoolConfig.getThreadPoolStateSwitch()) {
            return;
        }

        _threadPoolStateJob = new ThreadPoolStateJob(
                _multiThreadPool,
                _threadPoolConfig.getThreadPoolStateInterval());
        _threadPoolStateJob.initJob();
        Thread jobThread = new Thread(_threadPoolStateJob);
        jobThread.setName("threadpool-state-job");
        jobThread.start();

        LoggerUtils.i("start job success");
    }

    /**
     * 初始化并启动线程状态统计Job。
     */
    private void startThreadStateJob() {
        if (!_threadPoolConfig.getThreadStateSwitch()) {
            return;
        }

        _threadStateJob = new ThreadStateJob(_threadPoolConfig.getThreadStateInterval());
        _threadStateJob.initJob();
        Thread jobThread = new Thread(_threadStateJob);
        jobThread.setName("thread-state-job");
        jobThread.start();
    }

    private void startThreadStackJob() {
        if (!_threadPoolConfig.getThreadStackSwitch()) {
            return;
        }

        _threadStackJob = new ThreadStackJob(_threadPoolConfig.getThreadStackInterval());
        _threadStackJob.initJob();
        Thread jobThread = new Thread(_threadStackJob);
        jobThread.setName("thread-stack-job");
        jobThread.start();

    }

    public Future<?> submit(Runnable task) {
        return submit(task, DEFAULT_THREAD_POOL);
    }

    public Future<?> submit(Runnable task, String threadpoolName) {
        if (null == task) {
            throw new IllegalArgumentException("task is null");
        }

        ExecutorService threadPool = getExistsThreadPool(threadpoolName);

        return threadPool.submit(task);
    }

    @Override
    public Future<?> submit(
            Runnable task, String threadpoolName,
            FailCallback<Runnable> failHandler) {
        try {
            return submit(task, threadpoolName);
        } catch (RejectedExecutionException e) {
            if (null != failHandler) {
                failHandler.execute(task);
            }
        }

        return null;
    }

    ExecutorService getThreadPool(String threadpoolName) {
        if (TextUtils.isEmpty(threadpoolName)) {
            throw new IllegalArgumentException("thread pool name is empty");
        }

        ExecutorService threadPool = _multiThreadPool.get(threadpoolName);

        return threadPool;
    }

    private ExecutorService getExistsThreadPool(String threadpoolName) {
        ExecutorService threadPool = getThreadPool(threadpoolName);
        if (null == threadPool) {
            throw new IllegalArgumentException(String.format("thread pool %s not exists", threadpoolName));
        }

        return threadPool;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return submit(task, DEFAULT_THREAD_POOL);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task, String threadpoolName) {
        if (null == task) {
            throw new IllegalArgumentException("task is null");
        }

        ExecutorService threadPool = getExistsThreadPool(threadpoolName);

        return threadPool.submit(task);
    }

    @Override
    public <T> Future<T> submit(
            Callable<T> task, String threadpoolName,
            FailCallback<Callable<T>> failHandler) {
        try {
            return submit(task, threadpoolName);
        } catch (RejectedExecutionException e) {
            if (null != failHandler) {
                failHandler.execute(task);
            }
        }

        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            Collection<Callable<T>> tasks,
            long timeout, TimeUnit timeoutUnit) {
        return invokeAll(tasks, timeout, timeoutUnit, DEFAULT_THREAD_POOL);
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            Collection<Callable<T>> tasks,
            long timeout, TimeUnit timeoutUnit, String threadpoolName) {
        if (null == tasks || tasks.isEmpty()) {
            throw new IllegalArgumentException("task list is null or empty");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout less than or equals zero");
        }

        ExecutorService threadPool = getExistsThreadPool(threadpoolName);

        try {
            return threadPool.invokeAll(tasks, timeout, timeoutUnit);
        } catch (InterruptedException e) {
            LoggerUtils.e("invoke task list occurs error", e);
        }

        return null;
    }

    @Override
    public boolean isExists(String threadpoolName) {
        ExecutorService threadPool = getThreadPool(threadpoolName);

        return (null == threadPool ? false : true);
    }

    @Override
    public ThreadPoolInfo getThreadPoolInfo(String threadpoolName) {
        ThreadPoolInfo info = _threadPoolConfig.getThreadPoolConfig(threadpoolName);

        return info.clone();
    }

    @Override
    public void destroy() {
        if (ThreadPoolStatus.DESTROYED == _status) {
            return;
        }

        for (Entry<String, ExecutorService> entry : _multiThreadPool.entrySet()) {
            LoggerUtils.i("shutdown the thread pool '{}'" + entry.getKey());
            entry.getValue().shutdown();
        }

        if (null != _threadPoolStateJob) {
            _threadPoolStateJob.destroy();
            _threadPoolStateJob = null;
        }

        if (null != _threadStateJob) {
            _threadStateJob.destroy();
            _threadStateJob = null;
        }

        if (null != _threadStackJob) {
            _threadStackJob.destroy();
            _threadStackJob = null;
        }

        _threadPoolConfig.destroy();
        _status = ThreadPoolStatus.DESTROYED;
    }

}
