package frc.team2767.deepspace.subsystem.safety;

enum IntakePosition {
  INTAKE_INTAKE(9664, 0),
  INTAKE_STOW(3224, 0);

  public final int forwardLimit;
  public final int reverseLimit;

  IntakePosition(int forwardLimit, int reverseLimit) {
    this.forwardLimit = forwardLimit;
    this.reverseLimit = reverseLimit;
  }

  public static IntakePosition of(int position) {
    if (position > 3224) {
      return INTAKE_INTAKE;
    }

    return INTAKE_STOW;
  }

  @Override
  public String toString() {
    return "Intake: forwardLimit=" + forwardLimit + ", reverseLimit=" + reverseLimit;
  }
}
