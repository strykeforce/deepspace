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

                val volt3currentRange = 0.5..0.65
                val volt6currentRange = 0.5..1.0
                val volt9currentRange = 1.0..1.25

                timedTest {
                    percentOutput = 0.25
                    currentRange = volt3currentRange
                    speedRange = 215..250
                }

                timedTest {
                    percentOutput = -0.25
                    currentRange = volt3currentRange
                    speedRange = -250..-215
                }

                timedTest {
                    percentOutput = 0.5
                    currentRange = volt6currentRange
                    speedRange = 475..535
                }

                timedTest {
                    percentOutput = -0.5
                    currentRange = volt6currentRange
                    speedRange = -535..-475
                }

                timedTest {
                    percentOutput = 0.75
                    currentRange = volt9currentRange
                    speedRange = 750..810
                }

                timedTest {
                    percentOutput = -0.75
                    currentRange = volt9currentRange
                    speedRange = -810..-750
                }

            }

            talonCheck {
                name = "swerve drive tests"
                talons = Robot.DRIVE.allWheels.map { it.driveTalon }

                val volt3currentRange = 0.75..1.125
                val volt6currentRange = 1.0..1.75
                val volt12currentRange = 2.5..5.0

                timedTest {
                    percentOutput = 0.25
                    currentRange = volt3currentRange
                    speedRange = 8500..9500
                }

                timedTest {
                    percentOutput = -0.25
                    currentRange = volt3currentRange
                    speedRange = -9500..-8500
                }

                timedTest {
                    percentOutput = 0.5
                    currentRange = volt6currentRange
                    speedRange = 17200..19500
                }

                timedTest {
                    percentOutput = -0.5
                    currentRange = volt6currentRange
                    speedRange = -19500..-17200
                }

                timedTest {
                    percentOutput = 1.0
                    currentRange = volt12currentRange
                    speedRange = 34750..38500
                }

                timedTest {
                    percentOutput = -1.0
                    currentRange = volt12currentRange
                    speedRange = -38500 .. -34750
                }
            }


            talonCheck {
                name = "elevator position tests"
                talons = Robot.ELEVATOR.talons

                val elevatorCurrentRange = 2.0..3.0

                positionTalon {
                    encoderTarget = 10_000
                    encoderGoodEnough = 100
                }

                positionTest {
                    percentOutput = 0.2

                    encoderChangeTarget = 15_000
                    encoderGoodEnough = 500
                    encoderTimeOutCount = 5000

                    currentRange = elevatorCurrentRange
                    speedRange = 500..650
                }

                positionTest {
                    percentOutput = -0.2

                    encoderChangeTarget = 15_000
                    encoderGoodEnough = 500
                    encoderTimeOutCount = 5000

                    currentRange = elevatorCurrentRange
                    speedRange = -650..-500
                }
            }


            talonCheck {
                name = "shoulder position tests"
                talons = Robot.INTAKE.shoulderTalon

                val shoulderDownCurrentRange = 0.4..1.125
                val shoulderUpCurrentRange = 0.5..1.125

                positionTest {
                    percentOutput = 0.2

                    encoderChangeTarget = 2500
                    encoderGoodEnough = 200
                    encoderTimeOutCount = 500

                    currentRange = shoulderDownCurrentRange
                    speedRange = 100..160
                }

                positionTest {
                    percentOutput = -0.2

                    encoderChangeTarget = 2500
                    encoderGoodEnough = 200
                    encoderTimeOutCount = 500

                    currentRange = shoulderUpCurrentRange
                    speedRange = -160..-100
                }
            }

            talonCheck {
                name = "biscuit position tests"
                talons = Robot.BISCUIT.talons

                val downCurrentRange = 0.375..1.0
                val upCurrentRange = 1.25..1.75

                positionTest {
                    percentOutput = 0.2

                    encoderChangeTarget = 3000
                    encoderGoodEnough = 50
                    encoderTimeOutCount = 500

                    currentRange = downCurrentRange
                    speedRange = 270..300
                }

                positionTest {
                    percentOutput = -0.2

                    encoderChangeTarget = 3000
                    encoderGoodEnough = 50
                    encoderTimeOutCount = 250

                    currentRange = upCurrentRange
                    speedRange = -230..-200
                }

                positionTalon {
                    encoderTarget = 0
                    encoderGoodEnough = 20
                }

                positionTest {
                    percentOutput = -0.2

                    encoderChangeTarget = 3000
                    encoderGoodEnough = 50
                    encoderTimeOutCount = 500

                    currentRange = downCurrentRange
                    speedRange = -300..-270
                }

                positionTest {
                    percentOutput = 0.2

                    encoderChangeTarget = 3000
                    encoderGoodEnough = 50
                    encoderTimeOutCount = 250

                    currentRange = upCurrentRange
                    speedRange = 200..230
                }

            }

            talonCheck {
                name = "intake roller tests"
                talons = Robot.INTAKE.rollerTalon

                val volt6currentRange = 1.0..3.0
                val volt12currentRange = 9.0..11.0

                timedTest {
                    percentOutput = 0.25
                    currentRange = volt6currentRange
                    speedRange = 1100..1350
                }

                timedTest {
                    percentOutput = -0.25
                    currentRange = volt6currentRange
                    speedRange = -1350..-1100
                }

                timedTest {
                    percentOutput = 1.0
                    currentRange = volt12currentRange
                    speedRange = 1100..6500
                }

                timedTest {
                    percentOutput = -1.0
                    currentRange = volt12currentRange
                    speedRange = -6500..-1100
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