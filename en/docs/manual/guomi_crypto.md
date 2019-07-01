# National cryptography

For fully supporting the national cryptography algorithm, FISCO integrates the national encryption, decryption, signature, verification, hash algorithm and SSL communication protocol in the FISCO BCOS platform based on the national cryptography standard. The design documents can be found in the [FISCO BCOS Design Manual. National Cryptography Version](../design/features/guomi.md).

## Initial deployment of FISCO BCOS national cryptography version

This section uses the [`build_chain`] (build_chain.md) script to build a 4-nodes FISCO BCOS chain locally, and uses `Ubuntu 16.04` system as an example to operate. This section uses pre-compiled static `fisco-bcos` binaries for testing on CentOS 7 and Ubuntu 16.04.

```bash
# rely on the installation of Ubuntu16
$ sudo apt install -y openssl curl
# prepare environment
$ cd ~ && mkdir -p fisco && cd fisco
# download build_chain.sh script
$ curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/`curl -s https://api.github.com/repos/FISCO-BCOS/FISCO-BCOS/releases | grep "\"v2\." | sort -u | tail -n 1 | cut -d \" -f 4`/build_chain.sh && chmod u+x build_chain.sh
```

After performing the above steps, the structure in the fisco directory is as follows:

```bash
fisco
├── bin
│   └── fisco-bcos
└── build_chain.sh
```

- build a 4-nodes FISCO BCOS chain

```bash
# Generate a 4-nodes FISCO chain. All nodes belong to group1. The following instructions are executed in the fisco directory.
# -p specifies the starting ports which are p2p_port, channel_port, jsonrpc_port
# According to the following instructions, it needs to ensure that the 30300~30303, 20200~20203, 8545~8548 ports of the machine are not occupied.
# -g The national cryptography compilation option. It will generate a node of national cryptography after using successfully. Download the latest version from GitHub by default.
$ ./build_chain.sh -l "127.0.0.1:4" -p 30300,20200,8545 -g
```

For the `build_chain.sh` script option, please [refer to here] (build_chain.md). The command that execute normally will output `All completed`. (If there is no output, refer to `nodes/build.log` for checking).

```bash
[INFO] Downloading tassl binary ...
Generating CA key...
Generating Guomi CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
[INFO] FISCO-BCOS Path   : bin/fisco-bcos
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /mnt/c/Users/asherli/Desktop/key-manager/build/nodes
[INFO] CA Key Path       : /mnt/c/Users/asherli/Desktop/key-manager/build/nodes/gmcert/ca.key
[INFO] Guomi mode        : yes
==============================================================
[INFO] All completed. Files in /mnt/c/Users/asherli/Desktop/key-manager/build/nodes
```

After the deployment of the national cryptography alliance chain is completed, the rest of operations are same as [installation] (../installation.md).

## National cryptography configuration information

The nodes of FISCO BCOS national cryptography version message through using SSL secure channel. The main configuration items of the SSL certificate are concentrated in the following configuration items:

```ini
[network_security]

data_path：path where the certificate file is located
key：the path of the node private key relative to the data_path
cert: the path of the certificate gmnode.crt relative to data_path
ca_cert: the path of certificate gmca

;certificate configuration
[network_security]
    ;directory the certificates located in
    data_path=conf/
    ;the node private key file
    key=gmnode.key
    ;the node certificate file
    cert=gmnode.crt
    ;the ca certificate file
    ca_cert=gmca.crt
```

## National cryptography SDK using

For details, refer to [SDK Documentation] (../sdk/sdk.html#id8).

## National cryptography console using

The function of national cryptography console is used in the same way as the standard console. See [Console Operations Manual] (../manual/console.md).

## National cryptography configuration

### National cryptography Key Manager

Using the national cryptography Key Manager needs to be recompiled the standard Key Manager. The difference of them is ``` -DBUILD_GM=ON ``` option needs to be added when doing cmake.

``` shell
# under centos
cmake3 .. -DBUILD_GM=ON
# under ubuntu
cmake .. -DBUILD_GM=ON
```

Other steps are same as the standard Key Manager. Please refer to [key-manager repository](https://github.com/FISCO-BCOS/key-manager).

### National cryptography node configuration

FISCO BCOS national cryptography version adopts dual certificate mode, so two sets of certificates are needed for disk encryption. They  are the conf/gmnode.key and conf/origin_cert/node.key. Other operations of disk encryption are the same as [Standard Edition Loading Encryption Operation] (./storage_security.md).


``` shell
cd key-manager/scripts
#encrypt conf/gmnode.key parameter: ip port  Node private key file cipherDataKey
bash encrypt_node_key.sh 127.0.0.1 31443 nodes/127.0.0.1/node0/conf/gmnode.key ed157f4588b86d61a2e1745efe71e6ea 
#encrypt conf/origin_cert/node.key parameter: ip port  Node private key file cipherDataKey
bash encrypt_node_key.sh 127.0.0.1 31443 nodes/127.0.0.1/node0/conf/origin_cert/node.key ed157f4588b86d61a2e1745efe71e6ea 
```
