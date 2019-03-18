package frc.team2767.deepspace.subsystem.safety;

import static frc.team2767.deepspace.subsystem.safety.BiscuitPosition.*;
import static frc.team2767.deepspace.subsystem.safety.ElevatorPosition.*;
import static frc.team2767.deepspace.subsystem.safety.IntakePosition.INTAKE_INTAKE;
import static frc.team2767.deepspace.subsystem.safety.IntakePosition.INTAKE_STOW;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import org.jetbrains.annotations.NotNull;

public class SafetySubsystem extends Subsystem {
  private final Limitable biscuitSubsystem;
  private final Limitable intakeSubsystem;
  private final Limitable elevatorSubsystem;
  private BiscuitPosition biscuitLimit;
  private IntakePosition intakeLimit;
  private ElevatorPosition elevatorLimit;
  private BiscuitPosition biscuitCurrent;
  private IntakePosition intakeCurrent;
  private ElevatorPosition elevatorCurrent;

  public SafetySubsystem() {
    this(Robot.BISCUIT, Robot.INTAKE, Robot.ELEVATOR);
  }

  SafetySubsystem(
      Limitable biscuitSubsystem, Limitable intakeSubsystem, Limitable elevatorSubsystem) {
    this.biscuitSubsystem = biscuitSubsystem;
    this.intakeSubsystem = intakeSubsystem;
    this.elevatorSubsystem = elevatorSubsystem;
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void periodic() {

    biscuitCurrent = BiscuitPosition.of(biscuitSubsystem.getTicks());
    intakeCurrent = IntakePosition.of(intakeSubsystem.getTicks());
    elevatorCurrent = ElevatorPosition.of(elevatorSubsystem.getTicks());

    biscuitLimit = biscuitLimit(biscuitCurrent, intakeCurrent, elevatorCurrent);
    intakeLimit = intakeLimit(biscuitCurrent, elevatorCurrent);
    elevatorLimit = elevatorLimit(biscuitCurrent, intakeCurrent);

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
          case ELEVATOR_4: // fall-through
          case ELEVATOR_9:
            biscuitLimit = BISCUIT_0;
            break;
          case ELEVATOR_10: // fall-through
          case ELEVATOR_16:
            biscuitLimit = isLeft ? BISCUIT_90L : BISCUIT_90R;
            break;
          case ELEVATOR_21:
            biscuitLimit = BISCUIT_360;
            break;
        }
      case INTAKE_STOW:
        switch (elevatorCurrent) {
          case ELEVATOR_4:
            biscuitLimit = BISCUIT_0;
            break;
          case ELEVATOR_9: // fall-through
          case ELEVATOR_10: // fall-through
          case ELEVATOR_16:
            if (biscuitCurrent == BISCUIT_180L
                || biscuitCurrent == BISCUIT_180R
                || biscuitCurrent == BISCUIT_120L_180L
                || biscuitCurrent == BISCUIT_120R_180R) {
              biscuitLimit = isLeft ? BISCUIT_180L : BISCUIT_180R;
            } else {
              biscuitLimit = isLeft ? BISCUIT_90L : BISCUIT_90R;
            }
            break;
          case ELEVATOR_21:
            biscuitLimit = BISCUIT_360;
            break;
        }
        break;
    }
    return biscuitLimit;
  }

  @SuppressWarnings("Duplicates")
  private IntakePosition intakeLimit(
      BiscuitPosition biscuitPosition, ElevatorPosition elevatorPosition) {
    IntakePosition intakeLimit = null;
    switch (elevatorPosition) {
      case ELEVATOR_4:
      case ELEVATOR_9:
        switch (biscuitPosition) {
          case BISCUIT_0: // fall-through
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
          case BISCUIT_360:
            intakeLimit = INTAKE_STOW;
            break;
        }
        break;
      case ELEVATOR_10: // fall-through
      case ELEVATOR_16:
        switch (biscuitPosition) {
          case BISCUIT_0: // fall-through
          case BISCUIT_90L:
          case BISCUIT_90R:
            intakeLimit = INTAKE_INTAKE;
            break;
          case BISCUIT_90L_120L: // fall-through
          case BISCUIT_90R_120R:
          case BISCUIT_120L:
          case BISCUIT_120R:
          case BISCUIT_120L_180L:
          case BISCUIT_120R_180R:
          case BISCUIT_180L:
          case BISCUIT_180R:
          case BISCUIT_360:
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

  @SuppressWarnings("Duplicates")
  private ElevatorPosition elevatorLimit(
      BiscuitPosition biscuitPosition, IntakePosition intakePosition) {
    ElevatorPosition elevatorPosition = null;
    switch (intakePosition) {
      case INTAKE_INTAKE:
        switch (biscuitPosition) {
          case BISCUIT_0: // fall-through
          case BISCUIT_90L:
          case BISCUIT_90R:
            elevatorPosition = ELEVATOR_10;
            break;
          case BISCUIT_90L_120L: // fall-through
          case BISCUIT_90R_120R:
          case BISCUIT_120L:
          case BISCUIT_120R:
          case BISCUIT_120L_180L:
          case BISCUIT_120R_180R:
          case BISCUIT_180L:
          case BISCUIT_180R:
          case BISCUIT_360:
            elevatorPosition = ELEVATOR_21;
            break;
        }
        break;
      case INTAKE_STOW:
        switch (biscuitPosition) {
          case BISCUIT_0:
            elevatorPosition = ELEVATOR_4;
            break;
          case BISCUIT_90L: // fall-through
          case BISCUIT_90R:
            elevatorPosition = ELEVATOR_9;
            break;
          case BISCUIT_180L: // fall-through
          case BISCUIT_180R:
            elevatorPosition = ELEVATOR_16;
            break;
          case BISCUIT_90L_120L: // fall-through
          case BISCUIT_90R_120R:
          case BISCUIT_120L_180L:
          case BISCUIT_120R_180R:
          case BISCUIT_120L:
          case BISCUIT_120R:
          case BISCUIT_360:
            elevatorPosition = ELEVATOR_21;
            break;
        }
        break;
    }
    return elevatorPosition;
  }

  @Override
  public String toString() {

    return "current="
        + "\n\t"
        + elevatorCurrent
        + "\t"
        + elevatorSubsystem.getTicks()
        + "\n\t"
        + intakeCurrent
        + "\t"
        + intakeSubsystem.getTicks()
        + "\n\t"
        + biscuitCurrent
        + "\t"
        + biscuitSubsystem.getTicks()
        + "\nlimits="
        + "\n\t"
        + elevatorLimit
        + "\t"
        + elevatorLimit.toString()
        + "\n\t"
        + intakeLimit
        + "\t"
        + intakeLimit.toString()
        + "\n\t"
        + biscuitLimit
        + "\t"
        + biscuitLimit.toString();
  }
}
