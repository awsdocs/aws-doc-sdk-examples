" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_ec2_actions DEFINITION DEFERRED.
CLASS zcl_aws1_ec2_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_ec2_actions.

CLASS ltc_zcl_aws1_ec2_actions DEFINITION FOR TESTING  DURATION LONG RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_ec2 TYPE REF TO /aws1/if_ec2.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_ec2_actions TYPE REF TO zcl_aws1_ec2_actions.

    METHODS: allocate_address FOR TESTING RAISING /aws1/cx_rt_generic,
      associate_address FOR TESTING RAISING /aws1/cx_rt_generic,
      create_instance FOR TESTING RAISING /aws1/cx_rt_generic,
      create_key_pair FOR TESTING RAISING /aws1/cx_rt_generic,
      create_security_group FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_security_group FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_key_pair FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_addresses FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_instances FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_key_pairs FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_regions FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_availability_zones FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_security_groups FOR TESTING RAISING /aws1/cx_rt_generic,
      monitor_instance FOR TESTING RAISING /aws1/cx_rt_generic,
      reboot_instance FOR TESTING RAISING /aws1/cx_rt_generic,
      release_address FOR TESTING RAISING /aws1/cx_rt_generic,
      start_instances FOR TESTING RAISING /aws1/cx_rt_generic,
      stop_instances FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.

    METHODS:
      get_ami_id
        RETURNING VALUE(ov_ami_id) TYPE /aws1/ec2string
        RAISING   /aws1/cx_rt_generic,
      wait_until_status_change
        IMPORTING iv_required_status       TYPE string
                  iv_instance_id           TYPE string
        RETURNING VALUE(ov_current_status) TYPE string
        RAISING   /aws1/cx_rt_service_generic,
      run_instance
        IMPORTING iv_subnet_id          TYPE /aws1/ec2subnetid
        RETURNING VALUE(ov_instance_id) TYPE /aws1/ec2string
        RAISING   /aws1/cx_rt_service_generic,
      terminate_instance
        IMPORTING iv_instance_id TYPE /aws1/ec2string
        RAISING   /aws1/cx_rt_service_generic.

ENDCLASS.

CLASS ltc_zcl_aws1_ec2_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_ec2 = /aws1/cl_ec2_factory=>create( ao_session ).
    ao_ec2_actions = NEW zcl_aws1_ec2_actions( ).

  ENDMETHOD.
  METHOD allocate_address.
    DATA(lo_result) = ao_ec2_actions->allocate_address( ).

    cl_abap_unit_assert=>assert_not_initial(
          act = lo_result->get_allocationid( )
          msg = |Failed to allocate an Elastic IP address|
        ).

    ao_ec2->releaseaddress( iv_allocationid = lo_result->get_allocationid( ) ).

  ENDMETHOD.
  METHOD associate_address.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.10.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lv_subnet_id) = ao_ec2->createsubnet( iv_vpcid = lv_vpc_id iv_cidrblock = '10.10.0.0/24' )->get_subnet( )->get_subnetid( ).
    DATA(lv_internet_gateway_id) = ao_ec2->createinternetgateway( )->get_internetgateway( )->get_internetgatewayid( ).
    ao_ec2->attachinternetgateway( iv_internetgatewayid = lv_internet_gateway_id iv_vpcid = lv_vpc_id ).
    DATA(lv_instance_id) = run_instance( iv_subnet_id = lv_subnet_id ).
    wait_until_status_change( iv_instance_id = lv_instance_id iv_required_status = 'running' ).
    DATA(lv_allocation_id) = ao_ec2->allocateaddress( iv_domain = 'vpc' )->get_allocationid( ).

    DATA(lo_result) = ao_ec2_actions->associate_address(
        iv_instance_id = lv_instance_id
        iv_allocation_id = lv_allocation_id
    ).

    cl_abap_unit_assert=>assert_not_initial(
          act = lo_result->get_associationid( )
          msg = |Failed to associate Elastic IP address with EC2 instancce|
        ).

    ao_ec2->disassociateaddress( iv_associationid = lo_result->get_associationid( ) ).
    ao_ec2->releaseaddress( iv_allocationid = lv_allocation_id ).
    terminate_instance( iv_instance_id = lv_instance_id ).
    ao_ec2->detachinternetgateway( iv_internetgatewayid = lv_internet_gateway_id iv_vpcid = lv_vpc_id ).
    ao_ec2->deleteinternetgateway( iv_internetgatewayid = lv_internet_gateway_id ).
    ao_ec2->deletesubnet( iv_subnetid = lv_subnet_id ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD describe_addresses.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.11.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lv_subnet_id) = ao_ec2->createsubnet( iv_vpcid = lv_vpc_id iv_cidrblock = '10.11.0.0/24' )->get_subnet( )->get_subnetid( ).
    DATA(lv_internet_gateway_id) = ao_ec2->createinternetgateway( )->get_internetgateway( )->get_internetgatewayid( ).
    ao_ec2->attachinternetgateway( iv_internetgatewayid = lv_internet_gateway_id iv_vpcid = lv_vpc_id ).
    DATA(lv_instance_id) = run_instance( iv_subnet_id = lv_subnet_id ).
    wait_until_status_change( iv_instance_id = lv_instance_id iv_required_status = 'running' ).

    DATA(lo_allocate_result) = ao_ec2->allocateaddress( iv_domain = 'vpc' ).
    DATA(lo_associate_result) = ao_ec2->associateaddress( iv_allocationid = lo_allocate_result->get_allocationid( ) iv_instanceid = lv_instance_id ).

    DATA(lo_describe_result) = ao_ec2_actions->describe_addresses( ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_describe_result->get_addresses( ) INTO DATA(lo_address).
      IF lo_address->get_instanceid( ) = lv_instance_id AND lo_address->get_publicip( ) = lo_allocate_result->get_publicip( ).
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Elastic IP address associated with EC2 instance should have been included in the address list|
    ).

    ao_ec2->disassociateaddress( iv_associationid = lo_associate_result->get_associationid( ) ).
    ao_ec2->releaseaddress( iv_allocationid = lo_allocate_result->get_allocationid( ) ).
    terminate_instance( iv_instance_id = lv_instance_id ).
    ao_ec2->detachinternetgateway( iv_internetgatewayid = lv_internet_gateway_id iv_vpcid = lv_vpc_id ).
    ao_ec2->deleteinternetgateway( iv_internetgatewayid = lv_internet_gateway_id ).
    ao_ec2->deletesubnet( iv_subnetid = lv_subnet_id ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD release_address.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.12.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lv_subnet_id) = ao_ec2->createsubnet( iv_vpcid = lv_vpc_id iv_cidrblock = '10.12.0.0/24' )->get_subnet( )->get_subnetid( ).
    DATA(lv_internet_gateway_id) = ao_ec2->createinternetgateway( )->get_internetgateway( )->get_internetgatewayid( ).
    ao_ec2->attachinternetgateway( iv_internetgatewayid = lv_internet_gateway_id iv_vpcid = lv_vpc_id ).
    DATA(lv_instance_id) = run_instance( iv_subnet_id = lv_subnet_id ).
    wait_until_status_change( iv_instance_id = lv_instance_id iv_required_status = 'running' ).

    DATA(lo_allocate_result) = ao_ec2->allocateaddress( iv_domain = 'vpc' ).
    DATA(lo_associate_result) = ao_ec2->associateaddress( iv_allocationid = lo_allocate_result->get_allocationid( ) iv_instanceid = lv_instance_id ).

    ao_ec2->disassociateaddress( iv_associationid = lo_associate_result->get_associationid( ) ).
    ao_ec2_actions->release_address( iv_allocation_id = lo_allocate_result->get_allocationid( ) ).

    DATA(lo_describe_result) = ao_ec2_actions->describe_addresses( ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_describe_result->get_addresses( ) INTO DATA(lo_address).
      IF lo_address->get_publicip( ) = lo_allocate_result->get_publicip( ).
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Elastic IP address should have been released|
    ).

    terminate_instance( iv_instance_id = lv_instance_id ).
    ao_ec2->detachinternetgateway( iv_internetgatewayid = lv_internet_gateway_id iv_vpcid = lv_vpc_id ).
    ao_ec2->deleteinternetgateway( iv_internetgatewayid = lv_internet_gateway_id ).
    ao_ec2->deletesubnet( iv_subnetid = lv_subnet_id ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD create_instance.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.4.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lv_subnet_id) = ao_ec2->createsubnet( iv_vpcid = lv_vpc_id iv_cidrblock = '10.4.0.0/24' )->get_subnet( )->get_subnetid( ).

    DATA(lo_create_result) = ao_ec2_actions->create_instance(
        iv_ami_id = get_ami_id( )
        iv_tag_value = 'code-example-create-instance'
        iv_subnet_id = lv_subnet_id
    ).
    READ TABLE lo_create_result->get_instances( ) INTO DATA(lo_instance) INDEX 1.
    DATA(lv_current_status) =  wait_until_status_change( iv_instance_id = lo_instance->get_instanceid( ) iv_required_status = 'running' ).

    cl_abap_unit_assert=>assert_equals(
      act = lv_current_status
      exp = 'running'
      msg = |EC2 instance { lo_instance->get_instanceid( ) } should have been in 'running' state|
    ).

    terminate_instance( iv_instance_id = lo_instance->get_instanceid( ) ).
    ao_ec2->deletesubnet( iv_subnetid = lv_subnet_id ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD monitor_instance.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.6.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lv_subnet_id) = ao_ec2->createsubnet( iv_vpcid = lv_vpc_id iv_cidrblock = '10.6.0.0/24' )->get_subnet( )->get_subnetid( ).
    DATA(lv_instance_id) = run_instance( iv_subnet_id = lv_subnet_id ).
    ao_ec2_actions->monitor_instance( iv_instance_id = lv_instance_id ).
    WAIT UP TO 5 SECONDS.
    DATA(lo_describe_result) = ao_ec2->describeinstances(
     it_instanceids = VALUE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist(
       ( NEW /aws1/cl_ec2instidstringlist_w( lv_instance_id ) )
     )
    ).
    READ TABLE lo_describe_result->get_reservations( ) INTO DATA(lo_reservation) INDEX 1.
    READ TABLE lo_reservation->get_instances( ) INTO DATA(lo_describe_instance) INDEX 1.
    cl_abap_unit_assert=>assert_equals(
          exp = lo_describe_instance->get_monitoring( )->get_state( )
          act = 'enabled'
          msg = |Detailed monitoring should have been enabled|
        ).

    terminate_instance( iv_instance_id = lv_instance_id ).
    ao_ec2->deletesubnet( iv_subnetid = lv_subnet_id ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD reboot_instance.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.7.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lv_subnet_id) = ao_ec2->createsubnet( iv_vpcid = lv_vpc_id iv_cidrblock = '10.7.0.0/24' )->get_subnet( )->get_subnetid( ).
    DATA(lv_instance_id) = run_instance( iv_subnet_id = lv_subnet_id ).
    wait_until_status_change( iv_instance_id = lv_instance_id iv_required_status = 'running' ).
    ao_ec2_actions->reboot_instance( iv_instance_id =  lv_instance_id ).
    DATA(lv_current_status) = wait_until_status_change( iv_instance_id = lv_instance_id iv_required_status = 'running' ).

    cl_abap_unit_assert=>assert_equals(
          exp = lv_current_status
          act = 'running'
          msg = |Failed to reboot the specified instance|
        ).

    terminate_instance( iv_instance_id = lv_instance_id ).
    ao_ec2->deletesubnet( iv_subnetid = lv_subnet_id ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD start_instances.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.8.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lv_subnet_id) = ao_ec2->createsubnet( iv_vpcid = lv_vpc_id iv_cidrblock = '10.8.0.0/24' )->get_subnet( )->get_subnetid( ).
    DATA(lv_instance_id) = run_instance( iv_subnet_id = lv_subnet_id ).
    wait_until_status_change( iv_instance_id = lv_instance_id iv_required_status = 'running' ).

    ao_ec2->stopinstances(
      it_instanceids = VALUE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist(
        ( NEW /aws1/cl_ec2instidstringlist_w( lv_instance_id ) )
      )
    ).
    wait_until_status_change( iv_instance_id = lv_instance_id iv_required_status = 'stopped' ).

    DATA(lo_start_result) = ao_ec2_actions->start_instance( iv_instance_id = lv_instance_id ).
    READ TABLE lo_start_result->get_startinginstances( ) INTO DATA(lo_start_instance) INDEX 1.
    cl_abap_unit_assert=>assert_equals(
          exp = lo_start_instance->get_currentstate( )->get_name( )
          act = 'pending'
          msg = |Instance should have been in 'pending' state when a request is made to start a stopped instance|
        ).

    DATA(lv_current_status) = wait_until_status_change( iv_instance_id = lv_instance_id iv_required_status = 'running' ).
    cl_abap_unit_assert=>assert_equals(
          exp = lv_current_status
          act = 'running'
          msg = |Failed to start a stopped instance|
        ).

    terminate_instance( iv_instance_id = lv_instance_id ).
    ao_ec2->deletesubnet( iv_subnetid = lv_subnet_id ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD stop_instances.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.5.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lv_subnet_id) = ao_ec2->createsubnet( iv_vpcid = lv_vpc_id iv_cidrblock = '10.5.0.0/24' )->get_subnet( )->get_subnetid( ).
    DATA(lv_instance_id) = run_instance( iv_subnet_id = lv_subnet_id ).
    wait_until_status_change( iv_instance_id = lv_instance_id iv_required_status = 'running' ).
    DATA(lo_stop_result) = ao_ec2_actions->stop_instance( iv_instance_id = lv_instance_id ).
    READ TABLE lo_stop_result->get_stoppinginstances( ) INTO DATA(lo_stop_instance) INDEX 1.
    cl_abap_unit_assert=>assert_equals(
          exp = lo_stop_instance->get_currentstate( )->get_name( )
          act = 'stopping'
          msg = |Instance should have been in 'stopping' state when a request is made to stop a running instance|
        ).

    DATA(lv_current_status) = wait_until_status_change( iv_instance_id = lv_instance_id iv_required_status = 'stopped' ).
    cl_abap_unit_assert=>assert_equals(
          exp = lv_current_status
          act = 'stopped'
          msg = |Failed to stop a running instance|
        ).

    terminate_instance( iv_instance_id = lv_instance_id ).
    ao_ec2->deletesubnet( iv_subnetid = lv_subnet_id ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD describe_instances.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.9.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lv_subnet_id) = ao_ec2->createsubnet( iv_vpcid = lv_vpc_id iv_cidrblock = '10.9.0.0/24' )->get_subnet( )->get_subnetid( ).
    DATA(lv_instance_id) = run_instance( iv_subnet_id = lv_subnet_id ).
    DATA(lo_describe_result) = ao_ec2_actions->describe_instances( ).
    READ TABLE lo_describe_result->get_reservations( ) INTO DATA(lo_reservation) INDEX 1.
    cl_abap_unit_assert=>assert_not_initial(
          act = lo_reservation->get_instances( )
          msg = |Instance List should not be empty|
        ).
    terminate_instance( iv_instance_id = lv_instance_id ).
    ao_ec2->deletesubnet( iv_subnetid = lv_subnet_id ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD create_key_pair.
    CONSTANTS cv_key_name TYPE /aws1/ec2string VALUE 'code-example-create-key-pair'.
    DATA(lo_result) = ao_ec2_actions->create_key_pair( iv_key_name =  cv_key_name ).
    cl_abap_unit_assert=>assert_not_initial(
          act = lo_result->get_keypairid( )
          msg = |Failed to create key pair { cv_key_name }|
        ).

    DATA lv_has_details TYPE abap_bool VALUE abap_false.
    IF lo_result->get_keyfingerprint( ) IS NOT INITIAL AND lo_result->get_keymaterial( ) IS NOT INITIAL AND lo_result->get_keyname( ) = cv_key_name.
      lv_has_details = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
      act = lv_has_details
      msg = |The response object for key pair { cv_key_name } does not contain the required elements|
    ).

    ao_ec2->deletekeypair( iv_keyname =  cv_key_name ).
  ENDMETHOD.
  METHOD delete_key_pair.
    CONSTANTS cv_key_name TYPE /aws1/ec2string VALUE 'code-example-delete-key-pair'.
    ao_ec2->createkeypair( iv_keyname =  cv_key_name ).
    ao_ec2_actions->delete_key_pair( iv_key_name =  cv_key_name ).
    DATA(lo_result) = ao_ec2->describekeypairs( ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_result->get_keypairs( ) INTO DATA(lo_key_pair).
      IF lo_key_pair->get_keyname( ) = cv_key_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Key Pair { cv_key_name } should have been deleted|
    ).
  ENDMETHOD.
  METHOD describe_key_pairs.
    CONSTANTS cv_key_name TYPE /aws1/ec2string VALUE 'code-example-describe-key-pairs'.
    ao_ec2->createkeypair( iv_keyname =  cv_key_name ).
    DATA(lo_result) = ao_ec2_actions->describe_key_pairs( ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_result->get_keypairs( ) INTO DATA(lo_key_pair).
      IF lo_key_pair->get_keyname( ) = cv_key_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Key Pair { cv_key_name } should have been included in key pair list|
    ).
    ao_ec2->deletekeypair( iv_keyname =  cv_key_name ).
  ENDMETHOD.
  METHOD describe_regions.
    DATA(lo_result) = ao_ec2_actions->describe_regions( ).
    cl_abap_unit_assert=>assert_not_initial(
          act = lo_result->get_regions( )
          msg = |Failed to retrieve list of regions|
        ).
  ENDMETHOD.
  METHOD describe_availability_zones.
    DATA(lo_result) = ao_ec2_actions->describe_availability_zones( ).
    cl_abap_unit_assert=>assert_not_initial(
          act = lo_result->get_availabilityzones( )
          msg = |Failed to retrieve list of availability zones|
        ).
  ENDMETHOD.
  METHOD create_security_group.
    CONSTANTS cv_security_group_name TYPE /aws1/ec2string VALUE 'code-example-create-security-group'.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.1.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lo_create_result) = ao_ec2_actions->create_security_group( iv_security_group_name =  cv_security_group_name iv_vpc_id = lv_vpc_id ).
    DATA(lo_describe_result) = ao_ec2->describesecuritygroups(
      it_groupids = VALUE /aws1/cl_ec2groupidstrlist_w=>tt_groupidstringlist(
                      ( NEW /aws1/cl_ec2groupidstrlist_w( lo_create_result->get_groupid( ) ) )
                    )
    ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_describe_result->get_securitygroups( ) INTO DATA(lo_security_group).
      IF lo_security_group->get_groupname( ) = cv_security_group_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Failed to create security group { cv_security_group_name }|
    ).

    ao_ec2->deletesecuritygroup( iv_groupid = lo_create_result->get_groupid( ) ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD delete_security_group.
    CONSTANTS cv_security_group_name TYPE /aws1/ec2string VALUE 'code-example-delete-security-group'.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.2.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lo_create_result) = ao_ec2->createsecuritygroup(
        iv_groupname =  cv_security_group_name
        iv_description = |security group for delete_security_group test|
        iv_vpcid = lv_vpc_id
    ).
    ao_ec2_actions->delete_security_group( iv_security_group_id = lo_create_result->get_groupid( ) ).
    DATA(lo_describe_result) = ao_ec2->describesecuritygroups(
      it_filters = VALUE /aws1/cl_ec2filter=>tt_filterlist(
                      ( NEW /aws1/cl_ec2filter(
                        iv_name = 'vpc-id'
                        it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
                          ( NEW /aws1/cl_ec2valuestringlist_w( lv_vpc_id ) )
                        )
                      ) )
                    )
    ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_describe_result->get_securitygroups( ) INTO DATA(lo_security_group).
      IF lo_security_group->get_groupname( ) = cv_security_group_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Security Group { cv_security_group_name } should have been deleted|
    ).

    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD describe_security_groups.
    CONSTANTS cv_security_group_name TYPE /aws1/ec2string VALUE 'code-example-describe-security-groups'.
    DATA(lv_vpc_id) = ao_ec2->createvpc( iv_cidrblock  = '10.3.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lo_create_result) = ao_ec2->createsecuritygroup(
        iv_groupname =  cv_security_group_name
        iv_description = |security group for describe_security_groups test|
        iv_vpcid = lv_vpc_id
    ).

    DATA(lo_describe_result) = ao_ec2_actions->describe_security_groups( iv_group_id = lo_create_result->get_groupid( ) ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_describe_result->get_securitygroups( ) INTO DATA(lo_security_group).
      IF lo_security_group->get_groupname( ) = cv_security_group_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Security Group { cv_security_group_name } should have been included in security group list|
    ).

    ao_ec2->deletesecuritygroup( iv_groupid = lo_create_result->get_groupid( ) ).
    ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
  ENDMETHOD.
  METHOD get_ami_id.
    CONSTANTS: cv_ami_name     TYPE string VALUE 'amzn2-ami-kernel-5.10-hvm*',
               cv_architecture TYPE string VALUE 'x86_64'.
    TYPES: BEGIN OF ty_ami,
             cdate TYPE string,
             image TYPE REF TO /aws1/cl_ec2image,
           END OF ty_ami.
    DATA(lt_images) = ao_ec2->describeimages(
         it_filters = VALUE /aws1/cl_ec2filter=>tt_filterlist(
           ( NEW /aws1/cl_ec2filter(
               iv_name = 'name'
               it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
                 ( NEW /aws1/cl_ec2valuestringlist_w( cv_ami_name )  )
           ) ) )
           ( NEW /aws1/cl_ec2filter(
               iv_name = 'architecture'
               it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
                ( NEW /aws1/cl_ec2valuestringlist_w( cv_architecture )  )
           ) ) )
         )
       )->get_images( ).
    DATA lt_ami TYPE TABLE OF ty_ami.
    LOOP AT lt_images ASSIGNING FIELD-SYMBOL(<image>).
      APPEND VALUE ty_ami( cdate = <image>->get_creationdate( ) image = <image> ) TO lt_ami.
    ENDLOOP.
    SORT lt_ami BY cdate DESCENDING.
    READ TABLE lt_ami INTO DATA(lo_ami) INDEX 1.
    ov_ami_id = lo_ami-image->get_imageid( ).
  ENDMETHOD.
  METHOD wait_until_status_change.
    DO 10 TIMES.
      WAIT UP TO 5 SECONDS.
      DATA(lo_describe_result) = ao_ec2->describeinstances(
          it_instanceids = VALUE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist(
            ( NEW /aws1/cl_ec2instidstringlist_w( iv_instance_id ) )
          )
      ).
      READ TABLE lo_describe_result->get_reservations( ) INTO DATA(lo_reservation) INDEX 1.
      READ TABLE lo_reservation->get_instances( ) INTO DATA(lo_describe_instance) INDEX 1.
      IF lo_describe_instance->get_state( )->get_name( ) = iv_required_status.
        EXIT.
      ENDIF.
    ENDDO.
    ov_current_status = lo_describe_instance->get_state( )->get_name( ).
  ENDMETHOD.
  METHOD run_instance.
    DATA(lo_create_result) = ao_ec2->runinstances(
        iv_imageid = get_ami_id( )
        iv_instancetype = 't2.micro'
        iv_maxcount = 1
        iv_mincount = 1
        iv_subnetid = iv_subnet_id
    ).
    READ TABLE lo_create_result->get_instances( ) INTO DATA(lo_instance) INDEX 1.
    ov_instance_id = lo_instance->get_instanceid( ).
  ENDMETHOD.
  METHOD terminate_instance.
    ao_ec2->terminateinstances00(
        it_instanceids = VALUE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist(
          ( NEW /aws1/cl_ec2instidstringlist_w( iv_instance_id ) )
        )
      ).
    wait_until_status_change( iv_instance_id = iv_instance_id iv_required_status = 'terminated' ).
  ENDMETHOD.
ENDCLASS.
