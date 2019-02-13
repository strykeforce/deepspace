package frc.team2767.deepspace.subsystem.health;

class Result {
  TestCase testCase;
  int id;
  double current;
  int velocity;

  public Result(int id, TestCase testCase) {
    this.testCase = testCase;
    this.id = id;
  }

  boolean hasPassed() {
    return testCase.hasPassed(current, velocity);
  }

  String comment() {
    return testCase.passFailString(current, velocity);
  }
}
