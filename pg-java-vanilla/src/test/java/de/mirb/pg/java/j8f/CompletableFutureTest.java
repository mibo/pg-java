package de.mirb.pg.java.j8f;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureTest {

  @Test
  public void testMe() throws Exception {
    CompletableFuture<String> first = CompletableFuture.supplyAsync(() -> {
      String name = "first";
      System.out.println("Started " + name + "...");
      silentWait(100);
      System.out.println("...finished " + name + ".");
      return name + " result";
    });

    CompletableFuture<String> second = CompletableFuture.supplyAsync(() -> {
      String name = "Second";
      System.out.println("Started " + name + "...");
      silentWait(500);
      System.out.println("...finished " + name + ".");
      return name + " result";
    });

    CompletableFuture<String> third = CompletableFuture.supplyAsync(() -> {
      String name = "Third";
      System.out.println("Started " + name + "...");
      silentWait(1000);
      System.out.println("...finished " + name + ".");
      return name + " result";
    });

    System.out.println("=> " + first.get());
    System.out.println("=> " + second.get());
    System.out.println("=> " + third.get());
  }

  @Test
  public void testMeChained() throws Exception {
    CompletableFuture<String> first = CompletableFuture.supplyAsync(() ->
      doSomeThing("first", 100));
    first.thenRun(() -> {
      doSomeThing("Run After first", 10);
    });

    CompletableFuture<String> second = CompletableFuture.supplyAsync(() -> {
      return doSomeThing("Second", 500);
    });

    CompletableFuture<String> third = CompletableFuture.supplyAsync(() -> {
      return doSomeThing("Third", 1000);
    });

    System.out.println("=> " + first.get());
    System.out.println("=> " + second.get());
    System.out.println("=> " + third.get());
  }

  private static String doSomeThing(String name, long wait) {
//    String name = "Third";
    System.out.println("Started " + name + "...");
    silentWait(wait);
    System.out.println("...finished " + name + ".");
    return name + " result";
  }


  private static void silentWait(long waitInMs) {
    try {
      TimeUnit.MILLISECONDS.sleep(waitInMs);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
