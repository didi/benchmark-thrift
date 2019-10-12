#!/usr/bin/env bash

set -o errexit
set -o pipefail
#set -o nounset
#set -o xtrace
# magic variables for current file & dir
declare -r __dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
declare -r __file="${__dir}/$(basename "${BASH_SOURCE[0]}")"
declare -r __base="$(basename ${__file} .sh)"
declare -r __root="$(cd "$(dirname "${__dir}")" && pwd)" # <-- change this as it depends on your app
# constants
declare -r _tool_name="demo thrift server"
declare -r _tool_version="0.0.1"
declare -r _thrift_version="0.11.0"

function start_server(){
  local port=$1;
  local classpath=${__dir}/lib/*:${__root}/lib/thrift/${_thrift_version}/*:
  local java_opts="-server -Xmx16G -Xms16G -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled"
  local pid_file="${__dir}/${__base}_pid"
  if [[ ! -s "${pid_file}" ]] || [[ "" == $(cat ${pid_file}) ]] || [[ -z "$(ps -eo pid | grep -w $(cat ${pid_file}))" ]]; then
    java ${java_opts} -cp ${classpath} com.didiglobal.pressir.thrift.demo.Main $* 2>&1
    echo $! > ${pid_file}
  else
    echo "${__base}: ${_tool_name} is ready running, pid=$(cat ${pid_file})"
    exit 1
  fi
}

function print_help(){
  printf "\
Usage: sh ${__base}.sh [options]

Options:
   -p <port>       Port number to use for connection
                   If not specified, default value is 8972
   -h              Display usage information (this message) and exit
   -v              Print version number and exit

Examples:
    # 1. start a demo thrift server on default port 8972
    sh ${__base}.sh
    # 2. start a demo thrift server on port 8900
    sh ${__base}.sh -p 8900
"
}

function main(){
	local port=8972

	while getopts "p:hv" opt
	do
		case "$opt" in
			p)
				port="$OPTARG"
				;;
			h)
				print_help
				exit 1
				;;
			v)
				printf "This is ${_tool_name}, version ${_tool_version}\n"
				exit 1
				;;
			*)
				printf "${__base}: illegal option ${OPTARG}\n"
				print_help
				exit 1
				;;
			esac
	done

	start_server ${port}
}

main "${@}"
