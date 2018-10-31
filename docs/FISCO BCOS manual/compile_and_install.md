## Deploy FISCO BCOS

### Get the code

Clone the code to the directory, eg */mydata*:

```shell
#create mydata dir
sudo mkdir -p /mydata
sudo chmod 777 /mydata
cd /mydata

#git clone
git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git
```

### Install and compile
Change to FISCO-BCOS root dir

In *FISCO-BCOS* root directory:

```shell
cd FISCO-BCOS
sh build.sh
```
Check if install successful:
```shell
fisco-bcos --version
#success: FISCO-BCOS version x.x.x
```
