# syntax=docker/dockerfile:1
FROM mcr.microsoft.com/dotnet/sdk:6.0
# copy in files
RUN mkdir /src
COPY . /src/

# TODO: update fork & resolve dependencies from /cross-service/AuroraItemTracker
WORKDIR /src/dotnetv3/SES
RUN dotnet build

CMD ["bash"]
