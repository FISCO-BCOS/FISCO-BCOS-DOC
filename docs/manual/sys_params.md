# 动态配置系统参数

FISCO BCOS系统目前主要包括如下系统参数(未来会扩展其他系统参数)：


```eval_rst
+-----------------+-----------+---------------------------------+
| 系统参数        | 默认值    |             含义                |
+=================+===========+=================================+
| tx_count_limit  | 1000      | 一个区块中可打包的最大交易数目  |
+-----------------+-----------+---------------------------------+
| tx_gas_limit    | 300000000 | 一个区块最大gas限制             |
+-----------------+-----------+---------------------------------+

```

控制台提供 **[setSystemConfigByKey](./console.html#setsystemconfigbykey)** 命令来修改这些系统参数，**[getSystemConfigByKey](./console.html#getsystemconfigbykey)** 命令可查看系统参数的当前值：


```eval_rst
.. important::

    不建议随意修改tx_count_limit和tx_gas_limit，如下情况可修改这些参数：

    - 机器网络或CPU等硬件性能有限：调小tx_count_limit，或降低业务压力；
    - 业务逻辑太复杂，执行区块时gas不足：调大tx_gas_limit。
```

```bash
# 设置一个区块可打包最大交易数为500
> setSystemConfigByKey tx_count_limit 500
# 查询tx_count_limit
> getSystemConfigByKey tx_count_limit
[500]

# 设置区块gas限制为400000000
> getSystemConfigByKey tx_gas_limit 400000000
> getSystemConfigByKey
[400000000]
```
