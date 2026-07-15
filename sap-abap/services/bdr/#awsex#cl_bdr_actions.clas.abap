" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_bdr_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
  PROTECTED SECTION.
  PRIVATE SECTION.

    METHODS prompt_claude_v2
      IMPORTING
        !iv_prompt       TYPE string
      RETURNING
        VALUE(ov_answer) TYPE string
      RAISING
        /aws1/cx_bdrserverexc
        /aws1/cx_bdrclientexc
        /aws1/cx_rt_technical_generic
        /aws1/cx_rt_service_generic
        /aws1/cx_rt_no_auth_generic .
    METHODS prompt_stable_diffusion
      IMPORTING
        !iv_prompt      TYPE string
      RETURNING
        VALUE(ov_image) TYPE xstring
      RAISING
        /aws1/cx_bdrserverexc
        /aws1/cx_bdrclientexc
        /aws1/cx_rt_technical_generic
        /aws1/cx_rt_service_generic
        /aws1/cx_rt_no_auth_generic .
    METHODS l2_prompt_claude_v2
      IMPORTING
        !iv_prompt       TYPE string
      RETURNING
        VALUE(ov_answer) TYPE string
      RAISING
        /aws1/cx_bdrserverexc
        /aws1/cx_bdrclientexc
        /aws1/cx_rt_technical_generic
        /aws1/cx_rt_service_generic
        /aws1/cx_rt_no_auth_generic .
    METHODS l2_prompt_stable_diffusion
      IMPORTING
        !iv_prompt      TYPE string
      RETURNING
        VALUE(ov_image) TYPE xstring
      RAISING
        /aws1/cx_bdrserverexc
        /aws1/cx_bdrclientexc
        /aws1/cx_rt_technical_generic
        /aws1/cx_rt_service_generic
        /aws1/cx_rt_no_auth_generic .
    METHODS l2_prompt_claude_v3
      IMPORTING
        !iv_prompt       TYPE string
      RETURNING
        VALUE(ov_answer) TYPE string
      RAISING
        /aws1/cx_bdrserverexc
        /aws1/cx_bdrclientexc
        /aws1/cx_rt_technical_generic
        /aws1/cx_rt_service_generic
        /aws1/cx_rt_no_auth_generic
        /aws1/cx_bdr_l2_generic .
ENDCLASS.



CLASS /AWSEX/CL_BDR_ACTIONS IMPLEMENTATION.


  METHOD l2_prompt_claude_v2.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_bdr) = /aws1/cl_bdr_factory=>create( lo_session ).
    "snippet-start:[bdr.abapv1.invokemodel_l2_claude_v2]
    TRY.
        DATA(lo_bdr_l2_claude) = /aws1/cl_bdr_l2_factory=>create_claude_2( lo_bdr ).
        " iv_prompt can contain a prompt like 'tell me a joke about Java programmers'.
        DATA(lv_answer) = lo_bdr_l2_claude->prompt_for_text( iv_prompt ).
      CATCH /aws1/cx_bdraccessdeniedex INTO DATA(lo_ex).
        WRITE / lo_ex->get_text( ).
        WRITE / |Don't forget to enable model access at https://console.aws.amazon.com/bedrock/home?#/modelaccess|.

    ENDTRY.
    "snippet-end:[bdr.abapv1.invokemodel_l2_claude_v2]
    ov_answer = lv_answer.
  ENDMETHOD.


  METHOD l2_prompt_claude_v3.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_bdr) = /aws1/cl_bdr_factory=>create( lo_session ).
    "snippet-start:[bdr.abapv1.invokemodel_l2_claude_v3]
    TRY.
        " Choose a model ID from Anthropic that supports the Messages API - currently this is
        " Claude v2, Claude v3 and v3.5.  For the list of model ID, see:
        " https://docs.aws.amazon.com/bedrock/latest/userguide/model-ids.html

        " for the list of models that support the Messages API see:
        " https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-anthropic-claude-messages.html
        DATA(lo_bdr_l2_claude) = /aws1/cl_bdr_l2_factory=>create_anthropic_msg_api(
          io_bdr = lo_bdr
          iv_model_id = 'anthropic.claude-3-sonnet-20240229-v1:0' ).  " choosing Claude v3 Sonnet
        " iv_prompt can contain a prompt like 'tell me a joke about Java programmers'.
        DATA(lv_answer) = lo_bdr_l2_claude->prompt_for_text( iv_prompt = iv_prompt
                                                             iv_max_tokens = 100 ).
      CATCH /aws1/cx_bdraccessdeniedex INTO DATA(lo_ex).
        WRITE / lo_ex->get_text( ).
        WRITE / |Don't forget to enable model access at https://console.aws.amazon.com/bedrock/home?#/modelaccess|.

    ENDTRY.
    "snippet-end:[bdr.abapv1.invokemodel_l2_claude_v3]
    ov_answer = lv_answer.
  ENDMETHOD.


  METHOD l2_prompt_stable_diffusion.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_bdr) = /aws1/cl_bdr_factory=>create( lo_session ).
    "snippet-start:[bdr.abapv1.invokemodel_l2_stable_diffusion]
    TRY.
        DATA(lo_bdr_l2_sd) = /aws1/cl_bdr_l2_factory=>create_stable_diffusion_xl_1( lo_bdr ).
        " iv_prompt contains a prompt like 'Show me a picture of a unicorn reading an enterprise financial report'.
        DATA(lv_image) = lo_bdr_l2_sd->text_to_image( iv_prompt ).
      CATCH /aws1/cx_bdraccessdeniedex INTO DATA(lo_ex).
        WRITE / lo_ex->get_text( ).
        WRITE / |Don't forget to enable model access at https://console.aws.amazon.com/bedrock/home?#/modelaccess|.

    ENDTRY.
    "snippet-end:[bdr.abapv1.invokemodel_l2_stable_diffusion]
    ov_image = lv_image.

  ENDMETHOD.


  METHOD prompt_claude_v2.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_bdr) = /aws1/cl_bdr_factory=>create( lo_session ).
    "snippet-start:[bdr.abapv1.invokemodel_claude_v2]
    "Claude V2 Input Parameters should be in a format like this:
*   {
*     "prompt":"\n\nHuman:\\nTell me a joke\n\nAssistant:\n",
*     "max_tokens_to_sample":2048,
*     "temperature":0.5,
*     "top_k":250,
*     "top_p":1.0,
*     "stop_sequences":[]
*   }

    DATA: BEGIN OF ls_input,
            prompt               TYPE string,
            max_tokens_to_sample TYPE /aws1/rt_shape_integer,
            temperature          TYPE /aws1/rt_shape_float,
            top_k                TYPE /aws1/rt_shape_integer,
            top_p                TYPE /aws1/rt_shape_float,
            stop_sequences       TYPE /aws1/rt_stringtab,
          END OF ls_input.

    "Leave ls_input-stop_sequences empty.
    ls_input-prompt = |\n\nHuman:\\n{ iv_prompt }\n\nAssistant:\n|.
    ls_input-max_tokens_to_sample = 2048.
    ls_input-temperature = '0.5'.
    ls_input-top_k = 250.
    ls_input-top_p = 1.

    "Serialize into JSON with /ui2/cl_json -- this assumes SAP_UI is installed.
    DATA(lv_json) = /ui2/cl_json=>serialize(
      data = ls_input
                pretty_name   = /ui2/cl_json=>pretty_mode-low_case ).

    TRY.
        DATA(lo_response) = lo_bdr->invokemodel(
          iv_body = /aws1/cl_rt_util=>string_to_xstring( lv_json )
          iv_modelid = 'anthropic.claude-v2'
          iv_accept = 'application/json'
          iv_contenttype = 'application/json' ).

        "Claude V2 Response format will be:
*       {
*         "completion": "Knock Knock...",
*         "stop_reason": "stop_sequence"
*       }
        DATA: BEGIN OF ls_response,
                completion  TYPE string,
                stop_reason TYPE string,
              END OF ls_response.

        /ui2/cl_json=>deserialize(
          EXPORTING jsonx = lo_response->get_body( )
                    pretty_name = /ui2/cl_json=>pretty_mode-camel_case
          CHANGING  data  = ls_response ).

        DATA(lv_answer) = ls_response-completion.
      CATCH /aws1/cx_bdraccessdeniedex INTO DATA(lo_ex).
        WRITE / lo_ex->get_text( ).
        WRITE / |Don't forget to enable model access at https://console.aws.amazon.com/bedrock/home?#/modelaccess|.

    ENDTRY.
    "snippet-end:[bdr.abapv1.invokemodel_claude_v2]
    ov_answer = lv_answer.
  ENDMETHOD.


  METHOD prompt_stable_diffusion.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_bdr) = /aws1/cl_bdr_factory=>create( lo_session ).
    "snippet-start:[bdr.abapv1.invokemodel_stable_diffusion]
    "Stable Image Core Input Parameters should be in a format like this:
*   {
*     "prompt": "Draw a dolphin with a mustache, photorealistic",
*     "aspect_ratio": "1:1",
*     "seed": 0,
*     "output_format": "png"
*   }
    DATA: BEGIN OF ls_input,
            prompt        TYPE /aws1/rt_shape_string,
            aspect_ratio  TYPE /aws1/rt_shape_string,
            seed          TYPE /aws1/rt_shape_integer,
            output_format TYPE /aws1/rt_shape_string,
          END OF ls_input.

    ls_input-prompt = iv_prompt.
    ls_input-aspect_ratio = '1:1'.
    ls_input-seed = 0. "or better, choose a random integer.
    ls_input-output_format = 'png'.

    DATA(lv_json) = /ui2/cl_json=>serialize(
      data = ls_input
                pretty_name   = /ui2/cl_json=>pretty_mode-low_case ).

    TRY.
        DATA(lo_response) = lo_bdr->invokemodel(
          iv_body = /aws1/cl_rt_util=>string_to_xstring( lv_json )
          iv_modelid = 'stability.stable-image-core-v1:1'
          iv_accept = 'application/json'
          iv_contenttype = 'application/json' ).

        "Stable Image Core Result Format:
*       {
*         "seeds": ["0"],
*         "finish_reasons": [null],
*         "images": ["iVBORw0KGgoAAAANSUhEUgAAAgAAA...."]
*       }
        DATA: BEGIN OF ls_response,
                images TYPE STANDARD TABLE OF /aws1/rt_shape_string,
              END OF ls_response.

        /ui2/cl_json=>deserialize(
          EXPORTING jsonx = lo_response->get_body( )
                    pretty_name = /ui2/cl_json=>pretty_mode-camel_case
          CHANGING  data  = ls_response ).
        IF ls_response-images IS NOT INITIAL.
          DATA(lv_image) = cl_http_utility=>if_http_utility~decode_x_base64( ls_response-images[ 1 ] ).
        ENDIF.
      CATCH /aws1/cx_bdraccessdeniedex INTO DATA(lo_ex).
        WRITE / lo_ex->get_text( ).
        WRITE / |Don't forget to enable model access at https://console.aws.amazon.com/bedrock/home?#/modelaccess|.

    ENDTRY.

    "snippet-end:[bdr.abapv1.invokemodel_stable_diffusion]
    ov_image = lv_image.
  ENDMETHOD.
ENDCLASS.
