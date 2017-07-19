cmake_minimum_required(VERSION 2.8)
project(ec2-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "create_instance")
list(APPEND EXAMPLES "describe_instances")
list(APPEND EXAMPLES "monitor_instance")
list(APPEND EXAMPLES "reboot_instance")
list(APPEND EXAMPLES "start_stop_instance")
list(APPEND EXAMPLES "describe_addresses")
list(APPEND EXAMPLES "allocate_address")
list(APPEND EXAMPLES "release_address")
list(APPEND EXAMPLES "describe_key_pairs")
list(APPEND EXAMPLES "create_key_pair")
list(APPEND EXAMPLES "delete_key_pair")
list(APPEND EXAMPLES "describe_regions_and_zones")
list(APPEND EXAMPLES "describe_security_groups")
list(APPEND EXAMPLES "create_security_group")
list(APPEND EXAMPLES "delete_security_group")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    target_link_libraries(${EXAMPLE} aws-cpp-sdk-ec2)
endforeach()


