package cn.springbook.course.config;

/**
 * <p>
 *
 * </p>
 *
 * @author: caifenglin
 * @date: 2023/4/17 11:03
 */
public class TencentCourseConfig {

    private String courseUrl;

    private String sqliteDir;

    private String videoDir;

    private String videoExtension;

    private String convertDir;

    /**
     * 默认转换时拷贝文件
     */
    private Boolean convertCopy = true;

    public String getCourseUrl() {
        return courseUrl;
    }

    public void setCourseUrl(String courseUrl) {
        this.courseUrl = courseUrl;
    }

    public String getSqliteDir() {
        return sqliteDir;
    }

    public void setSqliteDir(String sqliteDir) {
        this.sqliteDir = sqliteDir;
    }

    public String getVideoDir() {
        return videoDir;
    }

    public void setVideoDir(String videoDir) {
        this.videoDir = videoDir;
    }

    public String getVideoExtension() {
        return videoExtension;
    }

    public void setVideoExtension(String videoExtension) {
        this.videoExtension = videoExtension;
    }

    public String getConvertDir() {
        return convertDir;
    }

    public void setConvertDir(String convertDir) {
        this.convertDir = convertDir;
    }

    public Boolean getConvertCopy() {
        return convertCopy;
    }

    public void setConvertCopy(Boolean convertCopy) {
        this.convertCopy = convertCopy;
    }
}