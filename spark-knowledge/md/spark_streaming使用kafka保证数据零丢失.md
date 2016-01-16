#Spark Streaming使用Kafka保证数据零丢失

spark streaming从1.2开始提供了数据的零丢失，想享受这个特性，需要满足如下条件：

1.数据输入需要可靠的sources和可靠的receivers

2.应用metadata必须通过应用driver checkpoint

3.WAL（write ahead log）

##可靠的sources和receivers

spark streaming可以通过多种方式作为数据sources（包括kafka），输入数据通过receivers接收，通过replication存储于spark中（为了faultolerance，默认复制到两个spark executors），如果数据复制完成，receivers可以知道（例如kafka中更新offsets到zookeeper中）。这样当receivers在接收数据过程中crash掉，不会有数据丢失，receivers没有复制的数据，当receiver恢复后重新接收。

![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/spark-streaming-kafka/spark-reliable-source-reliable-receiver.png)

##metadata checkpoint

![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/spark-streaming-kafka/spark-metadata-checkpointing.png)


![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/spark-streaming-kafka/spark-wal.png)

![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/spark-streaming-kafka/spark-wall-at-least-once-delivery.png)

![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/spark-streaming-kafka/spark-kafka-direct-api.png)