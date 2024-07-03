# Contract Codec

Tags: "java-sdk" "abi" "scale" "codec" "

----
Java SDK 3.x uses two codec formats, 'ABI' and 'Scale', respectively**Solidity Contract**和**WebankBlockchain-Liquid contract (WBC-Liquid)** The function signature, parameter encoding, and return results are encoded and decoded。

**Note**To distinguish between ABI and Scale, after 3.0.0-rc3, the name of org.fisco.bcos.sdk.ABICodec is changed to org.fisco.bcos.sdk.v3.codec.ContractCodec

In the Java SDK, the 'org.fisco.bcos.sdk.v3.codec.ContractCodec' class provides the functions of encoding the output of the transaction (the field of the 'data'), parsing the return value of the transaction, and parsing the content pushed by the contract event。

Here, we take the 'Add.sol' contract as an example to provide a reference for the use of 'ContractCodec'。

```solidity
pragma solidity^0.6.0;

contract Add {

    uint256 private _n;
    event LogAdd(uint256 base, uint256 e);
 
    constructor() public {
        _n = 100;
    }

    function get() public view returns (uint256 n) {
        return _n;
    }

    function add(uint256 e) public returns (uint256 n) {
        emit LogAdd(_n, e);
        _n = _n + e;
        return _n;
    }
}
```

Call'add(uint256)The transaction receipt of the interface is as follows, focusing on the 'input', 'output' and 'logs' fields:

```Java
{
  / / Omit..
  "input":"0x1003e2d2000000000000000000000000000000000000000000000000000000000000003c",
  "output":"0x00000000000000000000000000000000000000000000000000000000000000a0",
  "logs":[
      {
        / / Omit..
        "data":"0x0000000000000000000000000000000000000000000000000000000000000064000000000000000000000000000000000000000000000000000000000000003c",
        / / Omit..
      }
  ],
  / / Omit..
}
```

## 1. Initialize ContractCodec

Before using the 'ContractCodec' method, you need to initially set the cryptographic environment and codec format。The ABICodec constructor is as follows:

```java
public ContractCodec(CryptoSuite cryptoSuite, boolean isWasm) {
   / / Omitted
}
```

CryptoSuite can be obtained from the initialized Client class, please refer to the link: [Initialize SDK](./assemble_transaction.html#sdk)

isWasm is an important parameter that determines the ContractCodec encoding format to use:

-If isWasm is true, the transaction input and output will be encoded and decoded using the Scale encoding format, which corresponds to the WBC-Liquid contract in the node；
- If isWasm is false, the transaction input and output will be encoded and decoded using the ABI encoding format, which corresponds to the Solidity contract in the node。

## 2. Construct the deployment contract constructor input

The input of the deployment transaction consists of two parts, the binary code of the contract deployment and the encoding of the parameters required by the constructor。where the binary code is the contract compiled binary code。

```java
// bin + The parameter list in Object format, which is inserted into the transaction during deployment
byte[] encodeConstructor(String abi, String bin, List<Object> params);
// bin + List of parameters in String format, abi is used to insert into the transaction at deployment time
byte[] encodeConstructorFromString(String abi, String bin, List<String> params);
// bin + List of parameters in String format, abi needs to be inserted into the transaction additionally
byte[] encodeConstructorFromBytes(String bin, byte[] params);
```

## 3. Construct the transaction input

The input of the transaction consists of two parts, the function selector and the encoding of the parameters required to call the function。where the first four bytes of input data (such as"0x1003e2d2") Specifies the function selector to be called, and the calculation method of the function selector is the function declaration (remove spaces, that is,'add(uint256)') hash, take the first 4 bytes。The rest of the input is the result of the input parameters encoded according to the ABI (e.g"000000000000000000000000000000000000000000000000000000000000003c"as parameter"60"result after encoding)。

Depending on how the function is specified and the parameter input format, 'ContractCodec' provides the following interfaces to calculate the 'data' of the transaction。

```Java
/ / function name+ Parameter list in Object format
byte[] encodeMethod(String abi, String methodName, List<Object> params);
/ / ABI already exists+ Parameter list in Object format
byte[] encodeMethodByAbiDefinition(ABIDefinition abiDefinition, List<Object> params);
/ / function declaration+ Parameter list in Object format
byte[] encodeMethodByInterface(String methodInterface, List<Object> params)
/ / function signature+ Parameter list in Object format
byte[] encodeMethodById(String abi, byte[] methodId, List<Object> params);
/ / function name+ List of parameters in String format
byte[] encodeMethodFromString(String abi, String methodName, List<String> params);
/ / function declaration+ List of parameters in String format
byte[] encodeMethodByInterfaceFromString(String methodInterface, List<String> params);
/ / function signature+ List of parameters in String format
byte[] encodeMethodByIdFromString(String ABI, byte[] methodId, List<String> params);
```

The following takes' encodeMethod 'as an example to illustrate the use of methods, other interfaces use similar methods。

```Java
/ / Initialize the SDK
BcosSDK sdk =  BcosSDK.build(configFile);
/ / Initialize group
Client client = sdk.getClient("group0");
/ / Use the Solidity contract
boolean isWasm = false;
ContractCodec contractCodec = new ContractCodec(client.getCryptoSuite(), isWasm);
String abi = ""; / / Contract ABI code, omitted

/ / Construct parameter list
List<Object> argsObjects = new ArrayList<Object>();
argsObjects.add(new BigInteger("60"));
try {
  String encoded = contractCodec.encodeMethod(abi, "add", argsObjects));
  logger.info("encode method result, " + encoded);
    // encoded = "0x1003e2d2000000000000000000000000000000000000000000000000000000000000003c"
} catch (ContractCodecException e) {
  logger.info("encode method error, " + e.getMessage());
}
```

## 4. Resolve the transaction return value

Depending on how the function is specified and the type of return value, 'ContractCodec' provides the following interfaces to parse the function return value。

```Java
/ / ABI Definition+ Object List+ ABIObject Return List
Pair<List<Object>, List<ABIObject>> decodeMethodAndGetOutputObject(ABIDefinition abiDefinition, String output)
/ / ABI Definition+ ABIObject Return List
ABIObject decodeMethodAndGetOutAbiObjectByABIDefinition(ABIDefinition abiDefinition, String output)
/ / function name+ ABIObject Return List
ABIObject decodeMethodAndGetOutputAbiObject(String abi, String methodName, String output)
/ / function name+ Return list in Object format
List<Object> decodeMethod(String abi, String methodName, String output)
/ / function declaration+ Return list in Object format
List<Object> decodeMethodByInterface(String abi, String methodInterface, byte[] output)
/ / function signature+ Return list in Object format
List<Object> decodeMethodById(String abi, byte[] methodId, byte[] output)
/ / function name+ Return list in String format
List<String> decodeMethodToString(String abi, String methodName, byte[] output)
/ / function declaration+ Return list in String format
List<String> decodeMethodByInterfaceToString(String abi, String methodInterface, byte[] output)
/ / function signature+ Return list in String format
List<String> decodeMethodByIdToString(String abi, byte[] methodId, byte[] output)
```

The 'output' in the above interface parameters is the 'output' field in the transaction receipt ("0x00000000000000000000000000000000000000000000000000000000000000a0"）。The method of using the interface can refer to the interface usage of constructing transaction input。

## 5. Parse the contract event push content

Depending on how the event is specified and the type of parsing result, 'ContractCodec' provides the following interfaces to parse the event content。

```Java
/ / Event name+ List of parsing results in Object format
List<Object> decodeEvent(String abi, String eventName, EventLog log)
/ / Event statement+ List of parsing results in Object format
List<Object> decodeEventByInterface(String abi, String eventSignature, EventLog log)
/ / Event signature / Topic+ List of parsing results in Object format
List<Object> decodeEventByTopic(String abi, String eventTopic, EventLog log)
/ / Event name+ List of parsing results in String format
List<String> decodeEventToString(String abi, String eventName, EventLog log)
/ / Event statement+ List of parsing results in String format
List<String> decodeEventByInterfaceToString(String abi, String eventSignature, EventLog output)
/ / Event signature / Topic+ List of parsing results in String format
List<String> decodeEventByTopicToString(String abi, String eventTopic, EventLog log)
```

For event push, the Java SDK requires users to inherit the 'EventCallback' class and rewrite the 'onReceiveLog' interface to implement their own callback processing logic。The following example uses' decodeEvent 'to parse the pushed event content。Other interfaces are used similarly。

```Java
class SubscribeCallback implements EventSubCallback {
    public void onReceiveLog(String eventSubId, int status, List<EventLog> logs) {
        if (logs != null) {
            String abi = ""; / / Contract ABI code, omitted
            for (EventLog log : logs) {
                / / Use Solidity
                boolean isWasm = false;
                ContractCodec contractCodec = new ContractCodec(client.getCryptoSuite(), isWasm); / / client initialization, omitted
                try {
                    List<Object> list = contractCodec.decodeEvent(abi, "LogAdd", log);
                    logger.debug("decode event log content, " + list);
                    // list.size() = 2
                } catch (ContractCodecException e) {
                    logger.error("decode event log error, " + e.getMessage());
                }
            }
        }
    }
}
```

## 6. Resolve the transaction input value

Compared with constructing transaction input, parsing transaction input is the reverse operation, and input parameters can be parsed according to ABI。The transaction is divided into the transaction of the deployment contract and the transaction of the call contract, as can be seen from the above, the transaction input of the deployment contract is composed of binary code and encoded constructor parameters；The transaction input of the call contract is spliced by the function selector with the encoded function parameters。

```java
/ / ABI Definition+ Encoded parameters after removing the function selector+ Object, ABIObject returns a list
Pair<List<Object>, List<ABIObject>> decodeMethodAndGetInputObject(ABIDefinition abiDefinition, String params)
/ / ABI Definition+ Transaction input+ ABIObject returns
ABIObject decodeMethodAndGetInputObjectByABIDefinition(ABIDefinition abiDefinition, String input)
// ABI + Method Name+ Transaction input+ ABIObject returns
ABIObject decodeMethodAndGetInputABIObject(String abi, String methodName, String input)
// ABI + Binary code+ Transaction input+ Object returns
List<Object> decodeConstructorInput(String abi, String bin, String input)
// ABI + Binary code+ Transaction input+ String returns
List<String> decodeConstructorInputToString(String abi, String bin, String input)
/ / ABI Definition+ Encoded parameters after removing the function selector+ Object, ABIObject returns a list
List<Object> decodeMethodInput(ABIDefinition abiDefinition, String input)
List<Object> decodeMethodInputById(String abi, byte[] methodId, byte[] input)
List<Object> decodeMethodInputByInterface(String abi, String methodInterface, byte[] input)
List<String> decodeMethodInputToString(String abi, String methodName, byte[] input)
List<String> decodeMethodInputByIdToString(String abi, byte[] methodId, byte[] input)
List<String> decodeMethodInputByInterfaceToString(String abi, String methodInterface, byte[] input)
```

