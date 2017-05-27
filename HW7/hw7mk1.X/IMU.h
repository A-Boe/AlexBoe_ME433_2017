#ifndef IMU_H
#define IMU_H
#include<xc.h>
#include<sys/attribs.h>

void IMU_init(void);
void IMU_read_multiple(unsigned char reg, unsigned char *data, int len);
unsigned char IMU_check(void);

#endif