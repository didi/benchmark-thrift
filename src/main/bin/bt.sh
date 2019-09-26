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
  if [ ! -s "$PID_FILE" ] || [[ "" == $(cat $PID_FILE) ]] || [ -z "$(ps -eo pid | grep -w $(cat $PID_FILE))" ]; then
    java $JAVA_OPTS -cp $CLASSPATH com.didiglobal.pressir.thrift.Main $* 2>&1
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
  . $protocol >/dev/null 2>&1
  if [[ ${version} == "" ]]; then
    echo "${name}: 必须在配置文件中指定Thrift的版本信息(Thrift version information must be specified in the configuration file)"
    exit 1
  fi
  if [[ ${versions} != *$version* ]]; then
    echo "${name}: 当前工具不支持您指定的Thrift版本,工具目前支持的版本有:"$versions"(The tool does not support the Thrift version you specified yet. The supported version of the tool are: "$versions")"
    exit 1
  fi

  # 是否指定jar包
  if [[ $classpath == "" || $classpath != *.jar ]]; then
    echo "${name}: 配置文件中必须指定Jar位置(Jar information must be specified in the configuration file)"
    exit 1
  fi
}

function print_usage(){
  file="conf/usage.txt";
  if [[ ! -f ${file} ]]; then
    file="conf/usage.txt";
  fi
  if [[ ! -f ${file} ]]; then
    echo "hello"
    return;
  fi

  while IFS= read -r line || [[ -n ${line} ]]; do
    printf '%s\n' "$line"
  done < "$file"
}

function print_tool_version(){
  file="conf/thrift-benchmark.properties"

  while IFS='=' read -r key value|| [[ -n ${key} ]]; do
    key=$(echo $key | tr '.' '_')
    eval ${key}=\${value}
  done < "$file"
  echo "This is ${project_name}, version ${project_version}"
}

# 设置默认值
name="bt"
params=""
types=0
while getopts ":n:c:D:q:p:d:hv:" opt
do
  case "$opt" in
    c)
      concurrency="$OPTARG"
      types=$[types+1]
      params="-c $concurrency $params "
      ;;
    D)
      duration="$OPTARG"
      params="-D $duration $params "
      ;;
    q)
      throughput="$OPTARG"
      types=$[types+1]
      params="-q $throughput $params "
      ;;
    p)
      protocol="$OPTARG"
      params="-p $protocol $params "
      validate
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
      print_tool_version
      exit 1
      ;;
    *)
      echo "${name}: illegal option ${OPTARG}"
      print_usage
      exit 1
      ;;
    esac
done
if [[ ${types} == 2 ]];  then
  echo "${name}: 发压类型必须是并发(-c)或QPS(-q)发压中的一种(only one of -c or -q could be specified)"
  print_usage
  exit 1
fi
if [[ ${protocol} == "" ]]; then
  echo "${name}: 必须通过-p来指定Thrift 配置文件(please use -p to specify thrift conf file)"
  print_usage
  exit 1
fi
if [[ ${duration} == "" ]]; then
  params="$params -D 60s"
fi
if [[ ${types} == 0 ]]; then
  params="$params -c 1"
fi

shift $(($OPTIND - 1))
if [[ $1 == "" ]];  then
  echo "${name}: 需要在启动命令的最后指定Thrift url信息(please enter thrift url)"
  print_usage
  exit 1
fi

params="$params -u $1"

start $params

