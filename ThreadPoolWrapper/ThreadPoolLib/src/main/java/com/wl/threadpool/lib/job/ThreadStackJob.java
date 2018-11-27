package com.wl.threadpool.lib.job;

import com.wl.threadpool.lib.util.LoggerUtils;

import java.util.Map;
import java.util.Map.Entry;


/**
 * 收集所有线程的堆栈信息
 */
public class ThreadStackJob extends AbstractJob {

    /**
     * 线程堆栈缓冲区初始大小
     */
    private final static int BUFFER_SIZE = 4096;

    public ThreadStackJob(int interval) {
        super._interval = interval;
    }

    @Override
    protected void execute() {
        Map<Thread, StackTraceElement[]> stackMap = Thread.getAllStackTraces();
        for (Entry<Thread, StackTraceElement[]> entry : stackMap.entrySet()) {
            // 线程基本信息
            Thread thread = entry.getKey();
            StringBuilder buffer = new StringBuilder(BUFFER_SIZE)
                    .append("name:").append(thread.getName())
                    .append(", id:").append(thread.getId())
                    .append(", status:").append(thread.getState().toString())
                    .append(", priority:").append(thread.getPriority())
                    .append(_lineSeparator);

            // 线程堆栈
            StackTraceElement[] stackList = entry.getValue();
            for (StackTraceElement ste : stackList) {
                buffer.append(ste.toString())
                        .append(_lineSeparator);
            }

            LoggerUtils.d(buffer.toString());
        }

        super.sleep();
    }

}
