@echo off
REM Build and zip up Lambda code in main.go

set GOOS=linux
go build main.go
%gopath%\build-lambda-zip.exe -output main.zip main
