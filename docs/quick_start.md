# 快速入门

在开始之前首先需要安装FISCO BCOS运行所需的依赖。FSICO BCOS节点的运行需要一套公私钥以及证书，使用提供的build_chain脚本生成配置时依赖于`openssl`。

一旦安装好提供预编译的二进制文件，该二进制文件可以在Ubuntu 16.04和CentOS 7.2以上版本运行，参考[这里](manual/install.md#二进制安装)安装。同时FISCO BCOS也提供源码编译的[说明文档](manual/install.md#源码编译)，用户可以自由选择安装方式。

为了简化安装与配置，FISCO BCOS提供了[build_chain][build_chain]脚本来帮助用户快读搭建FISCO BCOS联盟链，该脚本默认从GitHub下载最新版本的预编译二进制，也可通过选项指定用户编译的二进制来搭建FISCO BCOS链。

初次接触FISCO BCOS的用户建议从[Hello World](manual/hello_world.md)教程开始，通过在本机部署FISCO-BCOS以及部署和调用Hello World合约快速入门。

## [用户手册](manual/index.html)

用户手册提供FISCO BCOS各种功能的操作说明。

## [设计文档](design/index.html)

设计文档介绍FISCO BCOS的各个模块设计以及实现原理。

## [Java SDK](api/sdk.md)

Java SDK提供业务与FISCO BCOS交互的能力，支持将Solidity合约转为Java接口。

## [合约开发](developer/index.html)

合约开发提供Solidity和Precompiled合约的开发教程。

## [企业工具](enterprise/index.html)

企业工具提供FISCO BCOS从部署到运维的整套解决方案，帮助企业用户快速使用FISCO BCOS。

## [API](api.md)

API介绍FISCO BCOS对外暴露的RPC接口。

[build_chain]:https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/build_chain.sh
