" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cx_generic DEFINITION
  PUBLIC
  INHERITING FROM cx_static_check
  CREATE PUBLIC .

  PUBLIC SECTION.

    INTERFACES if_t100_message .

    CONSTANTS:
      BEGIN OF /awsex/cx_generic,
        msgid TYPE symsgid VALUE '/AWSEX/LIB_EXC',
        msgno TYPE symsgno VALUE '001',
        attr1 TYPE scx_attrname VALUE 'AV_MSG',
        attr2 TYPE scx_attrname VALUE '',
        attr3 TYPE scx_attrname VALUE '',
        attr4 TYPE scx_attrname VALUE '',
      END OF /awsex/cx_generic .
    DATA av_msg TYPE string .

    METHODS constructor
      IMPORTING
        !textid   LIKE if_t100_message=>t100key OPTIONAL
        !previous LIKE previous OPTIONAL
        !av_msg   TYPE string OPTIONAL .
protected section.
private section.
ENDCLASS.



CLASS /AWSEX/CX_GENERIC IMPLEMENTATION.


  method CONSTRUCTOR.
CALL METHOD SUPER->CONSTRUCTOR
EXPORTING
PREVIOUS = PREVIOUS
.
me->AV_MSG = AV_MSG .
clear me->textid.
if textid is initial.
  IF_T100_MESSAGE~T100KEY = /AWSEX/CX_GENERIC .
else.
  IF_T100_MESSAGE~T100KEY = TEXTID.
endif.
  endmethod.
ENDCLASS.
