package com.how2java.pojo;

/**
 * @program: ssm
 * @description:
 * @author: syx
 * @create: 2019-07-26 12:08
 **/
public class Category {
    private int id;
    private String name;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + "]";
    }
}
