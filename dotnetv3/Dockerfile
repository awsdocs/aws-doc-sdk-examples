# syntax=docker/dockerfile:1
FROM mcr.microsoft.com/dotnet/sdk:6.0

# Copy source code
COPY . /dotnetv3

# Perform build steps
COPY --from=mcr.microsoft.com/dotnet/sdk:5.0 /usr/share/dotnet/sdk /usr/share/dotnet/sdk
COPY --from=mcr.microsoft.com/dotnet/sdk:5.0 /usr/share/dotnet/shared /usr/share/dotnet/shared

# RUN cd /dotnetv3 && dotnet build

CMD ["dotnet", "test", "--filter", "Category=Unit"]
