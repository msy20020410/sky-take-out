package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.CategoryService;
import com.sky.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @ClassName CategoryServiceImpl
 * @Author mashiyu
 * @Version 1.0
 * @Date 2025/11/22 18:32
 * @Description
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
