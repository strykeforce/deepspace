package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class WaitForIntakeBeamCommand extends Command {
  private boolean hasBroken;
  private final IntakeSubsystem INTAKE = Robot.INTAKE;

  @Override
  protected void initialize() {
    hasBroken = false;
  }

  @Override
  protected void execute() {
    if (INTAKE.isIntakeSlowBeamBroken() && !hasBroken) {
      hasBroken = true;
      INTAKE.rollerOpenLoop(0.50);
    }
  }

  @Override
  protected boolean isFinished() {
    return hasBroken && !INTAKE.isIntakeSlowBeamBroken();
  }

  @Override
  protected void end() {
    INTAKE.rollerStop();
  }
}
