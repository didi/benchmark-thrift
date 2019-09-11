#!/bin/bash

if [ -f /etc/profile ]; then
    . /etc/profile
fi

if [ -f ~/.bash_profile ]; then
    . ~/.bash_profile
fi

function get_versions(){
  for element in `ls thrift`
  do
    if [[ -d thrift/$element ]]; then
      versions=$element","$versions
    fi
  done
  versions=${versions%,*}
}

function start(){
  BASE_DIR=$(cd $(dirname $0); pwd)
  CLASSPATH=$BASE_DIR/conf:$BASE_DIR/lib/*:$BASE_DIR/thrift/$version/*
  BIN_DIR=$(cd $(dirname $0); pwd)
  JAVA_OPTS="-server -Xmx16G -Xms16G -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled -XX:ErrorFile=$BIN_DIR/hs_err_pid%p.log -Xloggc:$BIN_DIR/gc.log -XX:HeapDumpPath=$BIN_DIR -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError"
  PID_FILE="$BIN_DIR/pid"
  if [ ! -s "$PID_FILE" ] || [ -z "$(ps -eo pid | grep -w $(cat $PID_FILE))" ]; then
    java $JAVA_OPTS -cp $CLASSPATH com.pressir.Test $* 2>&1 &
    echo $! > $PID_FILE
  else
    echo "error: application can not start duplicate! running pid=$(cat $PID_FILE)"
    exit 1
  fi
}

function validate(){
  # 获取工具支持的所有thrift version
  get_versions

  # 指定版本是否合规
  version=$(awk '{if ($1 == "version:") {print $2}}' $protocol)
  if [[ $versions != *$version* ]]; then
    echo error: thrift版本信息必须为"$versions"中的一个!;
    exit 1
  fi

  # 是否指定jar包
  jar=$(awk '{if ($1 == "jar:") {print $2}}' $protocol)
  if [[ $jar == "" || $jar != *.jar ]]; then
    echo "error: jar file must be prepared!"
    exit 1
  fi
}
# 设置默认值
concurrency=1
duration=60
startparam=""
types=0
while getopts ":n:c:D:q:p:d:hv" opt
do
  case "$opt" in
    n)
      requests="$OPTARG"
      startparam="-n $requests $startparam "
      ;;
    c)
      concurrency="$OPTARG"
      type=$[type+1]
      startparam="-c $concurrency $startparam "
      ;;
    D)
      duration="$OPTARG"
      startparam="-D $duration $startparam "
      ;;
    q)
      throughput="$OPTARG"
      type=$[type+1]
      startparam="-q $throughput $startparam "
      ;;
    p)
      protocol="$OPTARG"
      startparam="-p $protocol $startparam "
      validate
      ;;
    d)
      param="$OPTARG"
      startparam="-d $param $startparam "
      ;;
    -h)
      echo "####EXAMPLE \n
./pstr.sh -p <protocol.conf> -d <data.conf> [ -c concurrency ] [ -n requests ] [options] host:port/service/method \n
####OPTIONS\n

   -p thriftfile  File pointed the protocol and the transport and thrift version

   -d paramsfile  File containing data to the rpc method

   -n requests    Number of requests to perform

   -c concurrency Number of multiple requests to make at a time

   -D duration    Pressure duration

   -q throughput  Number of requests issued in 1 Second

   -h             Display usage information (this message) and exit

   -v             Print version number and exit"

      exit 1
      ;;
    v)
      echo "the tool version is 0.9.0!"
      exit 1
      ;;
    *)
      echo "error: param error!"
      exit 1
      ;;
    esac
done
if [ $type == 2 ];  then
  echo "error: pressure type must be -c or -q!"
  exit 1
fi

shift $(($OPTIND - 1))
if [[ $1 == "" ]];  then
  echo "error: please enter url!"
  exit 1
fi
startparam="$startparam -u $1"

start $startparam

