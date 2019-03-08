package frc.team2767.deepspace.command

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
        requires(Robot.VACUUM)
    }

    private lateinit var healthCheck: HealthCheck

    override fun initialize() {
        healthCheck = healthCheck {
            //
//            vacuumCheck {
//                name = "pressure tests"
//
//                pressureTest {
//                    name = "climb pressure test"
//
//                    pressure = 16.0
//                    encoderTimeOutCount = 20_000
//                    maxAcceptablePressureDrop = 2
//                }
//            }

            // pump tests are highly dependent on valve states set in pressure tests
            talonCheck {
                name = "pump tests"
                talons = Robot.VACUUM.talons

                timedTest {
                    percentOutput = 0.25
                    currentRange = 0.0..0.0
                    speedRange = 0..0
                    duration = 5.0
                }
            }


            talonCheck {
                name = "swerve azimuth tests"
                talons = Robot.DRIVE.allWheels.map { it.azimuthTalon }

                timedTest {
                    percentOutput = 0.25
                    currentRange = 0.5..0.75
                    speedRange = 215..245
                }

                timedTest {
                    percentOutput = -0.25
                    currentRange = 0.5..0.75
                    speedRange = -245..-215
                }

                timedTest {
                    percentOutput = 0.5
                    currentRange = 0.5..1.0
                    speedRange = 475..510
                }

                timedTest {
                    percentOutput = -0.5
                    currentRange = 0.5..1.0
                    speedRange = -510..-475
                }

                timedTest {
                    percentOutput = 0.75
                    currentRange = 1.125..1.5
                    speedRange = 730..760
                }

                timedTest {
                    percentOutput = -0.75
                    currentRange = 1.125..1.5
                    speedRange = -760..730
                }

            }

            talonCheck {
                name = "swerve drive tests"
                talons = Robot.DRIVE.allWheels.map { it.driveTalon }

                timedTest {
                    percentOutput = 0.25
                    currentRange = 0.0..0.5
                    speedRange = 500..600
                }

                timedTest {
                    percentOutput = -0.25
                    currentRange = 0.0..0.5
                    speedRange = 500..600
                }

                timedTest {
                    percentOutput = -0.5
                    currentRange = 0.0..0.5
                    speedRange = 500..600
                }

                timedTest {
                    percentOutput = 0.75
                    currentRange = 0.6..1.2
                    speedRange = 1000..1100
                }
            }


            talonCheck {
                name = "elevator position tests"
                talons = Robot.ELEVATOR.talons

                positionTalon {
                    encoderTarget = 10_000
                    encoderGoodEnough = 100
                }

                positionTest {
                    percentOutput = 0.2

                    encoderChangeTarget = 15_000
                    encoderGoodEnough = 500
                    encoderTimeOutCount = 5000

                    currentRange = 0.0..0.5
                    speedRange = 500..600
                }

                positionTest {
                    percentOutput = -0.2

                    encoderChangeTarget = 15_000
                    encoderGoodEnough = 500
                    encoderTimeOutCount = 5000

                    currentRange = 0.0..0.5
                    speedRange = 500..600
                }
            }


            talonCheck {
                name = "shoulder position tests"
                talons = Robot.INTAKE.shoulderTalon

                positionTest {
                    percentOutput = 0.2

                    encoderChangeTarget = 2500
                    encoderGoodEnough = 200
                    encoderTimeOutCount = 500

                    currentRange = 0.0..0.5
                    speedRange = 500..600
                }

                positionTest {
                    percentOutput = -0.2

                    encoderChangeTarget = 2500
                    encoderGoodEnough = 200
                    encoderTimeOutCount = 500

                    currentRange = 0.0..0.5
                    speedRange = 500..600
                }
            }

            talonCheck {
                name = "biscuit position tests"
                talons = Robot.BISCUIT.talons

                positionTest {
                    percentOutput = 0.2

                    encoderChangeTarget = 3000
                    encoderGoodEnough = 50
                    encoderTimeOutCount = 500

                    currentRange = 0.0..0.5
                    speedRange = 500..600
                }

                positionTest {
                    percentOutput = -0.2

                    encoderChangeTarget = 3000
                    encoderGoodEnough = 50
                    encoderTimeOutCount = 250

                    currentRange = 0.0..0.5
                    speedRange = 500..600
                }
            }

            talonCheck {
                name = "intake roller tests"
                talons = Robot.INTAKE.rollerTalon

                timedTest {
                    percentOutput = 0.25
                    currentRange = 0.0..0.5
                    speedRange = 500..600
                }

                timedTest {
                    percentOutput = 0.5
                    currentRange = 0.6..1.2
                    speedRange = 1000..1100
                }
            }
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