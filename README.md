##BencharkThrift
**BenchmarkThrift**是一款测试Thrift应用程序性能的工具，提供开箱即用的压测功能。
> [README in English](README_EN.md)

##特点

 * 简洁的使用方式：用户仅仅需要使用简单的命令，就可以实现发压。不需要用户有任何代码开发能力  

 * Thrift版本兼容：工具支持Thrift从0.9.0到0.12.0的所有版本，可以通过修改配置文件完成版本的指定  
 
 * 两种类型的压力：工具不仅支持以并发度的方式进行发压，还可以以固定吞吐量的形式进行测试  

##环境要求

如果想工具运行起来，您需要满足以下条件：

 * ##JAVA环境：

需要一个完全兼容的Java 8运行环境来执行工具。

 * ##idl生成的jar包：

用户需要跟据idl生成相应的jar包，然后将jar路径在配置文件中配置好
```bash
    thrift -r --gen java xxx.thrift #通过命令生成相应的java文件
    sh jar_generate.sh version java_path jar_path  #version: 指定thrift版本，java_path:指定执行完上条命令所生成的java文件夹路径，jar_path:指定最终的jar包的位置和名称
```        

##如何运行

确保已正确配置java运行环境，然后：

```bash
    echo $JAVA_HOME             # 应该打印您的Java home目录。如果命令失败，则需要安装Java环境。Java下载 https://www.oracle.com/technetwork/java/javase/downloads/index.html
    cd benchmark-thrift
    chmod 755 *.sh              # 修改权限，确保命令是可执行的
    sh benchmark.sh -c 10 -D 100s -e thrift.conf 127.0.0.1:8090/Test/test?@dataFile # 如果持续时间和压力类型没有指定，会默认按照1个并发的强度进行1分钟测试
```

####具体用法
```bash
    sh bt.sh [options] thrift://<host>:<port>/<service>/<method>[?@<data_file>]
```

####参数选项

 * -e   

与thrift相关的配置，包括TTransport、TProtocol、thrift版本和生成的jar包位置。如果没有指定该参数，工具会默认扫描conf目录下的thrift.conf文件

* ######示例  
        version=0.12.0  
        classpath=/users/didi/test.jar  
        transport=TFramedTransport（transport=tSocket）  
        protocol=TCompactProtocol 其他选项：TBinaryProtocol，TJSONProtocol
        
*-c 并发度 模拟多少个线程同时发送请求,如果并发度和吞吐量都不指定，会默认采用1个并发度

*-q 吞吐量 在1秒内发出的请求数

*-t 持续时间 默认值为60s。您可以通过以下方式指定持续时间:

        -t 10[s[econd[s]]] 或者 -t 10[m[inute[s]]] 或者 -t 1[h[hour[s]]] 或者 -t 1[d[day[s]]]
        
*-v 打印版本号

*-h 显示使用信息

Where:
   <data_file> 一个包含方法参数的本地文件，通过使用@识别为文件信息,如果thrift方法有参数，此文件为必需配置


##贡献

请参阅贡献指南。

##许可证

Benchmark-thrift是根据ApacheLicense2.0授权的。请参阅许可证文件。

##Note
这不是一个正式的didi产品（实验性的或其他的），它只是碰巧代码属于didi。

感谢您使用这款工具!
