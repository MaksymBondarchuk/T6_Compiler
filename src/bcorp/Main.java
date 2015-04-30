package bcorp;

import bcorp.tables.TableError;
import bcorp.tables.TableTokens;

import java.io.IOException;
import java.util.Vector;

public class Main {

    public static void main(String[] args) throws IOException {
        String file_name = "src/bcorp/text";
        FileChecker f_ch = new FileChecker(file_name);
        if (f_ch.check_for_utf_16()) {
            System.out.println("UTF-16BE. Welcome!");
            System.out.println();

            Scanner prsr = new Scanner("src/bcorp/text");
            prsr.analyse();

            Vector<String> reserved_words_table = prsr.reserved_words;
            for (int i = 0; i < reserved_words_table.size(); i++)
                System.out.println(i + " " + reserved_words_table.get(i));
            System.out.println();

            Vector<Character> one_symbol_tokens_table = prsr.one_symbol_tokens;
            for (int i = 0; i < one_symbol_tokens_table.size(); i++)
                System.out.println(i + " " + one_symbol_tokens_table.get(i));
            System.out.println();

            Vector<String> identifiers_table = prsr.identifiers_table;
            for (int i = 0; i < identifiers_table.size(); i++)
                System.out.println(i + " " + identifiers_table.get(i));
            System.out.println();

            TableTokens _tableTokens = prsr._tableTokens;
            for (int i = 0; i < _tableTokens.size(); i++)
                System.out.println(_tableTokens.get_token_type(i) + " " + _tableTokens.get_token_index_in_table(i) + " " + _tableTokens.get_token_file_row(i) + " " + _tableTokens.get_token_file_column(i));
            System.out.println();

            TableError _tableError = prsr._tableError;

            Parser s_a = new Parser(reserved_words_table, one_symbol_tokens_table, identifiers_table, _tableTokens, _tableError);

            if (s_a.try_analyse()) {
                System.out.println(true);
                System.out.println();
                s_a.tree.print();
                System.out.println();
            } else System.out.println(false);

            _tableError = s_a._tableError;
            for (int i = 0; i < _tableError.size(); i++)
                System.out.println(_tableError.get_error_row(i) + " " + _tableError.get_error_column(i) + " " + _tableError.get_error_comment(i));
            System.out.println();
        } else
            System.out.println("Use UTF-16BE");
    }
}