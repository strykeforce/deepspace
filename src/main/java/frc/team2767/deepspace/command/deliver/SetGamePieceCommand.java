package frc.team2767.deepspace.command.deliver;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.GamePiece;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class SetGamePieceCommand extends InstantCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

  private GamePiece gamePiece;

  public SetGamePieceCommand(GamePiece gamePiece) {
    this.gamePiece = gamePiece;
    requires(BISCUIT);
    requires(VISION);
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    BISCUIT.setCurrentGamePiece(gamePiece);
    ELEVATOR.setCurrentGamepiece(gamePiece);
  }
}
