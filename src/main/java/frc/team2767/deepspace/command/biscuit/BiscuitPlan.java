package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitPlan extends InstantCommand {
  BiscuitSubsystem biscuitSubsystem = Robot.BiscuitSubsystem;
  BiscuitSubsystem.FieldDirections direction;

  public BiscuitPlan(BiscuitSubsystem.FieldDirections direction) {
    this.direction = direction;
    requires(biscuitSubsystem);
  }

  @Override
  protected void initialize() {
    biscuitSubsystem.plannedDirection = direction;
  }
}
