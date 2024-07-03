# Quick installation

Tags: "Python API" "Quick Install"

----
## Environmental Requirements

**Dependent software**

- **Ubuntu**: `sudo apt install -y zlib1g-dev libffi6 libffi-dev wget git`
- **CentOS**：`sudo yum install -y zlib-devel libffi-devel wget git`
- **MacOs**: `brew install wget git`

**Python environment requirements**

- **Supported Versions**：
    - **python 3.6.3**
    - **3.7.x**

## Deploying the Python SDK

### Environmental Requirements
- Python environment: python 3.6.3, 3.7.x
- FISCO BCOS Node: Please refer to [FISCO BCOS Installation](../../quick_start/air_installation.md)Build


### Initialize environment(If the python environment meets the requirements, you can skip the)

#### **Linux environment initialization**

**Pull source code**

```bash
git clone https://github.com/FISCO-BCOS/python-sdk

# If the above command cannot be executed for a long time due to network problems, try the following command:
git clone https://gitee.com/FISCO-BCOS/python-sdk
```

**Configure Environment**

```eval_rst
.. note::
   - "bash init _ env.sh-p" The main function is to install pyenv and use pyenv to install python-3.7.3 virtual environment with the name "python-sdk"
   - If the python environment meets the requirements, you can skip this step
   - If the script is executed incorrectly, check whether the dependency is installed by referring to [Dependency Software]
   - Installing python-3.7.3 may take a long time
   -This step only needs to be initialized once, log in again and directly use the command "pyenv activate python-sdk" to activate "python-sdk" virtual environment
```

```bash
# Determine the python version and install the virtual environment of python 3.7.3 for the unqualified python environment, named python-sdk
# If the Python environment meets the requirements, you can skip this step
# If the script is executed incorrectly, check whether the dependency is installed by referring to [Dependency Software]
# Note: installing python-3.7.3 may take a long time
cd python-sdk && bash init_env.sh -p

# activate python-sdk virtual environment
source ~/.bashrc && pyenv activate python-sdk && pip install --upgrade pip
```

#### **Windows Environment Initialization**

To run the Python SDK on Windows, follow these steps to install the dependent software and configure the contract compiler:

**Installing dependent software**


```eval_rst
.. note::
    - Microsoft Visual C++ 14.0 is required. Get it with "Microsoft Visual C++ Build Tools"Workaround: https:/ / visualstudio.microsoft.com / downloads (note that vs 2005 is version 14.0) or https:/ / pan.baidu.com / s / 1ZmDUGZjZNgFJ8D14zBu9og extraction code: zrby

    After the -solc compiler is downloaded successfully, extract it and copy the "solc.exe" file "${python-sdk}\ bin "directory, if the python-sdk path is" D:\\ open-source\\ python-sdk ", the" solc.exe "file copy path is" D:\\open-source\\python-sdk\\bin\\solc.exe`` 
```

- Direct install [Python-3.7.x](https://www.python.org/downloads/release/python-373/)and [git](https://git-scm.com/download/win)Software
python environment variable configuration can refer to [here](https://jingyan.baidu.com/article/b0b63dbff271e24a4830708d.html)

- [Visual C++ 14.0 Library](https://visualstudio.microsoft.com/downloads)

- To download Windows version solc, click [here](https://github.com/ethereum/solidity/releases/download/v0.4.25/solidity-windows.zip)Download


**Pull source code**

Open git and execute the following command in any directory

```bash
git clone https://github.com/FISCO-BCOS/python-sdk

# If the above command cannot be executed for a long time due to network problems, try the following command:
git clone https://gitee.com/FISCO-BCOS/python-sdk
```


**Configure the solc compiler**

Modify 'client _ config.py.template' and configure the 'solc' compiler path. For downloading solc binary, refer to the description in 'bcos _ solc.py' and copy 'client _ config.py.template' to 'client _ config.py'。

```bash
# Modify client _ config.py.template: 
# Configure the solc compiler path. If the storage path of solc is D:\\ open-source\\ python-sdk\\ bin\\ solc.exe, configure solc _ path as follows:
solc_path = "D:\\open-source\\python-sdk\\bin\\solc.exe"

# Copy client _ config.py.template to client _ config.py
```

### **Installing Python SDK dependencies**

```bash
pip install -r requirements.txt
```

**If the installation of dependencies fails due to network reasons, you can use Tsinghua's pip source to download the installation command as follows:**

```bash
pip install -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt
```

### Initialize Configuration(Windows environment can be skipped)

```bash
# The script performs the following operations:
# 1. Copy client _ config.py.template->client_config.py
# 2. Download the solc compiler
bash init_env.sh -i
```

If you do not perform the above initialization steps, you need to manually compile the 'sol' code in the 'contracts /' directory into 'bin' and 'abi' files and place them in the 'contracts' directory before you can deploy and call the corresponding contract。Contract compilation can be done using [remix](https://remix.ethereum.org)


## Configure Channel Communication Protocol

Python SDK supports the use of [Channel protocol](../../design/protocol_description.html#channelmessage-v1)Communicate with FISCO BCOS nodes and ensure the confidentiality of communication between SDK and nodes through SSL encrypted communication。

If the node connected to the SDK is deployed in the directory '~ / fisco / nodes / 127.0.0.1', use the Channel protocol as follows:

**Configure Channel Information**

```eval_rst
.. note::
    To facilitate development and experience, the channel _ listen _ ip reference configuration is' 0.0.0.0 '. For security reasons, please modify it to a secure listening address according to the actual business network conditions, such as the intranet IP or a specific external IP
```

Get channel _ listen _ port in the config.ini file under the node directory, here 20200
```bash
[rpc]
    channel_listen_ip=0.0.0.0
    jsonrpc_listen_ip=127.0.0.1
    channel_listen_port=20200
    jsonrpc_listen_port=8545
```
    
Switch to the python-sdk directory and modify 'channel _ host' in the client _ config.py file to the actual IP address, and 'channel _ port' to the 'channel _ listen _ port' obtained in the previous step:

```bash
channel_host = "127.0.0.1"
channel_port = 20200
```

**Configure Certificate**

```bash
# If the node and python-sdk are located on different machines, copy all related files in the node's sdk directory to the bin directory
# If the node and the SDK are located on the same machine, directly copy the node certificate to the SDK configuration directory
cp ~/fisco/nodes/127.0.0.1/sdk/* bin/
```

**Configure Certificate Path**

```eval_rst
.. note::
    - The "channel _ node _ cert" and "channel _ node _ key" options of "client _ config.py" are used to configure the SDK certificate and private key, respectively
    - "release-2.1.0", update the SDK certificate and private key to "sdk.crt" and "sdk.key." Before configuring the certificate path, check the certificate name and private key copied in the previous step, set "channel _ node _ cert" as the SDK certificate path, and set "channel _ node _ key" as the SDK private key path
    -FISCO-BCOS 2.5 and later versions, adding the restriction that the SDK can only connect to the local node, you need to confirm the path of the copy certificate during operation, otherwise Jianlian reports an error
```

Check the path of the sdk certificate copied from the node. If the paths of the sdk certificate and private key are 'bin / sdk.crt' and 'bin / sdk.key', the configuration items in 'client _ config.py' are as follows:

```bash
channel_node_cert = "bin/sdk.crt"  # When using the channel protocol, you need to set the sdk certificate. If you use the rpc protocol for communication, you can leave it blank
channel_node_key = "bin/sdk.key"   # When using the channel protocol, you need to set the sdk private key, such as using the rpc protocol communication, this can be left blank
```

If the paths of the sdk certificate and private key are 'bin / node.crt' and 'bin / node.key' respectively, the relevant configuration items in 'client _ config.py' are as follows:
```bash
channel_node_cert = "bin/node.crt"  # When using the channel protocol, you need to set the sdk certificate. If you use the rpc protocol for communication, you can leave it blank
channel_node_key = "bin/node.key"   # When using the channel protocol, you need to set the sdk private key, such as using the rpc protocol communication, this can be left blank
```

**Accessing a Node Using the Channel Protocol**

```eval_rst
.. note::
    To run console.py in windows, use '.\ console.py' or 'python console.py'
```

```bash
# Get FISCO BCOS node version number
./console.py getNodeVersion
```

## Enable command line auto-complete

Python SDK introduction [argcomplete](https://argcomplete.readthedocs.io/en/latest/)Supports command line completion. Run the following command to enable this function(**Bashrc only needs to be set once, after setting, each login will take effect automatically**)

```eval_rst
.. note::

    -This step only needs to be set once, after setting, each login will automatically take effect
    - Please in**bash environment** Perform this step under
    - Currently only supports bash, not zsh
```

```bash
echo "eval \"\$(register-python-argcomplete ./console.py)\"" >> ~/.bashrc
source ~/.bashrc
```
