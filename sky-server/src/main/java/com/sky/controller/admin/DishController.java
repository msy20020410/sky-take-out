package com.sky.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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


    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dto) {
        PageResult pageResult = dishService.pageQuery(dto);
        return Result.success(pageResult);
    }
}
