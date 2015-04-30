package bcorp;

import bcorp.tables.TableError;
import bcorp.tables.TableTokens;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class Scanner {
    String file_name;
    public Vector<Character> delimiters, one_symbol_tokens;
    public Vector<String> reserved_words;
    public TableTokens _tableTokens;
    public Vector<String> identifiers_table;
    public TableError _tableError;

    Scanner(String file_name) {
        this.file_name = file_name;
        _tableTokens = new TableTokens();
        identifiers_table = new Vector<String>();
        _tableError = new TableError();

        delimiters = new Vector<Character>();
        delimiters.add(';');
        delimiters.add('.');
        delimiters.add('(');
        delimiters.add(')');
        delimiters.add(':');
        delimiters.add(',');
        delimiters.add(' ');
        delimiters.add('\t');
        delimiters.add('\r');
        delimiters.add('\n');
        delimiters.add('=');
        delimiters.add('+');

        reserved_words = new Vector<String>();
        reserved_words.add("PROGRAM");
        reserved_words.add("PROCEDURE");
        reserved_words.add("BEGIN");
        reserved_words.add("END");
        reserved_words.add("SIGNAL");
        reserved_words.add("COMPLEX");
        reserved_words.add("INTEGER");
        reserved_words.add("FLOAT");
        reserved_words.add("BLOCKFLOAT");
        reserved_words.add("EXT");

        one_symbol_tokens = new Vector<Character>();
        one_symbol_tokens.add(';');
        one_symbol_tokens.add('.');
        one_symbol_tokens.add('(');
        one_symbol_tokens.add(')');
        one_symbol_tokens.add(':');
        one_symbol_tokens.add(',');
        one_symbol_tokens.add('+');
        one_symbol_tokens.add('=');
    }

    private boolean is_delimiter(char symbol) {
        return delimiters.contains(symbol);
    }

    private boolean is_one_symbol_token(char symbol) {
        return one_symbol_tokens.contains(symbol);
    }

    private boolean is_reserved_word(String word) {
        return reserved_words.contains(word);
    }

    private boolean is_identifier(String word) {
        if (Character.isLetter((word.charAt(0))) && Character.isUpperCase(word.charAt(0))) {
            for (int i = 1; i < word.length(); i++) {
                if (!Character.isLetterOrDigit(word.charAt(i)))
                    return false;
                if (Character.isLetter(word.charAt(i)) && !Character.isUpperCase(word.charAt(i)))
                    return false;
            }
        } else
            return false;
        return true;
    }

    private void add_token(String word, int file_row, int file_column) {
        byte token_type;
        int index_in_table = -1;

        if (is_reserved_word(word)) {
            token_type = 0;
            index_in_table = reserved_words.indexOf(word);
            file_column -= word.length() + 1;
        } else if (is_one_symbol_token(word.charAt(0))) {
            token_type = 1;
            index_in_table = one_symbol_tokens.indexOf(word.charAt(0));
            file_column--;
        } else if (is_identifier(word)) {
            token_type = 2;
            if (identifiers_table.contains(word))
                index_in_table = identifiers_table.indexOf(word);
            else {
                identifiers_table.add(word);
                index_in_table = identifiers_table.size() - 1;
            }
            file_column -= word.length() + 1;
        } else {
            token_type = 3;
            file_column -= word.length() + 1;
            _tableError.add_error(file_row, file_column, "Unresolved Token");
        }
        _tableTokens.add_token(token_type, index_in_table, file_row, file_column);
    }


    public void analyse() throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File(file_name), "r");

        long file_length = file.length();
        long i = 2;
        file.seek(i);
        boolean inside_comment = false;
        String word = "";
        int file_row = 1;
        int file_column = 1;
        int comment_start_row = -1;
        int comment_start_column = -1;

        while (i < file_length) {
            file_column++;
            char symbol = file.readChar();
            i += 2;

            if (!inside_comment) {
                if (is_delimiter(symbol)) {
                    if (symbol == '(' && i < file_length) {
                        char next_symbol = file.readChar();
                        if (next_symbol == '*') {
                            comment_start_row = file_row;
                            comment_start_column = file_column - 1;
                            i += 2;
                            inside_comment = true;
                            if (!word.equals("")) {
                                add_token(word, file_row, file_column);
                                word = "";
                            }
                            file_column++;
                            continue;
                        } else
                            file.seek(i);
                    }

                    if (!word.equals("")) {
                        add_token(word, file_row, file_column);
                        word = "";
                    }

                    if (is_one_symbol_token(symbol)) {
                        word += symbol;
                        add_token(word, file_row, file_column);
                        word = "";
                        continue;
                    }

                    if (symbol == '\n') {
                        file_row++;
                        file_column = 1;
                        continue;
                    }

                    if (symbol == '\t')
                        file_column += 3;
                } else
                    word += symbol;
            } else {
                if (symbol == '*' && i < file_length) {
                    char next_symbol = file.readChar();
                    file_column++;
                    if (next_symbol == ')') {
                        inside_comment = false;
                        i += 2;
                    } else
                        file.seek(i);
                } else if (symbol == '\n') {
                    file_row++;
                    file_column = 1;
                }
            }
        }
        if (!word.equals(""))
            add_token(word, file_row, ++file_column);
        if (inside_comment)
            _tableError.add_error(comment_start_row, comment_start_column, "Comment not closed");

        file.close();
    }
}