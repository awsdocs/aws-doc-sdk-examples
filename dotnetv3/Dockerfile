# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4126
FROM mcr.microsoft.com/dotnet/sdk:6.0
COPY . /dotnetv3/
RUN cd /dotnetv3/SES && \
    dotnet build
CMD ["bash"]
