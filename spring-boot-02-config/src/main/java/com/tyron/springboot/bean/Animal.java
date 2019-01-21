package com.tyron.springboot.bean;

/**
 * @Description: 动物实体类
 * @Author: tyron
 * @date: 2019/1/22
 */
public class Animal {

    private String name;
    private Integer age;

    @Override
    public String toString() {
        return "Animal{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
