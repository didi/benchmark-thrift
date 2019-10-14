# Benchark-thrift简介
**Benchmark-thrift**是一款测试`Thrift`应用程序性能的工具，开箱即用，高效简单。
> [README in English](README1.md)
#### 主要特点
 * 使用简单：下载后，通过命令行即可使用 
 * 功能丰富：支持多个版本的Thrift协议，支持多种TProtocol及TTransport
 * 压力模型：支持并发数、吞吐量两种压力模型  
# 下载与安装
#### 环境说明
已在Mac、Centos等环境上多次测试，但其他环境上测试尚不充分，暂不支持Windows。
需要JDK 8或更高版本的Java运行环境。
#### 下载方法
[点击这里](http://XXX "Download")下载最新版本，或者通过命令行：
     
```bash
curl -O http://XXX
```
下载完成后，解压缩即可。

# 如何运行
请确保对[Thrift协议](https://thrift.apache.org/tutorial/)有一定的了解。
**`本文档中的<TOOL_HOME>不作特殊解释的话，均表示为工具的安装目录`**
#### 准备jar
在Java中发送Thrift请求需要用idl生成jar包。
1. 将idl转化为java文件，执行后会在当前路径下生成`gen-java`文件夹
	```bash
	thrift -r --gen java /xxx/xxx.thrift 
	```
	
2. 通过工具提供的脚本(`bin`目录下)将上步生成的`gen-java`打包，将打包结果配到配置文件中 [详见下一步](#准备配置文件)
	```bash
	cd <TOOL_HOME>/bin
	# 三个参数含义分别是: 1、thrift_version: 指定Thrift版本；2、java_path:指定java文件夹路径(绝对路径)；3、jar_path:指定输出jar包的位置和名称
	sh jar_generator.sh <thrift_version> <java_path> <jar_path> 
	# 示例: sh jar_generator.sh 0.11.0 /xxx/xxx/gen-java /xxx/xxx/xxx.jar
	```

#### 准备配置文件
如果是`第一次`使用工具，推荐按下述步骤准备配置文件。如果`知悉`配置方式，可以`跳过此阶段通过-e`指定配置文件
1. 工具`conf`下提供了配置文件样例，您可以使用任意一个，并重命名为`thrift.env`。
	```bash
	cd <TOOL_HOME>/conf
	cp xxx_sample.env thrift.env
	```
2. 修改文件内容。检查`transport`、`protocol`、以及[client_jar](#准备jar)是否正确。其中`client_jar为准备jar阶段得到的jar包`
	```bash
	vim thrift.env
	```
	```bash   
	# 文件内容示例
	version=0.11.0
	# client_jar为准备jar阶段得到的jar包
	client_jar=/xxx/xxx/xxx.jar  
	transport=TSocket  
	protocol=TBinaryProtocol  
	```
#### 启动工具 

**Note:如果执行启动命令出现no matches found: thrift://xxx/xxx/xxx/xxx?@xxxx错误, 可能是由于?无法识别，需要将`?替换为\?`**

```bash
cd <TOOL_HOME>/bin
sh benchmark.sh [options] thrift://<host>:<port>/<service>/<method>[?@<data_file>]
#示例: sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
```

##### 启动参数选项
 * ###### -e 配置文件，主要包括TTransport、TProtocol、[client_jar](#准备jar)。如果没有指定，以工具`conf`目录下的`thrift.env`为默认配置文件
    ```bash
    #配置文件内容示例:     
    version=0.12.0  
    client_jar=/users/didi/test.jar  
    transport=TFramedTransport(transport=tSocket)  
    protocol=TCompactProtocol
    ```    
 * ###### -q 吞吐量 QPS 
   ```bash
    #示例: 1秒发送100个请求
    -q 100
    ``` 
 * ###### -c 并发度 如果QPS和并发度都不指定，默认按1个并发进行测试 
    ```bash
    示例: 10个并发度
    -c 10
    ``` 
 * ###### -t 持续时间 如果不指定该参数，默认按照60秒时长进行测试。
    ```bash
    #示例: 3秒
    -t 3s 或者 -t 3
    #示例: 3分钟
    -t 3m
    #示例: 3小时
    -t 3h
    #示例: 1天
    -t 1d
    ``` 
 * ###### -v 打印版本号
 * ###### -h 打印帮助信息
 * ###### Where: data_file表示为一个包含方法参数的本地文件，通过使用`@`识别为文件，如果目标服务的方法含有参数，那么必须指定参数文件。`文件内容应该为一行表示方法的一个参数`
    ```bash
    #示例: 假设方法有四个参数，类型分别为i32、string、list<i32>以及struct，文件内容形式应为
    2019
    happy new year
    [2,0,1,9]
    {"key":"value"}
    ```

# 快速上手(运行第一个thrift压测)
使用工具自带的例子，您只需三步，就可以运行第一个Thrift压测。
1. 创建环境文件：去conf目录，从已有模板拷贝：
	```bash
	cd <TOOL_HOME>/conf
	cp thrift_tsocket_sample.env thrift_env
	```
2. 启动被测服务：工具提供了一个样例Thrift Server，可以一键启动： 
	```bash
	cd <TOOL_HOME>/demo
	sh demo_thrift_server.sh -p 8972 
	```
3. 启动压测工具，进行压力测试
	```bash
	cd <TOOL_HOME>/bin
	# 一个最简单的Thrift方法，不含任何参数
	sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
	# 或者一个带参数的Thrift方法，需要指定数据文件
	# sh benchmark.sh thrift://127.0.0.1:8972/DemoService/oneArgMethod?@../demo/data/oneArgMethod.text
	# 或者手工指定配置文件
	# sh benchmark.sh -e ../conf/thrift_socket_sample.env thrift://127.0.0.1:8972/DemoService/noArgMethod
	```
# 贡献

请参阅[贡献指南](CONTRIBUTING.md)。

# 许可证

Benchmark-thrift是根据ApacheLicense2.0授权的。请参阅[许可证文件](LICENSE)。