package frc.team2767.deepspace.subsystem.health;

class Range {
  final double low, high;

  Range(double low, double high) {
    this.low = low;
    this.high = high;
  }

  boolean inRange(double val) {
    return val >= low && val <= high;
  }
}
