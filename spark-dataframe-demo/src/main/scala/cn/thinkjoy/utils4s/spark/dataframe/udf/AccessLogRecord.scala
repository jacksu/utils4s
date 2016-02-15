package cn.thinkjoy.utils4s.spark.dataframe.udf

/**
 * Created by jacksu on 16/2/5.
 */

case class AccessLogRecord(
  clientIpAddress: String, // should be an ip address, but may also be the hostname if hostname-lookups are enabled
  rfc1413ClientIdentity: String, // typically `-`
  remoteUser: String, // typically `-`
  dateTime: String, // [day/month/year:hour:minute:second zone]
  request: String, // `GET /foo ...`
  httpStatusCode: String, // 200, 404, etc.
  bytesSent: String, // may be `-`
  referer: String, // where the visitor came from
  userAgent: String // long string to represent the browser and OS
  )

case class UserAgent(
  family: String,
  major: Option[String] = None,
  minor: Option[String] = None,
  patch: Option[String] = None)