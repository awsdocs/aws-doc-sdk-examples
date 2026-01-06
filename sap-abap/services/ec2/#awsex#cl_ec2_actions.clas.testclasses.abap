" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_ec2_actions DEFINITION DEFERRED.
CLASS /awsex/cl_ec2_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_ec2_actions.

CLASS ltc_awsex_cl_ec2_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_ec2 TYPE REF TO /aws1/if_ec2.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_ec2_actions TYPE REF TO /awsex/cl_ec2_actions.
    CLASS-DATA av_vpc_id TYPE /aws1/ec2string.
    CLASS-DATA av_subnet_id TYPE /aws1/ec2string.
    CLASS-DATA at_instance_id TYPE TABLE OF /aws1/ec2string. " table of instance IDs to terminate
    CLASS-DATA av_instance_id TYPE /aws1/ec2string. " main instance Id for tests

    METHODS: allocate_address FOR TESTING RAISING /aws1/cx_rt_generic,
      associate_address FOR TESTING RAISING /aws1/cx_rt_generic,
      disassociate_address FOR TESTING RAISING /aws1/cx_rt_generic,
      authorize_sec_group_ingress FOR TESTING RAISING /aws1/cx_rt_generic,
      terminate_instances FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_images FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_instance_types FOR TESTING RAISING /aws1/cx_rt_generic,
      create_vpc FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_route_tables FOR TESTING RAISING /aws1/cx_rt_generic,
      create_vpc_endpoint FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_vpc_endpoints FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_vpc FOR TESTING RAISING /aws1/cx_rt_generic,
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

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic /awsex/cx_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic /awsex/cx_generic.


    CLASS-METHODS:
      get_ami_id
        RETURNING VALUE(ov_ami_id) TYPE /aws1/ec2string
        RAISING   /aws1/cx_rt_generic,
      wait_until_status_change
        IMPORTING iv_required_status       TYPE string
                  iv_instance_id           TYPE string
        RETURNING VALUE(ov_current_status) TYPE string
        RAISING   /aws1/cx_rt_generic,
      run_instance
        IMPORTING iv_subnet_id          TYPE /aws1/ec2subnetid
        RETURNING VALUE(ov_instance_id) TYPE /aws1/ec2string
        RAISING   /aws1/cx_rt_generic,
      terminate_instance
        IMPORTING iv_instance_id TYPE /aws1/ec2string
        RAISING   /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_ec2_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_ec2 = /aws1/cl_ec2_factory=>create( ao_session ).
    ao_ec2_actions = NEW /awsex/cl_ec2_actions( ).
    av_vpc_id = ao_ec2->createvpc( iv_cidrblock  = '10.10.0.0/16' )->get_vpc( )->get_vpcid( ).
    av_subnet_id = ao_ec2->createsubnet( iv_vpcid = av_vpc_id
                                               iv_cidrblock = '10.10.0.0/24' )->get_subnet( )->get_subnetid( ).
    av_instance_id = run_instance( av_subnet_id ).

  ENDMETHOD.

  METHOD class_teardown.
    LOOP AT at_instance_id ASSIGNING FIELD-SYMBOL(<instance_id>).
      terminate_instance( <instance_id> ).
    ENDLOOP.

    DO 4 TIMES.
      TRY.
          ao_ec2->deletesubnet( iv_subnetid = av_subnet_id ).
          EXIT.  " exit the loop
        CATCH /aws1/cx_ec2clientexc INTO DATA(lo_ex).
          IF lo_ex->get_text( ) CS 'dependencies'.
            WAIT UP TO 15 SECONDS.
          ELSE.
            RAISE EXCEPTION lo_ex.
          ENDIF.

      ENDTRY.
    ENDDO.
    DO 4 TIMES.
      TRY.
          ao_ec2->deletevpc( iv_vpcid = av_vpc_id ).
        CATCH /aws1/cx_ec2clientexc INTO lo_ex.
          IF lo_ex->av_err_code = 'DependencyViolation'.
            WAIT UP TO 15 SECONDS.
          ELSEIF lo_ex->av_err_code = 'InvalidVpcID.NotFound'.
            EXIT.
          ELSE.
            RAISE EXCEPTION lo_ex.
          ENDIF.
      ENDTRY.
    ENDDO.
  ENDMETHOD.

  METHOD allocate_address.
    DATA(lo_result) = ao_ec2_actions->allocate_address( ).

    cl_abap_unit_assert=>assert_not_initial(
          act = lo_result->get_allocationid( )
          msg = |Failed to allocate an Elastic IP address| ).

    ao_ec2->releaseaddress( iv_allocationid = lo_result->get_allocationid( ) ).

  ENDMETHOD.
  METHOD associate_address.
    " Skip this test as it takes too long waiting for instance to be in running state
    cl_abap_unit_assert=>skip( 'Test skipped - takes too long due to instance state changes' ).
  ENDMETHOD.
  METHOD describe_addresses.
    " Skip this test as it takes too long waiting for instance to be in running state
    cl_abap_unit_assert=>skip( 'Test skipped - takes too long due to instance state changes' ).
  ENDMETHOD.
  METHOD release_address.
    " Skip this test as it takes too long waiting for instance to be in running state
    cl_abap_unit_assert=>skip( 'Test skipped - takes too long due to instance state changes' ).
  ENDMETHOD.
  METHOD create_instance.
    " Skip this test as it takes too long due to instance state transitions
    cl_abap_unit_assert=>skip( 'Test skipped - takes too long due to instance state changes' ).
  ENDMETHOD.
  METHOD monitor_instance.
    " Skip this test as it takes too long due to instance state transitions
    cl_abap_unit_assert=>skip( 'Test skipped - takes too long due to instance state changes' ).
  ENDMETHOD.
  METHOD reboot_instance.
    " Skip this test as it takes too long due to instance state transitions
    cl_abap_unit_assert=>skip( 'Test skipped - takes too long due to instance state changes' ).
  ENDMETHOD.
  METHOD start_instances.
    " Skip this test as it takes too long due to instance state transitions
    cl_abap_unit_assert=>skip( 'Test skipped - takes too long due to instance state changes' ).
  ENDMETHOD.
  METHOD stop_instances.
    " Skip this test as it takes too long due to instance state transitions
    cl_abap_unit_assert=>skip( 'Test skipped - takes too long due to instance state changes' ).
  ENDMETHOD.
  METHOD describe_instances.
    DATA(lo_describe_result) = ao_ec2_actions->describe_instances( ).
    READ TABLE lo_describe_result->get_reservations( ) INTO DATA(lo_reservation) INDEX 1.
    cl_abap_unit_assert=>assert_not_initial(
          act = lo_reservation->get_instances( )
          msg = |Instance List should not be empty| ).
  ENDMETHOD.
  METHOD create_key_pair.
    CONSTANTS cv_key_name TYPE /aws1/ec2string VALUE 'code-example-create-key-pair'.
    DATA(lo_result) = ao_ec2_actions->create_key_pair( cv_key_name ).
    cl_abap_unit_assert=>assert_not_initial(
          act = lo_result->get_keypairid( )
          msg = |Failed to create key pair { cv_key_name }| ).


    IF lo_result->get_keyfingerprint( ) IS NOT INITIAL AND lo_result->get_keymaterial( ) IS NOT INITIAL AND lo_result->get_keyname( ) = cv_key_name.
      DATA(lv_has_details) = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
      act = lv_has_details
      msg = |The response object for key pair { cv_key_name } does not contain the required elements| ).

    ao_ec2->deletekeypair( iv_keyname = cv_key_name ).
  ENDMETHOD.
  METHOD delete_key_pair.
    CONSTANTS cv_key_name TYPE /aws1/ec2string VALUE 'code-example-delete-key-pair'.
    ao_ec2->createkeypair( iv_keyname = cv_key_name ).
    ao_ec2_actions->delete_key_pair( cv_key_name ).
    DATA(lo_result) = ao_ec2->describekeypairs( ).


    LOOP AT lo_result->get_keypairs( ) INTO DATA(lo_key_pair).
      IF lo_key_pair->get_keyname( ) = cv_key_name.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Key Pair { cv_key_name } should have been deleted| ).
  ENDMETHOD.
  METHOD describe_key_pairs.
    CONSTANTS cv_key_name TYPE /aws1/ec2string VALUE 'code-example-describe-key-pairs'.
    ao_ec2->createkeypair( iv_keyname = cv_key_name ).
    DATA(lo_result) = ao_ec2_actions->describe_key_pairs( ).


    LOOP AT lo_result->get_keypairs( ) INTO DATA(lo_key_pair).
      IF lo_key_pair->get_keyname( ) = cv_key_name.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Key Pair { cv_key_name } should have been included in key pair list| ).
    ao_ec2->deletekeypair( iv_keyname = cv_key_name ).
  ENDMETHOD.
  METHOD describe_regions.
    DATA(lo_result) = ao_ec2_actions->describe_regions( ).
    cl_abap_unit_assert=>assert_not_initial(
          act = lo_result->get_regions( )
          msg = |Failed to retrieve list of regions| ).
  ENDMETHOD.
  METHOD describe_availability_zones.
    DATA(lo_result) = ao_ec2_actions->describe_availability_zones( ).
    cl_abap_unit_assert=>assert_not_initial(
          act = lo_result->get_availabilityzones( )
          msg = |Failed to retrieve list of availability zones| ).
  ENDMETHOD.
  METHOD create_security_group.
    CONSTANTS cv_security_group_name TYPE /aws1/ec2string VALUE 'code-example-create-security-group'.
    DATA(lo_create_result) = ao_ec2_actions->create_security_group( iv_security_group_name = cv_security_group_name
                                                                    iv_vpc_id = av_vpc_id ).
    DATA(lo_describe_result) = ao_ec2->describesecuritygroups(
      it_groupids = VALUE /aws1/cl_ec2groupidstrlist_w=>tt_groupidstringlist(
                      ( NEW /aws1/cl_ec2groupidstrlist_w( lo_create_result->get_groupid( ) ) )
                    ) ).


    LOOP AT lo_describe_result->get_securitygroups( ) INTO DATA(lo_security_group).
      IF lo_security_group->get_groupname( ) = cv_security_group_name.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Failed to create security group { cv_security_group_name }| ).

    ao_ec2->deletesecuritygroup( iv_groupid = lo_create_result->get_groupid( ) ).
  ENDMETHOD.
  METHOD delete_security_group.
    CONSTANTS cv_security_group_name TYPE /aws1/ec2string VALUE 'code-example-delete-security-group'.
    DATA(lo_create_result) = ao_ec2->createsecuritygroup(
        iv_groupname = cv_security_group_name
        iv_description = |security group for delete_security_group test|
        iv_vpcid = av_vpc_id ).
    ao_ec2_actions->delete_security_group( lo_create_result->get_groupid( ) ).
    DATA(lo_describe_result) = ao_ec2->describesecuritygroups(
      it_filters = VALUE /aws1/cl_ec2filter=>tt_filterlist(
                      ( NEW /aws1/cl_ec2filter(
                        iv_name = 'vpc-id'
                        it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
                          ( NEW /aws1/cl_ec2valuestringlist_w( av_vpc_id ) )
                        )
                      ) )
                    ) ).


    LOOP AT lo_describe_result->get_securitygroups( ) INTO DATA(lo_security_group).
      IF lo_security_group->get_groupname( ) = cv_security_group_name.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Security Group { cv_security_group_name } should have been deleted| ).

  ENDMETHOD.
  METHOD describe_security_groups.
    CONSTANTS cv_security_group_name TYPE /aws1/ec2string VALUE 'code-example-describe-security-groups'.
    DATA(lo_create_result) = ao_ec2->createsecuritygroup(
        iv_groupname = cv_security_group_name
        iv_description = |security group for describe_security_groups test|
        iv_vpcid = av_vpc_id ).

    DATA(lo_describe_result) = ao_ec2_actions->describe_security_groups( lo_create_result->get_groupid( ) ).

    LOOP AT lo_describe_result->get_securitygroups( ) INTO DATA(lo_security_group).
      IF lo_security_group->get_groupname( ) = cv_security_group_name.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Security Group { cv_security_group_name } should have been included in security group list| ).

    ao_ec2->deletesecuritygroup( iv_groupid = lo_create_result->get_groupid( ) ).
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
                 ( NEW /aws1/cl_ec2valuestringlist_w( cv_ami_name ) )
           ) ) )
           ( NEW /aws1/cl_ec2filter(
               iv_name = 'architecture'
               it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
                ( NEW /aws1/cl_ec2valuestringlist_w( cv_architecture ) )
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
    DO 96 TIMES.
      WAIT UP TO 5 SECONDS.
      DATA(lo_describe_result) = ao_ec2->describeinstances(
          it_instanceids = VALUE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist(
            ( NEW /aws1/cl_ec2instidstringlist_w( iv_instance_id ) )
          ) ).
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
        iv_instancetype = 't3.micro'
        iv_maxcount = 1
        iv_mincount = 1
        iv_subnetid = iv_subnet_id ).
    READ TABLE lo_create_result->get_instances( ) INTO DATA(lo_instance) INDEX 1.
    ov_instance_id = lo_instance->get_instanceid( ).
    APPEND ov_instance_id TO at_instance_id.
  ENDMETHOD.
  METHOD terminate_instance.
    ao_ec2->terminateinstances00(
        it_instanceids = VALUE /aws1/cl_ec2instidstringlist_w=>tt_instanceidstringlist(
          ( NEW /aws1/cl_ec2instidstringlist_w( iv_instance_id ) )
        ) ).
    wait_until_status_change( iv_instance_id = iv_instance_id
                              iv_required_status = 'terminated' ).
  ENDMETHOD.

  METHOD disassociate_address.
    " Skip this test as it takes too long waiting for instance to be in running state
    cl_abap_unit_assert=>skip( 'Test skipped - takes too long due to instance state changes' ).
  ENDMETHOD.

  METHOD authorize_sec_group_ingress.
    CONSTANTS cv_security_group_name TYPE /aws1/ec2string VALUE 'code-ex-auth-sec-grp'.
    DATA(lo_create_result) = ao_ec2->createsecuritygroup(
        iv_groupname = cv_security_group_name
        iv_description = |security group for authorize test|
        iv_vpcid = av_vpc_id ).
    DATA(lv_group_id) = lo_create_result->get_groupid( ).

    DATA(lo_auth_result) = ao_ec2_actions->authorize_sec_group_ingress(
      iv_group_id = lv_group_id
      iv_cidr_ip = '192.0.2.0/24' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_auth_result->get_return( )
      msg = |Failed to authorize security group ingress rule| ).

    ao_ec2->deletesecuritygroup( iv_groupid = lv_group_id ).
  ENDMETHOD.

  METHOD terminate_instances.
    " Skip this test as it takes too long due to instance state transitions
    cl_abap_unit_assert=>skip( 'Test skipped - takes too long due to instance state changes' ).
  ENDMETHOD.

  METHOD describe_images.
    DATA(lv_ami_id) = get_ami_id( ).
    DATA lt_image_ids TYPE /aws1/cl_ec2imageidstrlist_w=>tt_imageidstringlist.
    APPEND NEW /aws1/cl_ec2imageidstrlist_w( lv_ami_id ) TO lt_image_ids.

    DATA(lo_result) = ao_ec2_actions->describe_images( lt_image_ids ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_images( )
      msg = |Failed to describe images| ).
    READ TABLE lo_result->get_images( ) INTO DATA(lo_image) INDEX 1.
    cl_abap_unit_assert=>assert_equals(
      act = lo_image->get_imageid( )
      exp = lv_ami_id
      msg = |Image ID should match requested AMI| ).
  ENDMETHOD.

  METHOD describe_instance_types.
    DATA(lo_result) = ao_ec2_actions->describe_instance_types( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_instancetypes( )
      msg = |Failed to describe instance types| ).
  ENDMETHOD.

  METHOD create_vpc.
    CONSTANTS cv_cidr_block TYPE /aws1/ec2string VALUE '10.20.0.0/16'.
    DATA(lo_result) = ao_ec2_actions->create_vpc( cv_cidr_block ).
    DATA(lv_vpc_id) = lo_result->get_vpc( )->get_vpcid( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_vpc_id
      msg = |Failed to create VPC| ).

    DO 4 TIMES.
      TRY.
          ao_ec2->deletevpc( iv_vpcid = lv_vpc_id ).
          EXIT.
        CATCH /aws1/cx_ec2clientexc INTO DATA(lo_ex).
          IF lo_ex->av_err_code = 'DependencyViolation'.
            WAIT UP TO 15 SECONDS.
          ELSEIF lo_ex->av_err_code = 'InvalidVpcID.NotFound'.
            EXIT.
          ELSE.
            RAISE EXCEPTION lo_ex.
          ENDIF.
      ENDTRY.
    ENDDO.
  ENDMETHOD.

  METHOD describe_route_tables.
    DATA lt_vpc_ids TYPE /aws1/cl_ec2vpcidstrlist_w=>tt_vpcidstringlist.
    APPEND NEW /aws1/cl_ec2vpcidstrlist_w( av_vpc_id ) TO lt_vpc_ids.

    DATA(lo_result) = ao_ec2_actions->describe_route_tables( lt_vpc_ids ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_routetables( )
      msg = |Failed to describe route tables| ).
  ENDMETHOD.

  METHOD create_vpc_endpoint.
    DATA(lv_test_vpc_id) = ao_ec2->createvpc( iv_cidrblock = '10.30.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lo_route_table_result) = ao_ec2->describeroutetables(
      it_filters = VALUE /aws1/cl_ec2filter=>tt_filterlist(
        ( NEW /aws1/cl_ec2filter(
          iv_name = 'vpc-id'
          it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
            ( NEW /aws1/cl_ec2valuestringlist_w( lv_test_vpc_id ) )
          )
        ) )
      ) ).
    READ TABLE lo_route_table_result->get_routetables( ) INTO DATA(lo_route_table) INDEX 1.
    DATA(lv_route_table_id) = lo_route_table->get_routetableid( ).
    DATA lt_route_table_ids TYPE /aws1/cl_ec2vpcendptroutetbl00=>tt_vpcendpointroutetableidlist.
    APPEND NEW /aws1/cl_ec2vpcendptroutetbl00( lv_route_table_id ) TO lt_route_table_ids.
    DATA(lv_region) = CONV string( ao_session->get_region( ) ).
    DATA(lv_service_name) = |com.amazonaws.{ lv_region }.s3|.

    DATA(lo_result) = ao_ec2_actions->create_vpc_endpoint(
      iv_vpc_id = lv_test_vpc_id
      iv_service_name = lv_service_name
      it_route_table_ids = lt_route_table_ids ).
    DATA(lv_vpc_endpoint_id) = lo_result->get_vpcendpoint( )->get_vpcendpointid( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_vpc_endpoint_id
      msg = |Failed to create VPC endpoint| ).

    ao_ec2->deletevpcendpoints(
      it_vpcendpointids = VALUE /aws1/cl_ec2vpcendptidlist_w=>tt_vpcendpointidlist(
        ( NEW /aws1/cl_ec2vpcendptidlist_w( lv_vpc_endpoint_id ) )
      ) ).
    DO 4 TIMES.
      TRY.
          ao_ec2->deletevpc( iv_vpcid = lv_test_vpc_id ).
          EXIT.
        CATCH /aws1/cx_ec2clientexc INTO DATA(lo_ex).
          IF lo_ex->av_err_code = 'DependencyViolation'.
            WAIT UP TO 15 SECONDS.
          ELSEIF lo_ex->av_err_code = 'InvalidVpcID.NotFound'.
            EXIT.
          ELSE.
            RAISE EXCEPTION lo_ex.
          ENDIF.
      ENDTRY.
    ENDDO.
  ENDMETHOD.

  METHOD delete_vpc_endpoints.
    DATA(lv_test_vpc_id) = ao_ec2->createvpc( iv_cidrblock = '10.40.0.0/16' )->get_vpc( )->get_vpcid( ).
    DATA(lo_route_table_result) = ao_ec2->describeroutetables(
      it_filters = VALUE /aws1/cl_ec2filter=>tt_filterlist(
        ( NEW /aws1/cl_ec2filter(
          iv_name = 'vpc-id'
          it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
            ( NEW /aws1/cl_ec2valuestringlist_w( lv_test_vpc_id ) )
          )
        ) )
      ) ).
    READ TABLE lo_route_table_result->get_routetables( ) INTO DATA(lo_route_table) INDEX 1.
    DATA(lv_route_table_id) = lo_route_table->get_routetableid( ).
    DATA(lv_region) = CONV string( ao_session->get_region( ) ).
    DATA(lv_service_name) = |com.amazonaws.{ lv_region }.s3|.
    DATA(lo_endpoint_result) = ao_ec2->createvpcendpoint(
      iv_vpcid = lv_test_vpc_id
      iv_servicename = lv_service_name
      it_routetableids = VALUE /aws1/cl_ec2vpcendptroutetbl00=>tt_vpcendpointroutetableidlist(
        ( NEW /aws1/cl_ec2vpcendptroutetbl00( lv_route_table_id ) )
      ) ).
    DATA(lv_vpc_endpoint_id) = lo_endpoint_result->get_vpcendpoint( )->get_vpcendpointid( ).

    ao_ec2_actions->delete_vpc_endpoints(
      VALUE /aws1/cl_ec2vpcendptidlist_w=>tt_vpcendpointidlist(
        ( NEW /aws1/cl_ec2vpcendptidlist_w( lv_vpc_endpoint_id ) )
      ) ).

    DATA(lo_describe_result) = ao_ec2->describevpcendpoints(
      it_vpcendpointids = VALUE /aws1/cl_ec2vpcendptidlist_w=>tt_vpcendpointidlist(
        ( NEW /aws1/cl_ec2vpcendptidlist_w( lv_vpc_endpoint_id ) )
      ) ).
    READ TABLE lo_describe_result->get_vpcendpoints( ) INTO DATA(lo_endpoint) INDEX 1.
    DATA(lv_state) = lo_endpoint->get_state( ).

    cl_abap_unit_assert=>assert_equals(
      act = lv_state
      exp = 'deleted'
      msg = |VPC endpoint should be in deleted state| ).

    DO 4 TIMES.
      TRY.
          ao_ec2->deletevpc( iv_vpcid = lv_test_vpc_id ).
          EXIT.
        CATCH /aws1/cx_ec2clientexc INTO DATA(lo_ex).
          IF lo_ex->av_err_code = 'DependencyViolation'.
            WAIT UP TO 15 SECONDS.
          ELSEIF lo_ex->av_err_code = 'InvalidVpcID.NotFound'.
            EXIT.
          ELSE.
            RAISE EXCEPTION lo_ex.
          ENDIF.
      ENDTRY.
    ENDDO.
  ENDMETHOD.

  METHOD delete_vpc.
    DATA(lv_test_vpc_id) = ao_ec2->createvpc( iv_cidrblock = '10.50.0.0/16' )->get_vpc( )->get_vpcid( ).

    ao_ec2_actions->delete_vpc( lv_test_vpc_id ).

    TRY.
        ao_ec2->describevpcs(
          it_vpcids = VALUE /aws1/cl_ec2vpcidstrlist_w=>tt_vpcidstringlist(
            ( NEW /aws1/cl_ec2vpcidstrlist_w( lv_test_vpc_id ) )
          ) ).
        cl_abap_unit_assert=>fail( msg = |VPC should have been deleted| ).
      CATCH /aws1/cx_ec2clientexc INTO DATA(lo_ex).
        cl_abap_unit_assert=>assert_equals(
          act = lo_ex->av_err_code
          exp = 'InvalidVpcID.NotFound'
          msg = |VPC should not be found after deletion| ).
    ENDTRY.
  ENDMETHOD.
ENDCLASS.
