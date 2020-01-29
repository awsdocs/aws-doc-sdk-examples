@echo off
REM Build and zip up Lambda code in main.go

set GOOS=linux
go build -o main main.go
%gopath%\build-lambda-zip.exe -o main.zip main

cp main.zip ..

