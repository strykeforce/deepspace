package frc.team2767.deepspace.subsystem.safety;

enum ElevatorPosition {
  ELEVATOR_4(3270, 41),
  ELEVATOR_9(3270, 1580),
  ELEVATOR_10(3270, 2000),
  ELEVATOR_16(3270, 5250),
  ELEVATOR_21(3270, 7800);

  public final int forwardLimit;
  public final int reverseLimit;

  ElevatorPosition(int forwardLimit, int reverseLimit) {
    this.forwardLimit = forwardLimit;
    this.reverseLimit = reverseLimit;
  }

  public static ElevatorPosition of(int position) {
    if (position > 7800) {
      return ELEVATOR_21;
    }

    if (position > 5250) {
      return ELEVATOR_16;
    }

    if (position > 2000) {
      return ELEVATOR_10;
    }

    if (position > 1580) {
      return ELEVATOR_9;
    }

    // else return most conservative case
    return ELEVATOR_4;
  }

  @Override
  public String toString() {
    return "forwardLimit=" + forwardLimit + ", reverseLimit=" + reverseLimit;
  }
}
