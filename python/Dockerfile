# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4125
FROM python:3
COPY . /python/
RUN python -m pip install -r python/test_tools/requirements.txt
CMD ["bash"]

# Run image with credentials:
#   Windows:
#     docker run -it --volume <user root>\.aws:/root/.aws <image ID>
# Run all unit tests in the docker container:
#   python -m python.test_tools.run_all_tests > test-run-$(date +"%Y-%m-%d").out
