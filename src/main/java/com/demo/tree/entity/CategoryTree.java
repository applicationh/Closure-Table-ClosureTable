package com.demo.tree.entity;

import java.io.Serializable;

/**
 * (CategoryTree)实体类
 *
 * @author wsh
 * 
 */
public class CategoryTree implements Serializable {
    private static final long serialVersionUID = 105603675260029734L;
    
    private Integer ancestor;
    
    private Integer descendant;
    
    private Integer distance;

    public Integer getAncestor() {
        return ancestor;
    }

    public void setAncestor(Integer ancestor) {
        this.ancestor = ancestor;
    }

    public Integer getDescendant() {
        return descendant;
    }

    public void setDescendant(Integer descendant) {
        this.descendant = descendant;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

}