package frc.team2767.deepspace.subsystem.health;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import org.slf4j.Logger;

abstract class Test implements Callable<Boolean> {

  private static final long DEFAULT_WARMUP_MS = 500;
  private static final int DEFAULT_ITERATIONS = 50;

  final String name;
  final List<Integer> ids = new ArrayList<>();
  final List<TestCase> testCases = new ArrayList<>();
  long warmup = DEFAULT_WARMUP_MS;
  int iterations = DEFAULT_ITERATIONS;

  Test(String name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

  public void addId(int id) {
    ids.add(id);
  }

  public void addAllIds(Collection<? extends Integer> ids) {
    this.ids.addAll(ids);
  }

  public TestCase newTestCase() {
    TestCase testCase = new TestCase(this);
    testCases.add(testCase);
    return testCase;
  }

  public void setWarmup(long millis) {
    this.warmup = millis;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  void log(Logger logger) {
    logger.info(name);
    logger.info(String.format("%2s  %4s   %4s   %6s", "id", "volt", "curr", "speed"));

    for (TestCase tc : testCases) {
      for (Result result : tc.results) {
        logger.info(
            String.format(
                "%2d  %4.1f   %4.2f   %6d  %s",
                result.id,
                tc.output * 12,
                result.current,
                result.velocity,
                tc.passFailString(result.current, result.velocity)));
      }
    }
  }
}
