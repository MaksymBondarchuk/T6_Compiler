package bcorp.syntax_tree;

public class AttributesList {  // <attribute><AttributesList>|<empty>
    public int attribute;   // Index in reserved words table
    public AttributesList _attributesList;
    public boolean empty;
}
