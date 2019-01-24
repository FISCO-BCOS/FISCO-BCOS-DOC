## 快速开始

```
$ git clone https://github.com/HaoXuan40404/generator.git
$ cd generator
./generator --demo
```
此操作会在执行目前的所有操作

1. 按照./conf/mchain.ini中的配置在./meta下生成证书
2. 按照./conf/mchain.ini的配置在./data下生成安装包
3. 拷贝./meta下的私钥至./data的安装包（还做了替换config.ini的操作）
4. 按照./conf/expand.ini中的配置在./meta下生成证书
5. 按照./conf/expand.ini的配置在./expand下生成扩容安装包
6. 按照./conf/mgroup.ini的配置在./data下生成group2的相关配置

操作完成后，用户可以:

1. 在./data下运行start_all.sh启动所有节点，并查看节点配置
2. 在./expand下查看扩容生成的节点
3. 在./data下查看group2的相关信息