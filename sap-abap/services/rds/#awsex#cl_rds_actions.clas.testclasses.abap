" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_rds_actions DEFINITION DEFERRED.
CLASS /awsex/cl_rds_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_rds_actions.

CLASS ltc_awsex_cl_rds_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_param_group_name TYPE /aws1/rdsstring.
    CLASS-DATA av_db_instance_id TYPE /aws1/rdsstring.
    CLASS-DATA av_snapshot_id TYPE /aws1/rdsstring.
    CLASS-DATA av_db_name TYPE /aws1/rdsstring.
    CLASS-DATA av_master_username TYPE /aws1/rdsstring.
    CLASS-DATA av_master_password TYPE /aws1/rdssensitivestring.
    CLASS-DATA av_engine TYPE /aws1/rdsstring.
    CLASS-DATA av_engine_version TYPE /aws1/rdsstring.
    CLASS-DATA av_instance_class TYPE /aws1/rdsstring.
    CLASS-DATA av_param_group_family TYPE /aws1/rdsstring.
    CLASS-DATA av_default_vpc_id TYPE /aws1/rdsstring.
    CLASS-DATA av_db_subnet_group_name TYPE /aws1/rdsstring.
    CLASS-DATA at_vpc_security_group_ids TYPE /aws1/cl_rdsvpcsecgrpidlist_w=>tt_vpcsecuritygroupidlist.

    CLASS-DATA ao_rds TYPE REF TO /aws1/if_rds.
    CLASS-DATA ao_ec2 TYPE REF TO /aws1/if_ec2.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_rds_actions TYPE REF TO /awsex/cl_rds_actions.

    METHODS: describe_db_parameter_groups FOR TESTING RAISING /aws1/cx_rt_generic,
      create_db_parameter_group FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_db_parameters FOR TESTING RAISING /aws1/cx_rt_generic,
      modify_db_parameter_group FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_db_engine_versions FOR TESTING RAISING /aws1/cx_rt_generic,
      descrorderabledbinstopts FOR TESTING RAISING /aws1/cx_rt_generic,
      create_db_instance FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_db_instances FOR TESTING RAISING /aws1/cx_rt_generic,
      create_db_snapshot FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_db_snapshots FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_db_instance FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_db_parameter_group FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    CLASS-METHODS get_default_vpc
      RETURNING
        VALUE(rv_vpc_id) TYPE /aws1/ec2_vpcid
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS create_db_subnet_group
      IMPORTING
        iv_subnet_group_name TYPE /aws1/rdsstring
        iv_vpc_id            TYPE /aws1/ec2_vpcid
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS get_default_security_group
      IMPORTING
        iv_vpc_id                   TYPE /aws1/ec2_vpcid
      RETURNING
        VALUE(rv_security_group_id) TYPE /aws1/ec2_groupid
      RAISING
        /aws1/cx_rt_generic.

    METHODS wait_for_db_instance_available
      IMPORTING
        iv_db_instance_id TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    METHODS wait_for_snapshot_available
      IMPORTING
        iv_snapshot_id TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    METHODS wait_for_db_instance_deleted
      IMPORTING
        iv_db_instance_id TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_rds_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_rds = /aws1/cl_rds_factory=>create( ao_session ).
    ao_ec2 = /aws1/cl_ec2_factory=>create( ao_session ).
    ao_rds_actions = NEW /awsex/cl_rds_actions( ).

    " Set up test data using utils
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_param_group_name = |sap-rds-pg-{ lv_uuid }|.
    av_db_instance_id = |sap-rds-db-{ lv_uuid }|.
    av_snapshot_id = |sap-rds-snap-{ lv_uuid }|.
    av_db_subnet_group_name = |sap-rds-sub-{ lv_uuid }|.
    av_db_name = |testdb{ lv_uuid }|.
    av_db_name = av_db_name(15).  " Limit to 15 characters for MySQL
    av_master_username = 'admin'.
    av_master_password = |Pass{ lv_uuid }123!|.
    av_engine = 'mysql'.
    av_param_group_family = 'mysql8.0'.
    av_engine_version = '8.0.35'.
    av_instance_class = 'db.t3.micro'.

    " Get default VPC - MUST succeed
    av_default_vpc_id = get_default_vpc( ).
    IF av_default_vpc_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'No default VPC found. Cannot proceed with tests.' ).
    ENDIF.

    " Get default security group for VPC - MUST succeed
    DATA(lv_security_group_id) = get_default_security_group( av_default_vpc_id ).
    IF lv_security_group_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'No default security group found. Cannot proceed with tests.' ).
    ENDIF.
    APPEND lv_security_group_id TO at_vpc_security_group_ids.

    " Create DB subnet group - MUST succeed
    create_db_subnet_group(
      iv_subnet_group_name = av_db_subnet_group_name
      iv_vpc_id            = av_default_vpc_id ).

    " Verify DB subnet group was created
    TRY.
        DATA(lo_subnet_group_result) = ao_rds->describedbsubnetgroups(
          iv_dbsubnetgroupname = av_db_subnet_group_name ).
        IF lo_subnet_group_result IS INITIAL OR
           lo_subnet_group_result->get_dbsubnetgroups( ) IS INITIAL.
          cl_abap_unit_assert=>fail( msg = 'DB subnet group was not created successfully.' ).
        ENDIF.
      CATCH /aws1/cx_rdsdbsnetgrnotfndfa00.
        cl_abap_unit_assert=>fail( msg = 'DB subnet group was not created successfully.' ).
    ENDTRY.

    " Create parameter group with convert_test tag - MUST succeed
    TRY.
        ao_rds->createdbparametergroup(
          iv_dbparametergroupname   = av_param_group_name
          iv_dbparametergroupfamily = av_param_group_family
          iv_description            = 'Test parameter group for ABAP SDK'
          it_tags                   = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
      CATCH /aws1/cx_rdsdbparmgralrexfault.
        " If already exists from failed previous run, that's acceptable
    ENDTRY.

    " Verify parameter group was created
    TRY.
        DATA(lo_param_group_result) = ao_rds->describedbparametergroups(
          iv_dbparametergroupname = av_param_group_name ).
        IF lo_param_group_result IS INITIAL OR
           lo_param_group_result->get_dbparametergroups( ) IS INITIAL.
          cl_abap_unit_assert=>fail( msg = 'DB parameter group was not created successfully.' ).
        ENDIF.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        cl_abap_unit_assert=>fail( msg = 'DB parameter group was not created successfully.' ).
    ENDTRY.

    " Create DB instance with convert_test tag - MUST succeed
    TRY.
        ao_rds->createdbinstance(
          iv_dbname               = av_db_name
          iv_dbinstanceidentifier = av_db_instance_id
          iv_dbparametergroupname = av_param_group_name
          iv_engine               = av_engine
          iv_engineversion        = av_engine_version
          iv_dbinstanceclass      = av_instance_class
          iv_storagetype          = 'gp2'
          iv_allocatedstorage     = 20
          iv_masterusername       = av_master_username
          iv_masteruserpassword   = av_master_password
          iv_dbsubnetgroupname    = av_db_subnet_group_name
          it_vpcsecuritygroupids  = at_vpc_security_group_ids
          it_tags                 = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
      CATCH /aws1/cx_rdsdbinstalrdyexfault.
        " If already exists from failed previous run, that's acceptable
    ENDTRY.

    " Wait for DB instance to become available - MUST succeed
    wait_for_db_instance_available( av_db_instance_id ).

  ENDMETHOD.

  METHOD class_teardown.
    " Delete snapshot (quick operation)
    TRY.
        ao_rds->deletedbsnapshot( iv_dbsnapshotidentifier = av_snapshot_id ).
      CATCH /aws1/cx_rdsdbsnapnotfndfault.
        " Already deleted
    ENDTRY.

    " Delete DB instance - Note: takes 10+ minutes, tagged with convert_test for manual cleanup
    TRY.
        ao_rds->deletedbinstance(
          iv_dbinstanceidentifier   = av_db_instance_id
          iv_skipfinalsnapshot      = abap_true
          iv_deleteautomatedbackups = abap_true ).
      CATCH /aws1/cx_rdsdbinstnotfndfault.
        " Already deleted
    ENDTRY.

    " Note: Parameter group and subnet group deletion will fail while DB instance exists
    " These are tagged with convert_test and will be manually cleaned up
    " Do not delete these resources here as they depend on DB instance deletion

  ENDMETHOD.

  METHOD get_default_vpc.
    " Get the default VPC
    DATA(lo_vpcs_result) = ao_ec2->describevpcs( ).

    LOOP AT lo_vpcs_result->get_vpcs( ) INTO DATA(lo_vpc).
      IF lo_vpc->get_isdefault( ) = abap_true.
        rv_vpc_id = lo_vpc->get_vpcid( ).
        RETURN.
      ENDIF.
    ENDLOOP.

  ENDMETHOD.

  METHOD create_db_subnet_group.
    " Get subnets from the VPC
    DATA(lo_filter) = NEW /aws1/cl_ec2_filter( iv_name = 'vpc-id' ).
    lo_filter->add_item_values( iv_vpc_id ).

    DATA(lo_subnets_result) = ao_ec2->describesubnets(
      it_filters = VALUE #( ( lo_filter ) ) ).

    DATA lt_subnet_ids TYPE /aws1/cl_rdssubnetidlist_w=>tt_subnetidentifierlist.
    DATA lv_count TYPE i VALUE 0.
    DATA lt_azs TYPE STANDARD TABLE OF string.

    " We need at least 2 subnets in different AZs for RDS
    LOOP AT lo_subnets_result->get_subnets( ) INTO DATA(lo_subnet).
      DATA(lv_az) = lo_subnet->get_availabilityzone( ).
      " Only add subnets from different AZs
      READ TABLE lt_azs TRANSPORTING NO FIELDS WITH KEY table_line = lv_az.
      IF sy-subrc <> 0.
        APPEND lo_subnet->get_subnetid( ) TO lt_subnet_ids.
        APPEND lv_az TO lt_azs.
        lv_count = lv_count + 1.
        IF lv_count >= 2.
          EXIT.
        ENDIF.
      ENDIF.
    ENDLOOP.

    IF lines( lt_subnet_ids ) < 2.
      cl_abap_unit_assert=>fail( msg = 'Need at least 2 subnets in different AZs for RDS DB subnet group.' ).
    ENDIF.

    " Create DB subnet group with convert_test tag
    TRY.
        ao_rds->createdbsubnetgroup(
          iv_dbsubnetgroupname        = iv_subnet_group_name
          iv_dbsubnetgroupdescription = 'Test subnet group for ABAP SDK'
          it_subnetids                = lt_subnet_ids
          it_tags                     = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
      CATCH /aws1/cx_rdsdbsnetgralrexfault.
        " Already exists from failed previous run, that's acceptable
    ENDTRY.

  ENDMETHOD.

  METHOD get_default_security_group.
    " Get the default security group for the VPC
    DATA(lo_vpc_filter) = NEW /aws1/cl_ec2_filter( iv_name = 'vpc-id' ).
    lo_vpc_filter->add_item_values( iv_vpc_id ).

    DATA(lo_name_filter) = NEW /aws1/cl_ec2_filter( iv_name = 'group-name' ).
    lo_name_filter->add_item_values( 'default' ).

    DATA(lo_sgs_result) = ao_ec2->describesecuritygroups(
      it_filters = VALUE #( ( lo_vpc_filter ) ( lo_name_filter ) ) ).

    LOOP AT lo_sgs_result->get_securitygroups( ) INTO DATA(lo_sg).
      rv_security_group_id = lo_sg->get_groupid( ).
      RETURN.
    ENDLOOP.

  ENDMETHOD.

  METHOD wait_for_db_instance_available.
    DATA lv_max_wait TYPE i.
    DATA lv_wait_time TYPE i.
    DATA lv_status TYPE /aws1/rdsstring.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbinstancemessage.
    DATA lo_instance TYPE REF TO /aws1/cl_rdsdbinstance.
    
    lv_max_wait = 1200. " 20 minutes
    lv_wait_time = 0.
    lv_status = ''.

    WHILE lv_wait_time < lv_max_wait.
      WAIT UP TO 30 SECONDS.
      lv_wait_time = lv_wait_time + 30.

      TRY.
          lo_result = ao_rds->describedbinstances( iv_dbinstanceidentifier = iv_db_instance_id ).
          LOOP AT lo_result->get_dbinstances( ) INTO lo_instance.
            lv_status = lo_instance->get_dbinstancestatus( ).
            IF lv_status = 'available'.
              RETURN.
            ELSEIF lv_status = 'failed' OR lv_status = 'incompatible-restore' OR lv_status = 'incompatible-parameters'.
              cl_abap_unit_assert=>fail( msg = |DB instance { iv_db_instance_id } is in failed state: { lv_status }| ).
            ENDIF.
          ENDLOOP.
        CATCH /aws1/cx_rdsdbinstnotfndfault.
          " Instance not found yet, continue waiting
      ENDTRY.
    ENDWHILE.

    " Timeout
    cl_abap_unit_assert=>fail( msg = |DB instance { iv_db_instance_id } did not become available within timeout| ).
  ENDMETHOD.

  METHOD wait_for_snapshot_available.
    DATA lv_max_wait TYPE i.
    DATA lv_wait_time TYPE i.
    DATA lv_status TYPE /aws1/rdsstring.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbsnapshotmessage.
    DATA lo_snapshot TYPE REF TO /aws1/cl_rdsdbsnapshot.
    
    lv_max_wait = 1200. " 20 minutes
    lv_wait_time = 0.
    lv_status = ''.

    WHILE lv_wait_time < lv_max_wait.
      WAIT UP TO 30 SECONDS.
      lv_wait_time = lv_wait_time + 30.

      TRY.
          lo_result = ao_rds->describedbsnapshots( iv_dbsnapshotidentifier = iv_snapshot_id ).
          LOOP AT lo_result->get_dbsnapshots( ) INTO lo_snapshot.
            lv_status = lo_snapshot->get_status( ).
            IF lv_status = 'available'.
              RETURN.
            ELSEIF lv_status = 'failed'.
              cl_abap_unit_assert=>fail( msg = |DB snapshot { iv_snapshot_id } is in failed state| ).
            ENDIF.
          ENDLOOP.
        CATCH /aws1/cx_rdsdbsnapnotfndfault.
          " Snapshot not found yet, continue waiting
      ENDTRY.
    ENDWHILE.

    " Timeout
    cl_abap_unit_assert=>fail( msg = |DB snapshot { iv_snapshot_id } did not become available within timeout| ).
  ENDMETHOD.

  METHOD wait_for_db_instance_deleted.
    DATA lv_max_wait TYPE i.
    DATA lv_wait_time TYPE i.
    
    lv_max_wait = 600. " 10 minutes
    lv_wait_time = 0.

    WHILE lv_wait_time < lv_max_wait.
      WAIT UP TO 30 SECONDS.
      lv_wait_time = lv_wait_time + 30.

      TRY.
          ao_rds->describedbinstances( iv_dbinstanceidentifier = iv_db_instance_id ).
        CATCH /aws1/cx_rdsdbinstnotfndfault.
          " Instance deleted successfully
          RETURN.
      ENDTRY.
    ENDWHILE.

    " Timeout
    cl_abap_unit_assert=>fail( msg = |DB instance { iv_db_instance_id } did not get deleted within timeout| ).
  ENDMETHOD.

  METHOD describe_db_parameter_groups.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbparamgroupsmsg.

    " Use parameter group created in class_setup
    ao_rds_actions->describe_db_parameter_groups(
      EXPORTING
        iv_dbparametergroupname = av_param_group_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_dbparametergroups( )
      msg = 'DB parameter groups should not be empty' ).

    DATA(lv_found) = abap_false.
    LOOP AT lo_result->get_dbparametergroups( ) INTO DATA(lo_param_group).
      IF lo_param_group->get_dbparametergroupname( ) = av_param_group_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Parameter group { av_param_group_name } should be found| ).

  ENDMETHOD.

  METHOD create_db_parameter_group.
    DATA lo_result TYPE REF TO /aws1/cl_rdscredbparamgrprslt.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_param_group) = |test-pg-{ lv_uuid }|.

    " Create a new parameter group for this test
    ao_rds_actions->create_db_parameter_group(
      EXPORTING
        iv_dbparametergroupname   = lv_test_param_group
        iv_dbparametergroupfamily = av_param_group_family
        iv_description            = 'Test parameter group'
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_param_group
      act = lo_result->get_dbparametergroup( )->get_dbparametergroupname( )
      msg = 'Parameter group name should match' ).

    " Tag the parameter group
    TRY.
        DATA(lv_param_group_arn) = lo_result->get_dbparametergroup( )->get_dbparametergrouparn( ).
        ao_rds->addtagstoresource(
          iv_resourcename = lv_param_group_arn
          it_tags = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
      CATCH /aws1/cx_rt_generic.
        " Continue even if tagging fails
    ENDTRY.

    " Cleanup - delete the test parameter group
    TRY.
        ao_rds->deletedbparametergroup( iv_dbparametergroupname = lv_test_param_group ).
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        " Already deleted
    ENDTRY.

  ENDMETHOD.

  METHOD describe_db_parameters.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbparamgroupdets.

    " Use parameter group created in class_setup
    ao_rds_actions->describe_db_parameters(
      EXPORTING
        iv_dbparametergroupname = av_param_group_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_parameters( )
      msg = 'Parameters should not be empty' ).

  ENDMETHOD.

  METHOD modify_db_parameter_group.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbparamgrpnamemsg.

    " First get parameters to find modifiable ones
    DATA(lo_params_result) = ao_rds->describedbparameters(
      iv_dbparametergroupname = av_param_group_name ).

    DATA lt_update_params TYPE /aws1/cl_rdsparameter=>tt_parameterslist.
    LOOP AT lo_params_result->get_parameters( ) INTO DATA(lo_param).
      IF lo_param->get_ismodifiable( ) = abap_true AND
         lo_param->get_datatype( ) = 'integer' AND
         lo_param->get_parametername( ) CP 'max_connections*'.
        lo_param->set_applymethod( 'immediate' ).
        lo_param->set_parametervalue( '100' ).
        APPEND lo_param TO lt_update_params.
        EXIT.
      ENDIF.
    ENDLOOP.

    IF lt_update_params IS INITIAL.
      " If no modifiable max_connections parameter found, try another parameter
      LOOP AT lo_params_result->get_parameters( ) INTO lo_param.
        IF lo_param->get_ismodifiable( ) = abap_true AND
           lo_param->get_datatype( ) = 'integer'.
          " Parse allowed values to get a valid value
          DATA(lv_allowed) = lo_param->get_allowedvalues( ).
          IF lv_allowed CP '*-*'.
            " Range format like '1-65535'
            SPLIT lv_allowed AT '-' INTO DATA(lv_min) DATA(lv_max).
            lo_param->set_applymethod( 'immediate' ).
            lo_param->set_parametervalue( lv_min ).
            APPEND lo_param TO lt_update_params.
            EXIT.
          ENDIF.
        ENDIF.
      ENDLOOP.
    ENDIF.

    IF lt_update_params IS NOT INITIAL.
      ao_rds_actions->modify_db_parameter_group(
        EXPORTING
          iv_dbparametergroupname = av_param_group_name
          it_parameters           = lt_update_params
        IMPORTING
          oo_result = lo_result ).

      cl_abap_unit_assert=>assert_bound(
        act = lo_result
        msg = 'Result should not be initial' ).

      cl_abap_unit_assert=>assert_equals(
        exp = av_param_group_name
        act = lo_result->get_dbparametergroupname( )
        msg = 'Parameter group name should match' ).
    ELSE.
      " Should not happen but if it does, fail the test
      cl_abap_unit_assert=>fail( msg = 'No modifiable parameters found for modification test' ).
    ENDIF.

  ENDMETHOD.

  METHOD describe_db_engine_versions.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbenginevrsmessage.

    ao_rds_actions->describe_db_engine_versions(
      EXPORTING
        iv_engine                 = av_engine
        iv_dbparametergroupfamily = av_param_group_family
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_dbengineversions( )
      msg = 'Engine versions should not be empty' ).

  ENDMETHOD.

  METHOD descrorderabledbinstopts.
    DATA lo_result TYPE REF TO /aws1/cl_rdsorderabledbinsto00.

    ao_rds_actions->descrorderabledbinstopts(
      EXPORTING
        iv_engine        = av_engine
        iv_engineversion = av_engine_version
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_orderabledbinstanceoptions( )
      msg = 'Orderable options should not be empty' ).

  ENDMETHOD.

  METHOD create_db_instance.
    " DB instance was created in class_setup and waited for availability
    " Verify the instance exists and is available
    DATA(lo_result) = ao_rds->describedbinstances( iv_dbinstanceidentifier = av_db_instance_id ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_dbinstances( )
      msg = 'DB instances should not be empty' ).

    DATA(lv_found) = abap_false.
    DATA(lv_status) = ''.
    LOOP AT lo_result->get_dbinstances( ) INTO DATA(lo_instance).
      IF lo_instance->get_dbinstanceidentifier( ) = av_db_instance_id.
        lv_found = abap_true.
        lv_status = lo_instance->get_dbinstancestatus( ).
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |DB instance { av_db_instance_id } should be created| ).

    cl_abap_unit_assert=>assert_equals(
      exp = 'available'
      act = lv_status
      msg = |DB instance should be in available state| ).

  ENDMETHOD.

  METHOD describe_db_instances.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbinstancemessage.

    " Use DB instance created in class_setup
    ao_rds_actions->describe_db_instances(
      EXPORTING
        iv_dbinstanceidentifier = av_db_instance_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_dbinstances( )
      msg = 'DB instances should not be empty' ).

    " Verify it's our instance
    DATA(lv_found) = abap_false.
    LOOP AT lo_result->get_dbinstances( ) INTO DATA(lo_instance).
      IF lo_instance->get_dbinstanceidentifier( ) = av_db_instance_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |DB instance { av_db_instance_id } should be found| ).

  ENDMETHOD.

  METHOD create_db_snapshot.
    DATA lo_result TYPE REF TO /aws1/cl_rdscreatedbsnapresult.

    " Check if snapshot already exists from previous run and delete it
    TRY.
        ao_rds->describedbsnapshots( iv_dbsnapshotidentifier = av_snapshot_id ).
        " Snapshot exists, delete it first
        TRY.
            ao_rds->deletedbsnapshot( iv_dbsnapshotidentifier = av_snapshot_id ).
            WAIT UP TO 10 SECONDS.
          CATCH /aws1/cx_rt_generic.
            " Continue if deletion fails
        ENDTRY.
      CATCH /aws1/cx_rdsdbsnapnotfndfault.
        " Snapshot doesn't exist, continue
    ENDTRY.

    " Create snapshot
    ao_rds_actions->create_db_snapshot(
      EXPORTING
        iv_dbsnapshotidentifier = av_snapshot_id
        iv_dbinstanceidentifier = av_db_instance_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_snapshot_id
      act = lo_result->get_dbsnapshot( )->get_dbsnapshotidentifier( )
      msg = 'Snapshot identifier should match' ).

    " Tag the snapshot
    TRY.
        DATA(lv_snapshot_arn) = lo_result->get_dbsnapshot( )->get_dbsnapshotarn( ).
        ao_rds->addtagstoresource(
          iv_resourcename = lv_snapshot_arn
          it_tags = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
      CATCH /aws1/cx_rt_generic.
        " Continue even if tagging fails
    ENDTRY.

  ENDMETHOD.

  METHOD describe_db_snapshots.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbsnapshotmessage.

    " Wait for the snapshot to be available
    wait_for_snapshot_available( av_snapshot_id ).

    ao_rds_actions->describe_db_snapshots(
      EXPORTING
        iv_dbsnapshotidentifier = av_snapshot_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_dbsnapshots( )
      msg = 'DB snapshots should not be empty' ).

    " Verify it's our snapshot
    DATA(lv_found) = abap_false.
    LOOP AT lo_result->get_dbsnapshots( ) INTO DATA(lo_snapshot).
      IF lo_snapshot->get_dbsnapshotidentifier( ) = av_snapshot_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |DB snapshot { av_snapshot_id } should be found| ).

  ENDMETHOD.

  METHOD delete_db_instance.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdeletedbinstresult.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_instance_id) = |test-db-{ lv_uuid }|.
    DATA(lv_test_db_name) = |tdb{ lv_uuid }|.
    lv_test_db_name = lv_test_db_name(15).

    " Create a test instance specifically for this delete test
    ao_rds->createdbinstance(
      iv_dbname               = lv_test_db_name
      iv_dbinstanceidentifier = lv_test_instance_id
      iv_dbparametergroupname = av_param_group_name
      iv_engine               = av_engine
      iv_engineversion        = av_engine_version
      iv_dbinstanceclass      = av_instance_class
      iv_storagetype          = 'gp2'
      iv_allocatedstorage     = 20
      iv_masterusername       = av_master_username
      iv_masteruserpassword   = av_master_password
      iv_dbsubnetgroupname    = av_db_subnet_group_name
      it_vpcsecuritygroupids  = at_vpc_security_group_ids
      it_tags                 = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).

    " Wait for instance to become available
    wait_for_db_instance_available( lv_test_instance_id ).

    " Now delete it
    ao_rds_actions->delete_db_instance(
      EXPORTING
        iv_dbinstanceidentifier = lv_test_instance_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_instance_id
      act = lo_result->get_dbinstance( )->get_dbinstanceidentifier( )
      msg = 'Deleted DB instance identifier should match' ).

    " Wait for deletion to complete
    wait_for_db_instance_deleted( lv_test_instance_id ).

  ENDMETHOD.

  METHOD delete_db_parameter_group.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_param_group) = |test-pg-del-{ lv_uuid }|.

    " Create a test parameter group specifically for deletion
    ao_rds->createdbparametergroup(
      iv_dbparametergroupname   = lv_test_param_group
      iv_dbparametergroupfamily = av_param_group_family
      iv_description            = 'Test parameter group for deletion'
      it_tags                   = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).

    " Verify it was created
    TRY.
        ao_rds->describedbparametergroups( iv_dbparametergroupname = lv_test_param_group ).
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        cl_abap_unit_assert=>fail( msg = 'Test parameter group was not created' ).
    ENDTRY.

    " Delete it
    ao_rds_actions->delete_db_parameter_group( iv_dbparametergroupname = lv_test_param_group ).

    " Verify deletion
    TRY.
        ao_rds->describedbparametergroups( iv_dbparametergroupname = lv_test_param_group ).
        cl_abap_unit_assert=>fail( msg = 'Parameter group should have been deleted' ).
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        " Expected - parameter group was deleted successfully
    ENDTRY.

  ENDMETHOD.

ENDCLASS.
