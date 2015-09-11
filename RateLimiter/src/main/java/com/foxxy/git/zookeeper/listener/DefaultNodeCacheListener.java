package com.foxxy.git.zookeeper.listener;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxxy.git.zookeeper.ZkNodeData;
import com.foxxy.git.zookeeper.util.ZkPathUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 对节点和子节点监听的处理器<br>
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DefaultNodeCacheListener implements NodeCacheListener, PathChildrenCacheListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, Set<ZkNodeListener>> zkNodeListenerMap = Maps.newConcurrentMap();

    private CuratorFramework curator;

    private NodeCache nodeCache;

    private Charset charset = Charset.forName("UTF-8");

    @SuppressWarnings("resource")
    public DefaultNodeCacheListener(String path, CuratorFramework curator) {
        this.curator = curator;
        nodeCache = new NodeCache(curator, path);
        PathChildrenCache pathNode = new PathChildrenCache(curator, path, true);
        try {
            nodeCache.start();
            pathNode.start(StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            log.error("node start failed", e);
        }
        nodeCache.getListenable().addListener(this);
        pathNode.getListenable().addListener(this);
    }

    @Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        byte[] data = event.getData().getData();
        String path = event.getData().getPath();
        ZkNodeData nodeData = new ZkNodeData(path, new String(data, charset), null);
        Set<ZkNodeListener> zkNodeListeners = zkNodeListenerMap.get(path);
        // 获取对父节点的监听
        Set<ZkNodeListener> parentZKNodeListeners = zkNodeListenerMap.get(ZkPathUtils.getParentPath(path));
        Set<ZkNodeListener> all_listeners = new HashSet<ZkNodeListener>();
        if (CollectionUtils.isNotEmpty(zkNodeListeners)) {
            all_listeners.addAll(zkNodeListeners);
        }
        if (CollectionUtils.isNotEmpty(parentZKNodeListeners)) {
            all_listeners.addAll(parentZKNodeListeners);
        }
        if (CollectionUtils.isEmpty(all_listeners)) {
            log.info("no listeners for this path:{}", path);
            return;
        }
        switch (event.getType()) {
            case CHILD_ADDED: {
                for (ZkNodeListener zkNodeListener : all_listeners) {
                    if (zkNodeListener.accept(nodeData)) {
                        zkNodeListener.childAdded(nodeData);
                    }
                }
                break;
            }
            case CHILD_UPDATED: {
                for (ZkNodeListener zkNodeListener : all_listeners) {
                    if (zkNodeListener.accept(nodeData)) {
                        zkNodeListener.childUpdated(nodeData);
                    }
                }
                break;
            }
            case CHILD_REMOVED: {
                for (ZkNodeListener zkNodeListener : all_listeners) {
                    if (zkNodeListener.accept(nodeData)) {
                        zkNodeListener.childDeleted(nodeData);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
	public void nodeChanged() throws Exception {
        byte[] data = nodeCache.getCurrentData().getData();
        String path = nodeCache.getCurrentData().getPath();
        Set<ZkNodeListener> zkNodeListeners = zkNodeListenerMap.get(path);
        ZkNodeData nodeData = new ZkNodeData(path, new String(data, charset), null);
        if (CollectionUtils.isNotEmpty(zkNodeListeners)) {
            for (ZkNodeListener zkNodeListener : zkNodeListeners) {
                if (zkNodeListener.accept(nodeData)) {
                    zkNodeListener.nodeUpdated(nodeData);
                }
            }
        }
    }

    public void registerNodeListener(String path, ZkNodeListener listener) {
        Set<ZkNodeListener> zkNodeListeners = zkNodeListenerMap.get(path);
        if (CollectionUtils.isEmpty(zkNodeListeners)) {
            zkNodeListeners = Sets.newHashSet(listener);
            zkNodeListenerMap.put(path, zkNodeListeners);
        } else {
            zkNodeListeners.add(listener);
        }
    }

    public CuratorFramework getCurator() {
        return curator;
    }
}
