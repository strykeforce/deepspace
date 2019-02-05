package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitZero extends InstantCommand {
    BiscuitSubsystem biscuitSubsystem = Robot.BiscuitSubsystem;

    public BiscuitZero(){
        requires(biscuitSubsystem);
    }

    @Override
    protected void initialize() {
        biscuitSubsystem.zero();
    }
}
