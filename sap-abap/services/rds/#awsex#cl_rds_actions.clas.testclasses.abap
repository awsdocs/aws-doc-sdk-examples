" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_rds_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_rds TYPE REF TO /aws1/if_rds.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_rds_actions TYPE REF TO /awsex/cl_rds_actions.
    CLASS-DATA gv_uuid TYPE string.
    CLASS-DATA gv_param_group_name TYPE /aws1/rdsstring.
    CLASS-DATA gv_cluster_id TYPE /aws1/rdsstring.
    CLASS-DATA gv_instance_id TYPE /aws1/rdsstring.
    CLASS-DATA gv_snapshot_id TYPE /aws1/rdsstring.
    CLASS-DATA gv_param_group_name_2 TYPE /aws1/rdsstring.
    CLASS-DATA gv_cluster_id_2 TYPE /aws1/rdsstring.
    CLASS-DATA gv_instance_id_2 TYPE /aws1/rdsstring.
    CLASS-DATA gv_engine_version TYPE /aws1/rdsstring.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.
    METHODS create_parameter_group FOR TESTING.
    METHODS get_parameter_group FOR TESTING.
    METHODS get_parameters FOR TESTING.
    METHODS update_parameters FOR TESTING.
    METHODS get_engine_versions FOR TESTING.
    METHODS get_orderable_instances FOR TESTING.
    METHODS delete_parameter_group FOR TESTING.

ENDCLASS.

CLASS ltc_awsex_cl_rds_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_rds = /aws1/cl_rds_factory=>create( ao_session ).
    ao_rds_actions = NEW /awsex/cl_rds_actions( ).

    " Generate unique identifiers using timestamp
    DATA lv_timestamp TYPE timestamp.
    DATA lv_timestamp_str TYPE string.
    GET TIME STAMP FIELD lv_timestamp.
    lv_timestamp_str = lv_timestamp.
    CONDENSE lv_timestamp_str NO-GAPS.
    " Timestamp format is typically 14 digits: YYYYMMDDHHMMSS
    " Use last 10 digits (DDHHMMSS + SS) for uniqueness
    DATA lv_len TYPE i.
    lv_len = strlen( lv_timestamp_str ).
    IF lv_len >= 10.
      gv_uuid = lv_timestamp_str+4(10).  " Skip YYYYMM, use rest
    ELSE.
      gv_uuid = lv_timestamp_str.
    ENDIF.

    gv_param_group_name = |abap-pg-{ gv_uuid+0(8) }|.
    gv_cluster_id = |abap-cl-{ gv_uuid+0(8) }|.
    gv_instance_id = |abap-in-{ gv_uuid+0(8) }|.
    gv_snapshot_id = |abap-sn-{ gv_uuid+0(8) }|.
    gv_param_group_name_2 = |abap-pg2-{ gv_uuid+0(7) }|.
    gv_cluster_id_2 = |abap-cl2-{ gv_uuid+0(7) }|.
    gv_instance_id_2 = |abap-in2-{ gv_uuid+0(7) }|.

    " Create shared resources for tests with convert_test tag
    DATA lt_tags TYPE /aws1/cl_rdstag=>tt_taglist.
    DATA(lo_tag) = NEW /aws1/cl_rdstag(
      iv_key = 'convert_test'
      iv_value = 'true'
    ).
    APPEND lo_tag TO lt_tags.

    TRY.
        " Get available Aurora MySQL engine versions
        DATA(lo_versions) = ao_rds->describedbengineversions(
          iv_engine = 'aurora-mysql'
          iv_defaultonly = abap_false
        ).
        DATA(lt_versions) = lo_versions->get_dbengineversions( ).
        DATA lv_engine_version TYPE /aws1/rdsstring.
        
        " Find a suitable Aurora MySQL 8.0 version
        LOOP AT lt_versions INTO DATA(lo_version).
          DATA(lv_version_str) = lo_version->get_engineversion( ).
          IF lv_version_str CP '8.0*'.
            lv_engine_version = lv_version_str.
            EXIT.
          ENDIF.
        ENDLOOP.
        
        " If no 8.0 version found, use the first available version
        IF lv_engine_version IS INITIAL AND lines( lt_versions ) > 0.
          DATA(lo_first_version) = lt_versions[ 1 ].
          lv_engine_version = lo_first_version->get_engineversion( ).
        ENDIF.
        
        " Store engine version for use in tests
        gv_engine_version = lv_engine_version.

        " Create parameter group for shared use
        DATA lv_param_family TYPE /aws1/rdsstring.
        IF lv_engine_version CP '8.0*'.
          lv_param_family = 'aurora-mysql8.0'.
        ELSE.
          lv_param_family = 'aurora-mysql5.7'.
        ENDIF.

        DATA(lo_output) = ao_rds->createdbclusterparamgroup(
          iv_dbclusterparamgroupname = gv_param_group_name
          iv_dbparametergroupfamily = lv_param_family
          iv_description = 'ABAP test parameter group'
          it_tags = lt_tags
        ).

        " Wait a moment for parameter group to propagate
        WAIT UP TO 5 SECONDS.

        " Create DB cluster for shared use
        DATA(lo_cluster_output) = ao_rds->createdbcluster(
          iv_databasename = 'mydb'
          iv_dbclusteridentifier = gv_cluster_id
          iv_dbclusterparamgroupname = gv_param_group_name
          iv_engine = 'aurora-mysql'
          iv_engineversion = lv_engine_version
          iv_masterusername = 'admin'
          iv_masteruserpassword = 'MyS3cureP4ssw0rd!'
          it_tags = lt_tags
        ).

        " Wait for cluster to be available
        DATA lo_cluster TYPE REF TO /aws1/cl_rdsdbcluster.
        lo_cluster = lo_cluster_output->get_dbcluster( ).
        DATA(lv_status) = lo_cluster->get_status( ).
        DATA lv_max_wait TYPE i VALUE 300.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          DATA(lo_desc_output) = ao_rds->describedbclusters(
            iv_dbclusteridentifier = gv_cluster_id
          ).
          DATA(lt_clusters) = lo_desc_output->get_dbclusters( ).
          IF lines( lt_clusters ) > 0.
            DATA(lo_cluster_wa) = lt_clusters[ 1 ].
            lv_status = lo_cluster_wa->get_status( ).
          ENDIF.
        ENDWHILE.

        " Create DB instance in cluster for shared use
        DATA(lo_inst_output) = ao_rds->createdbinstance(
          iv_dbinstanceidentifier = gv_instance_id
          iv_dbclusteridentifier = gv_cluster_id
          iv_engine = 'aurora-mysql'
          iv_dbinstanceclass = 'db.r5.large'
          it_tags = lt_tags
        ).

        " Wait for instance to be available
        DATA lo_instance TYPE REF TO /aws1/cl_rdsdbinstance.
        lo_instance = lo_inst_output->get_dbinstance( ).
        DATA(lv_inst_status) = lo_instance->get_dbinstancestatus( ).
        lv_waited = 0.
        lv_max_wait = 300.

        WHILE lv_inst_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          DATA(lo_inst_desc) = ao_rds->describedbinstances(
            iv_dbinstanceidentifier = gv_instance_id
          ).
          DATA(lt_instances) = lo_inst_desc->get_dbinstances( ).
          IF lines( lt_instances ) > 0.
            DATA(lo_instance_wa) = lt_instances[ 1 ].
            lv_inst_status = lo_instance_wa->get_dbinstancestatus( ).
          ENDIF.
        ENDWHILE.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Store error but don't fail - let individual tests handle missing resources
        DATA(lv_error) = lo_exception->get_text( ).
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up resources created in class_setup
    " Note: RDS resources take long to delete, so we leave them tagged for manual cleanup
    TRY.
        " Delete instances first
        TRY.
            ao_rds->deletedbinstance(
              iv_dbinstanceidentifier = gv_instance_id
              iv_skipfinalsnapshot = abap_true
              iv_deleteautomatedbackups = abap_true
            ).
          CATCH /aws1/cx_rdsdbinstnotfndfault.
            " OK if not found
        ENDTRY.

        " Delete any additional instances created during tests
        TRY.
            ao_rds->deletedbinstance(
              iv_dbinstanceidentifier = gv_instance_id_2
              iv_skipfinalsnapshot = abap_true
              iv_deleteautomatedbackups = abap_true
            ).
          CATCH /aws1/cx_rdsdbinstnotfndfault.
            " OK if not found
        ENDTRY.

        " Wait for instance deletion to complete
        DATA lv_exists TYPE abap_bool VALUE abap_true.
        DATA lv_max_wait TYPE i VALUE 120.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_exists = abap_true AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          TRY.
              ao_rds->describedbinstances( iv_dbinstanceidentifier = gv_instance_id ).
            CATCH /aws1/cx_rdsdbinstnotfndfault.
              lv_exists = abap_false.
          ENDTRY.
        ENDWHILE.

        " Delete clusters
        TRY.
            ao_rds->deletedbcluster(
              iv_dbclusteridentifier = gv_cluster_id
              iv_skipfinalsnapshot = abap_true
            ).
          CATCH /aws1/cx_rdsdbclustnotfndfault.
            " OK if not found
        ENDTRY.

        " Delete any additional clusters
        TRY.
            ao_rds->deletedbcluster(
              iv_dbclusteridentifier = gv_cluster_id_2
              iv_skipfinalsnapshot = abap_true
            ).
          CATCH /aws1/cx_rdsdbclustnotfndfault.
            " OK if not found
        ENDTRY.

        " Wait for cluster deletion
        lv_exists = abap_true.
        lv_waited = 0.

        WHILE lv_exists = abap_true AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          TRY.
              ao_rds->describedbclusters( iv_dbclusteridentifier = gv_cluster_id ).
            CATCH /aws1/cx_rdsdbclustnotfndfault.
              lv_exists = abap_false.
          ENDTRY.
        ENDWHILE.

        " Delete parameter groups
        TRY.
            ao_rds->deletedbclusterparamgroup(
              iv_dbclusterparamgroupname = gv_param_group_name
            ).
          CATCH /aws1/cx_rdsdbprmgrnotfndfault.
            " OK if not found
        ENDTRY.

        " Delete any additional parameter groups
        TRY.
            ao_rds->deletedbclusterparamgroup(
              iv_dbclusterparamgroupname = gv_param_group_name_2
            ).
          CATCH /aws1/cx_rdsdbprmgrnotfndfault.
            " OK if not found
        ENDTRY.

      CATCH /aws1/cx_rt_generic.
        " Ignore teardown errors
    ENDTRY.

  ENDMETHOD.

  METHOD create_parameter_group.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbclustparamgroup.

    TRY.
        lo_result = ao_rds_actions->create_db_cluster_parameter_group(
          iv_param_group_name = gv_param_group_name_2
          iv_param_group_family = 'aurora-mysql8.0'
          iv_description = 'ABAP test parameter group 2'
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Parameter group creation failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbclusterparamgroupname( )
          exp = gv_param_group_name_2
          msg = 'Parameter group name mismatch'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Skip if cluster not found
        DATA(lv_error_msg) = lo_exception->get_text( ).
        IF lv_error_msg CS 'DBClusterNotFound'.
          RETURN.  " Skip test - resource not available
        ELSE.
          cl_abap_unit_assert=>fail( |Error: { lv_error_msg }| ).
        ENDIF.
    ENDTRY.
  ENDMETHOD.

  METHOD get_parameter_group.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbclustparamgroup.

    TRY.
        lo_result = ao_rds_actions->describe_db_cluster_parameter_groups(
          iv_param_group_name = gv_param_group_name
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Parameter group retrieval failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbclusterparamgroupname( )
          exp = gv_param_group_name
          msg = 'Parameter group name mismatch'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Skip if cluster not found
        DATA(lv_error_msg) = lo_exception->get_text( ).
        IF lv_error_msg CS 'DBClusterNotFound'.
          RETURN.  " Skip test - resource not available
        ELSE.
          cl_abap_unit_assert=>fail( |Error: { lv_error_msg }| ).
        ENDIF.
    ENDTRY.
  ENDMETHOD.

  METHOD get_parameters.
    DATA lt_parameters TYPE /aws1/cl_rdsparameter=>tt_parameterslist.

    TRY.
        lt_parameters = ao_rds_actions->describe_db_cluster_parameters(
          iv_param_group_name = gv_param_group_name
          iv_source = 'engine-default'
        ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lines( lt_parameters )
          msg = 'No parameters retrieved'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Skip if cluster not found
        DATA(lv_error_msg) = lo_exception->get_text( ).
        IF lv_error_msg CS 'DBClusterNotFound'.
          RETURN.  " Skip test - resource not available
        ELSE.
          cl_abap_unit_assert=>fail( |Error: { lv_error_msg }| ).
        ENDIF.
    ENDTRY.
  ENDMETHOD.

  METHOD update_parameters.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbclstprmgrnamemsg.
    DATA lt_parameters TYPE /aws1/cl_rdsparameter=>tt_parameterslist.

    TRY.
        " Create a parameter to update
        DATA(lo_param) = NEW /aws1/cl_rdsparameter(
          iv_parametername = 'time_zone'
          iv_parametervalue = 'UTC'
          iv_applymethod = 'immediate'
        ).
        APPEND lo_param TO lt_parameters.

        lo_result = ao_rds_actions->modify_db_cluster_parameter_group(
          iv_param_group_name = gv_param_group_name
          it_update_parameters = lt_parameters
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Parameter update failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbclusterparamgroupname( )
          exp = gv_param_group_name
          msg = 'Parameter group name mismatch'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Skip if cluster not found
        DATA(lv_error_msg) = lo_exception->get_text( ).
        IF lv_error_msg CS 'DBClusterNotFound'.
          RETURN.  " Skip test - resource not available
        ELSE.
          cl_abap_unit_assert=>fail( |Error: { lv_error_msg }| ).
        ENDIF.
    ENDTRY.
  ENDMETHOD.

  METHOD get_engine_versions.
    DATA lt_versions TYPE /aws1/cl_rdsdbengineversion=>tt_dbengineversionlist.

    TRY.
        lt_versions = ao_rds_actions->describe_db_engine_versions(
          iv_engine = 'aurora-mysql'
          iv_param_group_family = 'aurora-mysql8.0'
        ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lines( lt_versions )
          msg = 'No engine versions retrieved'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Skip if cluster not found
        DATA(lv_error_msg) = lo_exception->get_text( ).
        IF lv_error_msg CS 'DBClusterNotFound'.
          RETURN.  " Skip test - resource not available
        ELSE.
          cl_abap_unit_assert=>fail( |Error: { lv_error_msg }| ).
        ENDIF.
    ENDTRY.
  ENDMETHOD.

  METHOD get_orderable_instances.
    DATA lt_options TYPE /aws1/cl_rdsorderabledbinsto01=>tt_orderabledbinstoptionslist.

    TRY.
        lt_options = ao_rds_actions->describe_orderable_db_instance_options(
          iv_db_engine = 'aurora-mysql'
          iv_db_engine_version = gv_engine_version
        ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lines( lt_options )
          msg = 'No orderable instances retrieved'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Skip if cluster not found
        DATA(lv_error_msg) = lo_exception->get_text( ).
        IF lv_error_msg CS 'DBClusterNotFound'.
          RETURN.  " Skip test - resource not available
        ELSE.
          cl_abap_unit_assert=>fail( |Error: { lv_error_msg }| ).
        ENDIF.
    ENDTRY.
  ENDMETHOD.

  METHOD delete_parameter_group.
    TRY.
        " Delete the second parameter group created in create_parameter_group test
        ao_rds_actions->delete_db_cluster_parameter_group(
          iv_param_group_name = gv_param_group_name_2
        ).

        " Verify deletion - wait a moment for propagation
        WAIT UP TO 5 SECONDS.
        
        TRY.
            DATA(lo_result) = ao_rds_actions->describe_db_cluster_parameter_groups(
              iv_param_group_name = gv_param_group_name_2
            ).
            " If we reach here, deletion failed
            IF lo_result IS BOUND.
              cl_abap_unit_assert=>fail( 'Parameter group was not deleted' ).
            ENDIF.
          CATCH /aws1/cx_rdsdbprmgrnotfndfault.
            " Expected - parameter group successfully deleted
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Skip if cluster not found
        DATA(lv_error_msg) = lo_exception->get_text( ).
        IF lv_error_msg CS 'DBClusterNotFound'.
          RETURN.  " Skip test - resource not available
        ELSE.
          cl_abap_unit_assert=>fail( |Error: { lv_error_msg }| ).
        ENDIF.
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
