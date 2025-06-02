# 在GitHub的FISCO - BCOS社区提交文档PR的流程及案例

## 一、准备工作

###众所周知，GitHub在国内浏览器进入比较困难，总是出现网络问题，可通过win+R打开命令板，输入以入内容解决:

微软edge浏览器使用命令（注意文件路径是否一致）：

&"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" --host-rules="MAP github.com octocaptcha.com, MAP github.githubassets.com yelp.com, MAP *.githubusercontent.com githubusercontent.com" --host-resolver-rules="MAP octocaptcha.com 20.27.177.113, MAP yelp.com 199.232.240.116, MAP githubusercontent.com 199.232.176.133"



谷歌浏览器使用命令（注意文件路径是否一致）：

&"C:\Program Files\Google\Chrome\Application\chrome.exe" --host-rules="MAP github.com octocaptcha.com, MAP github.githubassets.com yelp.com, MAP *.githubusercontent.com githubusercontent.com" --host-resolver-rules="MAP octocaptcha.com 20.27.177.113, MAP yelp.com 199.232.240.116, MAP githubusercontent.com 199.232.176.133"

### 1. 注册GitHub账号

若你还没有GitHub账号，需要访问GitHub官网，点击右上角的“Sign up”按钮，按照提示完成注册流程。

### 2. 了解Git基础

在尝试提交PR前，你需要具备基本的Git知识。Git是一个分布式版本控制系统，广泛用于软件开发中跟踪代码更改。你需要熟悉以下基础命令：

- `git clone`：用于克隆一个远程仓库到本地。

- `git branch`：管理分支。

- `git checkout`：切换分支。

- `git add`：添加文件到暂存区。

- `git commit`：提交更改。

- `git push`：将本地更改推送到远程仓库。

### 3. 找到项目并阅读贡献指南

在GitHub上找到FISCO - BCOS社区项目，通常可以通过项目的README文件了解如何贡献代码，务必阅读项目的贡献指南（CONTRIBUTING.md）。

###4.在Git上配置ssh key步骤——

（1） 在Git命令板上输入: 

```bash

ssh-keygen -t rsa -C "XXX@XXX.com（GitHub上注册的邮箱地址）

//执行后一直回车即可

```

（2） 查看和获取ssh key——

```bash

cd ～/.ssh

cat id_rsa.pub

//将生成的内容全部复制

```

（3）到GitHub页面，点击个人头像找到设置"Settings"进入，再找到"SSH and GPG keys" ，点击绿色的"New SSH key"按钮，然后把复制的内容粘贴上去。

## 二、创建PR

### 1. Fork和克隆仓库

- **Fork仓库**：进入FISCO - BCOS社区项目主页，点击“Fork”按钮，将项目仓库复制到自己的GitHub账户下。

- **克隆仓库**：复制你Fork仓库的链接，在本地终端使用`git clone`命令将仓库克隆到本地。以你的仓库为例，使用以下命令：

```bash

git clone https://github.com/Aiw520/FISCO-BCOS-DOC.git

```

- **因为先前配置了SSH，你也可以这样克隆仓库**：复制你Fork仓库的SSH链接，在Git终端使用`git clone`命令将仓库克隆到本地。以你的仓库为例，使用以下命令：

```bash

git clone git@github.com:AiW520/FISCO-BCOS.git

```



### 2. 添加官方仓库为上游仓库

为了保持你的Fork与原始仓库同步，添加原始仓库为远程上游仓库。假设原始FISCO - BCOS - DOC仓库地址为`https://github.com/FISCO-BCOS/FISCO-BCOS-DOC.git`，使用以下命令：

```bash

git remote add upstream https://github.com/FISCO-BCOS/FISCO-BCOS-DOC.git

```

### 特别注意，到这一步可以去你的VS code上进行你的修改文档操作了。

###3. 创建新分支

每次提交PR之前，建议从`main`或`master`分支创建一个新的功能分支进行开发，避免直接在主分支上修改。以你创建的分支名为例，使用以下命令创建并切换到新分支：

```bash

git checkout -b LHZ.text

```



### 4. 同步官方文档

每次提交PR之前，均需要执行以下命令，同步最新的官方文档：

- 拉取官方文档`dev`分支的最新文档：

```bash

git fetch upstream dev

```

- 同步官方文档`dev`分支最新文档到本地：

```bash

git rebase upstream/dev

```

注：本步骤可能会有冲突，如果有冲突，请解决冲突。

- 将同步后的文档推到个人的git仓库：

```bash

git push origin -f

```



### 5. 修改并提交文档

在新分支上进行文档的修改或添加，完成后使用`git add`和`git commit`命令提交更改，例如：

```bash

git add .

git commit -m "详细描述提交的内容"

```



### 6. 推送分支到GitHub

使用`git push origin 分支名`将本地分支推送到自己的GitHub仓库。以你的分支为例，使用以下命令：

```bash

git push origin LHZ.text

```



### 7. 创建Pull Request

回到GitHub，在自己Fork的仓库页面上，点击“Compare & pull request”按钮，选择自己刚推送的分支（`LHZ.text`）和原始仓库的目标分支（通常是`main`或`master`），填写PR的标题和描述，清晰地说明文档的改动内容和原因。



## 三、文档格式说明

- 须采用markdown的格式编辑文章内容。

- 提交PR之前，建议先基于个人仓库的文档构建readthedocs，检查构建出来的文档显示是否符合预期，并在提交PR的时候，附带说明个人构建的readthedocs链接。可参考[readthedocs构建方法](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/articles/index.html) ，FISCO - BCOS - DOC的readthedocs配置选项如下：

| 设置字段 | 设置结果 |

| --- | --- |

| 默认分支 | release |

| 文档类型 | Sphinx |

| 所需的文件 | repuirements.txt |

| Python解释器 | CPython |

| 使用系统包 | 是 |

| Python配置文件 | conf.py |



## 四、Reviewer反馈和合入

- 提交PR后，Reviewer会将修改意见直接在GitHub上进行反馈，你也可以添加小助手微信FISCOBCOS010进行直接沟通。

- 最后当Reviewer合入PR时，你的文章就被录入到社区文档中了

