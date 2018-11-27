package com.wl.threadpool.lib.job;

import com.wl.threadpool.lib.job.status.ThreadStateInfo;
import com.wl.threadpool.lib.util.LoggerUtils;
import com.wl.threadpool.lib.util.ThreadUtil;

import java.util.Map;
import java.util.Map.Entry;


/**
 * 收集所有线程组中所有线程的状态信息，统计并输出汇总信息。
 */
public class ThreadStateJob extends AbstractJob {

    public ThreadStateJob(int interval) {
        super._interval = interval;
    }

    @Override
    protected void execute() {
        Map<String, ThreadStateInfo> statMap = ThreadUtil.statAllGroupThreadState();

        for (Entry<String, ThreadStateInfo> entry : statMap.entrySet()) {
            ThreadStateInfo stateInfo = entry.getValue();

            LoggerUtils.d("ThreadGroup:" + entry.getKey() + ", "
                    + " " + "Runnable:" + stateInfo.getRunnableCount()
                    + "New:" + stateInfo.getNewCount() + ", "
                    + "Blocked:" + stateInfo.getBlockedCount() + ", "
                    + "Waiting:" + stateInfo.getWaitingCount());
        }

        super.sleep();
    } // end of execute

}
