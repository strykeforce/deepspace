package frc.team2767.deepspace.subsystem.health;

import java.util.ArrayList;
import java.util.List;

public class TestCase {

  private static final long DEFAULT_DURATION_MS = 2000;

  final Test test;
  double output;
  long duration = DEFAULT_DURATION_MS;
  Range current;
  Range speed;
  List<Result> results = new ArrayList<>();

  TestCase(Test test) {
    this.test = test;
  }

  public void setOutput(double output) {
    this.output = output;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public void setCurrentRange(double low, double high) {
    current = new Range(low, high);
  }

  public void setSpeedRange(double low, double high) {
    speed = new Range(low, high);
  }

  String passFailString(double current, int speed) {
    return hasPassed(current, speed) ? "PASS" : "FAIL";
  }

  boolean hasPassed(double current, int speed) {
    return hasCurrentPassed(current) && hasSpeedPassed(speed);
  }

  private boolean hasCurrentPassed(double current) {
    return this.current.inRange(current);
  }

  private boolean hasSpeedPassed(int speed) {
    return this.speed.inRange(speed);
  }
}
