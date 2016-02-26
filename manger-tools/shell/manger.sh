#!/bin/bash

###################
#主要用于程序启动和停止时，只需要修改函数start中COMMAND
#COMMAND赋值为你要操作的程序即可
# Created by jacksu on 16/1/15.

BASE_NAME=`dirname $0`
NAME=`basename $0 | awk -F '.' '{print $1}'`

function print_usage(){
 echo "manger.sh <start | status | restart | stop> [OPTION]"
 echo "  --help|-h"
 echo "  --daemon|-d        默认后台运行"
 echo "  --logdir|-l        日志目录"
 echo "  --conf             配置文件"
 echo "  --workdir"
}

# Print an error message and exit
function die() {
  echo -e "\nError: $@\n" 1>&2
  print_usage
  exit 1
}

for i in "$@"
do
  case "$1" in
    start|stop|restart|status)
      ACTION="$1"
      ;;
    --workdir)
      WORK_DIR="$2"
      shift
      ;;
    --fwdir)
      FWDIR="$2"
      shift
      ;;
    --logdir)
      LOG_DIR="$2"
      shift
      ;;
    --jars)
      JARS="$2"
      shift
      ;;
    --conf)
      CONFIG_DIR="$2"
      shift
      ;;
    --jvmflags)
      JVM_FLAGS="$2"
      shift
      ;;
    --help|-h)
      print_usage
      exit 0
      ;;
    *)
      ;;
  esac
  shift
done

PID="$BASE_NAME/.${NAME}_pid"

if [ -f "$PID" ]; then
  PID_VALUE=`cat $PID` > /dev/null 2>&1
else
  PID_VALUE=""
fi

if [ ! -d "$LOG_DIR" ]; then
  mkdir "$LOG_DIR"
fi

function start(){
  echo "now is starting"

  #TODO 添加需要执行的命令
  COMMAND=""
  COMMAND+=""

  echo "Running command:"
  echo "$COMMAND"
  nohup $COMMAND & echo $! > $PID
}

function stop() {
  if [ -f "$PID" ]; then
    if kill -0 $PID_VALUE > /dev/null 2>&1; then
      echo 'now is stopping'
      kill $PID_VALUE
      sleep 1
      if kill -0 $PID_VALUE > /dev/null 2>&1; then
        echo "Did not stop gracefully, killing with kill -9"
        kill -9 $PID_VALUE
      fi
    else
      echo "Process $PID_VALUE is not running"
    fi
  else
    echo "No pid file found"
  fi
}

# Check the status of the process
function status() {
  if [ -f "$PID" ]; then
    echo "Looking into file: $PID"
    if kill -0 $PID_VALUE > /dev/null 2>&1; then
      echo "The process is running with status: "
      ps -ef | grep -v grep | grep $PID_VALUE
    else
      echo "The process is not running"
      exit 1
    fi
  else
    echo "No pid file found"
    exit 1
  fi
}


case "$ACTION" in
  "start")
    start
    ;;
  "status")
    status
    ;;
  "restart")
    stop
    echo "Sleeping..."
    sleep 1
    start
    ;;
  "stop")
    stop
    ;;
  *)
    print_usage
    exit 1
    ;;
esac