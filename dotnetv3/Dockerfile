# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4126
FROM mcr.microsoft.com/dotnet/sdk:6.0
COPY . /src/
RUN cd /src/SES && \
    dotnet build
CMD ["bash"]
