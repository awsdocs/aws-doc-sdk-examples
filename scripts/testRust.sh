#!/usr/bin/bash

vetService () {
    SERVICE=$(basename $PWD)
    echo "${SERVICE}"

    # Special case for cross_service
    if [ $SERVICE == "cross_service" ]
    then
	for d in ./*
	do
	    if [ -d "$d" ]; then
		pushd $d > /dev/null
         	vetService $from $to
	        popd > /dev/null
		echo
           fi
        done

	return
    fi

    # Does Cargo.toml have the old version?
    foundOld=`grep "$1" Cargo.toml`

    if [ "$foundOld" != "" ]    
    then
        # Convert $from to $to
	echo Converting $from to $to
        sed -i s/$from/$to/ Cargo.toml
    fi

    foundNew=`grep "$2" Cargo.toml`

    if [ "$foundNew" == "" ]
    then
	echo "$SERVICE does not have the latest versions!!!"
	return
    fi
    
    touch errors.txt
    rm errors.txt

    # Get name from Cargo.toml
    NAME=`grep name Cargo.toml | sed 's/.*=//' | tr -d \"`

    # Run clippy:
    cargo clippy -p $NAME -- --no-deps 2> errors.txt
    hasErrors=`grep aborting errors.txt`

    if [ "$hasErrors" != "" ]
    then
	echo Found errors in $SERVICE
	echo See errors.txt for details
	return
    fi

    rm errors.txt
    cargo fmt
}

# We need three environment variables:
# RustRoot is where we can find aws-doc-sdk-examples/rust_dev_preview locally
if [[ -z "${RustRoot}" ]]; then
    echo You must define the environment variable RustRoot
    exit 1
else
    root="${RustRoot}"
    echo The Rust source is found at $root
fi

# FromVersion is the old version of the Rust SDK crates that we are replacing
if [[ -z "${FromVersion}" ]]; then
    echo You must define the environment variable FromVersion
    exit 1
else
    from="${FromVersion}"
    echo The old crate version is $from
fi

# ToVersion is the current version of the Rust SDK crates
if [[ -z "${ToVersion}" ]]; then
    echo You must define the environment variable ToVersion
    exit 1
else
    to="${ToVersion}"
    echo The new crate version is $to
fi

echo

for f in $root/*
do
    if [ -d "$f" ]; then
	pushd $f > /dev/null
	vetService $from $to
	popd > /dev/null
	echo
  fi
done

echo
