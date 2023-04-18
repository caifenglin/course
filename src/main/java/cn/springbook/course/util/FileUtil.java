package cn.springbook.course.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>
 *
 * </p>
 *
 * @author: caifenglin
 * @date: 2023/4/16 下午3:34
 */
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 将Byte数组转换成文件
     * @param bytes byte数组
     * @param fileFullName 文件全路径 如果/data/app/1.txt
     */
    public static boolean writeBytesToFile(byte[] bytes, String fileFullName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            File file = new File(fileFullName);
            if (!file.getParentFile().exists()){
                //文件夹不存在 生成
                file.getParentFile().mkdirs();
            } else {
                file.delete();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            log.error("将bytes数组写入文件【{}】失败：{}\n", fileFullName, e);
            return false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    // e.printStackTrace();
                    log.error("将bytes数组写入文件【{}】失败后关闭bos异常：{}\n", fileFullName, e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // e.printStackTrace();
                    log.error("将bytes数组写入文件【{}】失败后关闭fos异常：{}\n", fileFullName, e);
                }
            }
        }
    }


    /**
     * 删除文件及目录
     * @param file;
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
        }
        return file.delete();
    }

    public static String readFile(String filePath) {
        String data = null;
        try {
            Path path = Paths.get(filePath);
            byte[] bytes = Files.readAllBytes(path);
            data = new String(bytes);
        } catch (IOException e) {
            // e.printStackTrace();
            log.error("将bytes数组写入文件【{}】失败后关闭fos异常：{}\n", filePath, e);
        }
        return data;
    }

    public static void mkdirFolder(String path) {
        File file = new File(path);
        if(file.isFile() && !file.exists()) {
            file.getParentFile().mkdirs();
        } else {
            file.mkdirs();
        }
    }

    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }  finally {
            inputChannel.close();
            outputChannel.close();
        }
    }


    public static boolean isSeparatorEnd(String path) {
        if(StringUtil.isNotBlank(path) && path.startsWith(File.separator)) {
            return true;
        }
        return false;
    }
}
