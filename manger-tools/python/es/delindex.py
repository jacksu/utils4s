#! /usr/local/bin/python3
# coding = utf-8

import sys
import requests
import datetime

__author__ = 'jacksu'


def delindex(index):
    auth = ("es_admin", "password")
    print(auth)
    top_url = "http://10.253.2.125:9200/"
    print(top_url)
    url = top_url + index
    result = requests.delete(url, auth=auth)
    if result.status_code != 200:
        return False
    return True


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("example: " + sys.argv[0] + " index")
        sys.exit(1)

    index = sys.argv[1]

    print("Start time: " + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))

    if not delindex(index):
        print("delete index error: " + index)
    print("End time: " + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
