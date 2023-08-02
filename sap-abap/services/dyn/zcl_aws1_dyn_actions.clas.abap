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
  methods PUT_ITEM
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME .
  methods GET_ITEM
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME
    returning
      value(OT_ATTR) type /AWS1/CL_DYNATTRIBUTEVALUE=>TT_ATTRIBUTEMAP .
  methods UPDATE_ITEM
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME .
  methods DELETE_ITEM
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME .
  methods QUERY_ITEMS
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME .
  methods SCAN_ITEMS
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME .
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
          "( NEW /aws1/cl_dynkeyschemaelement( iv_attributename = 'year' iv_keytype = 'HASH' ) )
          ( NEW /aws1/cl_dynkeyschemaelement( iv_attributename = 'title' iv_keytype = 'HASH' ) )
      ).
      DATA(lt_attributedefinitions) = VALUE /aws1/cl_dynattributedefn=>tt_attributedefinitions(
          ( NEW /aws1/cl_dynattributedefn( iv_attributename = 'title' iv_attributetype = 'S' ) )
          "( NEW /aws1/cl_dynattributedefn( iv_attributename = 'year' iv_attributetype = 'N' ) )
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


METHOD DELETE_ITEM.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

  DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
  DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.delete_item]
  TRY.
    DATA(lo_resp) = lo_dyn->deleteitem(
         iv_tablename                = iv_table_name
         it_key                      = VALUE /aws1/cl_dynattributevalue=>tt_key(
          ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
            key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
          ) ).
    MESSAGE '1 row deleted from DynamoDB Table' && iv_table_name TYPE 'I'.
  CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
    DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
    MESSAGE lv_error TYPE 'E'.
  ENDTRY.
  " snippet-end:[dyn.abapv1.delete_item]

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
    CATCH /aws1/cx_rt_value_missing.
      lv_error = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
      MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[dyn.abapv1.describe_table]

ENDMETHOD.


METHOD get_item.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

  DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
  DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.get_item]
  TRY.
      DATA(lo_resp) = lo_dyn->getitem(
         iv_tablename                = iv_table_name
         it_key                      = VALUE /aws1/cl_dynattributevalue=>tt_key(
           ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
             key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
         ) ). " TYPE REF TO ZCL_AWS1_dyn_GET_ITEM_OUTPUT
      DATA(lo_attr) = lo_resp->get_item( ).
      DATA(lo_title) = lo_attr[ key = 'title' ]-value.
      DATA(lo_year) = lo_attr[ key = 'year' ]-value.
      MESSAGE 'Movie name is: ' && lo_title->get_s( ) TYPE 'I'.
      MESSAGE 'Movie year is: ' && lo_year->get_n( ) TYPE 'I'.
    CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
      DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
      MESSAGE lv_error TYPE 'E'.
  ENDTRY.
  " snippet-end:[dyn.abapv1.get_item]

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


METHOD PUT_ITEM.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

  DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
  DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.put_item]
  TRY.
    DATA(lo_resp) = lo_dyn->putitem(
       iv_tablename = iv_table_name
       it_item      = VALUE /aws1/cl_dynattributevalue=>tt_putiteminputattributemap(
          ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
            key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
          ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
            key = 'year' value = NEW /aws1/cl_dynattributevalue( iv_n = |{ '1975' }| ) ) )
       ) ).
    " TYPE REF TO ZCL_AWS1_dyn_PUT_ITEM_OUTPUT
    MESSAGE '1 row inserted into DynamoDB Table' && iv_table_name TYPE 'I'.
  CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
    DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
    MESSAGE lv_error TYPE 'E'.
  ENDTRY.
  " snippet-end:[dyn.abapv1.put_item]

ENDMETHOD.


METHOD query_items.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

  DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
  DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.query_items]

  TRY.
    DATA(lo_attributelist) = VALUE /AWS1/CL_DYNATTRIBUTEVALUE=>TT_ATTRIBUTEVALUELIST(
              ( NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) ).
    DATA(lo_keyconditions) = VALUE /AWS1/CL_DYNCONDITION=>TT_KEYCONDITIONS(
        ( VALUE /AWS1/CL_DYNCONDITION=>TS_KEYCONDITIONS_MAPROW(
          key = 'title'
          value = NEW /AWS1/CL_DYNCONDITION(
            IT_ATTRIBUTEVALUELIST = lo_attributelist
            IV_COMPARISONOPERATOR = |EQ|
          ) ) ) ).
    DATA(lo_query_result) = lo_dyn->query(
      iv_tablename = iv_table_name
      it_keyconditions = lo_keyconditions ).
    DATA(lo_items) = lo_query_result->get_items( ).
    LOOP AT lo_items INTO DATA(lo_item).
      DATA(lo_title) = lo_item[ key = 'title' ]-value.
      DATA(lo_year) = lo_item[ key = 'year' ]-value.
      MESSAGE 'Movie name is: ' && lo_title->get_s( ) TYPE 'I'.
    ENDLOOP.
  CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
    DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
    MESSAGE lv_error TYPE 'E'.
  ENDTRY.
  " snippet-end:[dyn.abapv1.query_items]

ENDMETHOD.


METHOD scan_items.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

  DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
  DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.scan_items]

  TRY.
    DATA(lo_scan_result) = lo_dyn->scan( iv_tablename = iv_table_name ).
    DATA(lo_items) = lo_scan_result->get_items( ).
    LOOP AT lo_items INTO DATA(lo_item).
      DATA(lo_title) = lo_item[ key = 'title' ]-value.
      DATA(lo_year) = lo_item[ key = 'year' ]-value.
      MESSAGE 'Movie name is: ' && lo_title->get_s( ) TYPE 'I'.
    ENDLOOP.
  CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
    DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
    MESSAGE lv_error TYPE 'E'.
  ENDTRY.
  " snippet-end:[dyn.abapv1.scan_items]

ENDMETHOD.


METHOD UPDATE_ITEM.
  CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

  DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
  DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).

  " snippet-start:[dyn.abapv1.update_item]
  TRY.
    DATA(lt_attributeupdates) = VALUE /AWS1/CL_DYNATTRVALUEUPDATE=>TT_ATTRIBUTEUPDATES(
      ( VALUE /AWS1/CL_DYNATTRVALUEUPDATE=>TS_ATTRIBUTEUPDATES_MAPROW(
          key = 'year' value = NEW /AWS1/CL_DYNATTRVALUEUPDATE(
            io_value  = NEW /aws1/cl_dynattributevalue( iv_n = '1980' )
            iv_action =  |PUT| ) ) ) ).
    DATA(lt_key) = VALUE /aws1/cl_dynattributevalue=>tt_key(
         ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
           key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
       ).
    DATA(lo_resp) = lo_dyn->updateitem(
         iv_tablename        = iv_table_name
         it_key              = lt_key
         IT_ATTRIBUTEUPDATES = lt_attributeupdates
       ).
    MESSAGE '1 item updated in DynamoDB Table' && iv_table_name TYPE 'I'.
  CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
    DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
    MESSAGE lv_error TYPE 'E'.
  ENDTRY.
  " snippet-end:[dyn.abapv1.update_item]

ENDMETHOD.
ENDCLASS.
