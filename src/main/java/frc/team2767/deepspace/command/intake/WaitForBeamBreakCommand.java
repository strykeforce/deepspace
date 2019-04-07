package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class WaitForBeamBreakCommand extends Command {
  private final IntakeSubsystem INTAKE = Robot.INTAKE;
  private static boolean hasBroken;
  private static double breakTime;
  private static double currentTime;
  private static double WAIT_TIME_MS = 200;

  @Override
  protected void initialize() {
    hasBroken = false;
  }

  @Override
  protected void execute() {
    if (INTAKE.isBeamBroken() && !hasBroken) {
      hasBroken = true;
      breakTime = System.currentTimeMillis();
    }
  }

  @Override
  protected boolean isFinished() {
    currentTime = System.currentTimeMillis();
    return hasBroken && (currentTime - breakTime > WAIT_TIME_MS);
  }

  @Override
  protected void end() {
    INTAKE.rollerStop();
  }
}
