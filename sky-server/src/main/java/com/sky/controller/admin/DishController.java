package com.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import javassist.runtime.Desc;
import org.springframework.stereotype.Service;
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


    /**
     * 菜品分页查询
     *
     * @param dto
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dto) {
        PageResult pageResult = dishService.pageQuery(dto);
        return Result.success(pageResult);
    }

    /**
     * 修改菜品
     *
     * @param dto
     * @return
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dto) {
        dishService.updateDishWithFlavor(dto);
        return Result.success("操作成功！");
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        dishService.removeBatch(ids);
        return Result.success("操作成功！");
    }
}
