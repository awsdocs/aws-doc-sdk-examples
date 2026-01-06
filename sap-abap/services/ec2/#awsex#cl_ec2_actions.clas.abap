" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_ec2_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
  PROTECTED SECTION.
  PRIVATE SECTION.

    METHODS allocate_address
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2allocateaddressrs
      RAISING
        /aws1/cx_rt_generic .
    METHODS create_instance
      IMPORTING
        !iv_ami_id       TYPE /aws1/ec2imageid
        !iv_tag_value    TYPE /aws1/ec2string
        !iv_subnet_id    TYPE /aws1/ec2subnetid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2reservation
      RAISING
        /aws1/cx_rt_generic .
    METHODS create_key_pair
      IMPORTING
        !iv_key_name     TYPE /aws1/ec2string
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2keypair
      RAISING
        /aws1/cx_rt_generic .
    METHODS create_security_group
      IMPORTING
        !iv_security_group_name TYPE /aws1/ec2string
        !iv_vpc_id              TYPE /aws1/ec2vpcid
      RETURNING
        VALUE(oo_result)        TYPE REF TO /aws1/cl_ec2createsecgrouprslt
      RAISING
        /aws1/cx_rt_generic .
    METHODS delete_security_group
      IMPORTING
        !iv_security_group_id TYPE /aws1/ec2securitygroupid
      RAISING
        /aws1/cx_rt_generic .
    METHODS delete_key_pair
      IMPORTING
        !iv_key_name TYPE /aws1/ec2keypairname
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_addresses
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2descraddressesrslt
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_instances
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2descrinstsresult
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_key_pairs
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2descrkeypairsrslt
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_regions
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2descrregionsresult
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_security_groups
      IMPORTING
        !iv_group_id     TYPE /aws1/ec2securitygroupid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2descrsecgroupsrslt
      RAISING
        /aws1/cx_rt_generic .
    METHODS monitor_instance
      IMPORTING
        !iv_instance_id TYPE /aws1/ec2instanceid
      RAISING
        /aws1/cx_rt_generic .
    METHODS reboot_instance
      IMPORTING
        !iv_instance_id TYPE /aws1/ec2instanceid
      RAISING
        /aws1/cx_rt_generic .
    METHODS release_address
      IMPORTING
        !iv_allocation_id TYPE /aws1/ec2allocationid
      RAISING
        /aws1/cx_rt_generic .
    METHODS start_instance
      IMPORTING
        !iv_instance_id  TYPE /aws1/ec2instanceid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2startinstsresult
      RAISING
        /aws1/cx_rt_generic .
    METHODS stop_instance
      IMPORTING
        !iv_instance_id  TYPE /aws1/ec2instanceid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2stopinstsresult
      RAISING
        /aws1/cx_rt_generic .
    METHODS associate_address
      IMPORTING
        !iv_instance_id   TYPE /aws1/ec2instanceid
        !iv_allocation_id TYPE /aws1/ec2allocationid
      RETURNING
        VALUE(oo_result)  TYPE REF TO /aws1/cl_ec2assocaddressresult
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_availability_zones
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2describeazsresult
      RAISING
        /aws1/cx_rt_generic .
    METHODS disassociate_address
      IMPORTING
        !iv_association_id TYPE /aws1/ec2elasticipassociatio00
      RAISING
        /aws1/cx_rt_generic .
    METHODS authorize_sec_group_ingress
      IMPORTING
        !iv_group_id    TYPE /aws1/ec2securitygroupid
        !iv_cidr_ip     TYPE /aws1/ec2string
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2authsecgrpingrslt
      RAISING
        /aws1/cx_rt_generic .
    METHODS terminate_instances
      IMPORTING
        !it_instance_ids TYPE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2terminateinstsrslt
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_images
      IMPORTING
        !it_image_ids    TYPE /aws1/cl_ec2imageidstrlist_w=>tt_imageidstringlist
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2descrimagesresult
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_instance_types
      IMPORTING
        !iv_architecture TYPE /aws1/ec2string DEFAULT 'x86_64'
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2descrinsttypesrslt
      RAISING
        /aws1/cx_rt_generic .
    METHODS create_vpc
      IMPORTING
        !iv_cidr_block   TYPE /aws1/ec2string
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2createvpcresult
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_route_tables
      IMPORTING
        !iv_vpc_id       TYPE /aws1/ec2vpcid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_ec2descrroutetblsrslt
      RAISING
        /aws1/cx_rt_generic .
    METHODS create_vpc_endpoint
      IMPORTING
        !iv_vpc_id            TYPE /aws1/ec2vpcid
        !iv_service_name      TYPE /aws1/ec2string
        !it_route_table_ids   TYPE /aws1/cl_ec2vpcendptroutetbl00=>tt_vpcendpointroutetableidlist
      RETURNING
        VALUE(oo_result)      TYPE REF TO /aws1/cl_ec2createvpcendptrslt
      RAISING
        /aws1/cx_rt_generic .
    METHODS delete_vpc_endpoints
      IMPORTING
        !it_vpc_endpoint_ids TYPE /aws1/cl_ec2vpcendptidlist_w=>tt_vpcendpointidlist
      RAISING
        /aws1/cx_rt_generic .
    METHODS delete_vpc
      IMPORTING
        !iv_vpc_id TYPE /aws1/ec2vpcid
      RAISING
        /aws1/cx_rt_generic .
ENDCLASS.



CLASS /AWSEX/CL_EC2_ACTIONS IMPLEMENTATION.


  METHOD allocate_address.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.allocate_address]
    TRY.
        oo_result = lo_ec2->allocateaddress( iv_domain = 'vpc' ).   " oo_result is returned for testing purposes. "
        MESSAGE 'Allocated an Elastic IP address.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.allocate_address]
  ENDMETHOD.


  METHOD associate_address.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.associate_address]
    TRY.
        oo_result = lo_ec2->associateaddress(                         " oo_result is returned for testing purposes. "
            iv_allocationid = iv_allocation_id
            iv_instanceid = iv_instance_id ).
        MESSAGE 'Associated an Elastic IP address with an EC2 instance.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.associate_address]
  ENDMETHOD.


  METHOD create_instance.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.create_instance]

    " Create tags for resource created during instance launch. "
    DATA lt_tagspecifications TYPE /aws1/cl_ec2tagspecification=>tt_tagspecificationlist.
    DATA ls_tagspecifications LIKE LINE OF lt_tagspecifications.
    ls_tagspecifications = NEW /aws1/cl_ec2tagspecification(
      iv_resourcetype = 'instance'
      it_tags = VALUE /aws1/cl_ec2tag=>tt_taglist(
        ( NEW /aws1/cl_ec2tag( iv_key = 'Name' iv_value = iv_tag_value ) )
      ) ).
    APPEND ls_tagspecifications TO lt_tagspecifications.

    TRY.
        " Create/launch Amazon Elastic Compute Cloud (Amazon EC2) instance. "
        oo_result = lo_ec2->runinstances(                           " oo_result is returned for testing purposes. "
          iv_imageid = iv_ami_id
          iv_instancetype = 't3.micro'
          iv_maxcount = 1
          iv_mincount = 1
          it_tagspecifications = lt_tagspecifications
          iv_subnetid = iv_subnet_id ).
        MESSAGE 'EC2 instance created.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.create_instance]
  ENDMETHOD.


  METHOD create_key_pair.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.create_key_pair]
    TRY.
        oo_result = lo_ec2->createkeypair( iv_keyname = iv_key_name ).                            " oo_result is returned for testing purposes. "
        MESSAGE 'Amazon EC2 key pair created.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.create_key_pair]
  ENDMETHOD.


  METHOD create_security_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.create_security_group]
    TRY.
        oo_result = lo_ec2->createsecuritygroup(                 " oo_result is returned for testing purposes. "
          iv_description = 'Security group example'
          iv_groupname = iv_security_group_name
          iv_vpcid = iv_vpc_id ).
        MESSAGE 'Security group created.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.create_security_group]
  ENDMETHOD.


  METHOD delete_key_pair.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.delete_key_pair]
    TRY.
        lo_ec2->deletekeypair( iv_keyname = iv_key_name ).
        MESSAGE 'Amazon EC2 key pair deleted.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.delete_key_pair]
  ENDMETHOD.


  METHOD delete_security_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.delete_security_group]
    TRY.
        lo_ec2->deletesecuritygroup( iv_groupid = iv_security_group_id ).
        MESSAGE 'Security group deleted.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.delete_security_group]
  ENDMETHOD.


  METHOD describe_addresses.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_addresses]
    TRY.
        oo_result = lo_ec2->describeaddresses( ).                        " oo_result is returned for testing purposes. "
        DATA(lt_addresses) = oo_result->get_addresses( ).
        MESSAGE 'Retrieved information about Elastic IP addresses.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_addresses]
  ENDMETHOD.


  METHOD describe_availability_zones.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_availability_zones]
    TRY.
        oo_result = lo_ec2->describeavailabilityzones( ).                        " oo_result is returned for testing purposes. "
        DATA(lt_zones) = oo_result->get_availabilityzones( ).
        MESSAGE 'Retrieved information about Availability Zones.' TYPE 'I'.

      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.

    " snippet-end:[ec2.abapv1.describe_availability_zones]
  ENDMETHOD.


  METHOD describe_instances.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_instances]
    TRY.
        oo_result = lo_ec2->describeinstances( ).                        " oo_result is returned for testing purposes. "

        " Retrieving details of EC2 instances. "
        DATA: lv_istance_id    TYPE /aws1/ec2string,
              lv_status        TYPE /aws1/ec2instancestatename,
              lv_instance_type TYPE /aws1/ec2instancetype,
              lv_image_id      TYPE /aws1/ec2string.
        LOOP AT oo_result->get_reservations( ) INTO DATA(lo_reservation).
          LOOP AT lo_reservation->get_instances( ) INTO DATA(lo_instance).
            lv_istance_id = lo_instance->get_instanceid( ).
            lv_status = lo_instance->get_state( )->get_name( ).
            lv_instance_type = lo_instance->get_instancetype( ).
            lv_image_id = lo_instance->get_imageid( ).
          ENDLOOP.
        ENDLOOP.
        MESSAGE 'Retrieved information about EC2 instances.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_instances]
  ENDMETHOD.


  METHOD describe_key_pairs.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_key_pairs]
    TRY.
        oo_result = lo_ec2->describekeypairs( ).                        " oo_result is returned for testing purposes. "
        DATA(lt_key_pairs) = oo_result->get_keypairs( ).
        MESSAGE 'Retrieved information about key pairs.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_key_pairs]
  ENDMETHOD.


  METHOD describe_regions.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_regions]
    TRY.
        oo_result = lo_ec2->describeregions( ).                        " oo_result is returned for testing purposes. "
        DATA(lt_regions) = oo_result->get_regions( ).
        MESSAGE 'Retrieved information about Regions.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.

    " snippet-end:[ec2.abapv1.describe_regions]
  ENDMETHOD.


  METHOD describe_security_groups.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_security_groups]
    TRY.
        DATA lt_group_ids TYPE /aws1/cl_ec2groupidstrlist_w=>tt_groupidstringlist.
        APPEND NEW /aws1/cl_ec2groupidstrlist_w( iv_value = iv_group_id ) TO lt_group_ids.
        oo_result = lo_ec2->describesecuritygroups( it_groupids = lt_group_ids ).         " oo_result is returned for testing purposes. "
        DATA(lt_security_groups) = oo_result->get_securitygroups( ).
        MESSAGE 'Retrieved information about security groups.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_security_groups]
  ENDMETHOD.


  METHOD monitor_instance.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.monitor_instance]

    DATA lt_instance_ids TYPE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist.
    APPEND NEW /aws1/cl_ec2instidstringlist_w( iv_value = iv_instance_id ) TO lt_instance_ids.

    "Perform dry run"
    TRY.
        " DryRun is set to true. This checks for the required permissions to monitor the instance without actually making the request. "
        lo_ec2->monitorinstances(
          it_instanceids = lt_instance_ids
          iv_dryrun = abap_true ).
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        " If the error code returned is `DryRunOperation`, then you have the required permissions to monitor this instance. "
        IF lo_exception->av_err_code = 'DryRunOperation'.
          MESSAGE 'Dry run to enable detailed monitoring completed.' TYPE 'I'.
          " DryRun is set to false to enable detailed monitoring. "
          lo_ec2->monitorinstances(
            it_instanceids = lt_instance_ids
            iv_dryrun = abap_false ).
          MESSAGE 'Detailed monitoring enabled.' TYPE 'I'.
          " If the error code returned is `UnauthorizedOperation`, then you don't have the required permissions to monitor this instance. "
        ELSEIF lo_exception->av_err_code = 'UnauthorizedOperation'.
          MESSAGE 'Dry run to enable detailed monitoring failed. User does not have the permissions to monitor the instance.' TYPE 'E'.
        ELSE.
          DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
    ENDTRY.
    " snippet-end:[ec2.abapv1.monitor_instance]
  ENDMETHOD.


  METHOD reboot_instance.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.reboot_instance]
    DATA lt_instance_ids TYPE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist.
    APPEND NEW /aws1/cl_ec2instidstringlist_w( iv_value = iv_instance_id ) TO lt_instance_ids.

    "Perform dry run"
    TRY.
        " DryRun is set to true. This checks for the required permissions to reboot the instance without actually making the request. "
        lo_ec2->rebootinstances(
          it_instanceids = lt_instance_ids
          iv_dryrun = abap_true ).
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        " If the error code returned is `DryRunOperation`, then you have the required permissions to reboot this instance. "
        IF lo_exception->av_err_code = 'DryRunOperation'.
          MESSAGE 'Dry run to reboot instance completed.' TYPE 'I'.
          " DryRun is set to false to make a reboot request. "
          lo_ec2->rebootinstances(
             it_instanceids = lt_instance_ids
             iv_dryrun = abap_false ).
          MESSAGE 'Instance rebooted.' TYPE 'I'.
          " If the error code returned is `UnauthorizedOperation`, then you don't have the required permissions to reboot this instance. "
        ELSEIF lo_exception->av_err_code = 'UnauthorizedOperation'.
          MESSAGE 'Dry run to reboot instance failed. User does not have permissions to reboot the instance.' TYPE 'E'.
        ELSE.
          DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
    ENDTRY.
    " snippet-end:[ec2.abapv1.reboot_instance]
  ENDMETHOD.


  METHOD release_address.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.release_address]
    TRY.
        lo_ec2->releaseaddress( iv_allocationid = iv_allocation_id ).
        MESSAGE 'Elastic IP address released.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.release_address]
  ENDMETHOD.


  METHOD start_instance.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.start_instance]

    DATA lt_instance_ids TYPE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist.
    APPEND NEW /aws1/cl_ec2instidstringlist_w( iv_value = iv_instance_id ) TO lt_instance_ids.

    "Perform dry run"
    TRY.
        " DryRun is set to true. This checks for the required permissions to start the instance without actually making the request. "
        lo_ec2->startinstances(
          it_instanceids = lt_instance_ids
          iv_dryrun = abap_true ).
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        " If the error code returned is `DryRunOperation`, then you have the required permissions to start this instance. "
        IF lo_exception->av_err_code = 'DryRunOperation'.
          MESSAGE 'Dry run to start instance completed.' TYPE 'I'.
          " DryRun is set to false to start instance. "
          oo_result = lo_ec2->startinstances(           " oo_result is returned for testing purposes. "
            it_instanceids = lt_instance_ids
            iv_dryrun = abap_false ).
          MESSAGE 'Successfully started the EC2 instance.' TYPE 'I'.
          " If the error code returned is `UnauthorizedOperation`, then you don't have the required permissions to start this instance. "
        ELSEIF lo_exception->av_err_code = 'UnauthorizedOperation'.
          MESSAGE 'Dry run to start instance failed. User does not have permissions to start the instance.' TYPE 'E'.
        ELSE.
          DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
    ENDTRY.
    " snippet-end:[ec2.abapv1.start_instance]
  ENDMETHOD.


  METHOD stop_instance.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.stop_instance]

    DATA lt_instance_ids TYPE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist.
    APPEND NEW /aws1/cl_ec2instidstringlist_w( iv_value = iv_instance_id ) TO lt_instance_ids.

    "Perform dry run"
    TRY.
        " DryRun is set to true. This checks for the required permissions to stop the instance without actually making the request. "
        lo_ec2->stopinstances(
          it_instanceids = lt_instance_ids
          iv_dryrun = abap_true ).
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        " If the error code returned is `DryRunOperation`, then you have the required permissions to stop this instance. "
        IF lo_exception->av_err_code = 'DryRunOperation'.
          MESSAGE 'Dry run to stop instance completed.' TYPE 'I'.
          " DryRun is set to false to stop instance. "
          oo_result = lo_ec2->stopinstances(           " oo_result is returned for testing purposes. "
            it_instanceids = lt_instance_ids
            iv_dryrun = abap_false ).
          MESSAGE 'Successfully stopped the EC2 instance.' TYPE 'I'.
          " If the error code returned is `UnauthorizedOperation`, then you don't have the required permissions to stop this instance. "
        ELSEIF lo_exception->av_err_code = 'UnauthorizedOperation'.
          MESSAGE 'Dry run to stop instance failed. User does not have permissions to stop the instance.' TYPE 'E'.
        ELSE.
          DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
    ENDTRY.
    " snippet-end:[ec2.abapv1.stop_instance]
  ENDMETHOD.


  METHOD disassociate_address.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.disassociate_address]
    TRY.
        lo_ec2->disassociateaddress( iv_associationid = iv_association_id ).
        MESSAGE 'Disassociated Elastic IP address from EC2 instance.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.disassociate_address]
  ENDMETHOD.


  METHOD authorize_sec_group_ingress.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.authorize_sec_group_ingress]
    " Create IP permissions for SSH access (port 22)
    " iv_cidr_ip = '192.0.2.0/24'
    DATA lt_ip_permissions TYPE /aws1/cl_ec2ippermission=>tt_ippermissionlist.
    DATA(lo_ip_permission) = NEW /aws1/cl_ec2ippermission(
      iv_ipprotocol = 'tcp'
      iv_fromport = 22
      iv_toport = 22
      it_ipranges = VALUE /aws1/cl_ec2iprange=>tt_iprangelist(
        ( NEW /aws1/cl_ec2iprange( iv_cidrip = iv_cidr_ip ) )
      )
    ).
    APPEND lo_ip_permission TO lt_ip_permissions.

    TRY.
        oo_result = lo_ec2->authsecuritygroupingress(             " oo_result is returned for testing purposes. "
          iv_groupid = iv_group_id
          it_ippermissions = lt_ip_permissions ).
        MESSAGE 'Authorized ingress rule for security group.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.authorize_sec_group_ingress]
  ENDMETHOD.


  METHOD terminate_instances.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.terminate_instances]
    TRY.
        oo_result = lo_ec2->terminateinstances00( it_instanceids = it_instance_ids ).             " oo_result is returned for testing purposes. "
        MESSAGE 'Terminated EC2 instance(s).' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.terminate_instances]
  ENDMETHOD.


  METHOD describe_images.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_images]
    TRY.
        oo_result = lo_ec2->describeimages( it_imageids = it_image_ids ).             " oo_result is returned for testing purposes. "
        DATA(lt_images) = oo_result->get_images( ).
        MESSAGE 'Retrieved information about Amazon Machine Images (AMIs).' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_images]
  ENDMETHOD.


  METHOD describe_instance_types.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_instance_types]
    " Create filters for architecture and instance type patterns
    " iv_architecture = 'x86_64'
    DATA lt_filters TYPE /aws1/cl_ec2filter=>tt_filterlist.
    APPEND NEW /aws1/cl_ec2filter(
      iv_name = 'processor-info.supported-architecture'
      it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
        ( NEW /aws1/cl_ec2valuestringlist_w( iv_architecture ) )
      )
    ) TO lt_filters.
    " Filter for instance type patterns like '*.micro', '*.small'
    APPEND NEW /aws1/cl_ec2filter(
      iv_name = 'instance-type'
      it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
        ( NEW /aws1/cl_ec2valuestringlist_w( '*.micro' ) )
        ( NEW /aws1/cl_ec2valuestringlist_w( '*.small' ) )
      )
    ) TO lt_filters.

    TRY.
        oo_result = lo_ec2->describeinstancetypes( it_filters = lt_filters ).             " oo_result is returned for testing purposes. "
        DATA(lt_instance_types) = oo_result->get_instancetypes( ).
        MESSAGE 'Retrieved information about EC2 instance types.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_instance_types]
  ENDMETHOD.


  METHOD create_vpc.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.create_vpc]
    " iv_cidr_block = '10.0.0.0/16'
    TRY.
        oo_result = lo_ec2->createvpc( iv_cidrblock = iv_cidr_block ).             " oo_result is returned for testing purposes. "
        DATA(lv_vpc_id) = oo_result->get_vpc( )->get_vpcid( ).
        MESSAGE 'Created VPC.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.create_vpc]
  ENDMETHOD.


  METHOD describe_route_tables.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.describe_route_tables]
    " Create filter for VPC ID
    " iv_vpc_id = 'vpc-abc123'
    DATA lt_filters TYPE /aws1/cl_ec2filter=>tt_filterlist.
    APPEND NEW /aws1/cl_ec2filter(
      iv_name = 'vpc-id'
      it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
        ( NEW /aws1/cl_ec2valuestringlist_w( iv_vpc_id ) )
      )
    ) TO lt_filters.

    TRY.
        oo_result = lo_ec2->describeroutetables( it_filters = lt_filters ).             " oo_result is returned for testing purposes. "
        DATA(lt_route_tables) = oo_result->get_routetables( ).
        MESSAGE 'Retrieved information about route tables.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.describe_route_tables]
  ENDMETHOD.


  METHOD create_vpc_endpoint.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.create_vpc_endpoint]
    " iv_vpc_id = 'vpc-abc123'
    " iv_service_name = 'com.amazonaws.region.service'
    TRY.
        oo_result = lo_ec2->createvpcendpoint(             " oo_result is returned for testing purposes. "
          iv_vpcid = iv_vpc_id
          iv_servicename = iv_service_name
          it_routetableids = it_route_table_ids ).
        DATA(lv_vpc_endpoint_id) = oo_result->get_vpcendpoint( )->get_vpcendpointid( ).
        MESSAGE 'Created VPC endpoint.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.create_vpc_endpoint]
  ENDMETHOD.


  METHOD delete_vpc_endpoints.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.delete_vpc_endpoints]
    TRY.
        lo_ec2->deletevpcendpoints( it_vpcendpointids = it_vpc_endpoint_ids ).
        MESSAGE 'Deleted VPC endpoint(s).' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.delete_vpc_endpoints]
  ENDMETHOD.


  METHOD delete_vpc.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ec2) = /aws1/cl_ec2_factory=>create( lo_session ).

    " snippet-start:[ec2.abapv1.delete_vpc]
    TRY.
        lo_ec2->deletevpc( iv_vpcid = iv_vpc_id ).
        MESSAGE 'Deleted VPC.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ec2.abapv1.delete_vpc]
  ENDMETHOD.
ENDCLASS.
