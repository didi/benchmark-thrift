## Benchark-thrift
**Benchmark-thrift**是一款测试Thrift应用程序性能的工具，开箱即用，高效简单。
> [README in English](README.md)

## 工具特点

 * 简单的使用方式：使用者只需要在命令行输入简单的启动命令，就可以对目标服务进行测试。工具不对使用者有任何代码开发能力要求 
 * 全量Thrift版本支持：工具支持截止到目前所有的Thrift版本，使用者只需要修改Thrift环境配置文件，就可以完成Thrift版本的切换  
 * 两种类型的压力：工具不仅支持模拟并发的方式进行性能测试，还支持以固定吞吐量的形式进行对目标服务的性能考量  

## 环境要求

如果想使用此工具，以下几点是必须要满足的：

 * #### JAVA环境要求：

工具是使用Java语言编写的，如果想工具正常运行，必须确保工具所在的机器上已经安装了Java 8或更高版本的Java运行环境。
```bash
    echo $JAVA_HOME             # 正常情况下应该会输出Java安装目录，如果这条指令失败了，您需要检查机器上是否安装了Java运行环境
    java -version               # 如果本地的Java版本低于Java 8，请先升级本地Java版本或者下载更高版本 https://www.oracle.com/technetwork/java/javase/downloads/index.html

```
* #### idl生成的jar包：

Thrift是一种接口描述语言和二进制通讯协议，用来定义和创建跨语言的远程服务。在Java中，一般需要提供通过接口描述语言生成的jar包来进行服务调用。
工具提供了另一个脚本命令方便使用者将idl文件转化成Java服务所需要使用的jar包。
```bash
    #1、通过命令将.thrift文件转化成相应的java文件,执行后会在当前路径下生成gen-java文件夹
    thrift -r --gen java xxx.thrift 
    #2、通过工具提供的脚本上条命令生成的java夹进一步打包为.jar文件
    # 其中三个参数含义分别是: 1、version: 指定Thrift版本；2、java_path:指定执行完上条命令所生成的java文件夹路径；3、jar_path:指定最终的jar包的位置和名称
    sh jar_generator.sh version java_path jar_path 
```      

## 如何运行

运行前确保已经阅读[环境要求](## 环境要求)的相应内容：

```bash
    # 1、将压缩文件进行解压
    unzip benchmark-thrift-1.0-SNAPSHOT.zip  
    # 2、如果是第一次使用工具，推荐参考并根据需要对conf目录下的环境配置文件样例进行修改。我们提供了两种示例，您可以根据实际情况进行调整。
    # 如果您已经使用过这款压测工具并知悉其运行方式，您可以跳过步骤2并选择使用-e 的方式来指定您想使用的环境配置文件
    cd conf
    # 2.1 选择一个Thrift环境配置文件并且修改相应内容
    vim tsocket.sample.env      
    # 2.2 重命名为thrift.env方便工具进行默认扫描
    mv tsocket.sample.env thrift.env 
    cd ..
    # 3、进入到bin目录下
    cd bin
    # 3.1 修改权限，确保命令是可执行的
    chmod 755 *.sh 
    # 3.2 如果持续时间和压力类型没有指定，会默认按照1个并发的强度进行1分钟测试。如果环境配置文件没有指定(-e filePath)，默认使用conf目录下的thrift.env作为环境配置。
    sh benchmark.sh thrift://127.0.0.1:8972/demoService/noArgMethod 
```

#### 具体用法
```bash
    # 注意 Thrift url 是必不可少且位置不可变更的，应该是启动脚本参数的最后一个
    sh benchmark.sh [options] thrift://<host>:<port>/<service>/<method>[?@<data_file>]
```

#### 参数选项
* -e thrift.env  
 与Thrift相关的配置，包括TTransport、TProtocol、Thrift版本和生成的jar包位置。如果没有指定该参数，工具会默认扫描conf目录下的thrift.env
    * ###### 示例 
        version=0.12.0  
        client_jar=/users/didi/test.jar  
        transport=TFramedTransport（transport=tSocket）  
        protocol=TCompactProtocol
* -c 并发度 模拟多少个线程同时发送请求，如果并发度和吞吐量都不指定，会默认采用1个并发度
* -q 吞吐量 在1秒内发出的请求数
* -t 持续时间 默认值为60s。您可以通过以下方式指定持续时间:
        -t 10[s[econd[s]]] 或者 -t 10[m[inute[s]]] 或者 -t 1[h[hour[s]]] 或者 -t 1[d[day[s]]]
* -v 打印版本号
* -h 显示使用信息
* Where: <data_file>   
一个包含方法参数的本地文件，通过使用@识别为文件信息，如果Thrift方法有参数，必须指定参数文件地址


## 贡献

请参阅[贡献指南](CONTRIBUTING.md)。

## 许可证

Benchmark-thrift是根据ApacheLicense2.0授权的。请参阅[许可证文件](LICENSE)。

## Note
这不是一个正式的滴滴产品，它只是碰巧代码属于滴滴。

感谢您使用这款工具!
