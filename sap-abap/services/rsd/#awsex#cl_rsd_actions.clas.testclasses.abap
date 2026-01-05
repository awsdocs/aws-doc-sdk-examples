" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_rsd_actions DEFINITION DEFERRED.
CLASS /awsex/cl_rsd_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_rsd_actions.

CLASS ltc_awsex_cl_rsd_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_cluster_id TYPE /aws1/rsdclusteridstring.
    CLASS-DATA av_database_name TYPE /aws1/rsdstring VALUE 'dev'.
    CLASS-DATA av_user_name TYPE /aws1/rsdstring VALUE 'awsuser'.
    CLASS-DATA av_user_password TYPE /aws1/rshsensitivestring VALUE 'AwsUser1000'.
    CLASS-DATA av_table_name TYPE string.

    CLASS-DATA ao_rsh TYPE REF TO /aws1/if_rsh.
    CLASS-DATA ao_rsd TYPE REF TO /aws1/if_rsd.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_rsd_actions TYPE REF TO /awsex/cl_rsd_actions.

    METHODS: list_databases FOR TESTING RAISING /aws1/cx_rt_generic,
      execute_statement FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_statement FOR TESTING RAISING /aws1/cx_rt_generic,
      get_statement_result FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_cluster_available
      IMPORTING
                iv_cluster_id TYPE /aws1/rshstring
      RAISING   /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_statement_finished
      IMPORTING
                iv_statement_id TYPE /aws1/rsduuid
      RAISING   /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_rsd_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_uuid_string TYPE string.
    DATA lt_tags TYPE /aws1/cl_rshtag=>tt_taglist.
    DATA lo_tag TYPE REF TO /aws1/cl_rshtag.
    DATA lv_status TYPE /aws1/rshstring.
    DATA lv_max_waits TYPE i VALUE 60.
    DATA lo_create_cluster_result TYPE REF TO /aws1/cl_rshcreateclustresult.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lv_create_sql TYPE string.
    DATA lo_create_result TYPE REF TO /aws1/cl_rsdexecutestmtoutput.
    DATA lv_insert_sql TYPE string.
    DATA lo_insert_result TYPE REF TO /aws1/cl_rsdexecutestmtoutput.
    DATA lv_statement_id TYPE /aws1/rsduuid.

    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).

    ao_rsh = /aws1/cl_rsh_factory=>create( ao_session ).
    ao_rsd = /aws1/cl_rsd_factory=>create( ao_session ).
    ao_rsd_actions = NEW /awsex/cl_rsd_actions( ).


    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    av_cluster_id = |rsd-test-{ lv_uuid_string }|.
    IF strlen( av_cluster_id ) > 30.
      av_cluster_id = substring( val = av_cluster_id len = 30 ).
    ENDIF.

    av_table_name = |movies_{ lv_uuid_string }|.
    IF strlen( av_table_name ) > 30.
      av_table_name = substring( val = av_table_name len = 30 ).
    ENDIF.

    CREATE OBJECT lo_tag
      EXPORTING
        iv_key   = 'convert_test'
        iv_value = 'true'.
    APPEND lo_tag TO lt_tags.

    TRY.
        lo_create_cluster_result = ao_rsh->createcluster(
          iv_clusteridentifier  = av_cluster_id
          iv_nodetype           = 'ra3.xlplus'
          iv_masterusername     = av_user_name
          iv_masteruserpassword = av_user_password
          iv_publiclyaccessible = abap_false
          iv_clustertype        = 'single-node'
          it_tags               = lt_tags
        ).

        lv_max_waits = 60.
        DO lv_max_waits TIMES.
          WAIT UP TO 30 SECONDS.
          lo_describe_result = ao_rsh->describeclusters(
            iv_clusteridentifier = av_cluster_id
          ).

          lt_clusters = lo_describe_result->get_clusters( ).

          IF lines( lt_clusters ) > 0.
            READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
            lv_status = lo_cluster->get_clusterstatus( ).

            IF lv_status = 'available'.
              EXIT.
            ELSEIF lv_status <> 'creating'.
              cl_abap_unit_assert=>fail(
                msg = |Unexpected cluster status: { lv_status }| ).
            ENDIF.
          ENDIF.
        ENDDO.

        IF lv_status <> 'available'.
          cl_abap_unit_assert=>fail(
            msg = |Cluster did not become available within timeout| ).
        ENDIF.

        lv_create_sql = |CREATE TABLE { av_table_name } (id INT, title VARCHAR(100), year INT)|.
        lo_create_result = ao_rsd_actions->execute_statement(
          iv_cluster_identifier = av_cluster_id
          iv_database_name      = av_database_name
          iv_user_name          = av_user_name
          iv_sql                = lv_create_sql
        ).
        lv_statement_id = lo_create_result->get_id( ).
        wait_for_statement_finished( lv_statement_id ).

        lv_insert_sql = |INSERT INTO { av_table_name } VALUES (1, 'Test Movie 1', 2024)|.
        lo_insert_result = ao_rsd_actions->execute_statement(
          iv_cluster_identifier = av_cluster_id
          iv_database_name      = av_database_name
          iv_user_name          = av_user_name
          iv_sql                = lv_insert_sql
        ).
        lv_statement_id = lo_insert_result->get_id( ).
        wait_for_statement_finished( lv_statement_id ).

      CATCH /aws1/cx_rshclustalrdyexfault.
        wait_for_cluster_available( av_cluster_id ).

        TRY.
            lv_create_sql = |CREATE TABLE { av_table_name } (id INT, title VARCHAR(100), year INT)|.
            lo_create_result = ao_rsd_actions->execute_statement(
              iv_cluster_identifier = av_cluster_id
              iv_database_name      = av_database_name
              iv_user_name          = av_user_name
              iv_sql                = lv_create_sql
            ).
            lv_statement_id = lo_create_result->get_id( ).
            wait_for_statement_finished( lv_statement_id ).

            lv_insert_sql = |INSERT INTO { av_table_name } VALUES (1, 'Test Movie 1', 2024)|.
            lo_insert_result = ao_rsd_actions->execute_statement(
              iv_cluster_identifier = av_cluster_id
              iv_database_name      = av_database_name
              iv_user_name          = av_user_name
              iv_sql                = lv_insert_sql
            ).
            lv_statement_id = lo_insert_result->get_id( ).
            wait_for_statement_finished( lv_statement_id ).
          CATCH /aws1/cx_rt_generic.
            " Table may already exist, continue
        ENDTRY.
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    DATA lv_drop_sql TYPE string.
    DATA lo_drop_result TYPE REF TO /aws1/cl_rsdexecutestmtoutput.
    DATA lv_statement_id TYPE /aws1/rsduuid.
    DATA lo_delete_cluster_result TYPE REF TO /aws1/cl_rshdelclusterresult.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
    DATA lv_status TYPE /aws1/rshstring.
    DATA lv_cleanup_successful TYPE abap_bool VALUE abap_false.

    " Drop the table first
    TRY.
        lv_drop_sql = |DROP TABLE IF EXISTS { av_table_name }|.
        lo_drop_result = ao_rsd_actions->execute_statement(
          iv_cluster_identifier = av_cluster_id
          iv_database_name      = av_database_name
          iv_user_name          = av_user_name
          iv_sql                = lv_drop_sql
        ).
        lv_statement_id = lo_drop_result->get_id( ).
        wait_for_statement_finished( lv_statement_id ).
      CATCH /aws1/cx_rt_generic.
        " Ignore table drop errors and continue with cluster deletion
    ENDTRY.

    " Delete the cluster
    TRY.
        lo_delete_cluster_result = ao_rsh->deletecluster(
          iv_clusteridentifier        = av_cluster_id
          iv_skipfinalclustersnapshot = abap_true
        ).

        " Verify deletion by checking cluster status
        TRY.
            lo_describe_result = ao_rsh->describeclusters(
              iv_clusteridentifier = av_cluster_id
            ).

            lt_clusters = lo_describe_result->get_clusters( ).

            IF lines( lt_clusters ) = 0.
              " No cluster returned - deletion successful
              lv_cleanup_successful = abap_true.
              MESSAGE 'Redshift cluster successfully deleted.' TYPE 'I'.
            ELSE.
              " Check if cluster status is 'deleting'
              READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
              IF lo_cluster IS BOUND.
                lv_status = lo_cluster->get_clusterstatus( ).
                IF lv_status = 'deleting'.
                  " Cluster is in deleting status - cleanup successful
                  lv_cleanup_successful = abap_true.
                  MESSAGE 'Redshift cluster deletion in progress.' TYPE 'I'.
                ELSE.
                  MESSAGE |Cluster status: { lv_status }. Manual cleanup may be required.| TYPE 'I'.
                ENDIF.
              ENDIF.
            ENDIF.
          CATCH /aws1/cx_rshclustnotfoundfault.
            " Cluster not found - deletion successful
            lv_cleanup_successful = abap_true.
            MESSAGE 'Redshift cluster successfully deleted (not found).' TYPE 'I'.
        ENDTRY.

      CATCH /aws1/cx_rshclustnotfoundfault.
        " Cluster already deleted
        lv_cleanup_successful = abap_true.
        MESSAGE 'Redshift cluster not found (already deleted).' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_delete_ex).
        MESSAGE |Cluster deletion error: { lo_delete_ex->get_text( ) }. Manual cleanup required.| TYPE 'I'.
    ENDTRY.

    IF lv_cleanup_successful = abap_false.
      MESSAGE 'Redshift cluster tagged with convert_test=true. Manual cleanup required.' TYPE 'I'.
    ENDIF.
  ENDMETHOD.

  METHOD wait_for_cluster_available.
    DATA lv_status TYPE /aws1/rshstring.
    DATA lv_max_waits TYPE i VALUE 60.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.

    DO lv_max_waits TIMES.
      WAIT UP TO 30 SECONDS.
      TRY.
          lo_describe_result = ao_rsh->describeclusters(
            iv_clusteridentifier = iv_cluster_id
          ).

          lt_clusters = lo_describe_result->get_clusters( ).

          IF lines( lt_clusters ) > 0.
            READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
            lv_status = lo_cluster->get_clusterstatus( ).

            IF lv_status = 'available'.
              RETURN.
            ELSEIF lv_status = 'creating' OR lv_status = 'modifying'.
              CONTINUE.
            ELSE.
              cl_abap_unit_assert=>fail(
                msg = |Unexpected cluster status: { lv_status }| ).
            ENDIF.
          ENDIF.
        CATCH /aws1/cx_rshclustnotfoundfault.
          " Continue waiting
      ENDTRY.
    ENDDO.

    cl_abap_unit_assert=>fail(
      msg = |Cluster did not become available within timeout| ).
  ENDMETHOD.

  METHOD wait_for_statement_finished.
    DATA lv_status TYPE /aws1/rsdstatusstring.
    DATA lv_max_waits TYPE i VALUE 30.
    DATA lo_describe_result TYPE REF TO /aws1/cl_rsddescrstmtresponse.
    DATA lv_error TYPE /aws1/rsdstring.

    DO lv_max_waits TIMES.
      WAIT UP TO 2 SECONDS.
      TRY.
          lo_describe_result = ao_rsd_actions->describe_statement(
            iv_statement_id = iv_statement_id
          ).

          lv_status = lo_describe_result->get_status( ).

          IF lv_status = 'FINISHED'.
            RETURN.
          ELSEIF lv_status = 'FAILED'.
            lv_error = lo_describe_result->get_error( ).
            cl_abap_unit_assert=>fail(
              msg = |Statement failed: { lv_error }| ).
          ELSEIF lv_status = 'PICKED' OR lv_status = 'STARTED' OR lv_status = 'SUBMITTED'.
            CONTINUE.
          ELSE.
            cl_abap_unit_assert=>fail(
              msg = |Unexpected statement status: { lv_status }| ).
          ENDIF.
        CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
          cl_abap_unit_assert=>fail(
            msg = |Error checking statement status: { lo_ex->get_text( ) }| ).
      ENDTRY.
    ENDDO.

    cl_abap_unit_assert=>fail(
      msg = |Statement did not finish within timeout| ).
  ENDMETHOD.

  METHOD list_databases.
    DATA lo_result TYPE REF TO /aws1/cl_rsdlistdatabasesrsp.
    DATA lt_databases TYPE /aws1/cl_rsddatabaselist_w=>tt_databaselist.
    DATA lv_dev_found TYPE abap_bool VALUE abap_false.
    DATA lv_database_name TYPE /aws1/rsdstring.
    FIELD-SYMBOLS <fs_database> TYPE any.

    lo_result = ao_rsd_actions->list_databases(
      iv_cluster_identifier = av_cluster_id
      iv_database_name      = av_database_name
      iv_database_user      = av_user_name
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |List databases failed| ).

    lt_databases = lo_result->get_databases( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_databases
      msg = |No databases returned| ).

    " The 'dev' database should exist by default
    LOOP AT lt_databases ASSIGNING <fs_database>.
      lv_database_name = <fs_database>.
      IF lv_database_name = 'dev'.
        lv_dev_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_dev_found
      msg = |Default 'dev' database not found| ).
  ENDMETHOD.

  METHOD execute_statement.
    " Insert test data
    DATA lv_insert_sql TYPE string.
    DATA lo_result TYPE REF TO /aws1/cl_rsdexecutestmtoutput.
    DATA lv_statement_id TYPE /aws1/rsduuid.

    lv_insert_sql = |INSERT INTO { av_table_name } VALUES (1, 'Test Movie', 2024)|.
    lo_result = ao_rsd_actions->execute_statement(
      iv_cluster_identifier = av_cluster_id
      iv_database_name      = av_database_name
      iv_user_name          = av_user_name
      iv_sql                = lv_insert_sql
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Execute statement failed| ).

    lv_statement_id = lo_result->get_id( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_statement_id
      msg = |Statement ID not returned| ).

    " Wait for statement to complete
    wait_for_statement_finished( lv_statement_id ).
  ENDMETHOD.

  METHOD describe_statement.
    " Execute a simple query first
    DATA lv_query_sql TYPE string.
    DATA lo_exec_result TYPE REF TO /aws1/cl_rsdexecutestmtoutput.
    DATA lv_statement_id TYPE /aws1/rsduuid.
    DATA lo_result TYPE REF TO /aws1/cl_rsddescrstmtresponse.
    DATA lv_status TYPE /aws1/rsdstatusstring.

    lv_query_sql = |SELECT * FROM { av_table_name } LIMIT 1|.
    lo_exec_result = ao_rsd_actions->execute_statement(
      iv_cluster_identifier = av_cluster_id
      iv_database_name      = av_database_name
      iv_user_name          = av_user_name
      iv_sql                = lv_query_sql
    ).

    lv_statement_id = lo_exec_result->get_id( ).

    " Now describe the statement
    lo_result = ao_rsd_actions->describe_statement(
      iv_statement_id = lv_statement_id
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Describe statement failed| ).

    lv_status = lo_result->get_status( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_status
      msg = |Status not returned| ).
  ENDMETHOD.

  METHOD get_statement_result.
    " Execute a query with parameters
    DATA lt_parameters TYPE /aws1/cl_rsdsqlparameter=>tt_sqlparameterslist.
    DATA lo_param TYPE REF TO /aws1/cl_rsdsqlparameter.
    DATA lv_query_sql TYPE string.
    DATA lo_exec_result TYPE REF TO /aws1/cl_rsdexecutestmtoutput.
    DATA lv_statement_id TYPE /aws1/rsduuid.
    DATA lo_result TYPE REF TO /aws1/cl_rsdgetstmtresultrsp.
    DATA lt_columns TYPE /aws1/cl_rsdcolumnmetadata=>tt_columnmetadatalist.
    DATA lt_records TYPE /aws1/cl_rsdfield=>tt_sqlrecords.

    CREATE OBJECT lo_param
      EXPORTING
        iv_name  = 'year'
        iv_value = '2024'.
    APPEND lo_param TO lt_parameters.

    lv_query_sql = |SELECT * FROM { av_table_name } WHERE year = :year|.
    lo_exec_result = ao_rsd_actions->execute_statement(
      iv_cluster_identifier = av_cluster_id
      iv_database_name      = av_database_name
      iv_user_name          = av_user_name
      iv_sql                = lv_query_sql
      it_parameter_list     = lt_parameters
    ).

    lv_statement_id = lo_exec_result->get_id( ).

    " Wait for statement to finish
    wait_for_statement_finished( lv_statement_id ).

    " Get the statement result
    lo_result = ao_rsd_actions->get_statement_result(
      iv_statement_id = lv_statement_id
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Get statement result failed| ).

    " Verify we got column metadata
    lt_columns = lo_result->get_columnmetadata( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_columns
      msg = |No column metadata returned| ).

    " Verify we got records
    lt_records = lo_result->get_records( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_records
      msg = |No records returned| ).
  ENDMETHOD.

ENDCLASS.
