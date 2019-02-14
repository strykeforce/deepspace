package frc.team2767.deepspace.subsystem.health;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class CsvReport {

  private static final File FILE = new File("/home/lvuser/healthcheck.csv");
  private final List<Test> tests;
  private final String now;

  CsvReport(List<Test> tests) {
    this.tests = tests;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    now = df.format(new Date());
  }

  void save() {
    boolean fileExists = FILE.exists();
    try (PrintWriter writer =
        new PrintWriter(Files.newBufferedWriter(FILE.toPath(), UTF_8, CREATE, APPEND))) {
      if (!fileExists) writer.write("timestamp,test,talon,testcase,current,speed,pass\r\n");
      for (Test test : tests) {
        for (TestCase testCase : test.testCases) {
          for (Result result : testCase.results) {
            writer.print(now);
            writer.print(",\"");
            writer.print(test.name);
            writer.print("\",");
            writer.print(result.id);
            writer.print(",");
            writer.print(testCase.output);
            writer.print(",");
            writer.print(result.current);
            writer.print(",");
            writer.print(result.velocity);
            writer.print(",");
            writer.print(result.hasPassed() ? "PASS" : "FAIL");
            writer.print("\r\n");
          }
        }
      }
    } catch (IOException e) {
      HealthCheckSubsystem.logger.error("can't write CSV report", e);
    }
  }
}
