package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class WaitForBeamBreakCommand extends Command {
  private final IntakeSubsystem INTAKE = Robot.INTAKE;

  @Override
  protected boolean isFinished() {
    return INTAKE.isBeamBroken();
  }
}
