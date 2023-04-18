package cn.springbook.course.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 线程池配置服务类
 * </p>
 *
 * @author: caifenglin
 * @date: 2022/5/17 16:23
 */
@Configuration
public class ThreadPoolConfiguration {

    /**
     * 主线程池参数配置
     */
    @Value("${threadpool.main.corePoolSize:}")
    private Integer mainCorePoolSize;

    @Value("${threadpool.main.maxPoolSize:}")
    private Integer mainMaxPoolSize;

    @Value("${threadpool.main.queueCapacity}")
    private Integer mainQueueCapacity;

    @Value("${threadpool.main.keepAliveSeconds}")
    private Integer mainKeepAliveSeconds;

    @Value("${threadpool.main.threadNamePrefix}")
    private String mainThreadNamePrefix;

    @Value("${threadpool.main.waitForTasksToCompleteOnShutdown}")
    private Boolean mainWaitForTasksToCompleteOnShutdown;


    /**
     * 副线程池参数配置
     */
    @Value("${threadpool.sub.corePoolSize:}")
    private Integer subCorePoolSize;

    @Value("${threadpool.sub.maxPoolSize:}")
    private Integer subMaxPoolSize;

    @Value("${threadpool.sub.queueCapacity}")
    private Integer subQueueCapacity;

    @Value("${threadpool.sub.keepAliveSeconds}")
    private Integer subKeepAliveSeconds;

    @Value("${threadpool.sub.threadNamePrefix}")
    private String subThreadNamePrefix;

    @Value("${threadpool.sub.waitForTasksToCompleteOnShutdown}")
    private Boolean subWaitForTasksToCompleteOnShutdown;

    @Bean("mainThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor mainThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new CustomThreadPoolTaskExecutor();
        // 设置核心线程数等于系统核数--8核
        int cpuNum = Runtime.getRuntime().availableProcessors();
        // 核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(mainCorePoolSize == null ? cpuNum : mainCorePoolSize);
        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(mainMaxPoolSize == null ? cpuNum * 2 : mainMaxPoolSize);
        // 缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(mainQueueCapacity);
        // 允许线程的空闲时间60秒：当超过了核心线程之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(mainKeepAliveSeconds);
        // 线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix(mainThreadNamePrefix);
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(mainWaitForTasksToCompleteOnShutdown);
        // 执行初始化
        executor.initialize();
        return executor;
    }


    /**
     * IO密集型任务  = 一般为2*CPU核心数（常出现于线程中：数据库数据交互、文件上传下载、网络数据传输等等）
     * CPU密集型任务 = 一般为CPU核心数+1（常出现于线程中：复杂算法）
     * 混合型任务  = 视机器配置和复杂度自测而定
     */
    @Bean("subThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor subThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new CustomThreadPoolTaskExecutor();
        // 设置核心线程数等于系统核数--8核
        int cpuNum = Runtime.getRuntime().availableProcessors();
        // 核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(subCorePoolSize == null ? cpuNum : subCorePoolSize);
        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(subMaxPoolSize == null ? cpuNum * 2 : subMaxPoolSize);
        // 缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(subQueueCapacity);
        // 允许线程的空闲时间60秒：当超过了核心线程之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(subKeepAliveSeconds);
        // 线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix(subThreadNamePrefix);
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(subWaitForTasksToCompleteOnShutdown);
        // 执行初始化
        executor.initialize();
        return executor;
    }


}
