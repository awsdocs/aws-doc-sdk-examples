# syntax=docker/dockerfile:1
FROM mcr.microsoft.com/dotnet/sdk:6.0

# Enable the .NET 5.0 SDK if running 5.0 examples:
COPY --from=mcr.microsoft.com/dotnet/sdk:5.0 /usr/share/dotnet/sdk /usr/share/dotnet/sdk
COPY --from=mcr.microsoft.com/dotnet/sdk:5.0 /usr/share/dotnet/shared /usr/share/dotnet/shared

COPY . /dotnetv3

RUN cd /dotnetv3 && dotnet build

CMD ["dotnet", "test", "dotnetv3", "--filter", "Category=Unit"]

