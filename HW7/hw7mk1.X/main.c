#include<xc.h>           // processor SFR definitions
#include<sys/attribs.h>  // __ISR macro
#include<stdio.h>
#include "ILI9163C.h"

// DEVCFG0
#pragma config DEBUG = OFF // no debugging
#pragma config JTAGEN = OFF // no jtag
#pragma config ICESEL = ICS_PGx1 // use PGED1 and PGEC1
#pragma config PWP = OFF // no write protect
#pragma config BWP = OFF // no boot write protect
#pragma config CP = OFF // no code protect

// DEVCFG1
#pragma config FNOSC = FRCPLL // use primary oscillator with pll
#pragma config FSOSCEN = OFF // turn off secondary oscillator
#pragma config IESO = OFF // no switching clocks
#pragma config POSCMOD = HS // high speed crystal mode
#pragma config OSCIOFNC = OFF // disable secondary osc
#pragma config FPBDIV = DIV_1 // divide sysclk freq by 1 for peripheral bus clock
#pragma config FCKSM = CSDCMD // do not enable clock switch
#pragma config WDTPS = PS1048576 // use slowest wdt
#pragma config WINDIS = OFF // wdt no window mode
#pragma config FWDTEN = OFF // wdt disabled
#pragma config FWDTWINSZ = WINSZ_25 // wdt window at 25%

// DEVCFG2 - get the sysclk clock to 48MHz from the 8MHz crystal
#pragma config FPLLIDIV = DIV_2 // divide input clock to be in range 4-5MHz
#pragma config FPLLMUL = MUL_24 // multiply clock after FPLLIDIV
#pragma config FPLLODIV = DIV_2 // divide clock after FPLLMUL to get 48MHz
#pragma config UPLLIDIV = DIV_2 // divider for the 8MHz input clock, then multiplied by 12 to get 48MHz for USB
#pragma config UPLLEN = ON // USB clock on

// DEVCFG3
#pragma config USERID = 00011010 // (26) some 16bit userid, doesn't matter what
#pragma config PMDL1WAY = OFF // allow multiple reconfigurations
#pragma config IOL1WAY = OFF // allow multiple reconfigurations
#pragma config FUSBIDIO = ON // USB pins controlled by USB module
#pragma config FVBUSONIO = ON // USB BUSON controlled by USB module

// Definitions
#define CLOCK 48000000
#define BCKGRND BLUE        // Background LCD color is "BLUE"   (0x001F)
#define TEXT WHITE          // Text LCD color is "WHITE"        (0xFFFF)
#define MAX_VAL 32768
#define VAL 256
#define CHECK 0b01101001

int main() {
__builtin_disable_interrupts();
__builtin_mtc0(_CP0_CONFIG,_CP0_CONFIG_SELECT,0xa4210583);
BMXCONbits.BMXWSDRM = 0x0;
INTCONbits.MVEC = 0x1;
DDPCONbits.JTAGEN = 0;

TRISAbits.TRISA4 = 0;
LATAbits.LATA4 = 1;

SPI1_init();
LCD_init();
IMU_init();

__builtin_enable_interrupts();

LCD_clearScreen(BCKGRND);

unsigned char msg[100];
unsigned char msg2[100];
unsigned char IMU_data[14];
unsigned char value;
signed short ACC_data[7];
signed short len2 = MAX_VAL/VAL;
signed short barx, bary;

while(1){
    while(_CP0_GET_COUNT()<CLOCK<4800000) {;}
    
    _CP0_SET_COUNT(0);
    
    value = IMU_check();
    
    if(value != CHECK){
        while (_CP0_GET_COUNT()<CLOCK/2){;}
        _CP0_SET_COUNT(0);
        
        LATAbits.LATA4 = !LATAbits.LATA4;
        
    }
    else{
        IMU_read_multiple(0x20, IMU_data, 14);
        int i;
        for(i=0;i<7;i++){
            ACC_data[i] = ((IMU_data[(2*i)+1] << 8) | (IMU_data{2*i}));
        }
        
        LCD_writeBar(60, 60, TEXT, 4, 4);
        
        if(ACC_data[4] < 0){
            barx = (-1)*ACC_data[4]/VAL;
        
        LCD_writeBar(64,60,TEXT,barx,4);
        LCD_writeBar(64+barx,60,BCKGRND,len2-barx,4);
        LCD_writeBar(59-len2,60,BCKGRND,len2,4);}
        
        else{
            barx = ACC_data[4]/VAL;
            
            LCD_writeBar(60-barx,60,TEXT,barx,4);
            LCD_writeBar(60-len2,60,BCKGRND,len2-barx,4);
            LCD_writeBar(65,60,BCKGRND,len2,4);}
        
        }
    }