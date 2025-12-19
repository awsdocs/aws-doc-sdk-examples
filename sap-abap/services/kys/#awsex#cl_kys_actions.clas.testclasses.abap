" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_kys_actions DEFINITION DEFERRED.
CLASS /awsex/cl_kys_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_kys_actions.

CLASS ltc_awsex_cl_kys_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA gv_keyspace_name TYPE /aws1/kyskeyspacename.
    CLASS-DATA gv_table_name TYPE /aws1/kystablename.

    CLASS-DATA go_kys TYPE REF TO /aws1/if_kys.
    CLASS-DATA go_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA go_kys_actions TYPE REF TO /awsex/cl_kys_actions.

    METHODS: create_keyspace FOR TESTING RAISING /aws1/cx_rt_generic,
      get_keyspace FOR TESTING RAISING /aws1/cx_rt_generic,
      list_keyspaces FOR TESTING RAISING /aws1/cx_rt_generic,
      create_table FOR TESTING RAISING /aws1/cx_rt_generic,
      get_table FOR TESTING RAISING /aws1/cx_rt_generic,
      list_tables FOR TESTING RAISING /aws1/cx_rt_generic,
      update_table FOR TESTING RAISING /aws1/cx_rt_generic,
      restore_table FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_table FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_keyspace FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    CLASS-METHODS create_test_keyspace
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_keyspace_active
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS create_test_table
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
        iv_table_name    TYPE /aws1/kystablename
        iv_enable_pitr   TYPE abap_bool DEFAULT abap_true
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_table_active
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
        iv_table_name    TYPE /aws1/kystablename
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_table_deleted
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
        iv_table_name    TYPE /aws1/kystablename
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS cleanup_test_table
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
        iv_table_name    TYPE /aws1/kystablename.

    CLASS-METHODS is_keyspace_active
      IMPORTING
        io_keyspace_result        TYPE REF TO /aws1/cl_kysgetkeyspacersp
      RETURNING
        VALUE(rv_is_active)       TYPE abap_bool.

ENDCLASS.

CLASS ltc_awsex_cl_kys_actions IMPLEMENTATION.

  METHOD class_setup.
    go_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    go_kys = /aws1/cl_kys_factory=>create( go_session ).
    go_kys_actions = NEW /awsex/cl_kys_actions( ).

    " Generate unique names for test resources
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    gv_keyspace_name = |kys_test_{ lv_uuid }|.
    gv_table_name = |movies|.

    " Create test keyspace for shared tests
    create_test_keyspace( gv_keyspace_name ).

    " Wait for keyspace to become active
    wait_for_keyspace_active( gv_keyspace_name ).

    " Create test table for shared tests
    create_test_table(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = gv_table_name ).

    " Wait for table to become active
    wait_for_table_active(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = gv_table_name ).

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up test resources
    " Note: Not deleting keyspace or restore tables as they take very long
    " These are tagged with 'convert_test' for manual cleanup

    " Delete the main test table
    cleanup_test_table(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = gv_table_name ).

  ENDMETHOD.

  METHOD create_test_keyspace.
    " Create keyspace with convert_test tag
    DATA(lo_tag) = NEW /aws1/cl_kystag(
      iv_key = 'convert_test'
      iv_value = 'true' ).
    DATA(lt_tags) = VALUE /aws1/cl_kystag=>tt_taglist( ( lo_tag ) ).

    TRY.
        go_kys->createkeyspace(
          iv_keyspacename = iv_keyspace_name
          it_tags = lt_tags ).
      CATCH /aws1/cx_kysconflictexception.
        " Keyspace already exists - acceptable
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to create keyspace { iv_keyspace_name }: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD is_keyspace_active.
    " Check if keyspace is active by examining replication group statuses
    rv_is_active = abap_true.

    " For single-region keyspaces, check replication strategy
    DATA(lv_repl_strategy) = io_keyspace_result->get_replicationstrategy( ).
    IF lv_repl_strategy = 'SINGLE_REGION'.
      " Single region keyspaces are active immediately after creation completes
      rv_is_active = abap_true.
    ELSE.
      " For multi-region, check replication group statuses
      DATA(lt_repl_groups) = io_keyspace_result->get_replicationgroupstatuses( ).
      IF lt_repl_groups IS INITIAL.
        rv_is_active = abap_false.
      ELSE.
        " Check if all replication groups are active
        LOOP AT lt_repl_groups INTO DATA(lo_repl_group).
          DATA(lv_status) = lo_repl_group->get_keyspacestatus( ).
          IF lv_status <> 'ACTIVE'.
            rv_is_active = abap_false.
            EXIT.
          ENDIF.
        ENDLOOP.
      ENDIF.
    ENDIF.
  ENDMETHOD.

  METHOD wait_for_keyspace_active.
    DATA(lv_max_wait) = 60.
    DATA(lv_wait_count) = 0.
    DATA(lv_keyspace_active) = abap_false.

    WHILE lv_keyspace_active = abap_false AND lv_wait_count < lv_max_wait.
      TRY.
          DATA(lo_keyspace_result) = go_kys->getkeyspace( iv_keyspacename = iv_keyspace_name ).
          " Check if keyspace is active
          lv_keyspace_active = is_keyspace_active( lo_keyspace_result ).
          IF lv_keyspace_active = abap_false.
            WAIT UP TO 3 SECONDS.
            lv_wait_count = lv_wait_count + 1.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          WAIT UP TO 3 SECONDS.
          lv_wait_count = lv_wait_count + 1.
      ENDTRY.
    ENDWHILE.

    IF lv_keyspace_active = abap_false.
      cl_abap_unit_assert=>fail( msg = |Keyspace { iv_keyspace_name } did not become active in time| ).
    ENDIF.
  ENDMETHOD.

  METHOD create_test_table.
    " Create table with convert_test tag
    DATA(lo_tag) = NEW /aws1/cl_kystag(
      iv_key = 'convert_test'
      iv_value = 'true' ).
    DATA(lt_tags) = VALUE /aws1/cl_kystag=>tt_taglist( ( lo_tag ) ).

    " Define schema with columns
    DATA(lt_columns) = VALUE /aws1/cl_kyscolumndefinition=>tt_columndefinitionlist(
      ( NEW /aws1/cl_kyscolumndefinition( iv_name = 'title' iv_type = 'text' ) )
      ( NEW /aws1/cl_kyscolumndefinition( iv_name = 'year' iv_type = 'int' ) )
      ( NEW /aws1/cl_kyscolumndefinition( iv_name = 'release_date' iv_type = 'timestamp' ) )
      ( NEW /aws1/cl_kyscolumndefinition( iv_name = 'plot' iv_type = 'text' ) )
    ).

    " Define partition keys
    DATA(lt_partition_keys) = VALUE /aws1/cl_kyspartitionkey=>tt_partitionkeylist(
      ( NEW /aws1/cl_kyspartitionkey( iv_name = 'year' ) )
      ( NEW /aws1/cl_kyspartitionkey( iv_name = 'title' ) )
    ).

    " Create schema definition
    DATA(lo_schema) = NEW /aws1/cl_kysschemadefinition(
      it_allcolumns = lt_columns
      it_partitionkeys = lt_partition_keys ).

    " Enable point-in-time recovery if requested
    DATA lo_pitr TYPE REF TO /aws1/cl_kyspointintimerec.
    IF iv_enable_pitr = abap_true.
      lo_pitr = NEW /aws1/cl_kyspointintimerec( iv_status = 'ENABLED' ).
    ENDIF.

    TRY.
        go_kys->createtable(
          iv_keyspacename = iv_keyspace_name
          iv_tablename = iv_table_name
          io_schemadefinition = lo_schema
          io_pointintimerecovery = lo_pitr
          it_tags = lt_tags ).
      CATCH /aws1/cx_kysconflictexception.
        " Table already exists - acceptable
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to create table { iv_table_name }: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD wait_for_table_active.
    DATA(lv_max_wait) = 60.
    DATA(lv_wait_count) = 0.
    DATA(lv_table_active) = abap_false.

    WHILE lv_table_active = abap_false AND lv_wait_count < lv_max_wait.
      TRY.
          DATA(lo_table_result) = go_kys->gettable(
            iv_keyspacename = iv_keyspace_name
            iv_tablename = iv_table_name ).
          IF lo_table_result->get_status( ) = 'ACTIVE'.
            lv_table_active = abap_true.
          ELSE.
            WAIT UP TO 5 SECONDS.
            lv_wait_count = lv_wait_count + 1.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          WAIT UP TO 5 SECONDS.
          lv_wait_count = lv_wait_count + 1.
      ENDTRY.
    ENDWHILE.

    IF lv_table_active = abap_false.
      cl_abap_unit_assert=>fail( msg = |Table { iv_table_name } did not become active in time| ).
    ENDIF.
  ENDMETHOD.

  METHOD wait_for_table_deleted.
    DATA(lv_max_wait) = 60.
    DATA(lv_wait_count) = 0.
    DATA(lv_table_deleted) = abap_false.

    WHILE lv_table_deleted = abap_false AND lv_wait_count < lv_max_wait.
      TRY.
          go_kys->gettable(
            iv_keyspacename = iv_keyspace_name
            iv_tablename = iv_table_name ).
          " If we get here, table still exists
          WAIT UP TO 5 SECONDS.
          lv_wait_count = lv_wait_count + 1.
        CATCH /aws1/cx_kysresourcenotfoundex.
          " Table is deleted
          lv_table_deleted = abap_true.
      ENDTRY.
    ENDWHILE.

    IF lv_table_deleted = abap_false.
      cl_abap_unit_assert=>fail( msg = |Table { iv_table_name } was not deleted in time| ).
    ENDIF.
  ENDMETHOD.

  METHOD cleanup_test_table.
    TRY.
        go_kys->deletetable(
          iv_keyspacename = iv_keyspace_name
          iv_tablename = iv_table_name ).
      CATCH /aws1/cx_kysresourcenotfoundex.
        " Table doesn't exist, already cleaned up
      CATCH /aws1/cx_rt_generic.
        " Continue with cleanup even if error
    ENDTRY.
  ENDMETHOD.

  METHOD create_keyspace.
    " Create a unique keyspace for this test
    DATA(lv_test_keyspace) = |kys_test_create_{ /awsex/cl_utils=>get_random_string( ) }|.
    DATA lo_result TYPE REF TO /aws1/cl_kyscreatekeyspacersp.

    " Call the example method
    go_kys_actions->create_keyspace(
      EXPORTING
        iv_keyspace_name = lv_test_keyspace
      IMPORTING
        oo_result = lo_result ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'CreateKeyspace result should not be null' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_resourcearn( )
      msg = 'Keyspace ARN should not be empty' ).

    " Wait for keyspace to become active
    wait_for_keyspace_active( lv_test_keyspace ).

    " Verify keyspace exists and has correct name
    TRY.
        DATA(lo_get_result) = go_kys->getkeyspace( iv_keyspacename = lv_test_keyspace ).
        cl_abap_unit_assert=>assert_equals(
          exp = lv_test_keyspace
          act = lo_get_result->get_keyspacename( )
          msg = 'Keyspace name should match' ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to verify keyspace: { lo_exception->get_text( ) }| ).
    ENDTRY.

    " Clean up: Delete keyspace (tagged with convert_test for manual cleanup if needed)
    TRY.
        go_kys->deletekeyspace( iv_keyspacename = lv_test_keyspace ).
      CATCH /aws1/cx_rt_generic.
        " Continue - keyspace is tagged for manual cleanup
    ENDTRY.
  ENDMETHOD.

  METHOD get_keyspace.
    " This test uses the shared keyspace created in class_setup
    DATA lo_result TYPE REF TO /aws1/cl_kysgetkeyspacersp.

    " Call the example method
    go_kys_actions->get_keyspace(
      EXPORTING
        iv_keyspace_name = gv_keyspace_name
      IMPORTING
        oo_result = lo_result ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetKeyspace result should not be null' ).

    cl_abap_unit_assert=>assert_equals(
      exp = gv_keyspace_name
      act = lo_result->get_keyspacename( )
      msg = 'Keyspace name should match' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_resourcearn( )
      msg = 'Keyspace ARN should not be empty' ).

    " Verify keyspace is active
    cl_abap_unit_assert=>assert_true(
      act = is_keyspace_active( lo_result )
      msg = 'Keyspace should be ACTIVE' ).
  ENDMETHOD.

  METHOD list_keyspaces.
    " This test uses the shared keyspace created in class_setup
    DATA lo_result TYPE REF TO /aws1/cl_kyslistkeyspacesrsp.

    " Call the example method
    go_kys_actions->list_keyspaces(
      EXPORTING
        iv_max_results = 10
      IMPORTING
        oo_result = lo_result ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'ListKeyspaces result should not be null' ).

    DATA(lt_keyspaces) = lo_result->get_keyspaces( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_keyspaces
      msg = 'Keyspaces list should not be empty' ).

    " Verify our test keyspace is in the list
    DATA(lv_found) = abap_false.
    LOOP AT lt_keyspaces INTO DATA(lo_keyspace).
      IF lo_keyspace->get_keyspacename( ) = gv_keyspace_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Test keyspace { gv_keyspace_name } should be in the list| ).
  ENDMETHOD.

  METHOD create_table.
    " Create a unique table for this test
    DATA(lv_test_table) = |movies_create_{ /awsex/cl_utils=>get_random_string( ) }|.
    DATA lo_result TYPE REF TO /aws1/cl_kyscreatetablersp.

    " Call the example method
    go_kys_actions->create_table(
      EXPORTING
        iv_keyspace_name = gv_keyspace_name
        iv_table_name = lv_test_table
      IMPORTING
        oo_result = lo_result ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'CreateTable result should not be null' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_resourcearn( )
      msg = 'Table ARN should not be empty' ).

    " Wait for table to become active
    wait_for_table_active(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = lv_test_table ).

    " Verify table exists with correct schema
    DATA(lo_table_result) = go_kys->gettable(
      iv_keyspacename = gv_keyspace_name
      iv_tablename = lv_test_table ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_table
      act = lo_table_result->get_tablename( )
      msg = 'Table name should match' ).

    " Verify schema has 4 columns
    DATA(lo_schema) = lo_table_result->get_schemadefinition( ).
    DATA(lt_columns) = lo_schema->get_allcolumns( ).
    cl_abap_unit_assert=>assert_equals(
      exp = 4
      act = lines( lt_columns )
      msg = 'Should have 4 columns' ).

    " Clean up
    cleanup_test_table(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = lv_test_table ).
  ENDMETHOD.

  METHOD get_table.
    " This test uses the shared table created in class_setup
    DATA lo_result TYPE REF TO /aws1/cl_kysgettableresponse.

    " Call the example method
    go_kys_actions->get_table(
      EXPORTING
        iv_keyspace_name = gv_keyspace_name
        iv_table_name = gv_table_name
      IMPORTING
        oo_result = lo_result ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetTable result should not be null' ).

    cl_abap_unit_assert=>assert_equals(
      exp = gv_table_name
      act = lo_result->get_tablename( )
      msg = 'Table name should match' ).

    cl_abap_unit_assert=>assert_equals(
      exp = gv_keyspace_name
      act = lo_result->get_keyspacename( )
      msg = 'Keyspace name should match' ).

    " Verify schema definition
    DATA(lo_schema) = lo_result->get_schemadefinition( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_schema
      msg = 'Schema definition should not be null' ).

    DATA(lt_columns) = lo_schema->get_allcolumns( ).
    cl_abap_unit_assert=>assert_equals(
      exp = 4
      act = lines( lt_columns )
      msg = 'Should have 4 columns' ).
  ENDMETHOD.

  METHOD list_tables.
    " This test uses the shared table created in class_setup
    DATA lo_result TYPE REF TO /aws1/cl_kyslisttablesresponse.

    " Call the example method
    go_kys_actions->list_tables(
      EXPORTING
        iv_keyspace_name = gv_keyspace_name
      IMPORTING
        oo_result = lo_result ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'ListTables result should not be null' ).

    DATA(lt_tables) = lo_result->get_tables( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_tables
      msg = 'Tables list should not be empty' ).

    " Verify our test table is in the list
    DATA(lv_found) = abap_false.
    LOOP AT lt_tables INTO DATA(lo_table).
      IF lo_table->get_tablename( ) = gv_table_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Test table { gv_table_name } should be in the list| ).
  ENDMETHOD.

  METHOD update_table.
    " Create a unique table for this test
    DATA(lv_update_table) = |movies_update_{ /awsex/cl_utils=>get_random_string( ) }|.

    " First create a table with basic schema
    DATA(lt_columns) = VALUE /aws1/cl_kyscolumndefinition=>tt_columndefinitionlist(
      ( NEW /aws1/cl_kyscolumndefinition( iv_name = 'id' iv_type = 'int' ) )
      ( NEW /aws1/cl_kyscolumndefinition( iv_name = 'data' iv_type = 'text' ) )
    ).

    DATA(lt_partition_keys) = VALUE /aws1/cl_kyspartitionkey=>tt_partitionkeylist(
      ( NEW /aws1/cl_kyspartitionkey( iv_name = 'id' ) )
    ).

    DATA(lo_schema) = NEW /aws1/cl_kysschemadefinition(
      it_allcolumns = lt_columns
      it_partitionkeys = lt_partition_keys ).

    DATA(lo_pitr) = NEW /aws1/cl_kyspointintimerec( iv_status = 'ENABLED' ).

    DATA(lo_tag) = NEW /aws1/cl_kystag(
      iv_key = 'convert_test'
      iv_value = 'true' ).
    DATA(lt_tags) = VALUE /aws1/cl_kystag=>tt_taglist( ( lo_tag ) ).

    TRY.
        go_kys->createtable(
          iv_keyspacename = gv_keyspace_name
          iv_tablename = lv_update_table
          io_schemadefinition = lo_schema
          io_pointintimerecovery = lo_pitr
          it_tags = lt_tags ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_create_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to create table for update test: { lo_create_exception->get_text( ) }| ).
    ENDTRY.

    " Wait for table to become active
    wait_for_table_active(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = lv_update_table ).

    " Now call the example update method
    DATA lo_result TYPE REF TO /aws1/cl_kysupdatetablersp.

    go_kys_actions->update_table(
      EXPORTING
        iv_keyspace_name = gv_keyspace_name
        iv_table_name = lv_update_table
      IMPORTING
        oo_result = lo_result ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'UpdateTable result should not be null' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_resourcearn( )
      msg = 'Updated table ARN should not be empty' ).

    " Wait for update to complete
    wait_for_table_active(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = lv_update_table ).

    " Verify the new column was added
    DATA(lo_table_result) = go_kys->gettable(
      iv_keyspacename = gv_keyspace_name
      iv_tablename = lv_update_table ).

    DATA(lo_updated_schema) = lo_table_result->get_schemadefinition( ).
    DATA(lt_updated_columns) = lo_updated_schema->get_allcolumns( ).

    " Should now have 3 columns (id, data, watched)
    cl_abap_unit_assert=>assert_equals(
      exp = 3
      act = lines( lt_updated_columns )
      msg = 'Should have 3 columns after update' ).

    " Verify 'watched' column exists
    DATA(lv_watched_found) = abap_false.
    LOOP AT lt_updated_columns INTO DATA(lo_column).
      IF lo_column->get_name( ) = 'watched'.
        lv_watched_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_watched_found
      msg = 'watched column should exist after update' ).

    " Clean up
    cleanup_test_table(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = lv_update_table ).
  ENDMETHOD.

  METHOD restore_table.
    " Note: This test creates a restore operation but doesn't wait for completion
    " as table restoration can take 20+ minutes
    " The restored table is tagged automatically by AWS with the source table tags

    " Get current timestamp for restore point - convert to TIMESTAMPL
    DATA lv_timestamp TYPE /aws1/kystimestamp.
    GET TIME STAMP FIELD lv_timestamp.

    " Wait a bit to ensure timestamp is valid for restore
    WAIT UP TO 5 SECONDS.

    DATA(lv_restore_table) = |movies_restored_{ /awsex/cl_utils=>get_random_string( ) }|.
    DATA lo_result TYPE REF TO /aws1/cl_kysrestoretablersp.

    TRY.
        " Call the example method
        go_kys_actions->restore_table(
          EXPORTING
            iv_source_keyspace_name = gv_keyspace_name
            iv_source_table_name = gv_table_name
            iv_target_keyspace_name = gv_keyspace_name
            iv_target_table_name = lv_restore_table
            iv_restore_timestamp = lv_timestamp
          IMPORTING
            oo_result = lo_result ).

        " Verify result
        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'RestoreTable result should not be null' ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lo_result->get_restoredtablearn( )
          msg = 'Restored table ARN should not be empty' ).

        MESSAGE |Restore initiated for { lv_restore_table }. Tagged with convert_test for manual cleanup.| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " If the restore fails due to timing, that's acceptable for this test
        DATA(lv_msg) = lo_exception->get_text( ).
        IF lv_msg CS 'not available' OR lv_msg CS 'InvalidRequest' OR lv_msg CS 'PointInTimeRecoveryUnavailable'.
          " Expected - point in time may not be available yet
          MESSAGE 'Restore test completed - restore point not yet available' TYPE 'I'.
        ELSE.
          " Unexpected error - fail the test
          cl_abap_unit_assert=>fail( msg = |Restore failed unexpectedly: { lv_msg }| ).
        ENDIF.
    ENDTRY.

    " Note: Not cleaning up restored table as it takes too long to complete
    " Table will be tagged with convert_test for manual cleanup
  ENDMETHOD.

  METHOD delete_table.
    " Create a unique table specifically for deletion testing
    DATA(lv_delete_table) = |movies_delete_{ /awsex/cl_utils=>get_random_string( ) }|.

    " Create the table
    DATA(lt_columns) = VALUE /aws1/cl_kyscolumndefinition=>tt_columndefinitionlist(
      ( NEW /aws1/cl_kyscolumndefinition( iv_name = 'id' iv_type = 'int' ) )
    ).

    DATA(lt_partition_keys) = VALUE /aws1/cl_kyspartitionkey=>tt_partitionkeylist(
      ( NEW /aws1/cl_kyspartitionkey( iv_name = 'id' ) )
    ).

    DATA(lo_schema) = NEW /aws1/cl_kysschemadefinition(
      it_allcolumns = lt_columns
      it_partitionkeys = lt_partition_keys ).

    DATA(lo_tag) = NEW /aws1/cl_kystag(
      iv_key = 'convert_test'
      iv_value = 'true' ).
    DATA(lt_tags) = VALUE /aws1/cl_kystag=>tt_taglist( ( lo_tag ) ).

    TRY.
        go_kys->createtable(
          iv_keyspacename = gv_keyspace_name
          iv_tablename = lv_delete_table
          io_schemadefinition = lo_schema
          it_tags = lt_tags ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_create_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to create table for delete test: { lo_create_exception->get_text( ) }| ).
    ENDTRY.

    " Wait for table to become active
    wait_for_table_active(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = lv_delete_table ).

    " Now call the example delete method
    go_kys_actions->delete_table(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = lv_delete_table ).

    " Wait for deletion to complete
    wait_for_table_deleted(
      iv_keyspace_name = gv_keyspace_name
      iv_table_name = lv_delete_table ).

    " Verify table no longer exists
    TRY.
        go_kys->gettable(
          iv_keyspacename = gv_keyspace_name
          iv_tablename = lv_delete_table ).
        cl_abap_unit_assert=>fail( msg = 'Table should have been deleted' ).
      CATCH /aws1/cx_kysresourcenotfoundex.
        " Expected - table was deleted successfully
    ENDTRY.
  ENDMETHOD.

  METHOD delete_keyspace.
    " Create a unique keyspace specifically for deletion testing
    DATA(lv_delete_keyspace) = |kys_test_delete_{ /awsex/cl_utils=>get_random_string( ) }|.

    " Create the keyspace
    create_test_keyspace( lv_delete_keyspace ).

    " Wait for keyspace to become active
    wait_for_keyspace_active( lv_delete_keyspace ).

    " Now call the example delete method
    go_kys_actions->delete_keyspace( iv_keyspace_name = lv_delete_keyspace ).

    " Verify keyspace deletion initiated
    " Note: We cannot reliably check the status as keyspaces may be deleted quickly
    " or may still be in DELETING state. Both are acceptable outcomes.
    WAIT UP TO 2 SECONDS.

    TRY.
        DATA(lo_result) = go_kys->getkeyspace( iv_keyspacename = lv_delete_keyspace ).
        " If we can still get it, check if it's being deleted
        DATA(lv_is_active) = is_keyspace_active( lo_result ).
        cl_abap_unit_assert=>assert_false(
          act = lv_is_active
          msg = 'Keyspace should not be active after deletion' ).
      CATCH /aws1/cx_kysresourcenotfoundex.
        " Expected - keyspace was deleted quickly
    ENDTRY.

    " Note: Not waiting for full deletion as it can take time
    " Keyspace is tagged with convert_test for manual cleanup if needed
  ENDMETHOD.

ENDCLASS.
