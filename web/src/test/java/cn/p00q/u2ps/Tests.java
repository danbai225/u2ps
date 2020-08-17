package cn.p00q.u2ps;

import cn.p00q.u2ps.utils.AutonymUtils;
import cn.p00q.u2ps.utils.zfUtils;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

/**
 * @program: web
 * @description:
 * @author: DanBai
 * @create: 2020-08-17 10:55
 **/
public class Tests {
    @Test
    public void testR(){
        System.out.println(new Random().nextFloat()*1024);
    }
    @Test
    public void testDate(){
        long time = System.currentTimeMillis();
        System.out.println(time);
        System.out.println(new Date().getTime());
    }
    @Test
    public void vzf(){
        DecimalFormat decimalFormat=new DecimalFormat("##0.00");
        String feeStr=decimalFormat.format(0.5);
        System.out.println(feeStr + "BB986363E96E0480E6A20FE20B69136F" + "1" + "1");
        String md5 = zfUtils.getHash2("0.5" + "BB986363E96E0480E6A20FE20B69136F" + "1" + "1", "MD5");
        System.out.println(md5.toLowerCase());
        System.out.println("B8F45FD979DA75F47F9DF9B48D0343CC".toLowerCase());
    }
    @Test
    public void smrz(){
        System.out.println(AutonymUtils.Autonym("黄建军", "510623200209066013", "15555221438"));
    }
}
