#!/bin/bash

if [[ -f /etc/profile ]]; then
    . /etc/profile
fi

if [[ -f ~/.bash_profile ]]; then
    . ~/.bash_profile
fi

function get_versions(){
  for element in `ls ../lib/thrift`
  do
    if [[ -d ../lib/thrift/$element ]]; then
      versions=$element","$versions
    fi
  done
  versions=${versions%,*}
}

function start(){
  BASE_DIR=$(cd $(dirname $0); cd ..; pwd)
  CLASSPATH=$BASE_DIR/lib/thrift/$version/*:$BASE_DIR/lib/*:$BASE_DIR/lib/classes
  BIN_DIR=$(cd $(dirname $0); pwd)
  JAVA_OPTS="-server -Xmx16G -Xms16G -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled -XX:ErrorFile=$BIN_DIR/hs_err_pid%p.log -Xloggc:$BIN_DIR/gc.log -XX:HeapDumpPath=$BIN_DIR -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError"
  PID_FILE="$BIN_DIR/pid"
  if [ ! -s "$PID_FILE" ] || [[ "" == $(cat $PID_FILE) ]] || [ -z "$(ps -eo pid | grep -w $(cat $PID_FILE))" ]; then
    java $JAVA_OPTS -cp $CLASSPATH com.didiglobal.pressir.thrift.Main $* 2>&1
    echo $! > $PID_FILE
  else
    echo "${shell}: application can not start duplicate! running pid=$(cat $PID_FILE)"
    exit 1
  fi
}

function print_conf(){
  printf "\
We will start with the thrift env:
  Thrift version   -> ${version}
  Classpath        -> ${classpath}
  TTransport       -> ${transport}
  TProtocol        -> ${protocol}
"
}
function read_conf(){
  while IFS='=' read -r key value
  do
    eval ${key}='${value}' >/dev/null 2>&1
  done < "$environment_file"
}
function validate(){
  # 获取工具支持的所有thrift version
  get_versions
  # 指定版本是否合规

  # . $environment_file >/dev/null 2>&1
  read_conf
  if [[ ${version} == "" ]]; then
    echo "${shell}: thrift version must be specified in thrift env file"
    exit 1
  fi
  if [[ ${versions} != *$version* ]]; then
    echo "${shell}: the tool does not support the thrift version you specified yet. tool support "$versions""
    exit 1
  fi

  # 是否指定jar包
  if [[ $classpath == "" || $classpath != *.jar ]]; then
    echo "${shell}: jar information must be specified in thrift env file"
    exit 1
  fi
  print_conf
}

function print_usage(){
  printf "\
Usage: sh ${shell}.sh [options] thrift://<host>:<port>/<service>/<method>[?[@<datafile>]]

Options:
   -c <concurrency>       Number of multiple requests to make at a time
                          If no -c nor -q is specified, default value is 1 concurrency
   -q <throughput>        Number of requests issued in 1 Second
                          If no -c nor -q is specified, default value is 1 concurrency
   -t <timelimit>         How long the benchmark runs, 2 or 2s means 2 seconds, 2m for 2 minutes, 2h for 2 hours
                          If not specified, default value is 60 seconds
   -e <environment file>  Thrift environment configuration file, containing thrift version, protocol and transport etc.
                          If not specified, default value is ../conf/thrift.env
   -h                     Display usage information (this message) and exit
   -v                     Print version number and exit

Where:
   <datafile>             A local file that contains request arguments, prefixed by a "@".
                          If the thrift method has parameters, <data_file> is mandatory.

Examples:
    # 1. benchmark a non-args method with default conf
    sh ${shell}.sh thrift://127.0.0.1:8090/service/method?@datafile
    # 2. benchmark at 10 concurrencies for 5 minutes
    sh ${shell}.sh -c 10 -t 5m thrift://127.0.0.1:8090/service/method?@datafile
    # 3. benchmark at 10 qps for 2 hours
    sh ${shell}.sh -q 10 -t 2h thrift://127.0.0.1:8090/service/method?@datafile
    # 4. benchmark by qps for 2 hours
    sh ${shell}.sh -c 10 -t 2h thrift://127.0.0.1:8090/service/method?@datafile
"
}

# 设置默认值
name="BenchmarkThrift"
shell="benchmark"
version="0.0.1"
params=""
types=0

while getopts ":q:c:t:e:hv" opt
do
  case "$opt" in
    c)
      concurrency="$OPTARG"
      types=$[types+1]
      params="-c $concurrency $params "
      ;;
    t)
      timelimit="$OPTARG"
      params="-t $timelimit $params "
      ;;
    q)
      throughput="$OPTARG"
      types=$[types+1]
      params="-q $throughput $params "
      ;;
    e)
      environment_file="$OPTARG"
      ;;
    d)
      param="$OPTARG"
      params="-d $param $params "
      ;;
    h)
      print_usage
      exit 1
      ;;
    v)
      printf "This is ${name}, version ${version}\n"
      exit 1
      ;;
    *)
      printf "${shell}: illegal option ${OPTARG}\n"
      print_usage
      exit 1
      ;;
    esac
done

shift $(($OPTIND - 1))
if [[ $1 == "" ]];  then
  echo "${shell}: please enter thrift url"
  print_usage
  exit 1
fi

if [[ ${types} == 2 ]];  then
  echo "${shell}: only one of -c or -q could be specified"
  print_usage
  exit 1
fi

# check environment file
if [[ ${environment_file} == "" ]]; then
  environment_file=../conf/thrift.env
  if [[ ! -f ${environment_file} ]]; then
    echo "${shell}: is it your first time to use ${name}? environment file ${environment_file} is missing, you could"
    echo "  either: choose one sample in conf directory and rename it to thrift.env"
    echo "  or:     manually specify one by -e <thrift file>"
    exit 1
  fi
  echo "${shell}: use default ${environment_file}, or you could specify one by -e <thrift file>"
elif [[! -f ${environment_file} ]]; then
  echo "${shell}: environment file ${environment_file} is missing, please check it"
  exit 1
fi
params="-e $environment_file $params "

# check timelimit
if [[ ${timelimit} == "" ]]; then
  echo "${shell}: use default timelimit 60s, or you could specify one by -t <timelimit>"
  params="$params -t 60s"
fi

if [[ $types == 0 ]]; then
  echo "${shell}: use default load type 1 concurrency, or you could specify one by -c <concurrency> or -q <throughput>"
  params="$params -c 1"
fi

validate

params="$params -u $1"

start $params

