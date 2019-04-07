package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitLevelOneElevatorCommand extends ConditionalCommand {
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static double elevatorPosition;
  private static double biscuitPosition;

  public BiscuitLevelOneElevatorCommand() {
    super(
        new CommandGroup() {
          {
            addSequential(new LogCommand("Safe Tuck Command -> Move elevator up"));
            addSequential(new ElevatorSetPositionCommand(23.0));
            addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
            addSequential(new ElevatorExecutePlanCommand());
          }
        },
        new BiscuitSafePlaceCommand());
  }

  @Override
  protected boolean condition() {
    elevatorPosition = ELEVATOR.getPosition();
    biscuitPosition = Math.abs(BISCUIT.getPosition());
    logger.info(
        "Tucking Biscuit Up, ElevatorPosition: {}, BiscuitPosition{}",
        elevatorPosition,
        biscuitPosition);
    // If Biscuit is wrapped past 180 and elevator is too low to swing under, reposition elevator
    return (biscuitPosition > 110 && biscuitPosition < 250) && elevatorPosition < 21.0;
  }
}
