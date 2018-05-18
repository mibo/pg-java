package de.mirb.pg.java.j8f;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AutoCloseableTest {
  // closeable

  private static class MyClosable implements AutoCloseable {
    private boolean throwExceptionOnClose = false;

    public String doSomething(boolean throwExceptionOnDo, boolean throwExceptionOnClose) throws IOException {
      if(throwExceptionOnClose) {
        this.throwExceptionOnClose = throwExceptionOnClose;
        System.out.println("Set throwExceptionOnClose");
      }
      if(throwExceptionOnDo) {
        throw new IOException("some exception");
      }
      return "done";
    }

    @Override
    public void close() {
      System.out.println("Close called");
    }
  }


  @Test
  public void closableWithoutException() {
    System.out.println("START::closableWithoutException");

    try(MyClosable closable = new MyClosable()) {
      String result = closable.doSomething(false, false);
      System.out.println("Result: " + result);
    } catch (IOException e) {
      System.out.println("Exception thrown in test");
    }
    System.out.println("END::closableWithoutException\n");
  }

  @Test
  public void closableWithoutExceptionAndInnerException() {
    System.out.println("START::closableWithoutExceptionAndInnerException");

    try(MyClosable closable = new MyClosable()) {
      String result = closable.doSomething(false, false);
      System.out.println("Result: " + result);
      throw new IOException("Inner IOException");
    } catch (IOException e) {
      System.out.println("Exception thrown in test: " + e.getMessage());
    }
    System.out.println("END::closableWithoutExceptionAndInnerException\n");
  }

  @Test
  public void closableWithoutExceptionAndInnerExceptionUnhandled() throws IOException {
    System.out.println("START::closableWithoutExceptionAndInnerExceptionUnhandled");

    assertThrows(IOException.class, () -> {
      try(MyClosable closable = new MyClosable()) {
        String result = closable.doSomething(false, false);
        System.out.println("Result: " + result);
        throw new IOException("Inner IOException");
      }
    });
  }

  @Test
  public void closableWithException() {
    System.out.println("START::closableWithException");

    try(MyClosable closable = new MyClosable()) {
      String result = closable.doSomething(true, false);
      System.out.println("Result: " + result);
    } catch (IOException e) {
      System.out.println("Exception thrown in test");
    }
    System.out.println("END::closableWithException\n");
  }

  @Test
  public void closableWithExceptionAndInnerException() {
    System.out.println("START::closableWithExceptionAndInnerException");
    try(MyClosable closable = new MyClosable()) {
      String result = closable.doSomething(true, false);
      System.out.println("Result: " + result);
      throw new IOException("Inner IOException");
    } catch (IOException e) {
      System.out.println("Exception thrown in test: " + e.getMessage());
    }
    System.out.println("END::closableWithExceptionAndInnerException\n");
  }
}
