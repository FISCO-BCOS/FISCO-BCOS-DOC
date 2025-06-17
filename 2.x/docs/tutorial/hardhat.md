1、更新资源包：`<font style="color:#000000;">sudo apt update</font>`

2、安装curl和git：`sudo apt install curl git`

3、安装官方Node.js 22.x脚本：`curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -`

4、安装nodejs：`sudo apt-get install -y nodejs`

5、查看node和npm的版本号：

6、创建并进入hardhat项目目录：`mkdir hardhat-tutorial   cd hardhat-tutorial/`

7、初始化一个Node.js 项目：`npm init`

8、安装Hardhat开发环境依赖：`npm install --save-dev hardhat`

9、初始化hardhat项目：`npx hardhat init`

11、安装一个可以把内置默认值集中到hardhat的插件：`npm install --save-dev @nomicfoundation/hardhat-toolbox`

12、安装dotenv作为开发依赖：`npm install dotenv`

13、编辑Hardhat核心配置文件：`gedit hardhat.config.js`（退出到<u>项目目录</u>）

```plain
require("@nomicfoundation/hardhat-toolbox");
/** @type import('hardhat/config').HardhatUserConfig */
module.exports = {
solidity: "0.8.28",
};
```

14、`cd contracts/`

15、编写Lock智能合约：`gedit Lock.sol`（在contracts目录）

```plain
// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.28;
// Uncomment this line to use console.log
// import "hardhat/console.sol";
contract Lock {
uint public unlockTime;
address payable public owner;
event Withdrawal(uint amount, uint when);
constructor(uint _unlockTime) payable {
require(
block.timestamp < _unlockTime,
"Unlock time should be in the future"
);
unlockTime = _unlockTime;
owner = payable(msg.sender);
}
function withdraw() public {
// Uncomment this line, and the import of "hardhat/console.sol", to print a log in your terminal
// console.log("Unlock time is %o and block timestamp is %o", unlockTime, block.timestamp);
require(block.timestamp >= unlockTime, "You can't withdraw yet");
require(msg.sender == owner, "You aren't the owner");
emit Withdrawal(address(this).balance, block.timestamp);
owner.transfer(address(this).balance);
}
}
```

16、`cd test/`

17、编写测试文件：`gedit Lock.js`

```plain
const {
time,
loadFixture,
} = require("@nomicfoundation/hardhat-toolbox/network-helpers");
const { anyValue } = require("@nomicfoundation/hardhat-chai-matchers/withArgs");
const { expect } = require("chai");
describe("Lock", function () {
// We define a fixture to reuse the same setup in every test.
// We use loadFixture to run this setup once, snapshot that state,
// and reset Hardhat Network to that snapshot in every test.
async function deployOneYearLockFixture() {
const ONE_YEAR_IN_SECS = 365 * 24 * 60 * 60;
const ONE_GWEI = 1_000_000_000;
const lockedAmount = ONE_GWEI;
const unlockTime = (await time.latest()) + ONE_YEAR_IN_SECS;
// Contracts are deployed using the first signer/account by default
const [owner, otherAccount] = await ethers.getSigners();
const Lock = await ethers.getContractFactory("Lock");
const lock = await Lock.deploy(unlockTime, { value: lockedAmount });
return { lock, unlockTime, lockedAmount, owner, otherAccount };
}
describe("Deployment", function () {
it("Should set the right unlockTime", async function () {
const { lock, unlockTime } = await loadFixture(deployOneYearLockFixture);
expect(await lock.unlockTime()).to.equal(unlockTime);
});
it("Should set the right owner", async function () {
const { lock, owner } = await loadFixture(deployOneYearLockFixture);
expect(await lock.owner()).to.equal(owner.address);
});
it("Should receive and store the funds to lock", async function () {
const { lock, lockedAmount } = await loadFixture(
deployOneYearLockFixture
);
expect(await ethers.provider.getBalance(lock.target)).to.equal(
lockedAmount
);
});
it("Should fail if the unlockTime is not in the future", async function () {
// We don't use the fixture here because we want a different deployment
const latestTime = await time.latest();
const Lock = await ethers.getContractFactory("Lock");
await expect(Lock.deploy(latestTime, { value: 1 })).to.be.revertedWith(
"Unlock time should be in the future"
);
});
});
describe("Withdrawals", function () {
describe("Validations", function () {
it("Should revert with the right error if called too soon", async function () {
const { lock } = await loadFixture(deployOneYearLockFixture);
await expect(lock.withdraw()).to.be.revertedWith(
"You can't withdraw yet"
);
});
it("Should revert with the right error if called from another account", async function () {
const { lock, unlockTime, otherAccount } = await loadFixture(
deployOneYearLockFixture
);
// We can increase the time in Hardhat Network
await time.increaseTo(unlockTime);
// We use lock.connect() to send a transaction from another account
await expect(lock.connect(otherAccount).withdraw()).to.be.revertedWith(
"You aren't the owner"
);
});
it("Shouldn't fail if the unlockTime has arrived and the owner calls it", async function () {
const { lock, unlockTime } = await loadFixture(
deployOneYearLockFixture
);
// Transactions are sent using the first signer by default
await time.increaseTo(unlockTime);
await expect(lock.withdraw()).not.to.be.reverted;
});
});
describe("Events", function () {
it("Should emit an event on withdrawals", async function () {
const { lock, unlockTime, lockedAmount } = await loadFixture(
deployOneYearLockFixture
);
await time.increaseTo(unlockTime);
await expect(lock.withdraw())
.to.emit(lock, "Withdrawal")
.withArgs(lockedAmount, anyValue); // We accept any value as `when` arg
});
});
describe("Transfers", function () {
it("Should transfer the funds to the owner", async function () {
const { lock, unlockTime, lockedAmount, owner } = await loadFixture(
deployOneYearLockFixture
);
await time.increaseTo(unlockTime);
await expect(lock.withdraw()).to.changeEtherBalances(
[owner, lock],
[lockedAmount, -lockedAmount]
);
});
});
});
});
```

18、`mkdir scripts/`   `cd scripts/`

19、编译部署区块链网络脚本文件：`gedit deploy.js`

```plain
const hre = require("hardhat");
async function main() {
const currentTimestamp = Math.floor(Date.now() / 1000);
const unlockTime = currentTimestamp + 60; // 60秒后解锁
const lock = await hre.ethers.deployContract("Lock", [unlockTime], {
value: hre.ethers.parseEther("0.001"),
});
await lock.waitForDeployment();
console.log(`Lock deployed to: ${lock.target}`);
console.log(`Unlock time: ${unlockTime}`);
}
main().catch((error) => {
console.error(error);
process.exitCode = 1;
});
```

20、运行编译：`npx hardhat compile`

21、开始测试：`npx hardhat test`

22、执行部署脚本：`npx hardhat run scripts/deploy.js --network localhost`

<font style="color:rgb(255,0,0);">！！！（出现该错误则代表没有开启节点）！！！</font>

23、开启节点：`npx hardhat node`（新开终端，进入项目目录运行，保持这个终端窗口运行，不要关闭，会看到20 个测试账户和私有密钥）

24、在本地测试网络上部署智能合约：`npx hardhat run scripts/deploy.js --network localhost`

<font style="background-color:#FBDE28;">Lock deployed to: 合约地址（你的实际地址会不同）</font>

<font style="background-color:#FBDE28;">Unlock time: 构造函数参数的实际值（根据你的合约而定）</font>

25、进入hardhat交互台：`npx hardhat console --network localhost`

获取当前时间并设置解锁时间：

`const currentBlockTime = (await ethers.provider.getBlock("latest")).timestamp;`

设置合约的解锁时间：

`const unlockTime = currentBlockTime + 600;`

加载Lock.sol合约的编译信息，生成合约工厂对象：

`const Lock = await ethers.getContractFactory("Lock");`

部署合约到区块链：

`const lock = await Lock.deploy(unlockTime, { value: ethers.parseEther("1") });`

查询合约地址：

`console.log("合约地址:", await lock.getAddress());`

等待合约部署交易被区块链确认：

`await lock.waitForDeployment();`

加速时间（模拟时间流逝后调用）：

`await ethers.provider.send("evm_increaseTime", [600]);`    // 快进600秒

强制挖一个新区块：

`await ethers.provider.send("evm_mine");`

查询owner对象：

`await lock.owner();`

查询合约地址的ETH 余额（显示原始余额为1000000000000000000 ETH）：

`console.log("Balance after withdrawal:", `

`await ethers.provider.getBalance(lock.getAddress()));`

调用合约的withdraw方法（将合约余额转账给 owner）：

`await lock.withdraw();`

再次查询合约地址的ETH 余额（失败时显示原始余额为1 ETH）：

`console.log("Balance after withdrawal:", `

`await ethers.provider.getBalance(lock.getAddress()));`    // 应该显示 0





