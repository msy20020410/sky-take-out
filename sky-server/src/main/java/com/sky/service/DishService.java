package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @InterfaceName DishService
 * @Author mashiyu
 * @Version 1.0
 * @Date 2025/11/25 21:23
 */
public interface DishService extends IService<Dish> {
    void addWithFlavor(DishDTO dto);

    PageResult pageQuery(DishPageQueryDTO dto);

    void updateDishWithFlavor(DishDTO dto);

    DishVO getByIdWithFlavor(Long id);

    void removeBatch(List<Long> ids);
}
