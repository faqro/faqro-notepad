
/*
faqro-notepad made by Faraaz Jan
(c) Faraaz Jan 2023
Redistributing without prior permission is not allowed.
*/

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.util.Vector;
import java.lang.Math;

import javax.swing.filechooser.FileNameExtensionFilter;

public class Notepad extends JFrame implements KeyListener, ActionListener {

    static String VERSION="1.0.0";
    static String APPNAME="faqro-notepad";

    
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            pingUpdateUndo();
        }
    };
    Timer timer = new Timer(2000, al);

    public void pingUpdateUndo() {
        if(area.getText().compareTo(undoData.get(undoSelector))!=0) {
            if(undoSelector==0) {
                undoData.insertElementAt(area.getText(), 0);
            } else {
                remVec(0, undoSelector-1);
                undoSelector=0;
                undoData.insertElementAt(area.getText(), 0);
            }
        }

        if(undoData.size()>MAXUNDOHISTORY&&(undoData.size()-1)!=undoSelector) {
            remVec(Math.max(MAXUNDOHISTORY, undoSelector+1), undoData.size()-1);
        }
    }
    
    private JScrollPane scpane;
    private JTextArea area;
    JFrame f = new JFrame(APPNAME);
    Image icon = Toolkit.getDefaultToolkit().getImage("src/icon.png");

    Vector<String> undoData = new Vector<>();
    int undoSelector = 0;
    boolean allowUndoSaving = true;
    private static int MAXUNDOHISTORY = 5;
    
    JLabel detailsOfFile;
    JPanel bottomPanel;
    JMenuBar menuBar;
    JMenu file, edit, format;
    JMenuItem newdoc, open, save, print, exit, about, info;
    JMenuItem copy, paste, cut, selectall, undo, redo;
    JMenuItem fontfamily, fontstyle, fontsize;
    JList<String> fontFamilyList, fontStyleList, fontSizeList;

    String text = "";
    Font newFont;
    String fontFamily, fontSize, fontStyle;
    int fstyle;
    int fsize = 17;

    int cl, linecount;
    String lastsavedcontent="";

    String fontFamilyValues[] = {"Agency FB", "Antiqua", "Architect", "Arial", "Calibri", "Comic Sans", "Courier", "Cursive", "Impact", "Serif", "Times New Roman"};
    String fontSizeValues[] = {"5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70"};
    int stylevalue[] = {Font.PLAIN, Font.BOLD, Font.ITALIC};
    String fontStyleValues[] = {"PLAIN", "BOLD", "ITALIC"};

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public Notepad() {
        initUI();
        
        timer.start();
        addActionEvents();
    }

    public void remVec(int i1, int i2) {
        for(int i = 0; i < ((i2-i1)+1); i++) {
            undoData.remove(i1);
        }
    }

    public void resetUndoData() {
        undoData.setSize(1);
        undoData.set(0, area.getText());
    }

    public void actionPerformed(ActionEvent ae) {
        if(ae.getActionCommand().equals("New")) {
            area.setText("");
            resetUndoData();
            lastsavedcontent=area.getText();
            updateWindow();
        } else if(ae.getActionCommand().equals("Open")) {
            if(lastsavedcontent.compareTo(area.getText())!=0) {
                int resultt = JOptionPane.showConfirmDialog(null, "Are you sure? You have unsaved work.", "Unsaved file", JOptionPane.YES_NO_OPTION);
                if(resultt == JOptionPane.YES_OPTION) {
                    JFileChooser chooser = new JFileChooser("C:/");
                    chooser.setAcceptAllFileFilterUsed(false);
                    FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .txt files", "txt");
                    chooser.addChoosableFileFilter(restrict);

                    int result = chooser.showOpenDialog(f);
                    if(result == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        try {
                            FileReader reader = new FileReader(file);
                            BufferedReader br = new BufferedReader(reader);
                            area.read(br, null);
                            br.close();
                            area.requestFocus();
                            resetUndoData();
                            lastsavedcontent=area.getText();
                            updateWindow();
                        } catch (Exception e) {
                            System.out.print(e);
                        }
                    }
                }
            } else {
                JFileChooser chooser = new JFileChooser("C:/");
                chooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .txt files", "txt");
                chooser.addChoosableFileFilter(restrict);

                int result = chooser.showOpenDialog(f);
                if(result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        FileReader reader = new FileReader(file);
                        BufferedReader br = new BufferedReader(reader);
                        area.read(br, null);
                        br.close();
                        area.requestFocus();
                        resetUndoData();
                        updateWindow();
                    } catch (Exception e) {
                        System.out.print(e);
                    }
                }
            }
        } else if (ae.getActionCommand().equals("Save")) {
            final JFileChooser SaveAs = new JFileChooser();
            SaveAs.setApproveButtonText("Save");
            int actionDialog = SaveAs.showOpenDialog(f);
            if(actionDialog != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File fileName = new File(SaveAs.getSelectedFile()+".txt");
            BufferedWriter outFile = null;
            try {
                outFile = new BufferedWriter(new FileWriter(fileName));
                area.write(outFile);
                lastsavedcontent=area.getText();
                updateWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(ae.getActionCommand().equals("Print")) {
            try {
                area.print();
            } catch (Exception e) {}
        } else if(ae.getActionCommand().equals("Exit")) {
            attemptClose();
        } else if(ae.getActionCommand().equals("Software Info")) {
            JOptionPane.showMessageDialog(null, APPNAME+" v"+VERSION+"\nMade by Faraaz Jan\nhttps://faraaz.page/", "About "+APPNAME, JOptionPane.INFORMATION_MESSAGE);
        } else if(ae.getActionCommand().equals("About")) {
            updateInf();
            JOptionPane.showMessageDialog(null, detailsOfFile, "Document info", JOptionPane.INFORMATION_MESSAGE);
        } else if(ae.getActionCommand().equals("Copy")) {
            String myString = area.getSelectedText();
            setClipboard(myString);
            
        } else if(ae.getActionCommand().equals("Paste")) {
            allowUndoSaving=false;
            String pastedText = getClipboard();
            area.replaceRange("", area.getSelectionStart(), area.getSelectionEnd());

            if(pastedText!=null && pastedText.length()>0) {
                area.insert(pastedText, area.getCaretPosition());
            }

            allowUndoSaving=true;
            undoData.insertElementAt(area.getText(), 0);
            pingUpdateUndo();
            updateWindow();
        } else if(ae.getActionCommand().equals("Cut")) {
            allowUndoSaving=true;
            String myString = area.getSelectedText();
            setClipboard(myString);

            area.replaceRange("", area.getSelectionStart(), area.getSelectionEnd());

            allowUndoSaving=true;
            undoData.insertElementAt(area.getText(), 0);
            pingUpdateUndo();
            updateWindow();
        } else if(ae.getActionCommand().equals("Select All")) {
            area.selectAll();
        } else if(ae.getActionCommand().equals("Undo")) {
            allowUndoSaving=false;
            if(undoData.size()>(undoSelector+1)) {
                undoData.set(undoSelector, area.getText());
                undoSelector++;
                area.setText(undoData.get(undoSelector));
            } allowUndoSaving=true;
        } else if(ae.getActionCommand().equals("Redo")) {
            allowUndoSaving=false;
            if(0<undoSelector) {
                undoData.set(undoSelector, area.getText());
                undoSelector--;
                area.setText(undoData.get(undoSelector));
            } allowUndoSaving=true;
        } else if(ae.getActionCommand().equals("Font Family")) {
            JOptionPane.showConfirmDialog(null, fontFamilyList, "Choose Font Family", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            newFont = new Font(fontFamily, fstyle, fsize);
            area.setFont(newFont);
        } else if(ae.getActionCommand().equals("Font Style")) {
            JOptionPane.showConfirmDialog(null, fontStyleList, "Choose Font Style", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            fstyle = stylevalue[fontStyleList.getSelectedIndex()];
            newFont = new Font(fontFamily, fstyle, fsize);
            area.setFont(newFont);
        } else if(ae.getActionCommand().equals("Font Size")) {
            JOptionPane.showConfirmDialog(null, fontSizeList, "Choose Font Size", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            fontSize = String.valueOf(fontSizeList.getSelectedValue());
            fsize = Integer.parseInt(fontSize);
            newFont = new Font(fontFamily, fstyle, fsize);
            area.setFont(newFont);
        }
    }

    public void setClipboard(String s) {
        StringSelection stringSelection = new StringSelection(s);
        clipboard.setContents(stringSelection, null);
    }

    public static String getClipboard() {
        try {
            return (String) Toolkit
                .getDefaultToolkit()
                .getSystemClipboard()
                .getData(DataFlavor.stringFlavor);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (HeadlessException | IOException | UnsupportedFlavorException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void attemptClose() {
        if(lastsavedcontent.compareTo(area.getText())!=0) {
            int result = JOptionPane.showConfirmDialog(null, "Are you sure? You have unsaved work.", "Unsaved file", JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION) f.dispose();
        } else {
            f.dispose();
        }
    }

    public void updateInf() {
        cl = area.getText().length();
        linecount = area.getLineCount();
        detailsOfFile.setText("Length: " + cl + " Lines: " + linecount);
    }

    public void updateWindow() {
        if(lastsavedcontent.compareTo(area.getText())==0) f.setTitle(APPNAME);
        else f.setTitle(APPNAME+"*");
    }

    //@Override
    public void keyTyped(KeyEvent e) {
        updateWindow();
    }
    public void keyPressed(KeyEvent e) {
        updateWindow();
    }
    public void keyReleased(KeyEvent e) {
        updateWindow();
    }

    public void initUI() {
        detailsOfFile = new JLabel();
        bottomPanel = new JPanel();
        menuBar = new JMenuBar();

        file = new JMenu("File");
        newdoc = new JMenuItem("New");
        newdoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        open = new JMenuItem("Open");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        save = new JMenuItem("Save");
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        print = new JMenuItem("Print");
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        exit = new JMenuItem("Exit");
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        about = new JMenuItem("About");
        info = new JMenuItem("Software Info");

        edit = new JMenu("Edit");
        copy = new JMenuItem("Copy");
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        paste = new JMenuItem("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        cut = new JMenuItem("Cut");
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        selectall = new JMenuItem("Select All");
        selectall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        undo = new JMenuItem("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        redo = new JMenuItem("Redo");
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));

        format = new JMenu("Format");
        fontfamily = new JMenuItem("Font Family");
        fontstyle = new JMenuItem("Font Style");
        fontsize = new JMenuItem("Font Size");
        fontFamilyList = new JList<String>(fontFamilyValues);
        fontStyleList = new JList<String>(fontStyleValues);
        fontSizeList = new JList<String>(fontSizeValues);
        
        fontFamilyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        area = new JTextArea();
        area.setFont(new Font("SAN_SERIF", Font.PLAIN, 20));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.addKeyListener(this);
        scpane = new JScrollPane(area);
        scpane.setBorder(BorderFactory.createEmptyBorder());
        f.setJMenuBar(menuBar);

        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(format);

        file.add(newdoc);
        file.add(open);
        file.add(save);
        file.add(print);
        file.add(exit);
        file.add(about);
        file.add(info);

        edit.add(copy);
        edit.add(paste);
        edit.add(cut);
        edit.add(selectall);
        edit.add(undo);
        edit.add(redo);

        format.add(fontfamily);
        format.add(fontstyle);
        format.add(fontsize);

        bottomPanel.add(detailsOfFile);

        f.setSize(640, 480);
        f.setLayout(new BorderLayout());
        f.setIconImage(icon);  
        f.add(scpane, BorderLayout.CENTER);
        f.setVisible(true);
        f.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
              attemptClose();
            }
        });

        resetUndoData();
    }

    public void addActionEvents() {
        newdoc.addActionListener(this);
        save.addActionListener(this);
        print.addActionListener(this);
        exit.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        cut.addActionListener(this);
        selectall.addActionListener(this);
        open.addActionListener(this);
        fontfamily.addActionListener(this);
        fontsize.addActionListener(this);
        fontstyle.addActionListener(this);
        info.addActionListener(this);
        undo.addActionListener(this);
        redo.addActionListener(this);
        about.addActionListener(this);
    }

    

    public static void main(String ar[]) {
        Notepad tnp = new Notepad();
    }
}
