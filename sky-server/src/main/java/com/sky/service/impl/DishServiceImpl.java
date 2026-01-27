package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DIshService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DIshService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor ->{
                flavor.setDishId(id);
            });
        dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        long total = page.getTotal();

        List records = page.getResult();


        return new PageResult(total,records);
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {

        ids.forEach(id ->{
            Dish dish = dishMapper.getById(id);
            Integer status = dish.getStatus();
            if(status == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        List<Long> setmealIdes = setmealDishMapper.getSetmealIdbyDishId(ids);
        if(setmealIdes != null && setmealIdes.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        dishMapper.deleteBatch(ids);

        dishFlavorMapper.deleteBatch(ids);

    }


    /**
     * 菜品回显/根据id查询菜品
     * @param id
     * @return
     */
    public DishVO getById(Long id) {

        DishVO dishVO = dishMapper.getDishVOById(id);

        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        Long id = dishDTO.getId();

        Long id1 = dish.getId();
        dishFlavorMapper.deleteByDishId(id1);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(flavor ->{
            flavor.setId(dish.getId());
        });
        dishFlavorMapper.insertBatch(flavors);

    }

    /**
     * 根据分类id查询菜品
     * @param id
     * @return
     */
    public List<Dish> getByCategoryId(Long categoryId) {
        Dish dish = Dish.builder()
                .status(StatusConstant.ENABLE)
                .categoryId(categoryId)
                .build();
        return dishMapper.getByCategoryId(dish);
    }
}
