#!/bin/bash

###################
#修改Kafka中数据的replica数
# Created by jacksu on 16/2/26.

if [ $# -ne 2 ]
then
  echo "exampl: $0 zookeeperURL TOPIC [replicaNum]"
  exit 1
fi

ZKURL=$1
TOPIC=$2

if [ $# -gt 2 ]
then
  REPNUM=$3
else
  REPNUM=2
fi

echo "replica num:$REPNUM"

export PATH=$PATH
KAFKAPATH="/opt/kafka"

PARTITIONS=$(${KAFKAPATH}/bin/kafka-topics.sh --zookeeper $ZKURL --topic $TOPIC --describe | grep PartitionCount | awk '{print $2}' | awk -F":" '{print $2}')


REPLICA=$(seq -s, 0 `expr $REPNUM - 1`)
PARTITIONS=$(expr $PARTITIONS - 2)
FILE=partition-to-move.json

##输出头
echo "{" > $FILE
echo  "\"partitions\":" >> $FILE
echo "[" >> $FILE

if [ $PARTITIONS -gt 0 ]
then
  for i in `seq 0 $PARTITIONS`
  do
    echo "{\"topic\": \"$TOPIC\", \"partition\": $i,\"replicas\": [$REPLICA]}," >> $FILE
  done
elif [ $PARTITIONS -eq 0 ]
then
  echo "{\"topic\": \"$TOPIC\", \"partition\": 0,\"replicas\": [$REPLICA]}," >> $FILE
fi
PARTITIONS=$(expr $PARTITIONS + 1)

##输出尾
echo "{\"topic\": \"$TOPIC\", \"partition\": $PARTITIONS,\"replicas\": [$REPLICA]}" >> $FILE
echo "]" >> $FILE
echo "}" >> $FILE


$KAFKAPATH/bin/kafka-reassign-partitions.sh --zookeeper $ZKURL -reassignment-json-file $FILE -execute
