package com.tyron.springboot.controller;

import com.tyron.springboot.dao.DepartmentDao;
import com.tyron.springboot.dao.EmployeeDao;
import com.tyron.springboot.entity.Department;
import com.tyron.springboot.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;

/**
 * @Description: 员工管理控制器
 * @Author: tyron
 * @date: 2019/3/4
 */
@Controller
public class EmployeeController {

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    DepartmentDao departmentDao;

    /**
     * 员工列表
     *
     * @param model 页面显示参数
     * @return 列表页
     */
    @GetMapping("emps")
    public String list(Model model) {
        Collection<Employee> employees = employeeDao.getAll();
        model.addAttribute("emps", employees);
        return "emp/list";
    }

    /**
     * 添加页面
     *
     * @param model 页面显示参数
     * @return 添加页
     */
    @GetMapping("/emp/add")
    public String getAdd(Model model) {
        // 获取部门列表
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts", departments);
        return "emp/add";
    }

    /**
     * 添加请求
     *
     * @param employee 员工实体
     * @return 员工列表页
     */
    @PostMapping("/emp")
    public String add(Employee employee) {
        System.out.println(employee);
        // 添加
        employeeDao.save(employee);
        return "redirect:/emps";
    }
}
