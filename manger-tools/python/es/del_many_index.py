#! /usr/local/bin/python3
# coding = utf-8

import datetime
import sys
sys.path.append('.')
from delindex import delindex

__author__ = 'jacksu'


def str_2_date(str):
    return datetime.datetime.strptime(str, "%Y%m%d")


def nextdate(str):
    return (datetime.datetime.strptime(str,'%Y%m%d') + datetime.timedelta(days=1)).strftime('%Y%m%d')

def formatdate(str):
    return datetime.datetime.strptime(str, "%Y%m%d").strftime('%Y.%m.%d')

if __name__ == '__main__':
    if len(sys.argv) != 4:
        print("example: " + sys.argv[0] + " index_prefix start_date end_date")
        sys.exit(1)

    prefix = sys.argv[1]
    begin = sys.argv[2]
    end = sys.argv[3]

    print("Start time: " + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))

    while str_2_date(begin) <= str_2_date(end):
        index = prefix + "-" + formatdate(begin)
        print(index)
        if not delindex(index):
            print("delete index error: " + index)
        begin = str(nextdate(begin))
    print("End time: " + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))