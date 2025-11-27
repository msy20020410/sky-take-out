package com.sky.service.impl;

import cn.hutool.core.bean.BeanException;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DishServiceImpl
 * @Author mashiyu
 * @Version 1.0
 * @Date 2025/11/25 21:23
 * @Description
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorService dishFlavorService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private SetmealDishService setmealDishService;

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

    @Override
    public PageResult pageQuery(DishPageQueryDTO dto) {
        // 开启分页
        Page<Dish> page = new Page<>(dto.getPage(), dto.getPageSize());
        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        if (dto.getCategoryId() != null) {
            queryWrapper.eq(Dish::getCategoryId, dto.getCategoryId());
        }
        if (dto.getName() != null) {
            queryWrapper.like(Dish::getName, dto.getName());
        }
        if (dto.getStatus() != null) {
            queryWrapper.eq(Dish::getStatus, dto.getStatus());
        }
        // 分页查询菜品信息
        Page<Dish> dishPage = dishMapper.selectPage(page, queryWrapper);
        List<Dish> records = dishPage.getRecords();
        // 查询分类名称,并封装结果
        List<DishVO> res = new ArrayList<>();
        records.forEach(dish -> {
            Category category = categoryService.lambdaQuery()
                    .eq(Category::getId, dish.getCategoryId())
                    .one();
            DishVO dishVO = new DishVO();
            BeanUtil.copyProperties(dish, dishVO);
            dishVO.setCategoryName(category.getName());
            res.add(dishVO);
        });
        return PageResult.builder()
                .total(dishPage.getTotal())
                .records(res)
                .build();
    }

    @Override
    public void updateDishWithFlavor(DishDTO dto) {
        // 更新菜品
        Dish dish = new Dish();
        BeanUtil.copyProperties(dto, dish);
        dishMapper.updateById(dish);
        // 更新菜品口味
        List<DishFlavor> flavors = dto.getFlavors();
        flavors.forEach(flavor -> {
            dishFlavorService.lambdaUpdate()
                    .eq(flavor.getDishId() != null, DishFlavor::getId, flavor.getDishId())
                    .eq(flavor.getId() != null, DishFlavor::getId, flavor.getId())
                    .set(DishFlavor::getName, flavor.getName())
                    .set(DishFlavor::getValue, flavor.getValue())
                    .update();
        });
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dishVO = dishMapper.selectByIdWithFlavor(id);
        log.info("查询到的菜品信息为：{}", dishVO);
        return dishVO;
    }

    @Override
    @Transactional
    public void removeBatch(List<Long> ids) {
        List<Dish> dishList = dishMapper.selectBatchIds(ids);
        dishList.forEach(dish -> {
            // 起售中的菜品无法删除
            if (dish.getStatus() == 1) {
                throw new BaseException("起售中的菜品无法删除！");
            }
        });
        // 被套餐关联的菜品无法删除
        List<SetmealDish> setmealDishList = setmealDishService.lambdaQuery()
                .in(SetmealDish::getDishId, ids)
                .list();
        if (setmealDishList != null && !setmealDishList.isEmpty()) {
            throw new BaseException("被套餐关联的菜品无法删除！");
        }

        ids.forEach(id -> {
            Dish dish = dishMapper.selectById(id);
            dishMapper.deleteById(id);


            // 删除关联的口味数据
            List<DishFlavor> list = dishFlavorService.lambdaQuery()
                    .eq(DishFlavor::getDishId, id)
                    .list();
            if (list != null && !list.isEmpty()) {
                dishFlavorService
                        .remove(new LambdaUpdateWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
            }
        });
    }
}
