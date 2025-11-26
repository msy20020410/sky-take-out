package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;

/**
 * @InterfaceName DishService
 * @Author mashiyu
 * @Version 1.0
 * @Date 2025/11/25 21:23
 */
public interface DishService extends IService<Dish> {
    void addWithFlavor(DishDTO dto);
}
