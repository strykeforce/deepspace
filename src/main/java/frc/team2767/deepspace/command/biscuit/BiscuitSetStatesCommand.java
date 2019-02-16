package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorLevel;
import frc.team2767.deepspace.subsystem.GamePiece;

public class BiscuitSetStatesCommand extends InstantCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  private ElevatorLevel level;
  private GamePiece gamePiece;
  private Action action;

  public BiscuitSetStatesCommand(GamePiece gamePiece) {
    this(null, gamePiece, null);
  }

  public BiscuitSetStatesCommand(ElevatorLevel level, GamePiece gamePiece, Action action) {
    this.level = level;
    this.gamePiece = gamePiece;
    this.action = action;
  }

  public BiscuitSetStatesCommand(Action action) {
    this(null, null, action);
  }

  public BiscuitSetStatesCommand(ElevatorLevel level) {
    this(level, null, null);
  }

  public BiscuitSetStatesCommand(ElevatorLevel level, Action action) {
    this(level, null, action);
  }

  public BiscuitSetStatesCommand(GamePiece gamePiece, Action action) {
    this(null, gamePiece, action);
  }

  public BiscuitSetStatesCommand(ElevatorLevel level, GamePiece gamePiece) {
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
