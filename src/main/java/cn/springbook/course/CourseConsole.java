package cn.springbook.course;

import cn.springbook.course.config.CustomThreadPoolTaskExecutor;
import cn.springbook.course.config.GlobalConfig;
import cn.springbook.course.config.TencentCourseConfig;
import cn.springbook.course.handle.TencentCourseHandle;
import cn.springbook.course.handle.ValidateHandle;
import cn.springbook.course.model.TencentCourse;
import cn.springbook.course.handle.SqliteHandle;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author: caifenglin
 * @date: 2023/04/13 21:24
 */
@SpringBootApplication
public class CourseConsole implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseConsole.class);

    @Resource
    private CustomThreadPoolTaskExecutor mainThreadPoolTaskExecutor;

    @Resource
    private GlobalConfig globalConfig;


    public static void main(String[] args) throws Exception {

        SpringApplication.run(CourseConsole.class, args);

    }

    /**
     * access command line arguments
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        //do something
        LOGGER.info("project is starting ······");

        TencentCourseConfig tencentCourseConfig = globalConfig.tencentCourseConfig();

        if(!ValidateHandle.validateTencentCourseConfig(tencentCourseConfig)) {
            LOGGER.error("参数验证失败,请检查后再试");
            System.exit(0);
        } else {
            LOGGER.error("参数验证成功,开始查询课程信息");
        }

        // 课程信息
        Map<String, String> vidMap = SqliteHandle.getVidMap(tencentCourseConfig.getSqliteDir());
        TencentCourse tencentCourse = TencentCourseHandle.getTencentCourse(tencentCourseConfig);
        TencentCourseHandle.convert(tencentCourseConfig, tencentCourse, vidMap, mainThreadPoolTaskExecutor);
        LOGGER.info("执行成功");

        // 关闭线程池
        mainThreadPoolTaskExecutor.shutdown();
    }
}
