package frc.team2767.deepspace.subsystem.safety;

enum ElevatorPosition {
  ELEVATOR_4(32000, 0),
  ELEVATOR_9(32000, 4900),
  ELEVATOR_10(32000, 7690),
  ELEVATOR_16(32000, 11799),
  ELEVATOR_21(32000, 17700);

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

    if (position > 7690) {
      return ELEVATOR_10;
    }

    if (position > 4900) {
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
