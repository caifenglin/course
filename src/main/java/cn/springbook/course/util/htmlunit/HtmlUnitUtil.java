package cn.springbook.course.util.htmlunit;

import cn.springbook.course.util.StringUtil;
import org.htmlunit.*;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.util.Cookie;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author: caifenglin
 * @date: 2023/4/17 12:19
 */
public class HtmlUnitUtil {

    public static HtmlPage getPage(HtmlUnitBuilder builder) throws Exception {
        WebClient webClient = getWebClient(builder);
        int count = -1;
        while (true) {
            try {
                count++;
                return webClient.getPage(builder.url());
            } catch (Exception e) {
                if (e instanceof IOException && count < builder.retry()) {
                    // 日志打印：e,重试次数:i,再次执行
                } else {//如果url错误等情况
                    //执行失败，抛出异常
                    throw e;
                }
            }
        }
    }

    public static WebClient getWebClient(HtmlUnitBuilder builder) {
        WebClient webClient = null;
        if (StringUtil.isBlank(builder.proxyHost())) {
            webClient = new WebClient(BrowserVersion.CHROME);
        } else {
            webClient = new WebClient(BrowserVersion.CHROME, builder.proxyHost(), builder.proxyPort());
            // 需要验证的代理服务器
            if (StringUtil.isNotBlank(builder.username())) {
                ((DefaultCredentialsProvider) webClient.getCredentialsProvider()).
                        addCredentials(builder.username(), builder.password());
            }
        }
        // 浏览器基本设置
        // 当JS执行出错的时候是否抛出异常
        webClient.getOptions().setThrowExceptionOnScriptError(builder.enableThrowExceptionOnScriptError());
        // 当HTTP的状态非200时是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(builder.enableThrowExceptionOnFailingStatusCode());
        // 是否启用CSS
        webClient.getOptions().setCssEnabled(builder.enableCSS());
        // 默认设置为禁用
        webClient.getOptions().setJavaScriptEnabled(builder.enableJS());
        if (builder.enableAjax()) {
            // 设置支持AJAX
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        }

        if (builder.enableCookie()) {
            webClient.getCookieManager().setCookiesEnabled(true);
            for (Map.Entry<String, String> pair : builder.cookies().entrySet()) {
                webClient.getCookieManager().addCookie(new Cookie("/", pair.getKey(), pair.getKey()));
            }

        }
        webClient.waitForBackgroundJavaScript(builder.waitForBackgroundJavaScript());
        Map<String, String> headers = builder.headers();
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                webClient.addRequestHeader(header.getKey(), header.getValue());
            }
        }
        return webClient;
    }


    public static void main(String[] args) throws Exception {
        String url = "https://ke.qq.com/course/391387#term_id=100466839";

        // 1. 创建WebClient并设置配置项
        WebClient webClient = new WebClient(BrowserVersion.CHROME);


        //2.设置参数
        //启动js
        webClient.getOptions().setJavaScriptEnabled(true);
        //关闭css渲染
        webClient.getOptions().setCssEnabled(false);
        //启动重定向
        webClient.getOptions().setRedirectEnabled(true);
        //启动cookie管理

        // webClient.addCookie(HeaderConstant.cookie, new URL("https://ke.qq.com"),"ke.qq.com");
        // webClient.setCookieManager(new CookieManager());
        //启动ajax代理
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        //js运行时错误，是否抛出异常
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        // 3. 从网站的初始域名开始请求
        //开始请求网站
        //webClient.addRequestHeader("cookie", HeaderConstant.cookie);
        //webClient.addRequestHeader("referer", HeaderConstant.referer);
        WebRequest request=new WebRequest(new URL(url));
        request.setCharset(Charset.forName("UTF-8"));
        // request.setAdditionalHeader("cookie", HeaderConstant.cookie);
        // request.setAdditionalHeader("referer", HeaderConstant.referer);

        HtmlPage page = webClient.getPage(request);
        webClient.getOptions().setTimeout(10 * 1000);
        //等待js渲染执行 waitime等待时间(ms)
        webClient.waitForBackgroundJavaScript(10 * 1000);

        // 模拟点击跳转
        HtmlAnchor htmlAnchor = (HtmlAnchor)page.getFirstByXPath("//a[@data-hook='login']");
        htmlAnchor.click();
        // HtmlSelect select = (HtmlSelect) page.getFirstByXPath("//a[@data-hook='login']");
        // HtmlSelect select = (HtmlSelect) page.getFirstByXPath("//div[@class='catalog-tab-title']");
        // System.out.println(page.asNormalizedText());
        System.out.println("------------");
        System.out.println("网页html："+page.asXml());//获取html
//        HtmlUnitBuilder builder = HtmlUnitBuilder.config()
//                .url("http://www.baidu.com")
//                .enableJS(true)
//                .enableCookie(true);
//        String xml = getPage(builder).asXml();
//        System.out.println(xml);
    }
}