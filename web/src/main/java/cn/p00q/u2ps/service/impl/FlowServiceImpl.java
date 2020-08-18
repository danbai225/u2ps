package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.bean.DateVal;
import cn.p00q.u2ps.bean.Flow;
import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.service.FlowService;
import cn.p00q.u2ps.utils.DateUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * @program: web
 * @description: 流量服务实现类
 * @author: DanBai
 * @create: 2020-08-15 15:39
 **/

@Service
public class FlowServiceImpl implements FlowService {
    private RedisTemplate redisTemplate;

    public FlowServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Flow countFlow() {
        Flow flow = (Flow) redisTemplate.opsForValue().get(Flow.FlowCount);
        return flow==null?new Flow(0L,0L):flow;
    }

    @Override
    public List<DateVal> get30Days() {
        List<DateVal> list = new ArrayList<>();
        Date date = new Date();
        int yue = 29;
        for (int i = yue; i >= 0; i--) {
            try {
                Date rdate = DateUtils.dateAdd(date, -i, false);
                String strDate = DateUtils.dateFormat(rdate, DateUtils.DATE_PATTERN);
                Flow flow = (Flow) redisTemplate.opsForValue().get(Flow.FlowToDayPrefix + strDate);
                if(flow!=null){
                    list.add(new DateVal(strDate, flow.toGB()));
                }else {
                    list.add(new DateVal(strDate, new Flow(0L, 0L)));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public List<DateVal> get30DaysUserFlow(String username) {
        List<DateVal> list = new ArrayList<>();
        Date date = new Date();
        int yue = 29;
        for (int i = yue; i >= 0; i--) {
            try {
                Date rdate = DateUtils.dateAdd(date, -i, false);
                String strDate = DateUtils.dateFormat(rdate, DateUtils.DATE_PATTERN);
                Integer flow = (Integer) redisTemplate.opsForValue().get(Flow.UserFlowToDayPrefix +username+strDate);
                if(flow!=null){
                    list.add(new DateVal(strDate, flow/1024));
                }else {
                    list.add(new DateVal(strDate, 0));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getNodeFlow(List<Node> nodes) {
        List list =new ArrayList();
        nodes.forEach(n->{
            HashMap<String, Object> objectObjectHashMap = new HashMap<>(2);
            Flow flow = (Flow) redisTemplate.opsForValue().get(Flow.NodeFlowPrefix + n.getId());
            if(flow==null) {
                flow = new Flow(0L, 0L);
            }
            objectObjectHashMap.put("flow", flow.toGB());
            objectObjectHashMap.put("node", n);
            list.add(objectObjectHashMap);
        });
        return list;
    }

    @Override
    public List<Map<String, Object>> getTunnelFlow(List<Tunnel> tunnels,Map<Integer,Node> nodeMap) {
        List list =new ArrayList();
        tunnels.forEach(t->{
            HashMap<String, Object> objectObjectHashMap = new HashMap<>(4);
            Flow flow = (Flow) redisTemplate.opsForValue().get(Flow.TunnelFlowPrefix + t.getId());
            if(flow==null){
                flow=new Flow(0L,0L);
            }
            objectObjectHashMap.put("flow", flow.toMB());
            objectObjectHashMap.put("tunnel", t);
            objectObjectHashMap.put("nodeIP",nodeMap.get(t.getNodeId()).getIp());
            objectObjectHashMap.put("nodeOnline",nodeMap.get(t.getNodeId()).getOnline());
            list.add(objectObjectHashMap);
        });
        return list;
    }
}
