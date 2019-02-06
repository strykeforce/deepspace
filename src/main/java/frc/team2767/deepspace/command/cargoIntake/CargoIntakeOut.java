package frc.team2767.deepspace.command.cargoIntake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.CargoIntakeSubsystem;

public class CargoIntakeOut extends InstantCommand {
    CargoIntakeSubsystem cargoIntakeSubsystem = Robot.CargoIntakeSubsystem;

    CargoIntakeOut(){
        requires(cargoIntakeSubsystem);
    }

    @Override
    protected void initialize() {
        cargoIntakeSubsystem.rollerOut();
    }
}
