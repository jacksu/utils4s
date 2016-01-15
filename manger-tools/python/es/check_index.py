#! /usr/local/bin/python3
# coding = utf-8

__author__ = 'jacksu'

import os
import sys
import xml.etree.ElementTree as ET
from calendar import datetime
import requests
sys.path.append('.')
import logger
import mail


if __name__ == '__main__':
    if len(sys.argv) != 2:  # 参数判断
        print("example: " + sys.argv[0] + " index_list.conf")
        sys.exit(1)
    if not os.path.exists(sys.argv[1]):  # 文件存在判断
        print("conf file does not exist")
        sys.exit(1)

    logger=logger.getLogger()
    logger.info("Start time: " + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
    tree = ET.parse(sys.argv[1])
    root = tree.getroot()
    list = []
    for hosts in root.findall("host"):
        logger.debug(hosts)
        auth_flag = hosts.get("auth")
        logger.debug(auth_flag)
        if "true" == auth_flag:
            auth = (hosts.get("user"), hosts.get("password"))
            logger.info(auth)
        top_url = hosts.get("url")
        logger.info(top_url)
        for child in hosts.findall("index"):
            prefix = child.find("name").text
            period = child.find("period").text
            type = child.find("period").get("type")
            logger.debug(type)
            if "day" == type:
                suffix = (datetime.datetime.now() - datetime.timedelta(days=int(period))).strftime('%Y.%m.%d')
            elif "month" == type:
                suffix = datetime.datetime.now().strftime('%Y%m')
            index = prefix + suffix
            logger.debug(index)
            url = top_url + index
            if "true" == auth_flag:
                result = requests.head(url, auth=auth)
            else:
                result = requests.head(url)
            if result.status_code != 200:
                list.append(index)
    if 0 != len(list):
        logger.debug("send mail")
        mail.send_mail('xbsu@thinkjoy.cn', 'xbsu@thinkjoy.cn', 'ES 索引错误', str(list))
    logger.info("End time: " + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
