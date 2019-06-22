package frc.team2767.deepspace.command.approach.sandstorm;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.approach.PathCommand;

public class ChooseStartPathCommand extends ConditionalCommand {

  public ChooseStartPathCommand() {
    super(
        new ChooseAutonFieldSideCommand(
            new PathCommand("hab_to_cargo_l", 90.0, false), // 90.0
            new PathCommand("hab_to_cargo_r", 90.0, false)),
        new ChooseAutonFieldSideCommand(
            new PathCommand("hab2_to_cargo_l", 90.0, false), // 90.0
            new PathCommand("hab2_to_cargo_r", 90.0, false)));
  }

  @Override
  protected boolean condition() {
    return Robot.startLevel == Robot.StartLevel.ONE;
  }
}
