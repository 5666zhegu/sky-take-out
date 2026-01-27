package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     * @param id
     * @return
     */
    @Select("select count(id) from dish where category_id = #{id}")
    Integer countByCategoryId(Long id);

    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);


    void deleteBatch(List<Long> ids);
}
