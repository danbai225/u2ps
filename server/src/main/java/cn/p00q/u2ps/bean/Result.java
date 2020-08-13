package cn.p00q.u2ps.bean;


/**
 * @program: u2ps
 * @description: 结果返回类
 * @author: DanBai
 * @create: 2020-08-02 14:12
 **/
public class Result{
    private String msg;
    private int code;
    private Object data;


    private static int OK_CODE=0;
    private static int ERR_CODE=1;
    private static String OK_MSG="成功";
    private static String ERR_MSG="错误";
    /**
     * 参数有误
     */
    public static final int ERR_PARAMETER=100;
    /**
     * 存在重复
     */
    public static final int ERR_DUPLICATION=101;
    public Result(String msg, int code, Object data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }
    public boolean isOk(){
        return this.code<100;
    }
    public String getMsg() {
        return msg;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    public static int getOkCode() {
        return OK_CODE;
    }

    public static void setOkCode(int okCode) {
        OK_CODE = okCode;
    }

    public static int getErrCode() {
        return ERR_CODE;
    }

    public static void setErrCode(int errCode) {
        ERR_CODE = errCode;
    }

    public static String getOkMsg() {
        return OK_MSG;
    }

    public static void setOkMsg(String okMsg) {
        OK_MSG = okMsg;
    }

    public static String getErrMsg() {
        return ERR_MSG;
    }

    public static void setErrMsg(String errMsg) {
        ERR_MSG = errMsg;
    }

    public Result() {
    }
    public static Result success(){
        return new Result(OK_MSG,OK_CODE,null);
    }
    public static Result success(String msg){
        return new Result(msg,OK_CODE,null);
    }
    public static Result success(String msg,Object data){
        return new Result(msg,OK_CODE,data);
    }
    public static Result err(){
        return new Result(ERR_MSG,ERR_CODE,null);
    }
    public static Result err(String msg){
        return new Result(msg,ERR_CODE,null);
    }
    public static Result err(String msg,Object data){
        return new Result(msg,ERR_CODE,data);
    }
}
