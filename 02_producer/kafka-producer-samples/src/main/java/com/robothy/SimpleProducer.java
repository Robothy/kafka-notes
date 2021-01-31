package com.robothy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SimpleProducer {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(SimpleProducer.class);
        logger.error("Error");
        logger.info("Info");
        logger.debug("Debug");
        logger.trace("Trace");

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 1);

        KafkaProducer<String, String> stringProducer = new KafkaProducer<>(properties);



        // 主题为 test，键为 name，值为 Robothy
        ProducerRecord<String, String> record = new ProducerRecord<>("quickstart-events",  "Robothy");

        System.out.println(stringProducer.send(record).get());
    }

}
