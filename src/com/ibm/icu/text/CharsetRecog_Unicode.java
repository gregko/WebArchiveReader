/*
 *******************************************************************************
 * Copyright (C) 1996-2012, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 *
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
 * This class matches UTF-16 and UTF-32, both big- and little-endian. The
 * BOM will be used if it is present.
 */
abstract class CharsetRecog_Unicode extends CharsetRecognizer {

    /* (non-Javadoc)
     * @see com.ibm.icu.text.CharsetRecognizer#getName()
     */
    abstract String getName();

    /* (non-Javadoc)
     * @see com.ibm.icu.text.CharsetRecognizer#match(com.ibm.icu.text.CharsetDetector)
     */
    abstract CharsetMatch match(CharsetDetector det);
    
    static class CharsetRecog_UTF_16_BE extends CharsetRecog_Unicode
    {
        String getName()
        {
            return "UTF-16BE";
        }
        
        CharsetMatch match(CharsetDetector det)
        {
            byte[] input = det.fRawInput;
            
            if (input.length>=2 && ((input[0] & 0xFF) == 0xFE && (input[1] & 0xFF) == 0xFF)) {
                int confidence = 100;
                return new CharsetMatch(det, this, confidence);
            }
            
            // TODO: Do some statistics to check for unsigned UTF-16BE
            return null;
        }
    }
    
    static class CharsetRecog_UTF_16_LE extends CharsetRecog_Unicode
    {
        String getName()
        {
            return "UTF-16LE";
        }
        
        CharsetMatch match(CharsetDetector det)
        {
            byte[] input = det.fRawInput;
            
            if (input.length >= 2 && ((input[0] & 0xFF) == 0xFF && (input[1] & 0xFF) == 0xFE))
            {
               // An LE BOM is present.
               if (input.length>=4 && input[2] == 0x00 && input[3] == 0x00) {
                   // It is probably UTF-32 LE, not UTF-16
                   return null;
               }
               int confidence = 100;
               return new CharsetMatch(det, this, confidence);
            }        
            
            // TODO: Do some statistics to check for unsigned UTF-16LE
            return null;
        }
    }
    
    static abstract class CharsetRecog_UTF_32 extends CharsetRecog_Unicode
    {
        abstract int getChar(byte[] input, int index);
        
        abstract String getName();
        
        CharsetMatch match(CharsetDetector det)
        {
            byte[] input   = det.fRawInput;
            int limit      = (det.fRawLength / 4) * 4;
            int numValid   = 0;
            int numInvalid = 0;
            boolean hasBOM = false;
            int confidence = 0;
            
            if (limit==0) {
                return null;
            }
            if (getChar(input, 0) == 0x0000FEFF) {
                hasBOM = true;
            }
            
            for(int i = 0; i < limit; i += 4) {
                int ch = getChar(input, i);
                
                if (ch < 0 || ch >= 0x10FFFF || (ch >= 0xD800 && ch <= 0xDFFF)) {
                    numInvalid += 1;
                } else {
                    numValid += 1;
                }
            }
            
            
            // Cook up some sort of confidence score, based on presence of a BOM
            //    and the existence of valid and/or invalid multi-byte sequences.
            if (hasBOM && numInvalid==0) {
                confidence = 100;
            } else if (hasBOM && numValid > numInvalid*10) {
                confidence = 80;
            } else if (numValid > 3 && numInvalid == 0) {
                confidence = 100;            
            } else if (numValid > 0 && numInvalid == 0) {
                confidence = 80;
            } else if (numValid > numInvalid*10) {
                // Probably corrupt UTF-32BE data.  Valid sequences aren't likely by chance.
                confidence = 25;
            }
            
            return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
        }
    }
    
    static class CharsetRecog_UTF_32_BE extends CharsetRecog_UTF_32
    {
        int getChar(byte[] input, int index)
        {
            return (input[index + 0] & 0xFF) << 24 | (input[index + 1] & 0xFF) << 16 |
                   (input[index + 2] & 0xFF) <<  8 | (input[index + 3] & 0xFF);
        }
        
        String getName()
        {
            return "UTF-32BE";
        }
    }

    
    static class CharsetRecog_UTF_32_LE extends CharsetRecog_UTF_32
    {
        int getChar(byte[] input, int index)
        {
            return (input[index + 3] & 0xFF) << 24 | (input[index + 2] & 0xFF) << 16 |
                   (input[index + 1] & 0xFF) <<  8 | (input[index + 0] & 0xFF);
        }
        
        String getName()
        {
            return "UTF-32LE";
        }
    }
}
