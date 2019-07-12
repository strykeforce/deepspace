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
            talonCheck {
                name = "swerve azimuth tests"
                talons = Robot.DRIVE.allWheels.map { it.azimuthTalon }

                val volt3currentRange = 0.25..0.65
                val volt6currentRange = 0.5..1.25
                val volt9currentRange = 1.0..1.5

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

                val volt3currentRange = 0.5..1.125
                val volt6currentRange = 1.0..2.0
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
                    speedRange = -38500..-34750
                }
            }


            talonCheck {
                name = "elevator position tests"
                talons = Robot.ELEVATOR.talons

                val elevatorDownCurrentRange = 0.5..3.0
                val elevatorUpCurrentRange = 0.5..3.0

                positionTalon {
                    encoderTarget = 10_000
                    encoderGoodEnough = 100
                }

                positionTest {
                    percentOutput = 0.2

                    encoderChangeTarget = 15_000
                    encoderGoodEnough = 500
                    encoderTimeOutCount = 5000

                    currentRange = elevatorUpCurrentRange
                    speedRange = 200..300
                }

                positionTest {
                    percentOutput = -0.2

                    encoderChangeTarget = 15_000
                    encoderGoodEnough = 500
                    encoderTimeOutCount = 5000

                    currentRange = elevatorDownCurrentRange
                    speedRange = -900..-750
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
                    speedRange = 130..190
                }

                positionTest {
                    percentOutput = -0.2

                    encoderChangeTarget = 2500
                    encoderGoodEnough = 200
                    encoderTimeOutCount = 500

                    currentRange = shoulderUpCurrentRange
                    speedRange = -190..-130
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
                    speedRange = 270..340
                }

                positionTest {
                    percentOutput = -0.2

                    encoderChangeTarget = 3000
                    encoderGoodEnough = 50
                    encoderTimeOutCount = 250

                    currentRange = upCurrentRange
                    speedRange = -240..-200
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
                    speedRange = -340..-270
                }

                positionTest {
                    percentOutput = 0.2

                    encoderChangeTarget = 3000
                    encoderGoodEnough = 50
                    encoderTimeOutCount = 250

                    currentRange = upCurrentRange
                    speedRange = 200..240
                }

            }

            talonCheck {
                name = "intake roller tests"
                talons = Robot.INTAKE.rollerTalon

                val volt6currentRange = 1.0..4.0
                val volt12currentRange = 13.0..17.0

                timedTest {
                    percentOutput = 0.25
                    currentRange = volt6currentRange
                    speedRange = 3500..4500
                }

                timedTest {
                    percentOutput = -0.25
                    currentRange = volt6currentRange
                    speedRange = -4500..-3500
                }

                timedTest {
                    percentOutput = 1.0
                    currentRange = volt12currentRange
                    speedRange = 16000..19000
                }

                timedTest {
                    percentOutput = -1.0
                    currentRange = volt12currentRange
                    speedRange = -19000..-16000
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