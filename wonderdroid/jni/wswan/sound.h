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

#ifndef __cplusplus
	void wswan_soundinit(void);
	uint8 wswan_soundread(uint32 A);
	void wswan_soundwrite(uint32 A, uint8 V);
	int16 wswan_soundflush(int16 *buffer);
	void wswan_soundreset(void);
	void wswan_soundcheckramwrite(uint32 A);
#endif

void WSwan_SoundCheckRAMWrite(uint32 A);

#endif
