# ThreadPoolWrapper

1、**支持多个线程池**。
* 每个线程池有独立的名称，可配置不同的线程数。
* 业务可根据异步任务的操作，将它们分发至不同的线程池，避免将所有的异步任务放在一个池中相互影响。
