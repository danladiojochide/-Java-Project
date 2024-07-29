import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.print.PrinterException;

public class Main{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NotepadFrame().setVisible(true));
    }
}

class NotepadFrame extends JFrame {

    private JTextArea textArea;
    private JLabel statusLabel;
    private Font currentFont;
    private String clipboardText;
    private int fstyle = Font.PLAIN;
    private int fsize = 17;

    // Defining List of Font Styles for Text
    private String[] fontStyleValues = {"PLAIN", "BOLD", "ITALIC"};
    private int[] styleValues = {Font.PLAIN, Font.BOLD, Font.ITALIC};

    public NotepadFrame() {
        setTitle("Text Notepad");
        setSize(980, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        textArea = new JTextArea();
        currentFont = new Font("SAN_SERIF", Font.PLAIN, 20);
        textArea.setFont(currentFont);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addKeyListener(new TextKeyListener());

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        statusLabel = new JLabel("Length: 0 Line: 1");

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = createMenuItem("New", KeyEvent.VK_N, e -> newFile());
        JMenuItem openItem = createMenuItem("Open", KeyEvent.VK_O, e -> openFile());
        JMenuItem saveItem = createMenuItem("Save", KeyEvent.VK_S, e -> saveFile());
        JMenuItem printItem = createMenuItem("Print", KeyEvent.VK_P, e -> printFile());
        JMenuItem exitItem = createMenuItem("Exit", KeyEvent.VK_ESCAPE, e -> exitApplication());
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(printItem);
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem copyItem = createMenuItem("Copy", KeyEvent.VK_C, e -> copyText());
        JMenuItem pasteItem = createMenuItem("Paste", KeyEvent.VK_V, e -> pasteText());
        JMenuItem cutItem = createMenuItem("Cut", KeyEvent.VK_X, e -> cutText());
        JMenuItem selectAllItem = createMenuItem("Select All", KeyEvent.VK_A, e -> selectAllText());
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(cutItem);
        editMenu.add(selectAllItem);

        JMenu formatMenu = new JMenu("Format");
        JMenuItem fontFamilyItem = createMenuItem("Font Family", e -> changeFontFamily());
        JMenuItem fontStyleItem = createMenuItem("Font Style", e -> changeFontStyle());
        JMenuItem fontSizeItem = createMenuItem("Font Size", e -> changeFontSize());
        formatMenu.add(fontFamilyItem);
        formatMenu.add(fontStyleItem);
        formatMenu.add(fontSizeItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);

        return menuBar;
    }

    private JMenuItem createMenuItem(String text, int keyEvent, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(listener);
        return menuItem;
    }

    private JMenuItem createMenuItem(String text, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(listener);
        return menuItem;
    }

    private void newFile() {
        textArea.setText("");
        updateStatus();
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .txt files", "txt");
        chooser.addChoosableFileFilter(restrict);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                textArea.read(br, null);
                textArea.requestFocus();
                updateStatus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        JFileChooser saveAs = new JFileChooser();
        saveAs.setApproveButtonText("Save");
        int actionDialog = saveAs.showOpenDialog(this);
        if (actionDialog == JFileChooser.APPROVE_OPTION) {
            File fileName = new File(saveAs.getSelectedFile() + ".txt");
            try (BufferedWriter outFile = new BufferedWriter(new FileWriter(fileName))) {
                textArea.write(outFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printFile() {
        try {
            textArea.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    private void exitApplication() {
        dispose();
    }

    private void copyText() {
        clipboardText = textArea.getSelectedText();
    }

    private void pasteText() {
        if (clipboardText != null) {
            textArea.insert(clipboardText, textArea.getCaretPosition());
        }
    }

    private void cutText() {
        clipboardText = textArea.getSelectedText();
        textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
    }

    private void selectAllText() {
        textArea.selectAll();
    }

    private void changeFontFamily() {
        String[] fontFamilyValues = {"Agency FB", "Antiqua", "Architect", "Arial", "Calibri", "Comic Sans", "Courier", "Cursive", "Impact", "Serif"};
        JList<String> fontFamilyList = new JList<>(fontFamilyValues);
        fontFamilyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JOptionPane.showConfirmDialog(this, fontFamilyList, "Choose Font Family", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        String selectedFontFamily = fontFamilyList.getSelectedValue();
        if (selectedFontFamily != null) {
            currentFont = new Font(selectedFontFamily, fstyle, fsize);
            textArea.setFont(currentFont);
        }

       
    }

    private void changeFontStyle() {
        JList<String> fontStyleList = new JList<>(fontStyleValues);
        fontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JOptionPane.showConfirmDialog(this, fontStyleList, "Choose Font Style", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        int selectedIndex = fontStyleList.getSelectedIndex();
        if (selectedIndex != -1) {
            fstyle = styleValues[selectedIndex];
            currentFont = new Font(currentFont.getFamily(), fstyle, fsize);
            textArea.setFont(currentFont);
        }
    }

    private void changeFontSize() {
        String[] fontSizeValues = {"5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70"};
        JList<String> fontSizeList = new JList<>(fontSizeValues);
        fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JOptionPane.showConfirmDialog(this, fontSizeList, "Choose Font Size", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        String selectedFontSize = fontSizeList.getSelectedValue();
        if (selectedFontSize != null) {
            fsize = Integer.parseInt(selectedFontSize);
            currentFont = new Font(currentFont.getFamily(), fstyle, fsize);
            textArea.setFont(currentFont);
        }
    }

    private void updateStatus() {
        int length = textArea.getText().length();
        int lineCount = textArea.getLineCount();
        statusLabel.setText("Length: " + length + " Line: " + lineCount);
    }

    private class TextKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            updateStatus();
        }
    }
}
