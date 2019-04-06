package frc.team2767.deepspace.subsystem.safety;

enum BiscuitPosition {
  // giving set locations a +/- 100 range
  BISCUIT_0(1000, -1000),
  BISCUIT_90L(3072, -3072),
  BISCUIT_90R(3072, -3072),
  BISCUIT_90L_120L(3996, 3072),
  BISCUIT_90R_120R(-3072, -3996),
  BISCUIT_120L(4196, -4196),
  BISCUIT_120R(4196, -4196),
  BISCUIT_120L_180L(6044, 4196),
  BISCUIT_120R_180R(-4196, -6044),
  BISCUIT_180L(6244, 6044),
  BISCUIT_180R(-6044, -6644),
  BISCUIT_270L(),
  BISCUIT_270R(),
  BISCUIT_360(6244, -6244);

  public final int forwardLimit;
  public final int reverseLimit;

  BiscuitPosition(int forwardLimit, int reverseLimit) {
    this.forwardLimit = forwardLimit;
    this.reverseLimit = reverseLimit;
  }

  public static BiscuitPosition of(int position) {
    if (-1000 <= position && position <= 1000) {
      return BISCUIT_0;
    }

    if (position > 0) {
      if (position > _____){
        return BISCUIT_270L;
      }
      if (position > 6044) {
        return BISCUIT_180L;
      }

      if (position > 4196) {
        return BISCUIT_120L_180L;
      }

      if (position > 3996) {
        return BISCUIT_120L;
      }

      if (position > 3400) {
        return BISCUIT_90L_120L;
      }

      return BISCUIT_90L;
    }

    if (position < ____){
      return BISCUIT_270R;
    }

    if (position < -6044) {
      return BISCUIT_180R;
    }

    if (position < -4196) {
      return BISCUIT_120R_180R;
    }

    if (position < -3966) {
      return BISCUIT_120R;
    }

    if (position < -3400) {
      return BISCUIT_90R_120R;
    }

    return BISCUIT_90R;
  }

  public boolean isLeft() {
    return this == BiscuitPosition.BISCUIT_90L
        || this == BiscuitPosition.BISCUIT_90L_120L
        || this == BiscuitPosition.BISCUIT_120L
        || this == BiscuitPosition.BISCUIT_120L_180L
        || this == BiscuitPosition.BISCUIT_180L
        || this == BiscuitPosition.BISCUIT_270L ;
  }

  public boolean isWrapped(){
    return this == BiscuitPosition.BISCUIT_270L
            || this == BiscuitPosition.BISCUIT_270R;
  }

  @Override
  public String toString() {
    return "Biscuit: forwardLimit=" + forwardLimit + ", reverseLimit=" + reverseLimit;
  }
}
