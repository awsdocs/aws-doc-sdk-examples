" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_pps_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS send_voice_message
      IMPORTING
        !iv_origination_number TYPE /aws1/ppsnonemptystring
        !iv_caller_id          TYPE /aws1/ppsstring
        !iv_destination_number TYPE /aws1/ppsnonemptystring
        !iv_language_code      TYPE /aws1/ppsstring
        !iv_voice_id           TYPE /aws1/ppsstring
        !iv_ssml_message       TYPE /aws1/ppsnonemptystring
      RETURNING
        VALUE(ov_message_id)   TYPE /aws1/ppsstring
      RAISING
        /aws1/cx_rt_generic .
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_pps_actions IMPLEMENTATION.


  METHOD send_voice_message.
    " Sends a voice message using speech synthesis provided by Amazon Polly.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_pps) = /aws1/cl_pps_factory=>create( lo_session ).

    " snippet-start:[pps.abapv1.send_voice_message]
    TRY.
        " Create SSML message type object with voice parameters
        DATA(lo_ssml_message) = NEW /aws1/cl_ppsssmlmessagetype(
          iv_languagecode = iv_language_code    " e.g., 'en-US'
          iv_voiceid = iv_voice_id              " e.g., 'Matthew'
          iv_text = iv_ssml_message             " SSML formatted message text
        ).

        " Create voice message content with the SSML message
        DATA(lo_content) = NEW /aws1/cl_ppsvoicemessagecont(
          io_ssmlmessage = lo_ssml_message
        ).

        " Send the voice message
        DATA(lo_result) = lo_pps->sendvoicemessage(
          iv_originationphonenumber = iv_origination_number  " e.g., '+12065550110'
          iv_callerid = iv_caller_id                         " e.g., '+12065550199'
          iv_destinationphonenumber = iv_destination_number  " e.g., '+12065550142'
          io_content = lo_content
        ).

        " Retrieve the message ID from the response
        ov_message_id = lo_result->get_messageid( ).

        MESSAGE 'Voice message sent successfully.' TYPE 'I'.

      CATCH /aws1/cx_ppsbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'I'.
        RAISE EXCEPTION lo_bad_request_ex.
      CATCH /aws1/cx_ppsinternalsvcerrorex INTO DATA(lo_internal_error_ex).
        MESSAGE lo_internal_error_ex->get_text( ) TYPE 'I'.
        RAISE EXCEPTION lo_internal_error_ex.
      CATCH /aws1/cx_ppstoomanyrequestsex INTO DATA(lo_too_many_requests_ex).
        MESSAGE lo_too_many_requests_ex->get_text( ) TYPE 'I'.
        RAISE EXCEPTION lo_too_many_requests_ex.
    ENDTRY.
    " snippet-end:[pps.abapv1.send_voice_message]

  ENDMETHOD.
ENDCLASS.
