# 快速安装

## 环境要求

**依赖软件**

- **Ubuntu**: `sudo apt install -y zlib1g-dev libffi6 libffi-dev`
- **CentOS**：`sudo yum install -y libffi-devel zlib-devel`

**Python环境要求**

- **支持版本**：python 3.6.3, 3.7.x

## 部署Python SDK

```eval_rst
.. note::

    部署Python SDK前，请确认已参考 `安装 <../../installation.html#fisco-bcos>`_ 部署了FISCO BCOS区块链节点
```

**拉取源代码**

```bash
git clone https://github.com/FISCO-BCOS/python-sdk
```

**初始化环境(若python环境符合要求，可跳过)**

```bash
# 判断python版本，并为不符合条件的python环境安装python 3.7.3的虚拟环境，命名为python-sdk
# 若python环境符合要求，可以跳过此步
cd python-sdk && bash init_env.sh

# 激活python-sdk虚拟环境
source ~/.bashrc && pyenv activate python-sdk
```

**安装依赖**
```bash
pip install -r requirements.txt
```

**SDK使用示例**
```bash
# 查看SDK使用方法
python console.py usage

# 获取节点版本
python console.py getNodeVersion
```

## 使用Channel通信协议

Python SDK支持使用[Channel协议](../../design/protocol_description.html#channelmessage)与FISCO BCOS节点通信，通过SSL加密通信保障SDK与节点通信的机密性。

设SDK连接的节点部署在目录`~/fisco/nodes/127.0.0.1`目录下，则通过如下步骤使用Channel协议：

**修改Python SDK配置**

```bash
# 切换到python-sdk目录
cd ~/fisco/python-sdk

# 修改通信协议为channel
sed -i "s/client_protocol = \"rpc\"/client_protocol = \"channel\"/g" client_config.py
```

**配置证书**
```bash
# 拷贝节点证书到SDK配置目录
cp ~/fisco/nodes/127.0.0.1/sdk/* bin/
```

**使用Channel协议访问节点**

```bash
# 获取FISCO BCOS节点版本号
python console.py getNodeVersion
```
