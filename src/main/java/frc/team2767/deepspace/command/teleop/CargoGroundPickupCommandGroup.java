package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.intake.RollerInCommand;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class CargoGroundPickupCommandGroup extends CommandGroup {

  public CargoGroundPickupCommandGroup() {
    addSequential(new RollerInCommand());
    addSequential(new IntakePositionCommand(IntakeSubsystem.ShoulderPosition.LOAD));
  }
}
