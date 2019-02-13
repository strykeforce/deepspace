package frc.team2767.deepspace.subsystem.safety;

enum ElevatorPosition {
  ELEVATOR_4(0, 0),
  ELEVATOR_9(0, 0),
  ELEVATOR_10(0, 0),
  ELEVATOR_16(0, 0),
  ELEVATOR_21(0, 0);

  public final int forwardLimit;
  public final int reverseLimit;

  ElevatorPosition(int forwardLimit, int reverseLimit) {
    this.forwardLimit = forwardLimit;
    this.reverseLimit = reverseLimit;
  }

  public static ElevatorPosition of(int position) {
    return ELEVATOR_4;
  }
}
