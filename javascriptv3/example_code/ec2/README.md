# Amazon Elastic Computing (Amazon EC2) JavaScript SDK v3 code examples
Amazon EC2 is a web service that provides secure, resizable compute capacity in the cloud. 
It is designed to make web-scale cloud computing easier for developers.

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon EC2 examples:

- [Allocate addresses](src/ec2_allocateaddress.js)
- [Create instances](src/ec2_createinstances.js)
- [Create a key pair](src/ec2_createkeypair.js)
- [Create a security group](src/ec2_createsecuritygroup.js)
- [Delete a key pair](src/ec2_deletekeypair.js)
- [Delete a security group](src/ec2_deletesecuritygroup.js)
- [Describe addresses](src/ec2_describeaddresses.js)
- [Describe instances](src/ec2_describeinstances.js)
- [Describe regions and zones](src/ec2_describeregionsandzones.js)
- [Describe security groups](src/ec2_describesecuritygroups.js)
- [Monitor instances](src/ec2_monitorinstances.js)
- [Reboot instances](src/ec2_rebootinstances.js)
- [Release addresses](src/ec2_releaseaddress.js)
- [Stop/start instances](src/ec2_startstopinstances.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-example-javascript-syntax.html).

## Getting started

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
   See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
   instructions.

2. Install the dependencies listed in the package.json.

```
npm install node -g
cd javascriptv3/example_code/ec2
npm install
```
3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js // For example, node ec2_allocateaddress.js
```
## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-ec2/index.html) 
 



