package com.foxxy.git.zookeeper.listener;

import com.foxxy.git.zookeeper.ZkNodeData;

/**
 * 节点数据监听改变监听类<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface ZkNodeListener {

    /**
     * 
     * 功能描述: 通知子节点数据更新 〈功能详细描述〉
     *
     * @param nodeData
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void childUpdated(ZkNodeData nodeData);

    /**
     * 
     * 功能描述: 通知子节点数据新增 〈功能详细描述〉
     *
     * @param nodeData
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void childAdded(ZkNodeData nodeData);

    /**
     * 
     * 功能描述:通知子节点数据删除 〈功能详细描述〉
     *
     * @param nodeData
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void childDeleted(ZkNodeData nodeData);

    /**
     * 
     * 功能描述: 通知节点数据改变 〈功能详细描述〉
     *
     * @param nodeData
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void nodeUpdated(ZkNodeData nodeData);

    /**
     * 
     * 功能描述: 接受指定path的访问，这是典型的策略模式实现 〈功能详细描述〉
     *
     * @param nodeData
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    boolean accept(ZkNodeData nodeData);
}
