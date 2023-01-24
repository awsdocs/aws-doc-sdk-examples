cmake_minimum_required(VERSION 3.12)
set(CMAKE_CXX_STANDARD 11)
project(cpp_lambda_calculator LANGUAGES CXX)

find_package(aws-lambda-runtime REQUIRED)
add_executable(${PROJECT_NAME} "main.cpp")
target_link_libraries(${PROJECT_NAME}
        PUBLIC
        AWS::aws-lambda-runtime
        jsoncpp
        )

aws_lambda_package_target(${PROJECT_NAME})