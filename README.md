# Compiler
SIGNAL compiler. Converts SIGNAL code (Pascal style) to Asssembler code.

Works with gramatics:
<signal-program> --> <program>
<program> --> PROGRAM <procedure-identifier> ; <block>. 
      | PROCEDURE <procedure-identifier><parameters-list> ; <block> ;
<block> --> BEGIN <statements-list> END
<statements-list> --> <empty>
<parameters-list> --> ( <declarations-list> ) | <empty>
<declarations-list> --> <declaration> <declarations-list> | <empty>
<declaration> --><variable-identifier><identifiers-list>:
                 <attribute><attributeslist> ;
<identifiers-list> --> , <variable-identifier> <identifiers-list> | <empty>
<attributes-list> --> <attribute> <attributeslist> | <empty>
<attribute> --> SIGNAL | COMPLEX | INTEGER | FLOAT | BLOCKFLOAT | EXT
<procedure-identifier> --> <identifier>
<variable-identifier> --> <identifier>
<identifier> --> <letter><string>
<string> --> <letter><string> | <digit><string> | <empty>
<letter> --> A | B | C | D | ... | Z
