" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_EC2_ACTIONS definition
  public
  final
  create public .

public section.
protected section.
private section.

  methods ALLOCATE_ADDRESS
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2ALLOCATEADDRESSRS .
  methods CREATE_INSTANCE
    importing
      !IV_AMI_ID type /AWS1/EC2IMAGEID
      !IV_TAG_VALUE type /AWS1/EC2STRING
      !IV_SUBNET_ID type /AWS1/EC2SUBNETID
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2RESERVATION .
  methods CREATE_KEY_PAIR
    importing
      !IV_KEY_NAME type /AWS1/EC2STRING
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2KEYPAIR .
  methods CREATE_SECURITY_GROUP
    importing
      !IV_SECURITY_GROUP_NAME type /AWS1/EC2STRING
      !IV_VPC_ID type /AWS1/EC2VPCID
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2CREATESECGROUPRSLT .
  methods DELETE_SECURITY_GROUP
    importing
      !IV_SECURITY_GROUP_ID type /AWS1/EC2SECURITYGROUPID .
  methods DELETE_KEY_PAIR
    importing
      !IV_KEY_NAME type /AWS1/EC2KEYPAIRNAME .
  methods DESCRIBE_ADDRESSES
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2DESCRADDRESSESRSLT .
  methods DESCRIBE_INSTANCES
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2DESCRINSTSRESULT .
  methods DESCRIBE_KEY_PAIRS
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2DESCRKEYPAIRSRSLT .
  methods DESCRIBE_REGIONS
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2DESCRREGIONSRESULT .
  methods DESCRIBE_SECURITY_GROUPS
    importing
      !IV_GROUP_ID type /AWS1/EC2SECURITYGROUPID
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2DESCRSECGROUPSRSLT .
  methods MONITOR_INSTANCE
    importing
      !IV_INSTANCE_ID type /AWS1/EC2INSTANCEID .
  methods REBOOT_INSTANCE
    importing
      !IV_INSTANCE_ID type /AWS1/EC2INSTANCEID .
  methods RELEASE_ADDRESS
    importing
      !IV_ALLOCATION_ID type /AWS1/EC2ALLOCATIONID .
  methods START_INSTANCE
    importing
      !IV_INSTANCE_ID type /AWS1/EC2INSTANCEID
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2STARTINSTSRESULT .
  methods STOP_INSTANCE
    importing
      !IV_INSTANCE_ID type /AWS1/EC2INSTANCEID
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2STOPINSTSRESULT .
  methods ASSOCIATE_ADDRESS
    importing
      !IV_INSTANCE_ID type /AWS1/EC2INSTANCEID
      !IV_ALLOCATION_ID type /AWS1/EC2ALLOCATIONID
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2ASSOCADDRESSRESULT .
  methods DESCRIBE_AVAILABILITY_ZONES
    returning
      value(OO_RESULT) type ref to /AWS1/CL_EC2DESCRIBEAZSRESULT .
ENDCLASS.



CLASS ZCL_AWS1_EC2_ACTIONS IMPLEMENTATION.


  METHOD allocate_address.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.allocate_address]
    TRY.
        oo_result = lo_ec2->allocateaddress( iv_domain = 'vpc' ).   " oo_result is returned for testing purpose "
        MESSAGE 'Allocated an Elastic IP address' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.allocate_address]
  ENDMETHOD.


  METHOD associate_address.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.associate_address]
    TRY.
        oo_result = lo_ec2->associateaddress(                         " oo_result is returned for testing purpose "
            iv_allocationid = iv_allocation_id
            iv_instanceid = iv_instance_id
        ).
        MESSAGE 'Associated Elastic IP address with an EC2 instance' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.associate_address]
  ENDMETHOD.


  METHOD create_instance.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.create_instance]

    " Create tags for resource created during instance launch "
    DATA lt_tagspecifications TYPE /aws1/cl_ec2tagspecification=>tt_tagspecificationlist.
    DATA ls_tagspecifications LIKE LINE OF lt_tagspecifications.
    ls_tagspecifications =  NEW /aws1/cl_ec2tagspecification(
      iv_resourcetype = 'instance'
      it_tags = VALUE /aws1/cl_ec2tag=>tt_taglist(
        ( NEW /aws1/cl_ec2tag( iv_key = 'Name' iv_value = iv_tag_value ) )
      )
    ).
    APPEND ls_tagspecifications TO lt_tagspecifications.

    TRY.
        " Create/Launch EC2 instance "
        oo_result = lo_ec2->runinstances(                           " oo_result is returned for testing purpose "
          iv_imageid = iv_ami_id
          iv_instancetype = 't2.micro'
          iv_maxcount = 1
          iv_mincount = 1
          it_tagspecifications = lt_tagspecifications
          iv_subnetid = iv_subnet_id
        ).
        MESSAGE 'EC2 instance created' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.create_instance]
  ENDMETHOD.


  METHOD create_key_pair.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.create_key_pair]
    TRY.
        oo_result = lo_ec2->createkeypair( iv_keyname = iv_key_name ).                            " oo_result is returned for testing purpose "
        MESSAGE 'EC2 key pair created' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.create_key_pair]
  ENDMETHOD.


  METHOD create_security_group.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.create_security_group]
    TRY.
        oo_result = lo_ec2->createsecuritygroup(                 " oo_result is returned for testing purpose "
          iv_description = 'Security group example'
          iv_groupname = iv_security_group_name
          iv_vpcid = iv_vpc_id
        ).
        MESSAGE 'Security group created' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.create_security_group]
  ENDMETHOD.


  METHOD delete_key_pair.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.delete_key_pair]
    TRY.
        lo_ec2->deletekeypair( iv_keyname = iv_key_name ).
        MESSAGE 'EC2 key pair deleted' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.delete_key_pair]
  ENDMETHOD.


  METHOD delete_security_group.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.delete_security_group]
    TRY.
        lo_ec2->deletesecuritygroup( iv_groupid = iv_security_group_id ).
        MESSAGE 'Security group deleted' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.delete_security_group]
  ENDMETHOD.


  METHOD describe_addresses.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_addresses]
    TRY.
        oo_result = lo_ec2->describeaddresses( ) .                        " oo_result is returned for testing purpose "
        DATA(lt_addresses) = oo_result->get_addresses( ).
        MESSAGE 'Retrieved information about Elastic IP addresses' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_addresses]
  ENDMETHOD.


  METHOD describe_availability_zones.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_availability_zones]
    TRY.
        oo_result = lo_ec2->describeavailabilityzones( ) .                        " oo_result is returned for testing purpose "
        DATA(lt_zones) = oo_result->get_availabilityzones( ).
        MESSAGE 'Retrieved information about availability zone(s)' TYPE 'I'.

      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.

    " snippet-end:[ec2.abapv1.describe_availability_zones]
  ENDMETHOD.


  METHOD describe_instances.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_instances]
    TRY.
        oo_result = lo_ec2->describeinstances( ) .                        " oo_result is returned for testing purpose "

        " Retrieving details of EC2 instance(s) "
        DATA: lv_istance_id    TYPE /aws1/ec2string,
              lv_status        TYPE /aws1/ec2instancestatename,
              lv_instance_type TYPE /aws1/ec2instancetype,
              lv_image_id      TYPE /aws1/ec2string.
        LOOP AT oo_result->get_reservations( ) INTO DATA(lo_reservation).
          LOOP AT lo_reservation->get_instances( ) INTO DATA(lo_instance).
            lv_istance_id = lo_instance->get_instanceid( ).
            lv_status =  lo_instance->get_state( )->get_name( ).
            lv_instance_type = lo_instance->get_instancetype( ).
            lv_image_id = lo_instance->get_imageid( ).
          ENDLOOP.
        ENDLOOP.
        MESSAGE 'Retrieved information about EC2 instances' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_instances]
  ENDMETHOD.


  METHOD describe_key_pairs.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_key_pairs]
    TRY.
        oo_result = lo_ec2->describekeypairs( ) .                        " oo_result is returned for testing purpose "
        DATA(lt_key_pairs) = oo_result->get_keypairs( ).
        MESSAGE 'Retrieved information about key pair(s)' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_key_pairs]
  ENDMETHOD.


  METHOD describe_regions.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_regions]
    TRY.
        oo_result = lo_ec2->describeregions( ) .                        " oo_result is returned for testing purpose "
        DATA(lt_regions) = oo_result->get_regions( ).
        MESSAGE 'Retrieved information about region(s)' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.

    " snippet-end:[ec2.abapv1.describe_regions]
  ENDMETHOD.


  METHOD describe_security_groups.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_security_groups]
    TRY.
        DATA lt_group_ids TYPE /aws1/cl_ec2groupidstrlist_w=>tt_groupidstringlist.
        APPEND NEW /aws1/cl_ec2groupidstrlist_w( iv_value = iv_group_id ) TO lt_group_ids.
        oo_result = lo_ec2->describesecuritygroups( it_groupids = lt_group_ids ).         " oo_result is returned for testing purpose "
        DATA(lt_security_groups) = oo_result->get_securitygroups( ).
        MESSAGE 'Retrieved information about security group(s)' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_security_groups]
  ENDMETHOD.


  METHOD monitor_instance.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.monitor_instance]

    DATA lt_instance_ids TYPE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist.
    APPEND NEW /aws1/cl_ec2instidstringlist_w( iv_value = iv_instance_id ) TO lt_instance_ids.

    "Perform Dry Run"
    TRY.
        " DryRun is set to true to check if the we have the required permissions to monitor the instance without actually making the request "
        lo_ec2->monitorinstances(
          it_instanceids = lt_instance_ids
          iv_dryrun = abap_true
        ).
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        " If the error code returned is `DryRunOperation`, it means we have the required permissions to monitor this instance "
        IF lo_exception->av_err_code = 'DryRunOperation'.
          MESSAGE 'Dry run to enable detailed monitoring completed' TYPE 'I'.
          " DryRun is set to false to enable detailed monitoring "
          lo_ec2->monitorinstances(
            it_instanceids = lt_instance_ids
            iv_dryrun = abap_false
          ).
          MESSAGE 'Detailed monitoring enabled' TYPE 'I'.
          " If the error code returned is `UnauthorizedOperation`, it means we do not have the required permissions to monitor this instance "
        ELSEIF lo_exception->av_err_code = 'UnauthorizedOperation'.
          MESSAGE 'Dry run to enable detailed monitoring failed: User does not have the permissions to monitor the instance' TYPE 'E'.
        ELSE.
          DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
    ENDTRY.
    " snippet-end:[ec2.abapv1.monitor_instance]
  ENDMETHOD.


  METHOD reboot_instance.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.reboot_instance]
    DATA lt_instance_ids TYPE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist.
    APPEND NEW /aws1/cl_ec2instidstringlist_w( iv_value = iv_instance_id ) TO lt_instance_ids.

    "Perform Dry Run"
    TRY.
        " DryRun is set to true to check if the we have the required permissions to reboot the instance without actually making the request "
        lo_ec2->rebootinstances(
          it_instanceids = lt_instance_ids
          iv_dryrun = abap_true
        ).
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        " If the error code returned is `DryRunOperation`, it means we have the required permissions to reboot this instance "
        IF lo_exception->av_err_code = 'DryRunOperation'.
          MESSAGE 'Dry run to reboot instance completed' TYPE 'I'.
          " DryRun is set to false to make a reboot request "
          lo_ec2->rebootinstances(
             it_instanceids = lt_instance_ids
             iv_dryrun = abap_false
           ).
          MESSAGE 'Instance rebooted' TYPE 'I'.
          " If the error code returned is `UnauthorizedOperation`, it means we do not have the required permissions to reboot this instance "
        ELSEIF lo_exception->av_err_code = 'UnauthorizedOperation'.
          MESSAGE 'Dry run to reboot instance failed: User does not have the permission to reboot the instance' TYPE 'E'.
        ELSE.
          DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
    ENDTRY.
    " snippet-end:[ec2.abapv1.reboot_instance]
  ENDMETHOD.


  METHOD release_address.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.release_address]
    TRY.
        lo_ec2->releaseaddress( iv_allocationid = iv_allocation_id ).
        MESSAGE 'Elastic IP address released' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.release_address]
  ENDMETHOD.


  METHOD START_INSTANCE.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.start_instance]

    DATA lt_instance_ids TYPE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist.
    APPEND NEW /aws1/cl_ec2instidstringlist_w( iv_value = iv_instance_id ) TO lt_instance_ids.

    "Perform Dry Run"
    TRY.
        " DryRun is set to true to check if the we have the required permissions to start the instance without actually making the request "
        lo_ec2->startinstances(
          it_instanceids = lt_instance_ids
          iv_dryrun = abap_true
        ).
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        " If the error code returned is `DryRunOperation`, it means we have the required permissions to start this instance "
        IF lo_exception->av_err_code = 'DryRunOperation'.
          MESSAGE 'Dry run to start instance completed' TYPE 'I'.
          " DryRun is set to false to start instance "
          oo_result = lo_ec2->startinstances(           " oo_result is returned for testing purpose "
            it_instanceids = lt_instance_ids
            iv_dryrun = abap_false
          ).
          MESSAGE 'Successfully started the EC2 instance' TYPE 'I'.
          " If the error code returned is `UnauthorizedOperation`, it means we do not have the required permissions to start this instance "
        ELSEIF lo_exception->av_err_code = 'UnauthorizedOperation'.
          MESSAGE 'Dry run to start instance failed: User does not have the permissions to start the instance' TYPE 'E'.
        ELSE.
          DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
    ENDTRY.
    " snippet-end:[ec2.abapv1.start_instance]
  ENDMETHOD.


  METHOD stop_instance.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.stop_instance]

    DATA lt_instance_ids TYPE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist.
    APPEND NEW /aws1/cl_ec2instidstringlist_w( iv_value = iv_instance_id ) TO lt_instance_ids.

    "Perform Dry Run"
    TRY.
        " DryRun is set to true to check if the we have the required permissions to stop the instance without actually making the request "
        lo_ec2->stopinstances(
          it_instanceids = lt_instance_ids
          iv_dryrun = abap_true
        ).
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        " If the error code returned is `DryRunOperation`, it means we have the required permissions to stop this instance "
        IF lo_exception->av_err_code = 'DryRunOperation'.
          MESSAGE 'Dry run to stop instance completed' TYPE 'I'.
          " DryRun is set to false to stop instance "
          oo_result = lo_ec2->stopinstances(           " oo_result is returned for testing purpose "
            it_instanceids = lt_instance_ids
            iv_dryrun = abap_false
          ).
          MESSAGE 'Successfully stopped the EC2 instance' TYPE 'I'.
          " If the error code returned is `UnauthorizedOperation`, it means we do not have the required permissions to stop this instance "
        ELSEIF lo_exception->av_err_code = 'UnauthorizedOperation'.
          MESSAGE 'Dry run to stop instance failed: User does not have the permissions to stop the instance' TYPE 'E'.
        ELSE.
          DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
    ENDTRY.
    " snippet-end:[ec2.abapv1.stop_instance]
  ENDMETHOD.
ENDCLASS.
