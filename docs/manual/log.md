## 日志操作手册

FISCO BCOS同时支持轻量级日志系统[easylogging++](https://github.com/zuhd-org/easyloggingpp)和功能强大的[boostlog](https://www.boost.org/doc/libs/1_63_0/libs/log/doc/html/index.html)，默认使用boostlog，可通过编译选项`EASYLOG`选择不同的日志系统。


### 开启boostlog

开启boostlog，需将`EASYLOG`选项置为`OFF`，具体操作如下：

```bash
## 进入源码目录，新建build文件夹(设源码位于~/FISCO-BCOS路径)
$ cd ~/FISCO-BCOS && mkdir build

## 关闭EASYLOG编译选项
# ubuntu系统
$ cmake .. -DEASYLOG=OFF
# centos 系统
$ cmake3 .. -DEASYLOG=OFF

------------------------------------------------------------------------
-- Configuring FISCO-BCOS
------------------------------------------------------------------------
-- CMake 3.10.0 (/root/opt/cmake-3.10.0-Linux-x86_64/bin/cmake)
-- CMAKE_BUILD_TYPE Build type                               RelWithDebInfo
-- TARGET_PLATFORM  Target platform                          Linux
-- STATIC_BUILD     Build static                             OFF
-- ARCH_TYPE                                                 OFF
-- TESTS            Build tests                              ON
-- EasyLog          Enable easyLog                           OFF
------------------------------------------------------------------------

-- OpenSSL headers: /usr/include
-- OpenSSL lib   : /usr/lib/x86_64-linux-gnu/libssl.so;/usr/lib/x86_64-linux-gnu/libcrypto.so
-- [cable ] Cable 0.2.11 initialized
-- [cable ] Build type: RelWithDebInfo
-- Configuring done
-- Generating done
-- Build files have been written to: /home/FISCO-BCOS/build

# 编译源码并产生二进制(可根据机器硬件配置选择编译并发度，这里设置并发度为2)
make -j2

# 查看生成的二进制文件
$ ls bin
fisco-bcos
```

### 开启easylogging++

开启easylogging++，需将`EASYLOG`选项置为`ON`，具体操作如下：

```bash
## 进入源码目录，新建build文件夹(设源码位于~/FISCO-BCOS路径)
$ cd ~/FISCO-BCOS && mkdir build

## 关闭EASYLOG编译选项
# ubuntu系统
$ cmake .. -DEASYLOG=ON
# centos 系统
$ cmake3 .. -DEASYLOG=ON

------------------------------------------------------------------------
-- Configuring FISCO-BCOS
------------------------------------------------------------------------
-- CMake 3.10.0 (/root/opt/cmake-3.10.0-Linux-x86_64/bin/cmake)
-- CMAKE_BUILD_TYPE Build type                               RelWithDebInfo
-- TARGET_PLATFORM  Target platform                          Linux
-- STATIC_BUILD     Build static                             OFF
-- ARCH_TYPE                                                 OFF
-- TESTS            Build tests                              ON
-- EasyLog          Enable easyLog                           ON
------------------------------------------------------------------------

-- OpenSSL headers: /usr/include
-- OpenSSL lib   : /usr/lib/x86_64-linux-gnu/libssl.so;/usr/lib/x86_64-linux-gnu/libcrypto.so
-- [cable ] Cable 0.2.11 initialized
-- [cable ] Build type: RelWithDebInfo
-- Configuring done
-- Generating done
-- Build files have been written to: /home/FISCO-BCOS/build

# 编译源码并产生二进制(可根据机器硬件配置选择编译并发度，这里设置并发度设置为2)
make -j2

# 查看生成的二进制文件
$ ls bin
fisco-bcos
```

### 日志配置

#### 配置easylogging++

为了尽量减少配置文件，FISCO BCOS将easylogging++的配置信息都集中到了节点配置文件config.ini的[log]段，一般建议不手动更改除了日志级别设置之外的其他配置，日志级别主要由以下关键字设置：

- INFO-ENABLED：true表明开启INFO级别日志；false表明关闭INFO级别日志
- ERROR-ENABLED：true表明开启ERROR级别日志；false表明关闭ERROR级别日志
- DEBUG-ENABLED：true表明开启DEBUG级别日志；false表明关闭DEBUG级别日志
- TRACE-ENABLED：true表明开启TRACE级别日志；false表明关闭TRACE级别日志
- FATAL-ENABLED：true表明开启FATAL级别日志；false表明关闭FATAL级别日志。

其他配置项包括：

- LOG_PATH：日志文件路径
- GLOBAL-ENABLED：是否采用全局日志配置
- GLOBAL-FORMAT：全局日志格式
- GLOBAL-MAX_LOG_FILE_SIZE：每个日志文件最大容量(默认是200MB)
- GLOBAL-LOG_FLUSH_THRESHOLD：日志刷新频率设置，即每GLOBAL-LOG_FLUSH_THRESHOLD行刷新日志到文件一次

easylogging++的配置示例如下：
```ini
;log configurations
[log]
    ;the directory of the log
    LOG_PATH=./log
    GLOBAL-ENABLED=true
    GLOBAL-FORMAT=%level|%datetime{%Y-%M-%d %H:%m:%s:%g}|%msg
    GLOBAL-MILLISECONDS_WIDTH=3
    GLOBAL-PERFORMANCE_TRACKING=false
    GLOBAL-MAX_LOG_FILE_SIZE=209715200
    GLOBAL-LOG_FLUSH_THRESHOLD=100

    ;log level configuration, enable(true)/disable(false) corresponding level log
    INFO-ENABLED=true
    WARNING-ENABLED=true
    ERROR-ENABLED=true
    DEBUG-ENABLED=true
    TRACE-ENABLED=false
    FATAL-ENABLED=false
```

#### 配置boostlog

FISCO BCOS默认使用boostlog，相较于easylogging++，boostlog配置项很简单，主要如下：

- Level: 日志级别，当前主要包括TRACE/DEBUG/INFO/WARNING/ERROR五种日志级别，设置某种日志级别后，日志文件中会输≥该级别的日志，日志级别从大到小排序`ERROR > WARNING > INFO > DEBUG > TRACE`

- MaxLogFileSize：每个日志文件最大容量

boostlog配置示例如下：

```ini
    ;log level for boost log 
    Level=TRACE
    MaxLogFileSize=1677721600
```