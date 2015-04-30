package bcorp.syntax_tree;

public class Declaration {
    public int identifier; // Index in identifiers table
    public IdentifiersList _identifiersList;
    public int attribute;   // Index in reserved words table
    public AttributesList _attributesList;
}
