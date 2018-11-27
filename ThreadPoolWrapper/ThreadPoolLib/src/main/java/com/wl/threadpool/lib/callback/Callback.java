package com.wl.threadpool.lib.callback;

import com.wl.threadpool.lib.config.ThreadPoolInfo;

import java.util.Map;

/**
 * 组件生命周期定义。提供初始化和销毁两个方法。
 */
public interface Callback {

    /**
     * 初始化资源。
     */
    public void init(Map<String, ThreadPoolInfo> threadPoolInfoMap);

    /**
     * 释放资源。
     */
    public void destroy();

}