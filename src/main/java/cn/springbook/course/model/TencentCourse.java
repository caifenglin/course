package cn.springbook.course.model;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author: caifenglin
 * @date: 2023/4/13 9:08
 */
@Data
public class TencentCourse {

    private String courseName;

    private Integer cid;

    private Integer aid;

    private String summary;

    private List<SubCourse> subCourses;


    @Data
    public static class SubCourse {

        private String subName;

        private Integer subId;

        private List<CourseTask> courseTasks;
    }

    @Data
    public static class CourseTask {

        private Integer index;

        private String taskName;

        private String vid;

        private Integer aid;

        private String taid;

        private Integer cid;

    }
}