# Transaction Construction and Sending

Tag: "c-sdk "" 'assembly transaction "

----

```eval_rst
.. important::
    FISCO BCOS supports V1 transactions after version 3.6.0 and V2 transactions after version 3.7.0. Please confirm the node version sent before using it.。Please refer to: 'v3.6.0 <.. / introduction / change _ log / 3 _ 6 _ 0.html >' for version 3.6.0 features
```

```eval_rst
.. note::
    The data structure of the transaction can refer to 'here <. / transaction _ data _ struct.html >' _
```

FISCO BCOS supports V1 transactions after version 3.6.0 and V2 transactions after version 3.7.0. The following five fields are added:

```c++
string       value;         / / v1 New transaction field, original transfer amount
string       gasPrice;      / / The new field in the v1 transaction. The unit price of gas during execution(gas/wei)
long         gasLimit;      / / The upper limit of the gas used when the transaction is executed.
string       maxFeePerGas;  / / v1 new transaction field, EIP1559 reserved field
string       maxPriorityFeePerGas; / / v1 new transaction field, EIP1559 reserved field
vector<byte> extension;    / / v2 new fields for additional storage
```

In order to meet the requirements of adding transaction fields in the future, the C SDK supports a new transaction service that can support flexible assembly, which is convenient for users and developers to use flexibly.。

## 1. Transaction Structure Definition

After 3.6.0, support for the use of transaction structures has been added, i.e. return values and inputs all support the use of transaction structures.。The structure is as follows:

```c
/ / Basic bytes type
struct bcos_sdk_c_bytes
{
    uint8_t* buffer;
    uint32_t length;
};
/ / v0 transaction data structure
struct bcos_sdk_c_transaction_data
{
    int32_t version;
    int64_t block_limit;
    char* chain_id;
    char* group_id;
    char* nonce;
    char* to;
    char* abi;
    struct bcos_sdk_c_bytes* input;
};
/ / v1 transaction data structure
struct bcos_sdk_c_transaction_data_v1
{
    struct bcos_sdk_c_transaction_data base;
    char* value;
    char* gas_price;
    int64_t gas_limit;
    char* max_fee_per_gas;
    char* max_priority_fee_per_gas;
};
/ / v2 transaction data structure
struct bcos_sdk_c_transaction_data_v2
{
    struct bcos_sdk_c_transaction_data_v1 base_v1;
    struct bcos_sdk_c_bytes* extension;
};
/ / v0 transaction structure
struct bcos_sdk_c_transaction
{
    struct bcos_sdk_c_transaction_data* transaction_data;
    struct bcos_sdk_c_bytes* data_hash;
    struct bcos_sdk_c_bytes* signature;
    struct bcos_sdk_c_bytes* sender;
    int64_t import_time;
    int32_t attribute;
    char* extra_data;
};
/ / v1 transaction structure
struct bcos_sdk_c_transaction_v1
{
    struct bcos_sdk_c_transaction_data_v1* transaction_data;
    struct bcos_sdk_c_bytes* data_hash;
    struct bcos_sdk_c_bytes* signature;
    struct bcos_sdk_c_bytes* sender;
    int64_t import_time;
    int32_t attribute;
    char* extra_data;
};
/ / v2 transaction structure
struct bcos_sdk_c_transaction_v2
{
    struct bcos_sdk_c_transaction_data_v2* transaction_data;
    struct bcos_sdk_c_bytes* data_hash;
    struct bcos_sdk_c_bytes* signature;
    struct bcos_sdk_c_bytes* sender;
    int64_t import_time;
    int32_t attribute;
    char* extra_data;
};
```

## 2. The assembly process of the transaction

The SDK needs to assemble 'TransactionData' first, then assemble the transaction data structure as' Transaction ', and finally encode the transaction data structure and send it to the blockchain node。Specific steps are as follows:

- The actual parameters of the transaction call contract, encoded using ABI / Scale as the 'input' field；
- Enter the 'blockLimit' field, which is usually the height of the current block+600；
- The 'nonce' field, which is a random hexadecimal string.；
- Pass in other parameters to construct the 'TransactionData' structure object；
- Hash the object of 'TransactionData'；
- Use the key to perform the signature calculation on the hash value (byte array) calculated in the previous step to obtain the signature；
- Pass in other parameters to construct the 'Transaction' structure object；
- Encode the 'Transaction' structure object using the 'Tars' encoding；
- Get the final transaction raw data, send to the chain。

## 3. Transaction structure calculation interface.

The following is an example of the 'v2' version of the transaction, using the process of transaction assembly as a timeline to introduce the calculation interface of the transaction structure.。

### 3.1 Constructing the TransactionData structure

Interface 'bcos _ sdk _ create _ transaction _ data _ struct _ v2'

- Function:
  - Create a 'bcos _ sdk _ c _ transaction _ data _ v2' transaction structure, which is an unsigned transaction object
- Parameters:
  - `version`: The transaction version. The corresponding transaction version is passed in according to the transaction field used. The default value is 2
  - `group_id`: Group ID
  - `chain_id`: The chain ID. You can call the 'bcos _ sdk _ get _ group _ chain _ id' operation to obtain the chain ID of the group.
  - `to`: Called contract address, set to empty string when contract is deployed""
  - `input`: The ABI-encoded parameter, which is a bytes array. You need to pass in the bytes pointer and length.
  - `abi`: The ABI of the contract, which is a JSON string with optional parameters. You can enter the ABI of the contract when deploying the contract. By default, an empty string is entered.""
  - `block_limit`: The block limit. You can call the 'bcos _ rpc _ get _ block _ limit' interface to obtain the
  - `value`: Value of transaction transfer balance
  - `gas_price`: Trade a given gas price
  - `gas_limit`: Maximum number of gas used in transactions
  - `max_fee_per_gas`: Trade a given EIP-1559 Field
  - `max_priority_fee_per_gas`: Trade a given EIP-1559 Field
  - `extension`: The bytes type of the transaction that can be stored additionally. You need to pass in the bytes pointer and length.
- Return:
  - 'bcos _ sdk _ c _ transaction _ data _ v2' transaction structure pointer
  - Failed to return 'NULL'. Use 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
- 注意:
  - 'bcos _ sdk _ c _ transaction _ data _ v2 'transaction structure. You need to call the' bcos _ sdk _ destroy _ transaction _ data _ struct _ v2 'interface to release the transaction structure to avoid memory leakage.

### 3.2 Calculating TransactionData Structure Hash

Interface 'bcos _ sdk _ calc _ transaction _ data _ struct _ hash _ v2'

- Function: Calculate the hash of the transaction Data object
- Parameters:
  - `crypto_type`: Hash type, 0 is keccak256, 1 is SM3
  - `bcos_sdk_c_transaction_data_v2`: transactionData pointer
- Return: Transaction hash, Hex String
- Note: When calculating the hash, different judgments will be made according to the version number of the transaction. The hash calculation error will cause the transaction chain check to fail, so please set the transaction version number correctly。

### 3.3 Constructing Transaction Transaction Structures Using Signatures and Transaction Hashes

After calculating the transaction hash using the interface of 3.2, you can use C-The interface of the SDK, or the external signing service calculates the signature to the transaction hash。

**Note: The signature of FISCO BCOS is constructed as follows:**

- If it is an ECDSA signature, the bytes of the signature are constructed as R||S||V, where V is the value of the standard ECDSA, the value range is [0,1]
- If it is an SM2 signature, the bytes of the signature are constructed as R||S||PK, where PK is the public key of the private key

Interface 'bcos _ sdk _ create _ encoded _ transaction _ v2'

- Function: Use signature, transaction hash to construct the encoded transaction, which can be sent directly to the chain.
- Parameters:
  - `bcos_sdk_c_transaction_data_v2` : Transaction TransactionData Object Pointer
  - 'signature ': the signature of the transaction hash, in Hex String format
  - `transaction_data_hash` : Transaction hash, Hex String format
  - `attribute`: Transaction attribute, to be expanded, default to 0
  - `extra_data`: Additional transaction data, additional transaction value can be saved, fill in""Empty string is enough
- Return: The encoded transaction data structure, in Hex String format, which can be used directly on the chain.

Interface 'bcos _ sdk _ encode _ transaction _ struct _ to _ hex _ v2'

- Function: You can additionally build a Transaction structure, encode it, and send it directly to the chain
- Parameters:
  - `bcos_sdk_c_transaction_v2` : Transaction Object Pointer
- Return: The encoded transaction data structure, in Hex String format, which can be used directly on the chain.

### 3.4 Parsing Encoded Transactions

Interface 'bcos _ sdk _ decode _ transaction _ struct _ from _ hex _ v2':

- Function: You can parse the transaction Hex String encoded by 'Tars' and construct the Transaction structure.
- Parameter: 'transaction _ hex _ str', the transaction structure encoded by Tars, Hex String
- Return: 'bcos _ sdk _ c _ transaction _ v2': transaction object pointer

### 3.5 Release the TransactionData structure

Due to 'C-The SDK 'uses pointers when constructing transaction structures.。Therefore, according to C's standard practice, each time after using the transaction structure should actively call the release structure interface to avoid memory leaks.。

Interface 'bcos _ sdk _ destroy _ transaction _ data _ struct _ v2'

- Function: Release the constructed transactionData object。
- Parameters: 'bcos _ sdk _ c _ transaction _ data _ v2' transactionData pointer
- Note: After calling the interface, you should not use the pointer again, nor should you use the same pointer more than once to call the interface。

### 3.6 Release Transaction Structure

Interface 'bcos _ sdk _ destroy _ transaction _ struct _ v2'

- Function: Release the constructed transaction object。
- Parameters: 'bcos _ sdk _ c _ transaction _ v2' transactionData pointer
- Note: After calling the interface, you should not use the pointer again, nor should you use the same pointer more than once to call the interface。
