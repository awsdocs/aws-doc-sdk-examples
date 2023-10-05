" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_dyn_actions DEFINITION DEFERRED.
CLASS zcl_aws1_dyn_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_dyn_actions.

CLASS ltc_zcl_aws1_dyn_actions DEFINITION FOR TESTING
  DURATION LONG
  RISK LEVEL HARMLESS.

  PROTECTED SECTION.
    METHODS: create_table FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_table FOR TESTING RAISING /aws1/cx_rt_generic,
      list_tables FOR TESTING RAISING /aws1/cx_rt_generic,
      put_item FOR TESTING RAISING /aws1/cx_rt_generic,
      get_item FOR TESTING RAISING /aws1/cx_rt_generic,
      query_table FOR TESTING RAISING /aws1/cx_rt_generic,
      scan_items FOR TESTING RAISING /aws1/cx_rt_generic,
      update_item FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_item FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_table FOR TESTING RAISING /aws1/cx_rt_generic.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_dyn TYPE REF TO /aws1/if_dyn.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_dyn_actions TYPE REF TO zcl_aws1_dyn_actions.
    DATA av_table_name TYPE /aws1/dyntablename.

    METHODS setup RAISING /aws1/cx_rt_generic.
    METHODS teardown RAISING /aws1/cx_rt_generic.

    METHODS put_item_local
       IMPORTING iv_title TYPE string
                 iv_year TYPE numeric
                 iv_rating TYPE numeric
       RAISING /aws1/cx_rt_generic.
    METHODS delete_table_local RAISING /aws1/cx_rt_generic.
    METHODS create_table_local RAISING /aws1/cx_rt_generic.
    METHODS assert_table_exists RAISING /aws1/cx_rt_generic.
    METHODS assert_table_notexists RAISING /aws1/cx_rt_generic.
    METHODS query_table_local
       IMPORTING iv_year TYPE numeric
       RETURNING VALUE(ot_items) TYPE /aws1/cl_dynattributevalue=>tt_itemlist
       RAISING /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_zcl_aws1_dyn_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_dyn = /aws1/cl_dyn_factory=>create( ao_session ).
    ao_dyn_actions = NEW zcl_aws1_dyn_actions( ).
    av_table_name = |code-example-create-table|.
  ENDMETHOD.

  METHOD teardown.
    delete_table_local( ).
  ENDMETHOD.

  METHOD create_table.
    DATA(lo_table) = ao_dyn_actions->create_table( av_table_name ).
    assert_table_exists( ).
    MESSAGE 'create_table successful' TYPE 'I'.
  ENDMETHOD.

  METHOD describe_table.
    create_table_local( ).
    DATA(lo_table_description) = ao_dyn_actions->describe_table(
      av_table_name ).
    DATA(lv_returned_tablename) = lo_table_description->get_table( )->ask_tablename( ).
    cl_abap_unit_assert=>assert_equals(
            exp = av_table_name
            act = lv_returned_tablename
            msg = |Expected the table name to be { av_table_name } but found { lv_returned_tablename }| ).
    MESSAGE 'describe_table successful' TYPE 'I'.
  ENDMETHOD.

  METHOD list_tables.
    create_table_local( ).
    DATA(lo_tables) = ao_dyn_actions->list_tables( ).

    LOOP AT lo_tables->get_tablenames( ) INTO DATA(lo_table_name).
      IF lo_table_name->get_value( ) = av_table_name.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |List table is successful| ).
    MESSAGE 'list_tables successful' TYPE 'I'.
  ENDMETHOD.

  METHOD put_item.
    create_table_local( ).
    DATA(lt_item) = VALUE /aws1/cl_dynattributevalue=>tt_putiteminputattributemap(
      ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
       key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
      ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
       key = 'year' value = NEW /aws1/cl_dynattributevalue( iv_n = '1975' ) ) )
      ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
       key = 'rating' value = NEW /aws1/cl_dynattributevalue( iv_n = '7.8' ) ) ) ).
    ao_dyn_actions->put_item( iv_table_name = av_table_name
      it_item = lt_item ).
    DATA(lt_items) = query_table_local( '1975' ).
    READ TABLE lt_items INTO DATA(lt_attributes) INDEX 1.
    DATA(lo_rating) = lt_attributes[ key = 'rating' ]-value.
    DATA(lv_rating) = lo_rating->ask_n( ).
    cl_abap_unit_assert=>assert_equals( exp = |7.8|
       act = lv_rating
       msg = |Expected rating 7.5, found { lv_rating } | ).
    MESSAGE 'put_item successful' TYPE 'I'.
  ENDMETHOD.

  METHOD get_item.
    create_table_local( ).
    put_item_local( iv_title = 'Jaws'
      iv_year = 1975
      iv_rating = '7.5' ).
    DATA(lo_item) = ao_dyn_actions->get_item( iv_table_name = av_table_name
       it_key = VALUE /aws1/cl_dynattributevalue=>tt_key(
           ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
             key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
           ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
             key = 'year' value = NEW /aws1/cl_dynattributevalue( iv_n = '1975' ) ) )
           ) ).
    DATA(lt_attributes) = lo_item->get_item( ).
    DATA(lo_rating) = lt_attributes[ key = 'rating' ]-value.
    DATA(lv_rating) = lo_rating->ask_n( ).
    cl_abap_unit_assert=>assert_equals( exp = |7.5|
       act = lv_rating
       msg = |Expected rating 7.5, found { lv_rating } | ).
    MESSAGE 'get_item successful' TYPE 'I'.
  ENDMETHOD.

  METHOD query_table.
    create_table_local( ).
    put_item_local( iv_title = 'Jaws'
      iv_year = 1975
      iv_rating = '7.5' ).
    put_item_local( iv_title = 'Star Wars'
      iv_year = 1979
      iv_rating = '8.1' ).
    put_item_local( iv_title = 'Barbie'
      iv_year = 2023
      iv_rating = '7.9' ).
    DATA(lo_query_result) = ao_dyn_actions->query_table( iv_table_name = av_table_name
        iv_year = 1975 ).
    READ TABLE lo_query_result->get_items( ) INTO DATA(lt_item) INDEX 1.
    DATA(lo_title) = lt_item[ key = 'title' ]-value.
    DATA(lv_title) = lo_title->ask_s( ).
    cl_abap_unit_assert=>assert_equals( exp = |Jaws|
       act = lv_title
       msg = |Expected title Jaws, found { lv_title }| ).
    MESSAGE 'query_table successful' TYPE 'I'.
  ENDMETHOD.

  METHOD scan_items.
    create_table_local( ).
    put_item_local( iv_title = 'Jaws'
      iv_year = 1975
      iv_rating = '7.5' ).
    put_item_local( iv_title = 'Star Wars'
      iv_year = 1979
      iv_rating = '8.1' ).
    put_item_local( iv_title = 'Barbie'
      iv_year = 2023
      iv_rating = '7.8' ).
    " Scan table for rating higher than 7.8
    DATA(lo_scan_result) = ao_dyn_actions->scan_items( iv_table_name = av_table_name
      iv_rating = '7.8' ).
    DATA(lv_count) = lo_scan_result->get_count( ).
    cl_abap_unit_assert=>assert_equals( exp = |2|
       act = lv_count
       msg = |Expected count 3, found { |lv_count| }| ).
    MESSAGE 'scan_item successful' TYPE 'I'.
  ENDMETHOD.

  METHOD update_item.
    create_table_local( ).
    put_item_local( iv_title = 'Jaws'
      iv_year = 1975
      iv_rating = '7.5' ).
    put_item_local( iv_title = 'Star Wars'
      iv_year = 1979
      iv_rating = '8.1' ).
    DATA(lt_attributeupdates) = VALUE /aws1/cl_dynattrvalueupdate=>tt_attributeupdates(
      ( VALUE /aws1/cl_dynattrvalueupdate=>ts_attributeupdates_maprow(
      key = 'rating' value = NEW /aws1/cl_dynattrvalueupdate(
        io_value  = NEW /aws1/cl_dynattributevalue( iv_n = '7.6' )
        iv_action = |PUT| ) ) ) ).
    DATA(lt_key) = VALUE /aws1/cl_dynattributevalue=>tt_key(
      ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
       key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
      ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
       key = 'year' value = NEW /aws1/cl_dynattributevalue( iv_n = '1975' ) ) ) ).
    DATA(lo_resp) = ao_dyn_actions->update_item(
      iv_table_name        = av_table_name
      it_item_key              = lt_key
      it_attribute_updates = lt_attributeupdates ).
    " Use query item to verify that the update was successful.
    DATA(lt_attributelist) = VALUE /aws1/cl_dynattributevalue=>tt_attributevaluelist(
            ( NEW /aws1/cl_dynattributevalue( iv_n = '1975' ) ) ).
    DATA(lt_key_conditions) = VALUE /aws1/cl_dyncondition=>tt_keyconditions(
        ( VALUE /aws1/cl_dyncondition=>ts_keyconditions_maprow(
        key = 'year'
        value = NEW /aws1/cl_dyncondition(
          it_attributevaluelist = lt_attributelist
          iv_comparisonoperator = |EQ|
        ) ) ) ).
    DATA(lt_items) = query_table_local( 1975 ).
    READ TABLE lt_items INTO DATA(lt_item) INDEX 1.
    DATA(lo_rating) = lt_item[ key = 'rating' ]-value.
    DATA(lv_rating) = lo_rating->ask_n( ).
    cl_abap_unit_assert=>assert_equals( exp = |7.6|
       act = lv_rating
       msg = |Expected ratig 7.6, found { lv_rating }| ).
    MESSAGE 'update_item successful' TYPE 'I'.
  ENDMETHOD.

  METHOD delete_item.
    create_table_local( ).
    put_item_local( iv_title = 'Jaws'
      iv_year = 1975
      iv_rating = '7.5' ).
    put_item_local( iv_title = 'Star Wars'
      iv_year = 1975
      iv_rating = '8.1' ).
    DATA(lt_key) = VALUE /aws1/cl_dynattributevalue=>tt_key(
          ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
            key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = 'Jaws' ) ) )
          ( VALUE /aws1/cl_dynattributevalue=>ts_key_maprow(
            key = 'year' value = NEW /aws1/cl_dynattributevalue( iv_n = '1975' ) ) ) ).
    ao_dyn_actions->delete_item( iv_table_name = av_table_name
      it_key_input = lt_key ).
    DATA(lt_items) = query_table_local( '1975' ).
    DATA(lv_count) = lines( lt_items ).
    cl_abap_unit_assert=>assert_equals( exp = |1|
       act = lv_count
       msg = |Expected count 1, found { |lv_count| }| ).
    MESSAGE 'delete_item successful' TYPE 'I'.
  ENDMETHOD.

  METHOD delete_table.
    create_table_local( ).
    ao_dyn_actions->delete_table( av_table_name ).
    assert_table_notexists( ).
    MESSAGE 'delete_table successful' TYPE 'I'.
  ENDMETHOD.

  METHOD assert_table_exists.
    DATA(lv_status) = ao_dyn->describetable( iv_tablename = av_table_name )->get_table( )->get_tablestatus( ).
    lv_status = ao_dyn->describetable( iv_tablename = av_table_name )->get_table( )->get_tablestatus( ).
    cl_abap_unit_assert=>assert_equals(
            exp = lv_status
            act = 'ACTIVE'
            msg = |Expected the table to be in 'ACTIVE' status but received { lv_status }| ).
  ENDMETHOD.

  METHOD assert_table_notexists.
    TRY.
        DATA(lv_status) = ao_dyn->describetable( iv_tablename = av_table_name )->get_table( )->get_tablestatus( ).
        /aws1/cl_rt_assert_abap=>assert_missed_exception( iv_exception = |/AWS1/CX_RT_SERVICE_GENERIC| ).
      CATCH /aws1/cx_rt_service_generic.
      "ignore. expected since the table does not exist
    ENDTRY.
  ENDMETHOD.

  METHOD delete_table_local.
    TRY.
        DATA(lo_resp) = ao_dyn->deletetable( av_table_name ).
        ao_dyn->get_waiter( )->tablenotexists(
          iv_max_wait_time = 200
          iv_tablename     = av_table_name ).
      CATCH /aws1/cx_dynresourcenotfoundex.
    ENDTRY.
  ENDMETHOD.

  METHOD put_item_local.
    DATA(lt_item) = VALUE /aws1/cl_dynattributevalue=>tt_putiteminputattributemap(
        ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
         key = 'title' value = NEW /aws1/cl_dynattributevalue( iv_s = iv_title ) ) )
        ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
         key = 'year' value = NEW /aws1/cl_dynattributevalue( iv_n = |{ iv_year }| ) ) )
        ( VALUE /aws1/cl_dynattributevalue=>ts_putiteminputattrmap_maprow(
         key = 'rating' value = NEW /aws1/cl_dynattributevalue( iv_n = |{ iv_rating }| ) ) ) ).
    ao_dyn->putitem( iv_tablename = av_table_name
      it_item = lt_item ).
  ENDMETHOD.

  METHOD create_table_local.
    TRY.
        DATA(lt_keyschema) = VALUE /aws1/cl_dynkeyschemaelement=>tt_keyschema(
          ( NEW /aws1/cl_dynkeyschemaelement( iv_attributename = 'year'
                                              iv_keytype = 'HASH' ) )
          ( NEW /aws1/cl_dynkeyschemaelement( iv_attributename = 'title'
                                              iv_keytype = 'RANGE' ) ) ).
        DATA(lt_attributedefinitions) = VALUE /aws1/cl_dynattributedefn=>tt_attributedefinitions(
          ( NEW /aws1/cl_dynattributedefn( iv_attributename = 'year'
                                           iv_attributetype = 'N' ) )
          ( NEW /aws1/cl_dynattributedefn( iv_attributename = 'title'
                                           iv_attributetype = 'S' ) ) ).

        " Adjust read/write capacities as desired.
        DATA(lo_dynprovthroughput)  = NEW /aws1/cl_dynprovthroughput(
          iv_readcapacityunits = 5
          iv_writecapacityunits = 5 ).
        ao_dyn->createtable(
          it_keyschema = lt_keyschema
          iv_tablename = av_table_name
          it_attributedefinitions = lt_attributedefinitions
          io_provisionedthroughput = lo_dynprovthroughput ).
        ao_dyn->get_waiter( )->tableexists(
          iv_max_wait_time = 200
          iv_tablename     = av_table_name ).
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_genericex).
        DATA(lv_error) = |"{ lo_genericex->av_err_code }" - { lo_genericex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
  ENDMETHOD.

  METHOD query_table_local.
    TRY.
        DATA(lt_attributelist) = VALUE /aws1/cl_dynattributevalue=>tt_attributevaluelist(
            ( NEW /aws1/cl_dynattributevalue( iv_n = |{ iv_year }| ) ) ).
        DATA(lt_key_conditions) = VALUE /aws1/cl_dyncondition=>tt_keyconditions(
          ( VALUE /aws1/cl_dyncondition=>ts_keyconditions_maprow(
          key = 'year'
          value = NEW /aws1/cl_dyncondition(
          it_attributevaluelist = lt_attributelist
          iv_comparisonoperator = |EQ|
          ) ) ) ).
        DATA(lo_result) = ao_dyn->query(
          iv_tablename = av_table_name
          it_keyconditions = lt_key_conditions ).
        ot_items = lo_result->get_items( ).
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_genericex).
        DATA(lv_error) = |"{ lo_genericex->av_err_code }" - { lo_genericex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
  ENDMETHOD.
ENDCLASS.
