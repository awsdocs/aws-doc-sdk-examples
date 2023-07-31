class ZCL_AWS1_DYN_SCENARIO definition
  public
  final
  create public .

public section.
protected section.
private section.

  methods GETTING_STARTED_WITH_DYN
    importing
      value(IV_TABLE_NAME) type /AWS1/DYNTABLENAME .
ENDCLASS.



CLASS ZCL_AWS1_DYN_SCENARIO IMPLEMENTATION.


  METHOD getting_started_with_dyn.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    " snippet-start:[dyn.abapv1.getting_started_with_dyn]
    " Create an Amazon Dynamo DB table
    TRY.
      DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
      DATA(lo_dyn) = /aws1/cl_dyn_factory=>create( lo_session ).
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
      DATA(lo_result) = lo_dyn->createtable(
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

    " Describe table
    TRY.
      DATA(lo_table) = lo_dyn->describetable( iv_tablename = iv_table_name ).
      lv_tablename = lo_table->get_table( )->ask_tablename( ).
      MESSAGE 'The table name is ' && lv_tablename TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO lo_exception.
        lv_error = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_rt_value_missing.
        lv_error = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.

    " Insert rows into the table
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
     CATCH /aws1/cx_rt_service_generic INTO lo_exception.
       lv_error = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
       MESSAGE lv_error TYPE 'E'.
    ENDTRY.

    "Query table
    TRY.
      DATA(lo_tableitem) = lo_dyn->getitem(
           iv_tablename                = iv_table_name
           it_key                      = VALUE /aws1/cl_dynattributevalue=>tt_key(
            ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
              key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
            ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
              key = 'year' value = NEW /aws1/cl_dynattributevalue( iv_n = |{ '1975' }| ) ) )
           ) ). " TYPE REF TO ZCL_AWS1_dyn_GET_ITEM_OUTPUT
      DATA(ot_attr) = lo_tableitem->get_item( ).
      MESSAGE '1 row selected from DynamoDB Table' && iv_table_name TYPE 'I'.
    CATCH /aws1/cx_rt_service_generic INTO lo_exception.
      lv_error = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
      MESSAGE lv_error TYPE 'E'.
  ENDTRY.

  " Delete table
  TRY.
    lo_dyn->deletetable( iv_tablename = iv_table_name ).
    lo_dyn->get_waiter( )->tablenotexists(
      iv_max_wait_time = 200
      iv_tablename     = iv_table_name
    ).
    MESSAGE 'DynamoDB Table deleted.' TYPE 'I'.
  CATCH /aws1/cx_rt_service_generic INTO lo_exception.
    lv_error = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
    MESSAGE lv_error TYPE 'E'.
  ENDTRY.
  " snippet-end:[dyn.abapv1.getting_started_with_dyn]
ENDMETHOD.
ENDCLASS.
