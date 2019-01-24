## 示例分析 -- 网状模式组网

假设如图所示，联盟链中共有7个节点，链中有三个群组，分别为group1、group2、group3。group1中节点最多，共有5个节点，节点序号为0、1、2、4、5。

group2中有3个节点，节点序号为2、3、6。

group2中有3个节点，节点序号为0、4、6。


![](../../../images/enterprise/simple_simple1.png)

组网步骤如下：

### 构建gourp1

![](../../../images/enterprise/simple_simple2.png)

1. 序号为0、1、2、4、5的节点交换证书，放置于meta文件夹下。
2. 修改mchain.ini中的配置项，使其指向对应节点的ip，端口号，指定组id为group1
3. 使用build命令，生成安装包
4. 拷贝节点私钥到对应节点安装包的conf文件夹下
5. 推送节点安装包至对应服务器



### 构建group2

![](../../../images/enterprise/simple_simple3.png)

1. 序号为0、2、6的节点交换证书，放置于meta文件夹下。
2. 修改mchain.ini中的配置项，使其指向对应节点的ip，端口号，指定组id为group2
3. 使用build命令，生成安装包
4. 序号0、2的节点将生成的group.2.ini和group.2.genesis拷贝至**构建group1**时生成的节点conf文件夹下，并将新生成节点的config.ini与**构建group1**时生成的config.ini的section[p2p]中的node.X合并
5. 序号6的节点拷贝节点私钥到对应节点安装包的conf文件夹下，并推送节点安装包至对应服务器

### 构建group3

![](../../../images/enterprise/simple_simple4.png)

1. 序号为0、4、5、6的节点交换证书，放置于meta文件夹下。
2. 修改mchain.ini中的配置项，使其指向对应节点的ip，端口号，指定组id为group3
3. 使用build命令，生成安装包
4. 序号0、4、5、6的节点将生成的group.2.ini和group.2.genesis拷贝至已生成的节点conf文件夹下，并将新生成节点的config.ini与已生成的config.ini的section[p2p]中的node.X合并

至此，完成了如图所示的网状模式组网



