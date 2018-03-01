package io.spring.up.tool;

import io.spring.up.cv.Encodings;
import io.spring.up.tool.fn.Fn;

import java.security.MessageDigest;

class Codec {

    private static final char[] HEX_ARR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * MD5 encript for input string.
     *
     * @param input
     * @return
     */
    static String encryptMD5(final String input) {
        return Fn.getJvm(() -> {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] source = input.getBytes(Encodings.CHARSET);
            digest.update(source);
            final byte[] middle = digest.digest();
            final char[] middleStr = new char[16 * 2];
            int position = 0;
            for (int idx = 0; idx < 16; idx++) {
                final byte byte0 = middle[idx];
                middleStr[position++] = HEX_ARR[byte0 >>> 4 & 0xF];
                middleStr[position++] = HEX_ARR[byte0 & 0xF];
            }
            return new String(middleStr);
        }, input);
    }

    /**
     * SHA-256
     *
     * @param input
     * @return
     */
    static String encryptSHA256(final String input) {
        return sha(input, "SHA-256");
    }

    /**
     * SHA-512
     *
     * @param input
     * @return
     */
    static String encryptSHA512(final String input) {
        return sha(input, "SHA-512");
    }

    private static String sha(final String strText, final String strType) {
        return Fn.getJvm(() -> {
            final MessageDigest messageDigest = MessageDigest.getInstance(strType);
            messageDigest.update(strText.getBytes());
            final byte[] byteBuffer = messageDigest.digest();
            final StringBuilder strHexString = new StringBuilder();
            for (int i = 0; i < byteBuffer.length; i++) {
                final String hex = Integer.toHexString(0xff & byteBuffer[i]);
                if (hex.length() == 1) {
                    strHexString.append('0');
                }
                strHexString.append(hex);
            }
            return strHexString.toString();
        }, strText, strType);
    }
}
