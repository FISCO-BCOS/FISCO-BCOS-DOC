# 13. 日志说明
标签： ``日志说明``  ``日志审计``
使用手册：


FISCO BCOS将区块链节点内关键步骤输出至于``nodeX/log/``目录下的`log_%YYYY%mm%dd%HH.%MM`的文件中，且定制了日志格式，方便用户通过日志查看链运行状态。

用户可以通过日志文件快速了解区块链节点的状态、交易执行错误的依据，可使用其他审计工具通过抓取日志从而获取区块链的实际状态。

```eval_rst
.. toctree::
   :maxdepth: 2

   log_description.md
   system_log_audit.md
```