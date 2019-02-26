package frc.team2767.deepspace.command.states;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class SetActionCommand extends InstantCommand {

  private static final VisionSubsystem VISION = Robot.VISION;

  private Action action;

  public SetActionCommand(Action action) {
    this.action = action;
  }

  @Override
  protected void initialize() {
    VISION.setAction(action);
  }
}
