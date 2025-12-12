" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_rds_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_rds TYPE REF TO /aws1/if_rds.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_rds_actions TYPE REF TO /awsex/cl_rds_actions.
    DATA gv_uuid TYPE sysuuid_c36.
    DATA gv_param_group_name TYPE /aws1/rdsstring.
    DATA gv_cluster_id TYPE /aws1/rdsstring.
    DATA gv_instance_id TYPE /aws1/rdsstring.
    DATA gv_snapshot_id TYPE /aws1/rdsstring.

    METHODS class_setup RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS class_teardown RAISING /aws1/cx_rt_generic.
    METHODS create_parameter_group FOR TESTING.
    METHODS get_parameter_group FOR TESTING.
    METHODS get_parameters FOR TESTING.
    METHODS update_parameters FOR TESTING.
    METHODS get_engine_versions FOR TESTING.
    METHODS create_db_cluster FOR TESTING.
    METHODS get_db_cluster FOR TESTING.
    METHODS get_orderable_instances FOR TESTING.
    METHODS create_instance_cluster FOR TESTING.
    METHODS get_db_instance FOR TESTING.
    METHODS create_cluster_snapshot FOR TESTING.
    METHODS get_cluster_snapshot FOR TESTING.
    METHODS delete_db_instance FOR TESTING.
    METHODS delete_cluster_snapshot FOR TESTING.
    METHODS delete_db_cluster FOR TESTING.
    METHODS delete_parameter_group FOR TESTING.

ENDCLASS.

CLASS ltc_awsex_cl_rds_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_rds = /aws1/cl_rds_factory=>create( ao_session ).
    ao_rds_actions = NEW /awsex/cl_rds_actions( ).

    " Generate unique identifiers using UUID
    gv_uuid = /awsex/cl_utl=>uuid_get_c36( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = gv_uuid.

    gv_param_group_name = |abap-rds-pg-{ lv_uuid_string+0(8) }|.
    gv_cluster_id = |abap-rds-cl-{ lv_uuid_string+0(8) }|.
    gv_instance_id = |abap-rds-in-{ lv_uuid_string+0(8) }|.
    gv_snapshot_id = |abap-rds-sn-{ lv_uuid_string+0(8) }|.
  ENDMETHOD.

  METHOD class_teardown.
    " Cleanup is performed in individual test methods
  ENDMETHOD.

  METHOD create_parameter_group.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbclustparamgroup.

    TRY.
        lo_result = ao_rds_actions->create_parameter_group(
          iv_param_group_name = gv_param_group_name
          iv_param_group_family = 'aurora-mysql8.0'
          iv_description = 'ABAP test parameter group'
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Parameter group creation failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbclusterparamgroupname( )
          exp = gv_param_group_name
          msg = 'Parameter group name mismatch'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_parameter_group.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbclustparamgroup.

    TRY.
        lo_result = ao_rds_actions->get_parameter_group(
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
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_parameters.
    DATA lt_parameters TYPE /aws1/cl_rdsparameter=>tt_parameterslist.

    TRY.
        lt_parameters = ao_rds_actions->get_parameters(
          iv_param_group_name = gv_param_group_name
          iv_source = 'engine-default'
        ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lines( lt_parameters )
          msg = 'No parameters retrieved'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
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

        lo_result = ao_rds_actions->update_parameters(
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
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_engine_versions.
    DATA lt_versions TYPE /aws1/cl_rdsdbengineversion=>tt_dbengineversionlist.

    TRY.
        lt_versions = ao_rds_actions->get_engine_versions(
          iv_engine = 'aurora-mysql'
          iv_param_group_family = 'aurora-mysql8.0'
        ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lines( lt_versions )
          msg = 'No engine versions retrieved'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD create_db_cluster.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbcluster.

    TRY.
        lo_result = ao_rds_actions->create_db_cluster(
          iv_cluster_name = gv_cluster_id
          iv_param_group_name = gv_param_group_name
          iv_db_name = 'mydb'
          iv_db_engine = 'aurora-mysql'
          iv_db_engine_version = '8.0.mysql_aurora.3.02.0'
          iv_admin_name = 'admin'
          iv_admin_password = 'MyS3cureP4ssw0rd!'
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'DB cluster creation failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbclusteridentifier( )
          exp = gv_cluster_id
          msg = 'Cluster ID mismatch'
        ).

        " Wait for cluster to be available
        DATA(lv_status) = lo_result->get_status( ).
        DATA lv_max_wait TYPE i VALUE 60.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          lo_result = ao_rds_actions->get_db_cluster( iv_cluster_name = gv_cluster_id ).
          lv_status = lo_result->get_status( ).
        ENDWHILE.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_db_cluster.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbcluster.

    TRY.
        lo_result = ao_rds_actions->get_db_cluster(
          iv_cluster_name = gv_cluster_id
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'DB cluster retrieval failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbclusteridentifier( )
          exp = gv_cluster_id
          msg = 'Cluster ID mismatch'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_orderable_instances.
    DATA lt_options TYPE /aws1/cl_rdsorderabledbinsto01=>tt_orderabledbinstoptionslist.

    TRY.
        lt_options = ao_rds_actions->get_orderable_instances(
          iv_db_engine = 'aurora-mysql'
          iv_db_engine_version = '8.0.mysql_aurora.3.02.0'
        ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lines( lt_options )
          msg = 'No orderable instances retrieved'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD create_instance_cluster.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbinstance.

    TRY.
        lo_result = ao_rds_actions->create_instance_cluster(
          iv_instance_id = gv_instance_id
          iv_cluster_id = gv_cluster_id
          iv_db_engine = 'aurora-mysql'
          iv_instance_class = 'db.r5.large'
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'DB instance creation failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbinstanceidentifier( )
          exp = gv_instance_id
          msg = 'Instance ID mismatch'
        ).

        " Wait for instance to be available
        DATA(lv_status) = lo_result->get_dbinstancestatus( ).
        DATA lv_max_wait TYPE i VALUE 120.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          lo_result = ao_rds_actions->get_db_instance( iv_instance_id = gv_instance_id ).
          lv_status = lo_result->get_dbinstancestatus( ).
        ENDWHILE.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_db_instance.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbinstance.

    TRY.
        lo_result = ao_rds_actions->get_db_instance(
          iv_instance_id = gv_instance_id
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'DB instance retrieval failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbinstanceidentifier( )
          exp = gv_instance_id
          msg = 'Instance ID mismatch'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD create_cluster_snapshot.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbclustersnapshot.

    TRY.
        lo_result = ao_rds_actions->create_cluster_snapshot(
          iv_snapshot_id = gv_snapshot_id
          iv_cluster_id = gv_cluster_id
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Cluster snapshot creation failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbclustersnapshotid( )
          exp = gv_snapshot_id
          msg = 'Snapshot ID mismatch'
        ).

        " Wait for snapshot to be available
        DATA(lv_status) = lo_result->get_status( ).
        DATA lv_max_wait TYPE i VALUE 120.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          lo_result = ao_rds_actions->get_cluster_snapshot( iv_snapshot_id = gv_snapshot_id ).
          lv_status = lo_result->get_status( ).
        ENDWHILE.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_cluster_snapshot.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbclustersnapshot.

    TRY.
        lo_result = ao_rds_actions->get_cluster_snapshot(
          iv_snapshot_id = gv_snapshot_id
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Cluster snapshot retrieval failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbclustersnapshotid( )
          exp = gv_snapshot_id
          msg = 'Snapshot ID mismatch'
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_cluster_snapshot.
    TRY.
        " Delete the snapshot
        ao_rds->deletedbclustersnapshot(
          iv_dbclustersnapshotid = gv_snapshot_id
        ).

        " Wait for snapshot deletion
        DATA lv_exists TYPE abap_bool VALUE abap_true.
        DATA lv_max_wait TYPE i VALUE 60.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_exists = abap_true AND lv_waited < lv_max_wait.
          WAIT UP TO 10 SECONDS.
          lv_waited = lv_waited + 10.
          TRY.
              ao_rds_actions->get_cluster_snapshot( iv_snapshot_id = gv_snapshot_id ).
            CATCH /aws1/cx_rdsdbclstsnapnotfnd00.
              lv_exists = abap_false.
          ENDTRY.
        ENDWHILE.

      CATCH /aws1/cx_rdsdbclstsnapnotfnd00.
        " Snapshot already deleted - OK
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_db_instance.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbinstance.

    TRY.
        lo_result = ao_rds_actions->delete_db_instance(
          iv_instance_id = gv_instance_id
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'DB instance deletion failed'
        ).

        " Wait for instance deletion
        DATA lv_exists TYPE abap_bool VALUE abap_true.
        DATA lv_max_wait TYPE i VALUE 120.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_exists = abap_true AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          TRY.
              ao_rds_actions->get_db_instance( iv_instance_id = gv_instance_id ).
            CATCH /aws1/cx_rdsdbinstnotfndfault.
              lv_exists = abap_false.
          ENDTRY.
        ENDWHILE.

      CATCH /aws1/cx_rdsdbinstnotfndfault.
        " Instance already deleted - OK
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_db_cluster.
    TRY.
        ao_rds_actions->delete_db_cluster(
          iv_cluster_name = gv_cluster_id
        ).

        " Wait for cluster deletion
        DATA lv_exists TYPE abap_bool VALUE abap_true.
        DATA lv_max_wait TYPE i VALUE 120.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_exists = abap_true AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          TRY.
              ao_rds_actions->get_db_cluster( iv_cluster_name = gv_cluster_id ).
            CATCH /aws1/cx_rdsdbclustnotfndfault.
              lv_exists = abap_false.
          ENDTRY.
        ENDWHILE.

      CATCH /aws1/cx_rdsdbclustnotfndfault.
        " Cluster already deleted - OK
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_parameter_group.
    TRY.
        ao_rds_actions->delete_parameter_group(
          iv_param_group_name = gv_param_group_name
        ).

        " Verify deletion
        DATA(lo_result) = ao_rds_actions->get_parameter_group(
          iv_param_group_name = gv_param_group_name
        ).

        " If we reach here, deletion failed
        IF lo_result IS BOUND.
          cl_abap_unit_assert=>fail( 'Parameter group was not deleted' ).
        ENDIF.

      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        " Parameter group successfully deleted
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
