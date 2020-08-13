package cn.p00q.u2ps.utils;


import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * @program: u2ps
 * @description: 人机验证
 * @author: DanBai
 * @create: 2020-08-12 21:10
 **/
public class Vaptcha {
    public static final String VerifyUrl = "http://0.vaptcha.com/verify";
    public static final String SecretKey = "bdd07f080c8c46bb8ab6f8a2a65c1ef4";
    public static final String Vid = "5f33dfe11273afe27016d595";
    public static final String Scene = "0";

    public static boolean V(String token, String ip) {
        String bodyString = "ip="+ip+"&token="+token+"&Scene=0&id=5f33dfe11273afe27016d595&secretkey=bdd07f080c8c46bb8ab6f8a2a65c1ef4";
        URL url = null;
        try {
            url = new URL(VerifyUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(bodyString.getBytes("utf-8"));
            os.flush();
            os.close();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return JSONObject.parseObject(sb.toString()).getBooleanValue("success");
            }
        } catch (Exception ignored) {
        }
        return false;
    }

}
