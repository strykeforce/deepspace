package frc.team2767.deepspace.subsystem.health;

import static j2html.TagCreator.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import j2html.TagCreator;
import j2html.tags.Tag;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class HtmlReport {

  private static final File FILE = new File("/home/lvuser/healthcheck.html");

  private final List<Test> tests;

  HtmlReport(List<Test> tests) {
    this.tests = tests;
  }

  void save() {
    try (Writer writer = Files.newBufferedWriter(FILE.toPath(), UTF_8)) {
      TagCreator.html(
              head(
                  title("Health Check Results"),
                  styleWithInlineFile("/META-INF/healthcheck.css")),
              body(
                  h1("DEEPSPACE Health Check"),
                  p(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                  each(tests, HtmlReport::test)))
          .render(writer);
    } catch (IOException e) {
      HealthCheckSubsystem.logger.error("can't write HTML report", e);
    }
  }

  private static Tag test(Test t) {
    return div(hr(), h2("Test: " + t.name), each(t.testCases, HtmlReport::testCase));
  }

  private static Tag testCase(TestCase c) {
    return div(
        h3(c.test.name + ": " + String.format("%4.2f", c.output * 12) + " Volts"),
        p(join(b("Current:"), String.format("%4.2f - %4.2f amps", c.current.low, c.current.high)))
            .withClass("spec"),
        p(join(b("Speed:"), String.format("%6.0f - %6.0f ticks/100 ms", c.speed.low, c.speed.high)))
            .withClass("spec"),
        table(
            thead(tr(th("Talon"), th("Current"), th("Speed"), th("Status"))),
            tbody(each(c.results, HtmlReport::result))));
  }

  private static Tag result(Result r) {
    return tr(
            td(String.valueOf(r.id)).withClass("id"),
            td(String.valueOf(String.format("%4.2f", r.current))).withClass("current"),
            td(String.valueOf(r.velocity)).withClass("speed"),
            td(String.valueOf(r.comment())).withClass("status"))
        .withClass(r.hasPassed() ? "pass" : "fail");
  }
}
