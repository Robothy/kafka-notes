# Kafka 生产者 Java 示例

Kafka 有各种语言版本的客户端，要使用 Java 版本的客户端需要引入如下依赖。

```groovy
implementation 'com.linkedin.kafka:kafka-clients:2.0.0.12'
```

## 创建生产者

创建生产者实例时至少需要指定 3 个参数。

+ `bootstrap.servers` 指定 Kafka 集群中 broker 的地址清单。可以指定多个，但不需要指定所有，因为 Producer 会根据给定的 broker 地址查找其它 broker 的信息。
  
+ `key.serializer` 指定消息键的序列化器。

+ `value.serializer` 指定消息值的序列化器。
  
一条消息是有键和值组成，且 Kafka 传输的数据格式是字节数组，所以在传输之前需要将消息的键和值转化为字节数组。
而序列化器的作用就是将 Java 对象转化为字节数组。序列化器需要实现接口 `org.apache.kafka.common.serialization.Serializer`，
 Kafka Java 客户端提供了很多常用的序列化器，通常情况下不需要自己写。

```java
Map<String, Object> properties = new HashMap<>();
properties.put("bootstrap.servers", "localhost:9092,localhost:9093");
properties.put("key.serializer", StringSerializer.class);
properties.put("value.serializer", StringSerializer.class);
KafkaProducer<String, String> stringProducer = new KafkaProducer<>(properties);
```

## 发送消息

对于生产者来说，一条消息就是一个 ProducerRecord 对象，创建一个对象至少需要指定 3 个参数：主题，键，值。

```java
// 主题为 test，键为 name，值为 Robothy 
ProducerRecord<String, String> record = new ProducerRecord<>("test", "name", "Robothy");
```

直接调用 KafkaProducer 的 send 方法即可发送消息。

```java

```



