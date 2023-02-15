# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4126 
FROM mcr.microsoft.com/dotnet/sdk:6.0

#uncomment these lines to also enable the .NET 5.0 SDK if running 5.0 examples:
#COPY --from=mcr.microsoft.com/dotnet/sdk:5.0 /usr/share/dotnet/sdk /usr/share/dotnet/sdk
#COPY --from=mcr.microsoft.com/dotnet/sdk:5.0 /usr/share/dotnet/shared /usr/share/dotnet/shared

#copy the dotnetv3 folder. Navigate to a service folder and/or a scenario folder,
#such as dotnetv3/CloudWatch/Scenarios to build and run an interactive scenario with `dotnet run`
COPY . /dotnetv3

CMD ["bash"]

