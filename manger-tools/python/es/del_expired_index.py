#! /usr/local/bin/python3
# coding = utf-8

__author__ = 'jacksu'

import os
import sys
import xml.etree.ElementTree as ET
from calendar import datetime
import requests
import logger

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("example: " + sys.argv[0] + " expired_index.conf")
        sys.exit(1)
    if not os.path.exists(sys.argv[1]):
        print("conf file does not exist")
        sys.exit(1)
    logger = logger.getLogger()
    logger.info("Start time: " + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
    tree = ET.parse(sys.argv[1])
    root = tree.getroot()
    # conn = httplib.HTTPConnection("http://es_admin:password@10.253.2.125:9200/")
    for host in root.findall("host"):

        top_url = host.get("url")
        logger.info(top_url)
        for index in host.findall("index"):
            prefix = index.find("name").text
            period = index.find("period").text
            suffix = (datetime.datetime.now() - datetime.timedelta(days=int(period))).strftime('%Y.%m.%d')
            index = prefix + "-" + suffix
            logger.debug(index)
            url = top_url + index
            if "true" == host.get("auth"):
                auth = (host.get("user"), host.get("password"))
                logger.info("auth: " + str(auth))
                result = requests.delete(url, auth=auth)
            else:
                result = requests.delete(url)
            logger.debug(result.json())
            logger.debug(result.status_code)
    logger.info("End time: " + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
