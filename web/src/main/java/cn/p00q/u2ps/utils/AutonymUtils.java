package cn.p00q.u2ps.utils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @program: web
 * @description: 实名认证工具
 * @author: DanBai
 * @create: 2020-08-17 19:31
 **/
public class AutonymUtils {
    private static String APPCODE="a95cb2e60944417d851d083c63e8a41c";
    private static String  url = "https://mobile3elements.shumaidata.com/mobile/verify_real_name";
    public static boolean Autonym(String name,String idcard,String mobile){
        Map<String,String> params = new HashMap<>(4);
        params.put("idcard",idcard);
        params.put("mobile",mobile);
        params.put("name",name);
        String result = null;
        try {
            result = postForm(APPCODE,url,params);
            return result.indexOf("一致")>0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String postForm(String appCode, String url, Map<String,String> params) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().build();
        FormBody.Builder formbuilder = new FormBody.Builder();
        Iterator<String> it = params.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            formbuilder.add(key,params.get(key));
        }
        FormBody body = formbuilder.build();
        Request request = new Request.Builder().url(url).addHeader("Authorization","APPCODE "+appCode).post(body).build();
        Response response = client.newCall(request).execute();
        String result =  response.body().string();
        return result;
    }
}
