package cn.p00q.u2ps.utils;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: web
 * @description: 验证码工具
 * @author: DanBai
 * @create: 2020-08-17 17:15
 **/
public class yzmUtils {
    public static boolean sendYzm(String mobile,String yzm) {
        String host = "https://smssend.shumaidata.com";
        String path = "/sms/send";
        String method = "POST";
        String appcode = "a95cb2e60944417d851d083c63e8a41c";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("receive", mobile);
        querys.put("tag", yzm);
        querys.put("templateId", "MCF8CD2AD2");
        Map<String, String> bodys = new HashMap<String, String>();
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            if(EntityUtils.toString(response.getEntity()).indexOf("成功")>0){
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
