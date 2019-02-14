package frc.team2767.deepspace.subsystem.safety;

enum IntakePosition {
  INTAKE_INTAKE(15100, 0),
  INTAKE_STOW(7700, 0);

  public final int forwardLimit;
  public final int reverseLimit;

  IntakePosition(int forwardLimit, int reverseLimit) {
    this.forwardLimit = forwardLimit;
    this.reverseLimit = reverseLimit;
  }

  public static IntakePosition of(int position) {
    return INTAKE_STOW;
  }

  @Override
  public String toString() {
    return "forwardLimit=" + forwardLimit + ", reverseLimit=" + reverseLimit;
  }
}
