zeppelin编译命令
mvn clean package -Pspark-1.4 -Dhadoop.version=2.6.0-cdh5.4.1 -Phadoop-2.6 -Pvendor-repo -Pyarn -Ppyspark -DskipTests

配置文件为：
[interpreter.json](../resources/zeppelin/interpreter.json)

[zeppelin-env.sh](../resources/zeppelin/zeppelin-env.sh)