package frc.team2767.deepspace.command.cargoIntake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.CargoIntakeSubsystem;

public class CargoIntakeUp extends InstantCommand {
    CargoIntakeSubsystem cargoIntakeSubsystem = Robot.CargoIntakeSubsystem;

    CargoIntakeUp(){
        requires(cargoIntakeSubsystem);
    }

    @Override
    protected void initialize() {
        cargoIntakeSubsystem.setPosition(CargoIntakeSubsystem.IntakePosition.UP);
    }
}
