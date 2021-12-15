# Hardware requirements

```eval_rst
.. note::
    - FISCO BCOS supports x86_ 64 and aarch64 (ARM) architecture CPU
    - Since multiple nodes share network bandwidth, CPU, and memory resources, it is not recommended to configure too much nodes on one machine in order to ensure the stability of service.

```

The following table is a recommended configuration for single-group and single-node. Node consumes resources in a linear relationship with the number of groups. You can configure the number of nodes reasonably according to actual business requirement and machine resource.

```eval_rst
+----------+---------+---------------------------------------------+
| configuration     | minimum | recommended                                    |
+==========+=========+=============================================+
| CPU      | 1.5GHz  | 2.4GHz                                      |
+----------+---------+---------------------------------------------+
| memory     | 1GB     | 8GB                                         |
+----------+---------+---------------------------------------------+
| core     | 1 core     | 8 cores                                         |
+----------+---------+---------------------------------------------+
| bandwidth     | 1Mb     | 10Mb                                        |
+----------+---------+---------------------------------------------+
```

# Supported Platforms

- CentOS 7.2+
- Ubuntu 16.04
- macOS 10.14+
- Kylin OS V10
- deepin