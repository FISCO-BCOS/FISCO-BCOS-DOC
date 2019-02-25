# 日志操作

FISCO BCOS同时支持轻量级日志系统[easylogging++](https://github.com/zuhd-org/easyloggingpp)和功能强大的[boostlog](https://www.boost.org/doc/libs/1_63_0/libs/log/doc/html/index.html)，默认使用boostlog，可通过编译选项`EASYLOG`选择不同的日志系统。


## 日志配置

```eval_rst
.. important::

    日志格式：

    - boostlog：`log_${year}${month}${day}${hour}.log`，如log_2018022112.log
    - easylog: `log_${year}${month}${day}${hour}.${min}.log`，如log_2018022112.00.log
```

### 配置boostlog

FISCO BCOS默认使用boostlog，相较于easylogging++，boostlog配置项很简单，主要如下：

- `level`: 日志级别，当前主要包括`trace、debug、info、warning、error`五种日志级别，设置某种日志级别后，日志文件中会输大于等于该级别的日志，日志级别从大到小排序`error > warning > info > debug > trace`；

- `max_log_file_size`：每个日志文件最大容量；

- `flush`：boostlog默认开启日志自动刷新，若需提升系统性能，建议将该值设置为false。

boostlog示例配置如下：

```ini
;log configurations
[log]
    ;the directory of the log
    log_path=./log
    ;log level INFO DEBUG TRACE
    level=info
    max_log_file_size=209715200
    flush=true
```

### 配置easylogging++

为了尽量减少配置文件，FISCO BCOS将easyloggin++的配置信息都集中到了config.ini的`[log]`配置，一般建议不手动更改除了日志级别外的其他配置，开启easylogging++的方法可参考[启用easylogging++](log.html#easylogging)。

- `format`：全局日志格式；

- `log_flush_threshold`：日志刷新频率设置，即每`log_flush_threshold`行刷新日志到文件一次。

easylogging++示例配置如下：

```ini
;log configurations
[log]
    ;the directory of the log
    log_path=./log
    ;log level INFO DEBUG TRACE
    level=info
    max_log_file_size=209715200
    ;easylog config
    format=%level|%datetime{%Y-%M-%d %H:%m:%s:%g}|%msg
    log_flush_threshold=100
```

## 开启boostlog

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
$ make -j2

# 查看生成的二进制文件
$ ls bin
fisco-bcos
```

## 开启easylogging++

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
$ make -j2

# 查看生成的二进制文件
$ ls bin
fisco-bcos
```