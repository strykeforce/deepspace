package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitSetStatesCommand extends InstantCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  private BiscuitSubsystem.Level level;
  private BiscuitSubsystem.GamePiece gamePiece;
  private BiscuitSubsystem.Action action;

  public BiscuitSetStatesCommand(BiscuitSubsystem.GamePiece gamePiece) {
    this(null, gamePiece, null);
  }

  public BiscuitSetStatesCommand(
      BiscuitSubsystem.Level level,
      BiscuitSubsystem.GamePiece gamePiece,
      BiscuitSubsystem.Action action) {
    this.level = level;
    this.gamePiece = gamePiece;
    this.action = action;
  }

  public BiscuitSetStatesCommand(BiscuitSubsystem.Action action) {
    this(null, null, action);
  }

  public BiscuitSetStatesCommand(BiscuitSubsystem.Level level) {
    this(level, null, null);
  }

  public BiscuitSetStatesCommand(BiscuitSubsystem.Level level, BiscuitSubsystem.Action action) {
    this(level, null, action);
  }

  public BiscuitSetStatesCommand(
      BiscuitSubsystem.GamePiece gamePiece, BiscuitSubsystem.Action action) {
    this(null, gamePiece, action);
  }

  public BiscuitSetStatesCommand(
      BiscuitSubsystem.Level level, BiscuitSubsystem.GamePiece gamePiece) {
    this(level, gamePiece, null);
  }

  @Override
  protected void initialize() {
    if (level != null) {
      BISCUIT.setTargetLevel(level);
    }

    if (gamePiece != null) {
      BISCUIT.setCurrentGamePiece(gamePiece);
    }

    if (action != null) {
      BISCUIT.setCurrentAction(action);
    }
  }
}
