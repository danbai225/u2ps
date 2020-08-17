package cn.p00q.u2ps.mapper;

import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author DanBai
 */
@Repository
public interface UserMapper extends MyMapper<User> {
    @Update("UPDATE `user` SET `flow` = #{flow} WHERE `user`.`id` = #{id};")
    void updateFlow(@Param("id") @NotNull Integer userId, @Param("flow")Long flow);

    @Update("UPDATE `user` SET `flow` = flow+#{flow} WHERE `user`.`username` = #{username};")
    void addFlowByUsername(@Param("username") @NotBlank String username, @Param("flow")Integer flow);
}