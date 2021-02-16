TestGoFile () {
    if [ "$1" == "" ]
    then
       exit 0
    fi

    pushd $1 > /dev/null 2>&1

    declare RESULT=(`go test`)  # (..) = array
    
    if [ "${RESULT[0]}" == "PASS" ]
    then
      exit 0
    else
      exit 1
    fi

    popd > /dev/null 2>&1
}

for f in $@ ; do
    # Do any end with "_test.go"?
    path="$(dirname $f)"
    file="$(basename $f)"

    # If it's a go test file
    # test it
    [[ $file =~ ^[a-zA-Z]*_test.go$ ]] && TestGoFile "$path"
done
