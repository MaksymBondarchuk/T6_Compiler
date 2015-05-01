package Compiler;

import Compiler.syntax_tree.*;
import Compiler.tables.TableErrors;
import Compiler.tables.TableTokens;

import java.util.Vector;

public class CodeGenerator extends Parser {
    public Vector<String> asmCode;

    private Vector<String> parametersIdentifiers;
    private Vector<String> parametersAttributes;

//    private int sizeSIGNAL = 4;     // As a link
//    private int sizeCOMPLEX = 8;    // For two integers
//    private int sizeINTEGER = 4;
//    private int sizeFLOAT = 4;
//    private int sizeBLOCKFLOAT = 8;
//    private int sizeEXT = 8;
    private int parameterOffset = 8;
    private String programOrProcedureIdentifier;

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

    private void addError(int indexInTokensTable, String comment) {
        if (indexInTokensTable == -1)
            tableErrors.addError(-1, -1, comment);
        else
            tableErrors.addError(tableTokens.getTokenFileRow(indexInTokensTable),
                    tableTokens.getTokenFileColumn(indexInTokensTable), comment);
//        return false;
    }

    public void generate() {
        asmCode.add(".386\n");
        programOrProcedureIdentifier = getTokenByTokensTableIndex(syntaxTree.identifier);

        if (syntaxTree.programBranch) {
            if (!syntaxTree.block.expressionsList.empty) {
                addError(syntaxTree.identifier + 3, "Expressions in block are forbidden.");
                return;
            }

            asmCode.add(String.format("%-8s%s\n", "TITLE", programOrProcedureIdentifier));
            asmCode.add(".CODE\n");
            asmCode.add(String.format("%-8s", "?BEGIN:"));
            asmCode.add(String.format("%-8s%s", "END", "?BEGIN"));
            return;
        }

        asmCode.add(".CODE\n");
        asmCode.add(String.format("%-8s%s", programOrProcedureIdentifier, "PROC"));
        generateParametersList(syntaxTree.parametersList);
        asmCode.add(String.format("%-8s%s", programOrProcedureIdentifier, "ENDP"));
    }

    private void generateParametersList(ParametersList parametersList) {
        if (parametersList.empty || parametersList.declarationsList.empty)
            return;

        asmCode.add(String.format("\t\t%-8s", "; Save registers for getting parameters"));
        asmCode.add(String.format("\t\t%-8s%s", "PUSH", "EBP"));
        asmCode.add(String.format("\t\t%-8s%s", "MOV", "EBP, ESP"));
        asmCode.add(String.format("\t\t%-8s%s\n", "PUSH", "ESI"));

        asmCode.add(String.format("\t\t%-8s", "; Get parameters"));
        parametersIdentifiers = new Vector<String>();
        parametersAttributes = new Vector<String>();
        generateDeclarationsList(parametersList.declarationsList);

        asmCode.add(String.format("\n\t\t%-8s", "; Restore registers"));
        asmCode.add(String.format("\t\t%-8s%s", "POP", "ESI"));
        asmCode.add(String.format("\t\t%-8s%s", "POP", "EBP"));
        asmCode.add(String.format("\t\t%-8s", "RET"));
    }

    private void generateDeclarationsList(DeclarationsList declarationsList) {
        if (declarationsList.empty)
            return;

        generateDeclaration(declarationsList.declaration);
        generateDeclarationsList(declarationsList.declarationsList);
    }

    private void generateDeclaration(Declaration declaration) {
        generateIdentifierAttribute(declaration.identifier, declaration.attribute);
        generateIdentifiersAttributesLists(declaration.identifiersList, declaration.attributesList);
    }

    private void generateIdentifiersAttributesLists(IdentifiersList identifiersList, AttributesList attributesList) {
        if (identifiersList.empty && attributesList.empty)
            return;
        if (!identifiersList.empty && attributesList.empty) {
            addError(identifiersList.identifier, "Extra identifier");
            return;
        }
        if (identifiersList.empty) {
            addError(attributesList.attribute, "Extra attribute");
            return;
        }

        generateIdentifierAttribute(identifiersList.identifier, attributesList.attribute);
        generateIdentifiersAttributesLists(identifiersList.identifiersList, attributesList.attributesList);
    }

    private void generateIdentifierAttribute(int identifier, int attribute) {
        String _identifier = getTokenByTokensTableIndex(identifier);
        String _attribute = getTokenByTokensTableIndex(attribute);
        if (parametersIdentifiers.contains(_identifier)) {
            addError(identifier, String.format("Repeat of parameter: %s", _identifier));
            return;
        }
        if (_identifier.equals(programOrProcedureIdentifier)) {
            addError(identifier, String.format("Parameter has the same name as a procedure: %s", _identifier));
            return;
        }

        parametersIdentifiers.add(_identifier);
        parametersAttributes.add(_attribute);
        asmCode.add(String.format("\t\t%-8s%-8s%s", _identifier, "EQU", String.format("[EBP + %d]", parameterOffset)));
        parameterOffset += parameterSize(_attribute);
    }

    // Returns parameter size by attribute (e.g. INTEGER - 4)
    private int parameterSize(String parameterAttribute) {
        if (parameterAttribute.equals("SIGNAL"))
            return 4;
        if (parameterAttribute.equals("COMPLEX"))
            return 8;
        if (parameterAttribute.equals("INTEGER"))
            return 4;
        if (parameterAttribute.equals("FLOAT"))
            return 4;
        if (parameterAttribute.equals("BLOCKFLOAT"))
            return 8;
        return 8;
    }
}
