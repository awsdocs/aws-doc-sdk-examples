" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_rds_fast DEFINITION DEFERRED.
CLASS /awsex/cl_rds_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_rds_fast.

CLASS ltc_awsex_cl_rds_fast DEFINITION FOR TESTING DURATION MEDIUM RISK LEVEL HARMLESS.
  " Fast test class - Tests only operations that complete quickly (<5 minutes)
  " Suitable for CI/CD pipelines and Lambda execution
  " Does NOT test: DB instance creation, snapshots (these require 10-20 minute waits)

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_param_group_name TYPE /aws1/rdsstring.
    CLASS-DATA av_engine TYPE /aws1/rdsstring.
    CLASS-DATA av_param_group_family TYPE /aws1/rdsstring.
    CLASS-DATA av_engine_version TYPE /aws1/rdsstring.

    CLASS-DATA ao_rds TYPE REF TO /aws1/if_rds.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_rds_actions TYPE REF TO /awsex/cl_rds_actions.

    METHODS: describe_db_parameter_groups FOR TESTING RAISING /aws1/cx_rt_generic,
      create_db_parameter_group FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_db_parameters FOR TESTING RAISING /aws1/cx_rt_generic,
      modify_db_parameter_group FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_db_engine_versions FOR TESTING RAISING /aws1/cx_rt_generic,
      descrorderabledbinstopts FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_db_parameter_group FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_rds_fast IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_uuid TYPE string.
    DATA lo_engine_versions TYPE REF TO /aws1/cl_rdsdbenginevrsmessage.
    DATA lo_engine_version TYPE REF TO /aws1/cl_rdsdbengineversion.
    
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_rds = /aws1/cl_rds_factory=>create( ao_session ).
    ao_rds_actions = NEW /awsex/cl_rds_actions( ).

    " Set up test data using utils
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    av_param_group_name = |sap-rds-fast-{ lv_uuid }|.
    av_engine = 'mysql'.
    av_param_group_family = 'mysql8.0'.

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

    " Create parameter group with convert_test tag for shared use
    TRY.
        ao_rds->createdbparametergroup(
          iv_dbparametergroupname   = av_param_group_name
          iv_dbparametergroupfamily = av_param_group_family
          iv_description            = 'Test parameter group for ABAP SDK fast tests'
          it_tags                   = VALUE #( ( NEW /aws1/cl_rdstag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
      CATCH /aws1/cx_rdsdbparmgralrexfault.
        " If already exists from failed previous run, that's acceptable
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up parameter group
    TRY.
        ao_rds->deletedbparametergroup( iv_dbparametergroupname = av_param_group_name ).
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        " Already deleted
      CATCH /aws1/cx_rdsinvdbprmgrstatef00.
        " In use, will be cleaned up manually via convert_test tag
    ENDTRY.
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
    lv_test_param_group = |test-fast-pg-{ lv_uuid }|.

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
          lv_allowed = lo_param->get_allowedvalues( ).
          IF lv_allowed CP '*-*'.
            SPLIT lv_allowed AT '-' INTO lv_min lv_max.
            lv_param_name = lo_param->get_parametername( ).
            lv_param_value = lv_min.
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
      
      cl_abap_unit_assert=>assert_true(
        act = boolc( lv_returned_name CP |{ av_param_group_name }*| OR
                     av_param_group_name CP |{ lv_returned_name }*| )
        msg = |Parameter group name should match: Expected { av_param_group_name }, Got { lv_returned_name }| ).
    ELSE.
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

  METHOD delete_db_parameter_group.
    DATA lv_uuid TYPE string.
    DATA lv_test_param_group TYPE /aws1/rdsstring.

    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_test_param_group = |test-fast-del-{ lv_uuid }|.

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
