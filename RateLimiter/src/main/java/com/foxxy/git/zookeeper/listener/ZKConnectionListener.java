package com.foxxy.git.zookeeper.listener;

import org.apache.curator.framework.state.ConnectionState;

import com.foxxy.git.zookeeper.client.ZKClusterClient;

/**
 * 连接重建的监听器，在连接重建时，需要对节点的监听重新注册<br> 
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface ZKConnectionListener {
    /**
     * 
     * 功能描述: 连接重建的通知
     * 〈功能详细描述〉
     *
     * @param client
     * @param newState
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    void stateChanged(ZKClusterClient client, ConnectionState newState);
}
