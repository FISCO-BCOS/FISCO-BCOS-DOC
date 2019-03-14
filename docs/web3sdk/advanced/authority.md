# 权限控制

```eval_rst

.. admonition:: 参考资料

   - `权限控制API接口 <https://github.com/FISCO-BCOS/web3sdk/blob/master-1.2/src/main/java/org/bcos/contract/tools/AuthorityManager.java>`_ 
   - `权限控制设计文档 <https://github.com/FISCO-BCOS/Wiki/tree/master/FISCO%20BCOS%E6%9D%83%E9%99%90%E6%A8%A1%E5%9E%8B>`_ 

   - `联盟链权限控制体系说明 <https://github.com/FISCO-BCOS/Wiki/tree/master/%E5%8C%BA%E5%9D%97%E9%93%BE%E7%9A%84%E6%9D%83%E9%99%90%E4%BD%93%E7%B3%BB>`_

```

```eval_rst

.. admonition:: 权限控制API
   
   权限控制配置
    在applicationContext.xml中配置账户和账户私钥(部署权限控制合约时，用GOD账户和GOD账户私钥): 
      
      .. code-block:: xml
       
         <!-- 系统合约地址配置置-->
         <bean id="toolConf" class="org.bcos.contract.tools.ToolConf">
         <!--系统合约-->
         <property name="systemProxyAddress" value="0x919868496524eedc26dbb81915fa1547a20f8998" />
         <!--GOD账户的私钥-->（注意去掉“0x”）
         <property name="privKey" value="bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd" />
         <!--God账户-->
         <property name="account" value="0x776bd5cf9a88e9437dc783d6414bccc603015cf0" />
         <property name="outPutpath" value="./output/" />
         </bean>
   
   权限控制接口命令
    .. code-block:: bash

       ./web3sdk ARPI_Model 
       ./web3sdk PermissionInfo 
       ./web3sdk FilterChain addFilter name1 version1 desc1 
       ./web3sdk FilterChain delFilter num 
       ./web3sdk FilterChain showFilter 
       ./web3sdk FilterChain resetFilter 
       ./web3sdk Filter getFilterStatus num 
       ./web3sdk Filter enableFilter num 
       ./web3sdk Filter disableFilter num 
       ./web3sdk Filter setUsertoNewGroup num account 
       ./web3sdk Filter setUsertoExistingGroup num account group 
       ./web3sdk Filter listUserGroup num account 
       ./web3sdk Group getBlackStatus num account 
       ./web3sdk Group enableBlack num account 
       ./web3sdk Group disableBlack num account 
       ./web3sdk Group getDeployStatus num account 
       ./web3sdk Group enableDeploy num account 
       ./web3sdk Group disableDeploy num account 
       ./web3sdk Group addPermission num account A.address fun(string) 
       ./web3sdk Group delPermission num account A.address fun(string) 
       ./web3sdk Group checkPermission num account A.address fun(string) 
       ./web3sdk Group listPermission num account 

```

