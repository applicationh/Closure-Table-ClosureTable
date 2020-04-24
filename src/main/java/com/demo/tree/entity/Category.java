package com.demo.tree.entity;

import java.io.Serializable;

/**
 * (Category)实体类
 *
 * @author wsh
 * 
 */
public class Category implements Serializable {
    private static final long serialVersionUID = -85701777763418428L;
    
    private Integer id;
    
    private String name;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}