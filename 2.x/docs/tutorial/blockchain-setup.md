# 搭建区块链

标签：``搭链`` ``单机部署`` ``控制台``

------

本章介绍在 Ubuntu 操作系统下单机搭建 FISCO BCOS 2.9.1 区块链网络，并接入控制台，适用于本地开发测试、快速体验和教学演示场景。

通过本方案，您将获得：

- 单群组 4 节点的 FISCO BCOS 联盟链（支持国密/非国密双模式）
- 可连接该链的交互式控制台

### 实验环境

| 环境 | 版本 |
|---|---|
| 操作系统 | Ubuntu 20.04.6 LTS |
| FISCO BCOS | 2.9.1 |
| Java | OpenJDK 11（控制台依赖） |

#### 第一步. 安装依赖

```bash
sudo apt-get update
sudo apt-get install -y openssl curl openjdk-11-jdk
```

#### 第二步. 下载构建脚本与节点二进制

```bash
mkdir -p ~/fisco && cd ~/fisco
# 下载官方构建脚本
curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.9.1/build_chain.sh
chmod +x build_chain.sh
```

节点机器无法访问外网时，可先下载 `fisco-bcos` 二进制，再通过 `-e` 参数指定本地二进制：

```bash
curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.9.1/fisco-bcos
chmod +x fisco-bcos
```

#### 第三步. 生成单群组 4 节点联盟链

编写 `node_list`，格式为 `IP:节点数 机构名 群组列表 起始端口列表`：

```bash
cat > node_list <<EOF
127.0.0.1:4 agency1 1,2 30300,20200,8545
EOF
```

**生成节点配置与启动脚本**：

```bash
bash build_chain.sh -f node_list -e fisco-bcos -o blockchain
```

如需搭建国密链，追加 `-g -G` 参数并指定不同的输出目录：

```bash
bash build_chain.sh -f node_list -e fisco-bcos -o guominodes -g -G
```

命令执行成功会输出 `All completed`，示例如下。若执行出错，请检查 `build.log` 中的错误信息。

```bash
Checking fisco-bcos binary...
Binary check passed.
==============================================================
Generating CA key...
==============================================================
Generating keys and certificates ...
Processing IP=127.0.0.1 Total=4 Agency=agency1 Groups=1
==============================================================
Generating configuration files ...
Processing IP=127.0.0.1 Total=4 Agency=agency1 Groups=1
Group:1 has 4 nodes
==============================================================
[INFO] FISCO-BCOS Path : fisco-bcos
[INFO] IP List File    : node_list
[INFO] Start Port      : 30300 20200 8545
[INFO] Server IP       : 127.0.0.1:4
[INFO] Output Dir      : /home/fisco/blockchain
[INFO] CA Path        : /home/fisco/blockchain/cert/
[INFO] RSA channel     : true
==============================================================
[INFO] Execute the download_console.sh script in directory named by IP to get FISCO-BCOS console.
e.g.  bash /home/fisco/blockchain/127.0.0.1/download_console.sh -f
==============================================================
[INFO] All completed. Files in /home/fisco/blockchain
```

#### 第四步. 启动 FISCO BCOS 链

```bash
cd blockchain/127.0.0.1
bash start_all.sh
```

启动失败时，先用 `netstat -an | grep tcp` 检查 `30300~30303`、`20200~20203`、`8545~8548` 端口是否被占用。

启动成功会输出类似下面的内容：

```bash
try to start node0
try to start node1
try to start node2
try to start node3
 node0 start successfully
 node1 start successfully
 node2 start successfully
 node3 start successfully
```

#### 第五步. 检查节点进程

```bash
ps -ef | grep -v grep | grep fisco-bcos
```

正常情况下会看到 4 个节点进程；若进程数不足 4 个，通常是端口被占用导致启动失败：

```bash
fisco    7603    3755  1 07:45 pts/0  00:00:03 /home/fisco/blockchain/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco    7605    3755  1 07:45 pts/0  00:00:03 /home/fisco/blockchain/127.0.0.1/node1/../fisco-bcos -c config.ini
fisco    7608    3755  1 07:45 pts/0  00:00:03 /home/fisco/blockchain/127.0.0.1/node3/../fisco-bcos -c config.ini
fisco    7610    3755  1 07:45 pts/0  00:00:03 /home/fisco/blockchain/127.0.0.1/node2/../fisco-bcos -c config.ini
```

#### 第六步. 检查节点连接情况

```bash
cd node0/log
ls
cat log_2025052507.45.log | grep connected
```

正常情况下可以看到 `node0` 与另外 3 个节点建立连接：

```bash
info|2025-05-25 07:45:27.281092|[P2P][Service] heartBeat,connected count=0
info|2025-05-25 07:45:37.282166|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:45:47.282639|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:45:57.282917|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:46:07.283622|[P2P][Service] heartBeat,connected count=3
```

#### 第七步. 配置并使用控制台

下载控制台：

```bash
bash blockchain/127.0.0.1/download_console.sh -f
tar -zxvf console.tar.gz
```

拷贝控制台配置与证书：

```bash
cp -n console/conf/config-example.toml console/conf/config.toml
cp -r blockchain/127.0.0.1/sdk/* console/conf/
```

启动控制台：

```bash
cd console && bash start.sh
```

输出下述信息表明启动成功，否则请检查 `conf/config.toml` 中的节点端口配置是否正确：

```bash
=============================================================================================
Welcome to FISCO BCOS console(2.9.1)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$

=============================================================================================
```
