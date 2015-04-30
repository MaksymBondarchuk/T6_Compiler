package bcorp.tables;

import java.util.Vector;

public class TableTokens {
    public Vector<Token> Tokens;

    public TableTokens() {
        Tokens = new Vector<Token>();
    }

    public void add_token(byte type, int index_in_table, int file_row, int file_column) {
        Token _token = new Token(type, index_in_table, file_row, file_column);
        Tokens.add(_token);
    }

    public Token get(int index) {
        return Tokens.get(index);
    }

    public byte get_token_type(int index) {
        return Tokens.get(index).type;
    }

    public int get_token_index_in_table(int index) {
        return Tokens.get(index).index_in_table;
    }

    public int get_token_file_row(int index) {
        return Tokens.get(index).file_row;
    }

    public int get_token_file_column(int index) {
        return Tokens.get(index).file_column;
    }

    public int size() {
        return Tokens.size();
    }
}