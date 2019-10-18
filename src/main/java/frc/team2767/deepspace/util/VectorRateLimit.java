package frc.team2767.deepspace.util;

public class VectorRateLimit {
  private static final Double CLOSE_ENOUGH = 0.0001;
  private final double rateLimit;

  private double maxFwdStp;
  private double maxStrStp;

  private double curVel;

  private double[] output = {0, 0};

  public VectorRateLimit(double rateLimit) {
    this.rateLimit = rateLimit;
  }

  public double[] apply(double forward, double strafe) {
    curVel = Math.hypot(forward, strafe);

    // Calculate Steps
    maxFwdStp = Math.sin(Math.atan2(forward, strafe));
    maxStrStp = Math.cos(Math.atan2(forward, strafe));

    if (Math.abs(maxFwdStp - 0) < CLOSE_ENOUGH) {
      maxFwdStp = rateLimit * (maxFwdStp - Math.sin(Math.atan2(output[0], output[1])));
    } else maxFwdStp = rateLimit * maxFwdStp;

    if (Math.abs(maxStrStp - 0) < CLOSE_ENOUGH) {
      maxStrStp = rateLimit * (maxStrStp - Math.cos(Math.atan2(output[0], output[1])));
    } else maxStrStp = rateLimit * maxStrStp;

    // Calculate Forward Component
    if (Math.abs(forward - output[0]) > Math.abs(maxFwdStp)) {
      output[0] = output[0] + Math.copySign(maxFwdStp, forward - output[0]);
    } else output[0] = forward;

    // Calculate Strafe Component
    if (Math.abs(strafe - output[1]) > Math.abs(maxStrStp)) {
      output[1] = output[1] + Math.copySign(maxStrStp, strafe - output[1]);
    } else output[1] = strafe;

    return output;
  }
}
