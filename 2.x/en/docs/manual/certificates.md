# Certificate description

FISCO BCOS network adopts a CA-oriented access mechanism to support any multi-level certificate structure for ensuring information confidentiality, authentication, integrity, and non-repudiation.

FISCO BCOS uses the [x509 protocol certificate format](https://en.wikipedia.org/wiki/X.509). According to the existing business scenario, a three level certificate structure is adopted by default, and from top to bottom, the three levels are chain certificate, agency certificate, and node certificate respective.

In multi-group architecture, a chain has a chain certificate and a corresponding chain private key, and the chain private key is jointly managed by alliance chain committee. Alliance chain committee can use the agency's certificate request file `agency.csr` to issue the agency certificate `agency.crt`.

Agency private key held by the agency administrator can issue node certificate to the agency's subordinate nodes.

Node certificate is the credential of node identity and uses this certificate to establish an SSL connection with other nodes for encrypted communication.

sdk certificate is a voucher for sdk communicating with node. Agency generates sdk certificate that allows sdk to do that.

The files’ suffixes of FISCO BCOS node running are described as follows:

| Suffix | Description |
| :-: | :-: |
| .key | private file|
| .crt | certificate file
| .csr  | certificate request file |

## Role definition

There are four roles in the FISCO BCOS certificate structure, namely the alliance chain committee administrator, agency, node, and SDK.

### Alliance chain committee

* The alliance chain committee manages private key of chain, and issues agency certificate according to agency's certificate request document `agency.csr`.

```bash
ca.crt chain certificate
ca.key chain private key
```

When FISCO BCOS performs SSL encrypted communication, only the node with the same chain certificate `ca.crt` can establish a connection.

### Agency

*	Agency has an agency private key that can issue node certificate and SDK certificate.

```bash
ca.crt chain certificate
agency.crt agency certificate
agency.csr agency certificate request file
agency.key agency private key
```

### Node/SDK

* FISCO BCOS nodes include node certificates and private keys for establishing SSL encrypted connection among nodes;
* SDK includes SDK certificate and private key for establishing SSL encrypted connection with blockchain nodes.

```bash
ca.crt #chain certificate
node.crt #node certificate
node.key #node private key
sdk.crt #SDK certificate
sdk.key #SDK private key
```

Node certificate `node.crt` includes the node certificate and the agency certificate information. When the node communicates with other nodes/SDKs, it will sign the message with its own private key `node.key`, and send its own `node.crt` to nodes/SDKs to verify.

## Certificate generation process

FISCO BCOS certificate generation process is as follows. Users can also use the [Enterprise Deployment Tool](../enterprise_tools/operation.md) to generate corresponding certificate

### Chain certificate generation

* Alliance chain committee uses openssl command to request chain private key `ca.key`, and generates chain certificate `ca.crt` according to ca.key.

### Agency certificate generation

* Agency uses openssl command to generate agency private key `agency.key`
* Agency uses private key `agency.key` to get agency certificate request file `agency.csr`, and sends `agency.csr` to alliance chain committee.
* Alliance chain committee uses chain private key `ca.key` to generate the agency certificate `agency.crt` according to the agency certificate request file `agency.csr`. And send agency certificate `agency.crt` to corresponding agency.

### Node/SDK certificate generation

* The node generates the private key `node.key` and the certificate request file `node.csr`. The agency administrator uses the private key `agency.key` and the certificate request file `node.csr` to issue the certificate to the node/SDK.

## TODO

