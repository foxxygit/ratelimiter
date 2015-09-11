package com.foxxy.git.zookeeper;

/**
 * 创建节点类型的枚举类<br> 
 * 〈功能详细描述〉
 *
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public enum CreateMode {
    PERSISTENT(0), PERSISTENT_SEQUENTIAL(1), EPHEMERAL(2), EPHEMERAL_SEQUENTIAL(3);

    private final int value;

    private CreateMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static org.apache.zookeeper.CreateMode findByValue(int value) {
        switch (value) {
            case 0:
                return org.apache.zookeeper.CreateMode.PERSISTENT;
            case 1:
                return org.apache.zookeeper.CreateMode.PERSISTENT_SEQUENTIAL;
            case 2:
                return org.apache.zookeeper.CreateMode.EPHEMERAL;
            case 3:
                return org.apache.zookeeper.CreateMode.EPHEMERAL_SEQUENTIAL;
            default:
                return null;
        }
    }
}
