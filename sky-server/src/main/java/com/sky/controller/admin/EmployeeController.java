package com.sky.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.BaseException;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.utils.UserContext;
import com.sky.vo.EmployeeLoginVO;
import com.sky.vo.PageResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@Api(tags = "员工相关接口")
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping
    public Result<String> add(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("新增员工：{}", employeeLoginDTO);

        // 校验用户名是否唯一
        List<Employee> employeeList = employeeService.lambdaQuery()
                .eq(Employee::getUsername, employeeLoginDTO.getUsername()).list();
        if (employeeList != null && employeeList.size() > 0) {
            throw new RuntimeException("用户名已存在！");
        }
        // 将dto转为entity
        Employee employee = new Employee();
        BeanUtil.copyProperties(employeeLoginDTO, employee);
        // 密码进行md5加密
        employee.setPassword(DigestUtil.md5Hex("123456"));
        boolean saved = employeeService.save(employee);
        if (!saved) {
            return Result.error("新增员工失败！");
        }
        return Result.success("新增成功！");
    }

    /**
     * 分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result page(EmployeePageQueryDTO employeePageQueryDTO) {
        // 给姓名添加默认值
        if (employeePageQueryDTO.getName() == null) {
            employeePageQueryDTO.setName("");
        }

        // 创建分页对象
        Page<Employee> page = new Page<>(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        // 查询
        Page<Employee> employeePage = employeeService.lambdaQuery()
                .like(Employee::getName, employeePageQueryDTO.getName())
                .page(page);

        return Result.success(PageResult.of(employeePage.getTotal(), employeePage.getRecords()));
    }

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        employeeService.lambdaUpdate()
                .eq(Employee::getId, id)
                .set(Employee::getStatus, status)
                .update();
        return Result.success("状态修改成功！");
    }

    /**
     * 根据id查询员工信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }


    /**
     * 编辑员工信息
     *
     * @param employeeLoginDTO
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        Employee employee = Employee.builder()
                .idNumber(employeeLoginDTO.getIdNumber())
                .name(employeeLoginDTO.getName())
                .phone(employeeLoginDTO.getPhone())
                .sex(employeeLoginDTO.getSex())
                .username(employeeLoginDTO.getUsername())
                .build();
        employeeService.update(employee, new UpdateWrapper<Employee>().eq("id", employeeLoginDTO.getId()));
        return Result.success("员工信息修改成功！");
    }

    @PutMapping("/editPassword")
    public Result<String> editPassword(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        // TODO 由于前端页面没有传递员工id 所以只能自己获取 获取当前登录用户的id
        Long empId = UserContext.getCurrentUser();
        // TODO 适配接口文档的empId参数
        employeeLoginDTO.setEmpId(Math.toIntExact(empId));
        Employee employee = employeeService.lambdaQuery()
                .eq(Employee::getId, employeeLoginDTO.getEmpId())
                .one();
        // 校验旧密码是否有误
        if (!employee.getPassword().equals(DigestUtil.md5Hex(employeeLoginDTO.getOldPassword()))) {
            throw new BaseException("旧密码输入有误！");
        }
        // 设置新密码
        employeeService.lambdaUpdate()
                .eq(Employee::getId, employeeLoginDTO.getEmpId())
                .set(Employee::getPassword, DigestUtil.md5Hex(employeeLoginDTO.getNewPassword()))
                .update();
        return Result.success("密码修改成功！");
    }
}
