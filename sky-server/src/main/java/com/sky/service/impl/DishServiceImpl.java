package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.dto.DishDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.service.CategoryService;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName DishServiceImpl
 * @Author mashiyu
 * @Version 1.0
 * @Date 2025/11/25 21:23
 * @Description
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorService dishFlavorService;
    @Resource
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dto
     */
    @Override
    @Transactional
    public void addWithFlavor(DishDTO dto) {
        // 判断当前传入的分类是否存在
        if (categoryService.lambdaQuery().eq(Category::getId, dto.getCategoryId()).one() == null) {
            throw new RuntimeException("当前分类不存在！");
        }
        // 1. 将菜品信息添加到数据库
        Dish dish = new Dish();
        // 1.1 传入必填参数
        dish.setCategoryId(dto.getCategoryId());
        dish.setImage(dto.getImage());
        dish.setName(dto.getName());
        dish.setPrice(dto.getPrice());
        // 1.2 传入可选参数
        if (dto.getDescription() != null) {
            dish.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            dish.setStatus(dto.getStatus());
        }
        dishMapper.insert(dish);

        // 2. 将菜品的口味信息添加到数据库
        List<DishFlavor> flavorList = dto.getFlavors();
        if (flavorList != null && !flavorList.isEmpty()) {
            DishFlavor dishFlavor = new DishFlavor();
            for (DishFlavor flavor : flavorList) {
                    // 这里要在实体表进行注解配置，才能拿到自增的id
                    dishFlavor.setDishId(dish.getId());
                if (flavor.getId() != null) {
                    dishFlavor.setDishId(flavor.getId());
                }
                dishFlavor.setName(flavor.getName());
                dishFlavor.setValue(flavor.getValue());
                dishFlavorService.save(dishFlavor);
            }
        }
    }
}
