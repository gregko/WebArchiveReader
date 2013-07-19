/**
*******************************************************************************
* Copyright (C) 2005-2012, International Business Machines Corporation and    *
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
 * Abstract class for recognizing a single charset.
 * Part of the implementation of ICU's CharsetDetector.
 * 
 * Each specific charset that can be recognized will have an instance
 * of some subclass of this class.  All interaction between the overall
 * CharsetDetector and the stuff specific to an individual charset happens
 * via the interface provided here.
 * 
 * Instances of CharsetDetector DO NOT have or maintain 
 * state pertaining to a specific match or detect operation.
 * The WILL be shared by multiple instances of CharsetDetector.
 * They encapsulate const charset-specific information.
 */
abstract class CharsetRecognizer {
    /**
     * Get the IANA name of this charset.
     * @return the charset name.
     */
    abstract String getName();
    
    /**
     * Get the ISO language code for this charset.
     * @return the language code, or <code>null</code> if the language cannot be determined.
     */
    public String getLanguage()
    {
        return null;
    }
    
    /**
     * Test the match of this charset with the input text data
     *      which is obtained via the CharsetDetector object.
     * 
     * @param det  The CharsetDetector, which contains the input text
     *             to be checked for being in this charset.
     * @return     A CharsetMatch object containing details of match
     *             with this charset, or null if there was no match.
     */
    abstract CharsetMatch  match(CharsetDetector det);

}
