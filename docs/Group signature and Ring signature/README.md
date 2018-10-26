# Group signature and Ring signature
**Author: fisco-dev**  

## Catalog
<!-- TOC -->
- [1 Introduction](#1-introduction)
- [2 Deployment](#2-deployment)
- [3 Note](#3-note)

<!-- TOC -->

<br>

## 1 Introduction

Group-Signature has the feature of tamper-resistant, repudiation-resistant, anonymity and  traceability. It can be used in many scenarios.


### 1.1 Application Scenarios

**(1) Group signature Scenarios**

- **First Scenario(acution, anonymous deposits)**：The users or the sub-institutions of specified institution publish their data (such as evidence, encrypted-biding) on the blockchain, but they don't want to reveal their identities while to ensure the authority of the data with signatures. In this case, the group-signature algorithm will come in handy. They can sign the data by using the group-signature algorithm with a private key issued by the administrator of the institution. Other people can verify the validity of the data using a common public key issued by the administrator of the institution when accessing the data, while can't figure-out the concrete identity of the signers. In this scenario, privacy and anonymity are guaranteed when put data in the block-chain.

- **Second Scenario (Privacy-Reveal Service for Regulators)**：The group-signature algorithm has the feature of traceability. The group administrator of specified group can reveal the signer of specified signature using its private key. This feature is very useful for the regulators by revealing the identity of given signature to trace illegal transactions. The regulators  can trace the signers of illegal transactions through group administrator.

<br>

**(2) Ring signature Scenarios**


- **First Scenario (Anonymous voting)**：Members (Consumer user) in the organization signs the vote and place the signature on the blockchain via the trusted agent(such as webank), When verifiers verifies the ring signature, they can only identify the individual agent the signature belongs to, but cannot trace the signers(voter).

- **Second  Scenario(Anonymous deposit, credit checks etc.)**：The scenario is similar to group signature's anonymous deposit scenario, but the difference is that nobody can trace the signer of the given signature.

- **Third Scenario(Anonymous transaction)**：The ring signature algorithm can be used to send anonymous transactions, and no one can trace the sender and receiver of the transaction.

<br>


### 1.2  Source Code Structure

The table below descripts the source code structure of group-signature algorithm and the ring-signature algorithm:

| <div align = left>Module</div>                   | <div align = left>path</div>                                 | <div align = left>Description</div>                          |
| ------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Shell Scripts for dependencies install           | scripts/install_pbc.sh<br>deploy_pbc.sh                      | pbc and pbc-sig lib are used for group signature，install the pbc and pbc-sig by calling deploy_pbc.sh |
| source code for group signature & ring signature | deps/src/group_sig_lib.tgz                                   | source code for group signature & ring signature             |
| Compile Module                                   | cmake/FindPBC.cmake<br>cmake/ProjectGroupSig.cmake           | compile cmake file related to group signature & ring signature |
| Verification implement                           | libevm/ethcall/EthcallGroupSig.h<br>libevm/ethcall/EthcallRingSig.h | use ethcall to call the group/ring signature lib             |

FISCO BCOS supports configuring to enable or disable the ethcall for group signature & ring signature ( default is disabled).

| <div align = left>Action</div>             | <div align = left>Compile</div>                                       | <div align = left>Compile time</div>                                       |<div align = left>Description</div>                                       |
| -------------- | ---------------------------------------- | ---------------------------------------- |---------------------------------------- |
| enable ethcall       | dependencies will compile  | long compile time |group/ring signature enabled |
| disable ethcall       | dependencies will not compile  | short compile time |group/ring signature disabled |

<br>


## 2 Deployment

Ensure that FISCO BCOS is deployed before deploying ethcall. ([How to deploy FISCO BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master/doc/manual) )

### Enable/Disable ethcall for group signature and ring signature

**(1) Install dependencies**


**① Install basic dependencies**


Install git, dos2unix and lsof before deploying FISCO BCOS：

- git: get the latest code
- dos2unix && lsof

Use below commands to install these dependencies:
(If installation fails, check the configuration of yum and ubuntu)

```bash
[centos]
sudo yum -y install git
sudo yum -y install dos2unix
sudo yum -y install lsof

[ubuntu]
sudo apt install git
sudo apt install lsof
sudo apt install tofrodos
ln -s /usr/bin/todos /usr/bin/unxi2dos
ln -s /usr/bin/fromdos /usr/bin/dos2unix
```

**② Install pbc and pbc-sig for group signature**


Install pbc and pbc-sig before using ethcall for group signature，FISCO BCOS provides pbc and pbc-sig deploy scripts(both centos and ubuntu OS are supported):

```bash
# Use dos2unix to format script, in case the Windows files upload to Linux cannot be parsed correctly
bash format.sh
# Use deploy_pbc.sh script to install pbc and pbc-sig
sudo bash deploy_pbc.sh
```
<br>

**(2) Enable/disable ethcall for group/ring signature**


FISCO BCOS group/ring signature use '-DGROUPSIG=ON' or '-DGROUPSIG=OFF' to control whether need compile, default '-DGROUPSIG=OFF'.

Enable:
```bash
# create build file for store the compiled file
cd FISCO-BCOS && mkdir -p build && cd build
# enable group/ring signature when cmake create Makefile
#**centos OS：
cmake3 -DGROUPSIG=ON -DEVMJIT=OFF -DTESTS=OFF -DMINIUPNPC=OFF ..
#**ubuntu OS:
cmake -DGROUPSIG=ON -DEVMJIT=OFF -DTESTS=OFF -DMINIUPNPC=OFF ..
```
Disable:
```bash
# create build file for store the compiled file
cd FISCO-BCOS && mkdir -p build && rm -rf build/* && cd build
# disable group/ring signature when cmake create Makefile
#centos OS：
cmake3 -DGROUPSIG=OFF -DEVMJIT=OFF -DTESTS=OFF -DMINIUPNPC=OFF ..
#ubuntu OS：
cmake -DGROUPSIG=OFF -DEVMJIT=OFF -DTESTS=OFF -DMINIUPNPC=OFF ..
```
<br>

<br>

**(3) Compile and start FISCO BCOS**

```bash
# compile fisco-bcos
make -j4 #Note: j4 stand for use 4 threads complicating compile, can be configured as needs.
# start fisco-bcos:
bash start.sh
```

<br>


### Other dependencies

**(1) Client application: [sig-service-client](https://github.com/FISCO-BCOS/sig-service-client)**

sig-service-client provides below features：

- Access [sig-service](https://github.com/FISCO-BCOS/sig-service) rpc interface.
- Place signature on the blockchain(by performing a transaction)

Usage and deployment details[ Group signature & Ring signature client guidebook](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master/doc/manual).

<br>

**(2) group signature and ring signature service：[sig-service](https://github.com/FISCO-BCOS/sig-service)**


sig-service is deployed in the organization and provides below features for [sig-service-client](https://github.com/FISCO-BCOS/sig-service-client):

- Create Group, add group member, generate group signature, verify group signature, trace signatory's identity.
- Create Ring, generate ring signature, verify ring signature.

In FISCO BCOS, sig-service-client always requests a group/ring signature first, then places the signature on the blockchain. Blockchain nodes verify the signature by invoking ethcall.

sig-service's usage and deployment details [group signature & ring signature RPC guidebook](https://github.com/FISCO-BCOS/sig-service).

<br>



## 3 Note

**(1) Group signature & ring signature is backward compatible**

<br>

(2) After enabling ethcall, **you cannot stop ethcall service**

If ethcall is stopped by mistake, FISCO BCOS can be reverted back to the version used to enable the ethcall.

<br>

**(3) The version of all nodes deploy FISCO BCOS must be consistent**

If a node enables group/ring signature verification service, then all the other nodes must enable it as well. Otherwise the nodes which have not enabled verification service will be exit abnormally.
<br>

(4) Before invoking group/ring signature verification service, **you must deploy [group signature & ring signature RPC guidebook](https://github.com/FISCO-BCOS/sig-service) and [sig-service-client](https://github.com/FISCO-BCOS/sig-service-client)**

[sig-service-client](https://github.com/FISCO-BCOS/sig-service-client) responsible for placing the signature on the blockchain，[group signature & ring signature RPC guidebook](https://github.com/FISCO-BCOS/sig-service) responsible for providing signature generating service
<br>
