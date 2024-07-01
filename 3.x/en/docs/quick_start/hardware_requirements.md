# 1. Hardware and System Requirements

Tags: "hardware requirements" "operating system" "development manual" "memory requirements" "domestic operating system" "" ARM "" Kirin "" APPLE M1 ""

----

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check < https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

## Hardware Requirements

```eval_rst
.. note::
    - FISCO BCOS supports CPU for x86 _ 64 and aarch64 (ARM) architectures
    - Because multiple groups of nodes share network bandwidth, CPU, and memory resources, it is not recommended to configure too many nodes on a machine to ensure service stability.。
```

The following table shows the recommended configurations for a single group and a single node. The resource consumption of nodes is linearly related to the number of groups. You can reasonably configure the number of nodes according to the actual business needs and machine resources.。


|   **Configuration**   |   **Minimum Configuration**  |   **Recommended Configuration**   |
| :--------: |  :--------:  | :-------: |
| Memory|  8GB   |   32GB  |
| Core|  4 Nuclear|    8 Nuclear|
| Bandwidth|  1Mb   |   10Mb  |

## Operating System

- CentOS 7.2+
- Ubuntu 18.04+
- macOS 10.14+
