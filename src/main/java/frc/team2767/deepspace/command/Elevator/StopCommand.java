package frc.team2767.deepspace.command.Elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class StopCommand extends InstantCommand{

    private final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

    public StopCommand() {requires(ELEVATOR);}

    @Override
    protected void initialize() { ELEVATOR.stop();}
}
