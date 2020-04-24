package com.demo.tree.service.impl;

import com.demo.tree.entity.Category;
import com.demo.tree.dao.CategoryDao;
import com.demo.tree.entity.CategoryTree;
import com.demo.tree.entity.TreeNode;
import com.demo.tree.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * (Category)表服务实现类
 *
 * @author wsh
 * 
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryDao categoryDao;

    @Override
    public Category selectRoot() {
        return categoryDao.selectRoot();
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Category selectById(Integer id) {
        if (id < 0) {
            return null;
        }
        return categoryDao.selectById(id);
    }
    /**
     * 获取所有分类的数量。
     *
     * @return 数量
     */
    @Override
    public int count() {
        return categoryDao.count();
    }

    /**
     * 获取某一级分类的数量，参数从1开始，表示第一级分类（根分类的子类）。
     * @param layer 层级（从1开始）
     * @return 数量
     */
    @Override
    public int countOfLayer(int layer) {
        checkPositive(layer, "layer");
        return categoryDao.selectCountByLayer(layer);
    }


    /**
     * 新增一个分类，其ID属性将自动生成或计算，并返回。
     * 新增分类的继承关系由parent属性指定，parent为0表示该分类为一级分类。
     *
     * @param category 分类实体对象
     * @param parent   上级分类id
     * @throws IllegalArgumentException 如果parent所指定的分类不存在、category为null或category中存在属性为null
     */
    @Override
    public int add(Category category, int parent) {
        checkNotNegative(parent,"parent");
        if (parent > 0 && categoryDao.contains(parent) == null) {
            throw new IllegalArgumentException("指定的上级分类不存在");
        }
        categoryDao.insert(category);
        categoryDao.insertPath(category.getId(), parent);
        categoryDao.insertNode(category.getId());
        return category.getId();
    }
    /**
     * 修改数据
     *  该方法仅更新分类的属性，不修改继承关系，若要移动节点请使用moveTo或者moveTreeTo
     */
    @Override
    public Boolean update(Category category) {
        return categoryDao.update(category) > 0;
    }


    //以下为查询关系操作

    /**
     * 获取分类的父分类。
     *
     * @return 父分类实体对象，如果指定的分类是一级分类，则返回null
     */
    @Override
    public Category getParent(int id) {
        return getAncestor(id,1);
    }

    /**
     * 查询指定分类往上第n级分类。
     *
     * @param id 自身节点的id
     * @param n 距离
     * @return 上级分类，如果不存在则为null
     */
    @Override
    public Category getAncestor(int id, int n) {
        checkNotNegative(n,"n");
        Integer parent = categoryDao.selectAncestor(id, n);
        return parent == null ? null : categoryDao.selectById(parent);
    }

    /**
     * list转树形 封装layui格式
     */
    @Override
    public TreeNode selectAll() {
        List<TreeNode> treeNodes = categoryDao.selectAll();


        Category category = categoryDao.selectRoot();
        //不存 储初始化一个默认的
        if (null == category) {
            category = new Category();
            category.setName("根目录");
            categoryDao.insert(category);
            categoryDao.insertNode(category.getId());
        }


        TreeNode root = new TreeNode();
        //根目录
        root.setId(category.getId());
        root.setTitle(category.getName());
        root.setSpread(true);
        root.setChildren(new ArrayList<>());
        List<TreeNode> result = root.getChildren();

        for (TreeNode treeNode : treeNodes) {
            if (treeNode.getPid() == root.getId()) {
                result.add(treeNode);
            }

            for (TreeNode node : treeNodes) {
                if (node.getPid() == treeNode.getId()) {
                    if (treeNode.getChildren() == null) {
                        treeNode.setChildren(new ArrayList<>());
                    }
                    treeNode.getChildren().add(node);
                }
            }
        }
        return root;
    }
    /**
     * 分类的下的直属子分类。
     * @param id 自身节点的id
     * @return 直属子类列表，如果id所指定的分类不存在、或没有符合条件的分类，则返回空列表
     * @throws IllegalArgumentException 如果id小于0
     */
    @Override
    public List<Category> getChildren(int id) {
        return getChildren(id,1);
    }
    /**
     * 分类的下的第n级子分类。
     *
     * @param id 自身节点的id
     * @param n 向下级数，1表示直属子分类
     * @return 子类列表，如果id所指定的分类不存在、或没有符合条件的分类，则返回空列表
     * @throws IllegalArgumentException 如果id小于0，或n不是正数
     */
    @Override
    public List<Category> getChildren(int id, int n) {
        checkNotNegative(id, "id");
        checkPositive(n, "n");
        return categoryDao.selectSubLayer(id, n);
    }

    /**
     * 获取由顶级分类到此分类(含)路径上的所有的分类对象。
     * 如果指定的分类不存在，则返回空列表。
     *
     * @param id 自身节点的id
     * @return 分类实体列表，越靠上的分类在列表中的位置越靠前
     */
    @Override
    public List<Category> getPath(int id) {
        return categoryDao.selectPathToRoot(id);
    }

    /**
     * 获取此分类(含)到其某个的上级分类（不含）之间的所有分类的实体对象（仅查询id和name属性）。
     * 如果指定的分类、上级分类不存在，或是上级分类不是指定分类的上级，则返回空列表
     *
     * @param id 自身节点的id
     * @param ancestor 上级分类的id，若为0则表示获取到一级分类（含）的列表。
     * @return 分类实体列表，越靠上的分类在列表中的位置越靠前。
     * @throws IllegalArgumentException 如果ancestor小于1。
     */
    @Override
    public List<Category> getPathRelativeTo(int id, int ancestor) {
        checkPositive(ancestor, "ancestor");
        return categoryDao.selectPathToAncestor(id, ancestor);
    }

    /**
     * 查询分类是哪一级的，根分类级别是0。
     *
     * @param id 自身节点的id
     * @return 级别
     */
    @Override
    public int getLevel(int id) {
        return categoryDao.selectDistance(0, id);
    }

    /**
     * 将一个分类移动到目标分类下面（成为其子分类）。被移动分类的子类将自动上浮（成为指定分类
     * 父类的子分类），即使目标是指定分类原本的父类。
     * <p>
     * 例如下图(省略顶级分类)：
     *       1                                    1
     *       |                                  / | \
     *       2                                 3  4  5
     *     / | \         (id=2).moveTo(7)           / \
     *    3  4  5       ----------------->         6   7
     *         / \                                /  / | \
     *       6    7                              8  9  10 2
     *      /    /  \
     *     8    9    10
     *
     * @param target 目标分类的id
     * @throws IllegalArgumentException 如果target所表示的分类不存在、或此分类的id==target
     */
    @Override
    public void moveTo(int id, int target) {
        if(id == target) {
            throw new IllegalArgumentException("不能移动到自己下面");
        }

        checkNotNegative(target, "target");
        if(target > 0 && categoryDao.contains(target) == null) {
            throw new IllegalArgumentException("指定的上级分类不存在");
        }

        moveSubTree(id, categoryDao.selectAncestor(id, 1));
        moveNode(id, target);
    }

    /**
     * 将一个分类移动到目标分类下面（成为其子分类），被移动分类的子分类也会随着移动。
     * 如果目标分类是被移动分类的子类，则先将目标分类（连带子类）移动到被移动分类原来的
     * 的位置，再移动需要被移动的分类。
     * <p>
     * 例如下图(省略顶级分类)：
     *       1                                      1
     *       |                                      |
     *       2                                      7
     *     / | \        (id=2).moveTreeTo(7)      / | \
     *    3  4  5      -------------------->     9  10  2
     *         / \                                  / | \
     *       6    7                                3  4  5
     *      /    /  \                                    |
     *     8    9    10                                  6
     *                                                   |
     *                                                   8
     *
     * @param target 目标分类的id
     * @throws IllegalArgumentException 如果id或target所表示的分类不存在、或id==target
     */
    @Override
    public void moveTreeTo(int id, int target) {
        checkNotNegative(target, "target");
        if(target > 0 && categoryDao.contains(target) == null) {
            throw new IllegalArgumentException("指定的上级分类不存在");
        }

        /* 移动分移到自己子树下和无关节点下两种情况 */
        Integer distance = categoryDao.selectDistance(id, target);

        // noinspection StatementWithEmptyBody
        if (distance == null) {
            // 移动到父节点或其他无关系节点，不需要做额外动作
        } else if (distance == 0) {
            throw new IllegalArgumentException("不能移动到自己下面");
        } else {
            // 如果移动的目标是其子类，需要先把子类移动到本类的位置
            int parent = categoryDao.selectAncestor(id, 1);
            moveNode(target, parent);
            moveSubTree(target, target);
        }

        moveNode(id, target);
        moveSubTree(id, id);
    }



    /**
     * 删除一个分类，原来在该分类下的子分类将被移动到该分类的父分类中，
     * 如果此分类是一级分类，则删除后子分类将全部成为一级分类。
     * <p>
     * 顶级分类不可删除。
     *
     * @param id 要删除的分类的id
     * @throws IllegalArgumentException 如果指定id的分类不存在
     */
    @Override
    public void deleteAndMove(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("参数id必须为正数:" + id);
        }
        if (categoryDao.contains(id) == null) {
            throw new IllegalArgumentException("指定的分类不存在");
        }
        Integer parent = categoryDao.selectAncestor(id, 1);
        if (parent == null) {
            parent = 0;
        }
        Category byId =selectById (id);
        moveSubTree(byId.getId(),parent);
        deleteBoth(id);
    }

    /**
     * 删除一个分类及其所有的下级分类。
     * <p>
     * 顶级分类不可删除。
     *
     * @param id 要删除的分类的id
     * @throws IllegalArgumentException 如果指定id的分类不存在
     */
    @Override
    public void deleteByIdAllTree(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("参数id必须为正数:" + id);
        }
        if (categoryDao.contains(id) == null) {
            throw new IllegalArgumentException("指定的分类不存在");
        }
        deleteBoth(id);
        for (int des : categoryDao.selectDescendantId(id)) {
            deleteBoth(des);
        }
    }




    /**
     * 删除一个分类，两个表中的相关记录都删除
     *
     * @param id 分类id
     */
    private void deleteBoth(int id) {
       categoryDao.deleteById(id);
       categoryDao.deletePath(id);
    }

    /**
     * 将指定节点的所有子树移动到某节点下
     * 如果两个参数相同，则相当于重建子树，用于父节点移动后更新路径
     *
     * @param id     指定节点id
     * @param parent 某节点id
     */
    private void moveSubTree(int id, int parent) {
        int[] subs = categoryDao.selectSubId(id);
        for (int sub : subs) {
            moveNode(sub, parent);
            moveSubTree(sub, sub);
        }
    }
    /**
     * 将指定节点移动到另某节点下面，该方法不修改子节点的相关记录，
     * 为了保证数据的完整性，需要与moveSubTree()方法配合使用。
     *
     * @param id 指定节点id
     * @param parent 某节点id
     */
    private void moveNode(int id, int parent) {
        categoryDao.deletePath(id);
        categoryDao.insertPath(id, parent);
        categoryDao.insertNode(id);
    }

    public  void checkPositive(int value, String valname) {
        if (value <= 0) throw new IllegalArgumentException("参数" + valname + "必须是正数:" + value);
    }

    public  void checkNotNegative(int value, String valname) {
        if (value < 0) throw new IllegalArgumentException("参数" + valname + "不能为负:" + value);
    }
}