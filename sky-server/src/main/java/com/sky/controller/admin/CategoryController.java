package com.sky.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.mapper.CategoryMapper;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.vo.PageResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName CategoryController
 * @Author mashiyu
 * @Version 1.0
 * @Date 2025/11/22 18:33
 * @Description
 */
@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类管理接口")
@Slf4j
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 分类分页查询
     *
     * @param dto
     * @return
     */
    @GetMapping("/page")
    public Result page(CategoryPageQueryDTO dto) {
        // 创建分页对象
        Page<Category> page = new Page<>(dto.getPage(), dto.getPageSize());
        // 分页查询
        Page<Category> res = categoryService.lambdaQuery()
                .like(dto.getName() != null, Category::getName, dto.getName())
                .eq(dto.getType() != null, Category::getType, dto.getType())
                .page(page);
        return Result.success(PageResult.of(res.getTotal(), res.getRecords()));

    }


    /**
     * 启用禁用分类
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        categoryService.lambdaUpdate()
                .eq(Category::getId, id)
                .set(Category::getStatus, status)
                .update();
        return Result.success("操作成功！");
    }

    /**
     * 编辑分类
     *
     * @param dto
     * @return
     */
    @PutMapping
    public Result update(@RequestBody CategoryDTO dto) {
        categoryService.lambdaUpdate()
                .eq(Category::getId, dto.getId())
                .set(Category::getType, dto.getType())
                .set(Category::getSort, dto.getSort())
                .set(Category::getName, dto.getName())
                .update();
        return Result.success("修改成功！");
    }
}
