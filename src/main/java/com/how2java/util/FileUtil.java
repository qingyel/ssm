package com.how2java.util;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: ssm
 * @description:
 * @author: syx
 * @create: 2019-07-30 16:02
 **/
public class FileUtil {
    /**
     * @param request
     * @param sliceFile 分片文件
     * @param name      文件名称
     * @param dirType   文件夹类型 如video/audio
     * @param fileExt   文件扩展名 如.mp4/.avi  ./mp3
     * @return
     * @Description: 分片文件追加
     */
    public static String randomWrite(HttpServletRequest request, byte[] sliceFile, String name, String dirType, String fileExt) {
        try {
            /** 以读写的方式建立一个RandomAccessFile对象 **/
            //获取相对路径/home/gzxiaoi/apache-tomcat-8.0.45/webapps
            String realPath = getRealPath(request);
            //拼接文件保存路径 /fileDate/video/2017/08/09  如果没有该文件夹，则创建
            String savePath = getSavePath(realPath, dirType);
            String realName = name;
            String saveFile = realPath + savePath + realName + fileExt;
            RandomAccessFile raf = new RandomAccessFile(saveFile, "rw");
            // 将记录指针移动到文件最后
            raf.seek(raf.length());
            raf.write(sliceFile);
            raf.close();
            return savePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param request
     * @return
     * @Description: 取得tomcat中的webapps目录 如： /home/software/apache-tomcat-8.0.45/webapps
     */
    public static String getRealPath(HttpServletRequest request) {
        String realPath = request.getSession().getServletContext().getRealPath(File.separator);
        realPath = realPath.substring(0, realPath.length() - 1);
        int aString = realPath.lastIndexOf(File.separator);
        realPath = realPath.substring(0, aString);
        return realPath;
    }

    /**
     * @param realPath 相对路径 ，如   /home/software/apache-tomcat-8.0.45/webapps
     * @param fileType 文件类型 如： images/video/audio用于拼接文件保存路径，区分音视频
     * @return
     * @Description: 获取文件保存的路径，如果没有该目录，则创建
     */
    public static String getSavePath(String realPath, String fileType) {
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat m = new SimpleDateFormat("MM");
        SimpleDateFormat d = new SimpleDateFormat("dd");
        Date date = new Date();
        String sp = File.separator + "fileDate" + File.separator + fileType + File.separator + year.format(date) + File.separator
                + m.format(date) + File.separator + d.format(date) + File.separator;
        String savePath = realPath + sp;
        File folder = new File(savePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return sp;
    }
}
