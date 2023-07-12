cmake_minimum_required(VERSION 3.12)
set(CMAKE_CXX_STANDARD 11)
project(cpp_pam_lambdas LANGUAGES CXX)

list(APPEND CMAKE_PREFIX_PATH "/Users/meyertst/Development/aws-lambda-install") # TODO remove from PR

find_package(aws-lambda-runtime REQUIRED)
find_package(AWSSDK REQUIRED COMPONENTS s3 sns rekognition dynamodb)



add_executable(${PROJECT_NAME}
        cpp_pam_lambdas.cpp
        cpp_lambda_functions.cpp)

# TODO remove from PR if only homebrew.
target_include_directories(${PROJECT_NAME}
        PRIVATE
        /opt/homebrew/include)

target_link_libraries(${PROJECT_NAME}
        PUBLIC
        AWS::aws-lambda-runtime
        ${AWSSDK_LINK_LIBRARIES}
        jsoncpp
        zip
        )

target_link_directories(${PROJECT_NAME}  #TODO: remove this
        PRIVATE
        /opt/homebrew/lib)

target_compile_definitions(${PROJECT_NAME}
        PUBLIC
        AWS_LAMBDA_LOG=3)

aws_lambda_package_target(${PROJECT_NAME})