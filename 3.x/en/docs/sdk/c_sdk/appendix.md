# APPENDIX

Tags: "c-sdk" "ABI Codec" "Signature Transaction Construction"

----------

This summary introduces the details of using some of the 'c-sdk' tool classes:

- ABI Codec
- Signature transaction construction

## ABI Codec

To be added

## Constructing Signature Transactions

There are two ways to construct a signature transaction, depending on whether the transaction construction and signature are distinguished together:

- Direct construction of signature transactions: You can load the signature private key and construct the signature transaction directly
- Separation of transaction construction and signature: In this case, the private key is hosted by other services due to security and other factors, the transaction construction is done locally, and the transaction signature needs to be done by the signing service

### Direct construction of signature transactions

- Construct signature object

Reference [KeyPair](./api.html#keypair)section, Creating the 'KeyPir' Object

```shell
bcos_sdk_create_keypair: Creating a KeyPair object
bcos_sdk_create_keypair_by_private_key: Loading the Private Key Creating a KeyPair Object
bcos_sdk_create_keypair_by_hex_private_key: Loading a private key in hexadecimal string format Creating a KeyPair object
```

- Construct signature transactions

Reference [Transaction Construction](./api.html#id5)Section, Constructing Signature Transactions

```shell
bcos_sdk_create_signed_transaction: Constructing Signature Transactions
```

Reference [Example](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/tx/hello_sample.c#L287)

### Transaction Construction & Signature Separation

```shell
# Create TransactionData
bcos_sdk_create_transaction_data
# Calculate Transaction Hash
bcos_sdk_calc_transaction_data_hash
# Transaction hash signature, implemented by the signing service
bcos_sdk_sign_transaction_data_hash
# Constructing Signature Transactions
bcos_sdk_create_signed_transaction_with_signed_data
```

Reference [Example](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/tx/hello_sample.c#L305)
