/*
 *
 *
    Copyright 2011 schirinowski@gmail.com
    This file is part of WebSMSsend.
    The following code is based on parts of the LessIsMore package by fkoester.
    For details on LessIsMore see <http://wiki.evolvis.org/lessismore/index.php/LessIsMore>
    and <https://evolvis.org/scm/viewvc.php/lessismore/LessIsMore-MIDlet-Prototype/trunk/src/org/evolvis/lessismore/>

    WebSMSsend is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    WebSMSsend is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with WebSMSsend.  If not, see <http://www.gnu.org/licenses/>.

 *
 *
 */

package ConnectorBase;

import java.util.Hashtable;

/**
 * This class provides methods for character encoding Unicode {@link String 
 * Strings} and decoding CP1252 encoded byte arrays according to the CP1252
 * charater encoding as defined by
 * <a href="ftp://ftp.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WINDOWS/CP1252.TXT">
 * ftp://ftp.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WINDOWS/CP1252.TXT</a>.
 * Note that the conversion is based on the differences between Unicode and
 * CP1252.
 *
 * @author Copyright 2011 schirinowski@gmail.com
 * @version $Revision$
 */
public class CP1252Coder {
    private static Hashtable unicodeToCP1252 = null;
    private static Hashtable cp1252toUnicode = null;


    private static Hashtable getUnicodeToCP1252Map() {
        if (unicodeToCP1252 == null) {
            // Mapping is taken from
            // ftp://ftp.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WINDOWS/CP1252.TXT
            unicodeToCP1252 = new Hashtable();
            unicodeToCP1252.put("\u20AC", new Byte((byte) 0x80)); //EURO SIGN
            unicodeToCP1252.put("\u201A", new Byte((byte) 0x82)); //SINGLE LOW-9 QUOTATION MARK
            unicodeToCP1252.put("\u0192", new Byte((byte) 0x83)); //LATIN SMALL LETTER F WITH HOOK
            unicodeToCP1252.put("\u201E", new Byte((byte) 0x84)); //DOUBLE LOW-9 QUOTATION MARK
            unicodeToCP1252.put("\u2026", new Byte((byte) 0x85)); //HORIZONTAL ELLIPSIS
            unicodeToCP1252.put("\u2020", new Byte((byte) 0x86)); //DAGGER
            unicodeToCP1252.put("\u2021", new Byte((byte) 0x87)); //DOUBLE DAGGER
            unicodeToCP1252.put("\u02C6", new Byte((byte) 0x88)); //MODIFIER LETTER CIRCUMFLEX ACCENT
            unicodeToCP1252.put("\u2030", new Byte((byte) 0x89)); //PER MILLE SIGN
            unicodeToCP1252.put("\u0160", new Byte((byte) 0x8A)); //LATIN CAPITAL LETTER S WITH CARON
            unicodeToCP1252.put("\u2039", new Byte((byte) 0x8B)); //SINGLE LEFT-POINTING ANGLE QUOTATION MARK
            unicodeToCP1252.put("\u0152", new Byte((byte) 0x8C)); //LATIN CAPITAL LIGATURE OE
            unicodeToCP1252.put("\u017D", new Byte((byte) 0x8E)); //LATIN CAPITAL LETTER Z WITH CARON
            unicodeToCP1252.put("\u2018", new Byte((byte) 0x91)); //LEFT SINGLE QUOTATION MARK
            unicodeToCP1252.put("\u2019", new Byte((byte) 0x92)); //RIGHT SINGLE QUOTATION MARK
            unicodeToCP1252.put("\u201C", new Byte((byte) 0x93)); //LEFT DOUBLE QUOTATION MARK
            unicodeToCP1252.put("\u201D", new Byte((byte) 0x94)); //RIGHT DOUBLE QUOTATION MARK
            unicodeToCP1252.put("\u2022", new Byte((byte) 0x95)); //BULLET
            unicodeToCP1252.put("\u2013", new Byte((byte) 0x96)); //EN DASH
            unicodeToCP1252.put("\u2014", new Byte((byte) 0x97)); //EM DASH
            unicodeToCP1252.put("\u02DC", new Byte((byte) 0x98)); //SMALL TILDE
            unicodeToCP1252.put("\u2122", new Byte((byte) 0x99)); //TRADE MARK SIGN
            unicodeToCP1252.put("\u0161", new Byte((byte) 0x9A)); //LATIN SMALL LETTER S WITH CARON
            unicodeToCP1252.put("\u203A", new Byte((byte) 0x9B)); //SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
            unicodeToCP1252.put("\u0153", new Byte((byte) 0x9C)); //LATIN SMALL LIGATURE OE
            unicodeToCP1252.put("\u017E", new Byte((byte) 0x9E)); //LATIN SMALL LETTER Z WITH CARON
            unicodeToCP1252.put("\u0178", new Byte((byte) 0x9F)); //LATIN CAPITAL LETTER Y WITH DIAERESIS
        }
        return unicodeToCP1252;
    }


    private static Hashtable getCP1252toUnicodeMap() {
        if (cp1252toUnicode == null) {
            // Mapping is taken from
            // ftp://ftp.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WINDOWS/CP1252.TXT
            cp1252toUnicode = new Hashtable();
            cp1252toUnicode.put(new Byte((byte) 0x80), "\u20AC"); // EURO SIGN
            cp1252toUnicode.put(new Byte((byte) 0x81), "\u003F"); // UNDEFINED
            cp1252toUnicode.put(new Byte((byte) 0x82), "\u201A"); // SINGLE LOW-9 QUOTATION MARK
            cp1252toUnicode.put(new Byte((byte) 0x83), "\u0192"); // LATIN SMALL LETTER F WITH HOOK
            cp1252toUnicode.put(new Byte((byte) 0x84), "\u201E"); // DOUBLE LOW-9 QUOTATION MARK
            cp1252toUnicode.put(new Byte((byte) 0x85), "\u2026"); // HORIZONTAL ELLIPSIS
            cp1252toUnicode.put(new Byte((byte) 0x86), "\u2020"); // DAGGER
            cp1252toUnicode.put(new Byte((byte) 0x87), "\u2021"); // DOUBLE DAGGER
            cp1252toUnicode.put(new Byte((byte) 0x88), "\u02C6"); // MODIFIER LETTER CIRCUMFLEX ACCENT
            cp1252toUnicode.put(new Byte((byte) 0x89), "\u2030"); // PER MILLE SIGN
            cp1252toUnicode.put(new Byte((byte) 0x8A), "\u0160"); // LATIN CAPITAL LETTER S WITH CARON
            cp1252toUnicode.put(new Byte((byte) 0x8B), "\u2039"); // SINGLE LEFT-POINTING ANGLE QUOTATION MARK
            cp1252toUnicode.put(new Byte((byte) 0x8C), "\u0152"); // LATIN CAPITAL LIGATURE OE
            cp1252toUnicode.put(new Byte((byte) 0x8D), "\u003F"); // UNDEFINED
            cp1252toUnicode.put(new Byte((byte) 0x8E), "\u017D"); // LATIN CAPITAL LETTER Z WITH CARON
            cp1252toUnicode.put(new Byte((byte) 0x8F), "\u003F"); // UNDEFINED
            cp1252toUnicode.put(new Byte((byte) 0x90), "\u003F"); // UNDEFINED
            cp1252toUnicode.put(new Byte((byte) 0x91), "\u2018"); // LEFT SINGLE QUOTATION MARK
            cp1252toUnicode.put(new Byte((byte) 0x92), "\u2019"); // RIGHT SINGLE QUOTATION MARK
            cp1252toUnicode.put(new Byte((byte) 0x93), "\u201C"); // LEFT DOUBLE QUOTATION MARK
            cp1252toUnicode.put(new Byte((byte) 0x94), "\u201D"); // RIGHT DOUBLE QUOTATION MARK
            cp1252toUnicode.put(new Byte((byte) 0x95), "\u2022"); // BULLET
            cp1252toUnicode.put(new Byte((byte) 0x96), "\u2013"); // EN DASH
            cp1252toUnicode.put(new Byte((byte) 0x97), "\u2014"); // EM DASH
            cp1252toUnicode.put(new Byte((byte) 0x98), "\u02DC"); // SMALL TILDE
            cp1252toUnicode.put(new Byte((byte) 0x99), "\u2122"); // TRADE MARK SIGN
            cp1252toUnicode.put(new Byte((byte) 0x9A), "\u0161"); // LATIN SMALL LETTER S WITH CARON
            cp1252toUnicode.put(new Byte((byte) 0x9B), "\u203A"); // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
            cp1252toUnicode.put(new Byte((byte) 0x9C), "\u0153"); // LATIN SMALL LIGATURE OE
            cp1252toUnicode.put(new Byte((byte) 0x9D), "\u003F"); // UNDEFINED
            cp1252toUnicode.put(new Byte((byte) 0x9E), "\u017E"); // LATIN SMALL LETTER Z WITH CARON
            cp1252toUnicode.put(new Byte((byte) 0x9F), "\u0178"); // LATIN CAPITAL LETTER Y WITH DIAERESIS
        }
        return cp1252toUnicode;
    }

    /**
     * Encodes a Unicode {@link String} to a CP1252 encoded byte[].
     * @param string Unicode String to be encoded
     * @return CP1252 encoded byte[]
     */
    public static byte[] encode(String string) {
        char[] stringAsChars = string.toCharArray();
        return encode(stringAsChars);
    }

    /**
     * Encodes a char[] representing a Unicode {@link String} to a CP1252
     * encoded byte[].
     * @param string Unicode String represented as a char[] to be encoded
     * @return CP1252 encoded byte[]
     */
    public static byte[] encode(char[] string) {
        Hashtable conversionMap = getUnicodeToCP1252Map();
        byte[] cp1252String = new byte[0];

        for (int i = 0; i < string.length; i++) {
            byte[] extended = new byte[cp1252String.length + 1];
            System.arraycopy(cp1252String, 0, extended, 0, cp1252String.length);
            cp1252String = extended;

            // Transform Unicode character to unsigned byte
            if (conversionMap.containsKey("" + string[i])) {
                Byte subst = (Byte) conversionMap.get("" + string[i]);
                cp1252String[cp1252String.length - 1] = (byte) (subst.byteValue() & 0xFF);
            } else {
                cp1252String[cp1252String.length - 1] = (byte) (string[i] & 0xFF);
            }
        }
        return cp1252String;
    }

    /**
     * Decodes a CP1252 encoded byte[] to its Unicode {@link String} representation.
     * @param bytes CP1252 encoded byte[] to be decoded
     * @return the byte[]'s String representation
     */
    public static String decodeToString(byte[] bytes) {
        return new String(decode(bytes));
    }

    /**
     * Decodes a CP1252 encoded byte[] to its Unicode char[] representation.
     * @param bytes CP1252 encoded byte[] to be decoded
     * @return the byte[]'s Unicode char[] representation
     */
    public static char[] decode(byte[] bytes) {
        Hashtable conversionMap = getCP1252toUnicodeMap();
        char[] unicodeString = new char[0];

        for (int i = 0; i < bytes.length; i++) {
            char[] extended = new char[unicodeString.length + 1];
            System.arraycopy(unicodeString, 0, extended, 0, unicodeString.length);
            unicodeString = extended;

            Byte b = new Byte(bytes[i]);
            if (conversionMap.containsKey(b)) {
                String subst = (String) conversionMap.get(b);
                unicodeString[unicodeString.length - 1] = subst.charAt(0);
            } else {
                // Create the Unicode char for further processing
                // from the unsigned byte value
                unicodeString[unicodeString.length - 1] = (char) (bytes[i] & 0xFF);
            }
        }
        return unicodeString;
    }
}
