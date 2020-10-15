FROM java:8-jre

ARG scala_version=2.13
ARG kafka_version=2.6.0
ARG kafka_home=/opt/kafka

ENV KAFKA_HOME=${kafka_home} \
	SCALA_VERSION=${scala_version} \ 
	KAFKA_VERSION=${kafka_version}

RUN wget https://downloads.apache.org/kafka/${kafka_version}/kafka_${scala_version}-${kafka_version}.tgz \
 && tar -xzf kafka_${scala_version}-${kafka_version}.tgz \
 && mv kafka_${scala_version}-${kafka_version} ${kafka_home} \
 && rm kafka_${scala_version}-${kafka_version}.tgz