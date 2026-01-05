" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_kys_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    "! <p class="shorttext synchronized">Creates a keyspace.</p>
    "! @parameter iv_keyspace_name | The name of the keyspace to create
    "! @parameter oo_result | The output from the CreateKeyspace operation
    "! @raising /aws1/cx_rt_generic | Raised for any AWS SDK error
    METHODS create_keyspace
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
      EXPORTING
        oo_result        TYPE REF TO /aws1/cl_kyscreatekeyspacersp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized">Checks if a keyspace exists.</p>
    "! @parameter iv_keyspace_name | The name of the keyspace to check
    "! @parameter oo_result | The output from the GetKeyspace operation
    "! @raising /aws1/cx_rt_generic | Raised for any AWS SDK error
    METHODS get_keyspace
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
      EXPORTING
        oo_result        TYPE REF TO /aws1/cl_kysgetkeyspacersp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized">Lists keyspaces in your account.</p>
    "! @parameter iv_max_results | Maximum number of keyspaces to return
    "! @parameter oo_result | The output from the ListKeyspaces operation
    "! @raising /aws1/cx_rt_generic | Raised for any AWS SDK error
    METHODS list_keyspaces
      IMPORTING
        iv_max_results TYPE /aws1/kysmaxresults
      EXPORTING
        oo_result      TYPE REF TO /aws1/cl_kyslistkeyspacesrsp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized">Creates a table in the keyspace.</p>
    "! @parameter iv_keyspace_name | The name of the keyspace
    "! @parameter iv_table_name | The name of the table to create
    "! @parameter oo_result | The output from the CreateTable operation
    "! @raising /aws1/cx_rt_generic | Raised for any AWS SDK error
    METHODS create_table
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
        iv_table_name    TYPE /aws1/kystablename
      EXPORTING
        oo_result        TYPE REF TO /aws1/cl_kyscreatetablersp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized">Gets information about a table.</p>
    "! @parameter iv_keyspace_name | The name of the keyspace
    "! @parameter iv_table_name | The name of the table
    "! @parameter oo_result | The output from the GetTable operation
    "! @raising /aws1/cx_rt_generic | Raised for any AWS SDK error
    METHODS get_table
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
        iv_table_name    TYPE /aws1/kystablename
      EXPORTING
        oo_result        TYPE REF TO /aws1/cl_kysgettableresponse
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized">Lists tables in the keyspace.</p>
    "! @parameter iv_keyspace_name | The name of the keyspace
    "! @parameter oo_result | The output from the ListTables operation
    "! @raising /aws1/cx_rt_generic | Raised for any AWS SDK error
    METHODS list_tables
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
      EXPORTING
        oo_result        TYPE REF TO /aws1/cl_kyslisttablesresponse
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized">Updates the schema of a table.</p>
    "! @parameter iv_keyspace_name | The name of the keyspace
    "! @parameter iv_table_name | The name of the table
    "! @parameter oo_result | The output from the UpdateTable operation
    "! @raising /aws1/cx_rt_generic | Raised for any AWS SDK error
    METHODS update_table
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
        iv_table_name    TYPE /aws1/kystablename
      EXPORTING
        oo_result        TYPE REF TO /aws1/cl_kysupdatetablersp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized">Restores a table to a previous point in time.</p>
    "! @parameter iv_source_keyspace_name | The source keyspace name
    "! @parameter iv_source_table_name | The source table name
    "! @parameter iv_target_keyspace_name | The target keyspace name
    "! @parameter iv_target_table_name | The target table name
    "! @parameter iv_restore_timestamp | The point in time to restore (UTC)
    "! @parameter oo_result | The output from the RestoreTable operation
    "! @raising /aws1/cx_rt_generic | Raised for any AWS SDK error
    METHODS restore_table
      IMPORTING
        iv_source_keyspace_name TYPE /aws1/kyskeyspacename
        iv_source_table_name    TYPE /aws1/kystablename
        iv_target_keyspace_name TYPE /aws1/kyskeyspacename
        iv_target_table_name    TYPE /aws1/kystablename
        iv_restore_timestamp    TYPE /aws1/kystimestamp
      EXPORTING
        oo_result               TYPE REF TO /aws1/cl_kysrestoretablersp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized">Deletes a table from the keyspace.</p>
    "! @parameter iv_keyspace_name | The name of the keyspace
    "! @parameter iv_table_name | The name of the table to delete
    "! @raising /aws1/cx_rt_generic | Raised for any AWS SDK error
    METHODS delete_table
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
        iv_table_name    TYPE /aws1/kystablename
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized">Deletes a keyspace.</p>
    "! @parameter iv_keyspace_name | The name of the keyspace to delete
    "! @raising /aws1/cx_rt_generic | Raised for any AWS SDK error
    METHODS delete_keyspace
      IMPORTING
        iv_keyspace_name TYPE /aws1/kyskeyspacename
      RAISING
        /aws1/cx_rt_generic .

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_kys_actions IMPLEMENTATION.

  METHOD create_keyspace.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kys) = /aws1/cl_kys_factory=>create( lo_session ).

    " snippet-start:[kys.abapv1.create_keyspace]
    TRY.
        oo_result = lo_kys->createkeyspace(
          iv_keyspacename = iv_keyspace_name ).
        MESSAGE 'Keyspace created successfully.' TYPE 'I'.
      CATCH /aws1/cx_kysconflictexception.
        MESSAGE 'Keyspace already exists.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[kys.abapv1.create_keyspace]
  ENDMETHOD.

  METHOD get_keyspace.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kys) = /aws1/cl_kys_factory=>create( lo_session ).

    " snippet-start:[kys.abapv1.get_keyspace]
    TRY.
        oo_result = lo_kys->getkeyspace(
          iv_keyspacename = iv_keyspace_name ).
        MESSAGE 'Keyspace retrieved successfully.' TYPE 'I'.
      CATCH /aws1/cx_kysresourcenotfoundex.
        MESSAGE 'Keyspace does not exist.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[kys.abapv1.get_keyspace]
  ENDMETHOD.

  METHOD list_keyspaces.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kys) = /aws1/cl_kys_factory=>create( lo_session ).

    " snippet-start:[kys.abapv1.list_keyspaces]
    TRY.
        oo_result = lo_kys->listkeyspaces(
          iv_maxresults = iv_max_results ).
        MESSAGE 'Keyspaces listed successfully.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[kys.abapv1.list_keyspaces]
  ENDMETHOD.

  METHOD create_table.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kys) = /aws1/cl_kys_factory=>create( lo_session ).

    " snippet-start:[kys.abapv1.create_table]
    TRY.
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

        " Enable point-in-time recovery
        DATA(lo_pitr) = NEW /aws1/cl_kyspointintimerec(
          iv_status = 'ENABLED' ).

        oo_result = lo_kys->createtable(
          iv_keyspacename = iv_keyspace_name
          iv_tablename = iv_table_name
          io_schemadefinition = lo_schema
          io_pointintimerecovery = lo_pitr ).
        MESSAGE 'Table created successfully.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[kys.abapv1.create_table]
  ENDMETHOD.

  METHOD get_table.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kys) = /aws1/cl_kys_factory=>create( lo_session ).

    " snippet-start:[kys.abapv1.get_table]
    TRY.
        oo_result = lo_kys->gettable(
          iv_keyspacename = iv_keyspace_name
          iv_tablename = iv_table_name ).
        MESSAGE 'Table information retrieved successfully.' TYPE 'I'.
      CATCH /aws1/cx_kysresourcenotfoundex.
        MESSAGE 'Table does not exist.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[kys.abapv1.get_table]
  ENDMETHOD.

  METHOD list_tables.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kys) = /aws1/cl_kys_factory=>create( lo_session ).

    " snippet-start:[kys.abapv1.list_tables]
    TRY.
        oo_result = lo_kys->listtables(
          iv_keyspacename = iv_keyspace_name ).
        MESSAGE 'Tables listed successfully.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[kys.abapv1.list_tables]
  ENDMETHOD.

  METHOD update_table.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kys) = /aws1/cl_kys_factory=>create( lo_session ).

    " snippet-start:[kys.abapv1.update_table]
    TRY.
        " Add a new column to track watched movies
        DATA(lt_add_columns) = VALUE /aws1/cl_kyscolumndefinition=>tt_columndefinitionlist(
          ( NEW /aws1/cl_kyscolumndefinition( iv_name = 'watched' iv_type = 'boolean' ) )
        ).

        oo_result = lo_kys->updatetable(
          iv_keyspacename = iv_keyspace_name
          iv_tablename = iv_table_name
          it_addcolumns = lt_add_columns ).
        MESSAGE 'Table updated successfully.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[kys.abapv1.update_table]
  ENDMETHOD.

  METHOD restore_table.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kys) = /aws1/cl_kys_factory=>create( lo_session ).

    " snippet-start:[kys.abapv1.restore_table]
    TRY.
        oo_result = lo_kys->restoretable(
          iv_sourcekeyspacename = iv_source_keyspace_name
          iv_sourcetablename = iv_source_table_name
          iv_targetkeyspacename = iv_target_keyspace_name
          iv_targettablename = iv_target_table_name
          iv_restoretimestamp = iv_restore_timestamp ).
        MESSAGE 'Table restore initiated successfully.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[kys.abapv1.restore_table]
  ENDMETHOD.

  METHOD delete_table.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kys) = /aws1/cl_kys_factory=>create( lo_session ).

    " snippet-start:[kys.abapv1.delete_table]
    TRY.
        lo_kys->deletetable(
          iv_keyspacename = iv_keyspace_name
          iv_tablename = iv_table_name ).
        MESSAGE 'Table deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[kys.abapv1.delete_table]
  ENDMETHOD.

  METHOD delete_keyspace.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kys) = /aws1/cl_kys_factory=>create( lo_session ).

    " snippet-start:[kys.abapv1.delete_keyspace]
    TRY.
        lo_kys->deletekeyspace(
          iv_keyspacename = iv_keyspace_name ).
        MESSAGE 'Keyspace deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[kys.abapv1.delete_keyspace]
  ENDMETHOD.

ENDCLASS.
