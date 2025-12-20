" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_ply_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS describe_voices
      IMPORTING
        !iv_engine      TYPE /aws1/plyengine OPTIONAL
        !iv_language    TYPE /aws1/plylanguagecode OPTIONAL
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_plydescrvoicesoutput
      RAISING
        /aws1/cx_rt_generic.

    METHODS synthesize_speech
      IMPORTING
        !iv_text        TYPE /aws1/plytext
        !iv_engine      TYPE /aws1/plyengine
        !iv_voice_id    TYPE /aws1/plyvoiceid
        !iv_output_fmt  TYPE /aws1/plyoutputformat
        !iv_lang_code   TYPE /aws1/plylanguagecode OPTIONAL
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_plysynthesizespeech01
      RAISING
        /aws1/cx_rt_generic.

    METHODS start_speech_synthesis_task
      IMPORTING
        !iv_text              TYPE /aws1/plytext
        !iv_engine            TYPE /aws1/plyengine
        !iv_voice_id          TYPE /aws1/plyvoiceid
        !iv_audio_format      TYPE /aws1/plyoutputformat
        !iv_s3_bucket         TYPE /aws1/plyoutputs3bucketname
        !iv_lang_code         TYPE /aws1/plylanguagecode OPTIONAL
        !iv_s3_key_prefix     TYPE /aws1/plyoutputs3keyprefix OPTIONAL
      EXPORTING
        !oo_result            TYPE REF TO /aws1/cl_plystrtspeechsynthe01
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_speech_synthesis_task
      IMPORTING
        !iv_task_id     TYPE /aws1/plytaskid
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_plygetspeechsynthes01
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_speech_synthesis_tasks
      IMPORTING
        !iv_max_results TYPE /aws1/plymaxresults OPTIONAL
        !iv_status      TYPE /aws1/plytaskstatus OPTIONAL
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_plylstspeechsynthes01
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_lexicon
      IMPORTING
        !iv_name        TYPE /aws1/plylexiconname
        !iv_content     TYPE /aws1/plylexiconcontent
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_lexicon
      IMPORTING
        !iv_name        TYPE /aws1/plylexiconname
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_plygetlexiconoutput
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_lexicons
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_plylistlexiconsoutput
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_lexicon
      IMPORTING
        !iv_name        TYPE /aws1/plylexiconname
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_ply_actions IMPLEMENTATION.

  METHOD describe_voices.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ply) = /aws1/cl_ply_factory=>create( lo_session ).

    " snippet-start:[ply.abapv1.describe_voices]
    TRY.
        oo_result = lo_ply->describevoices(
          iv_engine = iv_engine
          iv_languagecode = iv_language ).
        MESSAGE 'Retrieved voice metadata.' TYPE 'I'.
      CATCH /aws1/cx_plyinvalidnexttokenex.
        MESSAGE 'The NextToken is invalid.' TYPE 'E'.
      CATCH /aws1/cx_plyservicefailureex.
        MESSAGE 'Service failure occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ply.abapv1.describe_voices]
  ENDMETHOD.

  METHOD synthesize_speech.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ply) = /aws1/cl_ply_factory=>create( lo_session ).

    " snippet-start:[ply.abapv1.synthesize_speech]
    TRY.
        oo_result = lo_ply->synthesizespeech(
          iv_engine = iv_engine
          iv_outputformat = iv_output_fmt
          iv_text = iv_text
          iv_voiceid = iv_voice_id
          iv_languagecode = iv_lang_code ).
        MESSAGE 'Speech synthesized successfully.' TYPE 'I'.
      CATCH /aws1/cx_plyinvalidssmlex.
        MESSAGE 'Invalid SSML.' TYPE 'E'.
      CATCH /aws1/cx_plylexiconnotfoundex.
        MESSAGE 'Lexicon not found.' TYPE 'E'.
      CATCH /aws1/cx_plyservicefailureex.
        MESSAGE 'Service failure occurred.' TYPE 'E'.
      CATCH /aws1/cx_plytextlengthexcdex.
        MESSAGE 'Text length exceeded maximum.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ply.abapv1.synthesize_speech]
  ENDMETHOD.

  METHOD start_speech_synthesis_task.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ply) = /aws1/cl_ply_factory=>create( lo_session ).

    " snippet-start:[ply.abapv1.start_speech_synthesis_task]
    TRY.
        oo_result = lo_ply->startspeechsynthesistask(
          iv_engine = iv_engine
          iv_outputformat = iv_audio_format
          iv_outputs3bucketname = iv_s3_bucket
          iv_outputs3keyprefix = iv_s3_key_prefix
          iv_text = iv_text
          iv_voiceid = iv_voice_id
          iv_languagecode = iv_lang_code ).
        MESSAGE 'Speech synthesis task started.' TYPE 'I'.
      CATCH /aws1/cx_plyinvalids3bucketex.
        MESSAGE 'Invalid S3 bucket.' TYPE 'E'.
      CATCH /aws1/cx_plyinvalidssmlex.
        MESSAGE 'Invalid SSML.' TYPE 'E'.
      CATCH /aws1/cx_plylexiconnotfoundex.
        MESSAGE 'Lexicon not found.' TYPE 'E'.
      CATCH /aws1/cx_plyservicefailureex.
        MESSAGE 'Service failure occurred.' TYPE 'E'.
      CATCH /aws1/cx_plytextlengthexcdex.
        MESSAGE 'Text length exceeded maximum.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ply.abapv1.start_speech_synthesis_task]
  ENDMETHOD.

  METHOD get_speech_synthesis_task.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ply) = /aws1/cl_ply_factory=>create( lo_session ).

    " snippet-start:[ply.abapv1.get_speech_synthesis_task]
    TRY.
        oo_result = lo_ply->getspeechsynthesistask( iv_task_id ).
        DATA(lo_task) = oo_result->get_synthesistask( ).
        IF lo_task IS BOUND.
          DATA(lv_status) = lo_task->get_taskstatus( ).
          MESSAGE |Task status: { lv_status }| TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_plyinvalidtaskidex.
        MESSAGE 'Invalid task ID.' TYPE 'E'.
      CATCH /aws1/cx_plyservicefailureex.
        MESSAGE 'Service failure occurred.' TYPE 'E'.
      CATCH /aws1/cx_plysynthesistsknotf00.
        MESSAGE 'Synthesis task not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ply.abapv1.get_speech_synthesis_task]
  ENDMETHOD.

  METHOD list_speech_synthesis_tasks.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ply) = /aws1/cl_ply_factory=>create( lo_session ).

    " snippet-start:[ply.abapv1.list_speech_synthesis_tasks]
    TRY.
        oo_result = lo_ply->listspeechsynthesistasks(
          iv_maxresults = iv_max_results
          iv_status = iv_status ).
        DATA(lt_tasks) = oo_result->get_synthesistasks( ).
        DATA(lv_count) = lines( lt_tasks ).
        MESSAGE |Found { lv_count } synthesis tasks| TYPE 'I'.
      CATCH /aws1/cx_plyinvalidnexttokenex.
        MESSAGE 'Invalid NextToken.' TYPE 'E'.
      CATCH /aws1/cx_plyservicefailureex.
        MESSAGE 'Service failure occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ply.abapv1.list_speech_synthesis_tasks]
  ENDMETHOD.

  METHOD create_lexicon.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ply) = /aws1/cl_ply_factory=>create( lo_session ).

    " snippet-start:[ply.abapv1.put_lexicon]
    TRY.
        lo_ply->putlexicon(
          iv_name = iv_name
          iv_content = iv_content ).
        MESSAGE 'Lexicon created successfully.' TYPE 'I'.
      CATCH /aws1/cx_plyinvalidlexiconex.
        MESSAGE 'Invalid lexicon.' TYPE 'E'.
      CATCH /aws1/cx_plylexiconsizeexcdex.
        MESSAGE 'Lexicon size exceeded.' TYPE 'E'.
      CATCH /aws1/cx_plymaxlexemelengthe00.
        MESSAGE 'Maximum lexeme length exceeded.' TYPE 'E'.
      CATCH /aws1/cx_plymaxlexiconsnoexc00.
        MESSAGE 'Maximum number of lexicons exceeded.' TYPE 'E'.
      CATCH /aws1/cx_plyservicefailureex.
        MESSAGE 'Service failure occurred.' TYPE 'E'.
      CATCH /aws1/cx_plyunsuppedplsalpha00.
        MESSAGE 'Unsupported PLS alphabet.' TYPE 'E'.
      CATCH /aws1/cx_plyunsuppedplslangu00.
        MESSAGE 'Unsupported PLS language.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ply.abapv1.put_lexicon]
  ENDMETHOD.

  METHOD get_lexicon.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ply) = /aws1/cl_ply_factory=>create( lo_session ).

    " snippet-start:[ply.abapv1.get_lexicon]
    TRY.
        oo_result = lo_ply->getlexicon( iv_name ).
        DATA(lo_lexicon) = oo_result->get_lexicon( ).
        IF lo_lexicon IS BOUND.
          DATA(lv_lex_name) = lo_lexicon->get_name( ).
          MESSAGE |Retrieved lexicon: { lv_lex_name }| TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_plylexiconnotfoundex.
        MESSAGE 'Lexicon not found.' TYPE 'E'.
      CATCH /aws1/cx_plyservicefailureex.
        MESSAGE 'Service failure occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ply.abapv1.get_lexicon]
  ENDMETHOD.

  METHOD list_lexicons.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ply) = /aws1/cl_ply_factory=>create( lo_session ).

    " snippet-start:[ply.abapv1.list_lexicons]
    TRY.
        oo_result = lo_ply->listlexicons( ).
        DATA(lt_lexicons) = oo_result->get_lexicons( ).
        DATA(lv_count) = lines( lt_lexicons ).
        MESSAGE |Found { lv_count } lexicons| TYPE 'I'.
      CATCH /aws1/cx_plyinvalidnexttokenex.
        MESSAGE 'Invalid NextToken.' TYPE 'E'.
      CATCH /aws1/cx_plyservicefailureex.
        MESSAGE 'Service failure occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ply.abapv1.list_lexicons]
  ENDMETHOD.

  METHOD delete_lexicon.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ply) = /aws1/cl_ply_factory=>create( lo_session ).

    " snippet-start:[ply.abapv1.delete_lexicon]
    TRY.
        lo_ply->deletelexicon( iv_name ).
        MESSAGE 'Lexicon deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_plylexiconnotfoundex.
        MESSAGE 'Lexicon not found.' TYPE 'E'.
      CATCH /aws1/cx_plyservicefailureex.
        MESSAGE 'Service failure occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ply.abapv1.delete_lexicon]
  ENDMETHOD.

ENDCLASS.
