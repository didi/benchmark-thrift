#!/bin/bash

function start_server(){
  local port = $1;
  local classpath=${LIB_DIR}/*:${LIB_DIR}/thrift/0.11.0/*:
  local java_opts="-server -Xmx16G -Xms16G -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled -XX:ErrorFile=$BIN_DIR/${SHELL_NAME}_hs_err_pid%p.log -Xloggc:$BIN_DIR/${SHELL_NAME}_gc.log -XX:HeapDumpPath=$BIN_DIR -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError"
  local pid_file="$BIN_DIR/${SHELL_NAME}_pid"
  if [[ ! -s "${pid_file}" ]] || [[ "" == $(cat ${pid_file}) ]] || [[ -z "$(ps -eo pid | grep -w $(cat ${pid_file}))" ]]; then
    java ${java_opts} -cp ${classpath} com.didiglobal.pressir.thrift.demo.DemoServer $* 2>&1
    echo $! > ${pid_file}
  else
    echo "${SHELL_NAME}: ${TOOL_NAME} is ready running, pid=$(cat ${pid_file})"
    exit 1
  fi
}


function print_usage(){
  printf "\
Usage: sh ${shell}.sh [options]

Options:
   -p <port>       Port number to use for connection
                   If not specified, default value is 8972
   -h              Display usage information (this message) and exit
   -v              Print version number and exit

Examples:
    # 1. start a demo thrift server on default port 8972
    sh ${shell}.sh
    # 2. start a demo thrift server on port 8900
    sh ${shell}.sh -p 8900
"
}

### start to execute from here
# constants
declare -r SHELL_NAME="demo_thrift_server"
declare -r TOOL_NAME="demo thrift server"
declare -r TOOL_VERSION="0.0.1"
declare -r HOME_DIR=$(cd $(dirname $0); cd ..; pwd)
declare -r BIN_DIR="${HOME_DIR}/bin"
declare -r LIB_DIR="${HOME_DIR}/lib"
declare -i port=8972

while getopts "p:hv" opt
do
  case "$opt" in
    p)
      port="$OPTARG"
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

start_server ${port}
