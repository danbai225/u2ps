package cn.p00q.u2ps.mapper;

import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author DanBai
 */
@Repository
public interface UserMapper extends MyMapper<User> {
    /**
     * 更新流量
     * @param userId
     * @param flow
     */
    @Update("UPDATE `user` SET `flow` = '#{flow}' WHERE `user`.`id` = #{id};")
    void updateFlow(@Param("id") Integer userId,@Param("flow")Long flow);
}