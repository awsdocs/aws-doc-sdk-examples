@echo off

if "%1"=="" goto noargs

aws lambda invoke --function-name %1 --payload file://sqs-payload.json output
goto end

:noargs
echo You must supply the name of a Lambda function

:end
