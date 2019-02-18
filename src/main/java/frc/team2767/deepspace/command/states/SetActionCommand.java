package frc.team2767.deepspace.command.states;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class SetActionCommand extends InstantCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

  private Action action;

  public SetActionCommand(Action action) {
    this.action = action;
    requires(BISCUIT);
    requires(VISION);
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    BISCUIT.setCurrentAction(action);
  }
}
