# Wasm 引擎

## FISCO BCOS 环境接口规范

FISCO BCOS 环境接口（FISCO BCOS Environment Interface，FBEI）规范中包含区块链底层平台[FISCO BCOS](https://gitee.com/FISCO-BCOS/FISCO-BCOS)向 Wasm 虚拟机公开的应用程序接口（Application Programming Interface，API）。FBEI 规范中所有的 API 均由 FISCO BCOS 负责实现，运行于 Wasm 虚拟机中的程序能够直接访问这些 API 以获取区块链的环境及状态。

### 数据类型

在 FBEI 规范中， API 参数及返回值的数据类型会使用`i32`、`i32ptr`及`i64`三种类型标记，其定义如下：

```eval_rst
.. list-table::
   :header-rows: 1

   * - 类型标记
     - 定义
   * - i32
     - 32位整数，与 Wasm 中i32类型的定义一致
   * - i32ptr
     - 32位整数，其存储方式与 Wasm 中i32类型一致，但是用于表示虚拟机中的内存偏移量
   * - i64
     - 64位整数，与 Wasm 中i64类型的定义一致
```

### API 列表

#### setStorage

**_描述_**

将键值对数据写入至区块链底层存储中以实现持久化存储。使用时需要先将表示键及值的字节序列存储在虚拟机内存中。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - keyOffset
     - i32ptr
     - 键在虚拟机内存中的存储位置的起始地址
   * - keyLength
     - i32
     - 键的长度
   * - valueOffset
     - i32ptr
     - 值在虚拟机内存中的存储位置的起始地址
   * - valueLength
     - i32
     - 值的长度
```

**_返回值_**

无。

```eval_rst
.. note::

   调用setStorage时，若提供的valueLength参数为0，则表示从区块链底层存储中删除键所对应的数据。在这种情况下，API的实现将直接跳过值的读取，因此valueOffset参数不用赋予有效值，一般直接置为0即可。
```

#### getStorage

**_描述_**

根据所提供的键，将区块链底层存储中对应的值读取至虚拟机内存中。使用时需要先将表示键的字节序列存储在虚拟机内存中，并提前分配好存储值的内存区域。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - keyOffset
     - i32ptr
     - 键在虚拟机内存中的存储位置的起始地址
   * - keyLength
     - i32
     - 键的长度
   * - valueOffset
     - i32ptr
     - 用于存放值的虚拟机内存起始地址
```

**_返回值_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 类型
     - 描述
   * - i32
     - 值的长度
```

#### getCallData

**_描述_**

将当前交易的输入数据拷贝至虚拟机内存中，使用时需要提前分配好存储交易输入数据的内存区域。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - resultOffset
     - i32ptr
     - 用于存放当前交易输入数据的虚拟机内存起始地址
```

**_返回值_**

无。

#### getCallDataSize

**_描述_**

获取当前交易输入数据的长度。

**_参数_**

无。

**_返回值_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 类型
     - 描述
   * - i32
     - 当前交易输入数据的长度
```

#### getCaller

**_描述_**

获取发起合约调用的调用方地址，使用时需要提前分配好存储调用方地址的内存区域。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - resultOffset
     - i32ptr
     - 用于存放调用方地址的虚拟机内存起始地址
```

**_返回值_**

无。

#### finish

**_描述_**

将表示返回值的字节序列传递至宿主环境并结束执行流程，宿主环境会将该其作为交易回执的一部分返回至调用方。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - dataOffset
     - i32ptr
     - 用于存放返回值的虚拟机内存起始地址
   * - dataLength
     - i32
     - 返回值的长度
```

**_返回值_**

无。

#### revert

**_描述_**

将表示异常信息的字节序列抛出至宿主环境，宿主环境会将其作为交易回执的一部分返回至调用者。调用此接口后，交易回执中的状态将会被标记为“已回滚”。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - dataOffset
     - i32ptr
     - 异常信息在虚拟机内存中的存储位置的起始地址
   * - dataLength
     - i32
     - 异常信息的长度
```

**_返回值_**

无。

```eval_rst
.. note::

   异常信息需要为人类可读的字符串，以方便快速定位异常原因。
```

#### log

**_描述_**

创建一条交易日志。可以至多为该日志创建 4 个日志索引。使用时需要先将表示日志数据及其索引的字节序列存储在虚拟机内存中。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - dataOffset
     - i32ptr
     - 日志数据在虚拟机内存中的存储位置的起始地址
   * - dataLength
     - i32
     - 日志数据的长度
   * - topic1
     - i32ptr
     - 第 1 个日志索引的虚拟机内存起始地址，没有时置0
   * - topic2
     - i32ptr
     - 第 2 个日志索引的虚拟机内存起始地址，没有时置0
   * - topic3
     - i32ptr
     - 第 3 个日志索引的虚拟机内存起始地址，没有时置0
   * - topic4
     - i32ptr
     - 第 4 个日志索引的虚拟机内存起始地址，没有时置0
```

**_返回值_**

无。

```eval_rst
.. note::

   日志索引的长度需要为恰好为32字节。
```

#### getTxOrigin

**_描述_**

获取调用链中最开始发起合约调用的调用方地址，使用时需要提前分配好存储调用方地址的内存区域。与`getCaller`接口不同，本接口获取到的调用方地址一定为外部账户地址。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - resultOffset
     - i32ptr
     - 用于存放调用方地址的虚拟机内存起始地址
```

**_返回值_**

无。

#### getBlockNumber

**_描述_**

获取当前块高。

**_参数_**

无。

**_返回值_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 类型
     - 描述
   * - i64
     - 当前块高
```

#### getBlockTimestamp

**_描述_**

获取当前块的时间戳。

**_参数_**

无。

**_返回值_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 类型
     - 描述
   * - i64
     - 当前块的时间戳
```

#### call

**_描述_**

发起外部合约调用，使用时需要先将表示调用参数的字节序列存储在虚拟机内存中。调用此接口后执行流程会陷入阻塞，直至外部合约调用结束或发生异常。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - addressOffset
     - i32ptr
     - 被调用合约地址在虚拟机内存中的存储位置的起始地址
   * - dataOffset
     - i32ptr
     - 调用参数在虚拟机内存中的存储位置的起始地址
   * - dataLength
     - i32
     - 调用参数的长度
```

**_返回值_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 类型
     - 描述
   * - i32
     - 调用状态，0表示成功，否则表示失败
```

#### getReturnDataSize

**_描述_**

获取外部合约调用的返回值长度，此接口仅能在外部合约调用成功后调用。

**_参数_**

无。

```eval_rst
.. list-table::
   :header-rows: 1

   * - 类型
     - 描述
   * - i32
     - 外部合约调用的返回值长度
```

#### getReturnData

获取外部合约调用的返回值，使用时需要根据`getReturnDataSize`的返回结果提前分配好存储返回值内存区域。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - resultOffset
     - i32ptr
     - 用于存放返回值的虚拟机内存起始地址
```

**_返回值_**

无。

## FISCO BCOS Wasm约束

FISCO BCOS Wasm 合约接口（FISCO BCOS Wasm Contract Interface，FBWCI）规范中包含关于合约文件格式及内容的约定。符合 FBWCI 规范要求合约文件能够在区块链底层平台[FISCO BCOS](https://gitee.com/FISCO-BCOS/FISCO-BCOS)内置的 Wasm 虚拟机中运行。

### 传输格式

所有的合约件必须以[WebAssembly 二进制编码](https://webassembly.github.io/spec/core/binary/index.html)格式保存及传输。

### 符号导入

合约文件仅能导入在FBEI中规定的接口，所有的接口都需要从名为`bcos`的命名空间中导入，且签名必须与 BCOS 环境接口规范中所声明的接口签名保持一致。除`bcos`命令空间外，还有一个名为`debug`的特殊命名空间。`debug`命名空间中所声明的函数的主要用于虚拟机的调试模式，在正式的生产环境中该命名空间不会被启用，详情请参考调试模式。

### 符号导出

合约文件必须恰好导出下列 3 个符号：

| 符号名 | 描述                                                                                                                                                                         |
| ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| memory | 共享线性内存，用于与宿主环境交换数据                                                                                                                                         |
| deploy | 初始化入口，无参数且无返回值，用于完成状态初始化的工作。当合约被初次部署至链上时，宿主环境会主动调用该函数                                                                   |
| main   | 执行入口，无参数且无返回值，用于执行具体的合约逻辑。当有发往该合约的交易时，宿主环境会主动调用该函数。当交易成功执行时，该函数正常退出；否则向宿主环境抛出异常原因并回滚交易 |

### 调试模式

调试模式是一种用于调试虚拟机的特殊模式，通过`debug`命名空间为合约提供了一组额外调试接口。但是在正式的生产环境中，若合约字节码尝试从`debug`命名空间中导入符号，则会被拒绝部署。`debug`命名空间中可用的接口如下所示，所有接口均没有返回值：

#### print32

**_描述_**

在区块链底层的日志中输出一个 32 位整数值。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - value
     - i32
     - 32位整数值
```

#### print64

**_描述_**

在区块链底层的日志中输出一个 64 位整数值。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - value
     - i64
     - 64位整数值
```

#### printMem

**_描述_**

以可打印字符的形式在区块链底层的日志中输出一段虚拟机内存中的内容。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - offset
     - i32
     - 内存区域的起始地址
   * - len
     - i32
     - 内存区域的长度
```

#### printMemHex

以 16 进制字符串的形式在区块链底层的日志中输出一段虚拟机内存中的内容。

**_参数_**

```eval_rst
.. list-table::
   :header-rows: 1

   * - 参数名
     - 类型
     - 描述
   * - offset
     - i32
     - 内存区域的起始地址
   * - len
     - i32
     - 内存区域的长度
```

### Start function

[Start function](https://webassembly.github.io/spec/core/syntax/modules.html#start-function) 会在虚拟机载入合约字节码时自动执行，而此时宿主环境尚无法获得虚拟机提供的共享内存的访问权限，因而可能会导致引发运行时异常，因此 FBWCI 规范规定合约文件中不允许存在 start function。
