// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;

import org.ejml.dense.block.MatrixOps_DDRB;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;
import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.DefaultDriveCommand;
import frc.robot.commands.ShooterWithLimelight;
import frc.robot.commands.TestColorCommand;
import frc.robot.commands.TurretRotateCommand;
import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.LimeLightSubsystem;
import frc.robot.subsystems.Pathweaver;
import frc.robot.subsystems.PneumaticSubsystem;
import frc.robot.subsystems.TurretSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...

  //Subsystems
  private final DrivetrainSubsystem m_drivetrainSubsystem = new DrivetrainSubsystem();
  private final TurretSubsystem m_turretSubsystem = new TurretSubsystem();
  private final PneumaticSubsystem m_pneumaticSubsystem = new PneumaticSubsystem();
  private final LimeLightSubsystem m_limelightSubsystem = new LimeLightSubsystem();
  private final FeederSubsystem m_feederSubsystem = new FeederSubsystem();
   
  private final XboxController m_controller = new XboxController(0);
  private final XboxController m_controller2 = new XboxController(1);

  //Single Commands
  private final TurretRotateCommand m_turretRotateCommand = new TurretRotateCommand(m_turretSubsystem, m_limelightSubsystem, m_controller2);
  private final DefaultDriveCommand m_driveCommand = new DefaultDriveCommand(m_drivetrainSubsystem,() -> -modifyAxis(m_controller.getLeftY()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,() -> -modifyAxis(m_controller.getLeftX()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,() -> -modifyAxis(m_controller.getRightX()) * DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND);
  private final ShooterWithLimelight m_shootCommand = new ShooterWithLimelight(4000, m_turretSubsystem, m_pneumaticSubsystem, m_limelightSubsystem, m_feederSubsystem);
  private final TestColorCommand m_colorTest = new TestColorCommand(m_feederSubsystem);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Set up the default command for the drivetrain.
    // The controls are for field-oriented driving:
    // Left stick Y axis -> forward and backwards movement
    // Left stick X axis -> left and right movement
    // Right stick X axis -> rotation
    PerpetualCommand DriveWithTurret = new PerpetualCommand(m_driveCommand.alongWith(m_turretRotateCommand)); 

    m_drivetrainSubsystem.zeroGyroscope();
    
    m_drivetrainSubsystem.setDefaultCommand(DriveWithTurret);
    
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // Back button zeros the gyroscope
    new Button(m_controller::getBackButton)
            // No requirements because we don't need to interrupt anything
            .whenPressed(m_drivetrainSubsystem::zeroGyroscope);

    //P1 BUTTONS
    JoystickButton DriverA = new JoystickButton(m_controller, XboxController.Button.kA.value);
    JoystickButton DriverB = new JoystickButton(m_controller, XboxController.Button.kB.value);
    
    //DriverA.whenPressed(m_shootCommand);
    //DriverB.whileHeld(m_colorTest);

    //P1 BUTTONS
    JoystickButton OperatorA = new JoystickButton(m_controller2, XboxController.Button.kA.value);
    JoystickButton OperatorB = new JoystickButton(m_controller2, XboxController.Button.kB.value);
    
    OperatorA.whenPressed(m_colorTest);
    //DriverB.whileHeld(m_colorTest);


  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return new InstantCommand();
  }

  private static double deadband(double value, double deadband) {
    if (Math.abs(value) > deadband) {
      if (value > 0.0) {
        return (value - deadband) / (1.0 - deadband);
      } else {
        return (value + deadband) / (1.0 - deadband);
      }
    } else {
      return 0.0;
    }
  }

  private static double modifyAxis(double value) {
    // Deadband
    value = deadband(value, 0.05);

    // Square the axis
    value = Math.copySign(value * value, value);

    return value;
  }
}
