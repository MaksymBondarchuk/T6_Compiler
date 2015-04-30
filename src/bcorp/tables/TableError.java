package bcorp.tables;

import java.util.Vector;

public class TableError {
    public Vector<Integer> errors_rows;
    public Vector<Integer> errors_columns;
    public Vector<String> errors_comments;

    public TableError() {
        errors_rows = new Vector<Integer>();
        errors_columns = new Vector<Integer>();
        errors_comments = new Vector<String>();
    }

    public int get_error_row(int index) {
        return errors_rows.get(index);
    }

    public int get_error_column(int index) {
        return errors_columns.get(index);
    }

    public String get_error_comment(int index) {
        return errors_comments.get(index);
    }

    public void add_error(int row, int column, String comment) {
        errors_rows.add(row);
        errors_columns.add(column);
        errors_comments.add(comment);
    }

    public int size() {
        return errors_rows.size();
    }
}
