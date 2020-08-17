package cn.p00q.u2ps.utils;

import cn.p00q.u2ps.entity.Pay;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

/**
 * @program: web
 * @description: 支付工具类
 * @author: DanBai
 * @create: 2020-08-17 14:36
 **/
public class zfUtils {
    private static int pid=672;
    private static String token="971FCD18696CAA771936FE78807A3C61";
    private static String NoticeKey ="BB986363E96E0480E6A20FE20B69136F";
    public static String  getZFUrl(Integer orderId,float fee,long time,int payType,String notice_url,String cancel_url,String return_url,String remark){
        DecimalFormat decimalFormat=new DecimalFormat("##0.00");
        String feeStr=decimalFormat.format(fee);
        //long time = System.currentTimeMillis();
        String URL="https://fackpay.com/Pay/"+pid+"/"+getHash2(token+feeStr+time+orderId, "MD5")
                +"?fee="+fee+"&timestamp="+time+"&order_id="+orderId+"&"+"remark="+remark+"&notice_url="+notice_url+"&cancel_url="+cancel_url+
                "&return_url="+return_url+"&paytype="+payType;
        return URL;
    }
    public static boolean checkSalt(String Salt,String fee,Integer orderId,Integer sign){
        String md5 = getHash2(fee + NoticeKey + orderId + sign, "MD5");
        return md5.toLowerCase().equals(Salt.toLowerCase());
    }
    public static String getHash2(String source, String hashType) {
        StringBuilder sb = new StringBuilder();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(hashType);
            md5.update(source.getBytes());
            for (byte b : md5.digest()) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getUrl(Pay pay){
        return getZFUrl(pay.getId(), pay.getFee(), pay.getCreationtime().getTime(), pay.getPaytype(), "http://127.0.0.1:2252/pay/callBack", "http://127.0.0.1:2252/pay/cancel","http://127.0.0.1:2252/pay/callBack","U2PS");
    }
}
