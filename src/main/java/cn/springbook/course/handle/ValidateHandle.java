package cn.springbook.course.handle;

import cn.springbook.course.CourseConsole;
import cn.springbook.course.config.TencentCourseConfig;
import cn.springbook.course.util.ValidateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *
 * </p>
 *
 * @author: caifenglin
 * @date: 2023/4/17 11:16
 */
public class ValidateHandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateHandle.class);

    public static boolean validateTencentCourseConfig(TencentCourseConfig tencentCourseConfig) {
        if(ValidateUtil.isEmpty(tencentCourseConfig.getCourseUrl())) {
            LOGGER.error("课程地址不能为空");
            return false;
        }
        if(ValidateUtil.isEmpty(tencentCourseConfig.getSqliteDir())) {
            LOGGER.error("sqlite数据库目录不能为空");
            return false;
        }
        if(ValidateUtil.isEmpty(tencentCourseConfig.getVideoDir())) {
            LOGGER.error("sqlite转换后的视频目录不能为空");
            return false;
        }
        if(ValidateUtil.isEmpty(tencentCourseConfig.getConvertDir())) {
            LOGGER.error("最终转换后的视频目录不能为空");
            return false;
        }
        return true;
    }
}