# MPT State

标签：``存储模块`` ``MPT`` ``状态数据`` 

----
MPT State是以太坊上经典的数据存储方式。通过MPT树的方式，将所有合约的数据组织起来，实现了对数据的查找和追溯。

```eval_rst
.. important::
   推荐使用 **storage state**
```

## MPT树

MPT(Merkle Patricia Trie)，是一种用hash索引数据的前缀树。

从宏观上来说，MPT树是一棵前缀树，用key查询value。通过key去查询value，就是用key去在MPT树上进行索引，在经过多个中间节点后，最终到达存储数据的叶子节点。

从细节上来说，MPT树，是一棵Merkle树，每个树上节点的索引，都是这个节点的hash值。在用key查找value的时候，是根据key在某节点内部，获取下一个需要跳转的节点的hash值，拿到下一个节点的hash值，才能从底层的数据库中取出下一个节点的数据，之后，再用key，去下一个节点中查询下下个节点的hash值，直至到达value所在的叶子节点。

当MPT树上某个叶子节点的数据更新后，此叶子节点的hash也会更新，随之而来的，是这个叶子节点回溯到根节点的所有中间节点的hash都会更新。最终，MPT根节点的hash也会更新。当要索引这个新的数据时，用MPT新的根节点hash，从底层数据库查出新的根节点，再往后一层层遍历，最终找到新的数据。而如果要查询历史数据，则可用老的树根hash，从底层数据库取出老的根节点，再往下遍历，就可查询到历史的数据。

MPT树的实现图（图片来自以太坊黄皮书）

![](../../../images/storage/mpt.png)

## 状态 State

在以太坊上，数据是以account为单位存储的，每个account内，保存着这个合约(用户)的代码、参数、nonce等数据。account的数据，通过account的地址（address）进行索引。以太坊上用MPT将这些address作为查询的key，实现了对account的查询。

随着account数据的改变，account的hash也进行改变。于此同时，MPT的根的hash也会改变。不同的时候，account的数据不同，对应的MPT的根就不同。此处，以太坊把这层含义进行了具体化，提出了“状态”的概念。把MPT根的hash，叫state root。不同的state root，对应着不同的“状态”，对应查询到不同的MPT根节点，再用account的address从不同的MPT根节点查询到此状态下的account数据。不同的state，拿到的MPT根节点不同，查询的account也许会有不同。

state root是区块中的一个字段，每个区块对应着不同的“状态”。区块中的交易会对account进行操作，进而改变account中的数据。不同的区块下，account的数据有所不同，即此区块的状态有所不同，具体的，是state root不同。从某个区块中取出这个区块的state root，查询到MPT的根节点，就能索引到这个区块当时account的数据历史。

(图片来自以太坊白皮书)

![](../../../images/storage/mpt_state.png)

## Trade Off

MPT State的引入，是为了实现对数据的追溯。根据不同区块下的state root，就能查询到当时区块下account的历史信息。而MPT State的引入，带来了大量hash的计算，同时也打散了底层数据的存储的连续性。在性能方面，MPT State存在着天然的劣势。可以说，MPT State是极致的追求可追溯性，而大大的忽略了性能。

在FISCO BCOS的业务场景中，性能与可追溯性相比，性能更为重要。FISCO BCOS对底层的存储进行了重新的设计，实现了[Storage State](storage.md)。Storage State牺牲了部分的可追溯性，但带来了性能上的提升。



