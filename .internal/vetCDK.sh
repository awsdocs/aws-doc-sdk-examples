#!/usr/bin/bash
# The test gives us better info
#  start=`date +%s`

# The relative path of the CDK apps,
# relative to the home directory. 
srcDir=resources/CDK

if [ -e $srcDir ]
then
    echo "Found source directory $srcDir"
else
    echo "Could not find $srcDir"
    exit 1
fi

# Navigate to src directory
pushd $srcDir

for i in `/bin/ls`
do
    # Filter out top-level readme
    if [ "$i" == "README.md" ]
    then
	echo Skipping $i
	continue
    fi

    # $i looks like:
    # lambda_using_api_gateway

    pushd $i

    echo Vetting $i
    npm update > build.txt 2>&1
    npm run build >> build.txt 2>&1
    grep error build.txt
    echo ""
    
    popd
done

echo ""

popd
