package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.log.SandstormHatchIndicator;
import frc.team2767.deepspace.command.vacuum.ActivateValveCommand;
import frc.team2767.deepspace.command.vacuum.PressureAccumulateCommandGroup;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.WaitForPressureCommand;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class SandstormHatchPickupCommandGroup extends CommandGroup {
  public SandstormHatchPickupCommandGroup() {
    addSequential(new SandstormHatchIndicator(false));
    addSequential(
        new CommandGroup() {
          {
            addSequential(new PressureAccumulateCommandGroup());
            addParallel(new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg));
          }
        });
    addSequential(new ActivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    addSequential(new WaitForPressureCommand(VacuumSubsystem.kHatchPressureInHg));
    addSequential(new SandstormHatchIndicator(true));
    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchLowPositionInches));
  }
}
