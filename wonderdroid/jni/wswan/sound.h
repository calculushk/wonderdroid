#ifndef __WSWAN_SOUND_H
#define __WSWAN_SOUND_H

#include <stdbool.h>

int16 WSwan_SoundFlush(int16 *buffer);
void WSwan_SoundInit();

void WSwan_Sound(int rate);

void WSwan_SoundWrite(uint32, uint8);
uint8 WSwan_SoundRead(uint32);
//void WSwan_SoundInit(void);

void WSwan_SoundReset(void);

#ifdef __cplusplus
extern "C" uint8 wsRAM[65536];
#endif

void WSwan_SoundCheckRAMWrite(uint32 A);

#endif
