package com.foxxy.git.zookeeper.client;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxxy.git.zookeeper.CreateMode;
import com.foxxy.git.zookeeper.Node;
import com.foxxy.git.zookeeper.exception.ZKConnectException;
import com.foxxy.git.zookeeper.exception.ZKOperateException;
import com.foxxy.git.zookeeper.listener.DefaultNodeCacheListener;
import com.foxxy.git.zookeeper.listener.ZKConnectionListener;
import com.foxxy.git.zookeeper.listener.ZkNodeListener;
import com.foxxy.git.zookeeper.util.ZkPathUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 默认zk集群的操作client<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DefaultZKClusterClient implements ZKClusterClient {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private CuratorFramework client;

    private Map<String, DefaultNodeCacheListener> watchMap = Maps.newConcurrentMap();

    private List<ZKConnectionListener> zkConnectionListeners = new CopyOnWriteArrayList<ZKConnectionListener>();

    private volatile boolean isConnected = false;

    private Charset charset = Charset.forName("UTF-8");

    public DefaultZKClusterClient(CuratorFramework client) {
        this.client = client;
    }

    /*
     * (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#createNode(java.lang.String, java.lang.String,
     * com.foxxy.git.zookeeper.CreateMode)
     */
    @Override
	public void createNode(String path, String value, CreateMode mode) {
        Preconditions.checkNotNull(path, "path can't be null");
        Preconditions.checkNotNull(value, "value can't be null");
        checkConnection();
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.findByValue(mode.getValue()))
                    .forPath(path, value.getBytes(charset));
        } catch (Exception e) {
            log.error("create path failed:{},value:{}", path, value);
            throw new ZKOperateException("create path failed:" + path, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#getNodeValue(java.lang.String)
     */
    @Override
	public String getNodeValue(String path) {
        Preconditions.checkNotNull(path, "path can't be null");
        checkConnection();
        try {
            byte[] bytes = client.getData().forPath(path);
            return new String(bytes, charset);
        } catch (Exception e) {
            log.error("getNodeValue path failed:{}", path);
            throw new ZKOperateException("getNodeValue path failed:" + path, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#getChildNodes(java.lang.String)
     */
    @Override
	public List<Node> getChildNodes(String path) {
        Preconditions.checkNotNull(path, "path can't be null");
        checkConnection();
        try {
            List<String> paths = client.getChildren().forPath(path);
            if (CollectionUtils.isEmpty(paths)) {
                return Lists.newArrayList();
            }
            List<Node> nodes = new ArrayList<Node>(paths.size());
            for (String vpath : paths) {
                byte[] bytes = client.getData().forPath(path);
                Node node = new Node(vpath, new String(bytes, charset), 0);
                nodes.add(node);
            }
            return nodes;
        } catch (Exception e) {
            log.error("getChildNodes path failed:{}", path);
            throw new ZKOperateException("getChildNodes path failed:" + path, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#deleteNode(java.lang.String)
     */
    @Override
	public void deleteNode(String path) {
        Preconditions.checkNotNull(path, "path can't be null");
        checkConnection();
        try {
            client.delete().deletingChildrenIfNeeded().inBackground().forPath(path);
        } catch (Exception e) {
            log.error("deleteNode path failed:{}", path);
            throw new ZKOperateException("deleteNode path failed:" + path, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#setNodeValue(java.lang.String, java.lang.String)
     */
    @Override
	public void setNodeValue(String path, String value) {
        Preconditions.checkNotNull(path, "path can't be null");
        Preconditions.checkNotNull(value, "value can't be null");
        checkConnection();
        try {
            client.setData().forPath(path, value.getBytes(charset));
        } catch (Exception e) {
            log.error("setNodeValue path failed:{},value:{}", path, value);
            throw new ZKOperateException("setNodeValue path failed:" + path, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#registerNodeListener(java.lang.String,
     * com.foxxy.git.zookeeper.listener.ZkNodeListener)
     */
    @Override
	public void registerNodeListener(String path, ZkNodeListener listener) {
        Preconditions.checkNotNull(path, "path can't be null");
        Preconditions.checkNotNull(listener, "listener can't be null");
        checkConnection();
        log.info("registerNodeListener for path:{},listener:{}", path, listener.getClass().getSimpleName());
        DefaultNodeCacheListener nodeCacheListener = watchMap.get(path);
        if (null == nodeCacheListener) {
            nodeCacheListener = new DefaultNodeCacheListener(path, this.getCuratorFramework());
            watchMap.put(path, nodeCacheListener);
        }
        nodeCacheListener.registerNodeListener(path, listener);
        //注册对父节点的监听，因为删除时触发的父亲节点的事件
        String parentPath=ZkPathUtils.getParentPath(path);
        nodeCacheListener.registerNodeListener(parentPath, listener);
    }

    /* (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#isConnected()
     */
    @Override
	public boolean isConnected() {
        return isConnected;
    }

    /* (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#setConnected(boolean)
     */
    @Override
	public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /* (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#getCuratorFramework()
     */
    @Override
	public CuratorFramework getCuratorFramework() {
        return client;
    }

    /* (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#shutdown()
     */
    @Override
	public void shutdown() {
        isConnected = false;
        if (null != client) {
            client.close();
        }
        this.watchMap.clear();
        zkConnectionListeners.clear();
    }

    private void checkConnection() {
        if (!isConnected()) {
            log.error("with zk server connection loss,please check");
            throw new ZKConnectException("with zk server connection loss,please check!!!!");
        }
    }

    /* (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#removeAllWhenConnectionLost()
     */
    @Override
    public void removeAllWhenConnectionLost() {
        this.watchMap.clear();
        isConnected = false;
    }

    /*
     * (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#getAllZKConnectionListeners()
     */
    @Override
    public List<ZKConnectionListener> getAllZKConnectionListeners() {
        return zkConnectionListeners;
    }

    /*
     * (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#registerConnectionListener(ZkNodeListener.ZkConnectionListener)
     */
    @Override
    public void registerConnectionListener(ZKConnectionListener listener) {
        Preconditions.checkNotNull(listener, "listener can't be null");
        zkConnectionListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * @see com.foxxy.git.zookeeper.client.ZKClusterClient#isExsit(java.lang.String)
     */
    @Override
    public boolean isExsit(String path) {
        Preconditions.checkNotNull(path, "path can't be null");
        checkConnection();
        Stat stat = null;
        try {
            stat = client.checkExists().forPath(path);
        } catch (Exception e) {
            log.error("isExsit path failed:{},value:{}", path);
            throw new ZKOperateException("isExsit path failed:" + path, e);
        }
        return null != stat;
    }
}
