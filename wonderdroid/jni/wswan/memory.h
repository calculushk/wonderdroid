#ifndef __WSWAN_MEMORY_H
#define __WSWAN_MEMORY_H

#include <stdbool.h>
#include <stdint.h>

#ifndef __cplusplus

uint8 wsRAM[65536];
uint8 *wsCartROM;
uint32 eeprom_size;
uint8 wsEEPROM[2048];
uint8 *wsSRAM;

#endif

uint8 WSwan_readmem20(uint32);
void WSwan_writemem20(uint32 address,uint8 data);

void WSwan_MemoryInit(bool IsWSC, uint32 ssize);
void WSwan_MemoryKill(void);

void WSwan_CheckSoundDMA(void);

void WSwan_MemoryReset(void);
void WSwan_writeport(uint32 IOPort, uint8 V);
uint8 WSwan_readport(uint32 number);

#endif
