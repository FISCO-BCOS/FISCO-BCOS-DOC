##############################################################
Storage model
##############################################################

FISCO BCOS inherits Ethereum storage while importing distributed storage with high extendability, TPS, adaptability and performance. The storge model contains 2 parts:

**Global state**: can further be divided into **MPTState** and **StorageState**

- **MPTState**: store account status by MPT tree, same with Ethereum

- **StorageState**: store account status by table structure of distributed storage, no historical records stored nor dependency on MPT tree, higher performance


**Distributed storage (Advanced Mass Database, AMDB)**: realize consistency of SQL and NOSQL through abstract table structure, support all kinds of data bases by realizing the storage drive, currently support LevelDB and MySQL.

.. image:: ../../../images/storage/architecture.png


.. toctree::
   :maxdepth: 1
   
   storage.md
   mpt.md
