#! /usr/local/bin/python3
# coding = utf-8

__author__ = 'jacksu'

import logging
import logging.handlers


def getLogger():
    logging.basicConfig(level=logging.DEBUG,
                        format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                        datefmt='%a, %d %b %Y %H:%M:%S',
                        filemode='w')
    return logging.getLogger()
