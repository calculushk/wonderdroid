#ifndef __WSWAN_H
#define __WSWAN_H

#include "../types.h"


#define  mBCD(value) (((value)/10)<<4)|((value)%10)

extern          uint32 rom_size;
extern          int wsc;

#include "interrupt.h"

#endif
