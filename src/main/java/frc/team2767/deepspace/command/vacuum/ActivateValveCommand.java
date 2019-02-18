package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class ActivateValveCommand extends InstantCommand {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private VacuumSubsystem.Valve[] valves;

  public ActivateValveCommand(VacuumSubsystem.Valve valve) {
    this(new VacuumSubsystem.Valve[] {valve});
  }

  public ActivateValveCommand(VacuumSubsystem.Valve[] valves) {
    this.valves = valves;
  }

  @Override
  protected void initialize() {
    for (VacuumSubsystem.Valve v : valves) {
      VACUUM.setSolenoid(v, true);
    }
  }
}
