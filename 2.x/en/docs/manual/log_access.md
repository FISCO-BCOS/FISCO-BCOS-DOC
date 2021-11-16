# Log operation

FISCO BCOS supports both the lightweight log system [easylogging++] (https://github.com/zuhd-org/easyloggingpp) and the powerful [boostlog] (https://www.boost.org/doc/libs/1_63_0 /libs/log/doc/html/index.html). boostlog are using by default. You can select different log systems by compiling the option `EASYLOG`.

## Log configuration

```eval_rst
.. important::

    log format:

    - boostlog：`log_${year}${month}${day}${hour}.log`, such as log_2018022112.log
    - easylog: `log_${year}${month}${day}${hour}.${min}.log`, such as log_2018022112.00.log
```

### Configure boostlog

FISCO BCOS uses boostlog by default. Compared to easylogging++, boostlog configuration item is very simple, mainly as follows:

- `enable`: Enable/disable log. Set to `true` to enable; Set to `false` to disable. **Set to true by default. For performance test, this option can be set to `false` to reduce the impact of print logs on test results**

- `level`: log level. It includes `trace, debug, info, warning, error` 5 levels. When setting a certain log level, the log file will output a log whose level is greater than or equal to the setting log level. The log level sorting from large to small is `error > warning > info > debug > trace`.

- `max_log_file_size`: The maximum size of each log file;

- `flush`: boostlog automatically turn on the log auto-refresh by default. To improve system performance, it is recommended to set this value to false.


boostlog example configuration is as follows:

```ini
;log configurations
[log]
    ; whether to enable log, it is true by default
    enable=true
    log_path=./log
    level=info
    ; Maximum size per log file is 200MB by default.
    max_log_file_size=200
    flush=true
```

### Configure easylogging++

In order to minimize the configuration file, FISCO BCOS concentrates the configuration information of easyloggin++ to the `[log]` configuration of config.ini. It is generally recommended not to manually change the configuration other than the log level. For the easylogging++ opening method, refer to [open easylogging++] (log_access.html#id4).


- `format`: global log format;

- `log_flush_threshold`: the log refresh frequency setting. That is, every `log_flush_threshold` line refreshes the log to the file once.

easylogging++ configuration sample is as follows:

```ini
;log configurations
[log]
    ;the directory of the log
    log_path=./log
    ;log level INFO DEBUG TRACE
    level=info
    max_log_file_size=200
    ;easylog config
    format=%level|%datetime{%Y-%M-%d %H:%m:%s:%g}|%msg
    log_flush_threshold=100
```

## Turn on boostlog

To turn on boostlog, set the `EASYLOG` option to `OFF`, operation is as follows:

```bash
## Enter the source code directory, and create a new build folder (the source code is located in the ~/FISCO-BCOS path)
$ cd ~/FISCO-BCOS && mkdir build

## turn off EASYLOG compilation option
# ubuntu system
$ cmake .. -DEASYLOG=OFF
# centos system
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

# compile the source code and generate binary (the compiled concurrency can be selected according to the hardware configuration of the machine, here to set the concurrency as 2)$ make -j2

# view the generated binary file
$ ls bin
fisco-bcos
```

## Turn on easylogging++

To turn on easylogging++, set the `EASYLOG` option to `ON`, operation is as follows:

```bash
## Enter the source code directory, and create a new build folder (the source code is located in the ~/FISCO-BCOS path)
$ cd ~/FISCO-BCOS && mkdir -p build

## turn off EASYLOG compilation option
# ubuntu system
$ cmake .. -DEASYLOG=ON
# centos system
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

# compile the source code and generate binary (the compiled concurrency can be selected according to the hardware configuration of the machine, here to set the concurrency as 2)
$ make -j2

# view the generated binary file
$ ls bin
fisco-bcos
```
