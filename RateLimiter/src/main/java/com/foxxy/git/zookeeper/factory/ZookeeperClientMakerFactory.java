package com.foxxy.git.zookeeper.factory;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxxy.git.zookeeper.client.DefaultZKClusterClient;
import com.foxxy.git.zookeeper.client.ZKClusterClient;
import com.foxxy.git.zookeeper.exception.ZKConnectException;
import com.google.common.collect.Maps;



/**
 ━━━━━━神兽出没━━━━━━
 *　　┏┓　　　┏┓
 *　┏┛┻━━━┛┻┓
 *　┃　　　　　　　┃
 *　┃　　　━　　　┃
 *　┃　┳┛　┗┳　┃
 *　┃　　　　　　　┃
 *　┃　　　┻　　　┃
 *　┃　　　　　　　┃
 *　┗━┓　　　┏━┛
 *　　　┃　　　┃神兽保佑, 永无BUG!
 *　　　┃　　　┃Code is far away from bug with the animal protecting
 *　　　┃　　　┗━━━┓
 *　　　┃　　　　　　　┣┓
 *　　　┃　　　　　　　┏┛
 *　　　┗┓┓┏━┳┓┏┛
 *　　　　　┃┫┫　┃┫┫
 *　　　　　┗┻┛　┗┻┛
 * ━━━━━━感觉萌萌哒━━━━━━
 * zkclient制造梦工场〉<br> 
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ZookeeperClientMakerFactory {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ConcurrentMap<String, ZKClusterClient> clientFactory = Maps.newConcurrentMap();

    private ConcurrentMap<CuratorFramework, ZKClusterClient> cur_clientFactory = Maps.newConcurrentMap();

    private AtomicBoolean inited = new AtomicBoolean(false);

    private static ZookeeperClientMakerFactory _factory = new ZookeeperClientMakerFactory();

    /**
     * default initial amount of time to wait between retries
     */
    private static final Integer RETRY_SLEEPTIMEMS = 1000;

    private static final Integer RETRY_TIMES = 3;

    private ZookeeperClientMakerFactory() {

    }

    public synchronized static ZookeeperClientMakerFactory getInstance() {
        return _factory;
    }

    public ZKClusterClient getZKClusterClient(String name) {
        ZKClusterClient client = clientFactory.get(name);
        if (null == client) {
            throw new IllegalArgumentException("name may be wrong name is：" + name);
        }
        if (!client.isConnected()) {
            throw new ZKConnectException("with zk server connection loss,please check!!!!");
        }
        return client;
    }

    public void init(ZkConfig congfig) {
        if (inited.compareAndSet(false, true)) {
            // 校验配置是否合法
            validate(congfig);
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(
                    null == congfig.getRetrySleepTimeMs() ? RETRY_SLEEPTIMEMS : congfig.getRetrySleepTimeMs(),
                    null == congfig.getRetryTimes() ? RETRY_TIMES : congfig.getRetryTimes());
            ZKClusterClient client = createZkClient(congfig.getUrl(), retryPolicy, congfig.getConnectionTimeoutMs(),
                    congfig.getSessionTimeoutMs());
            clientFactory.put(congfig.getName(), client);
            cur_clientFactory.put(client.getCuratorFramework(), client);
        }
    }

    private ZKClusterClient createZkClient(String connectionString, RetryPolicy retryPolicy, int connectionTimeoutMs,
            int sessionTimeoutMs) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(connectionString)
                .retryPolicy(retryPolicy).connectionTimeoutMs(connectionTimeoutMs).sessionTimeoutMs(sessionTimeoutMs)
                .build();
        final CountDownLatch downLactch = new CountDownLatch(1);
        curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener() {

            @Override
			public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.CONNECTED || newState == ConnectionState.RECONNECTED) {
                    downLactch.countDown();
                    log.info("with zk server connection is ok!!!!");
                }
            }
        });
        curatorFramework.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
            @Override
			public void unhandledError(String message, Throwable e) {
                log.info("CuratorFramework unhandledError: {}", message);
            }
        });
        curatorFramework.start();
        try {
            downLactch.await(connectionTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("connection the url:{} failed at connectionTimeoutMs:{}", connectionString, connectionTimeoutMs);
            String message = MessageFormat.format("connection the url:{0} failed at connectionTimeoutMs:{1}",
                    connectionString, connectionTimeoutMs);
            throw new ZKConnectException(message, e);
        }
        ZKClusterClient client = new DefaultZKClusterClient(curatorFramework);
        client.setConnected(true);
        return client;
    }

    /**
     * 功能描述: 自定义创建zkclient 〈功能详细描述〉
     *
     * @param connectionString 连接url如127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
     * @param connectionTimeoutMs 连接超时时间
     * @param sessionTimeoutMs session超时时间
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public ZKClusterClient createZkClient(String connectionString, int connectionTimeoutMs, int sessionTimeoutMs) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(RETRY_SLEEPTIMEMS, RETRY_TIMES);
        return this.createZkClient(connectionString, retryPolicy, connectionTimeoutMs, sessionTimeoutMs);
    }

    /**
     * 功能描述: 校验配置是否合法 〈功能详细描述〉
     *
     * @param congfig
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    private void validate(ZkConfig congfig) {
        if (null == congfig) {
            throw new IllegalArgumentException("congfig can't be null");
        }
        if (StringUtils.isBlank(congfig.getUrl()) || StringUtils.isBlank(congfig.getName())) {
            throw new IllegalArgumentException("name or url can't be null");
        }
        if (null == congfig.getConnectionTimeoutMs() || null == congfig.getSessionTimeoutMs()) {
            throw new IllegalArgumentException("connectionTimeoutMs or sessionTimeoutMs can't be null");
        }
    }

    public ZKClusterClient getZKClusterClient(CuratorFramework client) {
        return cur_clientFactory.get(client);
    }

}
