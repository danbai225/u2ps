package cn.p00q.u2ps.mapper;

import cn.p00q.u2ps.entity.Booth;
import cn.p00q.u2ps.utils.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author DanBai
 */
@Repository
public interface BoothMapper extends MyMapper<Booth> {
    @Select("SELECT *  FROM `booth` WHERE `id` = #{id}")
    Booth selectById(Integer id);
    @Update("UPDATE `booth` SET `quantity`= quantity+#{num} WHERE id=#{id} AND quantity+#{num}>=0")
    int updateQuantity(Integer id,Integer num);
}