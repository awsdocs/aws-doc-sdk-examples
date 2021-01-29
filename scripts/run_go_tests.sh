TestGoFile () {
    if [ "$1" == "" ]
    then
       return
    fi

    pushd $1

    declare RESULT=(`go test`)  # (..) = array
    
    if [ "${RESULT[0]}" == "PASS" ]
    then
      echo 0
    else
      echo 1
    fi

    popd
}

for f in $@ ; do
    # Do any end with "_test.go"?
    path="$(dirname $f)"
    file="$(basename $f)"

    # If it's a go test file
    # test it
    [[ $file =~ ^[a-zA-Z]*_test.go$ ]] && TestGoFile "$path"
done
