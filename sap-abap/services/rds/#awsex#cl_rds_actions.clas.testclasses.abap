" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_rds_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_rds TYPE REF TO /aws1/if_rds.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_rds_actions TYPE REF TO /awsex/cl_rds_actions.
    DATA gv_uuid TYPE string.
    DATA gv_param_group_name TYPE /aws1/rdsstring.
    DATA gv_cluster_id TYPE /aws1/rdsstring.
    DATA gv_instance_id TYPE /aws1/rdsstring.
    DATA gv_snapshot_id TYPE /aws1/rdsstring.
    DATA gv_param_group_name_2 TYPE /aws1/rdsstring.
    DATA gv_cluster_id_2 TYPE /aws1/rdsstring.
    DATA gv_instance_id_2 TYPE /aws1/rdsstring.

    METHODS class_setup RAISING /aws1/cx_rt_generic.
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

    gv_param_group_name = |abap-rds-pg-{ gv_uuid+0(8) }|.
    gv_cluster_id = |abap-rds-cl-{ gv_uuid+0(8) }|.
    gv_instance_id = |abap-rds-in-{ gv_uuid+0(8) }|.
    gv_snapshot_id = |abap-rds-sn-{ gv_uuid+0(8) }|.
    gv_param_group_name_2 = |abap-rds-pg2-{ gv_uuid+0(7) }|.
    gv_cluster_id_2 = |abap-rds-cl2-{ gv_uuid+0(7) }|.
    gv_instance_id_2 = |abap-rds-in2-{ gv_uuid+0(7) }|.

    " Create shared resources for tests with convert_test tag
    DATA lt_tags TYPE /aws1/cl_rdstag=>tt_taglist.
    DATA(lo_tag) = NEW /aws1/cl_rdstag(
      iv_key = 'convert_test'
      iv_value = 'true'
    ).
    APPEND lo_tag TO lt_tags.

    TRY.
        " Create parameter group for shared use
        DATA(lo_output) = ao_rds->createdbclusterparamgroup(
          iv_dbclusterparamgroupname = gv_param_group_name
          iv_dbparametergroupfamily = 'aurora-mysql8.0'
          iv_description = 'ABAP test parameter group'
          it_tags = lt_tags
        ).
        MESSAGE 'Created parameter group for testing' TYPE 'I'.

        " Wait a moment for parameter group to propagate
        WAIT UP TO 5 SECONDS.

        " Create DB cluster for shared use
        DATA(lo_cluster_output) = ao_rds->createdbcluster(
          iv_databasename = 'mydb'
          iv_dbclusteridentifier = gv_cluster_id
          iv_dbclusterparamgroupname = gv_param_group_name
          iv_engine = 'aurora-mysql'
          iv_engineversion = '8.0.mysql_aurora.3.02.0'
          iv_masterusername = 'admin'
          iv_masteruserpassword = 'MyS3cureP4ssw0rd!'
          it_tags = lt_tags
        ).
        MESSAGE 'Created DB cluster for testing' TYPE 'I'.

        " Wait for cluster to be available
        DATA(lv_status) = lo_cluster_output->get_dbcluster( )->get_status( ).
        DATA lv_max_wait TYPE i VALUE 180.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          DATA(lo_desc_output) = ao_rds->describedbclusters(
            iv_dbclusteridentifier = gv_cluster_id
          ).
          DATA(lt_clusters) = lo_desc_output->get_dbclusters( ).
          IF lines( lt_clusters ) > 0.
            lv_status = lt_clusters[ 1 ]->get_status( ).
          ENDIF.
        ENDWHILE.

        MESSAGE |Cluster status: { lv_status }| TYPE 'I'.

        " Create DB instance in cluster for shared use
        DATA(lo_inst_output) = ao_rds->createdbinstance(
          iv_dbinstanceidentifier = gv_instance_id
          iv_dbclusteridentifier = gv_cluster_id
          iv_engine = 'aurora-mysql'
          iv_dbinstanceclass = 'db.r5.large'
          it_tags = lt_tags
        ).
        MESSAGE 'Created DB instance for testing' TYPE 'I'.

        " Wait for instance to be available
        DATA(lv_inst_status) = lo_inst_output->get_dbinstance( )->get_dbinstancestatus( ).
        lv_waited = 0.
        lv_max_wait = 180.

        WHILE lv_inst_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          DATA(lo_inst_desc) = ao_rds->describedbinstances(
            iv_dbinstanceidentifier = gv_instance_id
          ).
          DATA(lt_instances) = lo_inst_desc->get_dbinstances( ).
          IF lines( lt_instances ) > 0.
            lv_inst_status = lt_instances[ 1 ]->get_dbinstancestatus( ).
          ENDIF.
        ENDWHILE.

        MESSAGE |Instance status: { lv_inst_status }| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        MESSAGE |Setup error: { lo_exception->get_text( ) }| TYPE 'I'.
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
            MESSAGE 'Initiated deletion of test DB instance' TYPE 'I'.
          CATCH /aws1/cx_rdsdbinstnotfndfault.
            MESSAGE 'Test DB instance not found for deletion' TYPE 'I'.
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
            MESSAGE 'Initiated deletion of test DB cluster' TYPE 'I'.
          CATCH /aws1/cx_rdsdbclustnotfndfault.
            MESSAGE 'Test DB cluster not found for deletion' TYPE 'I'.
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
            MESSAGE 'Deleted test parameter group' TYPE 'I'.
          CATCH /aws1/cx_rdsdbprmgrnotfndfault.
            MESSAGE 'Test parameter group not found for deletion' TYPE 'I'.
        ENDTRY.

        " Delete any additional parameter groups
        TRY.
            ao_rds->deletedbclusterparamgroup(
              iv_dbclusterparamgroupname = gv_param_group_name_2
            ).
          CATCH /aws1/cx_rdsdbprmgrnotfndfault.
            " OK if not found
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        MESSAGE |Teardown error: { lo_exception->get_text( ) }| TYPE 'I'.
    ENDTRY.

  ENDMETHOD.

  METHOD create_parameter_group.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbclustparamgroup.

    TRY.
        lo_result = ao_rds_actions->create_parameter_group(
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

    " Note: The main cluster is created in class_setup
    " This test creates an additional cluster for delete testing
    TRY.
        lo_result = ao_rds_actions->create_db_cluster(
          iv_cluster_name = gv_cluster_id_2
          iv_param_group_name = gv_param_group_name
          iv_db_name = 'mydb2'
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
          exp = gv_cluster_id_2
          msg = 'Cluster ID mismatch'
        ).

        " Wait for cluster to be available
        DATA(lv_status) = lo_result->get_status( ).
        DATA lv_max_wait TYPE i VALUE 180.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          lo_result = ao_rds_actions->get_db_cluster( iv_cluster_name = gv_cluster_id_2 ).
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
          iv_instance_id = gv_instance_id_2
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
          exp = gv_instance_id_2
          msg = 'Instance ID mismatch'
        ).

        " Wait for instance to be available
        DATA(lv_status) = lo_result->get_dbinstancestatus( ).
        DATA lv_max_wait TYPE i VALUE 180.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          lo_result = ao_rds_actions->get_db_instance( iv_instance_id = gv_instance_id_2 ).
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
        DATA lv_max_wait TYPE i VALUE 180.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          lo_result = ao_rds_actions->get_cluster_snapshot( iv_snapshot_id = gv_snapshot_id ).
          lv_status = lo_result->get_status( ).
        ENDWHILE.

        " Delete snapshot after test to clean up
        TRY.
            ao_rds->deletedbclustersnapshot(
              iv_dbclustersnapshotid = gv_snapshot_id
            ).
          CATCH /aws1/cx_rt_generic.
            " Ignore deletion errors
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_cluster_snapshot.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbclustersnapshot.

    " First create a snapshot to retrieve
    TRY.
        DATA(lv_snapshot_id) = |abap-snap-{ gv_uuid+0(8) }|.
        DATA(lo_create) = ao_rds->createdbclustersnapshot(
          iv_dbclustersnapshotid = lv_snapshot_id
          iv_dbclusteridentifier = gv_cluster_id
        ).

        " Wait for snapshot to be available
        DATA(lv_status) = lo_create->get_dbclustersnapshot( )->get_status( ).
        DATA lv_max_wait TYPE i VALUE 180.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          lo_result = ao_rds_actions->get_cluster_snapshot( iv_snapshot_id = lv_snapshot_id ).
          lv_status = lo_result->get_status( ).
        ENDWHILE.

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Cluster snapshot retrieval failed'
        ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_dbclustersnapshotid( )
          exp = lv_snapshot_id
          msg = 'Snapshot ID mismatch'
        ).

        " Clean up snapshot
        TRY.
            ao_rds->deletedbclustersnapshot(
              iv_dbclustersnapshotid = lv_snapshot_id
            ).
          CATCH /aws1/cx_rt_generic.
            " Ignore deletion errors
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_cluster_snapshot.
    " This test verifies deletion using the snapshot created in create_cluster_snapshot
    " Since create_cluster_snapshot already cleans up, this test creates its own snapshot
    TRY.
        DATA(lv_snapshot_id) = |abap-delsn-{ gv_uuid+0(7) }|.
        
        " Create a snapshot to delete
        DATA(lo_create) = ao_rds->createdbclustersnapshot(
          iv_dbclustersnapshotid = lv_snapshot_id
          iv_dbclusteridentifier = gv_cluster_id
        ).

        " Wait for snapshot to be available
        DATA(lv_status) = lo_create->get_dbclustersnapshot( )->get_status( ).
        DATA lv_max_wait TYPE i VALUE 180.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_status <> 'available' AND lv_waited < lv_max_wait.
          WAIT UP TO 30 SECONDS.
          lv_waited = lv_waited + 30.
          DATA(lo_result) = ao_rds_actions->get_cluster_snapshot( iv_snapshot_id = lv_snapshot_id ).
          lv_status = lo_result->get_status( ).
        ENDWHILE.

        " Delete the snapshot
        ao_rds->deletedbclustersnapshot(
          iv_dbclustersnapshotid = lv_snapshot_id
        ).

        " Wait for snapshot deletion
        DATA lv_exists TYPE abap_bool VALUE abap_true.
        lv_max_wait = 60.
        lv_waited = 0.

        WHILE lv_exists = abap_true AND lv_waited < lv_max_wait.
          WAIT UP TO 10 SECONDS.
          lv_waited = lv_waited + 10.
          TRY.
              ao_rds_actions->get_cluster_snapshot( iv_snapshot_id = lv_snapshot_id ).
            CATCH /aws1/cx_rdsdbclstsnapnotfnd00.
              lv_exists = abap_false.
          ENDTRY.
        ENDWHILE.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_db_instance.
    DATA lo_result TYPE REF TO /aws1/cl_rdsdbinstance.

    TRY.
        " Delete the second instance created in create_instance_cluster test
        lo_result = ao_rds_actions->delete_db_instance(
          iv_instance_id = gv_instance_id_2
        ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'DB instance deletion failed'
        ).

        " Wait for instance deletion to start
        DATA lv_exists TYPE abap_bool VALUE abap_true.
        DATA lv_max_wait TYPE i VALUE 60.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_exists = abap_true AND lv_waited < lv_max_wait.
          WAIT UP TO 10 SECONDS.
          lv_waited = lv_waited + 10.
          TRY.
              lo_result = ao_rds_actions->get_db_instance( iv_instance_id = gv_instance_id_2 ).
              DATA(lv_status) = lo_result->get_dbinstancestatus( ).
              " Check if status is 'deleting'
              IF lv_status = 'deleting'.
                lv_exists = abap_false.
              ENDIF.
            CATCH /aws1/cx_rdsdbinstnotfndfault.
              lv_exists = abap_false.
          ENDTRY.
        ENDWHILE.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_db_cluster.
    TRY.
        " Delete the second cluster created in create_db_cluster test
        ao_rds_actions->delete_db_cluster(
          iv_cluster_name = gv_cluster_id_2
        ).

        " Wait for cluster deletion to start
        DATA lv_exists TYPE abap_bool VALUE abap_true.
        DATA lv_max_wait TYPE i VALUE 60.
        DATA lv_waited TYPE i VALUE 0.

        WHILE lv_exists = abap_true AND lv_waited < lv_max_wait.
          WAIT UP TO 10 SECONDS.
          lv_waited = lv_waited + 10.
          TRY.
              DATA(lo_result) = ao_rds_actions->get_db_cluster( iv_cluster_name = gv_cluster_id_2 ).
              DATA(lv_status) = lo_result->get_status( ).
              " Check if status is 'deleting'
              IF lv_status = 'deleting'.
                lv_exists = abap_false.
              ENDIF.
            CATCH /aws1/cx_rdsdbclustnotfndfault.
              lv_exists = abap_false.
          ENDTRY.
        ENDWHILE.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_parameter_group.
    TRY.
        " Delete the second parameter group created in create_parameter_group test
        ao_rds_actions->delete_parameter_group(
          iv_param_group_name = gv_param_group_name_2
        ).

        " Verify deletion - wait a moment for propagation
        WAIT UP TO 5 SECONDS.
        
        TRY.
            DATA(lo_result) = ao_rds_actions->get_parameter_group(
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
        cl_abap_unit_assert=>fail( |Error: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
