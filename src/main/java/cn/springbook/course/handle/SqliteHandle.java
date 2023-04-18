package cn.springbook.course.handle;

import cn.springbook.course.util.FileUtil;
import com.google.common.base.Splitter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author: caifenglin
 * @date: 2023/4/16 下午8:01
 */
public class SqliteHandle {

    /**
     * 从Sqlite中获取信息
     */
    public static String getVid(String sqlitePath) {
        String vid = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
            conn.setAutoCommit(false);

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM caches;" );

            int index = 0;
            while ( rs.next() ) {
                if(index == 1) {
                    String key = rs.getString("key");
                    vid = getParam(key, "fileId");
                    break;
                }
                index ++ ;
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return vid;
    }

    public static String getParam(String url, String name) {
        String params = url.substring(url.indexOf("?") + 1, url.length());
        Map<String, String> split = Splitter.on("&").withKeyValueSeparator("=").split(params);
        return split.get(name);
    }


    public static Map<String, String> getVidMap(String sqliteFolder) {
        // key 是vid
        // value 是sqliteName
        Map<String, String> sqliteMap = new ConcurrentHashMap<>();
        if(!FileUtil.isSeparatorEnd(sqliteFolder)) {
            sqliteFolder = sqliteFolder + File.separator;
        }
        File sqliteFile = new File(sqliteFolder);
        if(sqliteFile.exists() && sqliteFile.isDirectory()) {
            File[] files = sqliteFile.listFiles();
            for (File file : files) {
                String name = file.getName();
                if(name.endsWith(".m3u8.sqlite")) {
                    String absolutePath = file.getAbsolutePath();
                    String vid = SqliteHandle.getVid(absolutePath);
                    if(vid != null && vid.length() > 0) {
                        String sqliteName = name.substring(0, name.indexOf(".m3u8.sqlite"));
                        sqliteMap.put(vid, sqliteName);
                    }
                }
            }
        }
        return sqliteMap;
    }
}
