#!/bin/bash
# Run the automated tests for cpp.


TEST_CMAKE_FILES=$(ls example_code/*/CMakeLists.txt)
TEST_CMAKE_FILES=($TEST_CMAKE_FILES)
echo "TEST_CMAKE_FILES ${TEST_CMAKE_FILES}"

len=${#TEST_CMAKE_FILES[@]}
echo "len ${len}"
if [[ ${len} == 0 ]]; then
    echo "No CMake files found!"
    usage
fi

BASE_DIR=$(pwd)
echo "BASE_DIR ${BASE_DIR}"
BUILD_DIR="${BASE_DIR}/build"
echo "BUILD_DIR ${BUILD_DIR}"
mkdir -pv ${BUILD_DIR}

HAD_ERROR=false
for MAKE_FILE in "${TEST_CMAKE_FILES[@]}"
do
  SOURCE_DIR="${MAKE_FILE%CMakeLists.txt}"
  MODULE_BUILD_DIR="${BUILD_DIR}/${SOURCE_DIR}"
  echo "MODULE_BUILD_DIR ${MODULE_BUILD_DIR}"
  mkdir -pv "${MODULE_BUILD_DIR}"
  cd "${MODULE_BUILD_DIR}"
  echo "cmake ${BASE_DIR}/${SOURCE_DIR}"
  cmake "${BASE_DIR}/${SOURCE_DIR}" -DBUILD_TESTS=ON
  if [ $? != 0 ]; then
    echo "ERROR with cmake ${$?}"
    HAD_ERROR=true
    continue
  fi
  make
  if [ $? != 0 ]; then
    echo "ERROR with make ${$?}"
    HAD_ERROR=true
    continue
  fi
done

if [ HAD_ERROR ]; then
  exit 1
else
  exit 0
fi
