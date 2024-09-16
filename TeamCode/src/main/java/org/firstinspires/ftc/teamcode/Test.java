package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by bm121 on 1/15/2017.
 */


@Autonomous(name="Test", group="Robot")
public class Test extends LinearOpMode {

    HardwareHFbot robot = new HardwareHFbot();
    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        waitForStart();
        int i = 0;
        while (opModeIsActive()) {
            robot.frontleftMotor.setPower(1);
        }
    }
}
