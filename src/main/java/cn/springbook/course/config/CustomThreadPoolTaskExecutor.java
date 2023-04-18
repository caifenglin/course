package cn.springbook.course.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 自定义线程池 以便于打印任务消息
 * </p>
 *
 * @author: caifenglin
 * @date: 2022/5/17 16:23
 */
public class CustomThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CustomThreadPoolTaskExecutor.class);

    public void showThreadPoolInfo() {

        ThreadPoolExecutor executor = getThreadPoolExecutor();

        if (executor == null) {
            return;
        }

        /*
        String info = "线程池" + this.getThreadNamePrefix() +
            "中，总任务数为 " + executor.getTaskCount() +
            " ，已处理完的任务数为 " + executor.getCompletedTaskCount() +
            " ，目前正在处理的任务数为 " + executor.getActiveCount() +
            " ，缓冲队列中任务数为 " + executor.getQueue().size();

        logger.info(info);
        */
    }

    @Override
    public void execute(Runnable task) {
        showThreadPoolInfo();
        super.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        showThreadPoolInfo();
        super.execute(task, startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        showThreadPoolInfo();
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        showThreadPoolInfo();
        return super.submit(task);
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        showThreadPoolInfo();
        return super.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        showThreadPoolInfo();
        return super.submitListenable(task);
    }
}
