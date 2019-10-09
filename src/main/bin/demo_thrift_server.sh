#!/bin/bash

function start_server(){
  echo "server started on $1"
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

# 设置默认值
shell="demo_thrift_server"
version="0.0.1"
port=8972

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
      printf "This is a demo thrift server, version ${version}\n"
      exit 1
      ;;
    *)
      printf "${shell}: illegal option ${OPTARG}\n"
      print_usage
      exit 1
      ;;
    esac
done

start_server ${port}
