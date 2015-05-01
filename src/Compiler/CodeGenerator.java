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

    private boolean addError(int indexInTokensTable, String comment) {
        if (indexInTokensTable == -1)
            tableErrors.addError(-1, -1, comment);
        else
            tableErrors.addError(tableTokens.getTokenFileRow(indexInTokensTable),
                    tableTokens.getTokenFileColumn(indexInTokensTable) -1, comment);
        return false;
    }

    public boolean generate() {
        if (syntaxTree.programBranch) {
            if (!syntaxTree.block.expressionsList.empty) {
                return addError(syntaxTree.identifier + 3, "Expressions in block are forbidden.");
            }

            asmCode.add(".386\n");
            asmCode.add(String.format("%-8s%s\n", "TITLE", getTokenByTokensTableIndex(syntaxTree.identifier)));
            asmCode.add(".CODE\n");
            asmCode.add(String.format("%-8s%s", "?BEGIN:", "NOP"));
            asmCode.add(String.format("%-8s%s", "END", "?BEGIN"));
            return true;
        }

        return false;
    }
}
