package frc.team2767.deepspace.command

import edu.wpi.first.wpilibj.command.CommandGroup

class HealthcheckCommandGroup : CommandGroup() {
    init {
        addSequential(HealthCheckCommand())
        addSequential(VacuumCheckCommand())
    }
}