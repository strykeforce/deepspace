package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitPlanCommand extends InstantCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private BiscuitSubsystem.FieldDirections direction;

  public BiscuitPlanCommand(BiscuitSubsystem.FieldDirections direction) {
    this.direction = direction;
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    BISCUIT.plannedDirection = direction;
  }
}
