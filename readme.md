# ClosureTable java  mysql  MyBatis  demo 


网上资料基于ClosureTable实例应用较少不完整，大部分都是讲的数据库层次
综合网上资料及参考已有代码[ClosureTableCateogryStore](https://github.com/Kaciras/ClosureTableCateogryStore)进行完整项目整合



基本上翻版了上述地址代码，主要修改有以下内容

* 去除table中无符号数据格式
* 将MyBatis注解方式转换为xml格式（个人习惯+项目需要）
* 去除entity中充血模型，将方法重新编写转为service层
* 解决查询出来树状图json格式
* 添加简单基于layui组件完成的增删改查


缺点

* 去除充血模型导致serviceImpl过于庞大，里面包含了的节点操作和查询操作
* 未拓展维护category_tree添加是否有子父节点


# 表结构梳理
完成这个树状图需要两个表格（sql脚本在resource下）


category 表和category_tree表

字段 | 备注| |字段 | 备注
-------- | -----| -----| -----| -----
id|节点的id| |ancestor| 祖先节点的ID
name|节点名称| |descendant|子节点id
 | | | | distance| 和祖先距离


```sql
/* 插入根分类 */
INSERT INTO `category_tree` (ancestor,descendant,distance) VALUES (0, 0, 0);
INSERT INTO `category` (`id`, `name`) VALUES (0, 'root');
```

比如category表中目前有三个节点1 2 3想存成（mk打树状图太麻烦了。。）
 

- root(0)
   - 1(1)
        - 2(2)  
            - 3(3)
   - 4(4)  

主要梳理tree表的存储过程
* 节点1存储
     * 0 1 1 （父级是根0，自身是1，距离1个）
     * 1 1 0 （存自身）
* 节点2存储
    *  0 2 2 （父级是根0，自身是2，距离2个）
    *  1 2 1 （父是节点1，自身是2，距离1个）
    *  2 2 0 （存自身）
* 节点3存储
    * 0 3 3 （父级是根0，自身是3，距离3个）
    * 1 3 2 （父是节点1，自身是3，距离2个）
    * 2 3 1 （父是节点2，自身是3，距离1个）
    * 3 3 0 （存自身）
    
    
如果我想在root下再挂一个4呢
* 节点4存储
     * 0 4 1 （父级是根0，自身是4，距离1个）
     * 4 4 0 （存自身）
     
     
     
有了tree表这些数据，思考一下查询是不是就简单多了，例如

1. 查询1节点下有多少节点，查父id是1 距离大于0（不包含自身）
2. 查询1节点下面有谁呢，查父id是1 距离是1
3. 查询当前是第几级，查节点id+父id为0
4. 某一级别所有节点，父id为0，距离固定就好了

等等




# 附录知识点

ClosureTable以一张表存储节点之间的关系、其中包含了任何两个有关系（祖先与子代）节点的关联信息。其包含3个字段：
                                                     
* `ancestor` 祖先：祖先节点的id
* `descendant` 子代：子代节点的id
* `distance` 距离：子代到祖先中间隔了几代

ClosureTable能够很好地解决树结构在关系数据库中的查询需求。


#演示
* jdk 1.8
* mysql 5.5
* spring boot 2.2.6.RELEASE

改一下数据库配置就行了，怕git没有图片，都截图在doc里了
