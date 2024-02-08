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

### Application Output

This Workflow does have user interaction. The following code shows the output of the Java V2 program. 

```
--------------------------------------------------------------------------------
Welcome to the AWS IoT example workflow.
This example program demonstrates various interactions with the AWS Internet of Things (IoT) Core service. The program guides you through a series of steps,
including creating an IoT Thing, generating a device certificate, updating the Thing with attributes, and so on.
It utilizes the AWS SDK for Java V2 and incorporates functionalities for creating and managing IoT Things, certificates, rules,
shadows, and performing searches. The program aims to showcase AWS IoT capabilities and provides a comprehensive example for
developers working with AWS IoT in a Java environment.


Press Enter to continue...
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
1. Create an AWS IoT Thing.
An AWS IoT Thing represents a virtual entity in the AWS IoT service that can be associated with a physical device.

Enter Thing name: foo5543
foo5543 was successfully created. The ARN value is arn:aws:iot:us-east-1:814548047983:thing/foo5543
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
2. Generate a device certificate.
A device certificate performs a role in securing the communication between devices (Things) and the AWS IoT platform.

Do you want to create a certificate for foo5543? (y/n)y
Private Key:
-----BEGIN RSA PRIVATE KEY-----
MIIEpAIBAAKCAQEAwa7WimCg83UiALVmzTsK87KbO+fug6s4CwSU86brfYLSTflq
HyjDnkAFO0o2HYi8QH+YIOfBsR+qN7v9m5+ByGHc1Vlitk0hV8KqNh6WVftktCXS
R8jVQx8LDOjj2b8QkAcx2r0yTWhsM1UyDVeqBU5YTBhgdY9CsVZtqqjcza1O/lWL
ymgTfV0tJBV2rAxAqocusjej1gHHMeFeyV9gc7w83BPnsmlcNaLawW/0MXl4367q
AnrIrk3OKMltaBfSbtUaUE12Km4L7EpEttn6AAZdry1wkKIOvkA+ig/R3mOa0Fd+
gikUPzb0pRQGrsVPmB1K4qvhrZ4sAR4LCVLhqwIDAQABAoIBACGm+jni3jRO5tCO
tET2fEmpf9Vh0AxqpHstmZ/YIdSV9Wp86pZtY8fMQdxyYYKyLltakBkvi3T2FoJG
lKMEjO0/K7qaJE+eiSuRdDehCuf3nSC77ZRxbbSS4T2l/WjpDLyps1zZQLM4cIxe
wiqVT8JKQYc/OQtUD/d2F+nuGydVJvB0h7HiKGFHnKhXJLmS4xhGYpkV4UUNTpN1
UgkLscRvJikpJFjmZ5Ubmb304VWjrZOAQGbjxLv3RPe240YIC6gyTMzsY82RdviW
3EhIdX29WHLXxjqzU72ALEpBLvqn9xrSeC80P0Ty8uJElaaojEYvbvt5hXXaWpl+
eatXUAkCgYEA7w5VhsIfS1aCEF7FjZ+MrW+Hq3r49JqmKLpN2/GG5PgjnHi223TR
ingmX89zWikdParwLIgDuOqeXKb+SiHOou7TPKtznb3umH9VYea3TQ0ID02dOPTA
TVzwoiudEiVmJouN4k7O6jT7jC+UIUlL9B3fEHeqzX/okFdMD6pDnp8CgYEAz2k2
Fb/fsjMhkln9FgTLunUgC+q6h1QHqG6KdluHCPrvU0qJalkvCxom6wEgHcljdy7y
2udirdb5f/aRLhcRJT4ufgBeuAdNJaMvuUNzRx/wKTEnsqQZAXUByJp8hfKzuCVe
GiaeKTQn2keDijoxMT2A/sxiSp76Dg3XCWGwvXUCgYAM9Nnt09eeXbHu8TQD2QHm
5ISV5rRXjYoz6uUNZqnI/ynu8Rv4CPzSZHCwAK5f1JqhHQKnZzfMbernWqWtnud0
LT2FrEU353NnEH+wLrEAnBQzwCElVR6XEtJ396Urtpsyu52VBnBwgS6hCnMc2rwj
HvtYCSRYvvX73L9imffQQQKBgQDD5cBynG4gtlBwFVSIFnGBo5xoBQS2SrSM7hMT
JPGOlsuyZIUTkDZxivrVHZQC7jJm4E36WW8HeDLhHLYUzS6heXCaC6lqsWK5OL8b
gUyBEfZYQZgYDwubJ7NOkDMqpIgAVjLiCjV5/0vgy4i+5qWNt2R9w/bWOf89hO7k
aoNu/QKBgQCOLj9ISPhH4NaMS6nUuNKUhq5MhwTI0wWng6MDXWakOdhB+FAJc/qr
d9hLZNIabkbkc5qGu8HsHO71TosGXmHg+ZSFutj8gJM5xFTynn759Ls5/iDuOToA
IUX8KgFs6VPvyxuedIc1bWtxI9FkxLkh7BGpY86tP5m9D2jvtWsNAg==
-----END RSA PRIVATE KEY-----


Certificate:
-----BEGIN CERTIFICATE-----
MIIDWTCCAkGgAwIBAgIUY3PjIZIcFhCrPuBvH16219CPqD0wDQYJKoZIhvcNAQEL
BQAwTTFLMEkGA1UECwxCQW1hem9uIFdlYiBTZXJ2aWNlcyBPPUFtYXpvbi5jb20g
SW5jLiBMPVNlYXR0bGUgU1Q9V2FzaGluZ3RvbiBDPVVTMB4XDTI0MDIwODE3MzQy
M1oXDTQ5MTIzMTIzNTk1OVowHjEcMBoGA1UEAwwTQVdTIElvVCBDZXJ0aWZpY2F0
ZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMGu1opgoPN1IgC1Zs07
CvOymzvn7oOrOAsElPOm632C0k35ah8ow55ABTtKNh2IvEB/mCDnwbEfqje7/Zuf
gchh3NVZYrZNIVfCqjYellX7ZLQl0kfI1UMfCwzo49m/EJAHMdq9Mk1obDNVMg1X
qgVOWEwYYHWPQrFWbaqo3M2tTv5Vi8poE31dLSQVdqwMQKqHLrI3o9YBxzHhXslf
YHO8PNwT57JpXDWi2sFv9DF5eN+u6gJ6yK5NzijJbWgX0m7VGlBNdipuC+xKRLbZ
+gAGXa8tcJCiDr5APooP0d5jmtBXfoIpFD829KUUBq7FT5gdSuKr4a2eLAEeCwlS
4asCAwEAAaNgMF4wHwYDVR0jBBgwFoAUUn3bmDMoitplSl0rH3twQtv9qcYwHQYD
VR0OBBYEFJn8RfLIEFDOOoAghWES4k4lgY2XMAwGA1UdEwEB/wQCMAAwDgYDVR0P
AQH/BAQDAgeAMA0GCSqGSIb3DQEBCwUAA4IBAQCsZzp4jLP0KZXqk6/7uhchXpm/
3Z0bxzM4DaVHtNqM3QRO5deJ+Cfgw4CwuGZuCps0PdVj9zIyp8tmqQHskDYgjXOy
wkBI+1KYNPJKul95HBK9abOAdXBEBQu87qKKT3UYCEqneOC0Z+FvUtdEHNrJYaxq
DSotd4dqoUO1bx9V7ufAlLGmnODd3PRqCrVgOgqJUQK6mTOOzQd3lbTMwnCdj07C
JZ3GSZrmxAnxVh2asjk1dAivb4PS4srIv1O2Fdwg+dfP/3OLm8hB3mTeyt4yk/8W
ztv1nXA4Lkm9jPt7aea+0VY2MkIH4toJOZFo/yy8o3Gj2NEK6Mlb7ZpLvl2p
-----END CERTIFICATE-----


Certificate ARN:
arn:aws:iot:us-east-1:814548047983:cert/1c9cd9a0f315b58e549e84c38ada37ced24e89047a15ff7ac4abafae9ff6dfc6
Attach the certificate to the AWS IoT Thing.
Certificate attached to Thing successfully.
Thing Details:
Thing Name: foo5543
Thing ARN: arn:aws:iot:us-east-1:814548047983:thing/foo5543
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
3. Update an AWS IoT Thing with Attributes.
 IoT Thing attributes, represented as key-value pairs, offer a pivotal advantage in facilitating efficient data
 management and retrieval within the AWS IoT ecosystem.

Press Enter to continue...
Thing attributes updated successfully.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4. Return a unique endpoint specific to the Amazon Web Services account.
 An IoT Endpoint refers to a specific URL or Uniform Resource Locator that serves as the entry point for communication between IoT devices and the AWS IoT service.

Press Enter to continue...
Extracted subdomain: a39q2exsoth3da
Full Endpoint URL: https://a39q2exsoth3da-ats.iot.us-east-1.amazonaws.com
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. List your AWS IoT certificates
Press Enter to continue...
Cert id: 1c9cd9a0f315b58e549e84c38ada37ced24e89047a15ff7ac4abafae9ff6dfc6
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/1c9cd9a0f315b58e549e84c38ada37ced24e89047a15ff7ac4abafae9ff6dfc6
Cert id: 0d211c9b39060561fb00b052f72f495cc8fc280c2cae805ba8f4a36d76ff5668
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/0d211c9b39060561fb00b052f72f495cc8fc280c2cae805ba8f4a36d76ff5668
Cert id: c0d340f1fa8484075d84b523144369a9a9c7916dee225d2f053ac1f434961fb6
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/c0d340f1fa8484075d84b523144369a9a9c7916dee225d2f053ac1f434961fb6

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. Create an IoT shadow that refers to a digital representation or virtual twin of a physical IoT device
 A Thing Shadow refers to a feature that enables you to create a virtual representation, or "shadow,"
 of a physical device or thing. The Thing Shadow allows you to synchronize and control the state of a device between
 the cloud and the device itself. and the AWS IoT service. For example, you can write and retrieve JSON data from a Thing Shadow.

Press Enter to continue...
Thing Shadow updated successfully.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Write out the state information, in JSON format.
Press Enter to continue...
Received Shadow Data: {"state":{"reported":{"temperature":25,"humidity":50}},"metadata":{"reported":{"temperature":{"timestamp":1707413791},"humidity":{"timestamp":1707413791}}},"version":1,"timestamp":1707413794}
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
8. Creates a rule
Creates a rule that is an administrator-level action.
Any user who has permission to create rules will be able to access data processed by the rule.

Enter Rule name: rule8823
IoT Rule created successfully.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
9. List your rules.
Press Enter to continue...
List of IoT Rules:
Rule Name: rule0099
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/rule0099
--------------
Rule Name: rule8823
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/rule8823
--------------
Rule Name: rule444
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/rule444
--------------
Rule Name: YourRuleName11
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/YourRuleName11

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
10. Search things using the Thing name.
Press Enter to continue...
Thing id found using search is abad8003-3abd-4614-bc04-8d0b6211eb9e
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Do you want to detach and delete the certificate for foo5543? (y/n)y
11. You selected to detach amd delete the certificate.
Press Enter to continue...
arn:aws:iot:us-east-1:814548047983:cert/1c9cd9a0f315b58e549e84c38ada37ced24e89047a15ff7ac4abafae9ff6dfc6 was successfully removed from foo5543
arn:aws:iot:us-east-1:814548047983:cert/1c9cd9a0f315b58e549e84c38ada37ced24e89047a15ff7ac4abafae9ff6dfc6 was successfully deleted.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
12. Delete the AWS IoT Thing.
Do you want to delete the IoT Thing? (y/n)y
Deleted Thing foo5543
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
The AWS IoT workflow has successfully completed.
--------------------------------------------------------------------------------

Process finished with exit code 0
