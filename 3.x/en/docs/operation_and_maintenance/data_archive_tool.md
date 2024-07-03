# 5. Data Archiving and Recovery

Tags: "data archiving" "data clipping"

----

## 简介

The data archiving tool is used to archive node data, query archive data, and re-import archive data. It supports RocksDB and TiKV modes。

## Node Configuration

If you need to use the archive function, you need to configure the IP address and port of the archive service in the 'config.ini' file of the node. We recommend that you only use '127.0.0.1' for the IP address. After the data archive tool archives the data of the node, it deletes the archived data in the node through this port

```ini
[storage]
    enable_archive=true
    archive_ip=127.0.0.1
    archive_port=8181
```

## Data archiving and re-import

### Data Archiving Tools

The data archiving tool is located in the source code 'tools / archive-tool / archiveTool.cpp', the parameter 'cmake-DTOOLS = ON..' is set at compile time, and the compiled binary is located in 'build / tools / archive-tool / archiveTool'. Note that the running directory of the archiving tool needs to have the node's' config.ini 'and' config.genesis archive 'files

```bash
$ ./tools/archive-tool/archiveTool -h
archive tool used to archive/reimport the data of FISCO BCOS v3:
  -h [ --help ]                         help of storage tool
  -a [ --archive ] arg                  [startBlock] [endBlock] the range is
                                        [start,end) which means the end is not
                                        archived
  -r [ --reimport ] arg                 [startBlock] [endBlock] the range is
                                        [start,end) which means the end is not
                                        reimported
  -c [ --config ] arg (=./config.ini)   config file path
  -g [ --genesis ] arg (=./config.genesis)
                                        genesis config file path
  -p [ --path ] arg                     the path to store the archived data or
                                        read archived data if reimport, if set
                                        path then use rocksDB
  -e [ --endpoint ] arg                 the ip and port of node archive service
                                        in format of IP:Port, ipv6 is not
                                        supported
  --pd arg                              pd address of TiKV, if set use TiKV to
                                        archive data of reimport from TiKV,
                                        multi address is split by comma
```

### Archive Data

The '-a' option indicates that the data archiving operation is performed. The parameter is' [start block] [end block] ', where the end block will not be archived。The '-e' option specifies the IP and port from which the node deletes the archive data service, for example, '127.0.0.1:8181`。Suppose the archive block [1,255)To the local '. / archive' rocksdb database, the node archive service address is' 127.0.0.1:8181 ', the corresponding operation is as follows:

```bash
# Archive [1,255)255 of which will not be archived, data archived to rocksdb, rocksdb path is. / archive
$ ./archiveTool -a 1 255 -e 127.0.0.1:8181 -p ./archive
config file: ./config.ini | genesis file: ./config.genesis
use rocksDB as archive DB, path: ./archive
archive the blocks of range [1,255) into RocksDB
current block number is 511
write block 254 size: 510
write to archive database, block range [1,255)
delete archived data from 1 to 255
---------------request send:
{
	"id" : 1,
	"jsonrpc" : "2.0",
	"method" : "deleteArchivedData",
	"params" :
	[
		1,
		255
	]
}

---------------response:
HTTP/1.1 200 OK
Server: Boost.Beast/329
Content-Type: application/json
Content-Length: 76

{
	"id" : 1,
	"jsonrpc" : "2.0",
	"result" :
	{
		"status" : "success"
	}
}
```

### Archive data re-import

The '-r' option indicates that the data archiving operation is performed. The parameter is' [start block] [end block] ', where the end block will not be imported。The '-p' option indicates to import from rocksdb. The parameter is the rocksdb path. If you need to import from TiKV, use the '--pd' parameter。If the re-imported node is RocksDB, stop the node before re-importing。An example operation is as follows:

```bash
# Archive [1,255)255 of which will not be archived, data archived to rocksdb, rocksdb path is. / archive
$ ./archiveTool -r 1 255 -p ./archive
config file: ./config.ini | genesis file: ./config.genesis
use rocksDB as archive DB, path: ./archive
reimport the blocks of range [1,255) from RocksDB
current block number is 511
reimport block 254 size: 510
reimport from archive database success, block range [1,255)
```

## Archive Data Query

The archive data query tool supports querying archived data. The tool is located at 'FISCO-BCOS / tools / archive-tool / archive-reader'。The tool is written using rust and compiled using the following methods to support TiKV and RocksDB。

```bash
cd tools/archive-tool/archive-reader
cargo build --release

# Tools Help
$ ./target/release/fisco-bcos-archive-reader -h
fisco-bcos-archive-reader 0.1.0

USAGE:
    fisco-bcos-archive-reader [OPTIONS] --pd-addrs <pd-addrs>

FLAGS:
    -h, --help       Prints help information
    -V, --version    Prints version information

OPTIONS:
    -i, --ip-port <ip-port>              The IP and port to listen [default: 127.0.0.1:8080]
    -p, --pd-addrs <pd-addrs>            pd address of TiKV cluster
    -r, --rocksdb-path <rocksdb-path>    The path of the abi json file
```

### Interface Description

The current archive data query service supports the following interfaces

#### getTransactionByHash

Get transaction information based on transaction hash。

##### Parameters

- transactionHash: Transaction hash, hexPrefix encoding

##### Return value

- BcosTransaction: Transaction information corresponding to the specified hash。

#### getTransactionReceipt

Get transaction receipt information based on transaction hash。

##### Parameters

- transactionHash: Transaction hash, hexPrefix encoding

##### Return value

- BcosTransactionReceipt: Receipt information corresponding to the transaction hash。

#### getTransactions

Get transaction information from transaction hash list。

##### Parameters

- transactionHashList: Transaction hash list, hexPrefix encoding, array type

##### Return value

- BcosTransaction: Transaction information corresponding to the specified hash list。

#### getTransactionReceipts

Get transaction receipt information based on transaction hash list。

##### Parameters

- transactionHashList: Transaction hash list, hexPrefix encoding, array type

##### Return value

- BcosTransactionReceipt: Receipt information corresponding to the transaction hash。

### Service Startup

Assuming the archive database is RokcsDB, start with the following command:

```bash
$ ./fisco-bcos-archive-reader --rocksdb-path ./archive
[2022-11-23T09:46:12Z INFO  fisco_bcos_archive_reader] rocksdb path: "./archive"
[2022-11-23T09:46:13Z INFO  fisco_bcos_archive_reader] listen: "127.0.0.1:8080"
[2022-11-23T09:46:13Z INFO  tide::server] Server listening on http://127.0.0.1:8080
```

```bash
# Query example getTransactionByHash, you can use the jq command to get a better display
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByHash","params":["0x818d1799160d4756bda3d541b4adf2978ffe2d56486c816f82c40d44cce376e8"],"id":1}' http://127.0.0.1:8080 -k -v

{"jsonrpc":"2.0","id":1,"result":{"0x818d1799160d4756bda3d541b4adf2978ffe2d56486c816f82c40d44cce376e8":"{\n\t\"abi\" : \"\",\n\t\"blockLimit\" : 621,\n\t\"chainID\" : \"chain0\",\n\t\"from\" : \"0xeaa166044bc22e78afaa192a65484133be8b8580\",\n\t\"groupID\" : \"group0\",\n\t\"hash\" : \"0x818d1799160d4756bda3d541b4adf2978ffe2d56486c816f82c40d44cce376e8\",\n\t\"importTime\" : 1031555460,\n\t\"input\" : \"0xd91921ed000000000000000000000000000000000000000000000000000000003b9aca00\",\n\t\"nonce\" : \"4688110319842126366217015934385308262665803377624097942392610184443141714263\",\n\t\"signature\" : \"0x886c893f29eed9deabd851859decbf2e36554c210c790e4827c8eec45764234d7bb919ae69fa9ebe9ccc01f6ebb4c7c44a2b95e04a450f6befc828a5b8b3874601\",\n\t\"to\" : \"0xb50e7380e1ca4328ad29deba76f357cbd0742e5b\",\n\t\"version\" : 0\n}\n"},"error":{"message":"","code":0,"data":""}}

# Query example getTransactionReceipt
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionReceipt","params":["0x8964189510b4a8b1c5a716864023f49bff281e676ade07dc5bf2605e42e02312"],"id":1}' http://127.0.0.1:8080 -k

{"jsonrpc":"2.0","id":1,"result":{"0x8964189510b4a8b1c5a716864023f49bff281e676ade07dc5bf2605e42e02312":"{\n\t\"blockNumber\" : 1,\n\t\"checksumContractAddress\" : \"0x64f39CdFC6A1BB9f11a67797BdFAb9b8E32E2563\",\n\t\"contractAddress\" : \"0x64f39cdfc6a1bb9f11a67797bdfab9b8e32e2563\",\n\t\"gasUsed\" : \"29391\",\n\t\"hash\" : \"0xf358ed29e83e2e807f4290ff9b6636f940df9d0a284eceb37e0f31104f7b4f09\",\n\t\"logEntries\" : [],\n\t\"message\" : \"\",\n\t\"output\" : \"0x\",\n\t\"status\" : 0,\n\t\"transactionHash\" : \"0x8964189510b4a8b1c5a716864023f49bff281e676ade07dc5bf2605e42e02312\",\n\t\"version\" : 0\n}\n"},"error":{"message":"","code":0,"data":""}}

# Query example getTransactions
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactions","params":[["0x818d1799160d4756bda3d541b4adf2978ffe2d56486c816f82c40d44cce376e8","0xa959b6393419e58d43ca7b61b8fdae77152cd146bd9c1be33c172d4d11a96b89"]],"id":1}' http://127.0.0.1:8080 -k

{"jsonrpc":"2.0","id":1,"result":{"\"0x818d1799160d4756bda3d541b4adf2978ffe2d56486c816f82c40d44cce376e8\"":"{\n\t\"abi\" : \"\",\n\t\"blockLimit\" : 621,\n\t\"chainID\" : \"chain0\",\n\t\"from\" : \"0xeaa166044bc22e78afaa192a65484133be8b8580\",\n\t\"groupID\" : \"group0\",\n\t\"hash\" : \"0x818d1799160d4756bda3d541b4adf2978ffe2d56486c816f82c40d44cce376e8\",\n\t\"importTime\" : 1031555460,\n\t\"input\" : \"0xd91921ed000000000000000000000000000000000000000000000000000000003b9aca00\",\n\t\"nonce\" : \"4688110319842126366217015934385308262665803377624097942392610184443141714263\",\n\t\"signature\" : \"0x886c893f29eed9deabd851859decbf2e36554c210c790e4827c8eec45764234d7bb919ae69fa9ebe9ccc01f6ebb4c7c44a2b95e04a450f6befc828a5b8b3874601\",\n\t\"to\" : \"0xb50e7380e1ca4328ad29deba76f357cbd0742e5b\",\n\t\"version\" : 0\n}\n","\"0xa959b6393419e58d43ca7b61b8fdae77152cd146bd9c1be33c172d4d11a96b89\"":"{\n\t\"abi\" : \"\",\n\t\"blockLimit\" : 621,\n\t\"chainID\" : \"chain0\",\n\t\"from\" : \"0xeaa166044bc22e78afaa192a65484133be8b8580\",\n\t\"groupID\" : \"group0\",\n\t\"hash\" : \"0xa959b6393419e58d43ca7b61b8fdae77152cd146bd9c1be33c172d4d11a96b89\",\n\t\"importTime\" : 1031555461,\n\t\"input\" : \"0xd91921ed000000000000000000000000000000000000000000000000000000003b9aca00\",\n\t\"nonce\" : \"71837098012586034676300230412759313337550570930652897352197745554033485861566\",\n\t\"signature\" : \"0xb128ae7d9f010ac8fc7a3f4c8739096e294dda37453d8df7d26186162053bf67607bb7dfdaa786c0f86e7df36433a644d7b4a52ea7a25c2c2ae08bd20f4e1f9700\",\n\t\"to\" : \"0xd82b4afffe803f1b6966d05e43d5869e84d769d2\",\n\t\"version\" : 0\n}\n"},"error":{"message":"","code":0,"data":""}}

# Query example getTransactionReceipts
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionReceipts","params":[["0x818d1799160d4756bda3d541b4adf2978ffe2d56486c816f82c40d44cce376e8","0xa959b6393419e58d43ca7b61b8fdae77152cd146bd9c1be33c172d4d11a96b89"]],"id":1}' http://127.0.0.1:8080 -k

{"jsonrpc":"2.0","id":1,"result":{"\"0x818d1799160d4756bda3d541b4adf2978ffe2d56486c816f82c40d44cce376e8\"":"{\n\t\"blockNumber\" : 122,\n\t\"checksumContractAddress\" : \"\",\n\t\"contractAddress\" : \"\",\n\t\"gasUsed\" : \"7672\",\n\t\"hash\" : \"0xfed85ec06fe082dcb1cc671f2e50af558b479e329dd50867863ba635a6f6bf4d\",\n\t\"logEntries\" : [],\n\t\"message\" : \"\",\n\t\"output\" : \"0x\",\n\t\"status\" : 0,\n\t\"transactionHash\" : \"\\ufffd\\u0017\\u0656\\rGV\\u0763\\u0541\\u052d\\uda1c\\udffe-VHl\\ufffd\\u0084\\rD\\u0323v\\ufffd\",\n\t\"version\" : 0\n}\n","\"0xa959b6393419e58d43ca7b61b8fdae77152cd146bd9c1be33c172d4d11a96b89\"":"{\n\t\"blockNumber\" : 122,\n\t\"checksumContractAddress\" : \"\",\n\t\"contractAddress\" : \"\",\n\t\"gasUsed\" : \"7672\",\n\t\"hash\" : \"0xfed85ec06fe082dcb1cc671f2e50af558b479e329dd50867863ba635a6f6bf4d\",\n\t\"logEntries\" : [],\n\t\"message\" : \"\",\n\t\"output\" : \"0x\",\n\t\"status\" : 0,\n\t\"transactionHash\" : \"\\u0259\\u05b94\\u0019\\u5343\\u02bba\\u063d\\u03b7\\u0015,\\u0446\\u075c\\u001b\\u3f17-M\\u0011\\u026b\\ufffd\",\n\t\"version\" : 0\n}\n"},"error":{"message":"","code":0,"data":""}}%
```
