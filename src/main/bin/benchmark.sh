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
declare -r _tool_name="BenchmarkThrift"
declare -r _tool_version="0.0.1"
declare -r _bin_dir="${__root}/bin"
declare -r _conf_dir="${__root}/conf"
declare -r _lib_dir="${__root}/lib"
declare -r _lib_classes_dir="${_lib_dir}/classes"
declare -r _lib_thrift_dir="${_lib_dir}/thrift"
declare -r _default_env_file="${_conf_dir}/thrift.env"
# global variables
declare thrift_version=""
declare host=""
declare port=""
declare service=""

function validate_tool_dir(){
	for dir in $@; do
		if [[ ! -d ${dir} ]]; then
			printf "${__base}: tool seems to be broken, ${dir} is missing, please download and redeploy.\n"
			exit 1
		fi
	done
}

function validate_env_file(){
  local env_file=$1
  local client_jar=""
  local transport=""
  local protocol=""

  if [[ ! -f "${env_file}" ]]; then
		printf "${__base}: environment file is missing\n"
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
  done < ${env_file}

  validate_env_thrift_version ${thrift_version}
  validate_env_client_jar ${client_jar}
  validate_env_transport ${transport}
  validate_env_protocol ${protocol}

  printf "${__base}: will benchmark with the following thrift environment:
  Thrift version  ->  ${thrift_version}
  Client jar      ->  ${client_jar}
  TTransport      ->  ${transport}
  TProtocol       ->  ${protocol}
"
}

function validate_env_thrift_version(){
	local supported_versions=""
  for element in `ls ${dir}`
  do
    if [[ -d ${_lib_thrift_dir}/${element} ]]; then
    	supported_versions=${element}","${supported_versions}
    fi
  done

	if [[ ${thrift_version} == "" ]]; then
    echo "${__base}: thrift version should be specified in environment file"
    exit 1
  fi
	if [[ ${supported_versions} != *${thrift_version}* ]]; then
    echo "${__base}: unsupported thrift version ${thrift_version}, available $(IFS=, ; echo ${supported_versions})"
    exit 1
  fi
}

function validate_env_client_jar(){
  local client_jar=$1
  if [[ ${client_jar} == "" ]]; then
    echo "${__base}: client jar should be specified in environment file"
    exit 1
  fi
  if [[ ${client_jar} != *.jar ]]; then
    echo "${__base}: client jar should be ended with .jar in environment file"
    exit 1
  fi
  if [[ ${client_jar} != "/"* ]]; then
    client_jar=$(dirname ${env_file})/${client_jar}
  fi
  if [[ ! -f ${client_jar} ]]; then
    echo "${__base}: client jar is missing: ${client_jar}"
    exit 1
  fi
}

function validate_env_transport(){
  local transport=$1
  if [[ ${transport} == "" ]]; then
    echo "${__base}: transport should be specified in environment file"
    exit 1
  fi
}

function validate_env_protocol(){
  local protocol=$1
  if [[ ${protocol} == "" ]]; then
    echo "${__base}: protocol should be specified in environment file"
    exit 1
  fi
}

function validate_and_parse_url(){
  local full_url=$1
  if [[ ${full_url} == "" ]];  then
		printf "${__base}: please enter thrift url\n"
    print_usage
    exit 1
  fi
  if [[ ${full_url} != "thrift://"* ]]; then
    echo "${__base}: incorrect thrift url, should start with thrift://"
    print_usage
    exit 1
  fi

	# replace :// to /
  full_url="${full_url/:\/\///}"
  # replace : to /
  full_url="${full_url/://}"

	IFS='/ ' read -ra array <<< ${full_url}
  host=${array[1]}
  port=${array[2]}
  service=${array[3]}
  local method=${array[4]}
  printf "  benchmarking: ${host}:${port}/${service}/${method}\n"
}

# check whether specified port is available
function validate_thrift_server(){
  local host=$1
  local port=$2
  local service=$3
  nc -zw5 ${host} ${port} && is_server_started=$? || is_server_started=$?
  if [[ ${is_server_started} -ne 0 ]]; then
    echo "  not started"
    if [[ "${host}" == "127.0.0.1" ]] && [[ "${service}" == "DemoService" ]]; then
      # If user is benchmarking demo-thrift-server
      printf "${__base}: demo thrift server ${host}:${port} seems to be down, make sure to start it before benchmarking\n"
      printf "  [usage] sh demo_thrift_server.sh -p ${port}\n"
    else
      printf "${__base}: thrift server ${host}:${port} seems to be down, make sure to start it before benchmarking\n"
    fi
		exit 1
  else
		echo "  started"
  fi
}

function print_usage_to_newbie(){
  local demo_port=8972
  printf "\
  [Notice] Is it your first time to use ${_tool_name}? Run your first thrift benchmarking in 3 steps:
  1. Rename tsocket.sample.env to thrift.env in conf directory
  2. Run demo thrift server: sh demo_thrift_server.sh -p ${demo_port}
  3. Start to benchmark: sh ${__base}.sh thrift://127.0.0.1:${demo_port}/DemoService/noArgMethod\n"
}

function print_usage_normally(){
  local demo_host="127.0.0.1"
  local demo_port=8972
  local demo_service="DemoService"
  printf "\
Usage: sh ${__base}.sh [options] thrift://<host>:<port>/<service>/<method>[?[@<data_file>]]

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
    sh ${__base}.sh thrift://${demo_host}:${demo_port}/${demo_service}/noArgMethod
    # 2. benchmark at 10 concurrencies for 5 minutes
    sh ${__base}.sh -c 10 -t 5m thrift://${demo_host}:${demo_port}/${demo_service}/noArgMethod
    # 3. benchmark at 10 qps for 2 hours
    sh ${__base}.sh -q 10 -t 2h thrift://${demo_host}:${demo_port}/${demo_service}/noArgMethod
    # 4. specify an environment file
    sh ${__base}.sh -e conf/tsocket.sample.env thrift://${demo_host}:${demo_port}/${demo_service}/method
    # 5. specify arguments
    sh ${__base}.sh thrift://${demo_host}:${demo_port}/${demo_service}/oneArgMethod?@demo/oneArgMethod_args.csv
"
}

function print_usage(){
	local is_newbie=false
	if [[ ! -f ${_default_env_file} ]]; then
		is_newbie=true
	fi

	if [[ ${is_newbie} == true ]]; then
		print_usage_to_newbie
  else
  	print_usage_normally
	fi
}

function start_to_benchmark(){
  local classpath=${_lib_thrift_dir}/${thrift_version}/*:${_lib_dir}/*:${_lib_classes_dir}
  local java_opts="-server -Xmx16G -Xms16G -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled -XX:ErrorFile=$_bin_dir/hs_err_pid%p.log -Xloggc:$_bin_dir/gc.log -XX:HeapDumpPath=$_bin_dir -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError"
  local pid_file="$_bin_dir/pid"
  if [[ ! -s "${pid_file}" ]] || [[ "" == $(cat ${pid_file}) ]] || [[ -z "$(ps -eo pid | grep -w $(cat ${pid_file}))" ]]; then
    java ${java_opts} -cp ${classpath} com.didiglobal.pressir.thrift.Main $* 2>&1
    echo $! > ${pid_file}
  else
    echo "${__base}: tool is ready running, pid=$(cat ${pid_file})"
    exit 1
  fi
}

function main(){
	local env_file=${_default_env_file} concurrency=0 throughput=0 time_limit=0
	local url=""

	# step 1
	while getopts ":c:q:t:e:hv" opt
	do
		case "$opt" in
			c)
				if [[ ${throughput} -gt 0 ]]; then
						echo "${__base}: only one of -c or -q should be specified"
						print_usage_normally
						exit 1
				fi
				concurrency="$OPTARG"
				;;
			q)
				if [[ ${concurrency} -gt 0 ]]; then
						echo "${__base}: only one of -c or -q should be specified"
						print_usage_normally
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
				print_usage_normally
				exit 1
				;;
			v)
				printf "This is ${_tool_name}, version ${_tool_version}\n"
				exit 1
				;;
			*)
				printf "${__base}: illegal option ${OPTARG}\n"
				print_usage
				exit 1
				;;
			esac
	done

	# step 2
	validate_tool_dir ${_bin_dir} ${_conf_dir} ${_lib_dir} ${_lib_classes_dir} ${_lib_thrift_dir}

	# step 3
	shift $(($OPTIND - 1))
	url=$1
	validate_and_parse_url ${url}

	# step 5
	validate_env_file ${env_file}

	# step 6
	validate_thrift_server ${host} ${port} ${service}

	# step 7
	declare params="-e ${env_file} -u ${url}"
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
		params="$params -t 60s"
	fi
	start_to_benchmark ${params}
}

main "${@}"
