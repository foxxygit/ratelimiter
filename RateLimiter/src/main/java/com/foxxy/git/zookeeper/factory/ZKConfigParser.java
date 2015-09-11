package com.foxxy.git.zookeeper.factory;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 解析zk-cluster.xml配置文件<br>
 * 〈功能详细描述〉
 *
 * @author 15050977 xy
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ZKConfigParser {
    private final static Logger log = LoggerFactory.getLogger(ZKConfigParser.class);

    public synchronized static ZkConfig parser() throws Exception {
        ZkConfig zkConfig = new ZkConfig();
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(ZKConfigParser.class.getResourceAsStream("/conf/zk-cluster.xml"));
            Element root = document.getDocumentElement();
            // 名字
            zkConfig.setName(root.getElementsByTagName("name").item(0).getFirstChild().getNodeValue().trim());
            zkConfig.setConnectionTimeoutMs(Integer.valueOf(root.getElementsByTagName("connectionTimeoutMs").item(0)
                    .getFirstChild().getNodeValue().trim()));
            zkConfig.setRetrySleepTimeMs(Integer.valueOf(root.getElementsByTagName("retrySleepTimeMs").item(0)
                    .getFirstChild().getNodeValue().trim()));
            zkConfig.setRetryTimes(Integer.valueOf(root.getElementsByTagName("retryTimes").item(0).getFirstChild()
                    .getNodeValue().trim()));
            zkConfig.setSessionTimeoutMs(Integer.valueOf(root.getElementsByTagName("sessionTimeoutMs").item(0)
                    .getFirstChild().getNodeValue().trim()));
            zkConfig.setUrl(root.getElementsByTagName("url").item(0).getFirstChild().getNodeValue().trim());
        } catch (Exception e) {
            log.error("parser file zk-cluster.xml failed!!!", e);
            throw e;
        }
        return zkConfig;
    }
}
