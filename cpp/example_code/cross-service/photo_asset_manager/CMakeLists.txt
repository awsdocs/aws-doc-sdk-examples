cmake_minimum_required(VERSION 3.13)
set(CMAKE_CXX_STANDARD 11)
project(cpp_pam_lambdas LANGUAGES CXX)

find_package(aws-lambda-runtime REQUIRED)
find_package(AWSSDK REQUIRED COMPONENTS s3 sns rekognition dynamodb)

add_executable(${PROJECT_NAME}
        cpp_pam_lambdas.cpp
        cpp_lambda_functions.cpp)


target_link_libraries(${PROJECT_NAME}
        PUBLIC
        AWS::aws-lambda-runtime
        ${AWSSDK_LINK_LIBRARIES}
        jsoncpp
        zip
        )

aws_lambda_package_target(${PROJECT_NAME})