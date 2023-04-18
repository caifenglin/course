package cn.springbook.course.util;

import cn.springbook.course.model.TencentCourse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author: caifenglin
 * @date: 2023/4/13 8:59
 */
public class TencentUtil {

    /**
     * 获取课程信息
     */
    public static void getCourse() {
        TencentCourse course = new TencentCourse();
        String url = "https://ke.qq.com/course/391387#term_id=100466839";
        Document doc = null;
        try {
            doc = Jsoup.connect("https://ke.qq.com/course/391387")
                    //.header("cookie", HeaderConstant.cookie)
                    //.header("referer", HeaderConstant.referer)
                    .timeout(3000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element element = doc.selectFirst("h3.course-title");
        // 课程标题
        String title = element.text();
        // course.setTitle(title);

        // 去除隐藏属性
        Elements remove = doc.select("*[style*=display:none]").remove();
        Element element1 = doc.getElementById("detail-tab");
        System.out.println(element1.html());

    }

    public static void main(String[] args) {
        getCourse();
    }

//    public static void main(String[] args) throws IOException {
//        // https://ke.qq.com/course/package/15300
//        // https://ke.qq.com/course/320036#term_id=100379872
//        Document doc = Jsoup.connect("https://ke.qq.com/course/391387")
//                .header("cookie", HeaderConstant.cookie)
//                .header("referer", HeaderConstant.referer)
//                .timeout(0).get();
//
//        Elements elements = doc.body().select("div#js-dir-tab .pkg-catalog-item");
//        for (Element element : elements) {
//            System.out.println(element.text());
//            System.out.println("data-index :" + element.attr("data-index"));
//            System.out.println("data-cid :" + element.attr("data-cid"));
//            System.out.println("data-tid :" + element.attr("data-tid"));
//            Elements elementsByClass = element.select(".task-part-list .task-part-item");
////            for (Element byClass : elementsByClass) {
////                byClass
////            }
//            System.out.println(elementsByClass);
//        }
//    }
}