
# 贡献代码

非常感谢能有心为FISCO-BCOS社区贡献代码！

如果你希望很快速的贡献代码，OK，直接pull request到develop分支吧。

如果你希望以更专业更规范的方式开发，那么就想想自己要做什么吧？

- 我要开发新工程！
- 我要开发新特性！
- 我要修bug！
- 我发现了一个bug，但不知道怎么改。
- 我想看看是怎么release大版本的。

OK，本文都会告诉你相关的流程。

FISCO-BCOS社区的开发分支策略是[git-flow](https://github.com/nvie/gitflow)，感兴趣的小伙伴可以直接跳到本文附录1了解具体情况。

有任何问题都可以直接找FISCO-BCOS管理员，联系方式在附录2。

## 新工程开发

新工程是在FISCO-BCOS生态中创建一个全新的组件。通过迭代的方式不断完善组件的功能。在开发前，可联系FISCO-BCOS社区相关成员，获取一些指导。

**准备**

1. 充分的调研、需求分析和知识储备
2. 联系FISCO-BCOS社区
3. FISCO-BCOS管理员新建工程，并授权给相关开发人员

**开发**

- 分支策略和迭代开发流程与本文档其它部分相同

## 新特性开发

新特性是对FISCO-BCOS中某个工程功能的补充，开发周期较长，在开发时需要付出较长的时间和精力。在开发前，可联系FISCO-BCOS社区相关成员，获取一些指导。

**开发前**

1. 从develop分支fork出一个分支，叫feature-xxxxxx

2. 将feature-xxxxxx分支授权给相关特性负责人

> tips：
>
> 推荐在FISCO-BCOS的工程内开发，开发情况对其它开发者可见。请联系FISCO-BCOS管理员（邮件或issue），由管理员新建feature分支，并将分支授权给开发人员。

**开发中**

1. 不定期拉取develop分支代码，合入此feature分支
2. 多人开发feature时，通过pull request的方式，将个人分支的代码提交代码到此feature分支，特性负责人负责审核pull request

**开发后**

1. 拉取最新develop代码
2. 补充changelog
3. 提测
4. 测试通过后，pull request的方式提交到develop分支
5. FISCO-BCOS管理员审核pull request后合入develop分支

## bug修复

bug是不可避免的事情。在修bug时，可与FISCO-BCOS社区内的相关开发者充分的沟通，以获取更多的建议。

在修bug前，先对bug进行评估，判断bug的严重程度，再根据程度进行相应的流程：

（1）非紧急bug：文档错误，注释错误，较小的代码错误

（2）紧急bug：代码逻辑错误，代码兼容性错误

### 非紧急bug修复

直接以pull request的方式提交到develop分支，给FISCO-BCOS管理员审核。

### 紧急bug修复

**修复前**

1. 从master分支fork出一个分支，叫hotfix-xxxxx分支
2. 将feature-xxxxxx分支授权给相关负责人

> tpis：
>
> 与feature类似，若需要在FISCO-BCOS工程内修bug，请联系FISCO-BCOS管理员（邮件或issue），由管理员新建hotfix分支，并将分支授权给开发人员。

**修复中**

- 由于hotfix分支较多，为避免冲突，需尽快修复

**修复后**

1. 拉取最新master代码，补充changelog
2. 提测
3. 测试通过后，修改版本号（FISCO-BCOS节点代码的版本号在CMakeList.txt，Changelog.md和release_note.txt中）
4. 同时提pull request到master分支和develop分支，若改动较大，可只提交到develop分支
5. FISCO-BCOS管理员审核pull request后合入master分支和develop分支

## issue提交

不懂的问题，新发现的bug，改进的建议等，都直接提交到相应工程的issue处即可。

## release版本

FISCO-BCOS社区按照计划会不定期的发布版本，发布版本的流程如下。

1. 从develop分支fork一个release分支，确定更新版本号
2. 测试，代码扫描
3. 测试和扫描通过后用pull request的方式合入master分支和develop分支
4. 基于master分支release相应的版本，并打上tag



## 附录1：Git Branching Model: [git-flow](https://jeffkreeftmeijer.com/git-flow/)

git-flow将以develop分支为核心，将feature和release过程分开。支持多个feature并行的开发和测试，同时能够并行的release和改bug。

![](https://jeffkreeftmeijer.com/git-flow/git-flow.png)

**master分支**

* 绝对稳定分支，有版本号

**develop分支**

* develop分支从master分支fork出来，永远领先或等于master分支
* 也要求稳定，但稳定程度比master稍差，只确保feature的稳定
* 无版本号

**feature-*分支**

* feature-*分支从develop分支fork出来，是每个独立特性的开发分支
* feature-*分支由特性开发者管理，多人开发时，通过pull request的方式，由特性核心开发者审查后合入
* feature-*分支必须从develop分支拉取代码，同步到最新，再进行feature测试
* feature测试后才能将相关feature分支合入develop分支

**release-*分支**

* release-*分支根据排期，从develop分支fork出来，形成大版本号
* release-*分支在release测试时，回归的问题直接提交到release分支上
* release-*分支在release测试后，需同时合入dev分支和master分支

**hotfix-*分支**

* 要修bug时，从master拉出此分支
* 为避免冲突，bug要尽快修复
* bug修复代码完成后，需从master拉取最新的代码，再进行测试
* 测试通过后，同时合入develop分支和master分支，形成小版本号
* 若bug修复改动较大，可先合入develop分支，不合入master分支



## 附录2：FISCO-BCOS 管理员联系方式

邮箱：[service@fisco.com.cn](mailto:service@fisco.com.cn)

微信群：添加群管理员微信号fiscobcosfan，拉您入FISCO BCOS官方技术交流群。

群管理员微信二维码：![](https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/doc/FISCO-BCOS.jpeg)









