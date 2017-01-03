cmake_minimum_required(VERSION 2.8)
project(ec2-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "ec2_create_instance")
list(APPEND EXAMPLES "ec2_describe_instances")
list(APPEND EXAMPLES "ec2_monitor_instance")
list(APPEND EXAMPLES "ec2_reboot_instance")
list(APPEND EXAMPLES "ec2_start_stop_instance")
list(APPEND EXAMPLES "ec2_describe_addresses")
list(APPEND EXAMPLES "ec2_allocate_address")
list(APPEND EXAMPLES "ec2_release_address")
list(APPEND EXAMPLES "ec2_describe_key_pairs")
list(APPEND EXAMPLES "ec2_create_key_pair")
list(APPEND EXAMPLES "ec2_delete_key_pair")
list(APPEND EXAMPLES "ec2_describe_regions_and_zones")
list(APPEND EXAMPLES "ec2_describe_security_groups")
list(APPEND EXAMPLES "ec2_create_security_group")
list(APPEND EXAMPLES "ec2_delete_security_group")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    target_link_libraries(${EXAMPLE} aws-cpp-sdk-ec2)
endforeach()


