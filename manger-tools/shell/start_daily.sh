#!/bin/bash

# Created by xbsu on 16/1/6.

if [ $# -ne 2 ]
then
  echo "exampl: $0 20160101 20160102"
  exit 1
fi

BEGIN_DATE=$1
END_DATE=$2

export PATH=$PATH

DB_HOST=""
DB_USER=""
DB_PASS=""
DB_DB=""
MYSQL="mysql -u${DB_USER} -p${DB_PASS} -h${DB_HOST} -D${DB_DB} --skip-column-name -e"


##################main####################
echo "======Start time `date`==========="

while [ $BEGIN_DATE -le $END_DATE ]; do
  FORMAT_DATE=`date -d "$BEGIN_DATE" +"%Y-%m-%d"`
  echo "fromat date $FORMAT_DATE"
  ##TODO something
  SQL=""
  $MYSQL $SQL
  BEGIN_DATE=`date -d "$BEGIN_DATE UTC +1 day" +"%Y%m%d"`
done

echo "======End time `date`==========="
