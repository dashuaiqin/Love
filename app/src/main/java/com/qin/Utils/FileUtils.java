package com.qin.Utils;

import java.io.File;

public class FileUtils {
    /**
     * 判断文件是否存在
     * @param path
     * @return
     */
    public static boolean isFileExists(String path){
        try{
            File f=new File(path);
            if(!f.exists()){
                return false;
            }
        }catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    /**
     * 删除文件
     * @param file
     */
        public static void delete(File file) {
            if (!file.exists()) {
            return;
            }
                if (file.isFile()) {
                    file.delete();
                    return;
                   }

                if(file.isDirectory()){
                       File[] childFiles = file.listFiles();
                        if (childFiles == null || childFiles.length == 0) {
                                file.delete();
                            return;
                          }

                        for (int i = 0; i < childFiles.length; i++) {
                               delete(childFiles[i]);
                        }
                    file.delete();
                    }
        }
}