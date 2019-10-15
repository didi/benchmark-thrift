# Introduction  
**Benchmark-Thrift** is an open source application designed to load test Thrift applications. 
> [中文版](README.md)  

#### Features  
  * **Out of the box**, just download a latest version and start to benchmark in command line
  * **Multiple Thrift versions** are supported, as well as different TProtocol and TTransport types
  * Both **concurrency** and **throughput** modes are available

# Download and Install

#### Environment
It has been tested a lot on Mac and Centos, but not on other operating systems. Windows is not supported yet. JDK 8 or higher version is required.

#### Download
[Click here](www.baidu.com) to download the latest version, or use `wget` or `curl` command:
```bash
$ curl -0 http://xxxx
```
Once the download is complete, unzip it.

# How to run it
Please visit [this tutorial](https://Thrift.apache.org/tutorial/) if you are new to Thrift protocol. A Thrift RPC needs a lot to configure(version, TTranport type, TProtocol type and even a SDK), which makes it more complicated than other protocols such as HTTP.  
To simplify, "environment file" is introduced, which contains configurations that don't changed often, such as Thrift version, TTransport type and TProtocol type.  
**`Note: In this document, without special explanation, <TOOL_HOME> means the installation directory of the tool`**

#### Prepare SDK
Firstly a SDK is required to make a Thrift call, and specifically it's a **jar file** as the tool is developed by Java. You cann ignore this section if you already have one, otherwise, please generate it manually or use `jar_generator.sh`, as below:
```bash
# 1. Generate the Java source code. After execution, generate the `gen-java` folder under the current path
$ Thrift -r --gen java /xxx/xxx.Thrift    
# 2. Generate the Jar package through the Jar generator script which has three parameters: 1. Thrift version; 2. Java source code path (absolute path); 3. Location and name of the jar package
$ sh <TOOL_HOME>/bin/jar_generator.sh version java_path jar_path  
# Example: sh jar_generator.sh 0.11.0 /xxx/xxx/gen-java xxx/xxx/xxx.jar
```        
#### Prepare environment 
As mentioned before, this tool will read an environment file `conf/Thrift.env` in default, or you could manually specify it by `-e <environment file>`. 

Several samples are provided in the conf directory, and it is recommended to use samples:
```bash
$ cd <TOOL_HOME>/conf
# 1.  Copy a sample file and name it to `Thrift.env`
$ cp xxx_sample.env Thrift.env
# 2. Check and modify the contents.（The `client_jar` means the location of the jar package which has been prepared in 'Prepare SDK' stage）
$ vim Thrift.env
```
#### Start the tool
Once the SDK and environment file are ready, start to benchmark as below:  
```bash
$ cd <TOOL_HOME>/bin
$ sh benchmark.sh [options] Thrift://<host>:<port>/<service>/<method>[?@<data_file>]
# Example: sh benchmark.sh Thrift://127.0.0.1:8972/DemoService/noArgMethod
```

# Command line options
Below is a description of the command-line startup parameters, it can also be shown by `sh jar_generator.sh -h`
  * ###### -e environment file
    Mainly including TTransport, TProtocol and client_jar. If not specified, the tool wil regard Thrift.env in the conf directory as the default configuration file   
    ```bash   
    # Example of content:
    version=0.12.0
    client_jar=/Users/didi/test.jar
    transport=TFramedTransport(transport=TSocket)
    protocol=TCompactProtocol 
    ```
    
   * ###### -q throughput 
        The number of requests issued per second.  
        ```bash
        # Example: send 100 requests per second
        -q 100
        ```
   * ###### -c concurrency 
        The number of multiple requests to make at one time. If neither -q nor -c is specified, the default value is 1 concurrency.
        ```bash
        # Example: 10 concurrency
        -c 10
        ```
   * ###### -t timelimit 
        If this parameter is not specified, the test will last 60 seconds by default.
        ```bash
        # Example: 3 seconds
        -t 3s or -t 3
        # Example: 3 minutes
        -t 3m
        # Example: 3 hours
        -t 3h
        # Example: 1 day
        -t 1d
        ```
   * ###### -v Print version number
   * ###### -h Display usage information  
   * ###### Where: <data_file>
        A local file that contains request arguments, prefixed by a "@". If the Thrift method has parameters, <data_file> is necessary.
        ```bash
        # Example: suppose the method has four arguments of type i32, string, list, and struct. so the file content should be in the form of
        2019
        Happy New Year
        [2,0,1,9]
        {"key":"value"}
        ```
# Quick Start
With a demo Thrift server supplied by the tool, you could run your first Thrift load test in just three steps.
1. Create an environment file, copying one from samples is suggested:
    ```bash
    $ cd <TOOL_HOME>/conf
    $ cp Thrift_tsocket_sample.env Thrift.env
    ```
2. Start the demo Thrift Server.
    ```bash
    $ cd <TOOL_HOME>/demo
    $ sh demo_Thrift_server.sh -p 8972 
    ```
3. Start the tool to benchmark.
    ```bash
    $ cd <TOOL_HOME>/bin
    # the simplest Thrift method, no arguments
    $ sh benchmark.sh Thrift://127.0.0.1:8972/DemoService/noArgMethod
    # a Thrift method with arguments, you need to specify the data file
    #$ sh benchmark.sh Thrift://127.0.0.1:8972/DemoService/oneArgMethod?@../demo/data/oneArgMethod.text
    # specify the configuration file 
    #$ sh benchmark.sh -e ../conf/Thrift_socket_sample.env Thrift://127.0.0.1:8972/DemoService/noArgMethod
    ```
# FAQ
1.  Q: Is it an absolute path or relative path when specifying environment file by `-e <environment file>` parameter?  
    A: Both are OK. The relative path is based on its relation to `benchmark.sh` file. 
2.  Q: Is it an absolute path or relative path when specifying client_jar in the environment file?  
    A: Both are OK. The relative path is based on its relation to the environment file. 
3.  Q: When starting the tool, why an error which like **`no matches found: Thrift://xxx/xxx/xxx/xx?@xxx`** occured?   
    A: May be caused by the ? unable to identify in Thrift url, you can use `\?` to replace ? .
# Contributing
Welcome to contribute by creating issues or sending pull requests. See [CONTRIBUTING](CONTRIBUTING.md) for guidelines.

# License
Benchmark-Thrift is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file.


