# 节点编译问题

## 1. 源码编译慢

### 1.1 case1: 先前没有编译过源码

修改`/etc/hosts`文件，添加如下内容可加速依赖包的下载：

```
140.82.113.4    github.com
185.199.108.153 assets-cdn.github.com
185.199.109.153 assets-cdn.github.com
185.199.110.153 assets-cdn.github.com
185.199.111.153 assets-cdn.github.com
199.232.69.194 github.global.ssl.fastly.net
151.101.108.133 github.map.fastly.net
151.101.108.133 raw.githubusercontent.com
```
```eval_rst
.. note::
    以上域名对应的ip可能会有变更，若在/etc/hosts中加了以上内容后，源码编译仍然很慢，请使用域名查询工具查询并更新这些域名对应的IP
```

### 1.2 case2: 以前编译过源码

若先前有一套编译完成的环境，可从原先环境的`deps/src`目录下拷贝已经下载好的依赖包到当前正在编译项目的`deps/src`目录下


## 2. 如何在国产ARM平台编译FISCO BCOS

FISCO BCOS全面适配国产服务器，若需在国产服务器使用FISCO BCOS，需要手动拉取源码编译，参考资料如下：

- [国产服务器编译并运行FISCO BCOS](../articles/7_practice/kunpeng_platform_compiles_and_runs_fisco-bcos-2.6.0.md)

- [FISCO BCOS支持的软硬件系统](../manual/hardware_requirements.md)


## 3. 常见的编译问题

- [没有安装flex和bison导致的编译问题](https://github.com/FISCO-BCOS/FISCO-BCOS/issues/1863)
- [FISCO BCOS 2.2.0版本的编译问题](https://github.com/FISCO-BCOS/FISCO-BCOS/issues/912)