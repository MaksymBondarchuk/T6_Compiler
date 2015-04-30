package bcorp;

import bcorp.tables.TableErrors;
import bcorp.tables.TableTokens;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class Scanner {
    String file_name;
    public Vector<Character> delimiters, oneSymbolTokens;
    public Vector<String> reservedWords;
    public TableTokens _tableTokens;
    public Vector<String> _tableIdentifiers;
    public TableErrors _tableErrors;

    Scanner(String file_name) {
        this.file_name = file_name;
        _tableTokens = new TableTokens();
        _tableIdentifiers = new Vector<String>();
        _tableErrors = new TableErrors();

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

        reservedWords = new Vector<String>();
        reservedWords.add("PROGRAM");
        reservedWords.add("PROCEDURE");
        reservedWords.add("BEGIN");
        reservedWords.add("END");
        reservedWords.add("SIGNAL");
        reservedWords.add("COMPLEX");
        reservedWords.add("INTEGER");
        reservedWords.add("FLOAT");
        reservedWords.add("BLOCKFLOAT");
        reservedWords.add("EXT");

        oneSymbolTokens = new Vector<Character>();
        oneSymbolTokens.add(';');
        oneSymbolTokens.add('.');
        oneSymbolTokens.add('(');
        oneSymbolTokens.add(')');
        oneSymbolTokens.add(':');
        oneSymbolTokens.add(',');
        oneSymbolTokens.add('+');
        oneSymbolTokens.add('=');
    }

    private boolean is_delimiter(char symbol) {
        return delimiters.contains(symbol);
    }

    private boolean is_one_symbol_token(char symbol) {
        return oneSymbolTokens.contains(symbol);
    }

    private boolean is_reserved_word(String word) {
        return reservedWords.contains(word);
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
            index_in_table = reservedWords.indexOf(word);
            file_column -= word.length() + 1;
        } else if (is_one_symbol_token(word.charAt(0))) {
            token_type = 1;
            index_in_table = oneSymbolTokens.indexOf(word.charAt(0));
            file_column--;
        } else if (is_identifier(word)) {
            token_type = 2;
            if (_tableIdentifiers.contains(word))
                index_in_table = _tableIdentifiers.indexOf(word);
            else {
                _tableIdentifiers.add(word);
                index_in_table = _tableIdentifiers.size() - 1;
            }
            file_column -= word.length() + 1;
        } else {
            token_type = 3;
            file_column -= word.length() + 1;
            _tableErrors.add_error(file_row, file_column, "Unresolved Token");
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
            _tableErrors.add_error(comment_start_row, comment_start_column, "Comment not closed");

        file.close();
    }
}