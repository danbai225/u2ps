package cn.p00q.u2ps.utils;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author danbai
 * @date 2019/10/13
 */

public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}