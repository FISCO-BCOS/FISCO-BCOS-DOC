# 15. Hardware cipher machine

Tags: "hardware encryption" "" HSM "" "cipher machine" "

-----

## I. Introduction to cipher machine and GMT0018

### cipher machine HSM

Hardware security module (HSM) is a computer hardware device used to secure and manage the digital keys used by strong authentication systems, while providing related cryptographic operations。Hardware security modules are typically connected directly to a computer or network server in the form of an expansion card or external device。

### GMT0018

"GMT0018-2012 Cryptographic Device Application Interface Specification" is a cryptographic device application interface specification issued by the National Cryptographic Administration and conforms to the Chinese cryptographic industry standard。It establishes a unified application interface standard for service-type cryptographic devices under the framework of the public key cryptographic infrastructure application system, through which cryptographic devices are invoked to provide basic cryptographic services to the upper layer。Provide standard basis and guidance for the development, use and testing of such cryptographic devices, which is conducive to improving the level of productization, standardization and serialization of such cryptographic devices。

FISCO BCOS 2.8.0 and FISCO BCOS 3.3.0 versions introduce cipher machine functionality。Users can put the password into the cipher machine, through the cipher machine**consensus signature**、**Transaction Validation**。FISCO BCOS supports the "GMT0018-2012 password device application interface specification" password card / password machine, supports the SDF standard, which makes FISCO BCOS have faster password calculation speed, more secure key protection。

## Second, call the password machine module

The consensus and trading module of FISCO BCOS calls the cipher machine。
When signing the consensus and transaction modules, call the 'bcos-crypto' module, 'bcos-crypto' and then call the 'hsm-crypto' module, and finally call the password machine API interface to complete the signature。The parameters involved are also the built-in key index keyIndex of the cipher machine passed in through the configuration file, and finally the cipher machine signature interface 'SDF _ InternalSign _ ECC' is called。
In the same way, the transaction module calls the 'bcos-crypto' module, and then calls the 'hsm-crypto' module, and finally calls the password machine API interface to complete the signature。Finally call the password machine verification interface 'SDF _ ExternalVerify _ ECC'。

### hsm-crypto module

hsm-crypto is an encapsulated cipher machine API interface that uses C++Hardware secure module (Hardware secure module), which can assist applications to call PCI password cards or cipher machines that comply with the GMT0018-2012 Common Interface Specification for Cryptographic Devices to perform state-secret algorithms SM2, SM3, and SM4 operations。FISCO BCOS node, as well as java-sdk by calling the module, call the password machine API interface。[Github Project Address](https://github.com/WeBankBlockchain/hsm-crypto)

At this point, the hardware cipher machine HSM design document is over, about FISCO BCOS and java-sdk how to use the cipher machine, please refer to [Building a national secret chain using hardware cryptographic modules](../tutorial/air/use_hsm.md)
