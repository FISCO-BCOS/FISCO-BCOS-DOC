# 快速安装

## 环境要求

**依赖软件**

- **Ubuntu**: `sudo apt install -y zlib1g-dev libffi6 libffi-dev wget git`
- **CentOS**：`sudo yum install -y zlib-devel libffi-devel wget git`
- **MacOs**: `brew install wget npm git`

**Python环境要求**

- **支持版本**：
    - **python 3.6.3**
    - **3.7.x**

## 部署Python SDK

### 环境要求
- Python环境：python 3.6.3, 3.7.x
- FISCO BCOS节点：请参考[FISCO BCOS安装](../../installation.html#fisco-bcos)搭建


### 拉取源代码

```bash
git clone https://github.com/FISCO-BCOS/python-sdk
```

### 初始化环境(若python环境符合要求，可跳过)

#### **Linux环境初始化**

```eval_rst
.. note::
   - ``bash init_env.sh -p`` 主要功能是安装pyenv，并使用pyenv安装名称为 ``python-sdk`` 的python-3.7.3虚拟环境
   - 若python环境符合要求，可以跳过此步
   - 安装python 3.7.3耗时比较就
   - 若脚本执行出错，请检查是否参考[依赖软件]说明安装了依赖
   - 安装python-3.7.3可能耗时比较久
   - 此步骤仅需初始化一遍，再次登录直接使用命令 ``pyenv activate python-sdk`` 激活 ``python-sdk`` 虚拟环境即可
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

#### **Windows环境初始化**

在Windows运行Python SDK，需要按照以下步骤安装依赖软件并配置合约编译器：

**安装依赖软件**

- 直接安装`python-3.7.3`和`git`软件，点击[这里](https://github.com/ethereum/solidity/releases/download/v0.4.25/solidity_0.4.25.tar.gz)下载Windows版本solc。
- [Visual C++ 14.0库](https://visualstudio.microsoft.com/downloads)

> (注：Microsoft Visual C++ 14.0 is required. Get it with "Microsoft Visual C++ Build Tools"解决方法: https://visualstudio.microsoft.com/downloads （注意选择vs 2005即14.0版）或 https://pan.baidu.com/s/1ZmDUGZjZNgFJ8D14zBu9og 提取码: zrby)

- python环境变量配置可参考[这里](https://jingyan.baidu.com/article/b0b63dbff271e24a4830708d.html)

**下载并配置solc编译器**

修改`client_config.py.template`，配置`solc`编译器路径，solc二进制下载请参考`bcos_solc.py`中的描述，并将`client_config.py.template`拷贝为`client_config.py`。

```bash
# 修改client_config.py.template: 
# 配置solc编译器路径，若solc存放路径为D:\open-source\python-sdk\bin\solc.exe，则solc_path配置如下：
solc_path = "D:\open-source\python-sdk\bin\solc.exe"

# 将client_config.py.template拷贝到client_config.py
```

### **安装Python SDK依赖**

```bash
pip install -r requirements.txt
```

**若因网络原因，安装依赖失败，可使用清华的pip源下载，安装命令如下：**

```bash
pip install -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt
```

### 初始化配置(Windows环境可跳过)

```bash
# 该脚本执行操作如下：
# 1. 拷贝client_config.py.template->client_config.py
# 2. 安装solc编译器
bash init_env.sh -i
```

**若MacOS环境solc安装较慢，可在python-sdk目录下执行如下命令安装solcjs**，python-sdk自动从该路径加载nodejs编译器：

```bash
# 安装编译器
npm install solc@v0.4.25
```

若没有执行以上初始化步骤，需要将`contracts/`目录下的`sol`代码手动编译成`bin`和`abi`文件并放置于`contracts`目录，才可以部署和调用相应合约。合约编译可以使用[remix](https://remix.ethereum.org)


## SDK使用示例

```eval_rst
.. note::
    windows环境下执行console.py请使用 ``.\console.py`` 或者 ``python console.py`` 
```


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
