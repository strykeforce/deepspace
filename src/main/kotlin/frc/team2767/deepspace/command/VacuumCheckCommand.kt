package frc.team2767.deepspace.command

import edu.wpi.first.wpilibj.command.Command
import frc.team2767.deepspace.Robot
import frc.team2767.deepspace.health.HealthCheck
import frc.team2767.deepspace.health.healthCheck
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class VacuumCheckCommand : Command() {
    init {
        requires(Robot.VACUUM)
    }

    private lateinit var healthCheck: HealthCheck

    override fun initialize() {
        healthCheck = healthCheck {
            talonCheck {
                name = "vacuum tests"
                talons = Robot.VACUUM.talons

                positionTest {
                    zeroGoodEnough = 50
                    percentOutput = 1.0
                    encoderTarget = 500
                    encoderTimeOutCount = 5000
                }

                positionTest {
                    zeroGoodEnough = 50
                    percentOutput = 1.0
                    encoderTarget = 900
                    encoderTimeOutCount = 5000
                }

            }
        }
    }

    override fun isFinished(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}