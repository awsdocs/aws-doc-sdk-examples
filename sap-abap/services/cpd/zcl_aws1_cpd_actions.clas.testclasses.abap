
CLASS ltc_zcl_aws1_cpd_actions DEFINITION FOR TESTING
  DURATION SHORT
  RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    DATA:
      f_cut TYPE REF TO zcl_aws1_cpd_actions.  "class under test

    METHODS: detectsentiment FOR TESTING.
ENDCLASS.       "ltc_Zcl_Aws1_Cpd_Actions


CLASS ltc_zcl_aws1_cpd_actions IMPLEMENTATION.

  METHOD detectsentiment.



  ENDMETHOD.




ENDCLASS.
