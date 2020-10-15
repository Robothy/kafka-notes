# ZooKeeper 安装

## 下载

地址：http://archive.apache.org/dist/zookeeper

```
# tar -xzf apache-zookeeper-3.5.8.tar.gz
# mv apache-zookeeper-3.5.8-bin /opt/zookeeper
# export $ZK_HOME /opt/zookeeper
```

## 单机模式

### 配置 zoo.cfg

单机模式可以直接使用默认配置，只需要将 `$ZK_HOME/conf/zoo_sample.cfg` 重命名为 `$ZK_HOME/conf/zoo_sample.cfg` 即可。
```
# mv $ZK_HOME/conf/zoo_sample.cfg $ZK_HOME/conf/zoo.cfg
```

配置内容为：
```
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/tmp/zookeeper
clientPort=2181
```

参数说明：

|---|---|
|tickTime| 表示 ZooKeeper 中最小时间单元长度，很多运行时间间隔都是使用整数倍的 tickTime 来表示。单位为毫秒，默认值为 3000 |
|initLimit| 表示 Leader 服务器等待 Follower 服务器启动并完成数据同步的时间。 Follower 服务器启动过程中会与 Leader 建立连接并完成数据同步，从而确定对外提供服务的初始状态。默认值为 10，表示 tickTime 的 10 倍。 |
|syncLimit| 表示 Leader 服务器与 Follower 服务器之间进行心跳检测的最大延时时间。若 Leader 在 syncLimit 时间内无法获得 Follower 的心跳检测响应， Leader 会认为 Follower 脱离了和自己的同步。默认值为 5, 表示 tickTime 的 10 倍。 |
|dataDir| 表示 ZooKeeper 服务器存储快照文件的目录。若没有配置 dataLogDir，那么事务日志也会存储在这个目录中。 |
|clientPort| 表示当前服务器对外提供服务的端口，客户端通过该端口与 ZooKeeper 服务器创建连接。值一般设置为 2181。 ZooKeeper 集群中，不同机器的 `clientPort` 可以不一样。 |

### 启动

可以通过 bin 目录下的脚本启动 ZooKeeper。

```
# $ZK_HOME/bin/zkServer.sh start
ZooKeeper JMX enabled by default
Using config: /opt/zookeeper/bin/../conf/zoo.cfg
Starting zookeeper ... STARTED
```

### 验证

可以通过 telnet 连接 ZooKeeper 客户端端口，然后输入 `srvr` 来验证 ZooKeeper 启动是否成功。

```bash
# telnet 172.21.0.2 2181
Trying 172.21.0.2...
Connected to 172.21.0.2.
Escape character is '^]'.
srvr
Zookeeper version: 3.5.8-f439ca583e70862c3068a1f2a7d4d068eec33315, built on 05/04/2020 15:07 GMT
Latency min/avg/max: 0/0/0
Received: 1
Sent: 0
Connections: 1
Outstanding: 0
Zxid: 0x0
Mode: standalone
Node count: 5
Connection closed by foreign host.
```

可以看到 `Mode: standalone` 表示单机模式。

## 集群模式

在每一台机器上下载并解压 ZooKeeper 包。

### 配置 zoo.cfg

集群模式同样可以复用 zoo_sample.cfg, 不过需要添加集群中机器的信息。 配置内容最终如下所示(集群中有3台机器)：

```
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/tmp/zookeeper
clientPort=2181
server.1=172.21.0.2:6666:8888
server.2=172.21.0.3:6666:8888
server.3=172.21.0.4:6666:8888
```

server.{id}={host}:{port1}:{port2}
其中 {id} 为整数（范围：1~255），表示机器的 ID。 `{port1}` 表示 Follower 与 Leader 进行运行时通信和数据同步时所用端口， `{port2}` 用于 Leader 选举过程中的投票通信。

### 创建 myid 文件

在每台机器配置文件 zoo.cfg 指定的 dataDir 目录下创建一个名为 myid 的文件，并写入一个数字，数字应该与 zoo.cfg 文件中 server.{id} 的 id 一致。
myid 中的内容为 ZooKeeper 集群中节点的标识。

### 启动

在每一台机器上执行如下命令：
```
# $ZK_HOME/bin/zkServer.sh start

```

#### 验证

与单机模式一样，可以通过 telnet 连接 clientPort 来验证启动是否成功。

```
# telnet 172.21.0.2 2181
Trying 172.21.0.2...
Connected to 172.21.0.2.
Escape character is '^]'.
srvr
Zookeeper version: 3.5.8-f439ca583e70862c3068a1f2a7d4d068eec33315, built on 05/04/2020 15:07 GMT
Latency min/avg/max: 0/0/0
Received: 1
Sent: 0
Connections: 1
Outstanding: 0
Zxid: 0x0
Mode: follower
Node count: 5
Connection closed by foreign host.
```

`Mode: follower` 表示当前机器在集群中是 Follower。

## 使用命令行客户端对 ZooKeer 进行操作

无论是单机模式还是集群模式，ZooKeeper 客户端对服务端进行操作都是一样的。 

ZooKeeper 安装包里提供了命令行客户端工具 $ZK_HOME/bin/zkCli.sh。

使用工具连接上服务端，格式： `$ZK_HOME/bin/zkCli.sh -server ip:port`，若服务端和客户端在同一机器，且 clientPort 为 2181 则可以省略 `-server ip:port`。进入命令行客户端工具的上下文。

```
$ZK_HOME/bin/zkCli.sh -server 172.21.0.2:2181
```

输入任意不存在的命令（比如：haha）可以查看命令说明。
```
[zk: 172.21.0.2:2181(CONNECTED) 3] haha
ZooKeeper -server host:port cmd args
        addauth scheme auth
        close
        config [-c] [-w] [-s]
        connect host:port
        create [-s] [-e] [-c] [-t ttl] path [data] [acl]
        delete [-v version] path
        deleteall path
        delquota [-n|-b] path
        get [-s] [-w] path
        getAcl [-s] path
        history
        listquota path
        ls [-s] [-w] [-R] path
        ls2 path [watch]
        printwatches on|off
        quit
        reconfig [-s] [-v version] [[-file path] | [-members serverID=host:port1:port2;port3[,...]*]] | [-add serverId=host:port1:port2;port3[,...]]* [-remove serverId[,...]*]
        redo cmdno
        removewatches path [-c|-d|-a] [-l]
        rmr path
        set [-s] [-v version] path data
        setAcl [-s] [-v version] [-R] path acl
        setquota -n|-b val path
        stat [-w] path
        sync path
Command not found: Command not found haha
```

创建数据节点

```
[zk: 172.21.0.2:2181(CONNECTED) 4] create /zk-test 666
Created /zk-test
```

查看数据节点
```
[zk: 172.21.0.2:2181(CONNECTED) 5] ls /
[zk-test, zookeeper]
```

获取节点数据
```
[zk: 172.21.0.2:2181(CONNECTED) 6] get /zk-test
666
```

设置节点数据
```
[zk: 172.21.0.2:2181(CONNECTED) 7] set /zk-test 777
[zk: 172.21.0.2:2181(CONNECTED) 8] get /zk-test
777
```

删除节点
```
[zk: 172.21.0.2:2181(CONNECTED) 10] delete /zk-test
[zk: 172.21.0.2:2181(CONNECTED) 11] ls /
[zookeeper]
```

## 参考

倪超.从 Paxos 到 ZooKeeper 分布式一致性原理与实践
