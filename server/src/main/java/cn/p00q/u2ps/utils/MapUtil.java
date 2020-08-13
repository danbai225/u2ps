package cn.p00q.u2ps.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: u2ps
 * @description: map
 * @author: DanBai
 * @create: 2020-08-06 22:26
 **/
public class MapUtil {
    public static Object getKey(Map map, Object value){
        List<Object> keyList = new ArrayList<>();
        for(Object key: map.keySet()){
            if(map.get(key).equals(value)){
                keyList.add(key);
            }
        }
        return keyList;
    }
}
