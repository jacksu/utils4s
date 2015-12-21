/**
create table weixiao_follower_info(
requestTime BIGINT,
requestParams STRUCT<timestamp:BIGINT,phone:STRING,cardName:STRING,provinceCode:STRING,cityCode:STRING>,
requestUrl STRING)
 row format serde "com.besttone.hive.serde.JSONSerDe"
 WITH SERDEPROPERTIES(
 "input.invalid.ignore"="true",
 "requestTime"="$.requestTime",
 "requestParams.timestamp"="$.requestParams.timestamp",
 "requestParams.phone"="$.requestParams.phone",
 "requestParams.cardName"="$.requestParams.cardName",
 "requestParams.provinceCode"="$.requestParams.provinceCode",
 "requestParams.cityCode"="$.requestParams.cityCode",
 "requestUrl"="$.requestUrl");
 **/

 //暂时发现patition有问题
 CREATE EXTERNAL TABLE weixiao_follower_info(
   uid STRING,
   schoolCode STRING,
   attend STRING,
   app STRING,
   suite STRING,
   timestamp STRING)
 ROW FORMAT serde "cn.thinkjoy.utils4s.hive.json.JSONSerDe"
 WITH SERDEPROPERTIES(
 "input.invalid.ignore"="true",
 "uid"="$.uid",
 "schoolCode"="$.schoolCode",
 "attend."="$.attend",
 "app"="$.app",
 "suite"="$.suite",
 "timestamp"="$.timestamp");

 load data inpath '/tmp/weixiao_user_guanzhu_log/20151217/20/' INTO TABLE weixiao_follower_info partition(dt='20151217',hour='20')
 select  * from weixiao_follower_info where cast(timestamp as bigint)>=unix_timestamp('2015121720','yyyyMMddHH')*1000;