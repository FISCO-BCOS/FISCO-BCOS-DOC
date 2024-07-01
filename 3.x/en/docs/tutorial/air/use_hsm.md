# Building a State Secret Chain Using Hardware Cryptographic Modules

Tags: "hardware encryption" "" HSM "" "cipher machine" "

---

The FISCO BCOS 3.3.0 Hardware Secure Module (HSM) adds the following features:
1. build _ chain.sh Loads the node.pem file of the built-in key of the cipher machine and builds a blockchain using the cipher machine。
2. java-sdk adds a password machine configuration item to use the password machine to verify the transaction signature；(Specific reference [java-sdk configuration](../../sdk/java_sdk/config.md))
This tutorial mainly introduces how to configure FISCO BCOS version 3.3.0 on the node side to use cipher machine.。

## 1. Node version

- When your node needs to use the hardware encryption module, you need to set the node configuration item to enable the cipher machine encryption function, where the signature of the node is verified with the key in the key machine.。All key pairs are stored in the password machine, and no key pairs remain in memory, which improves the security of key storage.。

## 2. Install password card / password machine
To build a state secret chain using a hardware cryptographic module, you need to install a password card or password machine on the server where the node is located.。FISCO BCOS supports GMT0018-Cipher Card / Cipher 2012 Cipher Device Application Interface Specification。

### Step 1. Please install the password machine according to your password card / password machine installation guidelines.
Installation complies with GMT0018-Dynamic library files for the 2012 specification, such as.
1. Place the dynamic library file "libgmt0018.so" under the default library search path (windows operating system is in .dll format), and ensure that the user has read and execute permissions。The path of the dynamic library can be configured in the configuration item 'security' of the node's configuration file 'config.ini'。For example, it can be placed in the "/ usr / local / lib" directory of the Ubuntu operating system and placed in the CentOS operating system, "/ lib64" or "/ usr / lib64" directory。

### Step 2. Please initialize the password card / password machine and run its test program to ensure that it functions properly.
Initialize the device according to the password card / password machine manufacturer's guidelines and create the internal key you need。Then run the test program to ensure that the function is normal and that the GMT0018 provided by the cipher machine can be called correctly through the libgmt0018.so dynamic library-2012 interface method。

## 3. Create a FISCO BCOS blockchain node using a cipher machine.
### Step 1. Dynamic Binary of Nodes
FISCO BCOS dynamic binary is required to load the dynamic library file of the password card.。Users can download the dynamic binaries provided by FISCO BCOS, or manually compile the node dynamic binaries themselves in the appropriate environment。Use source code to compile binary, refer to [source code compilation](../../tutorial/compile_binary.md)。
Note:**Compile**link, you need to specify a compiled dynamic binary, that is, you do not specify '-DBUILD_STATIC=ON`
```shell
# Create Compile Directory
mkdir -p build && cd build
cmake .. || cat *.log
```

### Step 2. Generate State Secret Node

Such as cipher key generation, there are two ways:
1. After generating the node key through the tool, import the node key into the cipher machine and record the index position。For example, import the key certificates of node0 and node1 into the key index positions 43 and 44 of the cipher machine.；
2. Through the cipher machine management program, generate the built-in key of the cipher machine and record the index position；

cipher machine**Public Private Key**用于**Signature verification**；

To build a blockchain, see [Building the first blockchain network](../../quick_start/air_installation.md)
```bash
cd ~/fisco

curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.6.0/build_chain.sh && chmod u+x build_chain.sh
```
In the build _ chain directory, create a folder (for example, nodeKeyDir) to store the node.pem file for the cipher key.(The number of certificates is consistent with the number of nodes built.)。
```bash
./build_chain.sh -e ~/fisco/FISCO-BCOS/build/fisco-bcos-air/fisco-bcos -p 30300,20200 -l 127.0.0.1:4 -s -H -n nodeKeyDir/
```
- `-H`: Open cipher machine；
- `-n`: Load the certificate file in the certificate directory to generate nodeid；
Specific reference [Deployment Tools(build_chain.sh)](./build_chain.md)；

### Step 3. Configure key type and key index
Add the configuration items' enable _ hsm ',' hsm _ lib _ path ',' key _ index ', and' password 'to the node configuration file' config.ini ', and set whether to use the key in the password machine for node signature verification.。
For example, configure node node0 to use the internal key of the cipher machine, and the signature verification key index is 43；
```
[security]
    ; true present that use hsm
    enable_hsm=true
    ; the path of lib file for HSM
    hsm_lib_path = /usr/local/lib/libgmt0018.so
    ; key index the key inside HSM
    key_id=43
    ; password to use HSM
    password = 12345678
```
Same for other nodes。

### Step 4. Start the node
```shell
./nodes/127.0.0.1/start_all.sh 
```
Startup successful
```shell
try to start node0
try to start node1
try to start node2
try to start node3
 node0 start successfully
 node1 start successfully
 node2 start successfully
 node3 start successfully
 ```

 ### Step 5. Confirm that the node is operating normally
 Check if the process is started

```shell
ps -ef | grep -v grep | grep fisco-bcos
```

Normally there would be output similar to the following；
If the number of processes is not 4 (based on the actual number of nodes started), the process is not started (usually caused by the occupied port)

```shell
fisco       5453     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco       5459     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
fisco       5464     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco       5476     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
```

View the number of nodes linked to node node0 as follows

```shell
tail -f nodes/127.0.0.1/node0/log/* |grep -i "heartBeat,connected count"
```

Normally, the connection information will be output continuously. From the output, it can be seen that node0 is connected to three other nodes.。
```bash
info|2022-08-15 19:38:59.270112|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:09.270210|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:19.270335|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:29.270427|[P2PService][Service][METRIC]heartBeat,connected count=3
```