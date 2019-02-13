package frc.team2767.deepspace.subsystem.safety;

import static frc.team2767.deepspace.subsystem.safety.BiscuitPosition.*;
import static frc.team2767.deepspace.subsystem.safety.ElevatorPosition.*;
import static frc.team2767.deepspace.subsystem.safety.IntakePosition.INTAKE_INTAKE;
import static frc.team2767.deepspace.subsystem.safety.IntakePosition.INTAKE_STOW;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafetySubsystem extends Subsystem {

  private final Limitable biscuitSubsystem;
  private final Limitable intakeSubsystem;
  private final Limitable elevatorSubsystem;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  SafetySubsystem(
      Limitable biscuitSubsystem, Limitable intakeSubsystem, Limitable elevatorSubsystem) {
    this.biscuitSubsystem = biscuitSubsystem;
    this.intakeSubsystem = intakeSubsystem;
    this.elevatorSubsystem = elevatorSubsystem;
  }

  public SafetySubsystem() {
    this(Robot.BISCUIT, Robot.INTAKE, Robot.ELEVATOR);
  }

  @Override
  public void periodic() {
    BiscuitPosition biscuitCurrent = BiscuitPosition.of(biscuitSubsystem.getPosition());
    IntakePosition intakeCurrent = IntakePosition.of(intakeSubsystem.getPosition());
    ElevatorPosition elevatorCurrent = ElevatorPosition.of(elevatorSubsystem.getPosition());

    BiscuitPosition biscuitLimit = biscuitLimit(biscuitCurrent, intakeCurrent, elevatorCurrent);
    IntakePosition intakeLimit = intakeLimit(biscuitCurrent, elevatorCurrent);
    ElevatorPosition elevatorLimit = elevatorLimit(biscuitCurrent, intakeCurrent);

    biscuitSubsystem.setLimits(biscuitLimit.forwardLimit, biscuitLimit.reverseLimit);
    intakeSubsystem.setLimits(intakeLimit.forwardLimit, intakeLimit.reverseLimit);
    elevatorSubsystem.setLimits(elevatorLimit.forwardLimit, elevatorLimit.reverseLimit);
  }

  @NotNull
  private BiscuitPosition biscuitLimit(
      BiscuitPosition biscuitCurrent,
      IntakePosition intakeCurrent,
      ElevatorPosition elevatorCurrent) {
    BiscuitPosition biscuitLimit = null;
    boolean isLeft = biscuitCurrent.isLeft();
    switch (intakeCurrent) {
      case INTAKE_INTAKE:
        switch (elevatorCurrent) {
          case ELEVATOR_4:
          case ELEVATOR_9:
            biscuitLimit = BISCUIT_0;
            break;
          case ELEVATOR_10:
          case ELEVATOR_16:
            biscuitLimit = isLeft ? BISCUIT_90L : BISCUIT_90R;
            break;
          case ELEVATOR_21:
            biscuitLimit = isLeft ? BISCUIT_180L : BISCUIT_180R;
            break;
        }
      case INTAKE_STOW:
        switch (elevatorCurrent) {
          case ELEVATOR_4:
            biscuitLimit = BISCUIT_0;
            break;
          case ELEVATOR_9:
          case ELEVATOR_10:
            biscuitLimit = isLeft ? BISCUIT_90L : BISCUIT_90R;
            break;
          case ELEVATOR_16:
          case ELEVATOR_21:
            biscuitLimit = isLeft ? BISCUIT_180L : BISCUIT_180R;
            break;
        }
        break;
    }
    return biscuitLimit;
  }

  @NotNull
  private IntakePosition intakeLimit(
      BiscuitPosition biscuitPosition, ElevatorPosition elevatorPosition) {
    IntakePosition intakeLimit = null;
    switch (elevatorPosition) {
      case ELEVATOR_4:
      case ELEVATOR_9:
        switch (biscuitPosition) {
          case BISCUIT_0:
            intakeLimit = INTAKE_INTAKE;
            break;
          case BISCUIT_90L:
          case BISCUIT_90R:
          case BISCUIT_90L_120L:
          case BISCUIT_90R_120R:
          case BISCUIT_120L:
          case BISCUIT_120R:
          case BISCUIT_120L_180L:
          case BISCUIT_120R_180R:
          case BISCUIT_180L:
          case BISCUIT_180R:
            intakeLimit = INTAKE_STOW;
            break;
        }
        break;
      case ELEVATOR_10:
      case ELEVATOR_16:
        switch (biscuitPosition) {
          case BISCUIT_0:
          case BISCUIT_90L:
          case BISCUIT_90R:
            intakeLimit = INTAKE_INTAKE;
            break;
          case BISCUIT_90L_120L:
          case BISCUIT_90R_120R:
          case BISCUIT_120L:
          case BISCUIT_120R:
          case BISCUIT_120L_180L:
          case BISCUIT_120R_180R:
          case BISCUIT_180L:
          case BISCUIT_180R:
            intakeLimit = INTAKE_STOW;
            break;
        }
        break;
      case ELEVATOR_21:
        intakeLimit = INTAKE_INTAKE;
        break;
    }
    return intakeLimit;
  }

  @NotNull
  private ElevatorPosition elevatorLimit(
      BiscuitPosition biscuitPosition, IntakePosition intakePosition) {
    ElevatorPosition elevatorPosition = null;
    switch (intakePosition) {
      case INTAKE_INTAKE:
        switch (biscuitPosition) {
          case BISCUIT_0:
          case BISCUIT_90L:
          case BISCUIT_90R:
            elevatorPosition = ELEVATOR_10;
            break;
          case BISCUIT_90L_120L:
          case BISCUIT_90R_120R:
          case BISCUIT_120L:
          case BISCUIT_120R:
          case BISCUIT_120L_180L:
          case BISCUIT_120R_180R:
          case BISCUIT_180L:
            elevatorPosition = ELEVATOR_21;
            break;
        }
        break;
      case INTAKE_STOW:
        switch (biscuitPosition) {
          case BISCUIT_0:
            elevatorPosition = ELEVATOR_4;
            break;
          case BISCUIT_90L:
          case BISCUIT_90R:
            elevatorPosition = ELEVATOR_9;
            break;
          case BISCUIT_90L_120L:
          case BISCUIT_90R_120R:
          case BISCUIT_120L:
          case BISCUIT_120R:
          case BISCUIT_120L_180L:
          case BISCUIT_120R_180R:
          case BISCUIT_180L:
            elevatorPosition = ELEVATOR_9;
            break;
        }
        break;
    }
    return elevatorPosition;
  }

  @Override
  protected void initDefaultCommand() {}
}
