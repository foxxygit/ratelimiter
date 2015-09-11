package com.foxxy.git.zookeeper;

import java.io.Serializable;
import java.util.List;

/**
 * 对zookeeper节点信息的包装<br> 
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Node implements Serializable{

    /**
     */
    private static final long serialVersionUID = 3577503174794446426L;
    
    /**
     * 节点路径
     */
    private String path;
    /**
     * 节点的值
     */
    private String value;
    
    /**
     * 节点版本
     */
    private Integer version;
    
    /**
     * 子节点列表
     */
    private List<Node> nodes;
    
    public Node(String path, String value, Integer version) {
        super();
        this.path = path;
        this.value = value;
        this.version = version;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
    
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Node [path=" + path + ", value=" + value + ", version=" + version + ", nodes=" + nodes + "]";
    }
}
