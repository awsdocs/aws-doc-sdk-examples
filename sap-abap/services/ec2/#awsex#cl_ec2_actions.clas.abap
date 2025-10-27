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
ENDCLASS.
