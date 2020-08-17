package cn.p00q.u2ps.bean;

/**
 * @program: web
 * @description: 日期对应值
 * @author: DanBai
 * @create: 2020-08-15 16:12
 **/
public class DateVal {
    private  String date;
    private Object val;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public DateVal(String date, Object val) {
        this.date = date;
        this.val = val;
    }

    public DateVal() {
    }
}
