class ZCL_AWS1_BDR_ACTIONS definition
  public
  final
  create public .

public section.
protected section.
private section.

  methods PROMPT_CLAUDE_V2
    importing
      !IV_PROMPT type STRING
    returning
      value(OV_ANSWER) type STRING
    raising
      /AWS1/CX_BDRSERVEREXC
      /AWS1/CX_BDRCLIENTEXC
      /AWS1/CX_RT_TECHNICAL_GENERIC
      /AWS1/CX_RT_SERVICE_GENERIC
      /AWS1/CX_RT_NO_AUTH_GENERIC .
  methods PROMPT_STABLE_DIFFUSION
    importing
      !IV_PROMPT type STRING
    returning
      value(OV_IMAGE) type XSTRING
    raising
      /AWS1/CX_BDRSERVEREXC
      /AWS1/CX_BDRCLIENTEXC
      /AWS1/CX_RT_TECHNICAL_GENERIC
      /AWS1/CX_RT_SERVICE_GENERIC
      /AWS1/CX_RT_NO_AUTH_GENERIC .
ENDCLASS.



CLASS ZCL_AWS1_BDR_ACTIONS IMPLEMENTATION.


  METHOD prompt_claude_v2.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

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
      EXPORTING data = ls_input
                pretty_name   = /ui2/cl_json=>pretty_mode-low_case ).

    TRY.
        DATA(lo_response) = lo_bdr->invokemodel(
          iv_body = /aws1/cl_rt_util=>string_to_xstring( lv_json )
          iv_modelid = 'anthropic.claude-v2'
          iv_accept = 'application/json'
          iv_contenttype = 'application/json'
        ).

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
        WRITE: / lo_ex->get_text( ).
        WRITE: / |Don't forget to enable model access at https://us-west-2.console.aws.amazon.com/bedrock/home?#/modelaccess|.
        "Catch other exceptions as desired...
    ENDTRY.
    "snippet-end:[bdr.abapv1.invokemodel_claude_v2]
    ov_answer = lv_answer.
  ENDMETHOD.


  METHOD prompt_stable_diffusion.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_bdr) = /aws1/cl_bdr_factory=>create( lo_session ).
    "snippet-start:[bdr.abapv1.invokemodel_stable_diffusion]
    "Stable Diffusion Input Parameters should be in a format like this:
*   {
*     "text_prompts": [
*       {"text":"Draw a dolphin with a mustache"},
*       {"text":"Make it photorealistic"}
*     ],
*     "cfg_scale":10,
*     "seed":0,
*     "steps":50
*   }
    TYPES: BEGIN OF prompt_ts,
             text TYPE /aws1/rt_shape_string,
           END OF prompt_ts.

    DATA: BEGIN OF ls_input,
            text_prompts TYPE STANDARD TABLE OF prompt_ts,
            cfg_scale    TYPE /aws1/rt_shape_integer,
            seed         TYPE /aws1/rt_shape_integer,
            steps        TYPE /aws1/rt_shape_integer,
          END OF ls_input.

    APPEND VALUE prompt_ts( text = iv_prompt ) TO ls_input-text_prompts.
    ls_input-cfg_scale = 10.
    ls_input-seed = 0. "or better, choose a random integer.
    ls_input-steps = 50.

    DATA(lv_json) = /ui2/cl_json=>serialize(
      EXPORTING data = ls_input
                pretty_name   = /ui2/cl_json=>pretty_mode-low_case ).

    TRY.
        DATA(lo_response) = lo_bdr->invokemodel(
          iv_body = /aws1/cl_rt_util=>string_to_xstring( lv_json )
          iv_modelid = 'stability.stable-diffusion-xl-v0'
          iv_accept = 'application/json'
          iv_contenttype = 'application/json'
        ).

        "Stable Diffusion Result Format:
*       {
*         "result": "success",
*         "artifacts": [
*           {
*             "seed": 0,
*             "base64": "iVBORw0KGgoAAAANSUhEUgAAAgAAA....
*             "finishReason": "SUCCESS"
*           }
*         ]
*       }
        TYPES: BEGIN OF artifact_ts,
                 seed         TYPE /aws1/rt_shape_integer,
                 base64       TYPE /aws1/rt_shape_string,
                 finishreason TYPE /aws1/rt_shape_string,
               END OF artifact_ts.

        DATA: BEGIN OF ls_response,
                result    TYPE /aws1/rt_shape_string,
                artifacts TYPE STANDARD TABLE OF artifact_ts,
              END OF ls_response.

        /ui2/cl_json=>deserialize(
          EXPORTING jsonx = lo_response->get_body( )
                    pretty_name = /ui2/cl_json=>pretty_mode-camel_case
          CHANGING  data  = ls_response ).
        IF ls_response-artifacts IS NOT INITIAL.
          DATA(lv_image) = cl_http_utility=>if_http_utility~decode_x_base64( ls_response-artifacts[ 1 ]-base64 ).
        ENDIF.
      CATCH /aws1/cx_bdraccessdeniedex INTO DATA(lo_ex).
        WRITE: / lo_ex->get_text( ).
        WRITE: / |Don't forget to enable model access at https://us-west-2.console.aws.amazon.com/bedrock/home?#/modelaccess|.
        "Catch other exceptions as desired...
    ENDTRY.

    "snippet-end:[bdr.abapv1.invokemodel_stable_diffusion]
    ov_image = lv_image.
  ENDMETHOD.
ENDCLASS.
