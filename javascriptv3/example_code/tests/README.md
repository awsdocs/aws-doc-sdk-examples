# Unit tests for AWS SDK for JavaScript (version 3)
Run the unit tests using [Jest](https://jestjs.io/).

1. Clone the [AWSDocs Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [here](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

2. Install Jest using npm.
```
npm install --save-dev jest
```
**NOTE**: For more information on installing Jest, see [Jest - Getting Started](https://jestjs.io/docs/en/getting-started).
3. Navigate to the directory containing the files you want to test. For example:
```
cd tests/s3
```
4. Change the extension of the files you're testing from ```.ts``` to ```.js```.
5. Add ```module.exports ={*}``` to the bottom of each file your testing.

4. Run tests.
```
npm run test
```
