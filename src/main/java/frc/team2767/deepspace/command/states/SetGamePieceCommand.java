package frc.team2767.deepspace.command.states;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.GamePiece;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class SetGamePieceCommand extends InstantCommand {
  private static final VisionSubsystem VISION = Robot.VISION;

  private GamePiece gamePiece;

  public SetGamePieceCommand(GamePiece gamePiece) {
    this.gamePiece = gamePiece;
  }

  @Override
  protected void initialize() {
    VISION.setGamePiece(gamePiece);
  }
}
