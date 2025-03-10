CLASS zcl_aws1_ex_utils DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    CONSTANTS cv_asset_prefix TYPE string VALUE 'aws-example' ##NO_TEXT.

    CLASS-METHODS get_random_string
      RETURNING
      VALUE(ov_str) TYPE string .
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
