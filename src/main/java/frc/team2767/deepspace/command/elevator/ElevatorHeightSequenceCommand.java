package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ElevatorHeightSequenceCommand extends ConditionalCommand {

  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private final double MIN_SAFE_HEIGHT = 22.0;

  public ElevatorHeightSequenceCommand(Command onTrue, Command onFalse) {
    super(onTrue, onFalse);
  }

  @Override
  protected boolean condition() {
    return ELEVATOR.getPosition() > MIN_SAFE_HEIGHT;
  }
}
