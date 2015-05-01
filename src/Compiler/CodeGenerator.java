package Compiler;

import Compiler.syntax_tree.SyntaxTree;
import Compiler.tables.TableErrors;
import Compiler.tables.TableTokens;

import java.util.Vector;

public class CodeGenerator extends Parser {
    public Vector<String> asmCode;

    public CodeGenerator(Vector<String> tableReservedWords,
                         Vector<Character> tableOneSymbolTokens,
                         Vector<String> tableIdentifiers,
                         TableTokens tableTokens,
                         TableErrors tableErrors,
                         SyntaxTree syntaxTree) {
        super(tableReservedWords, tableOneSymbolTokens, tableIdentifiers, tableTokens, tableErrors);
        this.syntaxTree = syntaxTree;
        asmCode = new Vector<String>();
    }

    public boolean generate() {
        if (syntaxTree.programBranch) {
            if (!syntaxTree.block.empty) {
                return false;
            }

            asmCode.add(".386");
            asmCode.add(String.format("%-8s%s", "TITLE", getToken(2, syntaxTree.identifier)));
            asmCode.add(".CODE");
            asmCode.add(String.format("%-8s%s", "?BEGIN:", "NOP"));
            asmCode.add(String.format("%-8s%s", "END", "?BEGIN"));
            return true;
        }

        return false;
    }
}
