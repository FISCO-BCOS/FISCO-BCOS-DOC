# CRUD合约开发

访问 AMDB 需要使用 AMDB 专用的智能合约 DB.sol 接口，该接口是数据库合约，可以创建表，并对表进行增删改查操作。

DB.sol文件代码如下:
```js

contract DBFactory {
    function openTable(string) public constant returns (DB); //打开表
    function createTable(string,string,string) public constant returns(DB); //创建表
}

//查询条件
contract Condition {
    function EQ(string, int);
    function EQ(string, string);
    
    function NE(string, int);
    function NE(string, string);

    function GT(string, int);
    function GE(string, int);
    
    function LT(string, int);
    function LE(string, int);
    
    function limit(int);
    function limit(int, int);
}

//单条数据记录
contract Entry {
    function getInt(string) public constant returns(int);
    function getAddress(string) public constant returns(address);
    function getBytes64(string) public constant returns(byte[64]);
    function getBytes32(string) public constant returns(bytes32);
    
    function set(string, int) public;
    function set(string, string) public;
}

//数据记录集
contract Entries {
    function get(int) public constant returns(Entry);
    function size() public constant returns(int);
}

//DB主类
contract DB {
    //查询接口
    function select(string, Condition) public constant returns(Entries);
    //插入接口
    function insert(string, Entry) public returns(int);
    //更新接口
    function update(string, Entry, Condition) public returns(int);
    //删除接口
    function remove(string, Condition) public returns(int);
    
    function newEntry() public constant returns(Entry);
    function newCondition() public constant returns(Condition);
}
```
提供一个合约案例 DBTest.sol，代码如下：
``` js
import "DB.sol";

contract DBTest {
    event selectResult(bytes32 name, int item_id, bytes32 item_name);
    event insertResult(int count);
    event updateResult(int count);
    event removeResult(int count);
    
    //创建表
    function create() public {
        DBFactory df = DBFactory(0x1001); //DBFactory的地址固定为0x1001
        df.createTable("t_test", "name", "item_id,item_name");
    }

    //查询数据
    function select(string name) public constant returns(bytes32[], int[], bytes32[]){
        DBFactory df = DBFactory(0x1001);
        DB db = df.openTable("t_test");
        
        Condition condition = db.newCondition();
        //condition.EQ("name", name);
        
        Entries entries = db.select(name, condition);
        bytes32[] memory user_name_bytes_list = new bytes32[](uint256(entries.size()));
        int[] memory item_id_list = new int[](uint256(entries.size()));
        bytes32[] memory item_name_bytes_list = new bytes32[](uint256(entries.size()));
        
        for(int i=0; i<entries.size(); ++i) {
            Entry entry = entries.get(i);
            
            user_name_bytes_list[uint256(i)] = entry.getBytes32("name");
            item_id_list[uint256(i)] = entry.getInt("item_id");
            item_name_bytes_list[uint256(i)] = entry.getBytes32("item_name");
            selectResult(user_name_bytes_list[uint256(i)], item_id_list[uint256(i)], item_name_bytes_list[uint256(i)]);
        }
 
        return (user_name_bytes_list, item_id_list, item_name_bytes_list);
    }
    //插入数据
    function insert(string name, int item_id, string item_name) public returns(int) {
        DBFactory df = DBFactory(0x1001);
        DB db = df.openTable("t_test");
        
        Entry entry = db.newEntry();
        entry.set("name", name);
        entry.set("item_id", item_id);
        entry.set("item_name", item_name);
        
        int count = db.insert(name, entry);
        insertResult(count);
        
        return count;
    }
    //更新数据
    function update(string name, int item_id, string item_name) public returns(int) {
        DBFactory df = DBFactory(0x1001);
        DB db = df.openTable("t_test");
        
        Entry entry = db.newEntry();
        entry.set("item_name", item_name);
        
        Condition condition = db.newCondition();
        condition.EQ("name", name);
        condition.EQ("item_id", item_id);
        
        int count = db.update(name, entry, condition);
        updateResult(count);
        
        return count;
    }
    //删除数据
    function remove(string name, int item_id) public returns(int){
        DBFactory df = DBFactory(0x1001);
        DB db = df.openTable("t_test");
        
        Condition condition = db.newCondition();
        condition.EQ("name", name);
        condition.EQ("item_id", item_id);
        
        int count = db.remove(name, condition);
        removeResult(count);
        
        return count;
    }
}
```
DBTest.sol 调用了 AMDB 专用的智能合约 DB.sol，实现的是创建用户表 t_test，并对 t_test 表进行增删改查的功能。

> **注意：** 
客户端需要调用转换为 Java 文件的合约代码，需要将 DBTest.sol 和 DB.sol 放入 web3sdk 的 contract 目录下，通过 web3sdk 的编译脚本生成 DBTest.java。