cmake_minimum_required(VERSION 2.8)
project(iam-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "iam_list_users")
list(APPEND EXAMPLES "iam_create_user")
list(APPEND EXAMPLES "iam_delete_user")
list(APPEND EXAMPLES "iam_update_user")
list(APPEND EXAMPLES "iam_access_key_last_used")
list(APPEND EXAMPLES "iam_create_access_key")
list(APPEND EXAMPLES "iam_delete_access_key")
list(APPEND EXAMPLES "iam_list_access_keys")
list(APPEND EXAMPLES "iam_update_access_key")
list(APPEND EXAMPLES "iam_create_policy")
list(APPEND EXAMPLES "iam_delete_policy")
list(APPEND EXAMPLES "iam_get_policy")
list(APPEND EXAMPLES "iam_list_policies")
list(APPEND EXAMPLES "iam_attach_role_policy")
list(APPEND EXAMPLES "iam_detach_role_policy")
list(APPEND EXAMPLES "iam_create_account_alias")
list(APPEND EXAMPLES "iam_delete_account_alias")
list(APPEND EXAMPLES "iam_list_account_aliases")
list(APPEND EXAMPLES "iam_delete_server_cert")
list(APPEND EXAMPLES "iam_get_server_cert")
list(APPEND EXAMPLES "iam_list_server_certs")
list(APPEND EXAMPLES "iam_update_server_cert")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    target_link_libraries(${EXAMPLE} aws-cpp-sdk-iam)
endforeach()


