package bwei.admin.wurui1505b20170630;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 类描述：网络请求类
 * 创建人：WuRui
 * 创建时间：2017/6/30 14:58
 */
public class Utils {
    public String getData(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len = 0;
                byte b[] = new byte[1024];
                while ((len = inputStream.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
                return bos.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
