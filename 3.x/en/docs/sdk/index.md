# 1. Multilingual SDK

Tag: "SDK"

----

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

FISCO BCOS 3.x version of the multilingual SDK is designed with**Hierarchical architecture**Implementation, from bottom to top, is divided into common base component layer, CPP-SDK layer, C-SDK layer, multi-language, multi-terminal access layer。The core function is implemented by the underlying CPP-SDK, and the upper layer is easily adapted for multi-language access, which can quickly adapt to access multi-language SDK。

- **Common Foundation Components**Encapsulating encryption algorithms, communication protocols, network protocols, encryption machine protocols；
- **CPP-SDK Layer**: Based on the common basic components, realize the network management, group management, AMOP communication, event mechanism, ledger and RPC interface related to blockchain connection, using C++Implementation of CPP-SDK by way of encapsulation；
- **C-SDK layer**CPP-SDK-based C-SDK with one layer of C interface call；
- **Multi-language, multi-terminal access layer**Through the C-SDK interface, you can quickly adapt to Java, golang, nodejs, python, rust, iOS, Android and other multi-language SDKs, and compatible with Windows, Linux, macOS, KyLin multi-operating system and X86, ARM (including M1) and other platforms。

The layered architecture diagram of the SDK is as follows:

![](../../images/sdk/sdk_layered_architecture.png)
