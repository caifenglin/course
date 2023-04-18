package cn.springbook.course.handle;

import cn.springbook.course.CourseConsole;
import cn.springbook.course.config.CustomThreadPoolTaskExecutor;
import cn.springbook.course.config.TencentCourseConfig;
import cn.springbook.course.model.TencentCourse;
import cn.springbook.course.util.FileUtil;
import cn.springbook.course.util.StringUtil;
import cn.springbook.course.util.htmlunit.HtmlUnitBuilder;
import cn.springbook.course.util.htmlunit.HtmlUnitUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.htmlunit.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author: caifenglin
 * @date: 2023/4/17 11:36
 */
public class TencentCourseHandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(TencentCourseHandle.class);

    public static TencentCourse getTencentCourse(TencentCourseConfig tencentCourseConfig) {
        HtmlUnitBuilder builder = HtmlUnitBuilder.config()
                .url(tencentCourseConfig.getCourseUrl())
                .enableJS(false)
                .enableCSS(false)
                .enableAjax(false);

        HtmlPage htmlPage = null;
        TencentCourse tencentCourse = new TencentCourse();
        try {
            htmlPage = HtmlUnitUtil.getPage(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String textContent = htmlPage.getHtmlElementById("__NEXT_DATA__").getTextContent();

        JSONObject courseInfo = JSON.parseObject(textContent).getJSONObject("props").getJSONObject("pageProps").getJSONObject("courseInfo");
        JSONObject data = courseInfo.getJSONObject("data");
        // 课程名称
        String courseName = data.getJSONObject("basic_info").getString("name");
        Integer cid = data.getJSONObject("basic_info").getInteger("cid");
        Integer aid = data.getJSONObject("basic_info").getInteger("aid");
        String summary = data.getJSONObject("basic_info").getString("summary");

        tencentCourse.setCourseName(courseName);
        tencentCourse.setCid(cid);
        tencentCourse.setAid(aid);
        tencentCourse.setSummary(summary);

        JSONObject catalogMap = courseInfo.getJSONObject("catalogMap");
        for (String catalog : catalogMap.keySet()) {
            JSONArray jsonArray = catalogMap.getJSONArray(catalog);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray subInfo = jsonObject.getJSONArray("sub_info");
                List<TencentCourse.SubCourse> subCourseList = new ArrayList<>();
                for (int j = 0; j < subInfo.size(); j++) {

                    TencentCourse.SubCourse subCourse = new TencentCourse.SubCourse();
                    List<TencentCourse.CourseTask> courseTaskList = new ArrayList<>();

                    JSONObject subInfoContent = subInfo.getJSONObject(j);

                    Integer subId = subInfoContent.getInteger("sub_id") + 1;
                    String subName = subInfoContent.getString("name");

                    subCourse.setSubName(subName);
                    subCourse.setSubId(subId);

                    JSONArray taskInfo = subInfoContent.getJSONArray("task_info");
                    for (int k = 0; k < taskInfo.size(); k++) {
                        TencentCourse.CourseTask courseTask = new TencentCourse.CourseTask();

                        JSONObject taskInfoContent = taskInfo.getJSONObject(k);
                        Integer aidTask = taskInfoContent.getInteger("aid");
                        Integer cidTask = taskInfoContent.getInteger("cid");
                        String taidTask = taskInfoContent.getString("taid");
                        String taskName = taskInfoContent.getString("name");
                        String vid = taskInfoContent.getString("resid_list");

                        courseTask.setTaskName(taskName);
                        courseTask.setVid(vid);
                        courseTask.setCid(cidTask);
                        courseTask.setAid(aidTask);
                        courseTask.setTaid(taidTask);
                        courseTask.setIndex(k + 1);
                        courseTaskList.add(courseTask);
                    }
                    subCourse.setCourseTasks(courseTaskList);
                    subCourseList.add(subCourse);
                }
                tencentCourse.setSubCourses(subCourseList);
            }
            break;
        }
        return tencentCourse;
    }


    public static void convert(TencentCourseConfig tencentCourseConfig, TencentCourse tencentCourse,
                          Map<String, String> vidMap, CustomThreadPoolTaskExecutor mainThreadPoolTaskExecutor) {

        String convertDir = tencentCourseConfig.getConvertDir();
        String videoDir = tencentCourseConfig.getVideoDir();
        String videoExtension = tencentCourseConfig.getVideoExtension();
        Boolean isConvertCopy = tencentCourseConfig.getConvertCopy();
        if(!FileUtil.isSeparatorEnd(convertDir)) {
            convertDir = convertDir + File.separator;
        }
        if(!FileUtil.isSeparatorEnd(videoDir)) {
            videoDir = videoDir + File.separator;
        }
        if(StringUtil.isBlank(videoExtension)) {
            videoExtension = ".ts";
        } else if(!videoExtension.startsWith(".")) {
            videoExtension = "." + videoExtension;
        }
        String courseRootPath = convertDir + tencentCourse.getCourseName();
        FileUtil.deleteFile(new File(courseRootPath));
        Map<String, String> taskMap = new ConcurrentHashMap<>();
        for (TencentCourse.SubCourse sub : tencentCourse.getSubCourses()) {
            String index = String.format("%02d", sub.getSubId()) + ". ";
            String subRootPath = courseRootPath + File.separator + index
                    + sub.getSubName();

            for (TencentCourse.CourseTask courseTask : sub.getCourseTasks()) {
                String taskIndex = courseTask.getIndex() + ". ";
                String taskRootPath = subRootPath + File.separator + taskIndex + courseTask.getTaskName();
                taskMap.put(courseTask.getVid(), taskRootPath);
                FileUtil.mkdirFolder(subRootPath);
            }
        }

        // 多线程执行转换操作
        executeAsyncConvert(videoDir, videoExtension, isConvertCopy, vidMap, taskMap, mainThreadPoolTaskExecutor);
    }


    public static void executeAsyncConvert(String videoDir, String videoExtension, Boolean isConvertCopy,
            Map<String, String> vidMap, Map<String, String> taskMap,
            CustomThreadPoolTaskExecutor mainThreadPoolTaskExecutor) {
        List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();
        for (String vid : taskMap.keySet()) {
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                String originName = vidMap.get(vid);
                if(StringUtil.isNotBlank(originName)) {
                    String currentName = taskMap.get(vid);
                    String videoPath = videoDir + File.separator + originName + videoExtension;
                    File sourceFile = new File(videoPath);
                    if(sourceFile.isFile() && sourceFile.exists()) {
                        String destPath = currentName + videoExtension;
                        File destFile = new File(destPath);
                        if(isConvertCopy) {
                            try {
                                FileUtil.copyFileUsingFileChannels(sourceFile, destFile);
                            } catch (IOException e) {
                                // e.printStackTrace();
                                LOGGER.error("【视频复制时---> 视频名称:{}, 转换新的名称是:{} 出现异常, 异常信息:】",
                                        videoPath, destPath, e.getMessage());
                            }
                        } else {
                            boolean flag = sourceFile.renameTo(destFile);
                            if(!flag) {
                                LOGGER.warn("【视频移动时---> 视频名称:{}, 转换新的名称是:{} 移动失败】", videoPath, destPath);
                            }
                        }
                    }
                }
            }, mainThreadPoolTaskExecutor);
            completableFutureList.add(completableFuture);
        }
        // 等待主任务全部完成
        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0])).join();
    }

}