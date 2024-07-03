# 7. Deploy blockchain with docker

Tags: "Use Docker to Build a Blockchain" "Blockchain Tutorial" "" Docker ""

----

[build_chain.sh](../manual/build_chain.md)The script provides the '-d' parameter, which supports deploying the blockchain in docker mode。This chapter will demonstrate how to build a four-node blockchain in docker mode, and help users become familiar with the process of building a blockchain in docker through examples。

```eval_rst
.. note::
    - Currently only supports the deployment of blockchain environment in Linux environment through docker
```

## 1. Installation Dependencies

**Install curl, openssl:**

```bash
# ubuntu
sudo apt install -y curl openssl
# centos
sudo yum install -y curl openssl openssl-devel
```

**Install Docker:**

Refer to official docker documentation: [https://docs.docker.com/engine/install/](https://docs.docker.com/engine/install/)

## 2. Download the installation script
```bash
## Create action directory
cd ~ && mkdir -p fisco && cd fisco

## Download Script
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.6.0/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    -If the build _ chain.sh script cannot be downloaded for a long time due to network problems, please try 'curl-#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.6.0/build_chain.sh && chmod u+x build_chain.sh`
```

## 3. Build a single group 4-node blockchain
Run the following command in the FICO directory to generate a blockchain with a single group of 4 nodes。
Please make sure that the '30300 ~ 30303, 20200 ~ 20203' ports of the machine are not occupied, or you can specify other ports through the '-p' parameter。

```bash
bash build_chain.sh -D -l 127.0.0.1:4 -p 30300,20200
```

```eval_rst
.. note::
    Use of each parameter of -build _ chain.sh, refer to 'here<../operation_and_maintenance/build_chain.html>`_
```

Successful command execution will output 'All completed'。If an error occurs, check the error message in the 'nodes / build.log' file。

```bash
[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/sdk cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node0/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node1/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node2/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node3/conf cert successful!
[INFO] Downloading get_account.sh from https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_account.sh...
######################################################################## 100.0%
[INFO] Admin account: 0x7096372ddefadc3e70057e907f6ab6cf468838ac
[INFO] Generate uuid success: e4640f4f-89ed-47fa-9693-695e96988ec2
[INFO] Generate uuid success: dd2c5bc8-66ab-4f2f-9949-2bb3ca465d18
[INFO] Generate uuid success: f503a549-3dbc-47ee-a9ea-709a9e3f1f03
[INFO] Generate uuid success: 3b46ac36-f944-49bb-ba25-d81b0a3d579b
==============================================================
[INFO] GroupID              : group0
[INFO] ChainID              : chain0
[INFO] docker mode      : true
[INFO] docker tag       : v3.6.0
[INFO] Auth mode            : false
[INFO] Start port           : 30300 20200
[INFO] Server IP            : 127.0.0.1:4
[INFO] SM model             : false
[INFO] enable HSM           : false
[INFO] Output dir           : ./nodes
[INFO] All completed. Files in ./nodes
```

## 4. Start the blockchain

Run 'nodes / 127.0.0.1 / start _ all.sh'

During startup, the node image of the FISCO-BCOS version is checked to see if it exists locally. If it does not exist, the node image is downloaded from the docker hub。

```shell
$ bash nodes/127.0.0.1/start_all.sh
try to start node0
try to start node1
try to start node2
try to start node3
Unable to find image 'fiscoorg/fiscobcos:v3.6.0' locally
Unable to find image 'fiscoorg/fiscobcos:v3.6.0' locally
Unable to find image 'fiscoorg/fiscobcos:v3.6.0' locally
Unable to find image 'fiscoorg/fiscobcos:v3.6.0' locally
v3.6.0: Pulling from fiscoorg/fiscobcos
v3.6.0: Pulling from fiscoorg/fiscobcos
v3.6.0: Pulling from fiscoorg/fiscobcos
v3.6.0: Pulling from fiscoorg/fiscobcos
Already exists a1778b69356: Already exists 

dd98998da8ef: Pulling fs layer 
82158e46ca02: Pulling fs layer 

82158e46ca02: Pulling fs layer 
Extracting [==================================================>]   12.3MB/12.3MB 12.3MB/12.3MB
dd98998da8ef: Pulling fs layer 
Extracting [==================================================>]  29.51MB/29.51MB9.51MB/29.51MB
82158e46ca02: 82158e46ca02: Pull complete 
Pull complete 82158e46ca02: Pull complete 
Extracting [==================================================>]  29.51MB/29.51MB27.2MB/29.51MB
Digest: sha256:ddd677f51458a0ae07458f02bcb4c8cdd876b9323d889dad0c92a5f44244f2f7
Digest: sha256:ddd677f51458a0ae07458f02bcb4c8cdd876b9323d889dad0c92a5f44244f2f7
Digest: sha256:ddd677f51458a0ae07458f02bcb4c8cdd876b9323d889dad0c92a5f44244f2f7
Digest: sha256:ddd677f51458a0ae07458f02bcb4c8cdd876b9323d889dad0c92a5f44244f2f7
Status: Image is up to date for fiscoorg/fiscobcos:v3.6.0
Status: Downloaded newer image for fiscoorg/fiscobcos:v3.6.0
Status: Image is up to date for fiscoorg/fiscobcos:v3.6.0
Status: Downloaded newer image for fiscoorg/fiscobcos:v3.6.0
74342b325faed4cb5913cc80f18dee210ce7757f5a696e26c0a04d665f87b9ce
a846dc34e23b32a5e5d7ee8f465f01e8d231734cf80bd6fd1ca92b2e8d3b9e9c
efae6adb1ebe71b2b81237d51d61d9142736927a28fe91411ecc793a382e6998
de8b704d51a23888d3a129c081bd1e32d61da4af4029415bf7379feef75c0dee
 node0 start successfully pid=74342b325fae
 node1 start successfully pid=a846dc34e23b
 node2 start successfully pid=efae6adb1ebe
 node3 start successfully pid=de8b704d51a2                           
```

## 5. Check the container

Check whether the container status is normal. The command is as follows::

```shell
$ docker ps -a | egrep fiscobcos

74342b325fae   fiscoorg/fiscobcos:v3.6.0    "/usr/local/bin/fisc…"   47 seconds ago   Up 45 seconds                        roottestnodes127.0.0.1node0
efae6adb1ebe   fiscoorg/fiscobcos:v3.6.0    "/usr/local/bin/fisc…"   47 seconds ago   Up 45 seconds                        roottestnodes127.0.0.1node2
a846dc34e23b   fiscoorg/fiscobcos:v3.6.0    "/usr/local/bin/fisc…"   47 seconds ago   Up 45 seconds                        roottestnodes127.0.0.1node1
de8b704d51a2   fiscoorg/fiscobcos:v3.6.0    "/usr/local/bin/fisc…"   47 seconds ago   Up 45 seconds                        roottestnodes127.0.0.1node3
```
If the container status is UP, the node starts normally。

For more information about docker, see the docker documentation: [https://docs.docker.com/](https://docs.docker.com/)

## 6. View Nodes

You can check the log to confirm whether the number of p2p connections and consensus of the node are normal。

- View the number of nodes connected to node node0

```bash
tail -f nodes/127.0.0.1/node0/log/* |grep -i "heartBeat,connected count"
```

Normally, the connection information will be output continuously. From the output, it can be seen that node0 is connected to three other nodes。
```bash
info|2023-06-15 12:28:47.014473|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2023-06-15 12:28:57.014577|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2023-06-15 12:29:07.014641|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2023-06-15 12:29:17.014742|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2023-06-15 12:29:27.014823|[P2PService][Service][METRIC]heartBeat,connected count=3
```

To this docker environment has been deployed。