# FAQ (Revision in progress)

## Version

Q:
  Is it allowed to modify the information in the genesis block? <br>
A:
  FISCO BCOS prohibits modification of the genesis block. If you want to modify the relevant configuration, it is recommended to modify it through the console. If the genesis block is modified, starting the node will fail, return an error message, and output the initial genesis block information to the nohup.out file.
 
Q:
  Whether the node supports binary operation with version numbers inconsistent with the genesis block? <br>
A: 
  The minimum version currently supported by FISCO BCOS is 3.0.0-rc4, and the highest supported version is v3.1.0. Binaries not within this range will not be able to start.
