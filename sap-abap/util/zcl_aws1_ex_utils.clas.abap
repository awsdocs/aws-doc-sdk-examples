class ZCL_AWS1_EX_UTILS definition
  public
  final
  create public .

public section.

  constants CV_ASSET_PREFIX type STRING value 'aws-example' ##NO_TEXT.

  class-methods GET_RANDOM_STRING
    returning
      value(OV_STR) type STRING .
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS ZCL_AWS1_EX_UTILS IMPLEMENTATION.


  METHOD get_random_string.
    CALL FUNCTION 'GENERAL_GET_RANDOM_STRING'
      EXPORTING
        number_chars  = 10
      IMPORTING
        random_string = ov_str.
  ENDMETHOD.
ENDCLASS.
