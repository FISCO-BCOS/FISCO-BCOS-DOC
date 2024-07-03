##############################################################
6. Privacy protection scheme
##############################################################

Tags: "Privacy Protection" "'Homomorphic Encryption" "' Group / Ring Signature" "'WeDPR"'

----

Privacy protection is a major technical challenge for the alliance chain。In order to protect on-chain data, protect the privacy of alliance members, and ensure the effectiveness of supervision, FISCO BCOS [pre-compiled contract](../../contract_develop/c++_contract/index.md)
The form of the integration of homomorphic encryption, group / ring signature verification functions, providing a variety of privacy protection means。In addition, through ZPK(zero-knowledge proof)And a series of privacy protection projects such as WeDPR-Lab to ensure on-chain and off-chain data privacy and information security。

**Group / Ring Signature:**
FISCOBCOS supports a group / ring signature, including a group signature(Group Signature)with ring signature(Ring Signature)two parts。

A group signature is a relatively anonymous digital signature scheme that protects the identity of the signer, where the user can sign the message in place of their group, and the verifier can verify that the signature is valid, but does not know which group member the signature belongs to。At the same time, users cannot abuse this anonymity because the group administrator can open the signature through the group master's private key, exposing the signature's attribution information。

Ring signature is a special group signature scheme, but it has complete anonymity, that is, there is no administrator role, all members can actively join the ring, and the signature cannot be opened。

**Homomorphic encryption:** 
Homomorphic encryption is essentially a public key encryption algorithm that uses the public key pk for encryption and the private key sk for decryption；And support ciphertext calculation。FISCO BCOS uses the paillier encryption algorithm and supports additive homomorphism。Paillier's public and private keys are compatible with mainstream RSA encryption algorithms and have low access barriers。At the same time, paillier, as a lightweight homomorphic encryption algorithm, has a small computational overhead and is easily accepted by the business system。

**ZPK：**
zero-knowledge proof


**WeDPR-Lab：**
WeDPR-Lab is a set of ready-to-use scenario-based and efficient solutions and services for privacy protection。This solution is dedicated to solving the privacy protection risks and pain points in business digitization, such as privacy is not "hidden," sharing and collaboration is not controllable, eliminating privacy concerns of privacy subjects and compliance barriers for business innovation, helping to interconnect core values and explore emerging businesses based on privacy data, creating a fair, peer-to-peer and win-win multi-party data collaboration environment, and achieving a controllable balance between data value integration across subjects and data governance。



.. toctree::
   :maxdepth: 1
 
   privacy.md
   zpk.md
   wedpr.md
