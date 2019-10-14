# Introduction  
**Benchmark-thrift** is an open-source application designed to load test thrift applications. It's an out-of-box, efficient and simple usage tool.

> [中文版](README.md)  

#### Features  
  * Simple Usage: you can use it through the command line after download
  * Rich features: multiple versions of Thrift protocol are supported, as well as multiple TProtocol and TTransport
  * Pressure model: support concurrency, throughput pressure models

# Download and Install

#### Environment
It has been tested more on Mac, Centos. But test on other environments is not enough. Windows is not supported yet.  
A Java runtime environment of JDK 8 or higher is required.

#### Download
[Click here](www.baidu.com) to download the latest version, or from the command line:
```bash
curl -0 http://xxxx
```
Once the download is complete, unzip it.

# How to run it
Please make sure you have some knowledge of [thrift](https://thrift.apache.org/tutorial/). **`Note: In this document, without special explanation, <TOOL_HOME> means the installation directory of the tool`**

#### Prepare jar
To send the Thrift request in Java, We need to generate the jar using the idl.

1. Convert the idl to .java files. It will generate gen-java folder under current path after run the command
    ```bash
    thrift -r --gen java /xxx/xxx.thrift
    ```    
2. Package the result generated in the previous step through the script provided by the tool (under the bin directory), and attach the package result to the configuration file as shown in **`[Prepare configuration file]`**  
    ```bash
    cd <TOOL_HOME>/bin
    # The meanings of the three parameters are: 1. thrift_version: Thrift version 2. java_path: specify the path of Java folder (absolute path); 3. jar_path: specify the location and name of the output jar package
    sh jar_generator.sh version java_path jar_path  
    # Example: sh jar_generator.sh 0.11.0 /xxx/xxx/gen-java xxx/xxx/xxx.jar
    ```        
#### Prepare configuration file
If you are using the tool for `the first time`, the following steps are recommended to prepare the configuration file. If you know how to prepare the configuration file, you can `skip this stage and specify the configuration file via -e`
1. Sample configuration files are provided under the conf directory, and you can use either and rename it `thrift.env`
    ```bash
    cd <TOOL_HOME>/conf
    cp xxx_sample.env thrift.env
    ```
2. Modify file contents. Check the `transport`, `protocol`, and `client_jar`. Where `client_jar is the jar file obtained in Prepare jar phase`
    ```bash
    vim thrift.env
    ```
    ```bash
    # Example of configuration file content. 
    version=0.11.0
    # client_jar is the jar file obtained in Prepare jar phase
    client_jar=/xxx/xxx/xxx.jar
    transport=TSocket
    protocol=TBinaryProtocol
    ```
#### Start the tool
**Note: If there is an error which like 'no matches found: thrift://xxx/xxx/xxx/xx?@xxx' in executing the startup command. May be caused by the ? unable to identify, you can use `\?` to replace ?**
```bash
cd <TOOL_HOME>/bin
sh benchmark.sh [options] thrift://<host>:<port>/<service>/<method>[?@<data_file>]
# Example: sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
```
##### Options
  * ###### -e configuration file
    Mainly including TTransport, TProtocol, client_jar. If not specified, take thrift.env in the conf directory as the default configuration file   
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
        The number of multiple requests to make at a time. If neither -q nor -c is specified, the default value is 1 concurrency.
        ```bash
        # Example: 10 concurrency
        -c 10
        ```
   * ###### -t timelimit 
        If this parameter is not specified, it will be tested in 60 seconds by default.
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
        A local file that contains request arguments, prefixed by a "@". If the thrift method has parameters, <data_file> is mandatory.
        ```bash
        # Example: suppose the method has four arguments of type i32, string, list, and struct. so the file content should be in the form of
        2019
        Happy New Year
        [2,0,1,9]
        {"key":"value"}
        ```
# Start quickly
In just three steps, you can run the first Thrift pressure test.
1. Create the configuration file, which you can copy directly from an existing sample in the conf directory:
    ```bash
    cd <TOOL_HOME>/conf
    cp thrift_tsocket_sample.env thrift.env
    ```
2. Start Thrift Server, and the tool provides a sample Thrift Server for a quick trial.
    ```bash
    cd <TOOL_HOME>/demo
    sh demo_thrift_server.sh -p 8972 
    ```
3. Start the pressure measuring tool to conduct the pressure test.
    ```bash
    cd <TOOL_HOME>/bin
    # the simplest Thrift method, no arguments
    sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
    # a Thrift method with arguments, you need to specify the data file
    # sh benchmark.sh thrift://127.0.0.1:8972/DemoService/oneArgMethod?@../demo/data/oneArgMethod.text
    # specify the configuration file 
    # sh benchmark.sh -e ../conf/thrift_socket_sample.env thrift://127.0.0.1:8972/DemoService/noArgMethod
    ```
# Contributing
Welcome to contribute by creating issues or sending pull requests. See [CONTRIBUTING](CONTRIBUTING.md) for guidelines.

# License
Benchmark-thrift is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file.


