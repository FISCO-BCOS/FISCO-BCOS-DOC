# Operation Tutorial

FISCO BCOS generator contains multiple operations about node generation, expansion, group division, and certificate, which are introduced as below:

|          Command          |                                                               Basic function                                                              |                                                                                                                       |
| :-----------------------: | :---------------------------------------------------------------------------------------------------------------------------------------: | --------------------------------------------------------------------------------------------------------------------- |
|    create_group_genesis   |                                                               assign folder                                                               | generate group Genesis Block in an assigned folder according to group_genesis.ini and the certificates in meta folder |
|   build_install_package   | generate <br> node configuration file folder of node_deployment.ini in assigned folder (node certificate should be placed in meta folder) |                                                                                                                       |
| generate_all_certificates |                                 generate node certificate and private key according to node_deployment.ini                                |                                                                                                                       |
| generate\_\*\_certificate |                                        generate chain, agency, node, SDK certificate or private key                                       |                                                                                                                       |
|        merge_config       |                                               merge p2p parts of the two configuration files                                              |                                                                                                                       |
|     deploy_private_key    |                                import private keys in batch to the generated node configuration file folder                               |                                                                                                                       |
|         add_peers         |                                  import node connection files in batch to node configuration file folder                                  |                                                                                                                       |
|         add_group         |                                      import Genesis Blocks in batch to node configuration file folder                                     |                                                                                                                       |
|          version          |                                                         print current version code                                                        |                                                                                                                       |
|           h/help          |                                                                help command                                                               |                                                                                                                       |

## create_group_genesis (-c)

|                     |                                                                                                                                            |
| :-----------------: | :----------------------------------------------------------------------------------------------------------------------------------------: |
| Command description |                                                        generate group Genesis Block                                                        |
|    Premise of use   |                             user needs to configure `group_genesis.ini` and node certificates under meta folder                            |
|  Parameter setting  |                                        assign and generate node configuration file folder for group                                        |
|       Function      | generate a configuration of the new group under the assigned section according to the user of the certificate configured under meta folder |
|  Adaptable scenario |                                        the existed nodes on consortium chain needs to form new group                                       |

Operation Tutorial

```bash
$ cp node0/node.crt ./meta/cert_127.0.0.1_3030n.crt
...
$ vim ./conf/group_genesis.ini
$ ./generator --create_group_genesis ~/mydata
```

After the program is executed, `group.i.genesis` of mgroup.ini will be generated under ~/mydata folder.

The generated `group.i.genesis` is the Genesis Block of the new group.

```eval_rst
.. note::
    In FISCO BCOS 2.0+, each group has one Genesis Block.
```

## build_install_package (-b)

|                     |                                                                                                           |
| :-----------------: | :-------------------------------------------------------------------------------------------------------: |
| Command description |                                       deploy new node and new group                                       |
|    Premise of use   |                user needs to configure `node_deployment.ini` and assign p2p connection file               |
|  Parameter setting  |                      assign file folder for the location of configuration file folder                     |
|       Function      | generate node configuration file folder according to Genesis Block, node certificate and node information |
|  Adaptable scenario |                                  generate node configuration file folder                                  |

Operation Tutorial

```bash
$ vim ./conf/node_deployment.ini
$ ./generator --build_install_package ./peers.txt ~/mydata
```

After the program is executed, a few file folders named node_hostip_port will be generated under ~/mydata folder and pushed to the relative server to activate the node.

## generate_chain_certificate

|                     |                                                                  |
| :-----------------: | :--------------------------------------------------------------: |
| Command description |                     generate root certificate                    |
|    Premise of use   |                                no                                |
|  Parameter setting  |              assign file folder for root certificate             |
|       Function      | generate root certificate and private key under assigned section |
|  Adaptable scenario |         user needs to generate self-sign root certificate        |

```bash
$ ./generator --generate_chain_certificate ./dir_chain_ca
```

Now, user can find root certificate `ca.crt` and private key `ca.key` under ./dir_chain_ca folder.

## generate_agency_certificate

|                     |                                                                                                  |
| :-----------------: | :----------------------------------------------------------------------------------------------: |
| Command description |                                    generate agency certificate                                   |
|    Premise of use   |                        root certificate and private key have been created                        |
|  Parameter setting  | assign section for agency certificate, chain certificate, and private key and create agency name |
|       Function      |                generate agency certificate and private key under assigned section                |
|  Adaptable scenario |                         user needs to create self-sign agency certificate                        |

```bash
$ ./generator --generate_agency_certificate ./dir_agency_ca ./chain_ca_dir The_Agency_Name
```

Now, user can locate The_Agency_Name folder containing agency certificate `agency.crt` and private key `agency.key` through route ./dir_agency_ca.

## generate_node_certificate

|                     |                                                                                               |
| :-----------------: | :-------------------------------------------------------------------------------------------: |
| Command description |                                   generate node certificate                                   |
|    Premise of use   |                      agency certificate and private key have been created                     |
|  Parameter setting  | assign section for node certificate, agency certificate, and private key and create node name |
|       Function      |                generate node certificate and private key under assigned section               |
|  Adaptable scenario |                         user need to create self-sign node certificate                        |

```bash
$ ./generator --generate_node_certificate node_dir(SET) ./agency_dir  node_p2pip_port
```

Then user can locate node certificate `node.crt` and private key `node.key` through route node_dir.

## generate_sdk_certificate

|                     |                                                                                              |
| :-----------------: | :------------------------------------------------------------------------------------------: |
| Command description |                                   generate SDK certificate                                   |
|    Premise of use   |                     agency certificate and private key have been created                     |
|  Parameter setting  | assign section for node certificate, agency certificate and private key and create node name |
|       Function      |                generate SDK certificate and private key under assigned section               |
|  Adaptable scenario |                       user needs to generate self-sign SDK certificate                       |

```bash
$ ./generator --generate_sdk_certificate ./dir_sdk_ca ./dir_agency_ca
```

Now user can locate SDK file folder containing SDK certificate `sdk.crt` and private key `sdk.key` through route ./sdk.

## generate_all_certificates

|                     |                                                                                                                                                                                     |
| :-----------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
| Command description |                                                        generate certificates and private key according to node_deployment.ini                                                       |
|    Premise of use   |                                                                                          no                                                                                         |
|  Parameter setting  |                                                                         assign section for node certificate                                                                         |
|       Function      | create private key and node certificate under meta folder, generate node certificate for exchanging and p2p connection file under assigned section according to node_deployment.ini |
|  Adaptable scenario |                                                                     user needs to transfer data with other nodes                                                                    |

    $ ./generator --generate_all_certificates ./cert

```eval_rst
.. note::
    the above command will create node certificate according to ca.crt, agency.crt and agency.key of meta folder.

    - absence of any of the above 3 files will fail the creation of node certificate, and the program will throw an exception
```

Once the execution is done, user can find node certificate and private key under ./cert folder, and node certificate under ./meta folder.

## merge_config (-m)

\--merge_config command can merge p2p sections of 2 config.ini

For instance, the p2p section in config.ini of A is:

```ini
[p2p]
listen_ip = 127.0.0.1
listen_port = 30300
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30301
node.2 = 127.0.0.1:30302
node.3 = 127.0.0.1:30303
```

The p2p section in config.ini of B is:

```ini
[p2p]
listen_ip = 127.0.0.1
listen_port = 30303
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30303
node.2 = 127.0.0.1:30300
node.3 = 127.0.0.1:30301
```

The command will result in:

```ini
[p2p]
listen_ip = 127.0.0.1
listen_port = 30304
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30301
node.2 = 127.0.0.1:30302
node.3 = 127.0.0.1:30303
node.4 = 127.0.0.1:30300
node.5 = 127.0.0.1:30301
```

For example:

```bash
$ ./generator --merge_config ~/mydata/node_A/config.ini  ~/mydata/node_B/config.ini
```

When it works, the p2p sections in config.ini of node_A and node_B will be merged to ~/mydata/node_B/config.ini

## deploy_private_key (-d)

\--deploy_private_key command will import the private key of nodes with same name to the generated configuration file folder

For example:

```bash
$./generator --deploy_private_key ./cert ./data
```

If ./cert contains file folders named node_127.0.0.1_30300 and node_127.0.0.1_30301, which are placed with node private key node.key,

./data contains file folders named node_127.0.0.1_30300 and node_127.0.0.1_30301,

then this command would import private key under./cert to ./data folder

## add_peers (-p)

\--add_peers command can import the files of assigned peers to the generated node configuration file folder.

For example:

```bash
$./generator --add_peers ./meta/peers.txt ./data
```

If ./data contains configuration file folder named node_127.0.0.1_30300 and node_127.0.0.1_30301, then this command will import peers file with connection information  to the node configuration files `config.ini` under ./data.

### add_group (-a)

\--add_group command will import assigned peers file to the generated node configuration file folder.

For example:

```bash
$./generator --add_group ./meta/group.2.genesis ./data
```

If ./data contains configuration file folder named node_127.0.0.1_30300 and node_127.0.0.1_30301, then this command will import connection information of Group 2 to `conf` folder of all nodes under ./data.

## download_fisco

\--download_fisco can download `fisco-bcos` binary file under assigned section.

For example:

```bash
$./generator --download_fisco ./meta
```

This command will download `fisco-bcos` executable binary file under ./meta folder.

## download_console

\--download_consolecan download and control console under assigned section.

For example:

```bash
$./generator --download_console ./meta
```

This command will configure the console under ./meta folder according to `node_deployment.ini`.

## get_sdk_file

\--get_sdk_file command can acquire `sdk.crt`, `sdk.key`, `ca.crt` that are needed in configuration of console and sdk under assigned section.

For example:

```bash
$./generator --get_sdk_file ./sdk
```

This command will generate the above configuration file according to `node_deployment.ini` under ./sdk

## version (-v)

\--version command can help view the version code of current deployment tool.

```bash
$ ./generator --version
```

## help (-h)

User can use -h or --help command to check help list

For example:

```bash
$ ./generator -h
usage: generator [-h] [-v] [-b peer_path data_dir] [-c data_dir]
                 [--generate_chain_certificate chain_dir]
                 [--generate_agency_certificate agency_dir chain_dir agency_name]
                 [--generate_node_certificate node_dir agency_dir node_name]
                 [--generate_sdk_certificate sdk_dir agency_dir] [-g]
                 [--generate_all_certificates cert_dir] [-d cert_dir pkg_dir]
                 [-m config.ini config.ini] [-p peers config.ini]
                 [-a group genesis config.ini]
```

## Operation in OSCCA standard (China)

All the commands in FISCO BCOS generator are adaptable for OSCCA version of `fisco-bcos` (oscca-approved encryption). When using this version, every certificate and private key should be prefixed with `gm`. The description reads below:

### On-off switch (-g command)

Once -g command is executed, all the operations concerning certificates, nodes, and group Genesis Block will generate the above files of OSCCA standard.

### generate certificate

When executing generate\_\*\_certificate together with -g command, the generated certificate will be in OSCCA standard.

For example:

```bash
./generator --generate_all_certificates ./cert -g
```

```eval_rst
.. note::
    The above command will generate node certificate according to gmca.crt, agency certificate gmagency.crt, and agency private key gmagency.key placed under meta folder.

    - Absence of any one of the three files will fail the generation of node certificate, and the program will throw an exception.
```

### generate group Genesis Block in OSCCA standard

For example:

```bash
$ cp node0/gmnode.crt ./meta/gmcert_127.0.0.1_3030n.crt
...
$ vim ./conf/group_genesis.ini
$ ./generator --create_group_genesis ~/mydata -g
```

After executing this program, the user can locate `group.i.genesis` in mgroup.ini under ~/mydata folder.

`group.i.genesis` is the Genesis Block of the new group.

### generate node configuration file folder in OSCCA standard

For example:

```bash
$ vim ./conf/node_deployment.ini
$ ./generator --build_install_package ./peers.txt ~/mydata -g
```

After executing the program, multiple folders named node_hostip_port will be generated under ~/mydata folder and pushed to the relative server to activate the node.

## Monitoring design

FISCO BCOS generator has a preset monitoring script in all generated node configuration folders. Warning information of node will be sent to the IP address assigned by the user after configuration. FISCO BCOS generator places the monitoring script under the assigned section for node configuration files. If the user assigns the folder named "data", then the user can locate it under the monitor folder of the data section.

For example:

```bash
$ cd ./data/monitor
```

Purpose of use:

1.  to monitor the status of nodes, to reactive node
2.  to acquire block number of node and view information, to ensure the consensus of nodes
3.  to analyze printing of node logs in last 1 minutes, to collect critical errors information of printed logs, to monitor the status of nodes in real-time
4.  to assign log file or period, to analyze data of consensus message management, block generation, and transaction volume and node's health.

### Warning service configuration

Before using this service, the user needs to configure the warning service. Here we take the WeChat notification of [server酱](http://sc.ftqq.com/3.version) as an example. You can read the configuration as reference: [server酱](http://sc.ftqq.com/3.version)

The user associates it with personal GitHub account and WeChat account, then uses -s command of this script to send a warning message to the associated WeChat.

If the user wants to try different services, the user can personalize the service by modifying alarm() {

    # change HTTP server

} function of monitor.sh

### Help command

The way to check the usage of the script by the help command

```bash
$ ./monitor.sh -h
Usage : bash monitor.sh
   -s : send alert to your address
   -m : monitor, statistics.  default : monitor .
   -f : log file to be analyzed.
   -o : dirpath
   -p : name of the monitored program , default is fisco-bcos
   -g : specified the group list to be analyzed
   -d : log analyze time range. default : 10(min), it should not bigger than max value : 60(min).
   -r : setting alert receiver
   -h : help.
 example :
   bash  monitor.sh -s YourHttpAddr -o nodes -r your_name
   bash  monitor.sh -s YourHttpAddr -m statistics -o nodes -r your_name
   bash  monitor.sh -s YourHttpAddr -m statistics -f node0/log/log_2019021314.log -g 1 2 -r your_name
```

Command description:

-   \-s assign the configuration address of warning service. It can be the ip of warning notification.
-   \-m set monitor mode to statistics or monitor. Monitor is the default mode.
-   \-f analyze node log
-   \-o assign location of node
-   \-p set notification name. "fisco-bcos" is the default one.
-   \-g assign group to be monitored. All group is defaulted to be monitored.
-   \-d time scope of log analysis. Default to be within 10 minutes and 60 minutes as the maximum
-   \-r assign the receiver of warning notification
-   \-h help command

### For example

-   use script to monitor status of assigned nodes, send message to receiver Alice:

```bash
$ bash monitor.sh -s https://sc.ftqq.com/[SCKEY(登入后可见)].send -o alice/nodes -r Alice
```

-   use script to collect node information and send message to Alice:

```bash
$ bash monitor.sh -s https://sc.ftqq.com/[SCKEY(登入后可见)].send -m statistics -o alice/nodes -r Alice
```

-   use script to collect information of group 1 and group 2 of specific node log, send message to Alice:

```bash
$ bash monitor.sh -s https://sc.ftqq.com/[SCKEY(replace by your code)].send -m statistics -f node0/log/log_2019021314.log -g 1 2 -o alice/nodes -r Alice
```

## handshake failed checked

The `check_certificates.sh` script in the scripts folder of FISCO BCOS generator contains anomaly detection with error message `handshake failed` in node log.

### Acquire test script

If the user needs to test node generated by `build_chain.sh`, the following command can help acquire test script:

```bash
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/generator/master/scripts/check_certificates.sh && chmod u+x check_certificates.sh
```

```eval_rst
.. note::
    - If the script cannot be downloaded for a long time due to network problems, try `curl -#LO https://gitee.com/FISCO-BCOS/generator/raw/master/scripts/check_certificates.sh && chmod u+x check_certificates.sh`
```

The user that deployed node with a generator can acquire script from scripts/check_certificates.sh under the root directory of the generator.

### Test valid date of certificate

\-t command of `check_certificates.sh` can test certificate by comparing the valid date with the current system time.

Example:

```bash
$ ./check_certificates.sh -t ~/certificates.crt
```

The second parameter can be any x509 certificate that prompt `check certificates time successfully` after passing verification or exception if  it is failing.

### Certificate verification

\-v command of `check_certificates.sh` will verify node certificate according to the root certificate set by user.

```bash
$ ./check_certificates.sh -v ~/ca.crt ~/node.crt
```

After successful verification, it will prompt `use ~/ca.crt verify ~/node.crt successful`, or prompt exception if it is failing.

## Node configuration error checking

### Acquire script

```bash
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/check_node_config.sh && chmod u+x check_node_config.sh
```

```eval_rst
.. note::
    - If the script cannot be downloaded for a long time due to network problems, try `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/check_node_config.sh`
```

### 使用

Using the following command, the script `-p` option specifies the node path, and the script will analyze the configuration errors based on config. ini under the path.

```bash
bash check_node_config.sh -p node_127.0.0.1_30300
```

