在测试下面合约时会出现问题：
某个多参数合约方法：

// SPDX-License-Identifier: MIT
pragma solidity ^0.6.0;
contract Test {
  function methodWithLotsOfParameters(
    uint256 param1,
    uint256 param2,
    uint256 param3,
    uint256 param4,
    uint256 param5,
    uint256 param6,
    uint256 param7,
    uint256 param8,
    uint256 param9,
    uint256 param10,
    uint256 param11,
    uint256 param12,
    uint256 param13,
    uint256 param14,
    uint256 param15,
    uint256 param16,
    uint256 param17,
    uint256 param18,
    uint256 param19,
    uint256 param20
  ) public pure returns (uint256) {
    // Perform some operations with the parameters
    return param1 + param2 + param3 + param4 + param5 + param6 + param7 + param8 + param9 + param10 + param11 + param12 + param13 + param14 + param15 + param16 + param17 + param18 + param19 + param20;
  }
}

填写参数后：
![image](https://github.com/FISCO-BCOS/FISCO-BCOS/assets/148929545/7d29fe4d-db50-41aa-9bc0-2aff1f677e27)

返回的这个报错：

![image](https://github.com/FISCO-BCOS/FISCO-BCOS/assets/148929545/e9d04b8a-5de1-4d03-af56-6a5fa05fa8da)


测试OpenZeppelin和WTF中的相关合约，使用合约IDE进行编译和部署的过程中未发现任何问题。合约IDE的编译版本为0.8.11。

针对测试，写了一个相关复杂合约（主合约代码如下），在编译、部署和调用过程中，未出现任何错误或异常情况，合约的功能和逻辑按预期工作，没有引发潜在的问题或漏洞。
`
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.7;

import "Ownable.sol";
import "Counters.sol";
import "ECDSA.sol";
import "EIP712.sol";
import "SafeMath.sol";
import "Strings.sol";
import "ERC721.sol";

/*
    @title 商品合约
*/
contract Product is ERC721, EIP712, Ownable {

    /// @title 商品信息
    struct ProductInfo {
        uint256 price;         // 当前价格
        uint256 tokenId;       // 商品NFT对应的TokenId
        address owner;         // 商品所有者钱包地址
        uint16  transferCount; // 转让次数
        uint8   status;        // 商品状态：0 未设置转让（不可购买）  1 设置转让了（可购买）
    }

    /* =================== Events =================== */

    /*
        @title 事件 - 购买商品
        @param _sender 购买商品用户地址（商品持有者地址）
        @param _productId 商品编号
        @param _price 商品价格 
        @param _tokenId NFT token Id 
    */
    event BuyProduct(address _sender, uint16 _productId, uint256 _price, uint256 _tokenId);

    using Strings for uint256;
    using SafeMath for uint256;
    using Counters for Counters.Counter;

    /* =================== Constants =================== */

    //购买商品函数Hash
    bytes32 public constant BUYLAND_HASH = keccak256("BuyProduct(address _admin,address _buyer,uint16 _productId,uint256 _productPrice,uint256 _nonce,uint256 _deadline)");

    /* =================== State Variables =================== */

    mapping(uint16 => ProductInfo) public Product;    // 商品编号对应的商品信息
    mapping(address => uint16[]) public userProduct;  // 用户持有的所有商品
    string public baseURI;
    uint256 public currentTokenId; // 当前生成的最大tokenId
    mapping(address => Counters.Counter) private _nonces;   // 用户签名计数器   
    mapping(address => bool) private admins;                // 管理员（可信任组）

     /* =================== Modifier =================== */

    // 用于判断是否是管理员
    modifier onlyAdmin(){
        require(msg.sender == owner(),"only by owner");
        _;
     }

    // 用于判断TokenId是否存在
    modifier tokenIdExists(uint256 _tokenId) {
        require(_exists(_tokenId), "Token does not exist");
        _;
    }

    // 用于判断商品归属人
    modifier onlyTokenOwner(uint256 _tokenId) {
        require(
            msg.sender == ownerOf(_tokenId),
            "Only the owner can perform this action"
        );
        _;
    }
  
    /* =================== Constructor =================== */

     /*
        @param _name 商品NFT名称
        @param _symbol 商品NFT符号
    */
    constructor(string memory _name, string memory _symbol) ERC721(_name, _symbol) EIP712("Product", "1.0") {
    }


    /* =================== External Functions =================== */

    /*
        @title 购买商品
        @param _productId 商品编号
        @param _productPrice 商品价格
        @param _deadline 签名过期时间
        @param v, r, s 签名信息
    */
    function buyProduct(
        uint16  _productId,
        uint256 _productPrice,
        uint256 _deadline,
        uint8 v,
        bytes32 r,
        bytes32 s
    ) external {       
        // 1、验证签名是否正确
        _validateSignature(_productId, _productPrice, _deadline, v, r, s);


        // 2、验证商品是否已被购买
        require(Product[_productId].owner == address(0), "Product is already bought.");

        // 3、tokenId++
        currentTokenId++;

        // 4、mint商品（userAddr, tokenId）
        _safeMint(msg.sender, currentTokenId);

        // 5、设置状态变量商品信息 Product
        Product[_productId] = ProductInfo({
            price: _productPrice,
            tokenId: currentTokenId,
            owner: msg.sender,
            transferCount: 0,
            status: 0
        });

        // 6、更新状态变量用户持有的商品 userProduct
        userProduct[msg.sender].push(_productId);

        // 添加事件
        emit BuyProduct(msg.sender, _productId, _productPrice, currentTokenId);
    }


    /* =================== External Getter Functions =================== */ 

    /*
        @title 查看商品详情
        @param _productId 商品编号
    */
    function getProductInfo(uint16 _productId) external view returns (ProductInfo memory) {
        return Product[_productId];
    }

    /*
        @title 查看用户持有的所有商品
        @param _owner 用户地址
    */
    function getAllProductByOwner(address _owner) external  view returns (ProductInfo[] memory) {       
        ProductInfo[] memory productList = new ProductInfo[](userProduct[_owner].length);

        for (uint256 i = 0; i < userProduct[_owner].length; i++) {
            productList[i] = Product[userProduct[_owner][i]];
        }

        return productList;
    }
  
    /*
     * @dev 获取用户签名nonce
     */
    function getNonces(address owner) external  view returns (uint256) {
        return _nonces[owner].current();
    }

    /* =================== Internal Getter Functions =================== */ 


    /*
        @title 验证购买商品时管理员的签名是否正确
        @param _productId 商品编号
        @param _productPrice 商品价格
        @param _deadline 签名过期时间
        @param v, r, s 管理员签名信息
    */
    function _validateSignature(       
        uint16  _productId,
        uint256 _productPrice,
        uint256 _deadline,
        uint8 v,
        bytes32 r,
        bytes32 s
    ) internal returns (bool) {
        require(block.timestamp <= _deadline, "Buy Product: expired deadline");
        bytes32 structHash = keccak256(abi.encode(BUYLAND_HASH, 
                                owner(),
                                msg.sender,
                                _productId, 
                                _productPrice, 
                                 _useNonce(owner()),
                                _deadline
                            ));

        bytes32 hash = _hashTypedDataV4(structHash);

        address signer = ECDSA.recover(hash, v, r, s);

        require(signer == owner(), "Invalid signature");
        return true;
    }

    /*
     * @dev 返回当前用户的签名计数，然后自增1，签名验证使用
     */
    function _useNonce(address owner) internal returns (uint256 current) {
        Counters.Counter storage nonce = _nonces[owner];
        current = nonce.current();
        nonce.increment();
    }
}`