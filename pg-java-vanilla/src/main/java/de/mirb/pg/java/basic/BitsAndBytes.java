package de.mirb.pg.java.basic;

import java.util.BitSet;

public class BitsAndBytes {

  public static void main(String ... args) {
    hexToBits(0x82, 0xfe, 0, 0xda);
    byteToBits(new byte[]{(byte) 0x82, (byte) 0xfe, (byte)0xff, (byte) 0xda});
    byteToBits(new byte[]{-126, -2, 0, -5, -59});
  }

  private static void hexToBits(int ... hexValues) {
    byte[] bytes = new byte[hexValues.length];
    for (int i = 0; i < hexValues.length; i++) {
      if(hexValues[i] > 255) {
        throw new RuntimeException("Hex value higher then 255");
      }
      bytes[i] = (byte) hexValues[i];
    }

    byteToBits(bytes, false, false, true);
  }
  private static void byteToBits(byte[] bytes) {
    byteToBits(bytes, false, false, true);
  }

  private static void byteToBits(byte[] bytes, boolean uInt, boolean setBits, boolean bits) {
    System.out.println("----");
    for (byte b : bytes) {
      if(uInt) {
        System.out.println("Unsigned int:: " + (b & 0xFF));
      }
      final BitSet bitSet = BitSet.valueOf(new byte[] { b });
      if(setBits) {
        System.out.println("Bits:: " + bitSet);
      }

      if(bits) {
        for (int i = 7; i >= 0; i--) {
          System.out.print(bitSet.get(i)? "1":"0");
          if(i==4) {
            System.out.print(".");
          }
        }
        System.out.println("");
      }
    }
    System.out.println("----");
  }
}
