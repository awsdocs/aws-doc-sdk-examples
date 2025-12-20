" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_ppt_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS send_email_message
      IMPORTING
                !iv_app_id       TYPE /aws1/ppt__string
                !iv_sender       TYPE /aws1/ppt__string
                !it_to_addresses TYPE /aws1/cl_pptlistof__string_w=>tt_listof__string
                !iv_char_set     TYPE /aws1/ppt__string
                !iv_subject      TYPE /aws1/ppt__string
                !iv_html_message TYPE /aws1/ppt__string
                !iv_text_message TYPE /aws1/ppt__string
      EXPORTING
                !ot_message_ids  TYPE /aws1/cl_pptmessageresult=>tt_mapofmessageresult
      RAISING   /aws1/cx_rt_generic.

    METHODS send_sms_message
      IMPORTING
                !iv_app_id             TYPE /aws1/ppt__string
                !iv_origination_number TYPE /aws1/ppt__string
                !iv_destination_number TYPE /aws1/ppt__string
                !iv_message            TYPE /aws1/ppt__string
                !iv_message_type       TYPE /aws1/pptmessagetype
      EXPORTING
                !ov_message_id         TYPE /aws1/ppt__string
      RAISING   /aws1/cx_rt_generic.

    METHODS send_templated_email_msg
      IMPORTING
                !iv_app_id           TYPE /aws1/ppt__string
                !iv_sender           TYPE /aws1/ppt__string
                !it_to_addresses     TYPE /aws1/cl_pptlistof__string_w=>tt_listof__string
                !iv_template_name    TYPE /aws1/ppt__string
                !iv_template_version TYPE /aws1/ppt__string
      EXPORTING
                !ot_message_ids      TYPE /aws1/cl_pptmessageresult=>tt_mapofmessageresult
      RAISING   /aws1/cx_rt_generic.

    METHODS send_templated_sms_message
      IMPORTING
                !iv_app_id             TYPE /aws1/ppt__string
                !iv_destination_number TYPE /aws1/ppt__string
                !iv_message_type       TYPE /aws1/pptmessagetype
                !iv_origination_number TYPE /aws1/ppt__string
                !iv_template_name      TYPE /aws1/ppt__string
                !iv_template_version   TYPE /aws1/ppt__string
      EXPORTING
                !ov_message_id         TYPE /aws1/ppt__string
      RAISING   /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_PPT_ACTIONS IMPLEMENTATION.


  METHOD send_email_message.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ppt) = /aws1/cl_ppt_factory=>create( lo_session ).

    " snippet-start:[ppt.abapv1.send_email_message]
    " Build the addresses map from the list of to_addresses
    DATA lt_addresses TYPE /aws1/cl_pptaddressconf=>tt_mapofaddressconfiguration.
    LOOP AT it_to_addresses INTO DATA(lo_address).
      INSERT VALUE /aws1/cl_pptaddressconf=>ts_mapofaddressconf_maprow(
        key = lo_address->get_value( )
        value = NEW /aws1/cl_pptaddressconf( iv_channeltype = 'EMAIL' )
      ) INTO TABLE lt_addresses.
    ENDLOOP.

    " Send the email message
    DATA(lo_result) = lo_ppt->sendmessages(
      iv_applicationid = iv_app_id
      io_messagerequest = NEW /aws1/cl_pptmessagerequest(
        it_addresses = lt_addresses
        io_messageconfiguration = NEW /aws1/cl_pptdirectmessageconf(
          io_emailmessage = NEW /aws1/cl_pptemailmessage(
            iv_fromaddress = iv_sender
            io_simpleemail = NEW /aws1/cl_pptsimpleemail(
              io_subject = NEW /aws1/cl_pptsimpleemailpart(
                iv_charset = iv_char_set
                iv_data = iv_subject
              )
              io_htmlpart = NEW /aws1/cl_pptsimpleemailpart(
                iv_charset = iv_char_set
                iv_data = iv_html_message
              )
              io_textpart = NEW /aws1/cl_pptsimpleemailpart(
                iv_charset = iv_char_set
                iv_data = iv_text_message
              )
            )
          )
        )
      )
    ).

    " Extract message IDs from response
    DATA(lo_message_response) = lo_result->get_messageresponse( ).
    ot_message_ids = lo_message_response->get_result( ).

    MESSAGE 'Email message sent successfully.' TYPE 'I'.
    " snippet-end:[ppt.abapv1.send_email_message]
  ENDMETHOD.


  METHOD send_sms_message.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ppt) = /aws1/cl_ppt_factory=>create( lo_session ).

    " snippet-start:[ppt.abapv1.send_sms_message]
    " Build the addresses map for the destination number
    DATA lt_addresses TYPE /aws1/cl_pptaddressconf=>tt_mapofaddressconfiguration.
    INSERT VALUE /aws1/cl_pptaddressconf=>ts_mapofaddressconf_maprow(
      key = iv_destination_number
      value = NEW /aws1/cl_pptaddressconf( iv_channeltype = 'SMS' )
    ) INTO TABLE lt_addresses.

    " Send the SMS message
    DATA(lo_result) = lo_ppt->sendmessages(
      iv_applicationid = iv_app_id
      io_messagerequest = NEW /aws1/cl_pptmessagerequest(
        it_addresses = lt_addresses
        io_messageconfiguration = NEW /aws1/cl_pptdirectmessageconf(
          io_smsmessage = NEW /aws1/cl_pptsmsmessage(
            iv_body = iv_message
            iv_messagetype = iv_message_type
            iv_originationnumber = iv_origination_number
          )
        )
      )
    ).

    " Extract message ID from response
    DATA(lo_message_response) = lo_result->get_messageresponse( ).
    DATA(lt_results) = lo_message_response->get_result( ).
    LOOP AT lt_results INTO DATA(ls_result).
      IF ls_result-key = iv_destination_number.
        ov_message_id = ls_result-value->get_messageid( ).
        EXIT.
      ENDIF.
    ENDLOOP.

    MESSAGE 'SMS message sent successfully.' TYPE 'I'.
    " snippet-end:[ppt.abapv1.send_sms_message]
  ENDMETHOD.


  METHOD send_templated_email_msg.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ppt) = /aws1/cl_ppt_factory=>create( lo_session ).

    " snippet-start:[ppt.abapv1.send_templated_email_message]
    " Build the addresses map from the list of to_addresses
    DATA lt_addresses TYPE /aws1/cl_pptaddressconf=>tt_mapofaddressconfiguration.
    LOOP AT it_to_addresses INTO DATA(lo_address).
      INSERT VALUE /aws1/cl_pptaddressconf=>ts_mapofaddressconf_maprow(
        key = lo_address->get_value( )
        value = NEW /aws1/cl_pptaddressconf( iv_channeltype = 'EMAIL' )
      ) INTO TABLE lt_addresses.
    ENDLOOP.

    " Send the email message using a template
    DATA(lo_result) = lo_ppt->sendmessages(
      iv_applicationid = iv_app_id
      io_messagerequest = NEW /aws1/cl_pptmessagerequest(
        it_addresses = lt_addresses
        io_messageconfiguration = NEW /aws1/cl_pptdirectmessageconf(
          io_emailmessage = NEW /aws1/cl_pptemailmessage(
            iv_fromaddress = iv_sender
          )
        )
        io_templateconfiguration = NEW /aws1/cl_ppttemplateconf(
          io_emailtemplate = NEW /aws1/cl_ppttemplate(
            iv_name = iv_template_name
            iv_version = iv_template_version
          )
        )
      )
    ).

    " Extract message IDs from response
    DATA(lo_message_response) = lo_result->get_messageresponse( ).
    ot_message_ids = lo_message_response->get_result( ).

    MESSAGE 'Templated email message sent successfully.' TYPE 'I'.
    " snippet-end:[ppt.abapv1.send_templated_email_message]
  ENDMETHOD.


  METHOD send_templated_sms_message.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ppt) = /aws1/cl_ppt_factory=>create( lo_session ).

    " snippet-start:[ppt.abapv1.send_templated_sms_message]
    " Build the addresses map for the destination number
    DATA lt_addresses TYPE /aws1/cl_pptaddressconf=>tt_mapofaddressconfiguration.
    INSERT VALUE /aws1/cl_pptaddressconf=>ts_mapofaddressconf_maprow(
      key = iv_destination_number
      value = NEW /aws1/cl_pptaddressconf( iv_channeltype = 'SMS' )
    ) INTO TABLE lt_addresses.

    " Send the SMS message using a template
    DATA(lo_result) = lo_ppt->sendmessages(
      iv_applicationid = iv_app_id
      io_messagerequest = NEW /aws1/cl_pptmessagerequest(
        it_addresses = lt_addresses
        io_messageconfiguration = NEW /aws1/cl_pptdirectmessageconf(
          io_smsmessage = NEW /aws1/cl_pptsmsmessage(
            iv_messagetype = iv_message_type
            iv_originationnumber = iv_origination_number
          )
        )
        io_templateconfiguration = NEW /aws1/cl_ppttemplateconf(
          io_smstemplate = NEW /aws1/cl_ppttemplate(
            iv_name = iv_template_name
            iv_version = iv_template_version
          )
        )
      )
    ).

    " Extract message ID from response
    DATA(lo_message_response) = lo_result->get_messageresponse( ).
    DATA(lt_results) = lo_message_response->get_result( ).
    LOOP AT lt_results INTO DATA(ls_result).
      IF ls_result-key = iv_destination_number.
        ov_message_id = ls_result-value->get_messageid( ).
        EXIT.
      ENDIF.
    ENDLOOP.

    MESSAGE 'Templated SMS message sent successfully.' TYPE 'I'.
    " snippet-end:[ppt.abapv1.send_templated_sms_message]
  ENDMETHOD.
ENDCLASS.
