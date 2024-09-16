/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


/**
 * This file provides basic Telop driving for a Pushbot robot.
 * The code sis structured as an Iterative OpMode
 * <p>
 * This OpMode uses the common Pushbot hardware class to define the devices on the robot.
 * All device access is managed through the HardwarePushbot class.
 * <p>
 * This particular OpMode executes a basic Tank Drive Teleop for a PushBot
 * It raises and lowers the claw using the Gampad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name = "Mecanum", group = "ROBOT")
//@Disabled
public class BenTeleOp extends OpMode {

    /* Declare OpMode members.s */
    HardwareHFbot robot = new HardwareHFbot(); // Use the class created to define a HFRobots hardware
    public static boolean sweeperIsRunning = false;   //Defaults sweeper to stop


    @Override
    public void init() {

        robot.init(hardwareMap); //Calls init of HardwareHFbot

        // Send telemetry message to signify Init
        telemetry.addData("Say", "Robot Initing");

        //Sets all motors to stationary
        robot.frontleftMotor.setPower(0);
        robot.frontrightMotor.setPower(0);
        robot.backleftMotor.setPower(0);
        robot.backrightMotor.setPower(0);
        robot.shooter.setPower(0);
        robot.sweeper.setPower(0);

        //Sets position of servos to start position;
        robot.lifter.setPosition(.025);
        robot.leftBeacon.setPosition(0);
        robot.rightBeacon.setPosition(0);
    }


    @Override
    public void init_loop() { //Not used. Supposed to run a loop after init is pressed.

    }



    @Override
    public void start() { //Not used. Called once after driver hits ply

    }


    @Override
    public void loop() { //Main loop. Called repeatably after driver hit play
        double leftTrigger1 = gamepad1.left_trigger; //Maps trigger values
        double rightTrigger1 = gamepad1.right_trigger;
        double leftTrigger2 = gamepad2.left_trigger;
        double rightTrigger2 = gamepad2.right_trigger;
        double BaseSpeed = 0.5; //Sets base speed value of robot

        if (rightTrigger1 > .1f) {
            robot.drive(RobotDirectionDrive.SPINRIGHT, rightTrigger1);  //Sets direction to drive based on what buttons are pressed.
        } else if (leftTrigger1 > .1f) {
            robot.drive(RobotDirectionDrive.SPINLEFT, leftTrigger1);
        } else if (gamepad1.dpad_up && gamepad1.dpad_left)
            robot.drive(RobotDirectionDrive.DFLEFT, BaseSpeed);
        else if (gamepad1.dpad_up && gamepad1.dpad_right)
            robot.drive(RobotDirectionDrive.DFRIGHT, BaseSpeed);
        else if (gamepad1.dpad_down && gamepad1.dpad_left)
            robot.drive(RobotDirectionDrive.DBLEFT, BaseSpeed);
        else if (gamepad1.dpad_down && gamepad1.dpad_right)
            robot.drive(RobotDirectionDrive.DBRIGHT, BaseSpeed);
        else if (gamepad1.dpad_up)
            robot.drive(RobotDirectionDrive.FORWARD, BaseSpeed);
        else if (gamepad1.dpad_down)
            robot.drive(RobotDirectionDrive.BACK, BaseSpeed);
        else if (gamepad1.dpad_left)
            robot.drive(RobotDirectionDrive.LEFT, BaseSpeed);
        else if (gamepad1.dpad_right)
            robot.drive(RobotDirectionDrive.RIGHT, BaseSpeed);
        else
            robot.drive(0, 0, 0, 0);

        if (gamepad1.y) {
            robot.reversed = !robot.reversed;  //Reverses robot driving
        }

        if(gamepad2.right_bumper){  //Raises lifter based on value of right trigger
            robot.lifter.setPosition(.477);
        }
        if(gamepad2.left_bumper){
            robot.lifter.setPosition(.025);
        }
        if (gamepad2.x) { //Button for shooter
            robot.shoot(true); //Calls shoot method in HardwareHFbot. Basically turns shooter while 'x' is held down.
        } else {
            robot.shoot(false); //Sets shooter to stop turning.
        }

        if(gamepad2.a){ //Checks if  a is pressed
            robot.sweeper.setPower(.75); //Sets sweeper running
        }
        if(gamepad2.b){
            robot.sweeper.setPower(0);
        }
        if(gamepad2.dpad_right){ //Checks if dpad right is pressed
            robot.leftBeacon.setPosition(0); //Sets left beacon presser to up
            robot.rightBeacon.setPosition(.58);//Sets right beacon presser to down
        }
        if(gamepad2.dpad_left){ //Checks if dpad left is pressed
            robot.rightBeacon.setPosition(0);//Sets right beacon presser to up
            robot.leftBeacon.setPosition(.5); //Sets left beacon presser down
        }
        if(gamepad2.dpad_down){ //Checks if dpad down is pressed
            robot.leftBeacon.setPosition(0); //Sets both beacon pressers to up position
            robot.rightBeacon.setPosition(0);
        }

        telemetry.addData("Is Reversed: ", robot.reversed); //Add telemetry to see if robot control are reversed
        telemetry.addData("Fork servo value", robot.lifter.getPosition()); //Adds lifter servo value
    }


    @Override
    public void stop() { //Called when driver hits stop
        robot.drive(0, 0, 0, 0); //Sets all motors to zero.
        robot.shooter.setPower(0);
        robot.sweeper.setPower(0);
        robot.lifter.setPosition(0);

    }


}
