/*
 * Lockdown Framework Library
 * Copyright (c) 2015 Lockdown Team 8564 (lockdown8564.weebly.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ftc8564opMode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import ftclib.*;
import ftc8564lib.*;

@Autonomous(name="TestAutonomous", group="Autonomous")
public class TestAutonomous extends LinearOpMode implements DriveBase.AbortTrigger {

    Robot robot;

    private enum Alliance {
        RED_ALLIANCE,
        BLUE_ALLIANCE
    }

    private enum State {
        INIT,
        GO_TO_BEACON,
        ALIGN_WITH_WALL
    }

    private Alliance alliance = Alliance.RED_ALLIANCE;
    private State state = State.INIT;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(this,true);
        waitForStart();
        robot.driveBase.resetHeading();

        robot.driveBase.drivePID(15, null);
        robot.driveBase.spinPID(45);
        robot.driveBase.drivePID(55, null);

        robot.shooter.resetMotors();
        robot.pulleySystem.resetMotors();
        robot.driveBase.resetMotors();
        robot.driveBase.resetPIDDrive();

    }

    private void newState(State state)
    {
        this.state = state;
    }

    @Override
    public boolean shouldAbort() { return robot.odsLeft.getLightDetected() >= 0.2 || robot.odsRight.getLightDetected() >= 0.2; }

}