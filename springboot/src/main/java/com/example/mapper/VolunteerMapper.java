package com.example.mapper;

import com.example.entity.User;
import com.example.entity.VolApply;
import com.example.entity.Volunteer;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作volunteer相关数据接口
 */
public interface VolunteerMapper {

    /**
     * 新增
     */
    int insert(Volunteer volunteer);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Volunteer volunteer);

    /**
     * 根据ID查询
     */
    Volunteer selectById(Integer id);

    /**
     * 查询所有
     */
    List<Volunteer> selectAll(Volunteer volunteer);

    @Select("select * from volunteer where username = #{username}")
    Volunteer selectByUsername(String username);

}