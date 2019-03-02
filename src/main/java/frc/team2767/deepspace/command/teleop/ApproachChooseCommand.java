package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.Controls;

public class ApproachChooseCommand extends ConditionalCommand {

  private static final Controls CONTROLS = Robot.CONTROLS;

  public ApproachChooseCommand(Command onTrue, Command onFalse) {
    super(onTrue, onFalse);
  }

  @Override
  protected boolean condition() {
    return CONTROLS.getDriverControls().getToggle();
  }
}
