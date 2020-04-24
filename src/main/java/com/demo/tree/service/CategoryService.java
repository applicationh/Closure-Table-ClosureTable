package com.demo.tree.service;

import com.demo.tree.entity.Category;
import com.demo.tree.entity.CategoryTree;
import com.demo.tree.entity.TreeNode;

import java.util.List;

/**
 * (Category)表服务接口
 *
 * @author wsh
 * 
 */
public interface CategoryService {

    /**
     * 通过ID查询单条数据
     */
    Category selectById(Integer id);

    int add(Category category, int parent);

    Boolean update(Category category);


    int count();

    int countOfLayer(int layer);


    Category getParent(int id);

    Category getAncestor(int id, int n);

    TreeNode selectAll();

    List<Category> getChildren(int id);

    List<Category> getChildren(int id, int n);

    List<Category> getPath(int id);

    List<Category> getPathRelativeTo(int id, int ancestor);

    int getLevel(int id);

    void moveTo(int id, int target);

    void moveTreeTo(int id, int target);

    void deleteAndMove(int id);

    void deleteByIdAllTree(int id);
}