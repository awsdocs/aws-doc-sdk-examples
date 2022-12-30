# Example Lambda function using the AWS C++ Lambda runtime

Shows how to build and package an AWS Lambda function using the AWS C++ 
Lambda runtime. This function can replace the Python lambda function in the 
[Get started with functions](../get_started_with_functions_scenario.cpp) 
scenario sample code.

## Build and run the Docker container

### Prerequisites

Install [Docker](https://www.docker.com/) on your computer.

### Build the docker image

Using a shell application, navigate to the folder containing this README. 
Then enter the following commands.

```bashcd 
docker build . -f Dockerfile -t cpp_lambda_image
```

### Run the docker image

```bash
docker run -i -t --name cpp_lambda --mount type=bind,source="$(pwd)",target=/cpp_lambda cpp_lambda_image bash
```

Your command line should now be running from within the Docker container. 
The preceding run command will have bound the directory containing this 
README to the Docker container.

Entering the following command:

```bash
ls /cpp_lambda
```

should give a result like this:
```bash
Dockerfile         README.md          calculator         increment   
```

## Build the Runtime inside the Docker container

```bash
git clone https://github.com/awslabs/aws-lambda-cpp-runtime.git
cd aws-lambda-cpp-runtime
mkdir build
cd build
cmake3 .. -DCMAKE_BUILD_TYPE=Release \
  -DBUILD_SHARED_LIBS=OFF \
  -DCMAKE_INSTALL_PREFIX=~/install 
make
make install
```

## Build the application

The next step is to build the two Lambda functions used by the sample code:

1. Build the "Increment" Lambda function code.

```bash
cd /cpp_lambda/increment
mkdir build
cd build
cmake3 .. -DCMAKE_BUILD_TYPE=Release -DCMAKE_PREFIX_PATH=~/install
make
make aws-lambda-package-cpp_lambda_calculator
```

2. Build the "Calculator" Lambda function code.

```bash
cd ../../calculator
mkdir build
cd build
cmake3 .. -DCMAKE_BUILD_TYPE=Release -DCMAKE_PREFIX_PATH=~/install
make
make aws-lambda-package-cpp_lambda_calculator
```
These build steps have generated two zip files which will be used by sample 
code in [Get started with functions](../get_started_with_functions_scenario.cpp).

Copy the file `cpp_lambda/increment/build/cpp_lambda_calculator.zip` to the 
directory containing `get_started_with_functions_scenario.cpp` and rename it
`cpp_lambda_increment.zip`.

Copy the file `cpp_lambda/calculator/build/cpp_lambda_calculator.zip` to the
directory containing `get_started_with_functions_scenario.cpp`.

These files are referenced in the following section of code:

```cpp
#if USE_CPP_LAMBDA_FUNCTION
        static Aws::String LAMBDA_HANDLER_NAME(
                "cpp_lambda_calculator");
        static Aws::String INCREMENT_LAMBDA_CODE(
                SOURCE_DIR "/cpp_lambda_increment.zip");
        static Aws::String CALCULATOR_LAMBDA_CODE(
                SOURCE_DIR "/cpp_lambda_calculator.zip");
#else
```

Finally, set preprocessor constant `USE_CPP_LAMBDA_FUNCTION` to 1.

```cpp
#define USE_CPP_LAMBDA_FUNCTION 1
```