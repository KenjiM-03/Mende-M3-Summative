import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanEncoder {
    // Define the size of the character alphabet (ASCII characters)
    private static final int ALPHABET_SIZE = 256;

    // Compress a given string and return the result
    public HuffmanEncodedResult compress(final String data) {
        // Build a frequency table for characters in the data
        final int[] freq = buildFrequencyTable(data);

        // Build a Huffman tree from the frequency table
        final Node root = buildHuffmanTree(freq);

        // Build a lookup table for characters and their Huffman codes
        final Map<Character, String> lookupTable = buildLookupTable(root);

        // Generate the encoded data using the lookup table
        final String encodedData = generateEncodedData(data, lookupTable);

        // Return the result, including the encoded data and the Huffman tree's root
        return new HuffmanEncodedResult(encodedData,root);
    }

    // Generate encoded data based on the lookup table
    private static String generateEncodedData(String data, Map<Character, String> lookupTable) {
        final StringBuilder builder = new StringBuilder();
        for (final char character : data.toCharArray()) {
            builder.append(lookupTable.get(character));
        }
        return builder.toString();
    }

    // Build a lookup table for characters and their Huffman codes
    private static Map<Character, String> buildLookupTable(final Node root) {
        final Map<Character, String> lookupTable = new HashMap<>();
        buildLookupTableImpl(root, "", lookupTable);
        return lookupTable;
    }

    // Helper method to recursively build the lookup table
    private static void buildLookupTableImpl(final Node node, String s,
            Map<Character, String> lookupTable) {
        if (!node.isLeaf()) {
            buildLookupTableImpl(node.leftChild, s + '0', lookupTable);
            buildLookupTableImpl(node.rightChild, s + '1', lookupTable);
        } else {
            lookupTable.put(node.character, s);
        }
    }

    // Build a frequency table for characters in the data
    private static int[] buildFrequencyTable(final String data) {
        final int[] freq = new int[ALPHABET_SIZE];
        for (final char character : data.toCharArray()) {
            freq[character]++;
        }
        return freq;
    }

    // Decompress a HuffmanEncodedResult and return the original data
    public String decompress(final HuffmanEncodedResult result) {
        final StringBuilder resultBuilder = new StringBuilder();
        Node current = result.getRoot();
        int i = 0;
        while (i < result.getEncodedData().length()) {
            while (!current.isLeaf()) {
                char bit = result.getEncodedData().charAt(i);
                if (bit == '1') {
                    current = current.rightChild;
                } else if (bit == '0') {
                    current = current.leftChild;
                } else {
                    throw new IllegalArgumentException("Invalid bit in message" + bit);
                }
                i++;
            }
            resultBuilder.append(current.character);
            current = result.getRoot();
        }
        return resultBuilder.toString();
    }

    // Define a helper class to store Huffman encoding results
    static class HuffmanEncodedResult implements Serializable {
        final Node root;
        final String encodedData;

        HuffmanEncodedResult(final String encodedData, final Node root) {
            this.encodedData = encodedData;
            this.root = root;
        }

        public String getEncodedData() {
            return this.encodedData;
        }

        public Node getRoot() {
            return this.root;
        }
    }

    // Build a Huffman tree from a frequency table
    private static Node buildHuffmanTree(int[] freq) {
        final PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if (freq[i] > 0) {
                priorityQueue.add(new Node((char) i, freq[i], null, null));
            }
        }
        while (priorityQueue.size() > 1) {
            final Node left = priorityQueue.poll();
            final Node right = priorityQueue.poll();
            final Node parent = new Node('\0', left.frequency + right.frequency, left, right);
            priorityQueue.add(parent);
        }
        return priorityQueue.poll();
    }

    // Define a helper class to represent nodes in the Huffman tree
    static class Node implements Comparable<Node>, Serializable {
        private final char character;
        private final int frequency;
        private final Node leftChild;
        private final Node rightChild;

        private Node(final char character, final int frequency, final Node leftChild, final Node rightChild) {
            this.character = character;
            this.frequency = frequency;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        // Check if the node is a leaf node
        boolean isLeaf() {
            return this.leftChild == null && this.rightChild == null;
        }

        @Override
        public int compareTo(final Node that) {
            // Compare nodes based on frequency and character
            final int frequencyComparison = Integer.compare(this.frequency, that.frequency);
            if (frequencyComparison != 0) {
                return frequencyComparison;
            }
            return Character.compare(this.character, that.character);
        }
    }

    // Debugging test cases
    public static void main(String[] args) {
        testEncodingAndDecoding("Lorem ipsum dolor sit amet");
        testEncodingAndDecoding("Hello, world!");
        testEncodingAndDecoding("AABBBC");
        testEncodingAndDecoding("This is a test message for Huffman encoding.");
    }

    private static void testEncodingAndDecoding(String input) {
        HuffmanEncoder encoder = new HuffmanEncoder();
        HuffmanEncoder.HuffmanEncodedResult result = encoder.compress(input);

        System.out.println("Original Data: " + input);
        System.out.println("Encoded Data: " + result.getEncodedData());

        String decodedData = encoder.decompress(result);

        System.out.println("Decoded Data: " + decodedData);

        if (input.equals(decodedData)) {
            System.out.println("Test Passed: Data matches after compression and decompression.");
        } else {
            System.out.println("Test Failed: Data does not match after compression and decompression.");
        }

        System.out.println("------------------------------------");
    }
}
