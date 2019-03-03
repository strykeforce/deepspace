package frc.team2767.deepspace.command

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.command.Command
import frc.team2767.deepspace.Robot
import frc.team2767.deepspace.health.HealthCheck
import frc.team2767.deepspace.health.healthCheck
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class HealthCheckCommand : Command() {

    init {
        requires(Robot.BISCUIT)
        requires(Robot.DRIVE)
        requires(Robot.ELEVATOR)
        requires(Robot.INTAKE)
    }

    private lateinit var healthCheck: HealthCheck

    override fun initialize() {
        healthCheck = healthCheck {

            talonCheck {
                talons = Robot.BISCUIT.talons

                positionTest {
                    percentOutput = 0.1
                    encoderTarget = 1250
                    encoderTimeOutCount = 500
                }

                positionTest {
                    percentOutput = -0.1
                    encoderTarget = -1250
                    encoderTimeOutCount = 250
                }
            }


//            talonCheck {
//                name = "Swerve Azimuth Talons"
//                talons = Robot.DRIVE.allWheels.map { it.azimuthTalon }
//
//                timedTest {
//                    percentOutput = 0.25
//                    currentRange = 0.0..0.5
//                    speedRange = 500..600
//                }
//
//                timedTest {
//                    percentOutput = -0.25
//                    currentRange = 0.0..0.5
//                    speedRange = 500..600
//                }
//
//                timedTest {
//                    percentOutput = -0.5
//                    currentRange = 0.0..0.5
//                    speedRange = 500..600
//                }
//
//                timedTest {
//                    warmUp = 0.5
//                    duration = 2.0
//                    percentOutput = 0.75
//                    currentRange = 0.6..1.2
//                    speedRange = 1000..1100
//                }
//
//            }
//
//            talonCheck {
//                name = "Swerve Drive Talons"
//                talons = Robot.DRIVE.allWheels.map { it.driveTalon }
//
//                timedTest {
//                    percentOutput = 0.25
//                    currentRange = 0.0..0.5
//                    speedRange = 500..600
//                }
//
//                timedTest {
//                    percentOutput = -0.25
//                    currentRange = 0.0..0.5
//                    speedRange = 500..600
//                }
//
//                timedTest {
//                    percentOutput = -0.5
//                    currentRange = 0.0..0.5
//                    speedRange = 500..600
//                }
//
//                timedTest {
//                    percentOutput = 0.75
//                    currentRange = 0.6..1.2
//                    speedRange = 1000..1100
//                }
//            }
//
//            talonCheck {
//                name = "Intake Roller Talon"
//                talons = Robot.INTAKE.rollerTalon
//
//                timedTest {
//                    percentOutput = 0.25
//                    currentRange = 0.0..0.5
//                    speedRange = 500..600
//                }
//
//                timedTest {
//                    percentOutput = 0.5
//                    currentRange = 0.6..1.2
//                    speedRange = 1000..1100
//                }
//            }
//
        }

    }

    override fun execute() {
        healthCheck.execute()
    }

    override fun isFinished() = healthCheck.isFinished()

    override fun end() {
        healthCheck.report()
    }
}