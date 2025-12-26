" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_rsd_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS list_databases
      IMPORTING
        !iv_cluster_identifier TYPE /aws1/rsdclusteridstring
        !iv_database_name      TYPE /aws1/rsdstring
        !iv_database_user      TYPE /aws1/rsdstring
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_rsdlistdatabasesrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS execute_statement
      IMPORTING
        !iv_cluster_identifier TYPE /aws1/rsdclusteridstring
        !iv_database_name      TYPE /aws1/rsdstring
        !iv_user_name          TYPE /aws1/rsdstring
        !iv_sql                TYPE /aws1/rsdstatementstring
        !it_parameter_list     TYPE /aws1/cl_rsdsqlparameter=>tt_sqlparameterslist OPTIONAL
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_rsdexecutestmtoutput
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_statement
      IMPORTING
        !iv_statement_id TYPE /aws1/rsduuid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_rsddescrstmtresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_statement_result
      IMPORTING
        !iv_statement_id TYPE /aws1/rsduuid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_rsdgetstmtresultrsp
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_RSD_ACTIONS IMPLEMENTATION.


  METHOD list_databases.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA lo_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA lo_rsd TYPE REF TO /aws1/if_rsd.
    DATA lt_databases TYPE /aws1/cl_rsddatabaselist_w=>tt_databaselist.
    DATA lv_db_count TYPE i.
    
    lo_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    lo_rsd = /aws1/cl_rsd_factory=>create( lo_session ).

    " snippet-start:[rsd.abapv1.list_databases]
    TRY.
        " Example values: iv_cluster_identifier = 'redshift-cluster-movies'
        " Example values: iv_database_name = 'dev'
        " Example values: iv_database_user = 'awsuser'
        oo_result = lo_rsd->listdatabases(
          iv_clusteridentifier = iv_cluster_identifier
          iv_database = iv_database_name
          iv_dbuser = iv_database_user
        ).
        lt_databases = oo_result->get_databases( ).
        lv_db_count = lines( lt_databases ).
        MESSAGE |Retrieved { lv_db_count } database(s).| TYPE 'I'.
      CATCH /aws1/cx_rsddatabaseconnex.
        MESSAGE 'Database connection error.' TYPE 'I'.
      CATCH /aws1/cx_rsdresourcenotfoundex.
        MESSAGE 'Cluster not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rsd.abapv1.list_databases]
  ENDMETHOD.


  METHOD execute_statement.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA lo_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA lo_rsd TYPE REF TO /aws1/if_rsd.
    DATA lv_statement_id TYPE /aws1/rsduuid.
    
    lo_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    lo_rsd = /aws1/cl_rsd_factory=>create( lo_session ).

    " snippet-start:[rsd.abapv1.execute_statement]
    TRY.
        " Example values: iv_cluster_identifier = 'redshift-cluster-movies'
        " Example values: iv_database_name = 'dev'
        " Example values: iv_user_name = 'awsuser'
        " Example values: iv_sql = 'SELECT * FROM movies WHERE year = :year'
        " Example values: it_parameter_list - SQL parameters for parameterized queries
        oo_result = lo_rsd->executestatement(
          iv_clusteridentifier = iv_cluster_identifier
          iv_database = iv_database_name
          iv_dbuser = iv_user_name
          iv_sql = iv_sql
          it_parameters = it_parameter_list
        ).
        lv_statement_id = oo_result->get_id( ).
        MESSAGE |Statement executed. ID: { lv_statement_id }| TYPE 'I'.
      CATCH /aws1/cx_rsdexecutestatementex.
        MESSAGE 'Statement execution error.' TYPE 'I'.
      CATCH /aws1/cx_rsdresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rsd.abapv1.execute_statement]
  ENDMETHOD.


  METHOD describe_statement.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA lo_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA lo_rsd TYPE REF TO /aws1/if_rsd.
    DATA lv_status TYPE /aws1/rsdstatusstring.
    
    lo_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    lo_rsd = /aws1/cl_rsd_factory=>create( lo_session ).

    " snippet-start:[rsd.abapv1.describe_statement]
    TRY.
        " Example values: iv_statement_id = 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'
        oo_result = lo_rsd->describestatement(
          iv_id = iv_statement_id
        ).
        lv_status = oo_result->get_status( ).
        MESSAGE |Statement status: { lv_status }| TYPE 'I'.
      CATCH /aws1/cx_rsdresourcenotfoundex.
        MESSAGE 'Statement not found.' TYPE 'I'.
      CATCH /aws1/cx_rsdinternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rsd.abapv1.describe_statement]
  ENDMETHOD.


  METHOD get_statement_result.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA lo_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA lo_rsd TYPE REF TO /aws1/if_rsd.
    DATA lv_next_token TYPE /aws1/rsdstring.
    DATA lt_all_records TYPE /aws1/cl_rsdfield=>tt_sqlrecords.
    DATA lo_result_page TYPE REF TO /aws1/cl_rsdgetstmtresultrsp.
    DATA lt_page_records TYPE /aws1/cl_rsdfield=>tt_sqlrecords.
    DATA lv_record_count TYPE i.
    
    lo_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    lo_rsd = /aws1/cl_rsd_factory=>create( lo_session ).

    " snippet-start:[rsd.abapv1.get_statement_result]
    TRY.
        " Example values: iv_statement_id = 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'
        " Handle pagination for large result sets

        DO.
          lo_result_page = lo_rsd->getstatementresult(
            iv_id = iv_statement_id
            iv_nexttoken = lv_next_token
          ).

          " Collect records from this page
          lt_page_records = lo_result_page->get_records( ).
          APPEND LINES OF lt_page_records TO lt_all_records.

          " Check if there are more pages
          lv_next_token = lo_result_page->get_nexttoken( ).
          IF lv_next_token IS INITIAL.
            EXIT. " No more pages
          ENDIF.
        ENDDO.

        " For the last call, set oo_result for return value
        oo_result = lo_result_page.
        lv_record_count = lines( lt_all_records ).
        MESSAGE |Retrieved { lv_record_count } record(s).| TYPE 'I'.
      CATCH /aws1/cx_rsdresourcenotfoundex.
        MESSAGE 'Statement not found or results not available.' TYPE 'I'.
      CATCH /aws1/cx_rsdinternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rsd.abapv1.get_statement_result]
  ENDMETHOD.
ENDCLASS.
