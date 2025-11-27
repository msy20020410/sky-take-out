package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;

public interface DishMapper extends BaseMapper<Dish> {
    DishVO selectByIdWithFlavor(Long id);
}
