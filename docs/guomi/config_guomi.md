# 国密版web3sdk搭建

```eval_rst
.. admonition:: 注意事项

   1. 搭建国密版web3sdk前，请参考 `web3sdk快速搭建文档 <https://fisco-bcos-test.readthedocs.io/zh/latest/docs/web3sdk/compile.html>`_ 编译web3sdk;
   2. 搭建国密版web3sdk前，请参考 `国密版FISCO-BCOS快速搭建文档 <https://fisco-bcos-test.readthedocs.io/zh/latest/docs/guomi/index.html>`_ 搭建一条可用的国密版FISCO-BCOS链

```

## 生成客户端证书

国密版FISCO-BCOS生成节点证书同时会生成SDK证书，请参考 [国密版FISCO BCOS证书生成](https://fisco-bcos-test.readthedocs.io/zh/latest/docs/guomi/gen_cert.html#id4).

生成SDK证书时，直接将节点证书拷贝到`web3sdk/dist/conf`目录即可:

```bash
# 设web3sdk连接的节点位于~/mydata/node0目录, sdk名称为sdk1
# 设web3sdk位于~/mydata/web3sdk目录
$ cp ~/mydata/node0/data/sdk1/* ~/mydata/web3sdk/dist/conf
```


## 配置SDK

```eval_rst
.. admonition:: web3sdk配置

   web3sdk中开启国密算法，需要将 `encryptType` 选项设置为1，其他选项参考 `非国密版web3sdk配置 <https://fisco-bcos-test.readthedocs.io/zh/latest/docs/web3sdk/config_web3sdk.html#>`_：

   .. image:: imgs/guomiconfig.PNG
      :align: center


```


## check国密版web3sdk

```eval_rst
.. admonition:: 测试web3sdk与节点连接是否正常

   类似于 `非国密版web3sdk <https://fisco-bcos-test.readthedocs.io/zh/latest/docs/web3sdk/config_web3sdk.html#id3>`_ ， 国密版web3sdk也可以通过TestOk测试web3sdk与节点连接是否正常，若输出 ``INIT GUOMI KEYPAIR from Private Key`` 和 ``to balance`` 等日志，则说明国密版web3sdk与节点连接成功。

    .. code-block:: bash
    
       # 进入web3sdk目录(设源码位于~/mydata/web3sdk/dist中)
       $ cd ~/mydata/web3sdk/dist
       
       # 调用测试程序TestOk
       $ java -cp 'conf/:apps/*:lib/*' org.bcos.channel.test.TestOk
       ===================================================================
       =====INIT GUOMI KEYPAIR from Private Key
       ====generate kepair from priv key:bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd
       generate keypair data succeed
       ####create credential succ, begin deploy contract
       ####contract address is: 0xee80d7c98cb9a840b9c4df742f61336770951875
       ============to balance:4
       ============to balance:8
       ============to balance:12
       ... 此处省略若干行 ...

```

