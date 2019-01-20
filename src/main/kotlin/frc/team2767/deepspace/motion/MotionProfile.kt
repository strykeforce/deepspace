package frc.team2767.deepspace.motion

import java.io.File

// Definitions, from https://www.chiefdelphi.com/forums/showpost.php?p=1204107&postcount=18
//
//    dt  = iteration time, loop period in ms
//    t1  = time for first filter, in ms
//    t2  = time for second filter, in ms
//   fl1  = filter 1 window length. fl1 = roundup(t1/dt)
//   fl2  = filter 2 window length. fl2 = roundup(t2/dt)
// vProg  = desired max speed, ticks/sec
//  dist  = travel actualDistance, ticks
//    t4  = time to get to destination, in ms, at vProg. t4 = dist/vProg
//     n  = number of inputs to filter. n = roundup(t4/dt)

class MotionProfile(val dt: Int, t1: Int, t2: Int, val vProg: Int, dist: Int) {

    private val f1 = IntArray(Math.ceil(t1.toDouble() / dt).toInt())
    private val f2 = DoubleArray(Math.ceil(t2.toDouble() / dt).toInt())
    private val n: Int
    var iteration = 0
        private set
    var currVel = 0.0
        private set
    var currPos = 0.0
        private set
    var currAcc = 0.0
        private set
    private var prevVel = 0.0
    private var prevPos = 0.0

    val isFinished: Boolean
        get() = iteration >= n + f1.size + f2.size + 1

    init {
        val t4 = dist / vProg.toDouble() * 1000
        n = Math.ceil(t4 / dt).toInt()
    }

    fun calculate() {
        f1[iteration % f1.size] = if (iteration == 0 || iteration > n) 0 else 1 // boxcar input to filter 1
        f2[iteration % f2.size] = f1.average()
        currVel = f2.average() * vProg
        currPos = (prevVel + currVel) / 2 * dt / 1000 + prevPos
        currAcc = (currVel - prevVel) / (dt.toDouble() / 1000)
        prevVel = currVel
        prevPos = currPos
        iteration++
    }
}

fun MotionProfile.dumpCsv(file: File) {
    val writer = file.printWriter()
    writer.use {
        writer.println("iteration,position,velocity,acceleration")
        while (!this.isFinished) {
            this.calculate()
            writer.println("${this.iteration},${this.currPos},${this.currVel},${this.currAcc}")
        }
    }
}
