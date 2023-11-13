#!/bin/bash
# Author : @razguru
# Version : 1.1 - Added cross account action through --profile assume role
# Desc : Track modification status of volumes ..
#
vol_id_list="$1"
region="$2"
account="$4"
dt=`date +%F-%T`
log=""$region"_"$dt"_vol_modification_progress.log"

#Ensure vol_id_list is provided
if [ -z $vol_id_list ] || [ ! -s $vol_id_list ] ; then
        echo "Please provide a file name listing target volume_ids followed by region_name .. example - $0 vol_id_list us-east-1"
exit 1
fi

#Ensure region is provided
if [ -z $region ]; then
	echo "Please provide a region .. example - $0 vol_id_list us-east-1"
exit 1
fi


for vol_id in `cat "$vol_id_list"`;do
	echo `date +%F-%T` >> $log && /usr/bin/aws ec2 describe-volumes-modifications --region "${region}" --profile "${account}" --volume-ids "${vol_id}" --query "VolumesModifications[*].{ID:VolumeId,STATE:ModificationState,Progress:Progress}"; done| tee -a $log
if [ $? -eq 0 ]; then
	echo " Logged the output to $log"
else
	echo "Failed to run ec2 describe-volumes-modifications, pls investigate"
fi
