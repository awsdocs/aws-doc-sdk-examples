# syntax=docker/dockerfile:1
FROM mcr.microsoft.com/dotnet/sdk:6.0

# Add the dotnet 8 runtime
COPY --from=mcr.microsoft.com/dotnet/sdk:8.0 /usr/share/dotnet/sdk /usr/share/dotnet/sdk
COPY --from=mcr.microsoft.com/dotnet/sdk:8.0 /usr/share/dotnet/shared /usr/share/dotnet/shared

# Copy the dotnetv4 folder and run unit tests from the top level solution.
# Or navigate to a service folder or a scenario folder,
# such as dotnetv4/Aurora/Scenarios to build and run an interactive scenario with `dotnet run`
COPY . /dotnetv4

RUN cd /dotnetv4 && dotnet build -v=q --property WarningLevel=0 /clp:ErrorsOnly --nologo

CMD ["dotnet", "test", "dotnetv3", "--filter", "Category=Unit|Category=Weathertop", "--verbosity=quiet", "/clp:ErrorsOnly", "--nologo"]