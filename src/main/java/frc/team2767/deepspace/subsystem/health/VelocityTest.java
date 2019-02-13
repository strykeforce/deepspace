package frc.team2767.deepspace.subsystem.health;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.slf4j.Logger;

public class VelocityTest extends Test {

  static final Logger logger = HealthCheckSubsystem.logger;
  protected final TalonSRX[] talons;

  public VelocityTest(String name, TalonSRX[] talons) {
    super(name);
    this.talons = talons;
  }

  @Override
  public Boolean call() throws Exception {
    logger.info("running {}", name);

    for (TestCase tc : testCases) {
      for (TalonSRX talon : talons) {
        Result result = new Result(talon.getDeviceID(), tc);
        tc.results.add(result);

        logger.debug(
            "setting talon {} to output {} for {} ms", talon.getDeviceID(), tc.output, tc.duration);
        try {
          talon.set(ControlMode.PercentOutput, tc.output);

          // come up to speed
          long start = System.nanoTime();
          while (System.nanoTime() - start < warmup * 1e6) {
            Thread.sleep(Math.max(10, warmup / 100));
          }

          // run test
          runTest(tc, talon, result);

        } finally {
          talon.set(ControlMode.PercentOutput, 0);
        }
        result.velocity /= iterations;
        result.current /= iterations;
      }
    }
    return true;
  }

  void runTest(TestCase tc, TalonSRX talon, Result result) throws InterruptedException {
    for (int i = 0; i < iterations; i++) {
      result.velocity += talon.getSelectedSensorVelocity(0);
      result.current += talon.getOutputCurrent();
      Thread.sleep(tc.duration / iterations);
    }
  }
}
