package frc.team2767.deepspace.subsystem.safety;

enum BiscuitPosition {
  BISCUIT_0(0, 0),
  BISCUIT_90L(3000, -3000),
  BISCUIT_90R(3000, -3000),
  BISCUIT_90L_120L(0, 0),
  BISCUIT_90R_120R(0, 0),
  BISCUIT_120L(0, 0),
  BISCUIT_120R(0, 0),
  BISCUIT_120L_180L(0, 0),
  BISCUIT_120R_180R(0, 0),
  BISCUIT_180L(0, 0),
  BISCUIT_180R(0, 0);

  public final int forwardLimit;
  public final int reverseLimit;

  BiscuitPosition(int forwardLimit, int reverseLimit) {
    this.forwardLimit = forwardLimit;
    this.reverseLimit = reverseLimit;
  }

  public static BiscuitPosition of(int position) {
    return BISCUIT_0;
  }

  public boolean isLeft() {
    return this == BiscuitPosition.BISCUIT_90L
        || this == BiscuitPosition.BISCUIT_90L_120L
        || this == BiscuitPosition.BISCUIT_120L
        || this == BiscuitPosition.BISCUIT_120L_180L
        || this == BiscuitPosition.BISCUIT_180L;
  }
}
