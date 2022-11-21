" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_kns_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL HARMLESS.

  PRIVATE SECTION.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_kns TYPE REF TO /aws1/if_kns.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_kns_actions TYPE REF TO zcl_aws1_kns_actions.

    METHODS setup FOR TESTING.
    METHODS create_stream FOR TESTING.
    METHODS delete_stream FOR TESTING.
    METHODS list_streams FOR TESTING.
    METHODS describe_stream FOR TESTING.
    METHODS put_record FOR TESTING.
    METHODS get_records FOR TESTING.
    METHODS register_stream_consumer FOR TESTING.

ENDCLASS.       "ltc_Zcl_Aws1_Kns_Actions


CLASS ltc_zcl_aws1_kns_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_kns = /aws1/cl_kns_factory=>create( ao_session ).
    ao_kns_actions = NEW zcl_aws1_kns_actions( ).
  ENDMETHOD.

  METHOD create_stream.

    DATA lv_stream_name TYPE /aws1/knsstreamname.
    DATA lo_stream_describe_result TYPE REF TO /aws1/cl_knsdescrstreamoutput.
    DATA lo_stream_description TYPE REF TO /aws1/cl_knsstreamdescription.
    DATA lv_stream_status TYPE /aws1/knsstreamstatus.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_shard_count TYPE /aws1/knspositiveintegerobject VALUE 1.

    "Define Stream name
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_stream_name = 'code-example-kns-stream-' && lv_uuid_16.
    TRANSLATE lv_stream_name TO LOWER CASE.

    "Testing
    ao_kns_actions->create_stream(
        iv_stream_name        = lv_stream_name
        iv_shard_count        = cv_shard_count
      ).

    "Wait for stream to become active
    lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
    lo_stream_description = lo_stream_describe_result->get_streamdescription( ).
    WHILE lo_stream_description->get_streamstatus( ) <> 'ACTIVE'.
      IF sy-index = 30.
        EXIT.               "maximum 5 minutes
      ENDIF.
      WAIT UP TO 10 SECONDS.
      lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
      lo_stream_description =  lo_stream_describe_result->get_streamdescription( ).
    ENDWHILE.

    "Testing
    lv_found = abap_false.
    IF lo_stream_description->get_streamstatus( ) = 'ACTIVE'.
      lv_found = abap_true.
    ENDIF.

    "Validation
    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Stream cannot be found|
    ).

    "Cleanup
    ao_kns->deletestream(
        iv_streamname = lv_stream_name ).

  ENDMETHOD.


  METHOD delete_stream.

    DATA lv_stream_name TYPE /aws1/knsstreamname.
    DATA lo_stream_describe_result TYPE REF TO /aws1/cl_knsdescrstreamoutput.
    DATA lo_stream_list_result TYPE REF TO /aws1/cl_knsliststreamsoutput.
    DATA lo_stream_description TYPE REF TO /aws1/cl_knsstreamdescription.
    DATA lv_stream_status TYPE /aws1/knsstreamstatus.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_shard_count TYPE /aws1/knspositiveintegerobject VALUE 1.

    "Define name
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_stream_name = 'code-example-kns-stream-' && lv_uuid_16.
    TRANSLATE lv_stream_name TO LOWER CASE.

    "Create stream
    ao_kns->createstream(
      EXPORTING
        iv_streamname        = lv_stream_name
        iv_shardcount        = cv_shard_count
      ).

    "Wait for stream to become active
    lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
    lo_stream_description = lo_stream_describe_result->get_streamdescription( ).
    WHILE lo_stream_description->get_streamstatus( ) <> 'ACTIVE'.
      IF sy-index = 30.
        EXIT.               "maximum 5 minutes
      ENDIF.
      WAIT UP TO 10 SECONDS.
      lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
      lo_stream_description = lo_stream_describe_result->get_streamdescription( ).
    ENDWHILE.

    "Testing
    ao_kns_actions->delete_stream(
      EXPORTING
        iv_stream_name        = lv_stream_name
    ).

    "Check if it is deleted
    lv_found = abap_true.
    lo_stream_list_result = ao_kns->liststreams( iv_exclusivestartstreamname = lv_stream_name ).

    IF  lo_stream_list_result->has_streamnames( ) = 'X'.
      lv_found = abap_false.
    ENDIF.

    cl_abap_unit_assert=>assert_false(
       act                    = lv_found
       msg                    = |Stream not deleted|
    ).

    "Nothing to clean up

  ENDMETHOD.

  METHOD list_streams.

    DATA lv_stream_name TYPE /aws1/knsstreamname.
    DATA lo_stream_describe_result TYPE REF TO /aws1/cl_knsdescrstreamoutput.
    DATA lo_stream_description TYPE REF TO /aws1/cl_knsstreamdescription.
    DATA lo_stream_list_result TYPE REF TO /aws1/cl_knsliststreamsoutput.
    DATA lv_stream_status TYPE /aws1/knsstreamstatus.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_shard_count TYPE /aws1/knspositiveintegerobject VALUE 1.
    CONSTANTS cv_limit TYPE /aws1/knsliststreamsinputlimit VALUE 20.

    "Define stream
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_stream_name = 'code-example-kns-stream-' && lv_uuid_16.
    TRANSLATE lv_stream_name TO LOWER CASE.

    "Create stream
    ao_kns->createstream(
      EXPORTING
        iv_streamname        = lv_stream_name
        iv_shardcount        = cv_shard_count
      ).

    "Wait for stream to become active
    lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
    lo_stream_description = lo_stream_describe_result->get_streamdescription( ).
    WHILE lo_stream_description->get_streamstatus( ) <> 'ACTIVE'.
      IF sy-index = 30.
        EXIT.               "maximum 5 minutes
      ENDIF.
      WAIT UP TO 10 SECONDS.
      lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
      lo_stream_description =  lo_stream_describe_result->get_streamdescription( ).
    ENDWHILE.

    "Testing
    CALL METHOD ao_kns_actions->list_streams(
      EXPORTING
        iv_limit  = cv_limit
      IMPORTING
        oo_result = lo_stream_list_result
                    ).

    "Validation
    lv_found = abap_false.

    IF  lo_stream_list_result->has_streamnames( ) = 'X'.
      lv_found = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Stream not found|
    ).

    "Cleanup
    ao_kns->deletestream(
        iv_streamname = lv_stream_name
        ).

  ENDMETHOD.

  METHOD describe_stream.

    DATA lv_stream_name TYPE /aws1/knsstreamname.
    DATA lo_stream_describe_result TYPE REF TO /aws1/cl_knsdescrstreamoutput.
    DATA lo_stream_description TYPE REF TO /aws1/cl_knsstreamdescription.
    DATA lo_stream_list_result TYPE REF TO /aws1/cl_knsliststreamsoutput.
    DATA lv_stream_status TYPE /aws1/knsstreamstatus.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_shard_count TYPE /aws1/knspositiveintegerobject VALUE 1.

    "Define name
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_stream_name = 'code-example-kns-stream-' && lv_uuid_16.
    TRANSLATE lv_stream_name TO LOWER CASE.

    "Create stream
    ao_kns->createstream(
      EXPORTING
        iv_streamname        = lv_stream_name
        iv_shardcount        = cv_shard_count
      ).

    "Wait for stream to become active
    lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
    lo_stream_description = lo_stream_describe_result->get_streamdescription( ).
    WHILE lo_stream_description->get_streamstatus( ) <> 'ACTIVE'.
      IF sy-index = 30.
        EXIT.               "maximum 5 minutes
      ENDIF.
      WAIT UP TO 10 SECONDS.
      lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
      lo_stream_description =  lo_stream_describe_result->get_streamdescription( ).
    ENDWHILE.

    "Testing
    CALL METHOD ao_kns_actions->describe_stream(
      EXPORTING
        iv_stream_name = lv_stream_name
      IMPORTING
        oo_result      = lo_stream_describe_result
                         ).

    "Validation
    lv_found = abap_false.

    lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
    lo_stream_description = lo_stream_describe_result->get_streamdescription( ).
    IF  lo_stream_description->get_streamstatus( ) = 'ACTIVE'.
      lv_found = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Stream not found|
    ).

    "Cleanup
    ao_kns->deletestream(
        iv_streamname = lv_stream_name
        ).

  ENDMETHOD.

  METHOD put_record.

    DATA lo_stream_describe_result TYPE REF TO /aws1/cl_knsdescrstreamoutput.
    DATA lo_stream_description TYPE REF TO /aws1/cl_knsstreamdescription.
    DATA lv_stream_status TYPE /aws1/knsstreamstatus.
    DATA lo_put_record_output TYPE REF TO /aws1/cl_knsputrecordoutput.
    DATA lo_get_record_output TYPE REF TO /aws1/cl_knsgetrecordsoutput.
    DATA lo_sharditerator TYPE REF TO /aws1/cl_knsgetsharditerator01.
    DATA lt_record_list TYPE /aws1/cl_knsrecord=>tt_recordlist.
    DATA lv_record_data TYPE /aws1/knsdata.
    DATA lv_stream_name TYPE /aws1/knsstreamname.
    DATA lv_shardid TYPE /aws1/knsshardid.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA(lv_data) = /aws1/cl_rt_util=>string_to_xstring(
      `{`  &&
        `"word": "This",`  &&
        `"word": "is"` &&
        `"word": "a"` &&
        `"word": "code"` &&
        `"word": "example"` &&
      `}`
    ).

    CONSTANTS cv_shard_count TYPE /aws1/knspositiveintegerobject VALUE 1.
    CONSTANTS cv_partition_key TYPE /aws1/knspartitionkey VALUE '123'.
    CONSTANTS cv_sharditeratortype TYPE /aws1/knssharditeratortype VALUE 'TRIM_HORIZON'.

    "Define name
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_stream_name = 'code-example-kns-stream-' && lv_uuid_16.
    TRANSLATE lv_stream_name TO LOWER CASE.

    "Create stream
    ao_kns->createstream(
      EXPORTING
        iv_streamname        = lv_stream_name
        iv_shardcount        = cv_shard_count
      ).

    "Wait for stream to become active
    lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
    lo_stream_description = lo_stream_describe_result->get_streamdescription( ).
    WHILE lo_stream_description->get_streamstatus( ) <> 'ACTIVE'.
      IF sy-index = 30.
        EXIT.               "maximum 5 minutes
      ENDIF.
      WAIT UP TO 10 SECONDS.
      lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
      lo_stream_description =  lo_stream_describe_result->get_streamdescription( ).
    ENDWHILE.

    "Testing
    CALL METHOD ao_kns_actions->put_record(
      EXPORTING
        iv_stream_name   = lv_stream_name
        iv_data          = lv_data
        iv_partition_key = cv_partition_key
      IMPORTING
        oo_result        = lo_put_record_output
                           ).

    "Get the shard ID
    lv_shardid = lo_put_record_output->get_shardid( ).

    "Get the shard iterator using the shard ID
    lo_sharditerator = ao_kns->getsharditerator(
        iv_shardid = lv_shardid
        iv_sharditeratortype = cv_sharditeratortype
        iv_streamname = lv_stream_name
    ).

    "Get the record using the shard iterator
    lo_get_record_output = ao_kns->getrecords(
        iv_sharditerator   = lo_sharditerator->get_sharditerator( )
    ).

    lt_record_list = lo_get_record_output->get_records( ).
    LOOP AT lt_record_list INTO DATA(lo_record).
      lv_record_data = lo_record->get_data( ).
    ENDLOOP.

    "Validation
    lv_found = abap_false.

    IF lv_record_data = lv_data.
      lv_found = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Record not found|
    ).

    "Cleanup
    ao_kns->deletestream(
        iv_streamname = lv_stream_name
        ).

  ENDMETHOD.

  METHOD get_records.

    DATA lo_stream_describe_result TYPE REF TO /aws1/cl_knsdescrstreamoutput.
    DATA lo_stream_description TYPE REF TO /aws1/cl_knsstreamdescription.
    DATA lv_stream_status TYPE /aws1/knsstreamstatus.
    DATA lo_put_record_output TYPE REF TO /aws1/cl_knsputrecordoutput.
    DATA lo_get_record_output TYPE REF TO /aws1/cl_knsgetrecordsoutput.
    DATA lo_sharditerator TYPE REF TO /aws1/cl_knsgetsharditerator01.
    DATA lt_record_list TYPE /aws1/cl_knsrecord=>tt_recordlist.
    DATA lv_record_data TYPE /aws1/knsdata.
    DATA lv_stream_name TYPE /aws1/knsstreamname.
    DATA lv_shardid TYPE /aws1/knsshardid.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA(lv_data) = /aws1/cl_rt_util=>string_to_xstring(
      `{`  &&
        `"word": "This",`  &&
        `"word": "is"` &&
        `"word": "a"` &&
        `"word": "code"` &&
        `"word": "example"` &&
      `}`
    ).

    CONSTANTS cv_shard_count TYPE /aws1/knspositiveintegerobject VALUE 1.
    CONSTANTS cv_partition_key TYPE /aws1/knspartitionkey VALUE '123'.
    CONSTANTS cv_sharditeratortype TYPE /aws1/knssharditeratortype VALUE 'TRIM_HORIZON'.

    "Define name
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_stream_name = 'code-example-kns-stream-' && lv_uuid_16.
    TRANSLATE lv_stream_name TO LOWER CASE.

    "Create stream
    ao_kns->createstream(
      EXPORTING
        iv_streamname        = lv_stream_name
        iv_shardcount        = cv_shard_count
      ).

    "Wait for stream to become active
    lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
    lo_stream_description = lo_stream_describe_result->get_streamdescription( ).

    WHILE lo_stream_description->get_streamstatus( ) <> 'ACTIVE'.
      IF sy-index = 30.
        EXIT.               "maximum 5 minutes
      ENDIF.
      WAIT UP TO 10 SECONDS.
      lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
      lo_stream_description =  lo_stream_describe_result->get_streamdescription( ).
    ENDWHILE.

    "Create a record
    lo_put_record_output = ao_kns->putrecord(

        iv_streamname   = lv_stream_name
        iv_data          = lv_data
        iv_partitionkey = cv_partition_key
    ).

    "Get the shard ID
    lv_shardid = lo_put_record_output->get_shardid( ).

    "Get the shard iteraator
    lo_sharditerator = ao_kns->getsharditerator(
        iv_shardid = lv_shardid
        iv_sharditeratortype = cv_sharditeratortype
        iv_streamname = lv_stream_name
    ).

    "Testing
    CALL METHOD ao_kns_actions->get_records(
      EXPORTING
        iv_shard_iterator = lo_sharditerator->get_sharditerator( )
      IMPORTING
        oo_result         = lo_get_record_output
                            ).

    "Get records
    lt_record_list = lo_get_record_output->get_records( ).

    LOOP AT lt_record_list INTO DATA(lo_record).
      lv_record_data = lo_record->get_data( ).
    ENDLOOP.

    "Validation
    lv_found = abap_false.
    IF lv_record_data = lv_data.
      lv_found = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Record not found|
    ).

    "Cleanup
    ao_kns->deletestream(
        iv_streamname = lv_stream_name
        ).

  ENDMETHOD.

  METHOD register_stream_consumer.

    DATA lo_stream_describe_result TYPE REF TO /aws1/cl_knsdescrstreamoutput.
    DATA lo_stream_description TYPE REF TO /aws1/cl_knsstreamdescription.
    DATA lv_stream_status TYPE /aws1/knsstreamstatus.
    DATA lo_put_record_output TYPE REF TO /aws1/cl_knsputrecordoutput.
    DATA lo_get_record_output TYPE REF TO /aws1/cl_knsgetrecordsoutput.
    DATA lo_sharditerator TYPE REF TO /aws1/cl_knsgetsharditerator01.
    DATA lo_knsregstreamconsout TYPE REF TO /aws1/cl_knsregstreamconsout.
    DATA lt_record_list TYPE /aws1/cl_knsrecord=>tt_recordlist.
    DATA lv_record_data TYPE /aws1/knsdata.
    DATA lv_stream_name TYPE /aws1/knsstreamname.
    DATA lv_consumer_name TYPE /aws1/knsconsumername.
    DATA lv_shardid TYPE /aws1/knsshardid.
    DATA lv_stream_arn TYPE /aws1/knsstreamarn.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_knsliststreamconsout TYPE REF TO /aws1/cl_knsliststreamconsout.
    DATA lo_knsconsumer TYPE REF TO /aws1/cl_knsconsumer.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_shard_count TYPE /aws1/knspositiveintegerobject VALUE 1.
    CONSTANTS cv_partition_key TYPE /aws1/knspartitionkey VALUE '123'.
    CONSTANTS cv_sharditeratortype TYPE /aws1/knssharditeratortype VALUE 'TRIM_HORIZON'.

    DATA(lv_data) = /aws1/cl_rt_util=>string_to_xstring(
      `{`  &&
        `"word": "This",`  &&
        `"word": "is"` &&
        `"word": "a"` &&
        `"word": "code"` &&
        `"word": "example"` &&
      `}`
    ).

    "Define name
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_stream_name = 'code-example-kns-stream-' && lv_uuid_16.
    TRANSLATE lv_stream_name TO LOWER CASE.
    lv_consumer_name = 'code-example-kns-consumer-' && lv_uuid_16.
    TRANSLATE lv_consumer_name TO LOWER CASE.

    "Create stream
    ao_kns->createstream(
      EXPORTING
        iv_streamname        = lv_stream_name
        iv_shardcount        = cv_shard_count
      ).

    "Wait for stream to become active
    lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
    lo_stream_description = lo_stream_describe_result->get_streamdescription( ).
    WHILE lo_stream_description->get_streamstatus( ) <> 'ACTIVE'.
      IF sy-index = 30.
        EXIT.               "maximum 5 minutes
      ENDIF.
      WAIT UP TO 10 SECONDS.
      lo_stream_describe_result = ao_kns->describestream( iv_streamname = lv_stream_name ).
      lo_stream_description =  lo_stream_describe_result->get_streamdescription( ).
    ENDWHILE.

    "Get stream ARN
    lv_stream_arn = lo_stream_description->get_streamarn( ).

    "Testing
    CALL METHOD ao_kns_actions->register_stream_consumer(
      EXPORTING
        iv_consumer_name = lv_consumer_name
        iv_stream_arn    = lv_stream_arn
      IMPORTING
        oo_result        = lo_knsregstreamconsout
                           ).

    "Validation
    lv_found = abap_false.

    lo_knsliststreamconsout = ao_kns->liststreamconsumers(
        iv_streamarn    = lv_stream_arn
    ).
    lo_knsconsumer = lo_knsregstreamconsout->get_consumer( ).

    IF lo_knsconsumer->get_consumername( ) = lv_consumer_name.
      lv_found = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Record not found|
    ).

    "Cleanup
    ao_kns->deregisterstreamconsumer(
        iv_streamarn    = lv_stream_arn
        iv_consumername = lv_consumer_name
        ).

    ao_kns->deletestream(
        iv_streamname = lv_stream_name
        ).
  ENDMETHOD.

ENDCLASS.
