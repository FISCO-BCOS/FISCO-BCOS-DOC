# 快速安装

## 环境要求

**依赖软件**

- **Ubuntu**: `sudo apt install -y zlib1g-dev libffi6 libffi-dev wget`
- **CentOS**：`sudo yum install -y zlib-devel libffi-devel wget`
- **MacOs**: `brew install wget npm`

**Python环境要求**

- **支持版本**：
    - **python 3.6.3**
    - **3.7.x**

## 部署Python SDK

```eval_rst
.. note::

    部署Python SDK前，请确认已参考 `安装 <../../installation.html#fisco-bcos>`_ 部署了FISCO BCOS区块链节点
```

**拉取源代码**

```bash
git clone https://github.com/FISCO-BCOS/python-sdk
```

**初始化环境(若python环境符合要求，可跳过)(Windows环境可跳过)**

windows环境初始化请参考[这里](./README.md#windows环境配置)

```eval_rst
.. important::
    - 若python版本小于3.6，执行本步骤会安装 **pyenv** ,并使用pyenv **安装python-3.7.3** ，创建命名为 **python-sdk** 的python虚拟环境
    - 若 **python版本>= 3.6** 可跳过此步骤
    - **若安装python-3.7.3出错，请检查是否安装了** `依赖软件 <./install.html>`_ 
    - 请在 **bash环境** 下执行此步骤
```

```bash
# 判断python版本，并为不符合条件的python环境安装python 3.7.3的虚拟环境，命名为python-sdk
# 若python环境符合要求，可以跳过此步
# 若脚本执行出错，请检查是否参考[依赖软件]说明安装了依赖
# 提示：安装python-3.7.3可能耗时比较久
cd python-sdk && bash init_env.sh -p

# 激活python-sdk虚拟环境
source ~/.bashrc && pyenv activate python-sdk && pip install --upgrade pip
```

**安装依赖**

```bash
pip install -r requirements.txt
```

**若因网络原因，安装依赖失败，可使用清华的pip源下载，安装命令如下：**

```bash
pip install -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt
```

**初始化配置(Windows环境可跳过)**

```eval_rst
.. note::

   - 配置项详细说明请参考 `配置说明 <./configuration.html>`_ 
   - 若没有执行初始化步骤，需要将 ``contracts/`` 目录下的sol代码手动编译成bin和abi文件并放置于 ``contracts`` 目录，才可以部署和调用相应合约，合约编译可以使用 `remix <https://remix.ethereum.org>`_ 

```

```bash
# 该脚本执行操作如下：
# 1. 拷贝client_config.py.template->client_config.py
# 2. 安装solc编译器
bash init_env.sh -i
```

**若MacOS环境solc安装较慢，可在python-sdk目录下执行如下命令安装solcjs**，并将安装的solcjs路径配置到`client_config.py`的`solcjs_path`(默认为node_modules/solc/solcjs)，python-sdk自动从该路径加载nodejs编译器：

```bash
# 安装编译器
# 默认安装到node_modules/solc/solcjs路径
npm install solc@v0.4.24
```

若没有执行以上初始化步骤，需要将`contracts/`目录下的`sol`代码手动编译成`bin`和`abi`文件并放置于`contracts`目录，才可以部署和调用相应合约。合约编译可以使用[remix](https://remix.ethereum.org)


## Windows环境配置

在Windows运行Python SDK，需要按照以下步骤安装依赖软件并配置合约编译器：

**依赖软件**

- 直接安装`python-3.7.3`和`git`软件，solc二进制下载请参考`bcos_solc.py`中的描述
- [Visual C++ 14.0库](https://visualstudio.microsoft.com/downloads)

> (注：Microsoft Visual C++ 14.0 is required. Get it with "Microsoft Visual C++ Build Tools"解决方法: https://visualstudio.microsoft.com/downloads （注意选择vs 2005即14.0版）或 https://pan.baidu.com/s/1ZmDUGZjZNgFJ8D14zBu9og 提取码: zrby)

**下载并配置solc编译器**
修改`client_config.py.template`，配置`solc`编译器路径，solc二进制下载请参考`bcos_solc.py`中的描述

```bash
# 配置solc编译器路径，若solc存放路径为D:\\open-source\\python-sdk\\bin\\solc，则solc_path配置如下：
solc_path = D:\\open-source\\python-sdk\\bin\\solc
```

**SDK使用示例**
```bash
# 查看SDK使用方法
./console.py usage

# 获取节点版本
./console.py getNodeVersion
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
./console.py getNodeVersion
```

## 开启命令行自动补全

Python SDK引入[argcomplete](https://argcomplete.readthedocs.io/en/latest/)支持命令行补全，运行如下命令开启此功能(**bashrc仅需设置一次，设置之后每次登陆自动生效**)

```eval_rst
.. note::

    - 此步骤仅需设置一次，设置之后以后每次登陆自动生效
    - 请在 **bash环境** 下执行此步骤
    - 目前仅支持bash，不支持zsh 
```

```bash
echo "eval \"\$(register-python-argcomplete ./console.py)\"" >> ~/.bashrc
source ~/.bashrc
```
