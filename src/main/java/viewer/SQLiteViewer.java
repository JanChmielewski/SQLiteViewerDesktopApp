package viewer;

import viewer.Exceptions.ErrorDialog;

import javax.swing.*;
import java.sql.Statement;

public class SQLiteViewer extends JFrame {

    public SQLiteViewer() {

        // set the title of the frame
        super("SQLite Viewer");
        JTable table = new JTable();
        add(table);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        // table chooser
        JComboBox<String> jComboBox = new JComboBox<>();
        jComboBox.setName("TablesComboBox");
        jComboBox.setBounds(10, 55, 680, 30);
        add(jComboBox);

        // query text area
        JTextArea jTextArea = new JTextArea();
        jTextArea.setName("QueryTextArea");
        jTextArea.setBounds(10, 90, 555, 50);
        add(jTextArea);

        JButton jButton = new JButton("Execute");
        jButton.setBounds(580, 90, 115, 50);
        jButton.setName("ExecuteQueryButton");
        add(jButton);

        // file chooser
        JTextField FileNameTextField = new JTextField();
        FileNameTextField.setName("FileNameTextField");
        FileNameTextField.setBounds(10, 20, 555, 30);
        add(FileNameTextField);

        JButton OpenFileButton = new JButton("Open");
        OpenFileButton.setBounds(580, 20, 115, 30);
        OpenFileButton.setName("OpenFileButton");
        add(OpenFileButton);

        // table viewer
        table.setName("Table");
        table.setFillsViewportHeight(true);
        JScrollPane tableScrollPane = new JScrollPane(table); // add table to JScrollPane
        tableScrollPane.setBounds(10, 150, 680, 700); // set the bounds of JScrollPane
        add(tableScrollPane); // add JScrollPane to the frame

        OpenFileButton.addActionListener(event -> {
            try (DataBase database = new DataBase(FileNameTextField.getText())) {
                jComboBox.removeAllItems();
                database.getTables().forEach(jComboBox::addItem);
                jTextArea.setText(String.format(DataBase.ALL_ROWS_QUERY, jComboBox.getSelectedItem()));
            } catch (Exception e) {
                ErrorDialog.show(e.getMessage());
            }
        });

        jComboBox.addItemListener(event -> jTextArea.setText(String.format(DataBase.ALL_ROWS_QUERY, event.getItem().toString())));

        jButton.addActionListener(event -> {
            String query = jTextArea.getText();
            try (DataBase database = new DataBase(FileNameTextField.getText())) {
                if (query.trim().toUpperCase().startsWith("SELECT")) {
                    // If it's a SELECT statement
                    DisplayTable tableModel = database.executeQuery(query);
                    table.setModel(tableModel);
                } else {
                    // If it's an INSERT, UPDATE, or DELETE statement
                    try (Statement statement = database.getConnection().createStatement()) {
                        int rowsAffected = statement.executeUpdate(query);
                        System.out.println("Rows affected: " + rowsAffected);
                    }
                }
            } catch (Exception e) {
                ErrorDialog.show(e.getMessage());
            }
        });

        setVisible(true);

    }
}

