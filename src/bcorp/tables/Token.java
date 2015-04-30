package bcorp.tables;

public class Token {
    public byte type;
    public int index_in_table;
    public int file_row, file_column;

    public Token(byte type, int index_in_table, int file_row, int file_column) {
        this.type = type;
        this.index_in_table = index_in_table;
        this.file_row = file_row;
        this.file_column = file_column;
    }
}
