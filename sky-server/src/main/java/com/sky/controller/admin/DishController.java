package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName DishController
 * @Author mashiyu
 * @Version 1.0
 * @Date 2025/11/25 21:21
 * @Description
 */
@Api(tags = "菜品管理相关接口")
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Resource
    private DishService dishService;


    /**
     * 新增菜品
     *
     * @param dto
     * @return
     */
    @PostMapping
    public Result add(@RequestBody DishDTO dto) {
        dishService.addWithFlavor(dto);
        return Result.success("操作成功！");
    }
}
