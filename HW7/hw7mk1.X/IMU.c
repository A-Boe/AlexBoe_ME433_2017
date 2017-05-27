#include<xc.h>
#include<sys/attribs.h>
#include<stdio.h>
#include "IMU.h"
#include "i2c_master_noint.h"

#define ACCE<_ADDR 0b11010110

void IMU_init(void){
    ANSELBbits.ANSB2 = 0;
    ANSELBbits.ANSB3 = 0;
    i2c2_master_setup();
    i2c2_master_start();
    i2c2_master_send(ACCEL_ADDR);
    i2c2_master_send(0x10);
    i2c2_master_send(0b10000010);
    i2c2_master_stop();
    
    i2c2_master_start();
    i2c2_master_send(ACCEL_ADDR);
    i2c2_master_send(0x11);
    i2c2_master_send(0b10001000);
    i2c2_master_stop();
    
}

void IMU_read_multiple(unsigned char reg, unsigned char *data, int len){
    int i;
    i2c2_master_start();
    i2c2_master_send(ACCEL_ADDR);
    i2c2_master_send(reg);
    i2c2_master_restart();
    i2c2_master_send(ACCEL_ADDR | 1);
    
    for(i=0;i<length;i++){
        data[i] = i2c2_master_recv();
        if(i<(len-1)){
            i2c2_master_ack(0);}
        else{
            i2c2_master_ack(1);}
        }
    i2c2_master_stop();
    }

unsigned char IMU_check(void){
    unsigned char value;
    i2c2_master_start();
    i2c2_master_send(ACCEL_ADDR);
    i2c2_master_send(0x0F);
    i2c2_master_restart();
    i2c2_master_send(ACCEL_ADDR | 1);
    value = i2c2_master_recv();
    return value;}
