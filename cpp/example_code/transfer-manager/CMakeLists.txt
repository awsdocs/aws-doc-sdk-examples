cmake_minimum_required(VERSION 3.2)
project(SDKTest)
SET(CMAKE_CXX_STANDARD 11)

find_package(AWSSDK REQUIRED COMPONENTS transfer)
add_executable(SDKTest transferOnStream.cpp)
target_link_libraries(SDKTest ${AWSSDK_LINK_LIBRARIES})
