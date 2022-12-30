#!/usr/bin/bash
# This script requires five environment variables:
#   RustRoot    where we can find aws-doc-sdk-examples/rust_dev_preview locally
#   FromVersion the old aws-sdk-* crate version
#   ToVersion   the new aws-sdk-* crate version
#   FromSmithy  the old Smithy crate version
#   ToSmithy    the new Smithy crate version

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
         	vetService $from $to $from_smithy $to_smithy
	        popd > /dev/null
		echo
           fi
        done

	return
    fi

    # Special case for logging
    if [ $SERVICE == "logging" ]
    then
	for d in ./*
	do
	    if [ -d "$d" ]; then
		pushd $d > /dev/null
         	vetService $from $to $from_smithy $to_smithy
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
	# Convert $from_smithy to $to_smithy
	echo Converting $from_smithy to $to_smithy
        sed -i s/$from_smithy/$to_smithy/ Cargo.toml
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

if [[ -z "${RustRoot}" ]]; then
    echo You must define the environment variable RustRoot
    exit 1
else
    root="${RustRoot}"
    echo The Rust source is found at $root
fi

# FromVersion is the old version of the Rust SDK crates that we are replacing,
# such as 0.3.0
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

if [[ -z "${FromSmithy}" ]]; then
    echo You must define the environment variable FromSmithy
    exit 1
else
    from_smithy="${FromSmithy}"
    echo The old Smithy crate version is $from_smithy
fi

# ToSmithy is the current version of the Smithy SDK crate
if [[ -z "${ToSmithy}" ]]; then
    echo You must define the environment variable ToSmithy
    exit 1
else
    to_smithy="${ToSmithy}"
    echo The new Smithy crate version is $to_smithy
fi

echo
echo Started vetting code examples at
date

for f in $root/*
do
    if [ -d "$f" ]; then
	pushd $f > /dev/null
	vetService $from $to $from_smithy $to_smithy
	popd > /dev/null
	echo
  fi
done

echo
echo Finished vetting code examples at
date

