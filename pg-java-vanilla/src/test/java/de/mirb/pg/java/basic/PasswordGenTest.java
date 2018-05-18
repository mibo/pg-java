package de.mirb.pg.java.basic;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PasswordGenTest {

  private static final char[] ALPHA_UPPER_CHARACTERS = { 'A', 'B', 'C', 'D',
    'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
    'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
  private static final char[] ALPHA_LOWER_CHARACTERS = { 'a', 'b', 'c', 'd',
    'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
    'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
  private static final char[] NUMERIC_CHARACTERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

  private String generatePassword() {
    return generatePassword(24);
  }

  private String generatePassword(int len) {
    final Random random = new Random();
    char[] pwd = new char[len];
    for (int i = 0; i < pwd.length; i++) {
      int uld = random.nextInt(Integer.MAX_VALUE) % 3;
      final int nextPosInt = random.nextInt(Integer.MAX_VALUE);
      if(uld == 0) {
        pwd[i] = ALPHA_UPPER_CHARACTERS[nextPosInt % ALPHA_UPPER_CHARACTERS.length];
      } else if(uld == 1) {
        pwd[i] = ALPHA_LOWER_CHARACTERS[nextPosInt % ALPHA_LOWER_CHARACTERS.length];
      } else if(uld == 2) {
        pwd[i] = NUMERIC_CHARACTERS[nextPosInt % NUMERIC_CHARACTERS.length];
      }
    }
    return new String(pwd);
  }

  @Test
  public void testPwd() {
    String password = generatePassword();
    System.out.println(password);
    assertNotNull(password);
  }
}
