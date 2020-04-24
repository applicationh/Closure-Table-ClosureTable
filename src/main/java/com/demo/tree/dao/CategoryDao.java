package com.demo.tree.dao;

import com.demo.tree.entity.Category;
import com.demo.tree.entity.CategoryTree;
import com.demo.tree.entity.TreeNode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (Category)表数据库访问层
 *
 * @author wsh
 * 
 */
@Mapper
public interface CategoryDao {

    /**
     * 通过ID查询单条数据
     */
    Category selectById(Integer id);
    /**
     * 获取所有分类的数量
     */
    Integer  count();

    /**
     * 修改数据
     */
    int update(Category category);

    /**
     * 新增数据
     */
    int insert(Category category);

    /**
     * 通过主键删除数据
     */
    int deleteById(Integer id);

    /**
     * 获取某一级分类的数量，参数从1开始，表示第一级分类（根分类的子类）。
     * @param level 层级（从1开始）
     * @return 数量
     */
    Integer selectCountByLayer(Integer level);
    /**
     * 查询某个节点的子树中所有的节点，不包括参数所指定的节点
     */
    List<Category> selectDescendant(int id);
    /**
     * 查询某个节点的子树中所有的节点的id，不包括参数所指定的节点
     */
    int[] selectDescendantId(int id);
    /**
     * 查询某个节点的第n级子节点
     *
     * @param ancestor 祖先节点ID
     * @param n 距离（0表示自己，1表示直属子节点）
     * @return 子节点列表
     */
    List<Category> selectSubLayer(int ancestor, int n);

    /**
     * 查询某个节点的第N级父节点。如果id指定的节点不存在、操作错误或是数据库被外部修改，
     * 则可能查询不到父节点，此时返回null。
     *
     * @param id 节点id
     * @param n 祖先距离（0表示自己，1表示直属父节点）
     * @return 父节点id，如果不存在则返回null
     */
    Integer selectAncestor(int id, int n);

    /**
     * 查询由id指定节点(含)到根节点(不含)的路径
     * 比下面的<code>selectPathToAncestor</code>简单些。
     *
     * @param id 节点ID
     * @return 路径列表。如果节点不存在，则返回空列表
     */
    List<Category> selectPathToRoot(int id);

    /**
     * 查询由id指定节点(含)到指定上级节点(不含)的路径
     *
     * @param id 节点ID
     * @param ancestor 上级节点的ID
     * @return 路径列表。如果节点不存在，或上级节点不存在，则返回空列表
     */

    List<Category> selectPathToAncestor(int id, int ancestor);

    /**
     * 查找某节点下的所有直属子节点的id
     * 该方法与上面的<code>selectSubLayer</code>不同，它只查询节点的id，效率高点
     *
     * @param parent 分类id
     * @return 子类id数组
     */

    int[] selectSubId(int parent);

    /**
     * 查询某节点到它某个祖先节点的距离
     *
     * @param ancestor 父节点id
     * @param id 节点id
     * @return 距离（0表示到自己的距离）,如果ancestor并不是其祖先节点则返回null
     */

    Integer selectDistance(int ancestor, int id);

    /**
     * 复制父节点的路径结构,并修改descendant和distance
     *
     * @param id 节点id
     * @param parent 父节点id
     */

    void insertPath(int id, int parent);

    /**
     * 在关系表中插入对自身的连接
     *
     * @param id 节点id
     */

    void insertNode(int id);

    /**
     * 从树中删除某节点的路径。注意指定的节点可能存在子树，而子树的节点在该节点之上的路径并没有改变，
     * 所以使用该方法后还必须手动修改子节点的路径以确保树的正确性
     *
     * @param id 节点id
     */

    void deletePath(int id);

    /**
     * 判断分类是否存在
     *
     * @param id 分类id
     * @return true表示存在，null或false表示不存在
     */

    Boolean contains(int id);


    List<TreeNode> selectAll();
}