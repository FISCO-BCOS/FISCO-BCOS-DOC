# 1. PR 代码规范


### 文章贡献PR操作指引

如果你已经是社区贡献者，可直接按照如下操作完成文章PR提交；如果你初次尝试PR贡献，请点击了解[提PR基本操作指引](https://mp.weixin.qq.com/s/Uq5r1IaZfelWnhCThHSKXw)。


#### 1、提PR的预置条件：Fork FISCO-BCOS-DOC到个人github仓库

1. step1：进入[注册GitHub账号](https://github.com/join)
2. step2: Fork [FISCO-BCOS-DOC](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC) 到个人仓库

#### 2、分支说明

PR须提到FISCO-BCOS-DOC的dev分支

#### 3、必备工具：git  [点击参考](https://www.liaoxuefeng.com/wiki/896043488029600/896067074338496)

1. mac系统：brew install git
2. windows系统：点击[下载安装](https://git-scm.com/downloads)
3. ubuntu系统：sudo apt install git
4. centos系统：sudo yum install git

#### 4、主要的git命令：

（以下步骤只需要执行一次即可）

- 将官方的[FISCO-BCOS-DOC](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC)添加为upstream: git remote add upstream



（以下步骤每次都要操作）

**每次提交PR之前，均需要执行以下命令，同步最新的官方文档：**

1. 拉取官方文档dev分支的最新文档：git fetch upstream dev

2. 同步官方文档dev分支最新文档到本地：git rebase upstream/dev

   (注：本步骤可能会有冲突，如果有冲突，请解决冲突，点击参考[冲突解决方法](https://www.jianshu.com/p/4c30eb30323a))

3. 将同步后的文档推到个人的git仓库：git push origin -f

**提交个人文档的主要命令**

git add, git commit, git push等，[点击参考](https://www.jianshu.com/p/2e1d551b8261)。

#### 5、文档格式说明

1. 须采用markdown的格式编辑文章内容，[点击参考markdown语法](https://www.runoob.com/markdown/md-tutorial.html)。
2. (本步骤不强制) 提交PR之前，建议先基于个人仓库的文档构建readthedocs，检查构建出来的文档显示是否符合预期，并在提交PR的时候，附带说明个人构建的readthedocs链接。

点击参考[readthedocs构建方法](https://www.jianshu.com/p/d1d59d0cd58c)， FISCO-BCOS-DOC的readthedocs配置选项参考下表：

| **设置字段**   | **设置结果**     |
| - | - |
| 默认分支       | release          |
| 文档类型       | Sphinx           |
| 所需的文件     | repuirements.txt |
| Python解释器   | CPython          |
| 使用系统包     | 是               |
| Python配置文件 | conf.py         |

#### 6、Reviewer反馈和合入

提交PR后，Reviewer会将修改意见直接在GitHub上进行反馈，你也可以添加小助手微信FISCOBCOS010进行直接沟通；最后当Reviewer合入PR时，你的文章就被录入啦！

### 文章PR贡献写作规范

- 开头署名为撰稿人姓名，也可以展现个人所属公司或学校，例如，作者：张三 | FISCO BCOS核心开发者；
- 文章内容尽量包含引言介绍，段与段之间自然衔接和过度，文末总结；
- 文章应保证文句通顺、无语病，表述不会引起读者误解；
- 实操类文章需确保技术点准确无误，并能完成测试跑通；
- 如果文章中涉及到相关技术文档或代码仓库链接，为避免链接失效，请使用FISCO BCOS官方链接。