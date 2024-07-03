# 3. PR code specification

-----
### Article Contribution PR Operating Guidelines

If you are already a community contributor, you can directly complete the PR submission as follows；If you first try PR contribution, please refer to [document](https://mp.weixin.qq.com/s/_w_auH8X4SQQWO3lhfNrbQ)。


#### 1. Preset condition of PR: Fork FISCO-BCOS-DOC to personal github warehouse

1. Step1: Enter [Register GitHub account](https://github.com/join)
2. step2: Fork [FISCO-BCOS-DOC](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC) To personal warehouse

#### 2. Branch Description

PR must mention the release-3 branch of FISCO-BCOS-DOC

#### 3. Essential tool: git [click for reference](https://gitee.com/help/articles/4106)

1. Mac system: brew install git
2.windows system: click [download and install](https://git-scm.com/downloads)
3. Ubuntu system: sudo apt install git
4.centos system: sudo yum install git

#### 4. Main git commands:

(The following steps only need to be performed once)

- Will the official [FISCO-BCOS-DOC](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC)Add as upstream: git remote add upstream



(The following steps should be done every time)

**Before submitting a PR, run the following command to synchronize the latest official documents:**

1. Pull the latest document from the official document release-3 branch: git fetch upstream release-3

2. Synchronize the latest document of the official document release-3 branch to the local: git rebase update / release-3

   (Note: This step may have a conflict, if there is a conflict, please resolve the conflict, click the reference [conflict resolution](https://gitee.com/help/articles/4194))

3. Push the synchronized document to your personal git repository: git push origin -f

**Main commands for submitting personal documents**

git add, git commit, git push, etc. [click for reference](https://gitee.com/help/articles/4114)。

#### 5. Document format description

1. The content of the article must be edited in markdown format, [click to refer to markdown syntax](https://www.runoob.com/markdown/md-tutorial.html)。
2. (This step is not mandatory) Before submitting a PR, it is recommended to build readthedocs based on the documents of the personal repository, check whether the built documents display as expected, and attach a link to the readthedocs that describes the personal build when submitting a PR。

Click on the reference [readthedocs build method](https://www.jianshu.com/p/d1d59d0cd58c)and the readthedocs configuration options of FISCO-BCOS-DOC refer to the following table:

| **Setting Fields**   | **Setting Results**     |
| - | - |
| Default Branch| release-3        |
| Document Type| Sphinx           |
| Required Documents| repuirements.txt |
| Python interpreter| CPython          |
| Using System Packages| Yes|
| Python configuration file| conf.py         |

#### 6. Reviewer Feedback and Integration

After submitting the PR, Reviewer will directly feedback the modification comments on GitHub, you can also add a small assistant WeChat FISCOBCOS010 for direct communication；Finally, when Reviewer joins the PR, your article is entered！

### Article PR Contribution Writing Norms

-The initial signature is the name of the contributor, which can also show the company or school to which the individual belongs, for example, by Zhang San| FISCO BCOS Core Developer；
- The content of the article contains the introduction as far as possible, the natural connection and transition between paragraphs, and the summary at the end of the article；
- The article should ensure that the sentence is smooth and free of speech disorders, and that the presentation does not cause misunderstanding by the reader；
-Practical articles need to ensure that the technical points are accurate and can complete the test run；
-If the article involves links to relevant technical documents or code repositories, please use the FISCO BCOS official link to avoid link failure。