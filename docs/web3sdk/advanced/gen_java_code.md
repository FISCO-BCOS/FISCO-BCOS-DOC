# 合约代码转换为java代码

```eval_rst
.. admonition:: 文档目标
   

   区块链系统因其开放性、不可篡改性、去中心化特性成为构建可信的去中心化应用的核心解决方案，Java是当前最主流的编程语言之一, 区块链系统支持java调用智能合约很重要。本文档主要介绍如何将智能合约转换成java代码
   
.. admonition:: 参考资料
   
   - web3sdk git: https://github.com/FISCO-BCOS/web3sdk
   - 智能合约参考文档：http://solidity.readthedocs.io/en/v0.4.24/

.. important::
   
   必须先安装好FISCO-BCOS的solidity编译器 `fisco-solc <https://github.com/FISCO-BCOS/fisco-solc>`_ ，详细步骤参考 `FISCO-BCOS入门 <https://fisco-bcos-test.readthedocs.io/zh/latest/docs/getstart/compile_and_install.html>`_ 

.. admonition:: 将合约代码转换成java代码

    web3sdk提供了转换脚本compile.sh，可将合约代码转换成java代码:
    - 转换脚本 ``compile.sh`` 存放路径: ``web3sdk/dist/bin/compile.sh``
    - 转换脚本 ``compile.sh`` 脚本用法：( ``${package}`` 时生成的java代码import的包名)
     (1) 合约代码转换成不包含国密特性的java代码(所有版本均支持): ``bash compile.sh ${package}``
     (2) 合约代码转换成支持国密特性的java代码( `1\.2\.0版本  <https://github.com/FISCO-BCOS/web3sdk/tree/V1.2.0>`_ 后支持):``bash compile.sh ${package} 1``
    - 建议使用 `1\.2\.0版本 <https://github.com/FISCO-BCOS/web3sdk/tree/V1.2.0>`_ 后的web3sdk时，生成支持国密特性的java代码，应用有更大可扩展空间


.. admonition:: 合约代码转换为java代码操作步骤

    将要转换的合约代码复制到 ``web3sdk/dist/contracts`` 目录

     .. code-block:: bash

        $ cd ~/mydata/web3sdk/tool/contracts
        $ ls
        EvidenceSignersData.sol  Evidence.sol  Ok.sol
        #----进入compile.sh脚本所在路径(设web3sdk代码路径是~/mydata/web3sdk)
        $ cd ~/mydata/web3sdk/dist/bin
    
    ``web3sdk/dist/contracts`` 目录下所有智能合约转换成不支持国密特性的java代码

       .. code-block:: bash

          #执行compile.sh脚本，将~/mydata/web3sdk/dist/contract目录下所有合约代码转换成java代码
          #(com是java代码所属的包，转换后可手动修改)
          $ bash compile.sh com

    查看生成的java代码(位于~/mydata/web3sdk/dist/output)

       .. code-block:: bash

          $ tree
          # ...此处省略若干输出...
          ├── Ok.abi
          ├── Ok.bin
          └── com
              ├── Evidence.java
              ├── EvidenceSignersData.java
              └── Ok.java

    高版本可选项：将合约转换成支持国密特性java代码(web3sdk版本号>= 1.2.0时,推荐使用)
     .. code-block:: bash

        $ bash compile.sh com 1

```