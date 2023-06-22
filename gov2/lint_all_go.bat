Rem Run this batch script to lint Go files in all subfolders.

Rem When all Go examples have been updated, change this allow list to (*).
for /d %%a in (aurora demotools, dynamodb, iam, lambda, s3, testtools) do (
    cd %%a
    call golangci-lint run
    cd ..
)
