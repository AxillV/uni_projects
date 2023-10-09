import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.text.DefaultCaret; // ?


public class RedirectAppOutputStream {          //Kanei ta stream tou System.out na phgainoun se ena JTextField

    private class GuiOutputStream extends OutputStream {
        JTextArea textArea;

        public GuiOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int data) throws IOException {           //Grafei sto textArea anti gia to IDE
            textArea.append(new String(new byte[] { (byte) data }));
        }
    }

    public void guiConsoleTest(JFrame frame) {

        //Ftiaxnoume to JTextArea, ScrollPane kai ta vazoume sto frame
        //https://docs.oracle.com/javase/7/docs/api/javax/swing/text/JTextComponent.html#setEditable(boolean)
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        //https://kodejava.org/how-do-i-wrap-the-text-lines-in-jtextarea/
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBounds(10,440,330,450);

        //Autoscroll
        //https://tips4java.wordpress.com/2008/10/22/text-area-scrolling/
        //https://docs.oracle.com/javase/7/docs/api/javax/swing/text/DefaultCaret.html
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane (textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setBounds(10,440,330,450);
        frame.add(scroll);
        SwingUtilities.updateComponentTreeUI(frame);
        frame.setVisible (true);

        GuiOutputStream rawout = new GuiOutputStream(textArea);     //Vazei to stream sto textArea
        System.setOut(new PrintStream(rawout, true));
    }
}