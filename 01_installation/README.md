# 安装 Kafka

## 下载

https://downloads.apache.org/kafka/

kafka 包名由 scala 版本号和 kafka 版本号构成，格式为： `kafka_{scala_version}-{kafka_version}.tgz`。

因为 kafka 发布版本包含了 ZooKeeper，所以不需要再去下载 ZooKeeper 了。不过可以了解一下 [ZooKeeper 的安装](./Zookeeper-Installation.md)，便于理解后面的一些配置。

```
tar -xzf kafka_2.13-2.6.0.tgz
mv kafka_2.13-2.6.0 /usr/local/kafka
```

上面步骤也可以直接通过 [Dockerfile](./Dockerfile) 构建镜像，然后基于此镜像启动一个 Docker 容器。
```
docker image build -t kafka:standalone .
docker container run -it --name kafka_standalone kafka:standalone /bin/bash
```

## Kafka 单机

单机版 Kafka 直接使用默认配置即可。

启动 zookeeper
```
/usr/local/kafka/bin# ./zookeeper-server-start.sh ../config/zookeeper.properties
```

测试 zookeeper 是否启动成功。`telnet {ip}:{port}`，`{ip}` 为 zookeeper 服务器的地址，{port} 是 zookeeper 提供客户端服务的端口（默认：2181）。
telnet 连接上去之后输入 `srvr`，观察输出信息。如下所示：

```
# telnet 172.20.0.5 2181
Trying 172.20.0.5...
Connected to 172.20.0.5.
Escape character is '^]'.
srvr
Zookeeper version: 3.5.8-f439ca583e70862c3068a1f2a7d4d068eec33315, built on 05/04/2020 15:53 GMT
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

其中 `Mode: standalone` 表示单机模式。

启动 Kafka （新开一个终端）
```
/usr/local/kafka/bin# ./kafka-server-start.sh ../config/server.properties
```

创建 Topic （新开一个终端）
```
./kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092
```

订阅 Topic
```
/usr/local/kafka/bin# ./kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092
```

生产者往 Topic 写数据
```
/usr/local/kafka/bin# ./kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092
```

消费者从 Topic 读数据 （新开一个终端）
```
/usr/local/kafka/bin# ./kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
```


## Kafka 集群

准备 3 台机器，或启动 3 个下载有 ZooKeeper 的 Docker 容器（上面有构建容器镜像的 Dockerfile）。 Kafka 的根目录为 $KAFKA_HOME，结构如下：

```
# ls $KAFKA_HOME
LICENSE  NOTICE  bin  config  libs  logs  site-docs
```

### ZooKeeper 配置与启动

Kafka 使用 ZooKeeper 作为元数据（集群，broker，主题，分区等内容）信息管理的组件。
Kafka 是 ZooKeeper 的客户端，ZooKeeper 为 Kafka 提供服务。
因此，在配置 Kafka 之前，应该先配置与启动 ZooKeeper 服务。参考[ZooKeeper 的安装](./Zookeeper-Installation.md)。

与单独下载的 ZooKeeper 包不同，Kafka 自带的 ZooKeeper 的配置文件为 `$KAFKA_HOME/config/zookeeper.properties`，
启动脚本为 `$KAFKA_HOME/bin/zookeeper-server-start.sh`。

启动命令：
```
# $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
```

### Kafka Broker 配置

所谓 Broker 配置即集群中某台机器节点的配置，是启动 Kafka 集群的必要操作。 
Kafka Broker 配置可以由包内默认配置修改而来，可以看见默认配置的内容如下所示。

```
# cat $KAFKA_HOME/config/server.properties | grep ^[^#]
broker.id=0
num.network.threads=3
num.io.threads=8
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600
log.dirs=/tmp/kafka-logs
num.partitions=1
num.recovery.threads.per.data.dir=1
offsets.topic.replication.factor=1
transaction.state.log.replication.factor=1
transaction.state.log.min.isr=1
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
zookeeper.connect=localhost:2181
zookeeper.connection.timeout.ms=18000
group.initial.rebalance.delay.ms=0
```

需要修改的参数是 `broker.id` 和 `zookeeper.connect`。

+ `broker.id` 用于标识 Kafka 集群中机器节点的 ID，它在整个集群中必须唯一。
+ `zookeeper.connect` 用于指明提供服务的 ZooKeeper 连接字符串。格式为： `hostname1:port1,hostname2:port2,hostname3:port3`。
同时，由于 ZooKeeper 集群一般不仅仅为当前的 Kafka 集群提供服务，它还又可能为其它应用提供服务，所以这里最好带上 chroot path, 格式为： `hostname1:port1,hostname2:port2,hostname3:port3/chroot/path`。

[更多 Broker 配置的说明](https://kafka.apache.org/documentation/#brokerconfigs)。

### 启动

Kafka 包提供了启动脚本 `$KAFKA_HOME/bin/kafka-server-start.sh`，启动命令为：
```
# $KAFKA_HOME/bin/kafka-server-start.sh -daemon $KAFKA_HOME/config/server.properties
```

可以通过查看日志来查看启动状态 `$KAFKA_HOME/logs/kafkaServer.out`。
```
# tail -n 20 $KAFKA_HOME/logs/kafkaServer.out
```

### 验证

启动成功之后，可以通过 Kafka 包提供的命令行客户端工具创建话题，生产消息，消费消息。

创建话题
```
# $KAFKA_HOME/bin/kafka-topics.sh --create --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1 --topic test
```

查看话题
```
# $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server kafka:9092 --list
```

生产消息
```
# $KAFKA_HOME/bin/kafka-console-producer.sh --broker-list kafka:9092 --topic test
>Hello
>Kafka
>
```

消费消息
```
# $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic test --from-beginning
Hello
Kafka
```


## 参考

[APACHE KAFKA QUICKSTART](https://kafka.apache.org/quickstart)
[Kafka Documentation](https://kafka.apache.org/documentation)

