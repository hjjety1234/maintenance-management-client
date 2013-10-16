package com.wondertek.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class FileUtil
{
  public static boolean copyFile(String paramString1, String paramString2)
  {
    try
    {
      File localFile1 = new File(paramString1);
      File localFile2 = new File(paramString2);
      FileInputStream localFileInputStream = new FileInputStream(localFile1);
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
      byte[] arrayOfByte = new byte[1024];
      while (true)
      {
        int i = localFileInputStream.read(arrayOfByte);
        if (i == -1)
        {
          // ((byte[])null);
          localFileInputStream.close();
          localFileOutputStream.close();
          return true;
        }
        localFileOutputStream.write(arrayOfByte, 0, i);
      }
    }
    catch (IOException localIOException)
    {
    }
    return false;
  }

  public static boolean deleteFile(String paramString)
  {
    File localFile = new File(paramString);
    if (localFile.exists())
      return localFile.delete();
    return true;
  }

  public static boolean exist(String paramString)
  {
    if (paramString == null)
      return false;
    return new File(paramString).exists();
  }

  public static void generateOtherImg(String paramString)
  {
  }

  public static byte[] getBytesFromFile(File paramFile)
    throws IOException
  {
//    FileInputStream localFileInputStream = new FileInputStream(paramFile);
//    long l = paramFile.length();
//    if (l > 2147483647L)
//    {
//      localFileInputStream.close();
//      throw new IOException("File is to large " + paramFile.getName());
//    }
//    byte[] arrayOfByte = new byte[(int)l];
//    int i = 0;
//    try
//    {
//      if (i < arrayOfByte.length)
//      {
//        int j = localFileInputStream.read(arrayOfByte, i, arrayOfByte.length - i);
//        if (j >= 0);
//      }
//      else
//      {
//        if (i >= arrayOfByte.length)
//          break label145;
//        throw new IOException("Could not completely read file " + paramFile.getName());
//      }
//    }
//    catch (Exception localException)
//    {
//      while (true)
//      {
//        int j;
//        return null;
//        i += j;
//      }
//      label145: localFileInputStream.close();
//      return arrayOfByte;
//    }
//    finally
//    {
//      // ((byte[])null);
//    }
	  return null;
  }

  public static byte[] getBytesFromFile(String paramString)
    throws IOException
  {
    return getBytesFromFile(new File(paramString));
  }

  public static int getFileLength(String paramString)
  {
    File localFile = new File(paramString);
    if (localFile.exists())
      return (int)localFile.length();
    return -1;
  }

  public static String getStrFromFile(File paramFile)
    throws IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    if (paramFile.length() > 2147483647L)
    {
      localFileInputStream.close();
      throw new IOException("File is to large " + paramFile.getName());
    }
    StringBuffer localStringBuffer = new StringBuffer();
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localFileInputStream, "GBK"));
    while (true)
    {
      String str1 = localBufferedReader.readLine();
      if (str1 == null)
      {
        String str2 = localStringBuffer.toString();
        localFileInputStream.close();
        return str2;
      }
      localStringBuffer.append(str1);
      localStringBuffer.append("\n");
    }
  }

  public static boolean isDirectory(String paramString)
  {
    return new File(paramString).isDirectory();
  }

  public static boolean makeSureDirExist(String paramString)
  {
    boolean bool = true;
    File localFile = new File(paramString);
    if (!localFile.exists())
      bool = localFile.mkdir();
    return bool;
  }

  public static boolean makeSureFileExist(String paramString)
  {
    boolean bool1 = true;
    File localFile = new File(paramString);
    if (!localFile.exists());
    try
    {
      boolean bool2 = localFile.createNewFile();
      bool1 = bool2;
      return bool1;
    }
    catch (IOException localIOException)
    {
    }
    return false;
  }

  public static int makeSureFileExistEx(String paramString)
  {
    int i = -1;
    File localFile = new File(paramString);
    if (!localFile.exists())
      try
      {
        boolean bool = localFile.createNewFile();
        if (bool)
          i = 0;
        return i;
      }
      catch (IOException localIOException)
      {
        return -1;
      }
    return (int)localFile.length();
  }

  public static String newImageName()
  {
    return UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
  }

  public void finalize()
  {
  }
}