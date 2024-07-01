# Develop sample

Tags: "Python API" "Quick Install"

----

The source code of Python SDK provides a complete Demo for developers to learn.

* [Call Node API](https://github.com/FISCO-BCOS/python-sdk/blob/master/demo_get.py)
* [Deploy contract, send transaction, process receipt, query contract data](https://github.com/FISCO-BCOS/python-sdk/blob/master/demo_transaction.py)

## Call Node API

Correct [node information configured for SDK connection](./configuration.md)Rear。Instantiate the client structure in the code and call the client's interface。Return to json, which can be based on the [fisco bcos rpc interface json format](../../api.md)Understanding, field fetching and transcoding。

Full Demo: [demo_get.py](https://github.com/FISCO-BCOS/python-sdk/blob/master/demo_get.py)

``` python
# Instantiate client
client = BcosClient()

# Call the node version check interface
res = client.getNodeVersion()
print("getClientVersion",res)

# Call the node block high interface
try:
    res = client.getBlockNumber()
    print("getBlockNumber",res)
except BcosError as e:
    print("bcos client error,",e.info())
```

## Operating Contract

Correct [node information configured for SDK connection](./configuration.md)Rear。Can deploy contracts, send transactions, process receipts, and query contract data。For example, call functions such as' deploy ',' sendRawTransactionGetReceipt ',' call ', and' parse _ event _ logs'.。

Full Demo: [demo_transaction.py](https://github.com/FISCO-BCOS/python-sdk/blob/master/demo_transaction.py)

``` python
# Instantiate client
client = BcosClient()

# Load abi definition from file
abi_file  ="contracts/SimpleInfo.abi"
data_parser = DatatypeParser()
data_parser.load_abi_file(abi_file)
contract_abi = data_parser.contract_abi

# Deployment contract
print("\n>>Deploy:---------------------------------------------------------------------")
with open("contracts/SimpleInfo.bin", 'r') as load_f:
    contract_bin = load_f.read()
    load_f.close()
result = client.deploy(contract_bin)
print("deploy",result)
print("new address : ",result["contractAddress"])
contract_name =  os.path.splitext(os.path.basename(abi_file))[0]
memo = "tx:"+result["transactionHash"]
#Save deployment results to file for future reference
from client.contractnote import ContractNote
ContractNote.save_address(contract_name, result["contractAddress"], int(result["blockNumber"], 16), memo)


#Send a transaction, call an interface that overwrites the data
print("\n>>sendRawTransaction:----------------------------------------------------------")
to_address = result['contractAddress'] #use new deploy address
args = ['simplename', 2024, to_checksum_address('0x7029c502b4F824d19Bd7921E9cb74Ef92392FB1c')]

receipt = client.sendRawTransactionGetReceipt(to_address,contract_abi,"set",args)
print("receipt:",receipt)

#Parsing the log in receipt
print("\n>>parse receipt and transaction:----------------------------------------------------------")
txhash = receipt['transactionHash']
print("transaction hash: " ,txhash)
logresult = data_parser.parse_event_logs(receipt["logs"])
i = 0
for log in logresult:
    if 'eventname' in log:
        i = i + 1
        print("{}): log name: {} , data: {}".format(i,log['eventname'],log['eventdata']))
#Obtain the corresponding transaction data and parse the calling method name and parameters

txresponse = client.getTransactionByHash(txhash)
inputresult = data_parser.parse_transaction_input(txresponse['input'])
print("transaction input parse:",txhash)
print(inputresult)

#Parse the output output of the transaction in receipt, that is, the return value of the method called by the transaction.
outputresult  = data_parser.parse_receipt_output(inputresult['name'], receipt['output'])
print("receipt output :",outputresult)


#Call to get the data.
print("\n>>Call:------------------------------------------------------------------------")
res = client.call(to_address,contract_abi,"getbalance")
print("call getbalance result:",res)
res = client.call(to_address,contract_abi,"getbalance1",[100])
print("call getbalance1 result:",res)
res = client.call(to_address,contract_abi,"getname")
print("call getname:",res)
res = client.call(to_address,contract_abi,"getall")
print("call getall result:",res)
print("demo_tx,total req {}".format(client.request_counter))
client.finish()
```



