package com.shixia.reader.utils;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.shixia.reader.db.BookList;

import org.litepal.crud.DataSupport;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11 0011.
 */
public class FictionFileUtils {

    private FictionFileUtils() {

    }

    public static class FileUtilsInstance {
        public static final FictionFileUtils INSTANCE = new FictionFileUtils();
    }

    public static FictionFileUtils getInstance() {
        return FileUtilsInstance.INSTANCE;
    }


    /**
     * 获取文件编码
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public String getCharset(String fileName) throws IOException {
        String charset;
        FileInputStream fis;
        try {
            fis = new FileInputStream(fileName);
            byte[] buf = new byte[4096];
            // (1)
            UniversalDetector detector = new UniversalDetector(null);
            // (2)
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();
            // (4)
            charset = detector.getDetectedCharset();
            // (5)
            detector.reset();
            return charset;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("lzr", "错误==" + e.getMessage());
        }
        return null;

    }

    /**
     * 根据路径获取文件名
     *
     * @param pathandname
     * @return
     */
    public String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return "";
        }

    }

    public List<File> getSuffixFile(String filePath, String suffere) {
        List<File> files = new ArrayList<>();
        File f = new File(filePath);
        return getSuffixFile(files, f, suffere);
    }

    /**
     * 读取sd卡上指定后缀的所有文件
     *
     * @param files   返回的所有文件
     * @param f       路径(可传入sd卡路径)
     * @param suffere 后缀名称 比如 .gif
     * @return
     */
    public List<File> getSuffixFile(List<File> files, File f, final String suffere) {
        if (!f.exists()) {
            return null;
        }

        File[] subFiles = f.listFiles();
        for (File subFile : subFiles) {
            if (subFile.isHidden()) {
                continue;
            }
            if (subFile.isDirectory()) {
                getSuffixFile(files, subFile, suffere);
            } else if (subFile.getName().endsWith(suffere)) {
                files.add(subFile);
            } else {
                //非指定目录文件 不做处理
            }
//            Log.e("filename",subFile.getName());
        }
        return files;
    }


    /**
     * 将下载好的小说保存到本地数据库
     *
     * @param context
     * @param book
     */
    public void saveBook(Context context, BookList book, OnSaveBookListener listener) {
        SaveBookToSqlLiteTask task = new SaveBookToSqlLiteTask(context, listener);
        List<BookList> bookLists = new ArrayList<>();
        bookLists.add(book);
        task.execute(bookLists);
    }

    public interface OnSaveBookListener {
        void onFinish();
    }


    private class SaveBookToSqlLiteTask extends AsyncTask<List<BookList>, Void, Integer> {
        private static final int FAIL = 0;
        private static final int SUCCESS = 1;
        private static final int REPEAT = 2;
        private BookList repeatBookList;
        private Context context;
        private OnSaveBookListener listener;

        public SaveBookToSqlLiteTask(Context context, OnSaveBookListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(List<BookList>... params) {
            List<BookList> bookLists = params[0];
            for (BookList bookList : bookLists) {
                List<BookList> books = DataSupport.where("bookpath = ?", bookList.getBookpath()).find(BookList.class);
                if (books.size() > 0) {
                    repeatBookList = bookList;
                    return REPEAT;
                }
            }
            try {
                DataSupport.saveAll(bookLists);
            } catch (Exception e) {
                e.printStackTrace();
                return FAIL;
            }
            return SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (listener != null) {
                Log.e("lzr", "书本保存成功");
                listener.onFinish();
            }
            String msg = "";
            switch (result) {
                case FAIL:
                    msg = "由于一些原因添加书本失败";
                    break;
                case SUCCESS:
                    msg = "添加书本成功";
                    break;
                case REPEAT:
                    msg = "书本" + repeatBookList.getBookname() + "重复了";
                    break;
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
