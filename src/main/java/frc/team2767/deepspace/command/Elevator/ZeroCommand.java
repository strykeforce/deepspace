package frc.team2767.deepspace.command.Elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ZeroCommand extends InstantCommand{

    private final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

    public ZeroCommand() {requires(ELEVATOR);}

    @Override
    protected void initialize() { ELEVATOR.positionToZero();}

    @Override
    protected boolean isFinished() { return ELEVATOR.onZero() || isTimedOut(); }

    @Override
    protected void end() { ELEVATOR.zeroPosition(); }
}
