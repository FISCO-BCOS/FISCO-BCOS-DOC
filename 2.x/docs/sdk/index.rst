##############################################################
多种语言SDK
##############################################################

标签： ``SDK``

----

FISCO BCOS区块链向外部暴露了接口，外部业务程序能够通过FISCO BCOS提供的SDK来调用这些接口。开发者只需要根据自身业务程序的要求，选择相应语言的SDK，用SDK提供的API进行编程，即可实现对区块链的操作。

**对接应用**

目前，SDK接口可实现的功能包括（但不限于）：

* 合约操作
  * 合约编译、部署、查询
  * 交易发送、上链通知、参数解析、回执解析
* 链管理
  * 链状态查询、链参数设置
  * 组员管理
  * 权限设置
* 其它
  * SDK间的相互消息推送（AMOP）

**内置控制台**

为了方便开发者，部分SDK内置了控制台的功能。开发者可直接通过用命令行进行上述功能的操作。如编译合约、部署合约、发送交易、查询交易、链管理等等。

**多种语言SDK**

目前，FISCO BCOS提供的SDK包括：

* `Java SDK <./java_sdk/index.html>`_ （稳定、功能强大、无内置控制台，推荐使用）
* `Web3SDK <./java_sdk.html>`_ （旧版Java SDK）
* `Python SDK <./python_sdk/index.html>`_  （简单轻便、有内置控制台）
* `Node-js SDK <./nodejs_sdk/index.html>`_ （简单轻便、有内置控制台）
* `Go SDK <./go_sdk/index.html>`_ （简单轻便、有内置控制台）
* `C# SDK <./csharp_sdk/index.html>`_ （完整适配Json RPC API）
* `Android SDK <./android_sdk/index.html>`_ （移动端 Android SDK，实现对合约的部署及调用）

.. toctree::
   :hidden:

   java_sdk/index.md
   java_sdk.md
   nodejs_sdk/index.rst
   python_sdk/index.rst
   go_sdk/index.rst
   csharp_sdk/index.rst
   android_sdk/index.md
   

