# 环境依赖
## 依赖  
- 机器配置  
   参考FISCO BCOS[机器配置](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master/doc/manual#第一章-部署fisco-bcos环境)  
   ```
   使用的测试服务器： 
   CentOS 7.2 64位
   CentOS 7.4 64位
   Ubuntu 16.04 64位
   ```
  
- 软件依赖  

```shell
Oracle JDK[1.8]
```

**依赖说明**
* FISCO BCOS搭建过程中需要的其他依赖会自动安装, 用户不需要再手动安装。
* CentOS/Ubuntu默认安装或者使用yum/apt安装的是openJDK, 并不符合使用要求, Oracle JDK 1.8 的安装链接[[ Oracle JDK 1.8 安装]](https://github.com/ywy2090/fisco-package-build-tool/blob/docker/doc/Oracle%20JAVA%201.8%20%E5%AE%89%E8%A3%85%E6%95%99%E7%A8%8B.md)。

- 其他依赖  
  sudo权限, 当前执行的用户需要具有sudo权限。

##  CheckList环境检查
使用物料包之前建议使用checkList文档对当前环境进行检查, 部署生产环境时建议将checkList作为必备的流程。