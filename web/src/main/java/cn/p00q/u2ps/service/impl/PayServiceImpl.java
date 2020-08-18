package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.bean.Flow;
import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Booth;
import cn.p00q.u2ps.entity.Pay;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.mapper.BoothMapper;
import cn.p00q.u2ps.mapper.PayMapper;
import cn.p00q.u2ps.mapper.UserMapper;
import cn.p00q.u2ps.service.PayService;
import cn.p00q.u2ps.service.UserService;
import cn.p00q.u2ps.utils.DateUtils;
import cn.p00q.u2ps.utils.zfUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: web
 * @description: 支付服务实现
 * @author: DanBai
 * @create: 2020-08-17 15:33
 **/
@Service
public class PayServiceImpl implements PayService {
    @Value("${u2ps.domain:http://127.0.0.1:2252}")
    private String doMainUrl;
    private RedisTemplate redisTemplate;
    private PayMapper payMapper;
    private BoothMapper boothMapper;
    private UserMapper userMapper;
    private UserService userService;
    public static Float AuthenticationFee = 0.50f;
    public static final String BUY_FLOW_PREFIX = "BUY_FLOW_PREFIX_";
    public static final String REMAINING_SUM = "REMAINING_SUM_";
    public static final String RedisAuthenticationFee = "AuthenticationFee";

    public PayServiceImpl(RedisTemplate redisTemplate, PayMapper payMapper, BoothMapper boothMapper, UserMapper userMapper, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.payMapper = payMapper;
        this.boothMapper = boothMapper;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @Override
    public String buyAuthentication(String username, Integer payType) {
        Float fee = (Float) redisTemplate.opsForValue().get(RedisAuthenticationFee);
        if (fee == null) {
            fee = AuthenticationFee;
        }
        Pay pay = Pay.PayInit();
        pay.setId(countPay() + 1);
        pay.setPaytype(payType);
        pay.setFee(fee);
        pay.setUsername(username);
        pay.setType(Pay.Type_Authentication);
        int insert = payMapper.insert(pay);
        if (insert > 0) {
            return zfUtils.getUrl(pay);
        }
        return "/autonym";
    }

    @Override
    public Integer countPay() {
        //统计总数
        Example example = new Example(Pay.class);
        int count = payMapper.selectCountByExample(example);
        return count;
    }

    @Override
    public String callBack(@NotBlank String Salt, Integer order_id, String fee, Integer sign) {
        String rUrl = "/";
        if (sign >= 1) {
            Pay payById = getById(order_id);
            if (payById != null && zfUtils.checkSalt(Salt, fee, order_id, sign)) {
                switch (payById.getType()) {
                    case Pay.Type_Authentication:
                        redisTemplate.opsForSet().add(User.RedisAuthenticationSet, payById.getUsername());
                        rUrl = doMainUrl + "/autonym";
                        break;
                    case Pay.Type_Flow:
                        rUrl = doMainUrl + "/panel/traffic_market";
                        Booth booth = (Booth) redisTemplate.opsForValue().get(BUY_FLOW_PREFIX + order_id);
                        if (booth != null) {
                            userMapper.addFlowByUsername(payById.getUsername(), Flow.GBtoByte(booth.getQuantity()));
                            redisTemplate.delete(BUY_FLOW_PREFIX + order_id);
                            if (!booth.getOfficial()) {
                                Double rNum = (Double) redisTemplate.opsForValue().get(REMAINING_SUM + booth.getUsername());
                                if (rNum == null) {
                                    rNum = Double.valueOf(fee);
                                } else {
                                    rNum += Double.valueOf(fee);
                                }
                                redisTemplate.opsForValue().set(REMAINING_SUM + booth.getUsername(), rNum);
                            }
                        }
                        break;
                    default:
                }
                payById.setSign(sign);
                payMapper.updateByPrimaryKey(payById);
            }
        }
        return rUrl;
    }

    @Override
    public Result buyFlow(String username, Integer payType, Integer boothId, Integer buyNum) {
        Booth booth = boothMapper.selectById(boothId);
        //官方摊位
        Pay pay = Pay.PayInit();
        pay.setId(countPay() + 1);
        BigDecimal a1 = new BigDecimal(booth.getPrice().toString());
        BigDecimal b1 = new BigDecimal(buyNum.toString());
        pay.setPaytype(payType);
        pay.setFee(a1.multiply(b1).floatValue());
        pay.setUsername(username);
        pay.setType(Pay.Type_Flow);
        if (booth.getOfficial()) {
            int insert = payMapper.insert(pay);
            if (insert > 0) {
                booth.setQuantity(buyNum);
                redisTemplate.opsForValue().set(BUY_FLOW_PREFIX + pay.getId(), booth, 1, TimeUnit.DAYS);
                return Result.success("创建支付成功", zfUtils.getUrl(pay));
            }
        }
        if (booth.getQuantity() - buyNum >= 0) {
            if (boothMapper.updateQuantity(boothId, -buyNum) > 0) {
                booth.setQuantity(buyNum);
                redisTemplate.opsForValue().set(BUY_FLOW_PREFIX + pay.getId(), booth, 1, TimeUnit.DAYS);
                int insert = payMapper.insert(pay);
                if (insert > 0) {
                    return Result.success("创建支付成功", zfUtils.getUrl(pay));
                }
                boothMapper.updateQuantity(boothId, buyNum);
                return Result.err("创建支付错误");
            }
        }
        return Result.err("数量不足");
    }

    @Override
    public Pay getById(Integer id) {
        Pay pay = new Pay();
        pay.setId(id);
        return payMapper.selectOne(pay);
    }

    @Override
    public Float getFlowPrice() {
        Float price = boothMapper.selectById(1).getPrice();
        if (price == null) {
            price = 0.8f;
        }
        return price;
    }

    @Override
    public Result sellFlow(String username, Float price, Integer quantity) {
        User userByUsername = userService.getUserByUsername(username);
        if (userByUsername.getFlow() - Flow.GBtoByte(quantity) > 0) {
            userMapper.addFlowByUsername(username, -Flow.GBtoByte(quantity));
            Booth booth = new Booth();
            booth.setQuantity(quantity);
            booth.setPrice(price);
            booth.setOfficial(false);
            booth.setUsername(username);
            boothMapper.insert(booth);
            return Result.success("摆摊成功!");
        }
        return Result.err("您没有辣么多流量!");
    }

    @Override
    public List<Booth> getNormalBooth() {
        Example commentExample = new Example(Booth.class);
        commentExample.createCriteria().andGreaterThan("quantity", 0).andEqualTo("official", false);
        return boothMapper.selectByExample(commentExample);
    }

    @Override
    public Float getRNum(String username) {
        Double rNum = (Double) redisTemplate.opsForValue().get(REMAINING_SUM + username);
        if (rNum == null) {
            rNum = 0d;
        }
        return new Float(rNum);
    }

    @Override
    public void cancel(@NotBlank String Salt, Integer order_id, String fee, Integer sign) {
        Pay payById = getById(order_id);
        if (payById != null && zfUtils.checkSalt(Salt, fee, order_id, sign)) {
            handlingFailurePay(payById);
        }
    }
    @Scheduled(fixedDelay = 15000)
    private void oderTask(){
        Example commentExample = new Example(Pay.class);
        commentExample.createCriteria().andEqualTo("sign", 0).andLessThan("creationtime", DateUtils.dateAddMinutes(null,-5));
        List<Pay> pays = payMapper.selectByExample(commentExample);
        pays.forEach(pay -> {
            handlingFailurePay(pay);
        });
    }
    private void handlingFailurePay(Pay pay){
        switch (pay.getType()){
            case Pay.Type_Flow:
                Booth booth = (Booth) redisTemplate.opsForValue().get(BUY_FLOW_PREFIX + pay.getId());
                if (booth != null) {
                    redisTemplate.delete(BUY_FLOW_PREFIX + pay.getId());
                    if (!booth.getOfficial()) {
                        //还原
                        Booth booth1 = boothMapper.selectById(booth.getId());
                        booth1.setQuantity(booth1.getQuantity() + booth.getQuantity());
                        boothMapper.updateByPrimaryKey(booth1);
                    }
                }
                break;
            default:
        }
        //取消支付处理
        pay.setSign(-1);
        payMapper.updateByPrimaryKey(pay);
    }
}
