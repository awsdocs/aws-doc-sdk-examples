#!/usr/bin/bash
# Build and zip up Lambda code in main.go

GOOS=linux
go build -o main main.go
zip main.zip main
