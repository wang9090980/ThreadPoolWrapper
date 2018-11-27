package com.wl.threadpool.lib.config;

import com.wl.threadpool.lib.callback.Callback;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ThreadPoolConfig implements Callback {

    /**
     * key为线程池名称，value为{@link ThreadPoolInfo}实例。
     */
    protected Map<String, ThreadPoolInfo> _multiThreadPoolInfo = new HashMap<String, ThreadPoolInfo>();

    /**
     * 线程池状态收集开关
     */
    protected boolean _threadPoolStateSwitch = false;
    protected int _threadPoolStateInterval = 60;   // 单位：秒

    /**
     * 线程状态收集开关
     */
    protected boolean _threadStateSwitch = false;
    protected int _threadStateInterval = 60;   // 单位：秒

    /**
     * 线程堆栈收集开关
     */
    protected boolean _threadStackSwitch = false;
    protected int _threadStackInterval = 60;   // 单位：秒

    private static final String DEFAULT_THREAD_POOL = "default";


    @Override
    public void init(Map<String, ThreadPoolInfo> multiThreadPoolInfo) {

        if (multiThreadPoolInfo != null && !multiThreadPoolInfo.isEmpty()) {
            this._multiThreadPoolInfo.putAll(multiThreadPoolInfo);
        }
        _multiThreadPoolInfo.put(DEFAULT_THREAD_POOL, new ThreadPoolInfo());

    }


    /**
     * 指定名称的线程池的配置是否存在。
     *
     * @return 如果指定名称的线程池的配置存在返回true，如果不存在返回false；如果传入的线程池名称为null也返回false。
     */
    public boolean containsPool(String poolName) {
        if (null == poolName || null == _multiThreadPoolInfo || _multiThreadPoolInfo.isEmpty()) {
            return false;
        }

        return _multiThreadPoolInfo.containsKey(poolName);
    }

    /**
     * 获取指定线程池的配置信息。
     *
     * @param threadpoolName 线程池名称
     * @return 线程池配置信息（{@link ThreadPoolInfo}）
     */
    public ThreadPoolInfo getThreadPoolConfig(String threadpoolName) {
        return _multiThreadPoolInfo.get(threadpoolName);
    }

    /**
     * 获取所有线程池的配置信息。
     *
     * @return 线程池配置信息（{@link ThreadPoolInfo}）集合
     */
    public Collection<ThreadPoolInfo> getThreadPoolConfig() {
        return _multiThreadPoolInfo.values();
    }

    /**
     * @return 输出各个线程池状态信息的开关，true表示开，false表示关
     */
    public boolean getThreadPoolStateSwitch() {
        return _threadPoolStateSwitch;
    }

    /**
     * @return 线程池状态信息输出的间隔时间（单位：秒）
     */
    public int getThreadPoolStateInterval() {
        return _threadPoolStateInterval;
    }

    /**
     * @return 输出各个线程组中线程状态信息的开关，true表示开，false表示关
     */
    public boolean getThreadStateSwitch() {
        return _threadStateSwitch;
    }

    /**
     * @return 线程状态信息输出的间隔时间（单位：秒）
     */
    public int getThreadStateInterval() {
        return _threadStateInterval;
    }

    /**
     * @return 输出所有线程堆栈的开关，true表示开，false表示关
     */
    public boolean getThreadStackSwitch() {
        return _threadStackSwitch;
    }

    /**
     * @return 线程堆栈信息输出的间隔时间（单位：秒）
     */
    public int getThreadStackInterval() {
        return _threadStackInterval;
    }

    @Override
    public void destroy() {
        _threadPoolStateSwitch = false;
        _threadStateSwitch = false;
        _multiThreadPoolInfo.clear();
    }

}
