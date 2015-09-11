package com.foxxy.git.listener;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.curator.framework.state.ConnectionState;

import com.foxxy.git.Limiter;
import com.foxxy.git.factory.RateLimiterFactory;
import com.foxxy.git.zookeeper.ZkNodeData;
import com.foxxy.git.zookeeper.client.ZKClusterClient;
import com.foxxy.git.zookeeper.listener.ZKConnectionListener;
import com.foxxy.git.zookeeper.listener.ZkNodeListener;

/**
 * 监听速率控制节点的数据改变，当数据改变时将变化推到客户端
 */
public class LimiterRateMonitorListener implements ZkNodeListener, ZKConnectionListener {

    private String path;

    public LimiterRateMonitorListener(String path) {
        this.path = path;
    }

    @Override
    public void stateChanged(ZKClusterClient client, ConnectionState newState) {
        // 连接重建时，需要重新注册对节点的监听
        client.registerNodeListener(path, this);
    }

    @Override
    public void childUpdated(ZkNodeData nodeData) {
        invokeChangeData(nodeData);
    }

    @Override
    public void childAdded(ZkNodeData nodeData) {
        // donothing
    }

    @Override
    public void childDeleted(ZkNodeData nodeData) {
        // donothing
    }

    @Override
    public void nodeUpdated(ZkNodeData nodeData) {
        invokeChangeData(nodeData);
    }

    @Override
    public boolean accept(ZkNodeData nodeData) {
        return path.equals(nodeData.getPath());
    }

    private void invokeChangeData(ZkNodeData nodeData) {
        String path = nodeData.getPath();
        String value = nodeData.getValue();
        String key = path.substring(0, path.lastIndexOf("/"));
        if (!NumberUtils.isNumber(value)) {
            throw new IllegalArgumentException("nodePath:" + path + " value is Illegal:" + value);
        }
        Limiter limiter = RateLimiterFactory.getInstance().getRateLimiter(key);
        // 设置速率
        limiter.setRate(NumberUtils.toInt(value));
    }

}
