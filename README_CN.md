## Benchark-thrift简介
**Benchmark-thrift**是一款测试`Thrift`应用程序性能的工具，开箱即用，高效简单。
> [README in English](README.md)
>#### 工具特点
> * 简单的使用方式：使用者只需要在命令行输入简单的启动命令，就可以对目标服务进行测试。工具不需要使用者有代码开发能力 
> * 全量Thrift版本支持：工具支持截止到目前所有的Thrift版本，使用者只需要修改Thrift环境配置文件，就可以完成Thrift版本的切换  
> * 两种类型的压力：工具不仅支持模拟并发的方式进行性能测试，还支持以固定吞吐量的形式进行对目标服务的性能考量  
## 下载与安装
> #### 环境要求
>> ##### 1.系统环境 
>> 支持Mac、Ubuntu和Centos，其他环境未完整测试，如果问题请联系xxx
>> ##### 2.JAVA环境
>> 工具是使用Java语言编写的，如果想工具正常运行，必须确保工具所在的机器上已经安装了Java 8或更高版本的Java运行环境。可以通过命令查看是否安装Java以及Java的版本信息
>>```bash
>>java -version  #如果本地的Java版本低于Java 8，请先升级本地Java版本或者下载更高版本 https://www.oracle.com/technetwork/java/javase/downloads/index.html
>>```
> #### 下载 
>```bash
>git clone xxxx
>```
> #### 安装
>```bash
>unzip benchmark-thrift-1.0-SNAPSHOT.zip
>```
## 如何运行
请确保已阅读[环境要求](#环境要求)的相应内容：
**`本文档中的<TOOL_HOME>不作特殊解释的话，均表示为工具的安装目录`**
> #### 准备jar
>Thrift是一种接口描述语言和二进制通讯协议，用来定义和创建跨语言的远程服务。在Java中，一般需要提供通过idl生成的jar包。工具提供了一个脚本，方便使用者将idl转化为工具所需要的jar包
>> ##### 1.通过命令将idl转化为java文件,执行后会在当前路径下生成gen-java文件夹
>>```bash
>>thrift -r --gen java /xxx/xxx.thrift 
>>```
>> ##### 2.通过脚本(工具`bin`目录下)将java文件进一步打包为jar文件
>>```bash
>># 三个参数含义分别是: 1、thrift_version: 指定Thrift版本；2、java_path:指定java文件夹路径(绝对路径)；3、jar_path:指定输出jar包的位置和名称
>>cd <TOOL_HOME>/bin
>># 示例: sh jar_generator.sh 0.11.0 /xxx/xxx/gen-java /xxx/xxx/xxx.jar
>>sh jar_generator.sh <thrift_version> <java_path> <jar_path> 
>>```
> #### 准备配置文件
>如果`第一次`使用工具，推荐您修改使用`<TOOL_HOME>/conf`下的样例，方式如下。如果`已经知悉`工具使用方式，您可以`跳过此阶段并通过-e`的方式来指定使用的环境配置文件
>> ##### 1.复制一个样例，并将其命名为`thrift.env`。我们以thrift_tsocket_sample.env为例
>>```bash
>>cd <TOOL_HOME>/conf
>>cp thrift_tsocket_sample.env thrift.env
>>```
>> ##### 2.根据实际情况修改内容。主要检查`transport`、`protocol`、以及[client_jar](#准备jar)是否配置正确。
>>```bash
>>vim thrift.env
>>```
> #### 启动工具
>```bash
>cd <TOOL_HOME>/bin
>#示例: sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
>sh benchmark.sh [options] thrift://<host>:<port>/<service>/<method>[?@<data_file>]
>```
>>##### 启动参数选项
>> * ###### -e Thrift环境配置文件，主要包括TTransport、TProtocol、[Client_jar](#准备jar)的配置。如果`没有指定该参数，会以工具conf目录下的thrift.env为默认配置文件`
>>>     配置文件内容示例:     
>>>     version=0.12.0  
>>>     client_jar=/users/didi/test.jar  
>>>     transport=TFramedTransport（transport=tSocket）  
>>>     protocol=TCompactProtocol
>> * ###### -c 并发度 模拟多少个线程同时发送请求，如果并发度和吞吐量都不指定，会默认采用1个并发进行测试
>> * ###### -q 吞吐量 一秒内发出的请求数
>> * ###### -t 持续时间 如果没有通过参数指定，系统默认按照60秒的时长进行测试。2或2s等同于2秒, 2m等同于2分钟, 2h等价于2小时
>> * ###### -v 打印版本号
>> * ###### -h 打印帮助信息
>> * ###### Where: data_file表示为一个包含方法参数的本地文件，通过使用`@`识别为文件，如果目标服务的方法含有参数，那么必须指定参数文件。`文件内容应该为一行表示方法的一个参数`。如果是参数类型为struct，需要使用json形式
>>>     示例:假设方法有四个参数，类型分别为i32、string、list<i32>、以及struct，那么文件内容为
>>>     2019
>>>     happy new year
>>>     [2,0,1,9]
>>>     {"key":"value"}
## 快速上手(启动示例)
```bash
cd <TOOL_HOME>/conf
# 复制样例并修改名称。因为样例内容是我们按照Demo设计的，所以不需要改变内容。如果想对您指定的服务进行测试，需要根据实际情况来改变内容
cp thrift_tsocket_sample.env thrift_env
cd ../demo
# 通过脚本启动Demo服务
sh demo_thrift_server.sh 8972 
cd ../bin
# 1.如果不指定配置文件，工具默认使用上步修改的<TOOL_HOME>/conf/thrift.env作为配置文件。如果不想使用该文件，可以通过-e的方式自己指定配置文件
# 指定配置文件示例: sh benchmark.sh -e <TOOL_HOME>/conf/thrift_tsocket_sample.env thrift://127.0.0.1:8972/DemoService/noArgMethod
# 2.由于DemoService的noArgMethod方法没有参数，所以不需要指定参数文件地址。但如果目标服务的方法有参数，需要通过?@指定参数文件地址
# 带参方法示例: sh benchmark.sh thrift://127.0.0.1:8972/DemoService/oneArgMethod?@<TOOL_HOME>/demo/data_file_demo/oneArgMethod.text
sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
```

## 贡献

请参阅[贡献指南](CONTRIBUTING.md)。

## 许可证

Benchmark-thrift是根据ApacheLicense2.0授权的。请参阅[许可证文件](LICENSE)。

## Note
这不是一个正式的滴滴产品，它只是碰巧代码属于滴滴。

感谢您使用这款工具!
