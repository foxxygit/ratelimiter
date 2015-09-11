package com.foxxy.git.zookeeper;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxxy.git.zookeeper.factory.ZKConfigParser;
import com.foxxy.git.zookeeper.factory.ZkConfig;
import com.foxxy.git.zookeeper.factory.ZookeeperClientMakerFactory;

public class ZKBootStartup {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static AtomicBoolean inited = new AtomicBoolean(false);

    public synchronized void start() {
        if (inited.compareAndSet(false, true)) {
            // 1.解析配置文件
            ZkConfig zkConfig = null;
            try {
                zkConfig = ZKConfigParser.parser();
            } catch (Exception e) {
                log.error("parser file zk-cluster.xml failed!!!", e);
                return;
            }
            String zkName = zkConfig.getName();
            // 2.创建zkClient
            ZookeeperClientMakerFactory.getInstance().init(zkConfig);
            // 3.悬吊进程钩子,优雅停机
            addShutdownHook(zkName);
        }

    }

    private void addShutdownHook(final String name) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    ZookeeperClientMakerFactory.getInstance().getZKClusterClient(name).shutdown();
                } catch (Exception e) {
                    // donothing
                }
            }
        });
    }

}
