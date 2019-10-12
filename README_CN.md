## Benchark-thrift简介
**Benchmark-thrift**是一款测试`Thrift`应用程序性能的工具，开箱即用，高效简单。
> [README in English](README.md)
#### 主要特点
 * 使用简单：下载后，通过命令行即可使用 
 * 功能丰富：支持多个版本的Thrift协议，支持多种TProtocol及TTransport
 * 压力模型：支持并发数、吞吐量等多种压力模型  
## 下载与安装
#### 环境说明
在Mac、Centos等环境上测试较多，但Ubuntu等环境上测试尚不充分，暂不支持Windows。
需要JDK 8或更高版本的Java运行环境。
#### 下载地址 
[点击这里](http://XXX "Download")下载最新版本，或者通过命令行：
```bash
 curl -O http://XXX
```
下载完成后，解压缩即可。

## 如何运行
请确保对[Thrift协议](https://thrift.apache.org/tutorial/)有一定的了解。
**`本文档中的<TOOL_HOME>不作特殊解释的话，均表示为工具的安装目录`**
#### 准备jar
Thrift应用在Java中，需要idl生成的jar包，因此工具提供了方便将idl转化为jar包的脚本
1. 通过命令将idl转化为java文件,执行后会在当前路径下生成gen-java文件夹
	```bash
	thrift -r --gen java /xxx/xxx.thrift 
	```
	
2. 通过脚本(工具`bin`目录下)将java文件进一步打包为jar文件，后续配到配置文件中
	```bash
	cd <TOOL_HOME>/bin
	# 三个参数含义分别是: 1、thrift_version: 指定Thrift版本；2、java_path:指定java文件夹路径(绝对路径)；3、jar_path:指定输出jar包的位置和名称
	sh jar_generator.sh <thrift_version> <java_path> <jar_path> 
	# 示例: sh jar_generator.sh 0.11.0 /xxx/xxx/gen-java /xxx/xxx/xxx.jar
	```

#### 准备配置文件
如果`第一次`使用工具，推荐使用`<TOOL_HOME>/conf`下的样例，如下操作。如果`已经知悉`使用方式，您可以`跳过此阶段以-e`的方式来指定配置文件
1. 复制一个样例，并将其命名为`thrift.env`。我们以thrift_tsocket_sample.env为例
	```bash
	cd <TOOL_HOME>/conf
	cp thrift_tsocket_sample.env thrift.env
	```
2. 根据实际情况修改内容。主要检查`transport`、`protocol`、以及[client_jar](#准备jar)是否配置正确。其中`client_jar为准备jar阶段生成的jar包`
	```bash
	vim thrift.env
	```
	```bash   
	# 文件内容示例
	version=0.11.0
	# client_jar准备jar阶段生成的jar包
	client_jar=/xxx/xxx/xxx.jar  
	transport=TSocket  
	protocol=TBinaryProtocol  
	```
#### 启动工具 

	```bash
	cd <TOOL_HOME>/bin
	sh benchmark.sh [options] thrift://<host>:<port>/<service>/<method>[?@<data_file>]
	#示例: sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
	```

##### 启动参数选项
 * ###### -e Thrift环境配置文件，主要包括TTransport、TProtocol、[Client_jar](#准备jar)的配置。如果`没有指定该参数，会以工具conf目录下的thrift.env为默认配置文件`
>     配置文件内容示例:     
>     version=0.12.0  
>     client_jar=/users/didi/test.jar  
>     transport=TFramedTransport（transport=tSocket）  
>     protocol=TCompactProtocol
 * ###### -c 并发度 模拟多少个线程同时发送请求，如果并发度和吞吐量都不指定，会默认采用1个并发进行测试
 * ###### -q 吞吐量 一秒内发出的请求数
 * ###### -t 持续时间 如果没有通过参数指定，系统默认按照60秒的时长进行测试。2或2s等同于2秒, 2m等同于2分钟, 2h等价于2小时
 * ###### -v 打印版本号
 * ###### -h 打印帮助信息
 * ###### Where: data_file表示为一个包含方法参数的本地文件，通过使用`@`识别为文件，如果目标服务的方法含有参数，那么必须指定参数文件。`文件内容应该为一行表示方法的一个参数`。如果是参数类型为struct，需要使用json形式
>     示例:假设方法有四个参数，类型分别为i32、string、list<i32>以及struct，那么文件内容为
>     2019
>     happy new year
>     [2,0,1,9]
>     {"key":"value"}

## 快速上手(运行第一个thrift压测)
只需三步，就可以运行第一个Thrift压测。
1. 创建Thrift环境文件，您可以在conf目录下，从已有模板直接拷贝：
	```bash
	cd <TOOL_HOME>/conf
	cp thrift_tsocket_sample.env thrift_env
	```
2. 启动Thrift Server，工具提供了一个样例Thrift Server，可以快速试用： 
	```bash
	cd <TOOL_HOME>/bin
	sh demo_thrift_server.sh -p 8972 
	```
3. 启动压测工具，进行压力测试
	```bash
	cd <TOOL_HOME>/bin
	# 一个最简单的Thrift方法，不含任何参数
	sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
	# 或者一个带参数的Thrift方法，需要指定数据文件
	sh benchmark.sh thrift://127.0.0.1:8972/DemoService/oneArgMethod?@../demo/data_file_demo/oneArgMethod.text
	```

## 贡献

请参阅[贡献指南](CONTRIBUTING.md)。

## 许可证

Benchmark-thrift是根据ApacheLicense2.0授权的。请参阅[许可证文件](LICENSE)。

## Note
这不是一个正式的滴滴产品，它只是碰巧代码属于滴滴。

感谢您使用这款工具!
