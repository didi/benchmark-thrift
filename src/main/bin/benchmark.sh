#!/bin/bash

if [[ -f /etc/profile ]]; then
    . /etc/profile
fi

if [[ -f ~/.bash_profile ]]; then
    . ~/.bash_profile
fi

function validate_tool_dir(){
    for dir in $@; do
      if [[ ! -d ${dir} ]]; then
        printf "${SHELL_NAME}: tool seems to be broken, ${dir} is missing, please download and redeploy.\n"
        exit 1
      fi
    done
}

function get_supported_thrift_versions(){
  local dir=$1
  for element in `ls ${dir}`
  do
    if [[ -d ${LIB_THRIFT_DIR}/${element} ]]; then
      supported_thrift_versions+=(${element})
    fi
  done
}

function validate_env_file(){
  local file=$1
  local client_jar=""
  local transport=""
  local protocol=""
  if [[ ! -f "${file}" ]]; then
    if [[ ${is_newbie} == true ]]; then
      printf "${SHELL_NAME}: environment file is missing"
      print_to_newbie
    else
      printf "${SHELL_NAME}: environment file is missing\n"
    fi
    print_usage
    exit 1
  fi

  while IFS='=' read -r key value
  do
    key="${key// /}"
    case "${key}" in
    "thrift_version")
      eval ${key}='${value}'
      ;;
    "client_jar")
      eval ${key}='${value}'
      ;;
    "transport")
      eval ${key}='${value}'
      ;;
    "protocol")
      eval ${key}='${value}'
      ;;
    *)
      ;;
    esac
  done < ${file}

  # validate version
  if [[ ${thrift_version} == "" ]]; then
    echo "${SHELL_NAME}: thrift version should be specified in environment file"
    exit 1
  fi

  if [[ ! "${supported_thrift_versions[@]}" =~ "${thrift_version}" ]]; then
    echo "${SHELL_NAME}: unsupported thrift version ${thrift_version}, available $(IFS=, ; echo ${supported_thrift_versions[*]})"
    exit 1
  fi

  # validate client_jar
  if [[ ${client_jar} == "" ]]; then
    echo "${SHELL_NAME}: client jar should be specified in environment file"
    exit 1
  fi
  if [[ ${client_jar} != *.jar ]]; then
    echo "${SHELL_NAME}: client jar should be ended with .jar in environment file"
    exit 1
  fi
  if [[ ${client_jar} != "/"* ]]; then
    client_jar=${HOME_DIR}/${client_jar}
  fi
  if [[ ! -f ${client_jar} ]]; then
    echo "${SHELL_NAME}: client jar is missing: ${client_jar}"
    exit 1
  fi

  printf "${SHELL_NAME}: will benchmark with the following thrift environment:
  Thrift version  ->  ${thrift_version}
  Client jar      ->  ${client_jar}
  TTransport      ->  ${transport}
  TProtocol       ->  ${protocol}
"
}

function validate_and_parse_url(){
  local full_url=$1
  if [[ ${full_url} == "" ]];  then
    if [[ ${is_newbie} == true ]]; then
      printf "${SHELL_NAME}: please enter thrift url"
      print_to_newbie
    else
      printf "${SHELL_NAME}: please enter thrift url\n"
    fi
    print_usage
    exit 1
  fi
  if [[ ${full_url} != "${scheme}://"* ]]; then
    echo "${SHELL_NAME}: incorrect thrift url, should start with ${scheme}://"
    print_usage
    exit 1
  fi

  local sub_url=${url#"$scheme://"}
  IFS='/ ' read -ra array <<< ${sub_url}
  if [[ ${#array[@]} < 3 ]]; then
    echo "${SHELL_NAME}: incorrect thrift url, should specify <host>:<port>/<service>/<method>: ${full_url}"
    print_usage
    exit 1
  fi
  IFS=': ' read -ra hostandport <<< ${array[0]}
  if [[ ${#hostandport[@]} != 2 ]]; then
    echo "${SHELL_NAME}: incorrect thrift url, should specify <host>:<port>: ${full_url}"
    print_usage
    exit 1
  fi
  host=${hostandport[0]}
  port=${hostandport[1]}
  service=${array[1]}
  method=${array[2]}
}

# check whether specified port is available
function validate_thrift_server(){
  local host=$1
  local port=$2
  local service=$3
  nc -zw5 ${host} ${port} &> /dev/null
  if [[ "$?" -ne 0 ]]; then
    if [[ ${host} == "127.0.0.1" ]] && [[ ${service} == "DemoService" ]]; then
      # If user is benchmarking demo-thrift-server
      printf "${SHELL_NAME}: demo thrift server ${host}:${port} seems to be down, make sure to start it before benchmarking\n"
      printf "  [usage] sh demo_thrift_server.sh -p ${port}\n"
      exit 1
    else
      printf "${SHELL_NAME}: thrift server ${host}:${port} seems to be down, make sure to start it before benchmarking\n"
    fi
  fi
}

function print_to_newbie(){
  printf ", is it your first time to use ${TOOL_NAME}? follow these steps to make it work\n"
  printf "  1. use -e to specify an environment file, or rename one sample to thrift.env in directory conf/\n"
  printf "  2. double check thrift version, client jar location, transport and protocol in environment file\n"
  printf "  3. see usages below and re-run this shell\n"
}

function print_usage(){
  local demo_host="127.0.0.1"
  local demo_port=8972
  local demo_service="DemoService"
  printf "\
Usage: sh ${SHELL_NAME}.sh [options] thrift://<host>:<port>/<service>/<method>[?[@<data_file>]]

Options:
   -c <concurrency>       Number of multiple requests to make at a time
                          If no -c nor -q is specified, default value is 1 concurrency
   -q <throughput>        Number of requests issued in 1 Second
                          If no -c nor -q is specified, default value is 1 concurrency
   -t <time_limit>        How long the benchmark runs, 2 or 2s means 2 seconds, 2m for 2 minutes, 2h for 2 hours
                          If not specified, default value is 60 seconds
   -e <environment file>  Thrift environment configuration file, containing thrift version, protocol and transport etc.
                          If not specified, default value is ../conf/thrift.env
   -h                     Display usage information (this message) and exit
   -v                     Print version number and exit

Where:
   <data_file>            A local file that contains request arguments, schemeed by a "@".
                          If the thrift method has parameters, <data_file> is mandatory.

Examples:
    # 1. benchmark a non-args method with default conf
    sh ${SHELL_NAME}.sh thrift://${demo_host}:${demo_port}/${demo_service}/noArgMethod
    # 2. benchmark at 10 concurrencies for 5 minutes
    sh ${SHELL_NAME}.sh -c 10 -t 5m thrift://${demo_host}:${demo_port}/${demo_service}/noArgMethod
    # 3. benchmark at 10 qps for 2 hours
    sh ${SHELL_NAME}.sh -q 10 -t 2h thrift://${demo_host}:${demo_port}/${demo_service}/noArgMethod
    # 4. specify an environment file
    sh ${SHELL_NAME}.sh -e conf/tsocket.sample.env thrift://${demo_host}:${demo_port}/${demo_service}/method
    # 5. specify arguments
    sh ${SHELL_NAME}.sh thrift://${demo_host}:${demo_port}/${demo_service}/oneArgMethod?@demo/oneArgMethod_args.csv
"
}

function start_to_benchmark(){
  local classpath=${LIB_THRIFT_DIR}/${thrift_version}/*:${LIB_DIR}/*:${LIB_CLASSES_DIR}
  local java_opts="-server -Xmx16G -Xms16G -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled -XX:ErrorFile=$BIN_DIR/hs_err_pid%p.log -Xloggc:$BIN_DIR/gc.log -XX:HeapDumpPath=$BIN_DIR -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError"
  local pid_file="$BIN_DIR/pid"
  if [[ ! -s "${pid_file}" ]] || [[ "" == $(cat ${pid_file}) ]] || [[ -z "$(ps -eo pid | grep -w $(cat ${pid_file}))" ]]; then
    java ${java_opts} -cp ${classpath} com.didiglobal.pressir.thrift.Main $* 2>&1
    echo $! > ${pid_file}
  else
    echo "${SHELL_NAME}: tool is ready running, pid=$(cat ${pid_file})"
    exit 1
  fi
}

### start to execute from here
# constants
declare -r SHELL_NAME="benchmark"
declare -r TOOL_NAME="BenchmarkThrift"
declare -r TOOL_VERSION="0.0.1"
declare -r HOME_DIR=$(cd $(dirname $0); cd ..; pwd)
declare -r BIN_DIR="${HOME_DIR}/bin"
declare -r CONF_DIR="${HOME_DIR}/conf"
declare -r LIB_DIR="${HOME_DIR}/lib"
declare -r LIB_CLASSES_DIR="${LIB_DIR}/classes"
declare -r LIB_THRIFT_DIR="${LIB_DIR}/thrift"
declare -r DEFAULT_ENV_FILE="${CONF_DIR}/thrift.env"
# parameters
declare -a supported_thrift_versions=()
declare thrift_version=""
declare -i concurrency=0
declare -i throughput=0
declare -i time_limit=0
declare env_file=${DEFAULT_ENV_FILE}
# thrift url
declare url=""
declare -r scheme="thrift"
declare host=""
declare -i port=0
declare service=""
declare method=""
# others
declare is_newbie=false

# step 1
while getopts ":c:q:t:e:hv" opt
do
  case "$opt" in
    c)
      if [[ ${throughput} -gt 0 ]]; then
          echo "${SHELL_NAME}: only one of -c or -q should be specified"
          print_usage
          exit 1
      fi
      concurrency="$OPTARG"
      ;;
    q)
      if [[ ${concurrency} -gt 0 ]]; then
          echo "${SHELL_NAME}: only one of -c or -q should be specified"
          print_usage
          exit 1
      fi
      throughput="$OPTARG"
      ;;
    t)
      time_limit="$OPTARG"
      ;;
    e)
      env_file="$OPTARG"
      ;;
    h)
      print_usage
      exit 1
      ;;
    v)
      printf "This is ${TOOL_NAME}, version ${TOOL_VERSION}\n"
      exit 1
      ;;
    *)
      printf "${SHELL_NAME}: illegal option ${OPTARG}\n"
      print_usage
      exit 1
      ;;
    esac
done
if [[ ${env_file} == ${DEFAULT_ENV_FILE} ]]; then
  if [[ ! -f ${env_file} ]]; then
    is_newbie=true
  fi
fi

# step 2
validate_tool_dir ${BIN_DIR} ${CONF_DIR} ${LIB_DIR} ${LIB_CLASSES_DIR} ${LIB_THRIFT_DIR}

# step 3
shift $(($OPTIND - 1))
url=$1
validate_and_parse_url ${url}

# step 4
get_supported_thrift_versions ${LIB_THRIFT_DIR}

# step 5
validate_env_file ${env_file}

# step 6
validate_thrift_server ${host} ${port} ${service}

# step 7
declare params=""
params="$params -e ${env_file}"
params="$params -u ${url}"
if [[ ${concurrency} -gt 0 ]]; then
  params="$params -c ${concurrency}"
elif [[ ${throughput} -gt 0 ]]; then
  params="$params -q ${throughput}"
else
  params="$params -c 1"
fi
if [[ ${timelit} -gt 0 ]]; then
  params="$params -t $time_limit"
else
  params="$params -t 60"
fi

start_to_benchmark ${params}
