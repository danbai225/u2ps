package cn.p00q.u2ps.bean;

import com.alibaba.fastjson.JSON;
import java.util.Date;

/**
 * @program: u2ps
 * @description: msg
 * @author: DanBai
 * @create: 2020-08-02 18:24
 **/
public class Msg{
    private Date time;
    private String type;
    private String msg;
    private Object data;
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Msg(String type, String msg, Object data) {
        this.time = new Date();
        this.type = type;
        this.msg = msg;
        this.data = data;
    }
    public Msg() {
    }
    public static Msg JsonStrToMsg(String jsonStr){
        return JSON.parseObject(jsonStr,Msg.class);
    }
    public static String toJsonStr(Msg msg){
        return JSON.toJSONString(msg);
    }
    public  String toJsonStr(){
        return JSON.toJSONString(this);
    }
    public static String getStringData(Msg msg){
       return JSON.parseObject((String) msg.data,String.class);
    }
    public static Boolean getBooleanData(Msg msg){
        return JSON.parseObject((String) msg.data,Boolean.class);
    }
    public static <T> T getData(Msg msg,Class<T> clazz){
        return JSON.parseObject((String) msg.data,clazz);
    }
    @Override
    public String toString() {
        return "Msg{" +
                "time=" + time +
                ", type='" + type + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * 客户端验证
     */
    public static final String AuthenticationClient="AuthenticationClient";
    /**
     * 服务端验证
     */
    public static final String AuthenticationServer="AuthenticationServer";
    /**
     * 登录验证失败
     */
    public static final String AuthenticationResultErr="AuthenticationResultErr";
    /**
     * 登录验证成功
     */
    public static final String TypeAuthenticationResultOk="TypeAuthenticationResultOk";
    /**
     * 所有隧道信息
     */
    public static final String AllTunnel="AllTunnel";
    /**
     * 询问端口是否被占用
     */
    public static final String IsPortUse="IsPortUse";
    /**
     * 更新隧道
     */
    public static final String UpdateTunnel="UpdateTunnel";
    /**
     * 删除隧道
     */
    public static final String DeleteTunnel="DeleteTunnel";
    /**
     * 更新node
     */
    public static final String UpdateNode="UpdateNode";
    /**
     * 删除node
     */
    public static final String DeleteNode="DeleteNode";
    public static final String UpdateFlow="UpdateFlow";

}
