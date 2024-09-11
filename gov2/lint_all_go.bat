Rem Run this batch script to lint Go files in all subfolders.

for /d %%a in (*) do (
    pushd %%a
    call golangci-lint run
    popd
)
