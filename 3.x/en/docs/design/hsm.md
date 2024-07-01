# 15. Hardware cipher machine

Tags: "hardware encryption" "" HSM "" "cipher machine" "

-----

## I. Introduction to cipher machine and GMT0018

### cipher machine HSM

Hardware security module (HSM) is a computer hardware device used to secure and manage the digital keys used by strong authentication systems, while providing related cryptographic operations.。Hardware security modules are typically connected directly to a computer or network server in the form of an expansion card or external device。

### GMT0018

《GMT0018-The 2012 Cryptographic Device Application Interface Specification is a cryptographic device application interface specification issued by the State Cryptographic Administration and conforms to the Chinese cryptographic industry standard.。It establishes a unified application interface standard for service-type cryptographic devices under the framework of the public key cryptographic infrastructure application system, through which cryptographic devices are invoked to provide basic cryptographic services to the upper layer.。Provide standard basis and guidance for the development, use and testing of such cryptographic devices, which is conducive to improving the level of productization, standardization and serialization of such cryptographic devices.。

FISCO BCOS 2.8.0 and FISCO BCOS 3.3.0 versions introduce cipher machine functionality。Users can put the password into the cipher machine, through the cipher machine**consensus signature**、**Transaction Validation**。FISCO BCOS supports GMT0018-The 2012 Cipher Device Application Interface Specification for Cipher Cards / Ciphers supports the SDF standard, which allows FISCO BCOS to have faster cryptographic calculations and more secure key protection。

## Second, call the password machine module.

The consensus and trading module of FISCO BCOS calls the cipher machine.。
Consensus and transaction modules call 'bcos when signing-crypto 'module,' bcos-crypto 'call again' hsm-The 'crypto' module, which finally calls the password machine API interface to complete the signature。The parameters involved are also the built-in key index keyIndex of the cipher machine passed in through the configuration file, and finally the cipher machine signature interface 'SDF _ InternalSign _ ECC' is called.。
The transaction module calls' bcos' in the same way when checking the signature.-crypto 'module,' bcos-crypto 'call again' hsm-The 'crypto' module, which finally calls the password machine API interface to complete the signature。Finally call the password machine verification interface 'SDF _ ExternalVerify _ ECC'。

### hsm-crypto module

hsm-Crypto is an encapsulated cipher API interface that uses C++The hardware security module (Hardware Secure Module) implemented to assist applications in calling the GMT0018-The PCI password card or password machine of 2012 Common Interface Specification for Password Equipment performs operations of SM2, SM3 and SM4。FISCO BCOS node, and java-The sdk calls the password machine API interface by calling this module.。[Github Project Address](https://github.com/WeBankBlockchain/hsm-crypto)

At this point, the hardware cipher machine HSM design document is over, about FISCO BCOS and java-sdk how to use password machine, please refer to [build a password module using hardware state chain](../tutorial/air/use_hsm.md)
