#!/bin/bash

function gen_classpath(){
    for element in `ls $LIB_DIR`
    do
        jarfile=$LIB_DIR/$element:$jarfile
    done
    jarfile=${jarfile%:*}
}

function getdir(){
    for element in `ls $1`
    do
        dir_or_file=$1"/"$element
        if [ -d $dir_or_file ]
        then
            getdir $dir_or_file
        else
            JAVA_FILES="$dir_or_file $JAVA_FILES"
        fi
    done
}

declare -r HOME_DIR=$(cd $(dirname $0); cd ..; pwd)
declare -r LIB_DIR="${HOME_DIR}/lib/thrift/$1"
declare JAVA_FILES=""
declare -r BASE_DIR=$(pwd)

if [[ ! -d ${LIB_DIR} ]]; then
    echo "Thrift版本错误"
    exit 1
fi


if [[ $# < 3 ]]; then
    echo "参数不对，第一个参数为version，第二个参数java路径，第三个参数生成的jar包位置和名称"
    exit 1
fi



if [[ $3 == "" || $3 != *.jar ]]; then
    echo "输入正确的jar路径，确保以.jar结尾"
    exit 1
fi

jar_path=${3}
if [[ ${3} != "/"* ]]; then
      jar_path=${BASE_DIR}/$3
fi

if [[ -d classdir ]]; then
    rm -rf classdir
fi
mkdir classdir

gen_classpath $1
getdir $2

echo "version   -> $1"
echo "java path -> $2"
echo "jar path  -> $jar_path"
echo "classpath -> $jarfile"

javac -classpath $jarfile -d classdir $JAVA_FILES > /dev/null

cd classdir
jar -cvf $jar_path * > /dev/null
cd ..
rm -rf classdir
