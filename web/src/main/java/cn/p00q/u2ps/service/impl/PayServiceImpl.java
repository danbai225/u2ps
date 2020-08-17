package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.entity.Pay;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.mapper.PayMapper;
import cn.p00q.u2ps.service.PayService;
import cn.p00q.u2ps.utils.zfUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotBlank;

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
    public static Float AuthenticationFee=0.50f;
    public static String RedisAuthenticationFee="AuthenticationFee";
    public PayServiceImpl(RedisTemplate redisTemplate, PayMapper payMapper) {
        this.redisTemplate = redisTemplate;
        this.payMapper = payMapper;
    }

    @Override
    public String buyAuthentication(String username, Integer payType) {
        Float fee = (Float) redisTemplate.opsForValue().get(RedisAuthenticationFee);
        if(fee==null){
            fee=AuthenticationFee;
        }
        Pay pay =  Pay.PayInit();
        pay.setId(countPay()+1);
        pay.setPaytype(payType);
        pay.setFee(fee);
        pay.setUsername(username);
        pay.setType(Pay.Type_Authentication);
        int insert = payMapper.insert(pay);
        if(insert>0){
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
        String rUrl="/";
        if(sign>=1){
            Pay payById = getById(order_id);
            if(payById!=null&&zfUtils.checkSalt(Salt,fee,order_id,sign)){
                switch (payById.getType()){
                    case Pay.Type_Authentication:
                        redisTemplate.opsForSet().add(User.RedisAuthenticationSet,payById.getUsername());
                        rUrl=doMainUrl+"/autonym";
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
    public Pay getById(Integer id) {
        Pay pay = new Pay();
        pay.setId(id);
        return payMapper.selectOne(pay);
    }
}
