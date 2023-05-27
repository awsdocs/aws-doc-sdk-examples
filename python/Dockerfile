# To build the docker image, run the following command from the shell. This command must be run in
# the "aws-doc-sdk-examples" directory, the parent directory of "python", in order to access the resources folder.
#
# 'docker build -f python/Dockerfile -t <a_docker_file_name> .'

# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4125
FROM python:3

RUN mkdir -p /src/python
COPY python /src/python/

RUN mkdir -p /src/resources/sample_files
COPY resources/sample_files /src/resources/sample_files

RUN python -m pip install -r src/python/test_tools/requirements.txt

# Needed for keyspaces integration test.
RUN curl https://certs.secureserver.net/repository/sf-class2-root.crt --output sf-class2-root.crt

WORKDIR /src/

CMD ["bash"]

# Run image with credentials:
#   Windows:
#     docker run -it --volume <user root>\.aws:/root/.aws <image ID>
# Run all unit tests in the docker container:
#   python -m python.test_tools.run_all_tests > test-run-unit-$(date +"%Y-%m-%d").out
# Run all integration tests in the docker container:
#   python -m python.test_tools.run_all_tests --integ > test-run-integ-$(date +"%Y-%m-%d").out
