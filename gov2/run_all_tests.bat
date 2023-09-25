Rem Run this batch script with no arguments to run unit tests or with 'integration' to run integration tests.

for /d %%a in (*) do (
    cd %%a 
    call go test -tags=%1 -timeout=60m ./...
    cd ..
)
