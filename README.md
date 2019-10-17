# benchark-thrift
**benchmark-thrift**是一款测试`Thrift`应用程序性能的工具，开箱即用，高效简单。
> [README in English](README_EN.md)
#### 主要特点
 * 使用简单：下载后，支持命令行发压 
 * 功能丰富：支持多个版本的Thrift协议，支持多种TProtocol及TTransport
 * 压力模型：支持并发数、吞吐量两种压力模型  
# 下载与安装
#### 环境说明
已在Mac、Centos等环境上多次测试，但其他环境上测试尚不充分，暂不支持Windows。
需要JDK 8或更高版本的Java运行环境。
#### 下载方法
[点击这里](http://XXX "Download")下载最新版本，或者通过`wget`、`curl`命令：
     
```bash
$ curl -O http://XXX
```
下载完成后，在合适的目录解压缩即可。
**`本文档后见的<TOOL_HOME>，表示该工具的安装目录`**

# 快速上手(运行第一个thrift压测)
使用工具自带的例子，您只需三步，就可以运行第一个Thrift压测。
1. 创建环境文件：去conf目录，从已有模板拷贝：
	```bash
	$ cd <TOOL_HOME>/conf
	$ cp thrift_tsocket_sample.env thrift.env
	```
2. 启动被测服务：工具提供了一个样例Thrift Server，可以一键启动： 
	```bash
	$ cd <TOOL_HOME>/demo
	# 可以后台启动样例服务，如果后台启动，请在结束时自行关闭
	$ sh demo_thrift_server.sh -p 8972 
	```
3. 启动压测工具，进行压力测试
	```bash
	$ cd <TOOL_HOME>/bin
	# 一个最简单的Thrift方法，不含任何参数
	$ sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
	# 或者一个带参数的Thrift方法，需要指定数据文件
	#$ sh benchmark.sh thrift://127.0.0.1:8972/DemoService/oneArgMethod?@../demo/data/oneArgMethod.text
	# 或者手工指定配置文件
	#$ sh benchmark.sh -e ../conf/thrift_socket_sample.env thrift://127.0.0.1:8972/DemoService/noArgMethod
	```

# 使用说明
运行之前，请确保对[Thrift协议](https://thrift.apache.org/tutorial/)有一定的了解。Thrift远程调用需要匹配版本、TTranport、TProtocol类型，调用方还要拿到SDK（Jar、go module、或者IDL文件），相比HTTP协议，更为复杂。

为简化操作，抽取了"环境文件"的概念，包含不常变化的Thrift版本、TTransport及TProtocol类型等配置项。

#### 准备SDK
Thrift调用需要待测服务的SDK，本工具使用Java开发，因此需要准备Jar包。如果Jar包已生成，请忽略本小节。否则请自行生成或参考本工具提供的Jar生成器生成Jar包，具体操作如下：

```bash
# 第一步：生成Java源码，执行后会在当前路径下生成`gen-java`文件夹
$ thrift -r --gen java /xxx/xxx.thrift 
# 第二步：通过Jar生成器生成Jar包，三个参数分别是: 1. Thrift版本；2. java源码路径(绝对路径)；3. jar包的位置和名称
$ sh <TOOL_HOME>/bin/jar_generator.sh <thrift_version> <java_path> <jar_path> 
# 示例: sh jar_generator.sh 0.11.0 /xxx/xxx/gen-java xxx.jar
```
**`注：以后工具会提供只需IDL文件、免SDK的功能。`**

#### 准备环境文件
工具默认会读取conf/thrift.env环境文件，您也可以通过`-e <environment file>`手工指定。conf目录下提供了几个样例环境文件，推荐在样例的基础上进行适当修改：
```bash
$ cd <TOOL_HOME>/conf
# 第一步：样例文件拷贝为thrift.env
$ cp xxx_sample.env thrift.env
# 第二步：检查并修改文件内容，其中client_jar为准备SDK阶段得到的Jar包
$ vim thrift.env
```

#### 启动工具 
环境文件准备完毕，待测服务也在运行，可以通过命令进行压测，示例如下：  
```bash
$ cd <TOOL_HOME>/bin
$ sh benchmark.sh [options] thrift://<host>:<port>/<service>/<method>[?@<data_file>]
#示例: sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
```

# 启动参数选项
下面是命令行启动参数及用法说明，也可以通过`sh benchmark.sh -h`进行了解。 
 * ###### -e 环境文件，包括TTransport、TProtocol、[client_jar](#准备SDK)等。若未指定，则默认读取`conf`目录下的`thrift.env`
    ```bash
    #环境文件内容示例:     
    version=0.11.0  
	client_jar=../demo/lib/demo-thrift-server-0.0.1.jar
    transport=TSocket  
    protocol=TBinaryProtocol 
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
 * ###### -t 持续时间 如果不指定，默认压测持续60秒。
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
 * ###### Where: data_file表示为一个包含方法参数的本地文件，通过使用`@`识别为文件，如果目标服务的方法含有参数，那么必须指定参数文件，文件路径为绝对路径。`文件内容应该为一行表示方法的一个参数`
    ```bash
    #示例: 假设方法有四个参数，类型分别为i32、string、list<i32>以及struct，文件内容形式应为
    2019
    happy new year
    [2,0,1,9]
    {"key":"value"}
    ```

# FAQ
1. `-e <environment file>`指定环境文件时，是相对路径还是绝对路径?  
	答：二者均可，如果是相对路径，是相对于当前目录
2. 环境文件中指定client_jar包时，是相对路径还是绝对路径?  
	答：二者均可，如果是相对路径，是相对于该环境文件所在的目录
3. 在执行启动命令时为什么出现`no matches found: thrift://xxx/xxx/xxx/xx?@xxx`?  
    答：是因为Thrift url中的?需要转义，请用`\?`替换掉?

# 贡献

请参阅[贡献指南](CONTRIBUTING.md)。

# 许可证

benchmark-thrift是根据ApacheLicense2.0授权的。请参阅[许可证文件](LICENSE)。