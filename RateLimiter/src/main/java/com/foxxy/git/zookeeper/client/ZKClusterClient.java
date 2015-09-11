package com.foxxy.git.zookeeper.client;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;

import com.foxxy.git.zookeeper.CreateMode;
import com.foxxy.git.zookeeper.Node;
import com.foxxy.git.zookeeper.listener.ZKConnectionListener;
import com.foxxy.git.zookeeper.listener.ZkNodeListener;

/**
 * 操作zk服务器的统一客户端封装<br> 
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface ZKClusterClient {

    /**
     * 
     * 功能描述: 创建节点 〈功能详细描述〉
     *
     * @param path
     * @param value
     * @param mode
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void createNode(String path, String value, CreateMode mode);

    /**
     * 
     * 功能描述: 获取指定节点的值 〈功能详细描述〉
     *
     * @param path
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    String getNodeValue(String path);

    /**
     * 
     * 功能描述: 获取指定节点的所有子节点列表 〈功能详细描述〉
     *
     * @param path
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    List<Node> getChildNodes(String path);

    /**
     * 
     * 功能描述: 删除指定节点 〈功能详细描述〉
     *
     * @param path
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void deleteNode(String path);

    /**
     * 
     * 功能描述: 修改指定节点的值 〈功能详细描述〉
     *
     * @param path
     * @param value
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void setNodeValue(String path, String value);
    
    /**
     * 
     * 功能描述: 节点是否存在,返回true表示存在
     * 〈功能详细描述〉
     *
     * @param path
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    boolean isExsit(String path);
    
    /**
     * 
     * 功能描述: 注册节点监听
     * 〈功能详细描述〉
     * @param path
     * @param listener
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void registerNodeListener(String path,ZkNodeListener listener);
    
    /**
     * 
     * 功能描述: 注册连接监听器
     * 〈功能详细描述〉
     *
     * @param listener
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void registerConnectionListener(ZKConnectionListener listener);
    
    /**
     * 
     * 功能描述: 连接是否有效
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    boolean isConnected();
    
    /**
     * 
     * 功能描述: 设置zkclient的连接状态
     * 〈功能详细描述〉
     *
     * @param isConnected
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void setConnected(boolean isConnected);
    
    /**
     * 
     * 功能描述: 关闭zkclient等操作
     * 〈功能详细描述〉
     *
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void shutdown();
    
    /**
     * 
     * 功能描述: 当连接断掉的时候需要移除所有保持在内存中的连接数据，此时是需要重建的
     * 〈功能详细描述〉
     *
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void removeAllWhenConnectionLost();
    
    /**
     * 
     * 功能描述: 获取所有的连接监听器
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    List<ZKConnectionListener> getAllZKConnectionListeners();
    
    /**
     * 
     * 功能描述: 获取操作的实际客户端
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    CuratorFramework getCuratorFramework();
}
