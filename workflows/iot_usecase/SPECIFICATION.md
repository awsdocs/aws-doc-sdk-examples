# Performing device management use cases using the AWS Iot SDK technical specification

## Overview
This example shows how to use AWS SDKs to perform device management use cases using the AWS Iot SDK.

The AWS Iot API provides secure, bi-directional communication between Internet-connected devices (such as sensors, actuators, embedded devices, or smart appliances) and the Amazon Web Services cloud. This example shows some typical use cases such as creating things, creating certifications, applying the certifications to the IoT Thing and so on. 

The IotClient service client is used in this example and the following service operations are covered:

1. Creates an AWS IoT Thing using the createThing().
2. Generate a device certificate using the createKeysAndCertificate().
3. Attach the certificate to the AWS IoT Thing using attachThingPrincipal().
4. Update an AWS IoT Thing with Attributes using updateThingShadow().
5. Get an AWS IoT Endpoint using describeEndpoint().
6. List your certificates using listCertificates().
7. Detach and delete the certificate from the AWS IoT thing.
8. Updates the shadow for the specified thing.
9. Write out the state information, in JSON format.
10. Creates a rule
11. List rules
12. Search things
13. Delete Thing.

 Note: We have buy off on these operations from IoT SME. 

Prerequisites
If you need to, install or update the latest version of the AWS CLI.

### Application Output

```
--------------------------------------------------------------------------------
Welcome to the AWS IoT example scenario.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
1. Create an AWS IoT Thing.
An AWS IoT Thing represents a virtual entity in the AWS IoT service that can be associated with a physical device.

Enter Thing name: ThingScott4
Thing ARN search result is arn:aws:iot:us-east-1:814548047983:thing/ThingScott4
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
2. Generate a device certificate.
A device certificate performs a role in securing the communication between devices (Things) and the AWS IoT platform.

Do you want to create a certificate? (y/n)y
Private Key:
-----BEGIN RSA PRIVATE KEY-----
MIIEogIBAAKCAQEAu+yx44boc0SoyVzV3ie++xb2l534J3nQgvNpoe4IzZsqrLnd
zO/ASaAiOmhBXuTzYKFcrS+m5PZv2+TpT7q7T5Fg6S/QCbB3oC3wd6vQqDaSuASO
RlSUKk2KyWe6419dHBzkTweF1GFcTFMNTCuDl9x+aKjoL5r0EhHPU4qhgV3y3N/o
ogWaaJDlsHESz2ftXEiEegAS6gXAyKRaPILrZCuRTGBfNUEq3nxd/o6V0+x8jyyG
BpGQ+n4Ah2MNqck3IqABiSQMHK/7Pmnm9cniVbRfB8ZKQOqGlPYC6z8NbRxensS3
WdfyPxBwl/hNbor19Z9vS5jVMI/TVir0scbKiwIDAQABAoIBAEcMxGzrsrLyAIax
LMLjcBdqpSzJsurp6WW5NfTdFEL4KXhIhY1YD/FGM+q1RBHmqgWcvHdWDtl+Ouh+
cZ+NUAhTRLqlMxiWuYO8GrCEK02N5IbiYhBBJgugaZqgN06D3aYlXkRq6bRUYxsQ
Q3TK9uPRn5FeWvq0sRar2SESxuErx9AvId5TB+B52y6jnTyQBJfcL6eORC4GPzZN
sq/JNoOBxvjod6A2VWM3PyfU9fHrQneL68R3z3e113P7ryd5rqGermiLepWAYGpq
btSUNPi3tTe60R1wt/z6giy/M/XDLtWxFAPvsQ1xFCac+0GeM7M8uSXG8VEJ9fuS
/Qbc+AECgYEA6mXRXXIr1hJIugPkyCa0GDkqFGMIbAB1kW6NUsS+bwb/gYg8m+I7
isbSm0Z9Kkoe7sZ4VfHBNjjk0bJ2pjvx5S4otMEW/ddzxrOPArDBs4dSZbyHZ+IA
8kTLeVuC+fo1tmQ2aLjtN4Ix1/yTILUMxVcCjgmJEgqZgruM6YIsEIsCgYEAzT5t
19qlUifakzFn8iKANAZzWWIZxSkj79OfiunknUtLkYacD3EL4/oGbrs/v/sb7S3T
tOxBfOBqLqB9CWXwYkQBxASOC9e6uol2OhhopQh7PvTUn7Up3o9pd2fsLFvpUQpZ
Tu2U3MeNGoA7T9NehRO6put7CS1TafvJqHpdbgECgYA9sTiukJsrB7rugpHXgCBM
c9c4cGxKCMDo2yEFFNVOYZgkHphdKuki+Ht79MyCsel8TO5lKHEC9Wx8KK2DySk8
ea6rDFshynAlbyMHwT/qIYa1Ui6NT/WRzjuDoGtToeA7Nhr/6xQ8VN8LiuWqaRyI
YANDIQ1bm0CWIQjQeLtiSwKBgHemk2apIoO4lCYHQW/ZSSALOqe7FwbnqvmfR302
SX6hbJaUANrHroTjN2jKZKnI/EjS1H6+1Ja2RXCVQw99jBvdRaI+fYjN5R/mDzRj
Em5TeXYESQgqFxSWSoe/3+EkW+pQvCvVOVlURCBU7T7mHE48aC3zTbDV6bD5hmBf
paQBAoGAVlxTMp8ltqBzaEHNodOWwkJ6xiLiQUg0Mm3Fclc0X+kIBH2p2vntpPJJ
LULHAgYfIcuQuVJOxwxXXyn8U2UKBtRfr10+rb6bdFxjcy+/K9QND5Xwktaq6Fzi
0NbCjP/+PVP30Wd0MmLwXaJDRhbzcrWBD9EvjHQK1J9FCOc7V+k=
-----END RSA PRIVATE KEY-----


Certificate:
-----BEGIN CERTIFICATE-----
MIIDWjCCAkKgAwIBAgIVAPkK0E8AU5pG7esMCGOagc9QQvRLMA0GCSqGSIb3DQEB
CwUAME0xSzBJBgNVBAsMQkFtYXpvbiBXZWIgU2VydmljZXMgTz1BbWF6b24uY29t
IEluYy4gTD1TZWF0dGxlIFNUPVdhc2hpbmd0b24gQz1VUzAeFw0yNDAyMDUxNTIz
MTRaFw00OTEyMzEyMzU5NTlaMB4xHDAaBgNVBAMME0FXUyBJb1QgQ2VydGlmaWNh
dGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC77LHjhuhzRKjJXNXe
J777FvaXnfgnedCC82mh7gjNmyqsud3M78BJoCI6aEFe5PNgoVytL6bk9m/b5OlP
urtPkWDpL9AJsHegLfB3q9CoNpK4BI5GVJQqTYrJZ7rjX10cHORPB4XUYVxMUw1M
K4OX3H5oqOgvmvQSEc9TiqGBXfLc3+iiBZpokOWwcRLPZ+1cSIR6ABLqBcDIpFo8
gutkK5FMYF81QSrefF3+jpXT7HyPLIYGkZD6fgCHYw2pyTcioAGJJAwcr/s+aeb1
yeJVtF8HxkpA6oaU9gLrPw1tHF6exLdZ1/I/EHCX+E1uivX1n29LmNUwj9NWKvSx
xsqLAgMBAAGjYDBeMB8GA1UdIwQYMBaAFDCS8hSJwupPnGkn4XUEPUfGyaNjMB0G
A1UdDgQWBBRaat/NhZrO1ktgHSYWHxVe4+mhlDAMBgNVHRMBAf8EAjAAMA4GA1Ud
DwEB/wQEAwIHgDANBgkqhkiG9w0BAQsFAAOCAQEAS7xqkAdJGNCPBaxYsE49yTUQ
lGK30Vv8SGS0dyyPvD4XK0xySWoSz35HNKF0RcYik4MFylZyAfzyXpQh8BM3sHDH
EVRJ9xgtYyKlZvOpMVZAaqVhEYZ1r2jsKMfw/iE7yNaR7fMucAgHrm9NzotUbXE4
ZkkIa4wrA/vd76r1wNBz1P+f9i1ZH9hehzE0Kgc8pTg3PE4b1IB3k4Osh0rwKQaE
qV/4IdDukQhDM+4CjoNoxlEVwQpDL91R4IWiIXaXu4kht74U7wp5ZdNnzEpNCv1q
JMZBb3m+nHKkMD3vZei8lW7RqEkVx0fMZjyrf2xaCzs5AUdNCvFL/kGJ4+ZMFQ==
-----END CERTIFICATE-----


Certificate ARN:
arn:aws:iot:us-east-1:814548047983:cert/89164b1acfe1b440c1874a49108b8f90f66ac97e212860ad9db9f3043d499088
Attach the certificate to the AWS IoT Thing.
Certificate attached to Thing successfully.
Thing Details:
Thing Name: ThingScott4
Thing ARN: arn:aws:iot:us-east-1:814548047983:thing/ThingScott4
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
3. Update an AWS IoT Thing with Attributes.
Attributes are key-value pairs that can be searchable.

Thing attributes updated successfully.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4 Return a unique endpoint specific to the Amazon Web Services account.
Extracted subdomain: a39q2exsoth3da
Full Endpoint URL: https://a39q2exsoth3da-ats.iot.us-east-1.amazonaws.com
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. List your IoT Certificates
Cert id: 89164b1acfe1b440c1874a49108b8f90f66ac97e212860ad9db9f3043d499088
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/89164b1acfe1b440c1874a49108b8f90f66ac97e212860ad9db9f3043d499088
Cert id: b9fa0e3e2129678fdb8611a5f6372fe3cdb844570e421825202e5c929b27dabb
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/b9fa0e3e2129678fdb8611a5f6372fe3cdb844570e421825202e5c929b27dabb
Cert id: dde2b3a53d6a0ee420d0051bae08596954398fd1f66af06e781b04f5569bc66b
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/dde2b3a53d6a0ee420d0051bae08596954398fd1f66af06e781b04f5569bc66b
Cert id: befa8da108e5dbf351185c0a25a3228383643dca8b87719d5449047a76439500
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/befa8da108e5dbf351185c0a25a3228383643dca8b87719d5449047a76439500
Cert id: b27f433dceaaaf7e4422fe8edd90bcc98ab19e3c06df389944fdcb3bfd62eaed
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/b27f433dceaaaf7e4422fe8edd90bcc98ab19e3c06df389944fdcb3bfd62eaed
Cert id: f6d79627626b7523aba008d817f5ec21295118a2960aba7233595798c6332824
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/f6d79627626b7523aba008d817f5ec21295118a2960aba7233595798c6332824
Cert id: f2ca111f9ca54bf5cadd5ecb2f57dcadb65b3d40e45fe636e8ec162ba9992463
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/f2ca111f9ca54bf5cadd5ecb2f57dcadb65b3d40e45fe636e8ec162ba9992463
Cert id: 1b23a809605a6b99f7e3e4b6116a6f8f10dce40f72d293740f11bc55bff3cb6a
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/1b23a809605a6b99f7e3e4b6116a6f8f10dce40f72d293740f11bc55bff3cb6a
Cert id: 0575fc1cb6b528992aaf0e427176b6ccfadd7557244e08a62f3bbd6c2e660417
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/0575fc1cb6b528992aaf0e427176b6ccfadd7557244e08a62f3bbd6c2e660417
Cert id: d167af8a9f7b1daa43c6425e3960ad684d76a46b7d3c7a5fcd12080514f58ec0
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/d167af8a9f7b1daa43c6425e3960ad684d76a46b7d3c7a5fcd12080514f58ec0
Cert id: b5a5b8f3a531edc7aaec4b51d6dddc87f56596dd0165f507ffe2b78625913b11
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/b5a5b8f3a531edc7aaec4b51d6dddc87f56596dd0165f507ffe2b78625913b11
Cert id: e5847e24aced27924dac657013d56cb0dcb1e0bbc4d6292971b134db997ec001
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/e5847e24aced27924dac657013d56cb0dcb1e0bbc4d6292971b134db997ec001
Cert id: 77d5a1e86fb741e064a8e72fc3738f7d9e6db923fd3f8a19c52cce10e712a337
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/77d5a1e86fb741e064a8e72fc3738f7d9e6db923fd3f8a19c52cce10e712a337
Cert id: f2c2924fd38fdea8c46c1d5478cd2f67a0647fd9a1d8cf0f5e711f7ddd2f6c93
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/f2c2924fd38fdea8c46c1d5478cd2f67a0647fd9a1d8cf0f5e711f7ddd2f6c93
Cert id: d5bf20f64bd3a91e6511dc1a51e8f57afdfa060804b5eba0d5b416271e7985f3
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/d5bf20f64bd3a91e6511dc1a51e8f57afdfa060804b5eba0d5b416271e7985f3
Cert id: fa17d5ac05178f344df9ea2d5e817060d66eddd83b0a8a7d6cfb623628a771dd
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/fa17d5ac05178f344df9ea2d5e817060d66eddd83b0a8a7d6cfb623628a771dd
Cert id: 5cca45082923ce60b98694abee3aa1887fe59a37f0039526e435c6ebec786eaf
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/5cca45082923ce60b98694abee3aa1887fe59a37f0039526e435c6ebec786eaf
Cert id: af8b7c4f6849d825a24c2f10d33024d40b5ea5993fc052293cb7805fbb70657a
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/af8b7c4f6849d825a24c2f10d33024d40b5ea5993fc052293cb7805fbb70657a
Cert id: 59f1c5c4949379201abd6c98368a4f171b23005866210047dcad382b644a52c8
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/59f1c5c4949379201abd6c98368a4f171b23005866210047dcad382b644a52c8
Cert id: d938103e1b02b2fc31aa2de85668a17067776a5259fb84c417796fc752da3e03
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/d938103e1b02b2fc31aa2de85668a17067776a5259fb84c417796fc752da3e03
Cert id: f35e6b83c2237b0dae43fb36ee89a6983710c46738c42d1ad2b7b96f16c6d5be
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/f35e6b83c2237b0dae43fb36ee89a6983710c46738c42d1ad2b7b96f16c6d5be
Cert id: 41864727b7f47679c5ae4054037cabc95b64697158a509f2d48115251e6674ab
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/41864727b7f47679c5ae4054037cabc95b64697158a509f2d48115251e6674ab
Cert id: d6f58e3bb9e983aea529364fb615750a8f76e593f3d90d08dcf93b93a88ae568
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/d6f58e3bb9e983aea529364fb615750a8f76e593f3d90d08dcf93b93a88ae568
Cert id: 7bd183013d2ef393e2c32468ce0bab8eb08d65d3e9ba6e78b4792f85f2eea557
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/7bd183013d2ef393e2c32468ce0bab8eb08d65d3e9ba6e78b4792f85f2eea557
Cert id: 236fc47d720b6fb7f2184358060f5da6ef2bb5e82038502bd03418d1a4207baf
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/236fc47d720b6fb7f2184358060f5da6ef2bb5e82038502bd03418d1a4207baf
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. Detach amd delete the certificate.
arn:aws:iot:us-east-1:814548047983:cert/89164b1acfe1b440c1874a49108b8f90f66ac97e212860ad9db9f3043d499088 was successfully removed from ThingScott4
arn:aws:iot:us-east-1:814548047983:cert/89164b1acfe1b440c1874a49108b8f90f66ac97e212860ad9db9f3043d499088 was successfully deleted.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Create an IoT shadow that refers to a digital representation or virtual twin of a physical IoT device
Thing Shadow updated successfully.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
8. Write out the state information, in JSON format.
Received Shadow Data: {"state":{"reported":{"temperature":25,"humidity":50}},"metadata":{"reported":{"temperature":{"timestamp":1707146715},"humidity":{"timestamp":1707146715}}},"version":1,"timestamp":1707146715}
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
9. Creates a rule
Creates a rule that is an administrator-level action.
Any user who has permission to create rules will be able to access data processed by the rule.

Enter Rule name: ScottRule88
IoT Rule created successfully.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
10. List your rules.
List of IoT Rules:
Rule Name: YourRuleName11
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/YourRuleName11
--------------
Rule Name: YourRuleName29
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/YourRuleName29
--------------
Rule Name: ScottRule88
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/ScottRule88
--------------
Rule Name: RuleScott
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/RuleScott
--------------
Rule Name: YourRuleName27
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/YourRuleName27
--------------
Rule Name: YourRuleName31
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/YourRuleName31
--------------
Rule Name: YourRuleName38
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/YourRuleName38
--------------
Rule Name: YourRuleName
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/YourRuleName
--------------
Rule Name: YourRuleName30
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/YourRuleName30
--------------
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
11. Search things.
Thing id found using search is 7925a972-c5d6-4238-a203-051aaba52613
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
12. Delete the AWS IoT Thing.
Do you want to delete the IoT Thing? (y/n)y
Deleted Thing ThingScott4
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
The AWS IoT Scenario has successfully completed.
--------------------------------------------------------------------------------

aws cloudformation deploy --template-file stack.yaml --stack-name LargeQueryStack
Destroy resources
To destroy the stack, run the following command:

aws cloudformation delete-stack --stack-name LargeQueryStack
Sample logs
A lot of logs are needed to make a robust example. If you happen to have a log group with over 10,000 logs at the ready, great! If not, there are two resources that can help:

Resources
make-log-files.sh will create 50,000 logs and divide them among 5 files of 10,000 logs each (the maximum for each call to 'PutLogEvents'). Two timestamps will output to the console. These timestamps can be used to configure the query. Five minutes of logs, starting at the time of execution, will be created. Wait at least five minutes after running this script before attempting to query.
put-log-events.sh will use the AWS CLI to put the created files from Step 1 into the log group/stream created by the CloudFormation template.
Implementations
This example is implemented in the following languages:

JavaScript
Additional reading
CloudWatch Logs Insights query syntax