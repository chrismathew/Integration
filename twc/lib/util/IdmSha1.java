/*****************************************************************************
 * TIME WARNER CABLE, INC. CONFIDENTIAL INFORMATION 
 * Copyright (c) 2007 Time Warner Cable, Inc. All Rights Reserved. 
 * Unauthorized reproduction,transmission, or distribution of this software 
 * is a violation of applicable laws.
****************************************************************************
 * Department:  Identity Management
 *
 * File Name:   IIdmSha1.java   
 * Description: Sha1 Impl for Identity Management
 * @author:     rbadhwar
 * @version:    1.0
 * @date:       Aug 21, 2007
 *
 ****************************************************************************/

package com.twc.eis.lib.util;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;

public class IdmSha1 implements IIdmSha1
{
    private int    digest[] = new int[5];
    
    private long   count;
    
    public byte[]  digestBits;
    
    public boolean digestValid;
    
    private IdmSha1()
    {
        digest = new int[5]; // message digest
        count = 0;
        if (block == null)
        {
            block = new int[16]; // sha data buffer
        }
        digestBits = new byte[20];
        digestValid = false;
    }
    
    public synchronized static IIdmSha1 getInstance()
    {
        IIdmSha1 sha = null;
        
        //if (sha == null)
        //{
            sha = new IdmSha1();
       // }
        return sha;
    }
    
    private synchronized void shaUpdate(byte input[], int offset, int len)
    {
        for (int i = 0; i < len; i++)
        {
            shaUpdate(input[i + offset]);
        }
    }
    
    private synchronized void shaUpdate(byte input[])
    {
        shaUpdate(input, 0, input.length);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.twc.eis.lib.util.IIdmSha1#shaUpdateAsc(java.lang.String)
     */
    public void shaUpdateAsc(String input) throws Exception
    {
        
        if (input.equals(null))
        {
            throw new Exception("Invalid Input");
        }
        
        int i, len;
        byte x;
        
        len = input.length();
        for (i = 0; i < len; i++)
        {
            x = (byte) (input.charAt(i) & 0xff);
            shaUpdate(x);
        }
    }
    
    private int block[] = new int[16];
    
    private int blockI;
    
    // 32 bit rotate
    final int shaRot32(int value, int bits)
    {
        int q = (value << bits) | (value >>> (32 - bits));
        return q;
    }
    
    final int shaBlk0(int i)
    {
        block[i] = (shaRot32(block[i], 24) & 0xFF00FF00) | (shaRot32(block[i], 8) & 0x00FF00FF);
        return block[i];
    }
    
    final int shaBlk(int i)
    {
        block[i & 15] = shaRot32(block[(i + 13) & 15] ^ block[(i + 8) & 15] ^ block[(i + 2) & 15]
                                                                                    ^ block[i & 15], 1);
        return (block[i & 15]);
    }
    
    final void Func0(int data[], int v, int w, int x, int y, int z, int i)
    {
        data[z] += ((data[w] & (data[x] ^ data[y])) ^ data[y]) + shaBlk0(i) + 0x5A827999
        + shaRot32(data[v], 5);
        data[w] = shaRot32(data[w], 30);
    }
    
    final void Func1(int data[], int v, int w, int x, int y, int z, int i)
    {
        data[z] += ((data[w] & (data[x] ^ data[y])) ^ data[y]) + shaBlk(i) + 0x5A827999
        + shaRot32(data[v], 5);
        data[w] = shaRot32(data[w], 30);
    }
    
    final void Func2(int data[], int v, int w, int x, int y, int z, int i)
    {
        data[z] += (data[w] ^ data[x] ^ data[y]) + shaBlk(i) + 0x6ED9EBA1 + shaRot32(data[v], 5);
        data[w] = shaRot32(data[w], 30);
    }
    
    final void Func3(int data[], int v, int w, int x, int y, int z, int i)
    {
        data[z] += (((data[w] | data[x]) & data[y]) | (data[w] & data[x])) + shaBlk(i) + 0x8F1BBCDC
        + shaRot32(data[v], 5);
        data[w] = shaRot32(data[w], 30);
    }
    
    final void Func4(int data[], int v, int w, int x, int y, int z, int i)
    {
        data[z] += (data[w] ^ data[x] ^ data[y]) + shaBlk(i) + 0xCA62C1D6 + shaRot32(data[v], 5);
        data[w] = shaRot32(data[w], 30);
    }
    
    int _digest[] = new int[5];
    
    // do SHA transformation by hashing a single 512 bit block.
    void shaTransform()
    {
        
        /* copy digest to temp variables */
        _digest[0] = digest[0];
        _digest[1] = digest[1];
        _digest[2] = digest[2];
        _digest[3] = digest[3];
        _digest[4] = digest[4];
        
        Func0(_digest, 0, 1, 2, 3, 4, 0);
        Func0(_digest, 4, 0, 1, 2, 3, 1);
        Func0(_digest, 3, 4, 0, 1, 2, 2);
        Func0(_digest, 2, 3, 4, 0, 1, 3);
        Func0(_digest, 1, 2, 3, 4, 0, 4);
        Func0(_digest, 0, 1, 2, 3, 4, 5);
        Func0(_digest, 4, 0, 1, 2, 3, 6);
        Func0(_digest, 3, 4, 0, 1, 2, 7);
        Func0(_digest, 2, 3, 4, 0, 1, 8);
        Func0(_digest, 1, 2, 3, 4, 0, 9);
        Func0(_digest, 0, 1, 2, 3, 4, 10);
        Func0(_digest, 4, 0, 1, 2, 3, 11);
        Func0(_digest, 3, 4, 0, 1, 2, 12);
        Func0(_digest, 2, 3, 4, 0, 1, 13);
        Func0(_digest, 1, 2, 3, 4, 0, 14);
        Func0(_digest, 0, 1, 2, 3, 4, 15);
        Func1(_digest, 4, 0, 1, 2, 3, 16);
        Func1(_digest, 3, 4, 0, 1, 2, 17);
        Func1(_digest, 2, 3, 4, 0, 1, 18);
        Func1(_digest, 1, 2, 3, 4, 0, 19);
        Func2(_digest, 0, 1, 2, 3, 4, 20);
        Func2(_digest, 4, 0, 1, 2, 3, 21);
        Func2(_digest, 3, 4, 0, 1, 2, 22);
        Func2(_digest, 2, 3, 4, 0, 1, 23);
        Func2(_digest, 1, 2, 3, 4, 0, 24);
        Func2(_digest, 0, 1, 2, 3, 4, 25);
        Func2(_digest, 4, 0, 1, 2, 3, 26);
        Func2(_digest, 3, 4, 0, 1, 2, 27);
        Func2(_digest, 2, 3, 4, 0, 1, 28);
        Func2(_digest, 1, 2, 3, 4, 0, 29);
        Func2(_digest, 0, 1, 2, 3, 4, 30);
        Func2(_digest, 4, 0, 1, 2, 3, 31);
        Func2(_digest, 3, 4, 0, 1, 2, 32);
        Func2(_digest, 2, 3, 4, 0, 1, 33);
        Func2(_digest, 1, 2, 3, 4, 0, 34);
        Func2(_digest, 0, 1, 2, 3, 4, 35);
        Func2(_digest, 4, 0, 1, 2, 3, 36);
        Func2(_digest, 3, 4, 0, 1, 2, 37);
        Func2(_digest, 2, 3, 4, 0, 1, 38);
        Func2(_digest, 1, 2, 3, 4, 0, 39);
        Func3(_digest, 0, 1, 2, 3, 4, 40);
        Func3(_digest, 4, 0, 1, 2, 3, 41);
        Func3(_digest, 3, 4, 0, 1, 2, 42);
        Func3(_digest, 2, 3, 4, 0, 1, 43);
        Func3(_digest, 1, 2, 3, 4, 0, 44);
        Func3(_digest, 0, 1, 2, 3, 4, 45);
        Func3(_digest, 4, 0, 1, 2, 3, 46);
        Func3(_digest, 3, 4, 0, 1, 2, 47);
        Func3(_digest, 2, 3, 4, 0, 1, 48);
        Func3(_digest, 1, 2, 3, 4, 0, 49);
        Func3(_digest, 0, 1, 2, 3, 4, 50);
        Func3(_digest, 4, 0, 1, 2, 3, 51);
        Func3(_digest, 3, 4, 0, 1, 2, 52);
        Func3(_digest, 2, 3, 4, 0, 1, 53);
        Func3(_digest, 1, 2, 3, 4, 0, 54);
        Func3(_digest, 0, 1, 2, 3, 4, 55);
        Func3(_digest, 4, 0, 1, 2, 3, 56);
        Func3(_digest, 3, 4, 0, 1, 2, 57);
        Func3(_digest, 2, 3, 4, 0, 1, 58);
        Func3(_digest, 1, 2, 3, 4, 0, 59);
        Func4(_digest, 0, 1, 2, 3, 4, 60);
        Func4(_digest, 4, 0, 1, 2, 3, 61);
        Func4(_digest, 3, 4, 0, 1, 2, 62);
        Func4(_digest, 2, 3, 4, 0, 1, 63);
        Func4(_digest, 1, 2, 3, 4, 0, 64);
        Func4(_digest, 0, 1, 2, 3, 4, 65);
        Func4(_digest, 4, 0, 1, 2, 3, 66);
        Func4(_digest, 3, 4, 0, 1, 2, 67);
        Func4(_digest, 2, 3, 4, 0, 1, 68);
        Func4(_digest, 1, 2, 3, 4, 0, 69);
        Func4(_digest, 0, 1, 2, 3, 4, 70);
        Func4(_digest, 4, 0, 1, 2, 3, 71);
        Func4(_digest, 3, 4, 0, 1, 2, 72);
        Func4(_digest, 2, 3, 4, 0, 1, 73);
        Func4(_digest, 1, 2, 3, 4, 0, 74);
        Func4(_digest, 0, 1, 2, 3, 4, 75);
        Func4(_digest, 4, 0, 1, 2, 3, 76);
        Func4(_digest, 3, 4, 0, 1, 2, 77);
        Func4(_digest, 2, 3, 4, 0, 1, 78);
        Func4(_digest, 1, 2, 3, 4, 0, 79);
        
        // Add the temp variables back into the digest[]
        digest[0] += _digest[0];
        digest[1] += _digest[1];
        digest[2] += _digest[2];
        digest[3] += _digest[3];
        digest[4] += _digest[4];
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.twc.eis.lib.util.IIdmSha1#shaInit()
     */
    public void shaInit()
    {
        digest[0] = 0x67452301;
        digest[1] = 0xEFCDAB89;
        digest[2] = 0x98BADCFE;
        digest[3] = 0x10325476;
        digest[4] = 0xC3D2E1F0;
        count = 0;
        digestBits = new byte[20];
        digestValid = false;
        blockI = 0;
    }
    
    // update the SHA digest.
    private synchronized void shaUpdate(byte b)
    {
        int mask = (8 * (blockI & 3));
        count += 8;
        block[blockI >> 2] &= ~(0xff << mask);
        block[blockI >> 2] |= (b & 0xff) << mask;
        blockI++;
        if (blockI == 64)
        {
            shaTransform();
            blockI = 0;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.twc.eis.lib.util.IIdmSha1#shaFinal()
     */
    public void shaFinal()
    {
        byte bits[] = new byte[8];
        int i, j;
        for (i = 0; i < 8; i++)
        {
            bits[i] = (byte) ((count >>> (((7 - i) * 8))) & 0xff);
        }
        
        shaUpdate((byte) 128);
        while (blockI != 56)
        {
            shaUpdate((byte) 0);
            // This should cause a transform to happen.
        }
        shaUpdate(bits);
        for (i = 0; i < 20; i++)
        {
            digestBits[i] = (byte) ((digest[i >> 2] >> ((3 - (i & 3)) * 8)) & 0xff);
        }
        digestValid = true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.twc.eis.lib.util.IIdmSha1#shaDigestPrint()
     */
    public String shaDigestPrint()
    {
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < 20; i++)
        {
            char c1, c2;
            
            c1 = (char) ((digestBits[i] >>> 4) & 0xf);
            c2 = (char) (digestBits[i] & 0xf);
            c1 = (char) ((c1 > 9) ? 'a' + (c1 - 10) : '0' + c1);
            c2 = (char) ((c2 > 9) ? 'a' + (c2 - 10) : '0' + c2);
            output.append(c1);
            output.append(c2);
        }
        return output.toString();
    }
}
