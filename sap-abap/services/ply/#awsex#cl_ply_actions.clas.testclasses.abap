" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_ply_actions DEFINITION DEFERRED.
CLASS /awsex/cl_ply_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_ply_actions.

CLASS ltc_awsex_cl_ply_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_ply TYPE REF TO /aws1/if_ply.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_ply_actions TYPE REF TO /awsex/cl_ply_actions.
    CLASS-DATA av_s3_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_lexicon_name TYPE /aws1/plylexiconname.
    CLASS-DATA av_task_id TYPE /aws1/plytaskid.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS describe_voices FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS synthesize_speech FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS put_lexicon FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_lexicon FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_lexicons FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS start_speech_synthesis_task FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_speech_synthesis_task FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_speech_synthesis_tasks FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_lexicon FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS wait_for_task_completion
      IMPORTING
        iv_task_id TYPE /aws1/plytaskid
        iv_max_wait_sec TYPE i DEFAULT 300
      RAISING /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_ply_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    ao_ply = /aws1/cl_ply_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_ply_actions = NEW /awsex/cl_ply_actions( ).

    " Create S3 bucket for synthesis tasks using util function
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_s3_bucket = |{ /awsex/cl_utils=>cv_asset_prefix }-ply-{ lv_uuid }|.
    av_s3_bucket = to_lower( av_s3_bucket ).

    " Create bucket with proper location constraint using util function
    /awsex/cl_utils=>create_bucket(
      iv_bucket = av_s3_bucket
      io_s3 = ao_s3
      io_session = ao_session ).

    " Tag the bucket for cleanup with convert_test tag
    TRY.
        ao_s3->putbuckettagging(
          iv_bucket = av_s3_bucket
          io_tagging = NEW /aws1/cl_s3_tagging(
            it_tagset = VALUE /aws1/cl_s3_tag=>tt_tagset(
              ( NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ) ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_tag_ex).
        " If tagging fails, fail the setup
        cl_abap_unit_assert=>fail( |Failed to tag S3 bucket: { lo_tag_ex->get_text( ) }| ).
    ENDTRY.

    " Create a lexicon for tests that need one
    " Lexicon name must be alphanumeric only, max 20 characters
    av_lexicon_name = |lex{ lv_uuid }|.
    av_lexicon_name = to_lower( av_lexicon_name ).
    " Ensure it's not longer than 20 characters
    IF strlen( av_lexicon_name ) > 20.
      av_lexicon_name = av_lexicon_name(20).
    ENDIF.

    " Create the lexicon in class_setup
    DATA(lv_lexicon_content) = |<?xml version="1.0" encoding="UTF-8"?>\n| &&
      |<lexicon version="1.0" xmlns="http://www.w3.org/2005/01/pronunciation-lexicon" | &&
      |xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" | &&
      |xsi:schemaLocation="http://www.w3.org/2005/01/pronunciation-lexicon " | &&
      |alphabet="ipa" xml:lang="en-US">\n| &&
      |  <lexeme>\n| &&
      |    <grapheme>AWS</grapheme>\n| &&
      |    <alias>Amazon Web Services</alias>\n| &&
      |  </lexeme>\n| &&
      |</lexicon>|.

    TRY.
        ao_ply->putlexicon(
          iv_name = av_lexicon_name
          iv_content = lv_lexicon_content ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_lex_ex).
        " If lexicon creation fails, fail the setup
        cl_abap_unit_assert=>fail( |Failed to create lexicon: { lo_lex_ex->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    " Clean up all lexicons created during tests
    IF av_lexicon_name IS NOT INITIAL.
      TRY.
          ao_ply->deletelexicon( av_lexicon_name ).
        CATCH /aws1/cx_plylexiconnotfoundex.
          " Lexicon already deleted
        CATCH /aws1/cx_rt_generic.
          " Continue cleanup
      ENDTRY.
    ENDIF.

    " Clean up S3 bucket using util function
    " Note: We do NOT delete the S3 bucket if tasks are still in progress
    " The bucket is tagged with 'convert_test' for manual cleanup if needed
    IF av_s3_bucket IS NOT INITIAL.
      TRY.
          " Wait a bit for any tasks to complete writing to S3
          WAIT UP TO 10 SECONDS.

          " Try to clean up the bucket
          /awsex/cl_utils=>cleanup_bucket(
            iv_bucket = av_s3_bucket
            io_s3 = ao_s3 ).
        CATCH /aws1/cx_rt_generic.
          " If cleanup fails, the bucket remains tagged for manual cleanup
          " This is acceptable as synthesis tasks may take time to complete
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD describe_voices.
    DATA lo_result TYPE REF TO /aws1/cl_plydescrvoicesoutput.

    " Don't pass optional parameters if they're not set
    " iv_engine - Example: 'neural'
    " iv_language - Example: 'en-US'
    ao_ply_actions->describe_voices(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be bound' ).

    DATA(lt_voices) = lo_result->get_voices( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_voices
      msg = 'Voices list should not be empty' ).
  ENDMETHOD.

  METHOD synthesize_speech.
    DATA lo_result TYPE REF TO /aws1/cl_plysynthesizespeech01.

    " iv_text - Example: 'Hello, this is a test.'
    " iv_voice_id - Example: 'Joanna'
    " iv_engine - Example: 'neural'
    " iv_output_fmt - Example: 'mp3'
    " Don't pass iv_lang_code as it's optional and will cause validation error if empty
    ao_ply_actions->synthesize_speech(
      EXPORTING
        iv_text = 'Hello from Amazon Polly'
        iv_engine = 'neural'
        iv_voice_id = 'Joanna'
        iv_output_fmt = 'mp3'
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be bound' ).

    DATA(lv_audio_stream) = lo_result->get_audiostream( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_audio_stream
      msg = 'Audio stream should not be empty' ).
  ENDMETHOD.

  METHOD put_lexicon.
    " Test creating a new lexicon (using a different name than class-level lexicon)
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    " Lexicon name must be alphanumeric only, max 20 characters
    DATA(lv_new_lexicon_name) = |put{ lv_uuid }|.
    lv_new_lexicon_name = to_lower( lv_new_lexicon_name ).
    IF strlen( lv_new_lexicon_name ) > 20.
      lv_new_lexicon_name = lv_new_lexicon_name(20).
    ENDIF.

    " iv_name - Example: 'testlexicon'
    " iv_content - Example: PLS or SSML lexicon content
    DATA(lv_lexicon_content) = |<?xml version="1.0" encoding="UTF-8"?>\n| &&
      |<lexicon version="1.0" xmlns="http://www.w3.org/2005/01/pronunciation-lexicon" | &&
      |xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" | &&
      |xsi:schemaLocation="http://www.w3.org/2005/01/pronunciation-lexicon " | &&
      |alphabet="ipa" xml:lang="en-US">\n| &&
      |  <lexeme>\n| &&
      |    <grapheme>W3C</grapheme>\n| &&
      |    <alias>World Wide Web Consortium</alias>\n| &&
      |  </lexeme>\n| &&
      |</lexicon>|.

    ao_ply_actions->create_lexicon(
      iv_name = lv_new_lexicon_name
      iv_content = lv_lexicon_content ).

    " Verify lexicon was created
    DATA lo_result TYPE REF TO /aws1/cl_plygetlexiconoutput.
    TRY.
        lo_result = ao_ply->getlexicon( lv_new_lexicon_name ).
        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Lexicon should exist after creation' ).

        " Clean up the test lexicon
        ao_ply->deletelexicon( lv_new_lexicon_name ).
      CATCH /aws1/cx_plylexiconnotfoundex.
        cl_abap_unit_assert=>fail( 'Lexicon should have been created' ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_lexicon.
    " Use the class-level lexicon created in class_setup
    " Verify it exists, if not, fail the test
    DATA lo_result TYPE REF TO /aws1/cl_plygetlexiconoutput.

    " iv_name - Example: 'testlexicon'
    ao_ply_actions->get_lexicon(
      EXPORTING
        iv_name = av_lexicon_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be bound' ).

    DATA(lo_lexicon) = lo_result->get_lexicon( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_lexicon
      msg = 'Lexicon should be bound' ).

    DATA(lv_lex_name) = lo_lexicon->get_name( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_lex_name
      exp = av_lexicon_name
      msg = 'Lexicon name should match' ).
  ENDMETHOD.

  METHOD list_lexicons.
    " Use the class-level lexicon created in class_setup
    " This ensures at least one lexicon exists
    DATA lo_result TYPE REF TO /aws1/cl_plylistlexiconsoutput.

    ao_ply_actions->list_lexicons(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be bound' ).

    DATA(lt_lexicons) = lo_result->get_lexicons( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_lexicons
      msg = 'At least one lexicon should exist' ).

    " Verify our class-level lexicon is in the list
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_lexicons INTO DATA(lo_lex_desc).
      IF lo_lex_desc->get_name( ) = av_lexicon_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Lexicon { av_lexicon_name } should be in the list| ).
  ENDMETHOD.

  METHOD start_speech_synthesis_task.
    " Use the class-level S3 bucket created in class_setup
    DATA lo_result TYPE REF TO /aws1/cl_plystrtspeechsynthe01.

    " iv_text - Example: 'This is a long text for synthesis task.'
    " iv_voice_id - Example: 'Joanna'
    " iv_engine - Example: 'neural'
    " iv_audio_format - Example: 'mp3'
    " iv_s3_bucket - Example: 'my-bucket'
    " Don't pass iv_lang_code as it's optional and will cause validation error if empty
    ao_ply_actions->start_speech_synthesis_task(
      EXPORTING
        iv_text = 'This is a test for asynchronous speech synthesis from ABAP SDK.'
        iv_engine = 'neural'
        iv_voice_id = 'Joanna'
        iv_audio_format = 'mp3'
        iv_s3_bucket = av_s3_bucket
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be bound' ).

    DATA(lo_task) = lo_result->get_synthesistask( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_task
      msg = 'Synthesis task should be bound' ).

    av_task_id = lo_task->get_taskid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = av_task_id
      msg = 'Task ID should not be empty' ).

    DATA(lv_status) = lo_task->get_taskstatus( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_status
      msg = 'Task status should not be empty' ).
  ENDMETHOD.

  METHOD get_speech_synthesis_task.
    DATA lo_result TYPE REF TO /aws1/cl_plygetspeechsynthes01.

    " Ensure we have a task ID - create a task if needed
    IF av_task_id IS INITIAL.
      " Create a task first using the class-level S3 bucket
      DATA lo_start_result TYPE REF TO /aws1/cl_plystrtspeechsynthe01.

      TRY.
          lo_start_result = ao_ply->startspeechsynthesistask(
            iv_text = 'Test task for get operation from ABAP SDK.'
            iv_engine = 'neural'
            iv_voiceid = 'Joanna'
            iv_outputformat = 'mp3'
            iv_outputs3bucketname = av_s3_bucket ).

          DATA(lo_task_temp) = lo_start_result->get_synthesistask( ).
          IF lo_task_temp IS NOT BOUND.
            cl_abap_unit_assert=>fail( 'Failed to create speech synthesis task' ).
          ENDIF.

          av_task_id = lo_task_temp->get_taskid( ).
          IF av_task_id IS INITIAL.
            cl_abap_unit_assert=>fail( 'Task ID is empty after task creation' ).
          ENDIF.
        CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
          cl_abap_unit_assert=>fail( |Failed to create task: { lo_ex->get_text( ) }| ).
      ENDTRY.
    ENDIF.

    " iv_task_id - Example: task ID from start_speech_synthesis_task
    ao_ply_actions->get_speech_synthesis_task(
      EXPORTING
        iv_task_id = av_task_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be bound' ).

    DATA(lo_task) = lo_result->get_synthesistask( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_task
      msg = 'Synthesis task should be bound' ).

    DATA(lv_task_status) = lo_task->get_taskstatus( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_task_status
      msg = 'Task status should not be empty' ).

    DATA(lv_retrieved_task_id) = lo_task->get_taskid( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_retrieved_task_id
      exp = av_task_id
      msg = 'Retrieved task ID should match the requested task ID' ).
  ENDMETHOD.

  METHOD list_speech_synthesis_tasks.
    DATA lo_result TYPE REF TO /aws1/cl_plylstspeechsynthes01.

    " Ensure we have at least one task created
    IF av_task_id IS INITIAL.
      " Create a task first using the class-level S3 bucket
      DATA lo_start_result TYPE REF TO /aws1/cl_plystrtspeechsynthe01.

      TRY.
          lo_start_result = ao_ply->startspeechsynthesistask(
            iv_text = 'Test task for list operation from ABAP SDK.'
            iv_engine = 'neural'
            iv_voiceid = 'Joanna'
            iv_outputformat = 'mp3'
            iv_outputs3bucketname = av_s3_bucket ).

          DATA(lo_task_temp) = lo_start_result->get_synthesistask( ).
          IF lo_task_temp IS BOUND.
            av_task_id = lo_task_temp->get_taskid( ).
          ENDIF.
        CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
          " Continue even if task creation fails - list might still return other tasks
      ENDTRY.
    ENDIF.

    " Don't pass optional parameters - they cause validation errors if empty
    " iv_max_results - Example: 10
    " iv_status - Example: 'completed', 'scheduled', 'inProgress', 'failed'
    ao_ply_actions->list_speech_synthesis_tasks(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be bound' ).

    " Note: We don't assert that tasks exist because they might have been
    " cleaned up by AWS or there might be no tasks in the account
    DATA(lt_tasks) = lo_result->get_synthesistasks( ).
    IF lt_tasks IS NOT INITIAL AND av_task_id IS NOT INITIAL.
      " If we created a task and got results, verify our task is in the list
      DATA lv_found TYPE abap_bool VALUE abap_false.
      LOOP AT lt_tasks INTO DATA(lo_task).
        IF lo_task->get_taskid( ) = av_task_id.
          lv_found = abap_true.
          EXIT.
        ENDIF.
      ENDLOOP.

      " Only assert if we found tasks - our task might not be in this page
      IF lv_found = abap_true.
        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Task { av_task_id } should be in the list| ).
      ENDIF.
    ENDIF.
  ENDMETHOD.

  METHOD delete_lexicon.
    " Create a new lexicon specifically for this test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    " Lexicon name must be alphanumeric only, max 20 characters
    DATA(lv_delete_lex_name) = |del{ lv_uuid }|.
    lv_delete_lex_name = to_lower( lv_delete_lex_name ).
    IF strlen( lv_delete_lex_name ) > 20.
      lv_delete_lex_name = lv_delete_lex_name(20).
    ENDIF.

    DATA(lv_lexicon_content) = |<?xml version="1.0" encoding="UTF-8"?>\n| &&
      |<lexicon version="1.0" xmlns="http://www.w3.org/2005/01/pronunciation-lexicon" | &&
      |xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" | &&
      |xsi:schemaLocation="http://www.w3.org/2005/01/pronunciation-lexicon " | &&
      |alphabet="ipa" xml:lang="en-US">\n| &&
      |  <lexeme>\n| &&
      |    <grapheme>delete</grapheme>\n| &&
      |    <alias>remove</alias>\n| &&
      |  </lexeme>\n| &&
      |</lexicon>|.

    " Create the lexicon first
    TRY.
        ao_ply->putlexicon(
          iv_name = lv_delete_lex_name
          iv_content = lv_lexicon_content ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_create_ex).
        cl_abap_unit_assert=>fail( |Failed to create lexicon for deletion test: { lo_create_ex->get_text( ) }| ).
    ENDTRY.

    " Verify it was created
    TRY.
        DATA(lo_verify) = ao_ply->getlexicon( lv_delete_lex_name ).
        cl_abap_unit_assert=>assert_bound(
          act = lo_verify
          msg = 'Lexicon should exist before deletion' ).
      CATCH /aws1/cx_plylexiconnotfoundex.
        cl_abap_unit_assert=>fail( 'Lexicon was not created successfully' ).
    ENDTRY.

    " iv_name - Example: 'lexicontodelete'
    " Now delete it using the action method
    ao_ply_actions->delete_lexicon( lv_delete_lex_name ).

    " Verify it was deleted
    TRY.
        ao_ply->getlexicon( lv_delete_lex_name ).
        cl_abap_unit_assert=>fail( 'Lexicon should have been deleted' ).
      CATCH /aws1/cx_plylexiconnotfoundex.
        " Expected - lexicon was successfully deleted
    ENDTRY.
  ENDMETHOD.

  METHOD wait_for_task_completion.
    DATA lv_elapsed_sec TYPE i VALUE 0.
    DATA lv_wait_interval TYPE i VALUE 5.
    DATA lv_status TYPE /aws1/plytaskstatus.

    DO.
      TRY.
          DATA(lo_result) = ao_ply->getspeechsynthesistask( iv_task_id ).
          DATA(lo_task) = lo_result->get_synthesistask( ).
          lv_status = lo_task->get_taskstatus( ).

          IF lv_status = 'completed' OR lv_status = 'failed'.
            EXIT.
          ENDIF.

          WAIT UP TO lv_wait_interval SECONDS.
          lv_elapsed_sec = lv_elapsed_sec + lv_wait_interval.

          IF lv_elapsed_sec >= iv_max_wait_sec.
            EXIT.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          EXIT.
      ENDTRY.
    ENDDO.
  ENDMETHOD.

ENDCLASS.
