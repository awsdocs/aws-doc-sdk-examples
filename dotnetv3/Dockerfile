# syntax=docker/dockerfile:1
FROM mcr.microsoft.com/dotnet/sdk:6.0

# Enable the .NET 5.0 SDK if running 5.0 examples:
COPY --from=mcr.microsoft.com/dotnet/sdk:5.0 /usr/share/dotnet/sdk /usr/share/dotnet/sdk
COPY --from=mcr.microsoft.com/dotnet/sdk:5.0 /usr/share/dotnet/shared /usr/share/dotnet/shared

# Copy the dotnetv3 folder and run unit tests from the top level solution.
# Or navigate to a service folder or a scenario folder,
# such as dotnetv3/CloudWatch/Scenarios to build and run an interactive scenario with `dotnet run`
COPY . /dotnetv3

RUN cd /dotnetv3 && dotnet build -v=q --property WarningLevel=0 /clp:ErrorsOnly --nologo

CMD ["dotnet", "test", "dotnetv3", "--filter", "Category=Unit", "--verbosity=quiet", "/clp:ErrorsOnly", "--nologo"]