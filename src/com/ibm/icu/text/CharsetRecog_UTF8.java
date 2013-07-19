/**
*******************************************************************************
* Copyright (C) 2005 - 2012, International Business Machines Corporation and  *
* others. All Rights Reserved.                                                *
*******************************************************************************
 ICU License - ICU 1.8.1 and later

 COPYRIGHT AND PERMISSION NOTICE

 Copyright (c) 1995-2013 International Business Machines Corporation and others

 All rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, provided that the above copyright notice(s) and this permission notice appear in all copies of the Software and that both the above copyright notice(s) and this permission notice appear in supporting documentation.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR HOLDERS INCLUDED IN THIS NOTICE BE LIABLE FOR ANY CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

 Except as contained in this notice, the name of a copyright holder shall not be used in advertising or otherwise to promote the sale, use or other dealings in this Software without prior written authorization of the copyright holder.
*/
package com.ibm.icu.text;

/**
 * Charset recognizer for UTF-8
 */
class CharsetRecog_UTF8 extends CharsetRecognizer {

    String getName() {
        return "UTF-8";
    }

    /* (non-Javadoc)
     * @see com.ibm.icu.text.CharsetRecognizer#match(com.ibm.icu.text.CharsetDetector)
     */
    CharsetMatch match(CharsetDetector det) {
        boolean     hasBOM = false;
        int         numValid = 0;
        int         numInvalid = 0;
        byte        input[] = det.fRawInput;
        int         i;
        int         trailBytes = 0;
        int         confidence;
        
        if (det.fRawLength >= 3 && 
                (input[0] & 0xFF) == 0xef && (input[1] & 0xFF) == 0xbb && (input[2] & 0xFF) == 0xbf) {
            hasBOM = true;
        }
        
        // Scan for multi-byte sequences
        for (i=0; i<det.fRawLength; i++) {
            int b = input[i];
            if ((b & 0x80) == 0) {
                continue;   // ASCII
            }
            
            // Hi bit on char found.  Figure out how long the sequence should be
            if ((b & 0x0e0) == 0x0c0) {
                trailBytes = 1;                
            } else if ((b & 0x0f0) == 0x0e0) {
                trailBytes = 2;
            } else if ((b & 0x0f8) == 0xf0) {
                trailBytes = 3;
            } else {
                numInvalid++;
                if (numInvalid > 5) {
                    break;
                }
                trailBytes = 0;
            }
                
            // Verify that we've got the right number of trail bytes in the sequence
            for (;;) {
                i++;
                if (i>=det.fRawLength) {
                    break;
                }
                b = input[i];
                if ((b & 0xc0) != 0x080) {
                    numInvalid++;
                    break;
                }
                if (--trailBytes == 0) {
                    numValid++;
                    break;
                }
            }
                        
        }
        
        // Cook up some sort of confidence score, based on presense of a BOM
        //    and the existence of valid and/or invalid multi-byte sequences.
        confidence = 0;
        if (hasBOM && numInvalid==0) {
            confidence = 100;
        } else if (hasBOM && numValid > numInvalid*10) {
            confidence = 80;
        } else if (numValid > 3 && numInvalid == 0) {
            confidence = 100;            
        } else if (numValid > 0 && numInvalid == 0) {
            confidence = 80;
        } else if (numValid == 0 && numInvalid == 0) {
            // Plain ASCII.  
            confidence = 10;            
        } else if (numValid > numInvalid*10) {
            // Probably corruput utf-8 data.  Valid sequences aren't likely by chance.
            confidence = 25;
        }
        return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
    }

}
