package frc.team2767.deepspace.subsystem.safety;

enum IntakePosition {
  INTAKE_INTAKE(12_081, -250),
  INTAKE_STOW(3_932, -250);

  public final int forwardLimit;
  public final int reverseLimit;

  IntakePosition(int forwardLimit, int reverseLimit) {
    this.forwardLimit = forwardLimit;
    this.reverseLimit = reverseLimit;
  }

  public static IntakePosition of(int position) {
    if (position > 3932) {
      return INTAKE_INTAKE;
    }

    return INTAKE_STOW;
  }

  @Override
  public String toString() {
    return "Intake: forwardLimit=" + forwardLimit + ", reverseLimit=" + reverseLimit;
  }
}
