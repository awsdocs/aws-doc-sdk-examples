# syntax=docker/dockerfile:1
FROM mcr.microsoft.com/dotnet/sdk:6.0
# copy in files
RUN mkdir /src
COPY . /src/

# resolve dependencies
WORKDIR /src
CMD ["bash"]
