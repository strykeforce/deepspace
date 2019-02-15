package frc.team2767.deepspace.subsystem.safety;

enum ElevatorPosition {
  ELEVATOR_4(32000, 0),
  ELEVATOR_9(32000, 1725),
  ELEVATOR_10(32000, 2250),
  ELEVATOR_16(32000, 5090),
  ELEVATOR_21(32000, 7800);

  public final int forwardLimit;
  public final int reverseLimit;

  ElevatorPosition(int forwardLimit, int reverseLimit) {
    this.forwardLimit = forwardLimit;
    this.reverseLimit = reverseLimit;
  }

  public static ElevatorPosition of(int position) {
    if (position > 17700) {
      return ELEVATOR_21;
    }

    if (position > 11799) {
      return ELEVATOR_16;
    }

    if (position > 6710) {
      return ELEVATOR_10;
    }

    if (position > 4277) {
      return ELEVATOR_9;
    }

    // else return most conservative case
    return ELEVATOR_4;
  }

  @Override
  public String toString() {
    return "elevator: forwardLimit=" + forwardLimit + ", reverseLimit=" + reverseLimit;
  }
}
