#  libinitializer 模块
作者：TrustChain [微信公众号]

libinitializer：初始化工作。

## 主要内容有：

+ 区块链初始化工作

![](../../../../images/articles/sourceCode_knowledge_map/libinitializer.png)

## 涉及知识点：

+ BoostLogInitializer.cpp：在initLog函数和initStatLog函数里都通过调用initLogSink函数初始化一个sink进行日志打印；


+ GlobalConfigureInitializer.cpp：
```
//EVM参数配置
g_BCOSConfig.setEVMSchedule(dev::eth::FiscoBcosScheduleV3);
//data_secure 落盘加密
 g_BCOSConfig.diskEncryption.enable = _pt.get<bool>(sectionName + ".enable", false);
/// compress related option, default enable
    bool enableCompress = _pt.get<bool>("p2p.enable_compress", true);
//国密算法
bool useSMCrypto = _pt.get<bool>("chain.sm_crypto", false);
g_BCOSConfig.setUseSMCrypto(useSMCrypto);
```
+ Initializer.cpp
```
void Initializer::init(std::string const& _path)
{
    try
    {
        boost::property_tree::ptree pt;
        boost::property_tree::read_ini(_path, pt);

        /// init log
        m_logInitializer = std::make_shared<LogInitializer>();
        m_logInitializer->initLog(pt);
        /// init global config. must init before DB, for compatibility
        initGlobalConfig(pt);

        // init the statLog
        if (g_BCOSConfig.enableStat())
        {
            m_logInitializer->initStatLog(pt);
        }

        // init certificates
        m_secureInitializer = std::make_shared<SecureInitializer>();
        m_secureInitializer->initConfig(pt);

        m_p2pInitializer = std::make_shared<P2PInitializer>();
        m_p2pInitializer->setSSLContext(
            m_secureInitializer->SSLContext(SecureInitializer::Usage::ForP2P));
        m_p2pInitializer->setKeyPair(m_secureInitializer->keyPair());
        m_p2pInitializer->initConfig(pt);

        m_rpcInitializer = std::make_shared<RPCInitializer>();
        m_rpcInitializer->setP2PService(m_p2pInitializer->p2pService());
        m_rpcInitializer->setSSLContext(
            m_secureInitializer->SSLContext(SecureInitializer::Usage::ForRPC));
        /// must start RPC server here for Ledger initializer depends on AMDB, AMDB depends on RPC
        m_rpcInitializer->initChannelRPCServer(pt);

        m_ledgerInitializer = std::make_shared<LedgerInitializer>();
        m_ledgerInitializer->setP2PService(m_p2pInitializer->p2pService());
        m_ledgerInitializer->setKeyPair(m_secureInitializer->keyPair());
        m_ledgerInitializer->setChannelRPCServer(m_rpcInitializer->channelRPCServer());
        m_ledgerInitializer->initConfig(pt);

        m_rpcInitializer->setLedgerInitializer(m_ledgerInitializer);
        m_rpcInitializer->initConfig(pt);
        m_ledgerInitializer->startAll();
    }
    catch (std::exception& e)
    {
        INITIALIZER_LOG(ERROR) << LOG_BADGE("Initializer") << LOG_DESC("Init failed")
                               << LOG_KV("EINFO", boost::diagnostic_information(e));
        ERROR_OUTPUT << LOG_BADGE("Initializer") << LOG_DESC("Init failed")
                     << LOG_KV("EINFO", boost::diagnostic_information(e)) << std::endl;
        BOOST_THROW_EXCEPTION(e);
    }
}
```

+ LedgerInitializer.cpp
```

bool LedgerInitializer::initLedger(
    dev::GROUP_ID const& _groupId, std::string const& _dataDir, std::string const& _configFileName)
{
    if (m_ledgerManager->isLedgerExist(_groupId))
    {
        // Already initialized
        return true;
    }
    INITIALIZER_LOG(INFO) << "[initSingleLedger] [GroupId], LedgerConstructor:  "
                          << std::to_string(_groupId) << LOG_KV("configFileName", _configFileName)
                          << LOG_KV("dataDir", _dataDir);
    auto ledger = std::make_shared<Ledger>(m_p2pService, _groupId, m_keyPair);
    ledger->setChannelRPCServer(m_channelRPCServer);
    auto ledgerParams = std::make_shared<LedgerParam>();
    ledgerParams->init(_configFileName, _dataDir);
    //调用Ledger.cpp 的函数Ledger::initLedger
    bool succ = ledger->initLedger(ledgerParams);
    if (!succ)
    {
        return false;
    }

    m_ledgerManager->insertLedger(_groupId, ledger);
    return true;
}
```
+ P2PInitializer.cpp
```

/**
[p2p]
listen_ip=0.0.0.0
listen_port=30300
*/
//负责节点间的P2P通信
void P2PInitializer::initConfig(boost::property_tree::ptree const& _pt)
{
std::string listenIP = _pt.get<std::string>("p2p.listen_ip", "0.0.0.0");
int listenPort = _pt.get<int>("p2p.listen_port", 30300);

//存放p2p节点
std::map<NodeIPEndpoint, NodeID> nodes;
nodes.insert(                  std::make_pair(NodeIPEndpoint{ip_address, port}, NodeID()));
nodes.insert(                          std::make_pair(NodeIPEndpoint{ipv4Endpoint[0], port}, NodeID()));
//节点黑名单crl
std::vector<std::string> certBlacklist;
certBlacklist.push_back(nodeID);
//白名单
auto whitelist = parseWhitelistFromPropertyTree(_pt);

//p2pService >> host >> asioInterface

//设置asioInterface
auto asioInterface = std::make_shared<dev::network::ASIOInterface>();
asioInterface->setIOService(std::make_shared<ba::io_service>());
asioInterface->setSSLContext(m_SSLContext);
asioInterface->setType(dev::network::ASIOInterface::SSL);

//设置host
auto host = std::make_shared<dev::network::Host>();
host->setASIOInterface(asioInterface);
host->setSessionFactory(std::make_shared<dev::network::SessionFactory>());
host->setMessageFactory(messageFactory);
host->setHostPort(listenIP, listenPort);
host->setThreadPool(std::make_shared<ThreadPool>("P2P", 4));
host->setCRL(certBlacklist);

//设置m_p2pService
m_p2pService = std::make_shared<Service>();
m_p2pService->setHost(host);
m_p2pService->setStaticNodes(nodes);
m_p2pService->setKeyPair(m_keyPair);
m_p2pService->setP2PMessageFactory(messageFactory);
m_p2pService->setWhitelist(whitelist);
// start the p2pService
m_p2pService->start();
// CAL means certificate accepted list, CAL optional in config.ini
std::vector<std::string> certWhitelist;
certWhitelist.push_back(nodeID);
}
```

+ RPCInitializer.cpp: 
```
//channel_listen_port端口使用了TCP的长连接，用心跳包检测和保持存活，通信效率较高，支持AMOP协议的点对点通信，功能相当灵活强大。
/**
[rpc]
listen_ip=127.0.0.1   内网监听
channel_listen_port=20200
jsonrpc_listen_port=8545
*/

//RPC 、channelMessage、TCP
ChannelRPCServer::Ptr m_channelRPCServer;
std::shared_ptr<dev::rpc::Rpc> m_rpcForChannel;
ModularServer<>* m_channelRPCHttpServer;

//控制台app+SDK<=====>节点的通信
void RPCInitializer::initChannelRPCServer(boost::property_tree::ptree const& _pt){
 // listen ip for channel service, load from rpc.listen_ip if rpc.channel_listen_ip is not setted
int listenPort = _pt.get<int>("rpc.channel_listen_port", 20200);
int httpListenPort = _pt.get<int>("rpc.jsonrpc_listen_port", 8545);
//涉及节点p2p通信的参数配置
 m_channelRPCServer.reset(new ChannelRPCServer(), [](ChannelRPCServer* p) { (void)p; });
m_channelRPCServer->setListenAddr(listenIP);
m_channelRPCServer->setListenPort(listenPort);
m_channelRPCServer->setSSLContext(m_sslContext);
m_channelRPCServer->setService(m_p2pService);
m_channelRPCServer->setNetworkStatHandler(m_networkStatHandler);
m_channelRPCServer->setNetworkBandwidthLimiter(networkBandwidth);

//涉及节点p2p通信，实际是用到这个server,格式是channelMessage
auto server = std::make_shared<dev::channel::ChannelServer>();
server->setIOService(ioService);
server->setSSLContext(m_sslContext);
server->setEnableSSL(true);
server->setBind(listenIP, listenPort);
server->setCheckCertIssuer(checkCertIssuer);
server->setMessageFactory(std::make_shared<dev::channel::ChannelMessageFactory>());
m_channelRPCServer->setChannelServer(server);
}
```

```

//RPC是客户端与区块链系统交互的一套协议和接口，用户通过RPC接口可查询区块链相关信息（如块高、区块、节点连接等）和发送交易。RPC端口接受JSON-RPC格式的请求，格式比较直观清晰，采用CURL、JavaScript、Python、Go等语言都可以组装JSON格式的请求，发送到节点来处理。当然发送交易时，要在客户端实现交易签名。要注意的是，RPC连接没有做证书验证，且网络传输默认是明文的，安全性相对不高，建议只监听内网端口，用于监控、运营管理，状态查询等内部的工作流程上。目前监控脚本，区块链浏览器连接的是RPC端口。
//RPC、jsonrpc、http
std::shared_ptr<dev::SafeHttpServer> m_safeHttpServer;
ModularServer<>* m_jsonrpcHttpServer;

//区块链浏览器+监控脚本<=====>节点的通信
void RPCInitializer::initConfig(boost::property_tree::ptree const& _pt){
// listen ip for jsonrpc, load from rpc.listen ip if rpc.jsonrpc_listen_ip is not setted
auto rpcEntity = new rpc::Rpc(m_ledgerInitializer, m_p2pService);
auto ipAddress = boost::asio::ip::make_address(listenIP);
m_safeHttpServer.reset(new SafeHttpServer(listenIP, httpListenPort, ipAddress.is_v6()),[](SafeHttpServer* p) { (void)p; });
m_jsonrpcHttpServer = new ModularServer<rpc::Rpc>(rpcEntity);
m_jsonrpcHttpServer->addConnector(m_safeHttpServer.get());
}
```
+ SecureInitializer.cpp：initConfig函数根据配置选择国密或者非国密初始化;
initConfigWithSMCrypto(pt)、initConfigWithCrypto(pt);
```

//举例：涉及国密的ssl通信，有默认的，有针对p2p端口通信的、有针对RPC端口通信的
m_sslContexts[Usage::Default] = gmConfig.sslContext;
m_sslContexts[Usage::ForP2P] = gmConfig.sslContext;
m_sslContexts[Usage::ForRPC] = gmConfig.sslContext;
```

参考文献：

[1] https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v2.7.2

[2] https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/