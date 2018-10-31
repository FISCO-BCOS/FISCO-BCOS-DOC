
## Configure Certificates

### Configure root certificate

```Shell
cd /mydata/FISCO-BCOS/tools/scripts/

#bash generate_chain_cert.sh -o root certificate directory
bash generate_chain_cert.sh -o /mydata
```

### Configure agency certificate

```shell
cd /mydata/FISCO-BCOS/tools/scripts/

#bash generate_agency_cert.sh -c root certificate directory -o agency certificate directory -n agency name
bash generate_agency_cert.sh -c /mydata -o /mydata -n test_agency
```
