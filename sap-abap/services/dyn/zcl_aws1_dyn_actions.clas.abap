class ZCL_AWS1_DYN_ACTIONS definition
  public
  final
  create public .

public section.
protected section.
private section.

  methods CREATE_TABLE
    returning
      value(OO_RESULT) type ref to /AWS1/CL_DYNCREATETABLEOUTPUT .
  methods DESCRIBE_TABLE
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME
    returning
      value(OO_RESULT) type ref to /AWS1/CL_DYNDESCRTABLEOUTPUT .
  methods DELETE_TABLE
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME .
  methods LIST_TABLES
    returning
      value(OO_RESULT) type ref to /AWS1/CL_DYNLISTTABLESOUTPUT .
  methods INSERT_INTO_TABLE
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME .
  methods QUERY_TABLE
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME
    returning
      value(OT_ATTR) type /AWS1/CL_DYNATTRIBUTEVALUE=>TT_ATTRIBUTEMAP .
ENDCLASS.



CLASS ZCL_AWS1_DYN_ACTIONS IMPLEMENTATION.


  METHOD create_table.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

    " snippet-start:[dyn.abapv1.create_table]
    TRY.
      DATA(lv_tablename) = |example-table|.
      DATA(ls_keyschema) = VALUE /aws1/cl_dynkeyschemaelement=>tt_keyschema(
          ( NEW /aws1/cl_dynkeyschemaelement( iv_attributename = 'year' iv_keytype = 'HASH' ) )
          ( NEW /aws1/cl_dynkeyschemaelement( iv_attributename = 'title' iv_keytype = 'RANGE' ) )
      ).
      DATA(lt_attributedefinitions) = VALUE /aws1/cl_dynattributedefn=>tt_attributedefinitions(
          ( NEW /aws1/cl_dynattributedefn( iv_attributename = 'title' iv_attributetype = 'S' ) )
          ( NEW /aws1/cl_dynattributedefn( iv_attributename = 'year' iv_attributetype = 'N' ) )
        ).
      DATA(lo_dynprovthroughput)  = NEW /aws1/cl_dynprovthroughput(
        iv_readcapacityunits = 5
        iv_writecapacityunits = 5
        ).
      oo_result = lo_dyn->createtable(
        it_keyschema = ls_keyschema
        iv_tablename = lv_tablename
        it_attributedefinitions = lt_attributedefinitions
        io_provisionedthroughput = lo_dynprovthroughput
      ).
      lo_dyn->get_waiter( )->tableexists(
        iv_max_wait_time = 200
        iv_tablename     = lv_tablename
      ).
      MESSAGE 'DynamoDB Table' && lv_tablename && 'created.' TYPE 'I'.
    CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
      DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
      MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[dyn.abapv1.create_table]
  ENDMETHOD.


METHOD delete_table.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

  DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
  DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.delete_table]
  TRY.
    lo_dyn->deletetable( iv_tablename = iv_table_name ).
    lo_dyn->get_waiter( )->tablenotexists(
      iv_max_wait_time = 200
      iv_tablename     = iv_table_name
    ).
    MESSAGE 'DynamoDB Table deleted.' TYPE 'I'.
  CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
    DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
    MESSAGE lv_error TYPE 'E'.
  ENDTRY.
  " snippet-end:[dyn.abapv1.delete_table]
ENDMETHOD.


METHOD describe_table.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.describe_table]
  TRY.
    oo_result = lo_dyn->describetable( iv_tablename = iv_table_name ).
    DATA(lv_tablename) = oo_result->get_table( )->ask_tablename( ).
    MESSAGE 'The table name is ' && lv_tablename TYPE 'I'.
    CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
      DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
      MESSAGE lv_error TYPE 'E'.
    CATCH /aws1/cx_rt_technical_generic.
      " ignore
    CATCH /aws1/cx_rt_no_auth_generic.
      "ignore
    CATCH /aws1/cx_rt_value_missing.
      lv_error = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
      MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[dyn.abapv1.describe_table]

ENDMETHOD.


METHOD insert_into_table.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

  DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
  DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.insert_into_table]
  TRY.
    DATA(lo_resp) = lo_dyn->putitem(
       iv_tablename = iv_table_name
       it_item      = VALUE /aws1/cl_dynattributevalue=>tt_putiteminputattributemap(
          ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
            key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
          ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
            key = 'year' value = NEW /aws1/cl_dynattributevalue( iv_n = |{ '1975' }| ) ) )
       ) ).
     lo_resp = lo_dyn->putitem(
       iv_tablename = iv_table_name
       it_item      = VALUE /aws1/cl_dynattributevalue=>tt_putiteminputattributemap(
          ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
            key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Star Wars' ) ) )
          ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
            key = 'year' value = NEW /aws1/cl_dynattributevalue( iv_n = |{ '1978' }| ) ) )
       ) ).
    " TYPE REF TO ZCL_AWS1_dyn_PUT_ITEM_OUTPUT
    MESSAGE '2 rows inserted into DynamoDB Table' && iv_table_name TYPE 'I'.
  CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
    DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
    MESSAGE lv_error TYPE 'E'.
  ENDTRY.
  " snippet-end:[dyn.abapv1.insert_into_table]

ENDMETHOD.


METHOD list_tables.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

  DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
  DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.list_tables]
  TRY.
    oo_result = lo_dyn->listtables( ).
    LOOP AT oo_result->get_tablenames( ) INTO DATA(lo_table_name).
      DATA(lv_tablename) = lo_table_name->get_value( ).
      MESSAGE 'Table name is: ' && lv_tablename TYPE 'I'.
    ENDLOOP.
    CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
      DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
      MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[dyn.abapv1.list_tables]

ENDMETHOD.


METHOD query_table.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

  DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
  DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.query_table]

  TRY.
    DATA(lo_resp) = lo_dyn->getitem(
         iv_tablename                = iv_table_name
         it_key                      = VALUE /aws1/cl_dynattributevalue=>tt_key(
          ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
            key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
          ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
            key = 'year' value = NEW /aws1/cl_dynattributevalue( iv_n = |{ '1975' }| ) ) )
         ) ). " TYPE REF TO ZCL_AWS1_dyn_GET_ITEM_OUTPUT
    ot_attr = lo_resp->get_item( ).
    MESSAGE '1 row selected from DynamoDB Table' && iv_table_name TYPE 'I'.
  CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
    DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
    MESSAGE lv_error TYPE 'E'.
  ENDTRY.
  " snippet-end:[dyn.abapv1.query_table]

ENDMETHOD.
ENDCLASS.
