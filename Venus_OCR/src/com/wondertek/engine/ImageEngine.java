package com.wondertek.engine;

import com.ym.idcard.reg.NativeImage;

public class ImageEngine
{
  public static final int IMG_COMPONENT_GRAY = 1;
  public static final int IMG_COMPONENT_RGB = 3;
  public static final int IMG_FMT_BMP = 1;
  public static final int IMG_FMT_JPG = 2;
  public static final int IMG_FMT_UNK = 0;
  public static final int RET_ERR_ARG = -2;
  public static final int RET_ERR_MEM = -3;
  public static final int RET_ERR_PTR = -1;
  public static final int RET_ERR_UNK = 0;
  public static final int RET_OK = 1;
  protected long mEngine = 0L;
  protected NativeImage mNativeImage = new NativeImage();
  
  public ImageEngine() {
	  this.mEngine = mNativeImage.createEngine();
  }
  

  public void finalize()
  {
    if ((this.mNativeImage != null) && (this.mEngine != 0L))
    {
      this.mNativeImage.freeImage(this.mEngine);
      if (this.mEngine != 0) mNativeImage.closeEngine(mEngine);
      this.mEngine = 0L;
    }
  }

  public int getComponent()
  {
    if (this.mNativeImage != null)
      return this.mNativeImage.getImageComponent(this.mEngine);
    return 0;
  }

  public long getDataEx()
  {
    if (this.mNativeImage != null)
      return this.mNativeImage.getImageDataEx(this.mEngine);
    return 0L;
  }

  public int getHeight()
  {
    if (this.mNativeImage != null)
      return this.mNativeImage.getImageHeight(this.mEngine);
    return 0;
  }

  public int getWidth()
  {
    if (this.mNativeImage != null)
      return this.mNativeImage.getImageWidth(this.mEngine);
    return 0;
  }

  public boolean init(int paramInt1, int paramInt2)
  {
    return (this.mNativeImage != null) && (this.mNativeImage.initImage(this.mEngine, paramInt1, paramInt2) == 1);
  }

  public boolean load(byte[] paramArrayOfByte)
  {
	 int ret = this.mNativeImage.loadmemjpg(this.mEngine, paramArrayOfByte, paramArrayOfByte.length);
	 return (this.mNativeImage != null) && (ret == 1);
  }
}