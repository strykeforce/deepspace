package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RollerStopCommand extends InstantCommand {

  private static final IntakeSubsystem INTAKE = Robot.INTAKE;

  public RollerStopCommand() {
    requires(INTAKE);
  }

  @Override
  protected void initialize() {
    INTAKE.rollerStop();
  }
}
