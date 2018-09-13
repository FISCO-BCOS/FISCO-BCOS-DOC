package org.bcosliteclient;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import org.bcos.channel.client.TransactionSucCallback;
import org.bcos.web3j.abi.EventEncoder;
import org.bcos.web3j.abi.EventValues;
import org.bcos.web3j.abi.TypeReference;
import org.bcos.web3j.abi.datatypes.Event;
import org.bcos.web3j.abi.datatypes.Function;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.Utf8String;
import org.bcos.web3j.abi.datatypes.generated.Uint256;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.DefaultBlockParameter;
import org.bcos.web3j.protocol.core.methods.request.EthFilter;
import org.bcos.web3j.protocol.core.methods.response.Log;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.tx.Contract;
import org.bcos.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * Auto generated code.<br>
 * <strong>Do not modify!</strong><br>
 * Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>, or {@link org.bcos.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version none.
 */
public final class Counter extends Contract {
    private static String BINARY = "6060604052341561000c57fe5b5b604060405190810160405280600b81526020017f49276d20636f756e74657200000000000000000000000000000000000000000081525060009080519060200190610059929190610068565b5060006001819055505b61010d565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a957805160ff19168380011785556100d7565b828001600101855582156100d7579182015b828111156100d65782518255916020019190600101906100bb565b5b5090506100e491906100e8565b5090565b61010a91905b808211156101065760008160009055506001016100ee565b5090565b90565b6105128061011c6000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063146af82b1461005c5780632cefeb0714610082578063c6ea59b9146100dc578063d8c1b18014610175575bfe5b341561006457fe5b61006c6101d8565b6040518082815260200191505060405180910390f35b341561008a57fe5b6100da600480803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919050506101e3565b005b34156100e457fe5b6100ec6102a8565b604051808060200182810382528381815181526020019150805190602001908083836000831461013b575b80518252602083111561013b57602082019150602081019050602083039250610117565b505050905090810190601f1680156101675780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561017d57fe5b6101d6600480803590602001909190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050610351565b005b600060015490505b90565b80600090805190602001906101f992919061042d565b507f42b90959a8e5381997923014ade0202cf9a119d14c27a3304fa5faabba36128f81604051808060200182810382528381815181526020019150805190602001908083836000831461026b575b80518252602083111561026b57602082019150602081019050602083039250610247565b505050905090810190601f1680156102975780820380516001836020036101000a031916815260200191505b509250505060405180910390a15b50565b6102b06104ad565b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103465780601f1061031b57610100808354040283529160200191610346565b820191906000526020600020905b81548152906001019060200180831161032957829003601f168201915b505050505090505b90565b6000600154905082600154016001819055507ff27afe48774ff6727d9d80f14cc0557905314c3b842d6e333720f07567d5203183826001548560405180858152602001848152602001838152602001806020018281038252838181518152602001915080519060200190808383600083146103eb575b8051825260208311156103eb576020820191506020810190506020830392506103c7565b505050905090810190601f1680156104175780820380516001836020036101000a031916815260200191505b509550505050505060405180910390a15b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061046e57805160ff191683800117855561049c565b8280016001018555821561049c579182015b8281111561049b578251825591602001919060010190610480565b5b5090506104a991906104c1565b5090565b602060405190810160405280600081525090565b6104e391905b808211156104df5760008160009055506001016104c7565b5090565b905600a165627a7a723058203f4b13448a216851d8d6bff3fa1f6da5f892e14095eaf1c0b1e341c74d7da4a60029";

    public static final String ABI = "[{\"constant\":true,\"inputs\":[],\"name\":\"getcount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"string\"}],\"name\":\"setname\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getname\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"c\",\"type\":\"uint256\"},{\"name\":\"memo\",\"type\":\"string\"}],\"name\":\"addcount\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"c\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"oldvalue\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"currvalue\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"memo\",\"type\":\"string\"}],\"name\":\"counted\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"oldname\",\"type\":\"string\"}],\"name\":\"changename\",\"type\":\"event\"}]";

    private Counter(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private Counter(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private Counter(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private Counter(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    public static List<CountedEventResponse> getCountedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("counted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<CountedEventResponse> responses = new ArrayList<CountedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            CountedEventResponse typedResponse = new CountedEventResponse();
            typedResponse.c = (Uint256) eventValues.getNonIndexedValues().get(0);
            typedResponse.oldvalue = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.currvalue = (Uint256) eventValues.getNonIndexedValues().get(2);
            typedResponse.memo = (Utf8String) eventValues.getNonIndexedValues().get(3);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CountedEventResponse> countedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("counted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, CountedEventResponse>() {
            @Override
            public CountedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                CountedEventResponse typedResponse = new CountedEventResponse();
                typedResponse.c = (Uint256) eventValues.getNonIndexedValues().get(0);
                typedResponse.oldvalue = (Uint256) eventValues.getNonIndexedValues().get(1);
                typedResponse.currvalue = (Uint256) eventValues.getNonIndexedValues().get(2);
                typedResponse.memo = (Utf8String) eventValues.getNonIndexedValues().get(3);
                return typedResponse;
            }
        });
    }

    public static List<ChangenameEventResponse> getChangenameEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("changename", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ChangenameEventResponse> responses = new ArrayList<ChangenameEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ChangenameEventResponse typedResponse = new ChangenameEventResponse();
            typedResponse.oldname = (Utf8String) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ChangenameEventResponse> changenameEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("changename", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ChangenameEventResponse>() {
            @Override
            public ChangenameEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ChangenameEventResponse typedResponse = new ChangenameEventResponse();
                typedResponse.oldname = (Utf8String) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public Future<Uint256> getcount() {
        Function function = new Function("getcount", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> setname(Utf8String n) {
        Function function = new Function("setname", Arrays.<Type>asList(n), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public void setname(Utf8String n, TransactionSucCallback callback) {
        Function function = new Function("setname", Arrays.<Type>asList(n), Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    public Future<Utf8String> getname() {
        Function function = new Function("getname", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> addcount(Uint256 c, Utf8String memo) {
        Function function = new Function("addcount", Arrays.<Type>asList(c, memo), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public void addcount(Uint256 c, Utf8String memo, TransactionSucCallback callback) {
        Function function = new Function("addcount", Arrays.<Type>asList(c, memo), Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    public static Future<Counter> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue) {
        return deployAsync(Counter.class, web3j, credentials, gasPrice, gasLimit, BINARY, "", initialWeiValue);
    }

    public static Future<Counter> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue) {
        return deployAsync(Counter.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "", initialWeiValue);
    }

    public static Counter load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Counter(contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    public static Counter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Counter(contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    public static Counter loadByName(String contractName, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Counter(contractName, web3j, credentials, gasPrice, gasLimit, true);
    }

    public static Counter loadByName(String contractName, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Counter(contractName, web3j, transactionManager, gasPrice, gasLimit, true);
    }

    public static class CountedEventResponse {
        public Uint256 c;

        public Uint256 oldvalue;

        public Uint256 currvalue;

        public Utf8String memo;
    }

    public static class ChangenameEventResponse {
        public Utf8String oldname;
    }
}
