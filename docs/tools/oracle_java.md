# JAVA安装
FISCO BCOS中需要使用Oracle JDK 1.8 (java 1.8) 环境, 在CentOS/Ubuntu中默认安装或者通过yum/apt安装的JDK均为openJDK, 并不符合使用的要求, 本文是一份简单的Oracle Java 1.8的安装教程.

假设安装目录为/usr/local/
## 下载.  
[[JAVA 1.8下载连接]](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)  
选择**jdk-8u181-linux-x64.tar.gz**下载.

##  上传解压.  
将下载的jdk-8u181-linux-x64.tar.gz上传至服务器, 放入/usr/local/目录, 然后解压.
```
$ tar -zxvf jdk-8u181-linux-x64.tar.gz
$ cd jdk1.8.0_181 && pwd
$ /usr/local/jdk1.8.0_181
```

##  配置环境变量 
- 全局安装, 所有用户均生效  
将下面的内容添加入 /etc/profile 文件的末尾. 
```
vim /etc/profile
```
```
export JAVA_HOME=/usr/local/jdk1.8.0_181  
export PATH=$JAVA_HOME/bin:$PATH  
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```
添加完毕之后执行:
```
$ source /etc/profile
```

- 用户环境变量配置, 当前用户有效  
将下面的内容添加到 ~/.bash_profile 文件的末尾。 若无法修改 ~/.bash_profile 文件，则将下面的内容添加到 ~/.bashrc 文件的末尾。
```
vim ~/.bash_profile 
或  vim ~/.bashrc
```
```
export JAVA_HOME=/usr/local/jdk1.8.0_181  
export PATH=$JAVA_HOME/bin:$PATH  
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```
添加完毕之后执行:
```
$ source ~/.bash_profile 
若添加的内容到~/.bashrc文件，则执行 $ source ~/.bashrc 
```

##  检查java版本  
```
$ java -version
$ java version "1.8.0_181"
$ Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
$ Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)
```
安装成功！
