## 示例分析 -- 星型模式组网

假设如图所示，联盟链中共有9个节点，其中序号为0的节点处于所有的group中，node8为后期扩容的节点


![](../../../images/enterprise/simple_star1.png)

组网步骤如下：

### 构建gourp1~7

![](../../../images/enterprise/simple_star2.png)

1. 序号为0的节点和序号为1、2、3、4、5、6、7的节点分别交换证书，放置于meta文件夹下。
2. 修改mchain.ini中的配置项，使其指向对应节点的ip，端口号，指定组id为group1~7
3. 使用build命令，生成安装包
4. 序号0的节点将生成的config.ini合并，并将group的相关配置放置节点conf文件夹下
5. 序号为1、2、3、4、5、6、7的节点拷贝节点私钥到对应节点安装包的conf文件夹下
6. 推送节点安装包至对应服务器



### 扩容节点加入group1

![](../../../images/enterprise/simple_simple1.png)

1. 序号为8的节点将自己的证书放置于meta文件夹下。
2. 序号为8的节点与序号为0或1的节点交涉，收集fisco-bcos二进制文件，config.ini,group.1.genesis和group.1.ini放置于某路径下，如./data
3. 修改mexpand.ini中的配置项，使其指向自己节点的ip，端口号，指定组id为group1
4. 使用expand命令，生成安装包
5. 拷贝节点私钥到对应节点安装包的conf文件夹下，并推送节点安装包至对应服务器
6. 启动节点，等待序号为0或1的节点将自己加入群组


至此，完成了如图所示的星型模式组网

