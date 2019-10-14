# Introduction  
**Benchmark-thrift** is an open-source application designed to load test thrift applications. It's an out-of-box, efficient and simple usage tool.

> [中文版](README.md)  

#### Features  
  * Simple Usage: you can use it through the command line after download
  * Rich features: multiple versions of Thrift protocol are supported, as well as multiple TProtocol and TTransport
  * Pressure model: support concurrency, throughput pressure models

# Download and Install

#### Environment
It has been tested more on Mac, Centos. But test on other environments is not enough. Windows is not supported yet. A Java runtime environment of JDK 8 or higher is required.

#### Download
[Click here](www.baidu.com) to download the latest version, or from the command line:
```bash
curl -0 http://xxxx
```
Once the download is complete, unzip it.

# How to run it
Please make sure you have some knowledge of [thrift](https://thrift.apache.org/tutorial/). The Thrift remote call needs to match the version, TTranport, TProtocol type and the caller needs to get the SDK (Jar, go module, or IDL file), which make it more complex than the HTTP protocol.  
To simplify the operation, the concept of "environment file" is extracted, including configure items such as  Thrift version, TTransport, and TProtocol type that change infrequently.  
**`Note: In this document, without special explanation, <TOOL_HOME> means the installation directory of the tool`**

#### Prepare SDK
the SDK is required when thrift call occured. Because tool was developed using Java, so you need to prepare the Jar package. If you have it, ignored this section. If not, you can generate the Jar yourself, or refer to the Jar generator provided by this tool, as follows:
```bash
# 1. Generate the Java source code. After execution, generate the `gen-java` folder under the current path
thrift -r --gen java /xxx/xxx.thrift    
# 2. Generate the Jar package through the Jar generator which has three parameters: 1. Thrift version; 2. Java source code path (absolute path); 3. Location and name of the jar package
cd <TOOL_HOME>/bin
sh jar_generator.sh version java_path jar_path  
# Example: sh jar_generator.sh 0.11.0 /xxx/xxx/gen-java xxx/xxx/xxx.jar
```        
#### Prepare environment 
Tool reads the conf/thrift.env as default environment file. Also you can manually specify by `-e environment file`. Several sample environment files are provided in the conf directory, and it is recommended to make appropriate modifications based on the sample:
```bash
cd <TOOL_HOME>/conf
# 1.  Copy a sample file and name it to `thrift.env`
cp xxx_sample.env thrift.env
# 2. Check and Modify the contents. Where client_jar is the jar generate in Prepare SDK
vim thrift.env
```
#### Start the tool
```bash
cd <TOOL_HOME>/bin
sh benchmark.sh [options] thrift://<host>:<port>/<service>/<method>[?@<data_file>]
# Example: sh benchmark.sh thrift://127.0.0.1:8972/DemoService/noArgMethod
```
##### Startup parameter options
The following is a description of the command-line startup parameters and their usage, which can also be understood by `sh jar_generator.sh -h`
  * ###### -e environment file
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
Using the demo which comes from the tool, you can run the first Thrift pressure test in just three steps.
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
# FAQ
1.  Q: When use `-e environment file` to specify environment file, the file path is relative path or absolute path?  
    A: Both are OK. If use relative path, the path should be relatived to the benchmark.sh.
2.  Q: When specify the client_jar in the environment file, the path is relative path or absolute path?  
    A: Both are OK. If use relative path, the path should be relatived to environment file. 
3.  Q: When start the tool, why an error which like **`no matches found: thrift://xxx/xxx/xxx/xx?@xxx`** occured?   
    A: May be caused by the ? unable to identify in thrift url, you can use `\?` to replace ? .
# Contributing
Welcome to contribute by creating issues or sending pull requests. See [CONTRIBUTING](CONTRIBUTING.md) for guidelines.

# License
Benchmark-thrift is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file.


