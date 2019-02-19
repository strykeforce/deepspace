package frc.team2767.deepspace.command.coconut;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.CoconutSubsystem;

public class CoconutCloseCommand extends InstantCommand {
  private final CoconutSubsystem COCONUT = Robot.COCONUT;

  public CoconutCloseCommand() {
    requires(COCONUT);
  }

  @Override
  protected void initialize() {
    COCONUT.close();
  }
}
