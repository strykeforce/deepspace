package frc.team2767.deepspace.command.Elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class PositionCommand extends InstantCommand{

    private final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
    private int position;

    public PositionCommand(int position) {

        requires(ELEVATOR);
    }

    @Override
    protected void initialize() { ELEVATOR.setPosition(position);}

    @Override
    protected void execute() { ELEVATOR.adjustVelocity(); }

    @Override
    protected void end() { ELEVATOR.onTarget(); }
}
