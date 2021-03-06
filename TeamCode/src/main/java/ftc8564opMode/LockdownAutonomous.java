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

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

import java.util.concurrent.locks.Lock;

import ftclib.*;
import ftc8564lib.*;
import hallib.HalUtil;

@Autonomous(name="LockdownAutonomous", group="Autonomous")
public class LockdownAutonomous extends LinearOpMode implements FtcMenu.MenuButtons{
    Robot robot;

    private ElapsedTime mClock = new ElapsedTime();

    public enum Alliance {
        RED_ALLIANCE,
        BLUE_ALLIANCE
    }

    public enum Alliance_Position {
        BLUE_RIGHT,
        BLUE_LEFT,
        RED_RIGHT,
        RED_LEFT
    }

    private Alliance_Position alliance = Alliance_Position.BLUE_RIGHT;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(this,true);// need this to run menu test
        doMenus();
        waitForStart();

        // robot.driveBase.resetHeading();

        if (alliance == Alliance_Position.BLUE_RIGHT) {
            robot.jewelArm.armDown();
            robot.jewelArm.pushJewels(true);
            robot.jewelArm.armUp();
            if(robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.LEFT){
                robot.driveBase.drivePID(2,false);
            }
            else if (robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.CENTER){
                robot.driveBase.drivePID(3,false);
            }
            else if (robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.RIGHT){
                robot.driveBase.drivePID(4,false);
            }
            robot.driveBase.spinPID(90);
            robot.driveBase.drivePID(1,true);
            robot.clamps.open();
        } else if (alliance == Alliance_Position.BLUE_LEFT) {
            robot.jewelArm.armDown();
            robot.jewelArm.pushJewels(true);
            robot.jewelArm.armUp();
            if(robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.LEFT){
                robot.driveBase.drivePID(2,false);
            }
            else if (robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.CENTER){
                robot.driveBase.drivePID(3,false);
            }
            else if (robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.RIGHT){
                robot.driveBase.drivePID(4,false);
            }
            robot.driveBase.spinPID(90);
            robot.driveBase.drivePID(1,true);
            robot.clamps.open();
        } else if (alliance == Alliance_Position.RED_RIGHT) {
            robot.jewelArm.armDown();
            robot.jewelArm.pushJewels(true);
            robot.jewelArm.armUp();
            if(robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.LEFT){
                robot.driveBase.drivePID(2,false);
            }
            else if (robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.CENTER){
                robot.driveBase.drivePID(3,false);
            }
            else if (robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.RIGHT){
                robot.driveBase.drivePID(4,false);
            }
            robot.driveBase.spinPID(90);
            robot.driveBase.drivePID(1,true);
            robot.clamps.open();
        } else if (alliance == Alliance_Position.RED_LEFT) {
            robot.jewelArm.armDown();
            robot.jewelArm.pushJewels(true);
            robot.jewelArm.armUp();
            if(robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.LEFT){
                robot.driveBase.drivePID(2,false);
            }
            else if (robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.CENTER){
                robot.driveBase.drivePID(3,false);
            }
            else if (robot.VuMark.getCryptoboxKey() == RelicRecoveryVuMark.RIGHT){
                robot.driveBase.drivePID(4,false);
            }
            robot.driveBase.spinPID(90);
            robot.driveBase.drivePID(1,true);
            robot.clamps.open();
        }
        runCleanUp();
        telemetry.clearAll();
        telemetry.update();
        sleep(7000); // sleep 7s

    }

    private void runCleanUp() throws InterruptedException {
        //robot.shooter.resetMotors();
        //robot.beaconPush.resetRacks();
        //robot.pulleySystem.resetMotors();
        //robot.driveBase.resetMotors();
        //robot.driveBase.resetPIDDrive();
    }

    @Override
    public boolean isMenuUpButton() {
        return gamepad1.dpad_up;
    }

    @Override
    public boolean isMenuDownButton() {
        return gamepad1.dpad_down;
    }

    @Override
    public boolean isMenuEnterButton() {
        return gamepad1.dpad_right;
    }

    @Override
    public boolean isMenuBackButton() {
        return gamepad1.dpad_left;
    }

    private void doMenus() throws InterruptedException {
        FtcChoiceMenu alliancePositionMenu = new FtcChoiceMenu("Alliance Position:", null, this);

        alliancePositionMenu.addChoice("BLUE RIGHT", LockdownAutonomous.Alliance_Position.BLUE_RIGHT);
        alliancePositionMenu.addChoice("BLUE LEFT", LockdownAutonomous.Alliance_Position.BLUE_LEFT);
        alliancePositionMenu.addChoice("RED RIGHT", LockdownAutonomous.Alliance_Position.RED_RIGHT);
        alliancePositionMenu.addChoice("RED LEFT", LockdownAutonomous.Alliance_Position.RED_LEFT);

        FtcMenu.walkMenuTree(alliancePositionMenu);
        alliance = (LockdownAutonomous.Alliance_Position) alliancePositionMenu.getCurrentChoiceObject();
    }

}