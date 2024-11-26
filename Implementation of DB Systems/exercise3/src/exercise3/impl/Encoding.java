package exercise3.impl;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;

public class Encoding {
    /**
     * Compresses the passed values using Differential Encoding.
     */
    public static int[] encodeDiff(int[] numbers) {
        int[] encoded = new int[numbers.length];
        encoded[0] = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            encoded[i] = numbers[i] - numbers[i - 1];
        }
        return encoded;
        //return null;
    }

    /**
     * Decompresses values previously compressed via Differential Encoding.
     */
    public static int[] decodeDiff(int[] numbers) {
        // TODO
        int[] decoded = new int[numbers.length];
        decoded[0] = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            decoded[i] = decoded[i - 1] + numbers[i];
        }
        return decoded;
        //return null;
    }

    /**
     * Compresses the passed values using Variable Byte Encoding.
     */
    public static byte[] encodeVB(int[] numbers) {
        // TODO
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int number : numbers) {
            while (number >= 128) {
                baos.write((number & 0x7F) | 0x80);
                number >>= 7;
            }
            baos.write(number);
        }
        return baos.toByteArray();
        //return null;
    }

    /**
     * Decompresses values previously compressed via Variable Byte Encoding.
     */
    public static int[] decodeVB(byte[] vbs) {
        // TODO
        List<Integer> decoded = new ArrayList<>();
        int current = 0;
        int shift = 0;

        for (byte b : vbs) {
            if ((b & 0x80) == 0) { // Last byte of the number
                current |= (b & 0x7F) << shift;
                decoded.add(current);
                current = 0;
                shift = 0;
            } else {
                current |= (b & 0x7F) << shift;
                shift += 7;
            }
        }
        return decoded.stream().mapToInt(i -> i).toArray();
        //return null;
    }

    public static void main(String[] args) {
        int[] seq = {1, 7, 56, 134, 256, 268, 384, 472, 512, 648};

        // Encoding using Differential Encoding
        int[] diffEncoded = encodeDiff(seq);
        System.out.println("Differential Encoded: " + java.util.Arrays.toString(diffEncoded));
        System.out.println("Size of Differential Encoded in bytes: " + (diffEncoded.length * Integer.BYTES) + " bytes");

        // Decoding using Differential Decoding
        int[] diffDecoded = decodeDiff(diffEncoded);
        System.out.println("Differential Decoded: " + java.util.Arrays.toString(diffDecoded));
        System.out.println("Size of Differential Decoded in bytes: " + (diffDecoded.length * Integer.BYTES) + " bytes");

        // Encoding using Variable Byte Encoding
        byte[] vbEncoded = encodeVB(seq);
        System.out.println("Variable Byte Encoded: " + java.util.Arrays.toString(vbEncoded));
        System.out.println("Size of Variable Byte Encoded in bytes: " + vbEncoded.length + " bytes");

        // Decoding using Variable Byte Decoding
        int[] vbDecoded = decodeVB(vbEncoded);
        System.out.println("Variable Byte Decoded: " + java.util.Arrays.toString(vbDecoded));
        System.out.println("Size of Variable Byte Decoded in bytes: " + (vbDecoded.length * Integer.BYTES) + " bytes");

    }
}