#! /bin/bash
# Author : @razguru
# Version : 1.1 - - Added cross account action through --profile assume role
# Desc : Discover or/and modify GP2 volumes with/without snapshot

reg="$1"
region="$2"
file_name="$3"
snap="$4"
account="$6"
dt=`date +%F-%T`

# Ensure region is provided
if [ -z $region ] || [[ $reg != --region ]] || [ -z $file_name ]; then
	echo "Please provide region/all, target volume-list/all, snapshot option and optional --profile for cross account action .."
	echo ""
	echo "To create list of all GP2, GP3, io1, io2 volumes in any single region or all regions ::  $0 --region <region_name>/all discover no-snapshot"
	echo "To migrate listed GP2 volumes from a file w/o snapshot ::  $0 --region <region_name> <volume_list_file.txt> no-snapshot"
	echo "To migrate listed GP2 volumes from a file with snapshot ::  $0 --region <region_name> <volume_list_file.txt> snapshot"
        echo "To migrate all GP2 volumes in a region w/o snapshot ::  $0 --region <region_name> migrate no-snapshot"
	echo "To migrate all GP2 volumes in a region with snapshot ::  $0 --region <region_name> migrate snapshot"
	echo "To migrate all GP2 volumes across all regions w/o snapshot ::  $0 --region all migrate no-snapshot"
	echo "To migrate all GP2 volumes across all regions with snapshot ::  $0 --region all migrate snapshot"
	echo "To perform any of the above action on cross accounts where IAM role, permission and profile is already setup, add last 2 parameters --profile <profile_name>"

	echo ""
exit 1
fi

if [[ $region != all ]]; then gp2_vol_id="$account"_"$region"_"$dt"_gp2_vol_id.txt
fi

if [[ $region != all ]] && [[ $file_name == discover ]]; then  disc="discover"
fi

if [[ $file_name != migrate ]] && [[ $file_name != discover ]] && [ $region == all ]; then
        echo "To target listed GP2 volumes from $file_name pls use - $0 region <region_name> <volume_list_file.txt>"
exit 1
fi

if [[ $file_name != migrate ]] && [[ $file_name != discover ]] && [ ! -s "$file_name" ] ; then
         echo "Provided file "$file_name" is either empty or does not exist !"
         exit 1
fi

if [ ! -z "$file_name" ] && [[ $file_name != migrate ]] && [[ $file_name != discover ]] && [ -s "$file_name" ] ; then
        manual="conversion"
fi
if [ ! -z "$file_name" ] && [[ $file_name != migrate ]] && [[ $file_name != discover ]] && [ -s "$file_name" ] && [[ $snap = snapshot ]]; then
        manual="snap_conversion"
fi

if [[ $region != all ]] && [[ $file_name = migrate ]] && [[ $file_name != discover ]] && [[ $snap != snapshot ]]; then
	action="auto_conversion"
fi

# Find all GP2 volumes within the given list
manual-disc()
{
      if [ ! -z "$file_name" ] && [ -s "$file_name" ] ; then 
	 /bin/cp "$file_name" "$gp2_vol_id" &> /dev/null
	 echo "Discovered ....( `egrep vol "$gp2_vol_id"|wc -l` ).... GP2 volumes from "$file_name""
 else
	 echo "Provided file "$file_name" is either empty or does not exist !"
	 exit 1
      fi
}

# Find all GP2 volumes within the given region
auto-disc()
{
gp2_vol_id="$account"_"$region"_"$dt"_gp2_vol_id.txt
gp3_vol_id="$account"_"$region"_"$dt"_gp3_vol_id.txt
io1_vol_id="$account"_"$region"_"$dt"_io1_vol_id.txt
io2_vol_id="$account"_"$region"_"$dt"_io2_vol_id.txt

gp2_vol_error="$account"_"$region"_"$dt"_gp2_vol_error.txt
/usr/bin/aws ec2 describe-volumes --region "${region}" --profile "${account}" --filters Name=volume-type,Values=gp2 > /dev/null  2> "$gp2_vol_error"
if [ -s "$gp2_vol_error" ]; then
        echo -n "$region -- error -- " && echo `cat $gp2_vol_error`
	echo ""
else
	rm -f $gp2_vol_error &> /dev/null
	/usr/bin/aws ec2 describe-volumes --region "${region}" --profile "${account}" --filters Name=volume-type,Values=gp2  | jq -r '.Volumes[].VolumeId' > "$gp2_vol_id"
        /usr/bin/aws ec2 describe-volumes --region "${region}" --profile "${account}" --filters Name=volume-type,Values=gp3 | jq -r '.Volumes[].VolumeId' > "$gp3_vol_id"
        /usr/bin/aws ec2 describe-volumes --region "${region}" --profile "${account}" --filters Name=volume-type,Values=io1 | jq -r '.Volumes[].VolumeId' > "$io1_vol_id"
        /usr/bin/aws ec2 describe-volumes --region "${region}" --profile "${account}" --filters Name=volume-type,Values=io2 | jq -r '.Volumes[].VolumeId' > "$io2_vol_id"	
	if [ -s "$gp2_vol_id" ]; then
       	echo -n "$account -- Discovered ....( `egrep vol "$gp2_vol_id"|wc -l` ).... GP2 volumes in $region" && echo " || GP2 volume-ids list : $gp2_vol_id"
	echo ""	
else
       echo "$account -- No GP2 volume found in $region" && rm -f $gp2_vol_id &> /dev/null
       echo ""
fi
        if [ -s "$gp3_vol_id" ]; then
        echo -n "$account -- Discovered ....( `egrep vol "$gp3_vol_id"|wc -l` ).... GP3 volumes in $region" && echo " || GP3 volume-ids list : $gp3_vol_id"
        echo ""
else
       echo "$account -- No GP3 volume found in $region" && rm -f $gp3_vol_id &> /dev/null
       echo ""
fi
        if [ -s "$io1_vol_id" ]; then
        echo -n "$account -- Discovered ....( `egrep vol "$io1_vol_id"|wc -l` ).... io1 volumes in $region" && echo " || io1 volume-ids list : $io1_vol_id"
        echo "" 
else
       echo "$account -- No io1 volume found in $region" && rm -f $io1_vol_id &> /dev/null
       echo ""
fi
        if [ -s "$io2_vol_id" ]; then
        echo -n "$account -- Discovered ....( `egrep vol "$io2_vol_id"|wc -l` ).... io2 volumes in $region" && echo " || io2 volume-ids list : $io2_vol_id"
        echo ""
else
       echo "$account -- No io2 volume found in $region" && rm -f $io2_vol_id &> /dev/null
       echo ""
fi
fi
}

# Converting discovered GP2 volumes to GP3
convert()
{
if [ -s "$gp2_vol_id" ]; then 
	for vol_id in `cat "$gp2_vol_id"`;do echo "Modifying volume ${vol_id} to GP3"
	result=$(/usr/bin/aws ec2 modify-volume --region "${region}" --volume-type=gp3 --volume-id "${vol_id}" --profile "${account}" | jq '.VolumeModification.ModificationState' | sed 's/"//g')
    if [ $? -eq 0 ] && [ "${result}" == "modifying" ];then
        echo "`date +%F-%T` SUCCESS: volume ${vol_id} changed to GP3 .. state 'modifying'" |tee -a "$account"_"$region"_"$dt"_modify_vol.log
	echo ""
    else
        echo -n "`date +%F-%T` ERROR: couldn't change volume ${vol_id} type to GP3!" |tee -a "$account"_"$region"_"$dt"_modify_vol.log
        echo ""
	exit 1
    fi
done

echo ""
echo "To track the progress of migration, please run -    bash gp2_gp3_migration_progress.sh $gp2_vol_id $region" 
echo ""
echo "GP2 volume-ids list : $gp2_vol_id"
echo ""
echo "GP2-GP3 volume modification logs : "$account"_"$region"_"$dt"_modify_vol.log"
echo "~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~"
echo ""
fi
}

snap-convert()
{
if [ -s "$gp2_vol_id" ]; then
	for vol_id in `cat "$gp2_vol_id"`;do
	/usr/bin/aws ec2 create-snapshot --region "${region}" --volume-id "${vol_id}" --profile "${account}" --description 'Pre GP3 migration' --tag-specifications 'ResourceType=snapshot,Tags=[{Key=state,Value=pre-gp3}]' &>> "$account"_"$region"_"$dt"_snapshot_vol.log
	if [ $? -ne 0 ]; then
	       echo ERROR: "snapshot creation failed for volume_id $vol_id, skipping this volume, check "$account"_"$region"_"$dt"_snapshot_vol.log"
       else
               echo "Creating snapshot for "${vol_id}""

        sleep 10 ; snap_state=$(/usr/bin/aws ec2 describe-snapshots --region "${region}" --profile "${account}" --filters Name=volume-id,Values="${vol_id}" Name=tag:state,Values=pre-gp3| jq -r '.Snapshots[].State')

	while [ "${snap_state}" != "completed" ];do
        echo "`date +%F-%T` : waiting for snapshot completion .. .. "
	sleep 10
	snap_state=$(/usr/bin/aws ec2 describe-snapshots --region "${region}" --profile "${account}" --filters Name=volume-id,Values="${vol_id}" Name=tag:state,Values=pre-gp3| jq -r '.Snapshots[].State')
        done
	snapshot_id=$(/usr/bin/aws ec2 describe-snapshots --region "${region}" --profile "${account}" --filters Name=volume-id,Values="${vol_id}" Name=tag:state,Values=pre-gp3 --query "reverse(sort_by(Snapshots, &StartTime))[0].SnapshotId")
        echo "`date +%F-%T` SUCCESS: snapshot $snapshot_id completed for  "${vol_id}"" |tee -a "$account"_"$region"_"$dt"_snapshot_vol.log
	
	echo "Modifying volume ${vol_id} to GP3"
	result=$(/usr/bin/aws ec2 modify-volume --region "${region}" --volume-type=gp3 --volume-id "${vol_id}" --profile "${account}"| jq '.VolumeModification.ModificationState' | sed 's/"//g')
    if [ $? -eq 0 ] && [ "${result}" == "modifying" ];then
        echo "`date +%F-%T` SUCCESS: volume ${vol_id} changed to GP3 .. state 'modifying'" |tee -a "$account"_"$region"_"$dt"_modify_vol.log
	echo ""
	echo "~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~"
    else
        echo "`date +%F-%T` ERROR: could not change volume ${vol_id} type to GP3!" |tee -a "$account"_"$region"_"$dt"_modify_vol.log
        echo ""
    fi
	fi
done
echo "To track the progress of migration, please run -    bash gp2_gp3_migration_progress.sh $gp2_vol_id $region"
echo ""
echo "GP2 volume-ids list : $gp2_vol_id"
echo ""
echo "Snapshot logs before GP3 migration : "$account"_"$region"_"$dt"_snapshot_vol.log"
echo ""
echo "GP2-GP3 volume modification logs : "$account"_"$region"_"$dt"_modify_vol.log"
echo "~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~*~~~~~~"
echo ""
fi
}

# Calling functions based on conditions ..
if [ $region = all ] && [[ $file_name == discover ]]; then
        /usr/bin/aws ec2 describe-regions --all-regions --profile "${account}" --filters Name=opt-in-status,Values=opt-in-not-required,opted-in --query "Regions[].{Name:RegionName}" --output text > "$account"_region_list.txt
        for region in `cat "$account"_region_list.txt`; do auto-disc ; done
	exit
fi

if [ $region = all ] && [[ $file_name == migrate ]] && [[ $snap != snapshot ]]; then
        /usr/bin/aws ec2 describe-regions --all-regions --profile "${account}" --filters Name=opt-in-status,Values=opt-in-not-required,opted-in --query "Regions[].{Name:RegionName}" --output text > "$account"_region_list.txt
        for region in `cat "$account"_region_list.txt`; do auto-disc ; convert ; done
exit
fi

if [ $region = all ] && [[ $file_name == migrate ]] && [[ $snap == snapshot ]]; then
        /usr/bin/aws ec2 describe-regions --all-regions --profile "${account}" --filters Name=opt-in-status,Values=opt-in-not-required,opted-in --query "Regions[].{Name:RegionName}" --output text > "$account"_region_list.txt
        for region in `cat "$account"_region_list.txt`; do auto-disc ; snap-convert ; done
exit
fi

case $disc in
	discover )
		auto-disc
		exit 0
		;;
esac

case $manual in
        conversion )
                manual-disc
                convert
		exit 0
                ;;
esac

case $manual in
        snap_conversion )
                manual-disc
                snap-convert
                exit 0
                ;;
esac

case $snap in
        snapshot ) echo ""${account}" -- Discovering all GP2 volumes in $region ... " && echo "..  ...  ....  .....  ...... " && echo "..  ...  ....  .....  ......  .......  ........  ........."
                auto-disc
                snap-convert
                exit 0
		;;
esac

case $action in
        auto_conversion ) echo ""${account}" -- Discovering all GP2 volumes in $region ... " && echo "..  ...  ....  .....  ...... " && echo "..  ...  ....  .....  ......  .......  ........  ........."
                auto-disc
                convert
                ;;
        * ) echo "invalid response";
                exit 1;;
esac
# -- END --
