#ifndef __MDFN_TYPES
#define __MDFN_TYPES

// Yes, yes, I know:  There's a better place for including config.h than here, but I'm tired, and this should work fine. :b
#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include <assert.h>
#include <inttypes.h>

typedef int8_t int8;
typedef int16_t int16;
typedef int32_t int32;

typedef uint8_t uint8;
typedef uint16_t uint16;
typedef uint32_t uint32;



typedef void (*writefunc)(uint32 A, uint8 V);
typedef uint8 (*readfunc)(uint32 A);

typedef uint32 UTF32; /* at least 32 bits */
typedef uint16 UTF16; /* at least 16 bits */
typedef uint8 UTF8; /* typically 8 bits */
typedef unsigned char Boolean; /* 0 or 1 */

#ifndef FALSE
#define FALSE 0
#endif

#ifndef TRUE
#define TRUE 1
#endif

#undef require
#define require( expr ) assert( expr )

#define INT_TO_BCD(A)  (((A) / 10) * 16 + ((A) % 10))              // convert INT --> BCD
#define BCD_TO_INT(B)  (((B) / 16) * 10 + ((B) % 16))              // convert BCD --> INT
#define INT16_TO_BCD(A)  ((((((A) % 100) / 10) * 16 + ((A) % 10))) | (((((((A) / 100) % 100) / 10) * 16 + (((A) / 100) % 10))) << 8))   // convert INT16 --> BCD

#endif
