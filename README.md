# Introduction  
> [中文版](README_CN.md)  

**Benchmark-thrift** is an open-source application designed to load test thrift applications. It's an out-of-box, efficient and simple usage tool.

# Features  
   * Simple Usage: you can use it through the command line after download
   * Rich features: multiple versions of Thrift protocol are supported, as well as multiple TProtocol and TTransport
   * Pressure model: support concurrency, throughput pressure models

# Download and install

#### Environment
It has been tested more on Mac, Centos. But test on Ubuntuand is not enough. Windows is not supported yet.  
A Java runtime environment of JDK 8 or higher is required.

#### Download
[Click here](www.baidu.com) to download the latest version, or from the command line:
```bash
curl -0 http://xxxx
```
Once the download is complete, unzip it.

# How to run it
lease make sure you have some knowledge of [thrift](www.apache). `Note: In this document, without special explanation, <TOOL_HOME> means the installation directory of the tool`

#### Prepare jar
To send the Thrift request in Java, We need to generate the jar using the idl.

###### Convert the idl to .java file. It will generate gen-java folder under current path after run the command

```bash
    thrift -r --gen java /xxx/xxx.thrift
```    
###### Package the result generated in the previous step through the script provided by the tool (under the bin directory), and attach the package result to the configuration file as shown in the next step    
```bash
    cd <TOOL_HOME>/bin
    # The meanings of the three parameters are: 1. thrift_version: Thrift version 2. java_path: specify the path of Java folder (absolute path); 3. jar_path: specify the location and name of the output jar package
    sh jar_generator.sh version java_path jar_path  
    # Example: sh jar_generator.sh 0.11.0 /xxx/xxx/gen-java xxx/xxx/xxx.jar
```        
#### Prepare configuration file
If you are using the tool for `the first time`, the following steps are recommended to prepare the configuration file. If you know how to prepare the configuration file, you can `skip this stage and specify the configuration file via -e`
###### Sample configuration files are provided under the conf directory, and you can use either and rename it thrift.env
```bash
    cd <TOOL_HOME>/conf
    cp xxx_sample.env thrift.env
```
###### Modify file contents. Check the transport, protocol, and [client_jar](# Prepare jar). Where client_jar is the jar file obtained in Prepare jar phase
```bash
vim thrift.Env
```
```bash
Example of configuration file content

version=0.11.0

# client_jar is the jar file obtained in Prepare jar phase

client_jar=/xxx/xxx/xxx.jar

transport=TSocket

protocol=TBinaryProtocol
```

#### Simplest Usage  
```bash
    sh benchmark.sh [options] thrift://<host>:<port>/<service>/<method>[?@<data_file>]
```

#### OPTIONS
   The options are:     
   * -e thrift.env   
   Thrift environment configuration file include Transport, Protocol, Thrift version, and the location of the generated jar package. 
        * ###### Example  
         version=0.12.0  
         client_jar=/Users/didi/test.jar        
         transport=TFramedTransport(transport=TSocket)  
         protocol=TCompactProtocol        
   * -c concurrency    
   The number of multiple requests to make at a time. If no -c nor -q is specified, default value is 1 concurrency
   * -q throughput  
   The number of requests issued in 1 Second. If no -c nor -q is specified, default value is 1 concurrency
   * -t timelimit  
   TimeLimit of the pressure. It with a default value 60s. You can specify the duration in the following ways:
   
         -t 10[s[econd[s]]] or -t 10[m[inute[s]]] or -t 1[h[hour[s]]] or -t 1[d[day[s]]]
   * -v     
   Print version number
   * -h  
   Display usage information  
   * Where: <data_file>      
         A local file that contains request arguments, prefixed by a "@".  
         If the thrift method has parameters, <data_file> is mandatory.
   

## Contributing
Welcome to contribute by creating issues or sending pull requests. See [CONTRIBUTING](CONTRIBUTING.md) for guidelines.

## License
Benchmark-thrift is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file.

## Note
This is not an official Didi product (experimental or otherwise), it is just code that happens to be owned by Didi.

Thank you for using Benchmark-thrift.

