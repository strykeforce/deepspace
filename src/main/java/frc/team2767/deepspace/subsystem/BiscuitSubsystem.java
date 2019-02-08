package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class BiscuitSubsystem extends Subsystem {
  // FIXME get from preferences
  private final int CLOSE_ENOUGH = 0; // FIXME
  private final int UP_POS = 3513;
  private final int DOWN_POS = 0; // FIXME
  private final int LEFT_POS = 0; // FIXME
  private final int RIGHT_POS = 0; // FIXME
  private final int BISCUIT_ID = 40;
  private final int NUM_ROTATIONS = 2; // FIXME
  private final int TICKS_PER_REV = 12300;
  private final int LOW_ENCODER_LIMIT = -8800; // FIXME
  private final int HIGH_ENCODER_LIMIT = 8800; // FIXME
  int zero = 0;

  TalonSRX biscuit = new TalonSRX(BISCUIT_ID);
  TalonSRXConfiguration biscuitConfig = new TalonSRXConfiguration();

  TelemetryService telemetryService = Robot.TELEMETRY;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public BiscuitSubsystem() {
    telemetryService.register(biscuit);
    biscuitConfig.forwardSoftLimitThreshold = HIGH_ENCODER_LIMIT;
    biscuitConfig.reverseSoftLimitThreshold = LOW_ENCODER_LIMIT;
    biscuitConfig.forwardSoftLimitEnable = true;
    biscuitConfig.reverseSoftLimitEnable = true;
    biscuit.configAllSettings(biscuitConfig);
  }

  @Override
  protected void initDefaultCommand() {}

  public void zero() {
    zero = biscuit.getSensorCollection().getPulseWidthPosition() - UP_POS;
    biscuit.setSelectedSensorPosition(zero);
  }

  public int getPosition() {
    if (biscuit.getSelectedSensorPosition() >= 0) {
      return biscuit.getSelectedSensorPosition() % TICKS_PER_REV;
    } else {
      return biscuit.getSelectedSensorPosition() % -TICKS_PER_REV;
    }
  }

  int getEncoderValue(Position position) {
    switch (position) {
      case UP:
        return UP_POS;
      case DOWN:
        return DOWN_POS;
      case LEFT:
        return LEFT_POS;
      case RIGHT:
        return RIGHT_POS;
      default:
        logger.warn("Invalid biscuit position");
        return 0;
    }
  }

  int findNearest(int basePosition) {
    int nearest = 0;
    for (int k = -NUM_ROTATIONS; k <= NUM_ROTATIONS; k++) {
      int loopedPosition = basePosition + k * TICKS_PER_REV;
      if (Math.abs(biscuit.getSelectedSensorPosition() - loopedPosition) < nearest
          && LOW_ENCODER_LIMIT < loopedPosition
          && HIGH_ENCODER_LIMIT > loopedPosition) {
        nearest = loopedPosition;
      }
    }
    return nearest;
  }

  public void setPosition(Position position) {
    int target = findNearest(getEncoderValue(position));
    biscuit.set(ControlMode.Position, target);
  }

  public void runOpenLoop(double power) {
    biscuit.set(ControlMode.PercentOutput, power);
  }

  public boolean positionReached(Position position) {
    if (Math.abs(biscuit.getSelectedSensorPosition() - getEncoderValue(position)) < CLOSE_ENOUGH) {
      return true;
    } else {
      return false;
    }
  }

  public void stop() {
    biscuit.set(ControlMode.PercentOutput, 0);
  }

  public enum Position {
    UP,
    DOWN,
    LEFT,
    RIGHT
  }
}
