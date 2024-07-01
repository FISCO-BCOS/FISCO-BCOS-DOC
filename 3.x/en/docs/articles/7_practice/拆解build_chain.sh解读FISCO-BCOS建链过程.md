# Disassemble build _ chain.sh Interpretation of FISCO-BCOS chain building process
Author ： Chongqing Electronic Engineering Vocational College| to the key male

Here is the tutorial: [companion video](https://space.bilibili.com/335373077)

# lifting chain
We're not going to talk about the chain here in a complete way, pick the point if you're interested in or unfamiliar with the chain, you can go to another article of mine.
[[Tutorial] Perfect FISCO-How to start the BCOS blockchain network, stand-alone four-node, alliance chain](https://blog.csdn.net/qq_57309855/article/details/126180787?spm=1001.2014.3001.5501)

First I will download the build _ chain.sh script

Use the chain building command to build the chain (use a node directory here to facilitate teaching)

```

bash build_chain.sh -l  127.0.0.1 -p 30300,20200,8545
We can first look at the information returned in the window

```
![build _ chain sh feedback screenshot](https://user-images.githubusercontent.com/111106471/184881953-bcacb07b-f6b5-4ab2-9166-20aebe5bcc37.png)




## Paragraph 1

```

[INFO] Downloading fisco-bcos binary from https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.9.0/fisco-bcos.tar.gz ... 
curl: (7) Failed to connect to github.com port 443: Connection refused ` 
 
[INFO] Download speed is too low, try https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v2.9.0/fisco-bcos.tar.gz 

Here we go to GitHub to download FISCO-BCOS compressed package, found that the link failed, so jump to the domestic code cloud to download

```

This corresponds to line 1633 in our build _ chain.sh
![download _ bin code screenshot](https://user-images.githubusercontent.com/111106471/184882007-e90f1b3c-0bb9-407d-b2ad-194c1c0a21d5.png)
![download _ link code screenshot](https://user-images.githubusercontent.com/111106471/184882027-38fe8614-632d-4335-9e50-7bc86f187529.png)



 The cdn _ download _ link here is specified in the above environment variable



 There are many judgments below to prevent GitHub from being inaccessible in China.

## Second paragraph

```

==============================================================
Generating CA key...
==============================================================

```
 
Generate the CA key corresponding to the script 1677 lines, the running process in the script is to find ${output_dir}The CA certificate is stored in the cert directory under ${output_dir}The nodes directory is defined above, so we can see our CA certificate after we enter, and there will be a column to explain the generation of the specific CA certificate.。

![prepareCA code screenshot](https://user-images.githubusercontent.com/111106471/184881843-f81179d6-945f-4e2e-b64c-6b1e86a3cd50.png)
![output _ dir screenshot](https://user-images.githubusercontent.com/111106471/184881884-f3ff536b-0d61-46de-af3b-832f2982b129.png)
![screenshot of nodes directory](https://user-images.githubusercontent.com/111106471/184881899-d5980b81-7f54-487b-b280-5cb435df7c36.png)


 

## Third paragraph

```

============================================================== 
Generating keys and certificates ...
Processing IP=127.0.0.1 Total=1 Agency=agency Groups=1
==============================================================

```

The generated secret key and certificate correspond to line 1793 of the script. The running process in the script is to assign them to the variable $OPTARG after entering the chain command, determine the chain mode, the number of nodes, determine the parameters such as IP, group, etc., and start creating the node directory. The node directory is determined by node _ count.

​
![Key certificate code screenshot](https://user-images.githubusercontent.com/111106471/184882272-d8c63950-9576-42c8-bff5-5b11349f6b9c.png)
![Key certificate generation code screenshot 2](https://user-images.githubusercontent.com/111106471/184882282-2bb1ed99-5605-478c-9d64-015e6b1a2ec9.png)
![127 0 0 1 Folder Screenshot](https://user-images.githubusercontent.com/111106471/184882372-f5230d39-98aa-4b27-9b5b-b18a1212e134.png)


​

​

## Fourth paragraph

```

==============================================================
Generating configuration files ...
Processing IP=127.0.0.1 Total=1 Agency=agency Groups=1
==============================================================

```

The generated configuration file corresponds to line 1925 of the script. The running process in the script is to first determine the location of the output directory of the certificate, and then send the generated certificate to the directory after receiving it with node _ count and node _ dir. The generated certificate includes group certificates, group.X.genesis, group.x.ini, config.ini, and agency directories.



## Paragraph 5

```

==============================================================
 [INFO] Start Port      : 30300 20200 8545`
 [INFO] Server IP       : 127.0.0.1
 [INFO] Output Dir      : /home/fisco223/fisco/nodes
 [INFO] CA Path         : /home/fisco223/fisco/nodes/cert/
 [INFO] RSA channel     : true`
==============================================================

```

Here is the feedback of all ports and services and the final working directory to the user, for their own determination of whether to meet expectations and to prevent excessive work after the configuration file, etc. can not be found, corresponding to script 226 lines.
![Generate Profile Code Screenshot](https://user-images.githubusercontent.com/111106471/184882563-01bbca48-4460-408d-878e-4214e5563777.png)



Sixth paragraph

```

 ==============================================================
 [INFO] Execute the download_console.sh script in directory named by IP to get FISCO-BCOS console.
e.g.  bash /home/fisco223/fisco/nodes/127.0.0.1/download_console.sh -f 
 ==============================================================
 [INFO] All completed. Files in /home/fisco223/fisco/nodes
 
 ```
 
This is to remind users to use sh script to obtain FISCO in the directory named by IP.-BCOS Console。And gave an example of e.g. to explain the usage, and finally prompted the user that all the processes have been completed, set up the completion of the work directory in ${output_dir}Lower。
![Feedback screenshot](https://user-images.githubusercontent.com/111106471/184882668-3f673308-042c-419d-9bc0-2b1fc49c3500.png)


