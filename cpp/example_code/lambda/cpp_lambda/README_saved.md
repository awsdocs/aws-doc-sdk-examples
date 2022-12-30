docker build . -f Dockerfile -t cpp_lambda_image

docker run -d \           
-it \
--name cpp_lambda \
--mount type=bind,source="$(pwd)",target=/cpp_lambda \
cpp_lambda_image bash

docker attach cpp_lambda

cmake3 .. -DCMAKE_BUILD_TYPE=Release -DCMAKE_PREFIX_PATH=~/out

make

make aws-lambda-package-cpp_lambda_calculator