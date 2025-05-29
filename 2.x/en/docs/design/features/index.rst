##############################################################
Other features
##############################################################

For better user experience in smart contract calls and higher security, FISCO BCOS adopts Contract Name Service, or CNS, OSCCA alogorithm and disk encryption.

- **Contract Name Service**

Smart contracts are called by address in Ethereum, which may occurs to following problems:

- contract abi is a long JSON string, no need to be sensed by caller

- contract address is 20-byte magic number, difficult to remember and if lost contract will not be accessible

- One or more callers need to update contract address for re-deployment

- inconvenient for version management and grey release of contract

CNS of FISCO BCOS offers records of map relations between contract name and contract address and query function, so caller can call contract through easier contract name.


- **OSCCA alogorithm**

To support home-made cryptographic algorithm, FISCO BCOS has realized and integrated OSCCA encryption and decryption, signature, signature verification, hash, OSCCA SSL communication protocol to fully support **business encryption proved by OSCCA**.


- **Disk encryption**

Considering that data is accessible to each agency in consortium chain structure, FISCO BCOS adopts disk encryption to encrypt data stored in node database and key manager to store encryption key, ensuring data secrecy.


.. toctree::
   :maxdepth: 1

   cns_contract_name_service.md
   guomi.md
   storage_security.md
   network_compress.md
   contract_management.md
   account_management.md
   stat.md
   flow_control.md 
