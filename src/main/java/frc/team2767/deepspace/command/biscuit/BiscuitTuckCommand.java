package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.GamePiece;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitTuckCommand extends ConditionalCommand {
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private static final VisionSubsystem VISION = Robot.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static double elevatorPosition;
  private static GamePiece gamePiece;

  public BiscuitTuckCommand() {
    super(
        new BiscuitSetPositionCommand(BiscuitSubsystem.kDownPosition),
        new BiscuitSafeTuckUpCommand());
  }

  @Override
  protected boolean condition() {
    gamePiece = VISION.gamePiece;
    elevatorPosition = ELEVATOR.getPosition();
    // If have cargo and are high enough to go down, go down
    logger.info("Tuck Command, Gamepiece: {}, ElevatorPosition: {}", gamePiece, elevatorPosition);
    return (gamePiece == GamePiece.CARGO && elevatorPosition >= 20.0);
  }
}
