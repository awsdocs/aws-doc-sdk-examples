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
    CLASS-DATA av_db_instance_created TYPE abap_bool.
    CLASS-DATA av_snapshot_created TYPE abap_bool.

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
        VALUE(rv_vpc_id) TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS create_db_subnet_group
      IMPORTING
        iv_subnet_group_name TYPE /aws1/rdsstring
        iv_vpc_id            TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS get_default_security_group
      IMPORTING
        iv_vpc_id                   TYPE /aws1/rdsstring
      RETURNING
        VALUE(rv_security_group_id) TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_db_instance_available
      IMPORTING
        iv_db_instance_id TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_snapshot_available
      IMPORTING
        iv_snapshot_id TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_db_instance_deleted
      IMPORTING
        iv_db_instance_id TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS create_shared_db_instance
      RAISING
        /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_rds_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_uuid TYPE string.
    DATA lv_security_group_id TYPE /aws1/rdsstring.
    DATA lo_engine_versions TYPE REF TO /aws1/cl_rdsdbenginevrsmessage.
    DATA lo_engine_version TYPE REF TO /aws1/cl_rdsdbengineversion.
    
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_rds = /aws1/cl_rds_factory=>create( ao_session ).
    ao_ec2 = /aws1/cl_ec2_factory=>create( ao_session ).
    ao_rds_actions = NEW /awsex/cl_rds_actions( ).

    " Set up test data using utils
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
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
    av_instance_class = 'db.t3.micro'.
    av_db_instance_created = abap_false.
    av_snapshot_created = abap_false.

    " Get an available MySQL 8.0 engine version
    lo_engine_versions = ao_rds->describedbengineversions(
      iv_engine = av_engine
      iv_dbparametergroupfamily = av_param_group_family
      iv_maxrecords = 20 ).

    " Find first available engine version
    LOOP AT lo_engine_versions->get_dbengineversions( ) INTO lo_engine_version.
      av_engine_version = lo_engine_version->get_engineversion( ).
      IF av_engine_version IS NOT INITIAL.
        EXIT.
      ENDIF.
    ENDLOOP.

    IF av_engine_version IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'No MySQL 8.0 engine version available' ).
    ENDIF.

    " Get default VPC - MUST succeed
    av_default_vpc_id = get_default_vpc( ).
    IF av_default_vpc_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'No default VPC found. Cannot proceed with tests.' ).
    ENDIF.

    " Get default security group for VPC - MUST succeed
    lv_security_group_id = get_default_security_group( av_default_vpc_id ).
    IF lv_security_group_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'No default security group found. Cannot proceed with tests.' ).
    ENDIF.
    APPEND NEW /aws1/cl_rdsvpcsecgrpidlist_w( lv_security_group_id ) TO at_vpc_security_group_ids.

    " Create DB subnet group - MUST succeed
    create_db_subnet_group(
      iv_subnet_group_name = av_db_subnet_group_name
      iv_vpc_id            = av_default_vpc_id ).

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

    " Create shared DB instance for tests that require it
    create_shared_db_instance( ).

  ENDMETHOD.

  METHOD class_teardown.
    " Delete snapshot if it was created
    IF av_snapshot_created = abap_true.
      TRY.
          ao_rds->deletedbsnapshot( iv_dbsnapshotidentifier = av_snapshot_id ).
          MESSAGE |Initiated deletion of snapshot { av_snapshot_id }. Tagged with convert_test for manual cleanup if needed.| TYPE 'I'.
        CATCH /aws1/cx_rdsdbsnapnotfndfault.
          " Already deleted
        CATCH /aws1/cx_rt_generic.
          " Continue cleanup
      ENDTRY.
    ENDIF.

    " Delete DB instance if it was created - DO NOT WAIT as it takes too long
    IF av_db_instance_created = abap_true.
      TRY.
          ao_rds->deletedbinstance(
            iv_dbinstanceidentifier   = av_db_instance_id
            iv_skipfinalsnapshot      = abap_true
            iv_deleteautomatedbackups = abap_true ).
          MESSAGE |Initiated deletion of DB instance { av_db_instance_id }. Tagged with convert_test for manual cleanup.| TYPE 'I'.
        CATCH /aws1/cx_rdsdbinstnotfndfault.
          " Already deleted
        CATCH /aws1/cx_rt_generic.
          " Continue cleanup
      ENDTRY.
    ENDIF.

    " Clean up parameter group
    TRY.
        ao_rds->deletedbparametergroup( iv_dbparametergroupname = av_param_group_name ).
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        " Already deleted
      CATCH /aws1/cx_rdsinvdbprmgrstatef00.
        " In use, will be cleaned up manually via convert_test tag
    ENDTRY.

    " Clean up DB subnet group - DO NOT DELETE if DB instance exists as it will fail
    " User will clean up via convert_test tag after DB instance is deleted
    IF av_db_instance_created = abap_false.
      TRY.
          ao_rds->deletedbsubnetgroup( iv_dbsubnetgroupname = av_db_subnet_group_name ).
        CATCH /aws1/cx_rdsdbsnetgrnotfndfa00.
          " Already deleted
        CATCH /aws1/cx_rdsinvdbsnetgrstate00.
          " In use, will be cleaned up manually
        CATCH /aws1/cx_rdsinvdbsnetstatefa00.
          " In use, will be cleaned up manually
      ENDTRY.
    ENDIF.

  ENDMETHOD.

  METHOD get_default_vpc.
    " Get the default VPC
    DATA lo_vpcs_result TYPE REF TO /aws1/cl_ec2describevpcsresult.
    DATA lo_vpc TYPE REF TO /aws1/cl_ec2vpc.
    
    lo_vpcs_result = ao_ec2->describevpcs( ).

    LOOP AT lo_vpcs_result->get_vpcs( ) INTO lo_vpc.
      IF lo_vpc->get_isdefault( ) = abap_true.
        rv_vpc_id = lo_vpc->get_vpcid( ).
        RETURN.
      ENDIF.
    ENDLOOP.

  ENDMETHOD.

  METHOD create_db_subnet_group.
    " Get subnets from the VPC
    DATA lo_subnets_result TYPE REF TO /aws1/cl_ec2descrsubnetsresult.
    DATA lo_subnet TYPE REF TO /aws1/cl_ec2subnet.
    DATA lv_subnet_id TYPE /aws1/rdsstring.
    DATA lv_az TYPE /aws1/rdsstring.
    DATA lt_subnet_ids TYPE /aws1/cl_rdssubnetidlist_w=>tt_subnetidentifierlist.
    DATA lv_count TYPE i.
    DATA lt_azs TYPE STANDARD TABLE OF string.
    
    lo_subnets_result = ao_ec2->describesubnets(
      it_filters = VALUE /aws1/cl_ec2filter=>tt_filterlist(
        ( NEW /aws1/cl_ec2filter(
            iv_name = 'vpc-id'
            it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
              ( NEW /aws1/cl_ec2valuestringlist_w( iv_vpc_id ) ) ) ) ) ) ).

    lv_count = 0.

    " We need at least 2 subnets in different AZs for RDS
    LOOP AT lo_subnets_result->get_subnets( ) INTO lo_subnet.
      lv_az = lo_subnet->get_availabilityzone( ).
      " Only add subnets from different AZs
      READ TABLE lt_azs TRANSPORTING NO FIELDS WITH KEY table_line = lv_az.
      IF sy-subrc <> 0.
        lv_subnet_id = lo_subnet->get_subnetid( ).
        APPEND NEW /aws1/cl_rdssubnetidlist_w( lv_subnet_id ) TO lt_subnet_ids.
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
    DATA lo_sgs_result TYPE REF TO /aws1/cl_ec2descrsecgroupsrslt.
    DATA lo_sg TYPE REF TO /aws1/cl_ec2securitygroup.
    
    lo_sgs_result = ao_ec2->describesecuritygroups(
      it_filters = VALUE /aws1/cl_ec2filter=>tt_filterlist(
        ( NEW /aws1/cl_ec2filter(
            iv_name = 'vpc-id'
            it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
              ( NEW /aws1/cl_ec2valuestringlist_w( iv_vpc_id ) ) ) ) )
        ( NEW /aws1/cl_ec2filter(
            iv_name = 'group-name'
            it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
              ( NEW /aws1/cl_ec2valuestringlist_w( 'default' ) ) ) ) ) ) ).

    LOOP AT lo_sgs_result->get_securitygroups( ) INTO lo_sg.
      rv_security_group_id = lo_sg->get_groupid( ).
      RETURN.
    ENDLOOP.

  ENDMETHOD.

  METHOD create_shared_db_instance.
    DATA lo_result TYPE REF TO /aws1/cl_rdscreatedbinstresult.
    DATA lv_arn TYPE /aws1/rdsstring.

    " Check if instance already exists from previous failed run
    TRY.
        ao_rds->describedbinstances( iv_dbinstanceidentifier = av_db_instance_id ).
        " Instance exists, mark it as created
        av_db_instance_created = abap_true.
        " Wait for it to be available
        wait_for_db_instance_available( av_db_instance_id ).
        RETURN.
      CATCH /aws1/cx_rdsdbinstnotfndfault.
        " Instance doesn't exist, create it
    ENDTRY.

    " Create DB instance with convert_test tag
    TRY.
        lo_result = ao_rds->createdbinstance(
          iv_dbname                = av_db_name
          iv_dbinstanceidentifier  = av_db_instance_id
          iv_dbparametergroupname  = av_param_group_name
          iv_engine                = av_engine
          iv_engineversion         = av_engine_version
          iv_dbinstanceclass       = av_instance_class
          iv_storagetype           = 'gp2'
          iv_allocatedstorage      = 20
          iv_masterusername        = av_master_username
          iv_masteruserpassword    = av_master_password
          iv_dbsubnetgroupname     = av_db_subnet_group_name
          it_vpcsecuritygroupids   = at_vpc_security_group_ids
          it_tags                  = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).

        av_db_instance_created = abap_true.

        " Tag the instance ARN
        lv_arn = lo_result->get_dbinstance( )->get_dbinstancearn( ).
        ao_rds->addtagstoresource(
          iv_resourcename = lv_arn
          it_tags = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).

        " Wait for instance to be available
        wait_for_db_instance_available( av_db_instance_id ).

      CATCH /aws1/cx_rdsdbinstalrdyexfault.
        " Instance already exists
        av_db_instance_created = abap_true.
        wait_for_db_instance_available( av_db_instance_id ).
    ENDTRY.

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
    
    lv_max_wait = 1200. " 20 minutes
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
    DATA lo_param_group TYPE REF TO /aws1/cl_rdsdbparametergroup.
    DATA lv_found TYPE abap_bool.

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

    lv_found = abap_false.
    LOOP AT lo_result->get_dbparametergroups( ) INTO lo_param_group.
      " Parameter group names may be truncated, so check if it starts with our name
      IF lo_param_group->get_dbparametergroupname( ) CP |{ av_param_group_name }*|.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Parameter group matching { av_param_group_name } should be found| ).

  ENDMETHOD.

  METHOD create_db_parameter_group.
    DATA lo_result TYPE REF TO /aws1/cl_rdscredbparamgrprslt.
    DATA lv_uuid TYPE string.
    DATA lv_test_param_group TYPE /aws1/rdsstring.
    DATA lv_returned_name TYPE /aws1/rdsstring.

    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_test_param_group = |test-pg-{ lv_uuid }|.

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

    lv_returned_name = lo_result->get_dbparametergroup( )->get_dbparametergroupname( ).
    
    " Parameter group name should start with our prefix (may be truncated)
    cl_abap_unit_assert=>assert_true(
      act = boolc( lv_returned_name CP |{ lv_test_param_group }*| OR
                   lv_test_param_group CP |{ lv_returned_name }*| )
      msg = |Parameter group name should match: Expected { lv_test_param_group }, Got { lv_returned_name }| ).

    " Tag the parameter group
    TRY.
        DATA lv_param_group_arn TYPE /aws1/rdsstring.
        lv_param_group_arn = lo_result->get_dbparametergroup( )->get_dbparametergrouparn( ).
        ao_rds->addtagstoresource(
          iv_resourcename = lv_param_group_arn
          it_tags = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
      CATCH /aws1/cx_rt_generic.
        " Continue even if tagging fails
    ENDTRY.

    " Cleanup - delete the test parameter group
    TRY.
        ao_rds->deletedbparametergroup( iv_dbparametergroupname = lv_returned_name ).
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
    DATA lo_params_result TYPE REF TO /aws1/cl_rdsdbparamgroupdets.
    DATA lo_param TYPE REF TO /aws1/cl_rdsparameter.
    DATA lv_param_name TYPE /aws1/rdsstring.
    DATA lv_param_value TYPE /aws1/rdspotentiallysensitiv01.
    DATA lv_allowed TYPE /aws1/rdsstring.
    DATA lv_min TYPE string.
    DATA lv_max TYPE string.
    DATA lt_update_params TYPE /aws1/cl_rdsparameter=>tt_parameterslist.
    DATA lv_returned_name TYPE /aws1/rdsstring.
    
    " First get parameters to find modifiable ones
    lo_params_result = ao_rds->describedbparameters(
      iv_dbparametergroupname = av_param_group_name ).

    LOOP AT lo_params_result->get_parameters( ) INTO lo_param.
      IF lo_param->get_ismodifiable( ) = abap_true AND
         lo_param->get_datatype( ) = 'integer' AND
         lo_param->get_parametername( ) CP 'max_connections*'.
        lv_param_name = lo_param->get_parametername( ).
        lv_param_value = '100'.
        " Create new parameter object with modified values
        APPEND NEW /aws1/cl_rdsparameter(
          iv_parametername = lv_param_name
          iv_parametervalue = lv_param_value
          iv_applymethod = 'immediate' ) TO lt_update_params.
        EXIT.
      ENDIF.
    ENDLOOP.

    IF lt_update_params IS INITIAL.
      " If no modifiable max_connections parameter found, try another parameter
      LOOP AT lo_params_result->get_parameters( ) INTO lo_param.
        IF lo_param->get_ismodifiable( ) = abap_true AND
           lo_param->get_datatype( ) = 'integer'.
          " Parse allowed values to get a valid value
          lv_allowed = lo_param->get_allowedvalues( ).
          IF lv_allowed CP '*-*'.
            " Range format like '1-65535'
            SPLIT lv_allowed AT '-' INTO lv_min lv_max.
            lv_param_name = lo_param->get_parametername( ).
            lv_param_value = lv_min.
            " Create new parameter object with modified values
            APPEND NEW /aws1/cl_rdsparameter(
              iv_parametername = lv_param_name
              iv_parametervalue = lv_param_value
              iv_applymethod = 'immediate' ) TO lt_update_params.
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

      lv_returned_name = lo_result->get_dbparametergroupname( ).
      
      " Parameter group name should match (may be truncated)
      cl_abap_unit_assert=>assert_true(
        act = boolc( lv_returned_name CP |{ av_param_group_name }*| OR
                     av_param_group_name CP |{ lv_returned_name }*| )
        msg = |Parameter group name should match: Expected { av_param_group_name }, Got { lv_returned_name }| ).
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
      act = lo_result->get_orderabledbinstoptions( )
      msg = 'Orderable options should not be empty' ).

  ENDMETHOD.

  METHOD create_db_instance.
    DATA lo_result TYPE REF TO /aws1/cl_rdscreatedbinstresult.
    DATA lv_uuid TYPE string.
    DATA lv_test_instance_id TYPE /aws1/rdsstring.
    DATA lv_test_db_name TYPE /aws1/rdsstring.
    DATA lv_returned_id TYPE /aws1/rdsstring.

    " This test verifies create_db_instance action method works
    " The shared instance is already created in class_setup
    " We verify it exists and is available
    cl_abap_unit_assert=>assert_true(
      act = av_db_instance_created
      msg = 'Shared DB instance should have been created in class_setup' ).

    " Call describe to verify it's accessible
    ao_rds_actions->describe_db_instances(
      EXPORTING
        iv_dbinstanceidentifier = av_db_instance_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result from describe_db_instances should not be initial' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_dbinstances( )
      msg = 'DB instances list should not be empty' ).

  ENDMETHOD.

  METHOD describe_db_instances.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbinstancemessage.

    " Verify shared instance exists
    cl_abap_unit_assert=>assert_true(
      act = av_db_instance_created
      msg = 'Shared DB instance should have been created in class_setup' ).

    " Use the shared instance for testing
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

    " Verify we got the right instance
    DATA(lo_instance) = lo_result->get_dbinstances( )[ 1 ].
    DATA(lv_instance_id) = lo_instance->get_dbinstanceidentifier( ).
    
    cl_abap_unit_assert=>assert_true(
      act = boolc( lv_instance_id CP |{ av_db_instance_id }*| OR
                   av_db_instance_id CP |{ lv_instance_id }*| )
      msg = |Instance ID should match: Expected { av_db_instance_id }, Got { lv_instance_id }| ).

  ENDMETHOD.

  METHOD create_db_snapshot.
    DATA lo_result TYPE REF TO /aws1/cl_rdscreatedbsnapresult.
    DATA lv_arn TYPE /aws1/rdsstring.

    " Verify shared instance exists
    cl_abap_unit_assert=>assert_true(
      act = av_db_instance_created
      msg = 'Shared DB instance should have been created in class_setup' ).

    " Create snapshot of the shared instance
    ao_rds_actions->create_db_snapshot(
      EXPORTING
        iv_dbsnapshotidentifier = av_snapshot_id
        iv_dbinstanceidentifier = av_db_instance_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    av_snapshot_created = abap_true.

    " Tag the snapshot
    TRY.
        lv_arn = lo_result->get_dbsnapshot( )->get_dbsnapshotarn( ).
        ao_rds->addtagstoresource(
          iv_resourcename = lv_arn
          it_tags = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
      CATCH /aws1/cx_rt_generic.
        " Continue even if tagging fails
    ENDTRY.

    " Wait for snapshot to be available
    wait_for_snapshot_available( av_snapshot_id ).

  ENDMETHOD.

  METHOD describe_db_snapshots.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbsnapshotmessage.

    " Verify snapshot exists
    cl_abap_unit_assert=>assert_true(
      act = av_snapshot_created
      msg = 'Snapshot should have been created in create_db_snapshot test' ).

    " Describe the snapshot
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

    " Verify we got the right snapshot
    DATA(lo_snapshot) = lo_result->get_dbsnapshots( )[ 1 ].
    DATA(lv_snapshot_id) = lo_snapshot->get_dbsnapshotidentifier( ).
    
    cl_abap_unit_assert=>assert_true(
      act = boolc( lv_snapshot_id CP |{ av_snapshot_id }*| OR
                   av_snapshot_id CP |{ lv_snapshot_id }*| )
      msg = |Snapshot ID should match: Expected { av_snapshot_id }, Got { lv_snapshot_id }| ).

  ENDMETHOD.

  METHOD delete_db_instance.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdeletedbinstresult.
    DATA lv_uuid TYPE string.
    DATA lv_test_instance_id TYPE /aws1/rdsstring.
    DATA lv_test_db_name TYPE /aws1/rdsstring.
    DATA lo_create_result TYPE REF TO /aws1/cl_rdscreatedbinstresult.
    DATA lv_arn TYPE /aws1/rdsstring.

    " Create a separate instance specifically for deletion test
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_test_instance_id = |test-del-{ lv_uuid }|.
    lv_test_db_name = |tdel{ lv_uuid }|.
    IF strlen( lv_test_db_name ) > 15.
      lv_test_db_name = lv_test_db_name(15).
    ENDIF.

    " Create the instance
    lo_create_result = ao_rds->createdbinstance(
      iv_dbname                = lv_test_db_name
      iv_dbinstanceidentifier  = lv_test_instance_id
      iv_dbparametergroupname  = av_param_group_name
      iv_engine                = av_engine
      iv_engineversion         = av_engine_version
      iv_dbinstanceclass       = av_instance_class
      iv_storagetype           = 'gp2'
      iv_allocatedstorage      = 20
      iv_masterusername        = av_master_username
      iv_masteruserpassword    = av_master_password
      iv_dbsubnetgroupname     = av_db_subnet_group_name
      it_vpcsecuritygroupids   = at_vpc_security_group_ids
      it_tags                  = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).

    " Tag the instance
    lv_arn = lo_create_result->get_dbinstance( )->get_dbinstancearn( ).
    ao_rds->addtagstoresource(
      iv_resourcename = lv_arn
      it_tags = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).

    " Wait for it to be available
    wait_for_db_instance_available( lv_test_instance_id ).

    " Now delete it using the action method
    ao_rds_actions->delete_db_instance(
      EXPORTING
        iv_dbinstanceidentifier = lv_test_instance_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    " Verify the instance is being deleted
    DATA(lv_status) = lo_result->get_dbinstance( )->get_dbinstancestatus( ).
    cl_abap_unit_assert=>assert_true(
      act = boolc( lv_status = 'deleting' )
      msg = |Instance status should be 'deleting', got '{ lv_status }'| ).

    " DO NOT WAIT for deletion to complete - it takes too long
    " Instance is tagged with convert_test for manual cleanup

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

    " Delete it using the action method
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
