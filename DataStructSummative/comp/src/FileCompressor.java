import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class FileCompressor {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Huffman File Compressor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        panel.setLayout(new FlowLayout());

        JButton compressButton = new JButton("Compress File");
        JButton decompressButton = new JButton("Decompress File");

        panel.add(compressButton);
        panel.add(decompressButton);

        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File inputFilePath = selectFile("Select the input file for compression");
                if (inputFilePath != null) {
                    try {
                        String data = readFile(inputFilePath);
                        HuffmanEncoder encoder = new HuffmanEncoder();
                        HuffmanEncoder.HuffmanEncodedResult result = encoder.compress(data);

                        // Create the compressed file in the same folder as the input file
                        File outputFilePath = new File(inputFilePath.getParentFile(), "compressed_" + inputFilePath.getName());

                        writeEncodedFile(outputFilePath, result);

                        JOptionPane.showMessageDialog(null, "File compressed successfully. Compressed file saved as: " + outputFilePath);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        decompressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File inputFilePath = selectFile("Select the compressed file for decompression");
                if (inputFilePath != null) {
                    try {
                        HuffmanEncoder encoder = new HuffmanEncoder();
                        HuffmanEncoder.HuffmanEncodedResult result = readEncodedFile(inputFilePath);

                        // Create the decompressed file in the same folder as the input file
                        File outputFilePath = new File(inputFilePath.getParentFile(), "decompressed_" + inputFilePath.getName());

                        String decodedData = encoder.decompress(result);

                        writeFile(outputFilePath, decodedData);

                        JOptionPane.showMessageDialog(null, "File decompressed successfully. Decompressed file saved as: " + outputFilePath);
                    } catch (IOException | ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private static File selectFile(String dialogTitle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);
        int returnValue = fileChooser.showDialog(null, "Select");

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    private static String readFile(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[128];
            int bytesRead;

            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }

            return new String(buffer.toByteArray());
        }
    }

    private static void writeEncodedFile(File file, HuffmanEncoder.HuffmanEncodedResult result) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(result);
        }
    }

    private static HuffmanEncoder.HuffmanEncodedResult readEncodedFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (HuffmanEncoder.HuffmanEncodedResult) inputStream.readObject();
        }
    }

    private static void writeFile(File file, String data) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(data.getBytes());
        }
    }
}
