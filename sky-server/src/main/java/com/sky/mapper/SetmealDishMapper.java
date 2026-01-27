package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    List<Long> getSetmealIdbyDishIdes(List<Long> ids);

    @Select("select * from setmeal where id = #{id}")
    List<Long> getSetmealIdbyDishId(Long id);
}
