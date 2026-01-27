package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sky.constant.MessageConstant.CATEGORY_BE_RELATED_BY_DISH;
import static com.sky.constant.MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL;

@Service
@Slf4j
public class CategoryServicelmpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapperd;


    /**
     * 新增分类
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.DISABLE);


        categoryMapper.insert(category);
    }

    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());

        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

        long total = page.getTotal();

        List<Category> records = page.getResult();

        return new PageResult(total,records);

    }

    /**
     * 启用或禁用分类
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        Category category = new Category();
        category.setStatus(status);
        category.setId(id);
        categoryMapper.update(category);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        categoryMapper.update(category);

    }

    /**
     * 删除分类
     * @param id
     */
    public void deleteById(long id) {

        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            throw new DeletionNotAllowedException(CATEGORY_BE_RELATED_BY_DISH);
        }

        Integer count1 = setmealMapperd.countByCategoryId(id);
        if(count1>0){
            throw new DeletionNotAllowedException(CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.deleteById(id);;

    }

    /**
     * 查询分类
     * @param type
     * @return
     */
    public List<Category> list(Long type) {
        List<Category> list = categoryMapper.list(type);
        return list;
    }
}
