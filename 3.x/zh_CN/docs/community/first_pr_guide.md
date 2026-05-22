# 如何完成 FISCO BCOS 的第一个 PR —— 实战教程

-----

### 代码贡献PR操作指引（实战版）

> 本教程以一个真实的 Bug 修复为例，带你走完 Fork → Clone → 修复 → 提交 PR 的完整流程，适合第一次参与开源项目的新手。

如果你已经是社区贡献者，可直接参考[PR代码规范](./pr.md)；如果你初次尝试PR贡献，请跟随本教程完成你的第一个PR。

#### 1、什么样的改动适合作为第一个 PR？

对于开源项目的第一次贡献，最佳切入点是**小型但有意义**的改动，例如：

- 命令行参数校验缺失或错误
- 文档拼写错误
- 变量命名不当
- 缺少边界检查

我们这次修复的就是一个经典的**命令行参数个数判断错误**问题。

#### 2、提PR的预置条件：Fork FISCO-BCOS到个人github仓库

1. 进入[注册GitHub账号](https://github.com/join)
2. 打开 [FISCO-BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS) 仓库页面，点击右上角 **Fork** 按钮，Fork 到个人仓库

#### 3、必备工具：git

1. mac系统：brew install git
2. windows系统：点击[下载安装](https://git-scm.com/downloads)
3. ubuntu系统：sudo apt install git
4. centos系统：sudo yum install git

#### 4、Clone到本地

Fork完成后，将你自己的仓库克隆到本地：

```bash
git clone https://github.com/<你的用户名>/FISCO-BCOS.git
cd FISCO-BCOS
```

添加上游仓库（即FISCO BCOS官方仓库）作为remote，方便后续同步最新代码：

```bash
git remote add upstream https://github.com/FISCO-BCOS/FISCO-BCOS.git
```

验证remote配置：

```bash
git remote -v
```

预期输出：

```
origin    https://github.com/<你的用户名>/FISCO-BCOS.git (fetch)
origin    https://github.com/<你的用户名>/FISCO-BCOS.git (push)
upstream  https://github.com/FISCO-BCOS/FISCO-BCOS.git (fetch)
upstream  https://github.com/FISCO-BCOS/FISCO-BCOS.git (push)
```

> - `origin` 指向你自己的Fork仓库（你有写入权限）
> - `upstream` 指向官方仓库（你只有读取权限）

#### 5、发现问题

##### 5.1 阅读源码

在 `bcos-sdk/sample/tx/tx_sign_perf.cpp` 中，`usage()` 函数明确描述了程序的用法：

```cpp
void usage()
{
    printf("Usage: tx_sign_perf isSM txCount\n");
    // ...
}
```

程序需要 **2 个参数**：`isSM`（是否使用国密）和 `txCount`（交易数量）。

##### 5.2 定位 Bug

查看 `main` 函数的参数校验：

```cpp
int main(int argc, char** argv)
{
    if (argc < 2)        // 只检查了 argv[1] 是否存在
    {
        usage();
    }

    bool smCrypto = (std::string(argv[1]) == "true");
    uint32_t txCount = std::stoul(argv[2]);  // 但这里访问了 argv[2]！
    // ...
}
```

**问题**：

| 参数位置 | 内容 | `argc < 2` 是否保证安全 |
|---------|------|----------------------|
| `argv[0]` | 程序名 | 始终存在 |
| `argv[1]` | isSM | `argc >= 2` 时存在 |
| `argv[2]` | txCount | **`argc >= 2` 不保证存在！需要 `argc >= 3`** |

当用户只传入一个参数（如 `./tx_sign_perf true`）时，`argc == 2`，通过了检查，但访问 `argv[2]` 是越界的，会导致**未定义行为或段错误**。

##### 5.3 修复方案

将参数校验改为 `argc < 3`：

```cpp
if (argc < 3)    // argv[0]=程序名, argv[1]=isSM, argv[2]=txCount，至少需要 3 个
{
    usage();
}
```

#### 6、创建特性分支并修复

##### 6.1 创建分支

**不要直接在master上修改**，创建一个有意义的分支名：

```bash
git checkout -b fix/tx-sign-perf-argc-check
```

> 分支命名建议：
> - `fix/` 前缀表示修复Bug
> - `feat/` 前缀表示新功能
> - `docs/` 前缀表示文档改动

##### 6.2 进行修改

编辑 `bcos-sdk/sample/tx/tx_sign_perf.cpp`，将第 137 行：

```cpp
if (argc < 2)
```

改为：

```cpp
if (argc < 3)
```

**仅此一行**，改动最小化，专注于修复目标问题。

##### 6.3 查看改动

```bash
git diff
```

预期输出：

```diff
-     if (argc < 2)
+     if (argc < 3)
```

##### 6.4 验证修复（可选）

如果你有本地构建环境，编译后可以这样测试：

```bash
# 传入 0 个参数，应打印 usage 并退出
./tx_sign_perf

# 传入 1 个参数，应打印 usage 并退出（修复前会崩溃！）
./tx_sign_perf true

# 传入 2 个参数，正常运行
./tx_sign_perf true 30000
```

#### 7、提交代码

##### 7.1 暂存并提交

```bash
git add bcos-sdk/sample/tx/tx_sign_perf.cpp
git commit -m "fix: correct argc check in tx_sign_perf to prevent argv[2] out-of-bounds access

The program requires 2 arguments (isSM and txCount) but only checked
argc < 2, which allows argv[2] to be accessed out-of-bounds when only
one argument is provided. Changed to argc < 3."
```

> **Commit Message 规范**：
> - 第一行以类型前缀开头：`fix:`、`feat:`、`docs:`、`refactor:` 等
> - 第一行简短总结（50 字符以内）
> - 空一行后写详细说明（解释为什么改，而不是改了什么）

##### 7.2 推送到你的Fork

```bash
git push origin fix/tx-sign-perf-argc-check
```

#### 8、创建 Pull Request

1. 打开你Fork的仓库页面 `https://github.com/<你的用户名>/FISCO-BCOS`
2. GitHub会自动检测到你推送了新分支，页面顶部会出现一个 **"Compare & pull request"** 按钮，点击它
3. 填写PR信息：

   **Title**（标题）：

   `fix: correct argc check in tx_sign_perf to prevent argv[2] out-of-bounds access`

   **Description**（描述）：

   ```markdown
   ## 问题
   `tx_sign_perf.cpp` 的 `main` 函数中，命令行参数个数检查为 `argc < 2`，
   但程序实际访问了 `argv[1]` 和 `argv[2]`，需要至少 3 个参数。
   当用户只传入 1 个参数时，访问 `argv[2]` 会导致未定义行为或段错误。

   ## 修复
   将 `argc < 2` 改为 `argc < 3`。

   ## 影响范围
   仅影响 `bcos-sdk/sample/tx/tx_sign_perf.cpp`。

   ## 测试
   - `./tx_sign_perf` 打印 usage 并退出
   - `./tx_sign_perf true` 打印 usage 并退出（修复前会崩溃）
   - `./tx_sign_perf true 30000` 正常运行
   ```

4. 确认base分支指向 `FISCO-BCOS/FISCO-BCOS` 的 `master`，head分支指向你的 `fix/tx-sign-perf-argc-check`
5. 点击 **"Create pull request"**

#### 9、提交后的注意事项

| 事项 | 说明 |
|------|------|
| **签署 CLA** | 首次提交PR可能需要签署贡献者许可协议（Contributor License Agreement），按照机器人提示操作即可 |
| **响应 Code Review** | 维护者可能会提出修改意见，及时在PR中回复并更新代码 |
| **保持分支同步** | 如果上游有更新，用以下命令同步并rebase：`git fetch upstream`，`git rebase upstream/master`，`git push origin fix/xxx --force` |
| **一个 PR 只做一件事** | 不要混合多个不相关的修改，方便Review |

**每次提交PR之前，均需要执行以下命令，同步最新的官方代码：**

1. 拉取官方master分支的最新代码：`git fetch upstream`

2. 同步官方master分支最新代码到本地：`git rebase upstream/master`

   (注：本步骤可能会有冲突，如果有冲突，请解决冲突，点击参考[冲突解决方法](https://gitee.com/help/articles/4194))

3. 将同步后的代码推到个人的git仓库：`git push origin -f`

#### 10、完整流程回顾

```
GitHub Fork
    |
    v
git clone（你的Fork）
    |
    v
git remote add upstream（添加上游）
    |
    v
git checkout -b fix/xxx（创建分支）
    |
    v
编辑代码 → 测试
    |
    v
git add → git commit（提交）
    |
    v
git push origin fix/xxx（推送到你的Fork）
    |
    v
GitHub上创建Pull Request → 等待Review → 合并
```

#### 11、常见问题

##### Q1：Fork后主仓库更新了怎么办？

```bash
git fetch upstream
git checkout master
git merge upstream/master
git push origin master
```

##### Q2：PR被要求修改怎么办？

直接在本地修改，然后：

```bash
git add .
git commit -m "fix: address review comments"
git push origin fix/tx-sign-perf-argc-check
```

新的commit会自动出现在PR中。

##### Q3：如何让commit历史更干净？

如果不想增加新的commit，可以用 `--amend`：

```bash
git add .
git commit --amend
git push origin fix/tx-sign-perf-argc-check --force
```

##### Q4：PR被拒绝了怎么办？

不要气馁！维护者通常会说明原因，根据反馈修改后重新提交即可。开源社区欢迎一切有意义的贡献。

#### 12、Reviewer反馈和合入

提交PR后，Reviewer会将修改意见直接在GitHub上进行反馈，你也可以添加小助手微信FISCOBCOS010进行直接沟通；最后当Reviewer合入PR时，你的代码就被录入啦！

#### 13、总结

这次修复的核心改动只有 **一行**，但它解决了一个真实的内存安全问题。对于第一个PR来说：

1. **改动小** → Review周期短，通过率高
2. **问题明确** → Reviewer一眼就能看懂
3. **有价值** → 修复了可能导致段错误的Bug
4. **有测试方法** → 可以构造用例验证修复效果

希望这个实战示例能帮助你迈出为FISCO BCOS贡献代码的第一步！
