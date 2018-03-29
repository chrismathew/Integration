/****************************************************************************
 * TIME WARNER CABLE, INC. CONFIDENTIAL INFORMATION
 *
 * Copyright (c) 2007 Time Warner Cable, Inc.  All Rights Reserved.
 * Unauthorized reproduction, transmission, or distribution of
 * this software is a violation of applicable laws.
 *
 ****************************************************************************
 * Department:  Identity Management
 *
 * File Name:   FileUtils.java  
 * Description: file utility functions
 * @author:     takadiri
 * @version:    1.0
 * @date:       Oct 2, 2007
 ***************************************************************************
 * @author:     rbadhwar
 * @version:    1.1
 * @date:       Aug 30, 2010 
 * Note : Updated to resolve jtest static analysis issues/optimizations and misc
 ****************************************************************************/
package com.twc.eis.lib.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil
{
    public static String fileToString(String fileName) throws FileNotFoundException, IOException,
            Exception
    {
        String s = null;

        if (fileName != null)
        {
            FileReader f = null;
            BufferedReader in = null;

            StringBuffer buf = new StringBuffer();

            try
            {
                f = new FileReader(fileName);

                if (f != null) // parasoft-suppress BD.PB.CC "working as designed."
                {
                    in = new BufferedReader(f);

                    if (in != null) // parasoft-suppress BD.PB.CC "working as designed."
                    {
                        while ((s = in.readLine()) != null)
                        {
                            buf.append(s);
                            buf.append('\n');
                        }
                    }
                }

                if (buf != null)
                {
                    s = buf.toString();
                }
            } catch (FileNotFoundException e)
            {
                throw new FileNotFoundException(e.getMessage());
            } catch (IOException e)
            {
                throw new IOException(e.getMessage());
            } catch (Exception e)
            {
                throw new Exception(e.getMessage());
            } finally
            {
                if (in != null)
                {
                    in.close();
                }
                if (f != null)
                {
                    f.close();
                }
                if (buf != null)
                {
                    buf.setLength(0);
                }
            }
        }

        return s;
    }

    public static void stringToFile(String s, String fileName) throws IOException
    {
        FileWriter fileWriter = null;
        BufferedWriter out = null;

        try
        {
            if (fileName != null && s != null)
            {
                fileWriter = new FileWriter(fileName);
                out = new BufferedWriter(fileWriter);
                out.write(s, 0, s.length());
            } else
            {
                throw new IOException("Invalid Input");
            }
        } catch (IOException e)
        {
            throw new IOException(e);
        } finally
        {
            try
            {
                if (fileWriter != null)
                {
                    fileWriter.close();
                }
                if (out != null)
                {
                    out.close();
                }
            } catch (Exception e)
            {
            }
        }

    }

    public static String toPathName(String path, String fileName)
    {
        return path + (path.endsWith(File.separator) ? fileName : File.separator + fileName);
    }

    public static String getSeparator()
    {
        return File.separator;
    }

    public static String getPathSeparator()
    {
        return File.pathSeparator;
    }

    public static void main(String[] args) throws Exception
    {
        //FileUtil fu = new FileUtil();
        //System.out.println(fu.fileToString("makefile"));
    }

}
