#include "motor.h"

void initMotors(){
     RPA0Rbits.RPA0R = 0b0101;   
    RPB2Rbits.RPB2R = 0b0101;   
   TRISAbits.TRISA1 = 0;       
    TRISBbits.TRISB3 = 0;       
    
    // initialize OC1
    T2CONbits.TCKPS = 2; // prescaler N=4 
    PR2 = 1200 - 1; // 10kHz
    TMR2 = 0;
    OC1CONbits.OCM = 0b110; // PWM mode without fault pin; other OC1CON bits are defaults
    OC4CONbits.OCM = 0b110;
    OC1RS = 0; // max allowed value is 1119
    OC1R = 0; // read-only initial value
    OC4RS = 0; // max allowed value is 1119
    OC4R = 0; // read-only initial value
    T2CONbits.ON = 1;
    OC1CONbits.ON = 1;
    OC4CONbits.ON = 1;
    
     // put these initializations in APP_Initialize()
    RPB9Rbits.RPB9R = 0b101; // B2 is OC4
    T3CONbits.TCKPS = 4; // prescaler N=16
    PR3 = 60000 - 1; // 50Hz
    TMR3 = 0;
    OC3CONbits.OCM = 0b110; // PWM mode without fault pin; other OC1CON bits are defaults
    OC3CONbits.OCTSEL = 1; // use timer3
    OC3RS = 4500; // should set the motor to 90 degrees (0.5ms to 2.5ms is 1500 to 7500 for 0 to 180 degrees)
    OC3R = 4500; // read-only
    T3CONbits.ON = 1;
    OC3CONbits.ON = 1;				// turn on OC1

}


void pwm_set(signed int pwm1, signed int pwm2) {
    // motor 1
    LATAbits.LATA1 = 0;
    LATBbits.LATB3 = 0;
    OC1RS = pwm1;
    OC4RS = pwm2;
    
    /*if (pwm1 <= 0) {
        LATAbits.LATA1 = 1;
        if (pwm1 >= -PR2-1)
            OC1RS = -pwm1*PWM_MUL;
        else
            OC1RS = (PR2+1)*PWM_MUL;
    }
    else {
        LATAbits.LATA1 = 0;
        if (pwm1 <= PR2+1)
            OC1RS = pwm1;
        else
            OC1RS = (PR2+1);
    }
    // motor 2
    if (pwm2 <= 0) {
        LATBbits.LATB3 = 0;
        if (pwm2 >= -PR2-1)
            OC4RS = -pwm2;
        else
            OC4RS = (PR2+1);
    }
    else {
        LATBbits.LATB3 = 1;
        if (pwm2 <= PR2+1)
            OC4RS = pwm2;
        else
            OC4RS = (PR2+1);
    }*/
}

void calc_control(int motor_offset) {
    int pwm1 = 100;         // left or right?
    int pwm2 = 100;         // left or right?
    if (motor_offset >= 0)
        pwm2 -= motor_offset;
    else 
        pwm1 += motor_offset;
    
    pwm_set(pwm1, pwm2);
}