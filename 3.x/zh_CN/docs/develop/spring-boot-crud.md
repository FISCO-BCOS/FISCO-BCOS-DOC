# spring-boot-crud

标签：``spring-boot-crud`` ``开发区块链应用``

---------

### 前置条件

搭建FISCO BCOS区块链，具体步骤[参考这里](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html)。

### 获取源码

```bash
# 直接从github克隆代码
git clone https://github.com/FISCO-BCOS/spring-boot-crud.git

# 若网络很慢，可从gittee克隆代码
git clone https://gitee.com/FISCO-BCOS/spring-boot-crud
```

### 配置节点证书

将节点所在目录`nodes/${ip}/sdk`下的ca.crt、sdk.crt和sdk.key文件拷贝到项目的`src/main/resources/conf`目录下供SDK使用(**FISCO BCOS 2.1以前，证书为ca.crt、node.crt和node.key**):

设节点路径为`~/fisco/nodes/127.0.0.1`，则可使用如下命令拷贝SDK证书:

```bash
# 进入项目路径
$ cd spring-boot-crud
# 创建证书存放路径
$ mkdir src/main/resources/conf
# 拷贝SDK证书
$ cp ~/fisco/nodes/127.0.0.1/sdk/* src/main/resources/conf/
```

### 设置配置文件

`spring-boot-crud`包括SDK配置文件（位于`src/main/resources/applicationContext.xml`路径）和WebServer配置文件(位于`src/main/resources/application.yml`路径)。

需要根据区块链节点的IP和端口相应配置`applicationContext.xml`的`network.peers`配置项，具体如下：

```xml
...
<property name="network">
    <map>
        <entry key="peers">
            <list>
                <!--节点，channel_listen_ip:channel_listen_port-->
                <value>127.0.0.1:20200</value>  
                <value>127.0.0.1:20201</value>
            </list>
        </entry>
    </map>
</property>
...
```
项目中关于SDK配置的详细说明请[参考这里](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/configuration.html)。

WebServer主要配置了监听端口，默认为`45000`，具体如下：

```yml
server:
  #端口号
  port: 45000
```

### 编译和安装项目

可以使用IDEA导入并编译并安装该项目，也可使用提供的`mvnw`脚本在命令行编译项目如下：

```bash
# 编译项目
$ bash mvnw compile

# 安装项目，安装完毕后，在target/目录下生成fisco-bcos-spring-boot-crud-0.0.1-SNAPSHOT.jar的jar包
$ bash mvnw install
```
### 启动spring-boot-crud服务

**方法一：** 

打开IDEA导入并编译该项目，编译成功后，运行`AppApplication.java`即可启动spring boot服务。

**方法二**

使用`bash mvnw install`生成的jar包`target/fisco-bcos-spring-boot-crud-0.0.1-SNAPSHOT.jar`启动spring-boot-crud服务：

```bash
# 启动spring-boot-crud(启动成功后会输出create client for group 1 success的日志)
$ java -jar ./target/fisco-bcos-spring-boot-crud-0.0.1-SNAPSHOT.jar
```

## 访问spring boot web服务
### 访问用户信息上链API(CRUD insert)

`spring-boot-crud`基于CRUD insert接口实现了用户信息上链的API，将`Person`类型的用户信息上链，API声明如下：

```java
@Data
public class Person {
    private String name;
    private String age;
    private String tel;
}
@PostMapping("/insert")
public ResponseData insert(@RequestBody Person person)  {
    crudClient.insert(person.getName(),person.getAge(), person.getTel());
    return ResponseData.success("新增成功");
}
```

**使用curl工具访问接口如下**：

```bash
# 这里假设WebServer监听端口为45000
# 将用户fisco的信息上链，其中name为fisco, age为6，tel为123456789
$ curl -H "Content-Type: application/json" -X POST --data '{"name":"fisco", "age":"6", "tel":"123456789"}' http://localhost:45000/insert
# 返回新增成功的信息
{"code":200,"msg":"新增成功","data":null}
```

### 访问链上查询用户信息API(CRUD select)

`spring-boot-crud`基于CRUD select接口实现了链上查询用户信息的API，基于用户名查询用户信息，API声明如下：

```java
@GetMapping("/query/{name}")
public ResponseData query(@PathVariable("name") String name) throws Exception {
    return ResponseData.success(crudClient.query(name));
}
```

**使用curl工具访问接口如下**：

```bash
# 这里假设WebServer监听端口为45000
# 查询用户名为fisco的用户信息
$ curl http://localhost:45000/query/fisco
# 返回用户fisco的具体信息
{"code":200,"msg":null,"data":{"value1":["fisco"],"value2":["6"],"value3":["123456789"],"size":3}}
```

### 访问用户信息链上更新API(CRUD update)

`spring-boot-crud`基于CRUD update接口实现了用户信息链上更新的API，API声明如下：

```java
@PutMapping("/update")
public ResponseData update(@RequestBody Person person){
    crudClient.edit(person.getName(),person.getAge(), person.getTel());
    return ResponseData.success("修改成功");
}
```

**使用curl工具访问接口如下**：

```bash
# 这里假设WebServer监听端口为45000
# 更新fisco用户的信息，将其age修改为10，tel修改为123
$ curl -H "Content-Type: application/json" -X PUT --data '{"name":"fisco", "age":"10", "tel":"123"}' http://localhost:45000/update
# 返回成功信息
{"code":200,"msg":"修改成功","data":null}

# 再次查询fisco信息：
$ curl http://localhost:45000/query/fisco
# 返回信息如下，用户fisco的age成功修改为10，tel成功修改为123
{"code":200,"msg":null,"data":{"value1":["fisco"],"value2":["10"],"value3":["123"],"size":3}}
```

### 访问用户信息删除API(CRUD delete)

`spring-boot-crud`基于CRUD delete接口实现了用户信息链上删除的API，该API根据用户名删除用户信息，API声明如下：

```java
@DeleteMapping("/remove/{name}")
public ResponseData remove(@PathVariable("name") String name){
    crudClient.remove(name);
    return ResponseData.success("删除成功");
}
```

**使用curl工具访问接口如下**：

```bash
# 这里假设WebServer监听端口为45000
# 删除用户fisco的信息：
$ curl -X DELETE http://localhost:45000/remove/fisco
# 返回删除成功的提示
{"code":200,"msg":"删除成功","data":null}

# 再次查询fisco信息：
$ curl http://localhost:45000/query/fisco
# 此时已经无法查询到用户fisco的信息
{"code":200,"msg":null,"data":{"value1":[],"value2":[],"value3":[],"size":3}}
```

## 贡献代码

- 我们欢迎并非常感谢您的贡献，请参阅[代码贡献流程](https://mp.weixin.qq.com/s/hEn2rxqnqp0dF6OKH6Ua-A)和[代码规范](./CONTRIBUTING_CN.md)。
- 如项目对您有帮助，欢迎star支持！
## 加入我们

**FISCO BCOS开源社区**是国内活跃的开源社区，社区长期为机构和个人开发者提供各类支持与帮助。已有来自各行业的数千名技术爱好者在研究和使用FISCO BCOS。如您对FISCO BCOS开源技术及应用感兴趣，欢迎加入社区获得更多支持与帮助。

![](https://media.githubusercontent.com/media/FISCO-BCOS/LargeFiles/master/images/QR_image.png)

## 相关链接

- 了解FISCO BCOS项目，请参考[FISCO BCOS文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/introduction.html)。
- 了解Java SDK项目，请参考[Java SDK文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html)。
- 了解spring boot，请参考[Spring Boot官网](https://spring.io/guides/gs/spring-boot/)。
