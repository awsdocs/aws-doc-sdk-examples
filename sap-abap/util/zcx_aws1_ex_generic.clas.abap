" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
class ZCX_AWS1_EX_GENERIC definition
  public
  inheriting from CX_STATIC_CHECK
  create public .

public section.

  interfaces IF_T100_MESSAGE .

  constants:
    begin of ZCX_AWS1_EX_GENERIC,
      msgid type symsgid value 'ZAWS1_EX_EXC',
      msgno type symsgno value '001',
      attr1 type scx_attrname value 'AV_MSG',
      attr2 type scx_attrname value '',
      attr3 type scx_attrname value '',
      attr4 type scx_attrname value '',
    end of ZCX_AWS1_EX_GENERIC .
  data AV_MSG type STRING .

  methods CONSTRUCTOR
    importing
      !TEXTID like IF_T100_MESSAGE=>T100KEY optional
      !PREVIOUS like PREVIOUS optional
      !AV_MSG type STRING optional .
protected section.
private section.
ENDCLASS.



CLASS ZCX_AWS1_EX_GENERIC IMPLEMENTATION.


  method CONSTRUCTOR.
CALL METHOD SUPER->CONSTRUCTOR
EXPORTING
PREVIOUS = PREVIOUS
.
me->AV_MSG = AV_MSG .
clear me->textid.
if textid is initial.
  IF_T100_MESSAGE~T100KEY = ZCX_AWS1_EX_GENERIC .
else.
  IF_T100_MESSAGE~T100KEY = TEXTID.
endif.
  endmethod.
ENDCLASS.
