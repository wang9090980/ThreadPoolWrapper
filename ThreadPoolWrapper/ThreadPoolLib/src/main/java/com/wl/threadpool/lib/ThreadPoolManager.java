package com.wl.threadpool.lib;


import com.wl.threadpool.lib.callback.Callback;
import com.wl.threadpool.lib.config.ThreadPoolInfo;

import java.util.Map;

/**
 * 线程池实例管理。
 */
public class ThreadPoolManager implements Callback {

    private Callback _threadPool = new ThreadPoolImpl();

    private static Object _lock = new Object();
    private boolean _initStatus = false;
    private boolean _destroyStatus = false;

    private static ThreadPoolManager _instance = new ThreadPoolManager();


    public static ThreadPoolManager getInstance() {
        return _instance;
    }

    public ThreadPool getThreadPool() {
        return (ThreadPool) _threadPool;
    }

    protected void setThreadPool(ThreadPool threadPool) {
        this._threadPool = (Callback) threadPool;
    }

    @Override
    public void init(Map<String, ThreadPoolInfo> threadPoolInfoMap) {
        synchronized (_lock) {
            if (_initStatus) {
                return;
            }
            _threadPool.init(threadPoolInfoMap);
            _initStatus = true;
        }
    }

    @Override
    public void destroy() {
        synchronized (_lock) {
            if (_destroyStatus) {
                return;
            }
            _threadPool.destroy();
            _destroyStatus = true;
        }
    }

}
