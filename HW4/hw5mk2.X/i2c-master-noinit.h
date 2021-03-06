/* 
 * File:   I2C.h
 * Author: Alex
 *
 * Created on April 17, 2017, 2:17 PM
 */

#ifndef I2C2_MASTER_NOINT_H__
#define I2C2_MASTER_NOINT_H__
// Header file for i2c_master_noint.c
// helps implement use I2C1 as a master without using interrupts

void i2c2_master_setup(void);              // set up I2C 1 as a master, at 100 kHz

void i2c2_master_start(void);              // send a START signal
void i2c2_master_restart(void);            // send a RESTART signal
void i2c2_master_send(unsigned char byte); // send a byte (either an address or data)
unsigned char i2c2_master_recv(void);      // receive a byte of data
void i2c2_master_ack(int val);             // send an ACK (0) or NACK (1)
void i2c2_master_stop(void);               // send a stop

#endif

