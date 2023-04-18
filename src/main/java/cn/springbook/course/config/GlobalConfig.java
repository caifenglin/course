package cn.springbook.course.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * <p>
 *
 * </p>
 *
 * @author: caifenglin
 * @date: 2023/4/17 11:02
 */
@Configuration
public class GlobalConfig {

    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix="common.tencent.course")
    public TencentCourseConfig tencentCourseConfig() {
        return new TencentCourseConfig();
    }

}