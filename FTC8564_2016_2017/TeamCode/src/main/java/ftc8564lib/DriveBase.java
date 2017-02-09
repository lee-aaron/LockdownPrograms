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

package ftc8564lib;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import ftclib.FtcAnalogGyro;
import hallib.HalDashboard;
import hallib.HalUtil;

public class DriveBase implements PIDControl.PidInput {

    private LinearOpMode opMode;
    private PIDControl pidControl, pidControlTurn;

    private final static double SCALE = (144.5/12556.5);    // INCHES_PER_COUNT
    private double degrees = 0.0;
    private double stallStartTime = 0.0;
    private int prevLeftPos = 0;
    private int prevRightPos = 0;
    private boolean slowSpeed;

    public DcMotor leftMotor, rightMotor;
    private FtcAnalogGyro gyro;
    private ModernRoboticsI2cGyro gyroSensor;
    private HalDashboard dashboard;
    private ElapsedTime mRunTime;

    public interface AbortTrigger
    {
        boolean shouldAbort();
    }

    public DriveBase(LinearOpMode opMode, boolean auto) throws InterruptedException {
        this.opMode = opMode;
        rightMotor = opMode.hardwareMap.dcMotor.get("rightMotor");
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
        leftMotor = opMode.hardwareMap.dcMotor.get("leftMotor");
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //gyro = new FtcAnalogGyro(opMode, "gyro1", 0.00067);
        gyroSensor = (ModernRoboticsI2cGyro)opMode.hardwareMap.gyroSensor.get("gyro");
        mRunTime = new ElapsedTime();
        mRunTime.reset();
        dashboard = Robot.getDashboard();
        if(auto)
        {
            //gyro.calibrate();
            gyroSensor.calibrate();
            dashboard.displayPrintf(0, "Gyro : Calibrating");
            while(gyroSensor.isCalibrating()) {
                opMode.idle();
            }
            dashboard.displayPrintf(1, "Gyro : Done Calibrating");
        }
        //Sets up PID Drive: kP, kI, kD, kF, Tolerance, Settling Time
        pidControl = new PIDControl(0.03,0,0,0,0.5,0.2,this);
        pidControlTurn = new PIDControl(0.02,0,0,0,0.5,0.2,this);
        pidControlTurn.setAbsoluteSetPoint(true);
        dashboard.clearDisplay();
    }

    //Input distance in inches and power with decimal to hundredth place
    public void drivePID(double distance, boolean slow, AbortTrigger abortTrigger) throws InterruptedException {
        if(slow)
        {
            pidControl.setOutputRange(-0.55, 0.55);
        } else {
            pidControl.setOutputRange(-0.85,0.85);
        }
        if (Math.abs(distance) <= 5)
        {
            pidControl.setPID(0.081,0,0,0);
        }
        else if(Math.abs(distance) <= 10)
        {
            pidControl.setPID(0.05,0,0,0);
        } else
        {
            pidControl.setPID(0.0345,0,0.0005,0);
        }
        pidControl.setTarget(distance);
        pidControlTurn.setTarget(degrees);
        stallStartTime = HalUtil.getCurrentTime();
        while ((!pidControl.isOnTarget() || !pidControlTurn.isOnTarget()) && opMode.opModeIsActive()) {
            if(abortTrigger != null && abortTrigger.shouldAbort())
            {
                break;
            }
            int currLeftPos = leftMotor.getCurrentPosition();
            int currRightPos = rightMotor.getCurrentPosition();
            double drivePower = pidControl.getPowerOutput();
            double turnPower = pidControlTurn.getPowerOutput();
            leftMotor.setPower(drivePower + turnPower);
            rightMotor.setPower(drivePower - turnPower);
            double currTime = HalUtil.getCurrentTime();
            if (currLeftPos != prevLeftPos || currRightPos != prevRightPos)
            {
                stallStartTime = currTime;
                prevLeftPos = currLeftPos;
                prevRightPos = currRightPos;
            }
            else if (currTime > stallStartTime + 0.2)
            {
                // The motors are stalled for more than 0.2 seconds.
                break;
            }
            pidControl.displayPidInfo(0);
            opMode.idle();
        }
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        resetPIDDrive();
    }

    public void spinPID(double degrees) throws InterruptedException {
        pidControlTurn.setOutputRange(-0.7,0.7);
        this.degrees = degrees;
        if(Math.abs(degrees - gyroSensor.getIntegratedZValue()) < 10.0)
        {
            pidControlTurn.setPID(0.11,0,0.0015,0);
        } else if(Math.abs(degrees - gyroSensor.getIntegratedZValue()) < 20.0)
        {
            pidControlTurn.setPID(0.042,0,0.0005,0);
        } else if(Math.abs(degrees - gyroSensor.getIntegratedZValue()) < 45.0)
        {
            pidControlTurn.setPID(0.03,0,0.0005,0);
        } else if(Math.abs(degrees - gyroSensor.getIntegratedZValue()) < 90.0)
        {
            pidControlTurn.setPID(0.024,0,0.0005,0);
        } else {
            pidControlTurn.setPID(0.023,0,0,0);
        }
        pidControlTurn.setTarget(degrees);
        stallStartTime = HalUtil.getCurrentTime();
        while (!pidControlTurn.isOnTarget() && opMode.opModeIsActive()) {
            int currLeftPos = leftMotor.getCurrentPosition();
            int currRightPos = rightMotor.getCurrentPosition();
            double outputPower = pidControlTurn.getPowerOutput();
            leftMotor.setPower(outputPower);
            rightMotor.setPower(-outputPower);
            double currTime = HalUtil.getCurrentTime();
            if (currLeftPos != prevLeftPos || currRightPos != prevRightPos)
            {
                stallStartTime = currTime;
                prevLeftPos = currLeftPos;
                prevRightPos = currRightPos;
            }
            else if (currTime > stallStartTime + 0.2)
            {
                // The motors are stalled for more than 0.2 seconds.
                break;
            }
            pidControlTurn.displayPidInfo(0);
            opMode.idle();
        }
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        resetPIDDrive();
    }

    public void sleep(double seconds)
    {
        double startTime = HalUtil.getCurrentTime();
        while(startTime + seconds > HalUtil.getCurrentTime())
        {

        }
    }

    public void slowSpeed(boolean slow)
    {
        slowSpeed = slow;
    }

    public void tankDrive(float leftPower, float rightPower) throws InterruptedException {
        leftPower = Range.clip(leftPower, -1, 1);
        rightPower = Range.clip(rightPower, -1, 1);
        if(slowSpeed)
        {
            leftPower = (float) scalePowerSlow(leftPower);
            rightPower = (float) scalePowerSlow(rightPower);
        } else {
            leftPower = (float) scalePower(leftPower);
            rightPower = (float) scalePower(rightPower);
        }
        leftMotor.setPower(leftPower);
        rightMotor.setPower(rightPower);
    }

    public void resetMotors() throws InterruptedException {
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void resetPIDDrive() {
        pidControl.reset();
        pidControlTurn.reset();
    }

    public void noEncoders()
    {
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private double scalePower(double dVal)
    {
        return -(Math.signum(dVal) * ((Math.pow(dVal, 2) * (.9)) + .1));
    }

    private double scalePowerSlow(double dVal)
    {
        return -(Math.signum(dVal) * ((Math.pow(dVal, 2) * (.35)) + .1));
    }

    public void resetHeading()
    {
        //gyro.resetIntegrator();
        gyroSensor.resetZAxisIntegrator();
    }

    @Override
    public double getInput(PIDControl pidCtrl)
    {
        double input = 0.0;
        if (pidCtrl == pidControl)
        {
            input = (leftMotor.getCurrentPosition() + rightMotor.getCurrentPosition())*SCALE/2.0;
        }
        else if (pidCtrl == pidControlTurn)
        {
            //input = gyro.getHeading();
            input = gyroSensor.getIntegratedZValue();
        }
        return input;
    }
}