# pro chain or max chain expansion air node

The build _ chian.sh script provides the function of expanding the air node of the pro chain / max chain. This chapter expands a new air blockchain node on the basis of building the pro chain / max chain to help users master the expansion steps of the pro version chain / max version chain expansion air node.。

## pro expansion air node

### 1. Deploy pro chain

 Specify the IP address and port of the service, automatically generate a configuration file, and run the following command to deploy the pro chain without tars. The starting ports of p2p, rpc, and tars are 31300, 21200, and 41400, respectively. The IP address of the organization is 172.31.184.227. There are two nodes under the organization, and the latest binary service is automatically downloaded (for specific deployment dependencies, see the specific pro chain deployment；

```eval_rst
bash build_chain.sh -p 31300,21200,41400 -l 172.31.184.227:2 -C deploy -V pro -o generate -t all
```

The generated node directory is as follows

```
$ tree generate/172.31.184.227 
generate/172.31.184.227
├── gateway_31300
│   ├── BcosGatewayService
│   ├── conf
│   │   ├── ca.crt
│   │   ├── cert.cnf
│   │   ├── config.ini
│   │   ├── nodes.json
│   │   ├── ssl.crt
│   │   ├── ssl.key
│   │   ├── ssl.nodeid
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── group0_node_41402
│   ├── BcosNodeService
│   ├── conf
│   │   ├── config.genesis
│   │   ├── config.ini
│   │   ├── node.nodeid
│   │   ├── node.pem
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── group0_node_41407
│   ├── BcosNodeService
│   ├── conf
│   │   ├── config.genesis
│   │   ├── config.ini
│   │   ├── node.nodeid
│   │   ├── node.pem
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── rpc_21200
│   ├── BcosRpcService
│   ├── conf
│   │   ├── ca.crt
│   │   ├── cert.cnf
│   │   ├── config.ini
│   │   ├── sdk
│   │   │   ├── ca.crt
│   │   │   ├── cert.cnf
│   │   │   ├── sdk.crt
│   │   │   ├── sdk.key
│   │   │   └── sdk.nodeid
│   │   ├── ssl.crt
│   │   ├── ssl.key
│   │   ├── ssl.nodeid
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── start_all.sh
└── stop_all.sh
```

### 2. Required Preparation Documents

When scaling the Air version of the blockchain, you need to prepare a certificate and configuration file in advance to generate the scaling node configuration. The files to be prepared include:

- **CA Certificates and CA Private Keys**: Used to issue a node certificate for a newly expanded node
- **Node configuration file 'config.ini'**: Can be copied from an existing node directory。
- **Node Genesis block configuration file 'config.genesis'**: Can be copied from an existing node directory。
- **Node connection configuration 'nodes.json'**: Configure the IP and port information of all node connections, which can be copied from the existing node directory and added with the IP and port of the new node。
- **fisco-bcos binary**

```shell
# Create a directory to store the expansion configuration
$ mkdir config

# Copy the root certificate and root certificate private key in gateway
$ cp -r generate/gateway/chain0/ca config

# Copy the node configuration file config.ini and the creation block configuration file config.genesis from the scaled node group0 _ node _ 41402
$ cp generate/172.31.184.227/group0_node_41402/conf/config.ini config/
$ cp generate/172.31.184.227/group0_node_41402/conf/config.genesis config/

# Copy the node connection configuration file nodes.json from the scaled node gateway _ 31300
# Here remember to look at the value in nodes.json in gateway _ 31300, if it is{"nodes": 1} to be modified to a specific ip and port, such as in this example{"nodes": ["172.31.184.227:31300"]}
$ cp generate/172.31.184.227/gateway_31300/conf/nodes.json config/

```

### 3. Expansion air node

```ini
# The command is as follows

# Call build _ chain.sh to expand the node. The new node is expanded to the nodes / 127.0.0.1 / node4 directory.
# -c: Specify the paths of config.ini, config.genesis, and nodes.json
# -d: Specify the path to the CA certificate and private key
# -o: Specify the directory where the expansion node configuration is located
# -e: Specify the capacity expansion node fisco-bcos binary path
bash build_chain.sh -C expand -c config -d config/ca -o expandAirNode/node0 -e fisco-bcos 

```

### 4. Modify related configuration

```shell
# replication fisco-bcos binary to expansion node
cp ./fisco-bcos ./expandAirNode

# Copy the tars _ proxy.ini file to the configuration file directory of the expansion node
cp generate/172.31.184.227/group0_node_41402/conf/tars_proxy.ini  ./expandAirNode/node0/conf/

# Add the ca path to the capacity expansion node config.ini
[cert]
    ; directory the certificates located in
    ca_path=./conf
    ; the ca certificate file
    ca_cert=ca.crt
    ; the node private key file
    node_key=ssl.key
    ; the node certificate file
    node_cert=ssl.crt

```

### 5. Add the new expansion node to the group

Copy the sdk certificate in the rpc in the pro chain to the console, modify the ip and port in the peers in the [network] in the config.toml configuration file in the console, and then start the console；

**Step 1: Obtain the NodeID of the scaling node**

After the new node is successfully scaled out, you can view the list of new nodes through the 'getGroupPeers' command in the console, and view the existing consensus nodes through the 'getSealerList' command:

```shell
[group0]: /apps> getGroupPeers
peer0: 4bd3f875ffaf3e00ad8330188df432cf4fb74b9cfdb958ae79af77fc9508a7f9a55826e4b91b0b48a1e526773849d576e528e3911deb54edd2e052065a28761f
peer1: 8a63a72ca74ec925e355da4c20bce34e3ff4d287d3c5adbc4b27fa263726a5eff0f25065d77a10c07ab86f9f1f0913382dfaca2985520eedf388e4c2781f4373
peer2: bf403537b4de11d45ccb1b67dfd5bafc77ea31b4388aca291a9f54759fa03e736a7fd319ce6bdf46777c093963f6a9c9fafea9ab8c9b0814a335c503e7e33442

[group0]: /apps> getSealerList 
[
    Sealer{
        nodeID='8a63a72ca74ec925e355da4c20bce34e3ff4d287d3c5adbc4b27fa263726a5eff0f25065d77a10c07ab86f9f1f0913382dfaca2985520eedf388e4c2781f4373',
        weight=1
    },
    Sealer{
   nodeID='bf403537b4de11d45ccb1b67dfd5bafc77ea31b4388aca291a9f54759fa03e736a7fd319ce6bdf46777c093963f6a9c9fafea9ab8c9b0814a335c503e7e33442',
        weight=1
    }
]
```

As can be seen from the console output, peer0: 4bd3f875ffaf3e00ad8330188df432cf4fb74b9cfdb958ae79af77fc9508a7f9a55826e4b91b0b48a1e526773849d576e528e3911deb54edd2e052065a28761f to its Observed node is not in


**Step 2: Add an expansion node as an observation node**

```shell
[group0]: /apps> addObserver 4bd3f875ffaf3e00ad8330188df432cf4fb74b9cfdb958ae79af77fc9508a7f9a55826e4b91b0b48a1e526773849d576e528e3911deb54edd2e052065a28761f
{
    "code":0,
    "msg":"Success"
}
```

**Step 3: After the scaling node is synchronized to the highest block, add the scaling node as a consensus node**

```shell
[group0]: /apps> addSealer 4bd3f875ffaf3e00ad8330188df432cf4fb74b9cfdb958ae79af77fc9508a7f9a55826e4b91b0b48a1e526773849d576e528e3911deb54edd2e052065a28761f 1
{
    "code":0,
    "msg":"Success"
}

[group0]: /apps> getSealerList 
[
    Sealer{
        nodeID='8a63a72ca74ec925e355da4c20bce34e3ff4d287d3c5adbc4b27fa263726a5eff0f25065d77a10c07ab86f9f1f0913382dfaca2985520eedf388e4c2781f4373',
        weight=1
    },
    Sealer{
        nodeID='bf403537b4de11d45ccb1b67dfd5bafc77ea31b4388aca291a9f54759fa03e736a7fd319ce6bdf46777c093963f6a9c9fafea9ab8c9b0814a335c503e7e33442',
        weight=1
    },
    Sealer{
        nodeID='4bd3f875ffaf3e00ad8330188df432cf4fb74b9cfdb958ae79af77fc9508a7f9a55826e4b91b0b48a1e526773849d576e528e3911deb54edd2e052065a28761f',
        weight=1
    }
]

```

**Step 4: Deploy and invoke the contract**

```shell
[group0]: /apps> deploy HelloWorld
transaction hash: 0x35facffef4b3d021ded67d088bd978958753a74b80ea2ddcef4bb2c28d76ed4c
contract address: 0xc8ead4b26b2c6ac14c9fd90d9684c9bc2cc40085
currentAccount: 0xd302016b12b6c65605c4f5e4f04b05bf3ba195d1
```

## max chain expansion air node

### 1. Deploy max chain

Take machine 172.30.35.60 as an example, deploy max chain, deploy single-agency RPC service, gateway service and node service, p2p, rpc, tars and tikv start ports are 31300, 21200, 41400, 2379, the agency's ip is 172.30.35.60, automatically download the latest binary services (specific deployment dependencies can be viewed for specific max chain deployment documents)；

#### Deploying TiKV

```
# Download and install tiup
$ curl --proto '=https' --tlsv1.2 -sSf https://tiup-mirrors.pingcap.com/install.sh | sh

# Start tikv v6.5.0
# Deploy and start tikv(Here, the physical ip of the machine is 172.30.35.60)
$ nohup tiup playground v6.5.0 --mode tikv-slim --host=172.30.35.60 -T tikv_demo --without-monitor > ~/tikv.log 2>&1 &

# Obtain the tikv listening port(The default listening port of tikv is 2379)
$ cat ~/tikv.log
tiup is checking updates for component playground ...timeout!
Starting component `playground`: /home/fisco/.tiup/components/playground/v1.9.4/tiup-playground v6.5.0 --mode tikv-slim --host=172.30.35.60 -T tikv_demo --without-monitor
Playground Bootstrapping...
Start pd instance:v6.5.0
Start tikv instance:v6.5.0
PD client endpoints: [172.30.35.60:2379]
```

#### Deploy Max version blockchain system

Run the following command to deploy a single-organization RPC service, gateway service, and node service. The starting ports of p2p, rpc, tars, and tikv are 30300, 20200, 40400, and 2379, respectively；

```
bash build_chain.sh -p 31300,21200,41400,2379 -l 172.30.35.60:1 -C deploy -V max -o generate -t all
```

The generated node directory is as follows

```
$ tree generate/172.30.35.60
generate/172.30.35.60
├── gateway_31300
│   ├── BcosGatewayService
│   ├── conf
│   │   ├── ca.crt
│   │   ├── cert.cnf
│   │   ├── config.ini
│   │   ├── nodes.json
│   │   ├── ssl.crt
│   │   ├── ssl.key
│   │   ├── ssl.nodeid
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── group0_executor_41402
│   ├── BcosExecutorService
│   ├── conf
│   │   ├── config.genesis
│   │   ├── config.ini
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── group0_max_node_41402
│   ├── BcosMaxNodeService
│   ├── conf
│   │   ├── config.genesis
│   │   ├── config.ini
│   │   ├── node.nodeid
│   │   ├── node.pem
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── rpc_21200
│   ├── BcosRpcService
│   ├── conf
│   │   ├── ca.crt
│   │   ├── cert.cnf
│   │   ├── config.ini
│   │   ├── sdk
│   │   │   ├── ca.crt
│   │   │   ├── cert.cnf
│   │   │   ├── sdk.crt
│   │   │   ├── sdk.key
│   │   │   └── sdk.nodeid
│   │   ├── ssl.crt
│   │   ├── ssl.key
│   │   ├── ssl.nodeid
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── start_all.sh
└── stop_all.sh
```

### 2. Required Preparation Documents

When expanding the Air version of the blockchain, you need to prepare a certificate and configuration file in advance to generate the expansion node configuration. The steps are as follows:

```
# Create a directory to store the expansion configuration
$ mkdir config

# Copy the root certificate and root certificate private key in gateway
$ cp -r generate/gateway/chain0/ca ./config 

# Copy the node configuration file config.ini and the creation block configuration file config.genesis from the scaled node group0 _ max _ node _ 41402
$ cp generate/172.30.35.60/group0_max_node_41402/conf/config.ini config/
$ cp generate/172.30.35.60/group0_max_node_41402/conf/config.genesis config/

# Copy the node connection configuration file nodes.json from the scaled node gateway _ 31300
# Here remember to look at the value in nodes.json in gateway _ 31300, if it is{"nodes": 1} to be modified to a specific ip and port, such as in this example{"nodes": ["172.30.35.60:31300"]}
$ cp generate/172.30.35.60/gateway_31300/conf/nodes.json config/
```

### 3. Expansion air node

```ini
# The command is as follows

# Call build _ chain.sh to expand the node. The new node is expanded to the nodes / 127.0.0.1 / node4 directory.
# -c: Specify the paths of config.ini, config.genesis, and nodes.json
# -d: Specify the path to the CA certificate and private key
# -o: Specify the directory where the expansion node configuration is located
# -e: Specify the capacity expansion node fisco-bcos binary path
bash build_chain.sh -C expand -c config -d config/ca -o expandAirNode/node0 -e fisco-bcos 

```

### 4. Modify related configuration

```
# replication fisco-bcos binary to expansion node
cp ./fisco-bcos ./expandAirNode

# Copy the tars _ proxy.ini file to the configuration file directory of the expansion node
cp generate/172.30.35.60/group0_max_node_41402/conf/tars_proxy.ini  ./expandAirNode/node0/conf/

# Add the ca path to the capacity expansion node config.ini
[cert]
    ; directory the certificates located in
    ca_path=./conf
    ; the ca certificate file
    ca_cert=ca.crt
    ; the node private key file
    node_key=ssl.key
    ; the node certificate file
    node_cert=ssl.crt
```

### 5. Add the new expansion node to the group

Copy the sdk certificate in the rpc in the max chain to the console, modify the ip and port in the peers in the [network] in the config.toml configuration file in the console, and then start the console；

**Step 1: Obtain the NodeID of the scaling node**

After the new node is successfully scaled out, you can view the list of new nodes through the 'getGroupPeers' command in the console, and view the existing consensus nodes through the 'getSealerList' command:

```
[group0]: /apps> getGroupPeers
peer0: 5447ea538ec7f5b5bb9dd1562d7341f98f020e9e59a78f991d2dde4e0aadf04164e284df51e4d79dfdaf0767be7a110368e21cbaa20453c786788caa1a8b7948
peer1: fa5a748954513531d502358dad8eaa4f1bc94d92f8b18c3069b586877486950eb905ba2e04c88b6ff921b8f9084f175ddafbe105dc28b03016e2f19995637786

[group0]: /apps> getSealerList 
[
    Sealer{
        nodeID='fa5a748954513531d502358dad8eaa4f1bc94d92f8b18c3069b586877486950eb905ba2e04c88b6ff921b8f9084f175ddafbe105dc28b03016e2f19995637786',
        weight=1
    }
]
```

As can be seen from the console output, peer0: 5447ea538ec7f5b5bb9dd1562d7341f98f020e9e59a78f991d2dde4e0aadf04164e284df51e4d79dfdaf0767be7a110368e21cbaa20453c786788caa1a8b7948 to the server console 's not in the

**Step 2: Add an expansion node as an observation node**

```

[group0]: /apps> addObserver  5447ea538ec7f5b5bb9dd1562d7341f98f020e9e59a78f991d2dde4e0aadf04164e284df51e4d79dfdaf0767be7a110368e21cbaa20453c786788caa1a8b7948
{
    "code":0,
    "msg":"Success"
}
```

**Step 3: Add the scaling node as a consensus node**

```
[group0]: /apps> addSealer 5447ea538ec7f5b5bb9dd1562d7341f98f020e9e59a78f991d2dde4e0aadf04164e284df51e4d79dfdaf0767be7a110368e21cbaa20453c786788caa1a8b7948 1
{
    "code":0,
    "msg":"Success"
}
```

