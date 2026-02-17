package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     * @param id
     * @return
     */
    @Select("select count(id) from dish where category_id = #{id}")
    Integer countByCategoryId(Long id);

    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);


    void deleteBatch(List<Long> ids);

    @Select("select d.*,c.name categoryName from dish d left join category c on d.category_id = c.id where d.id = #{id}")
    DishVO getDishVOById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);


    List<Dish> getByCategoryId(Dish dish);

    @AutoFill(OperationType.UPDATE)
    void startOrStop(Dish dish);

    @Select("select a.* from dish a left join setmeal_dish b on b.dish_id = a.id where b.setmeal_id = #{id}")
    List<Dish> getBySetmealId(Long id);

    List<Dish> list(Dish dish);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);



}
