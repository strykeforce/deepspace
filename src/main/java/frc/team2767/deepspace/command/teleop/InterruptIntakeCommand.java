package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class InterruptIntakeCommand extends InstantCommand {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;

  public InterruptIntakeCommand() {
    requires(INTAKE);
  }
}
