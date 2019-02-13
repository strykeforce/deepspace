package frc.team2767.deepspace.subsystem.health;

import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthCheckSubsystem extends Subsystem {

  static final Logger logger = LoggerFactory.getLogger(HealthCheckSubsystem.class);

  private ExecutorService executorService;
  private Future<Boolean> future;

  private List<Test> tests = Collections.emptyList();
  private boolean initialized = false;

  public HealthCheckSubsystem() {}

  public void initialize() {
    if (executorService == null) executorService = Executors.newSingleThreadExecutor();
    tests = new ArrayList<>();
    initialized = true;
    logger.debug("initialized health check subsystem");
  }

  public void runTest(Test test) {
    if (!initialized) throw new IllegalStateException("must initialize before running tests");
    test.testCases.forEach(tc -> tc.results.clear());
    tests.add(test);
    future = executorService.submit(test);
  }

  public void saveReports() {
    if (!initialized) throw new IllegalStateException("must initialize before saving reports");
    future =
        executorService.submit(
            new Callable<Boolean>() {
              @Override
              public Boolean call() throws Exception {
                HtmlReport htmlReport = new HtmlReport(tests);
                htmlReport.save();
                CsvReport csvReport = new CsvReport(tests);
                csvReport.save();
                return true;
              }
            });
  }

  public boolean isFinished() {
    return future.isDone();
  }

  public void cancel() {
    logger.info("healthcheck cancelled");
    executorService.shutdownNow();
    executorService = null;
    end();
  }

  public void end() {
    if (initialized) logger.info("healthcheck ended");
    initialized = false;
  }

  @Override
  protected void initDefaultCommand() {}
}
