package com.wondertek.vo;

public class IDCard
{
  private int recogStatus = -2;
  private String ymAddress;
  private String ymAuthority;
  private String ymBirth;
  private String ymCardNo;
  private String ymEthnicity;
  private String ymMemo;
  private String ymName;
  private String ymPeriod;
  private String ymSex;

  public String getAddress()
  {
    return this.ymAddress;
  }

  public String getAuthority()
  {
    return this.ymAuthority;
  }

  public String getBirth()
  {
    return this.ymBirth;
  }

  public String getCardNo()
  {
    return this.ymCardNo;
  }

  public String getEthnicity()
  {
    return this.ymEthnicity;
  }

  public String getMemo()
  {
    return this.ymMemo;
  }

  public String getName()
  {
    return this.ymName;
  }

  public String getPeriod()
  {
    return this.ymPeriod;
  }

  public int getRecogStatus()
  {
    return this.recogStatus;
  }

  public String getSex()
  {
    return this.ymSex;
  }

  public void setAddress(String paramString)
  {
    this.ymAddress = paramString;
  }

  public void setAuthority(String paramString)
  {
    this.ymAuthority = paramString;
  }

  public void setBirth(String paramString)
  {
    this.ymBirth = paramString;
  }

  public void setCardNo(String paramString)
  {
    this.ymCardNo = paramString;
  }

  public void setEthnicity(String paramString)
  {
    this.ymEthnicity = paramString;
  }

  public void setMemo(String paramString)
  {
    this.ymMemo = paramString;
  }

  public void setName(String paramString)
  {
    this.ymName = paramString;
  }

  public void setPeriod(String paramString)
  {
    this.ymPeriod = paramString;
  }

  public void setRecogStatus(int paramInt)
  {
    this.recogStatus = paramInt;
  }

  public void setSex(String paramString)
  {
    this.ymSex = paramString;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("������").append(this.ymName).append("\n");
    localStringBuffer.append("��ݺ��룺").append(this.ymCardNo).append("\n");
    localStringBuffer.append("�Ա�").append(this.ymSex).append("\n");
    localStringBuffer.append("���壺").append(this.ymEthnicity).append("\n");
    localStringBuffer.append("������").append(this.ymBirth).append("\n");
    localStringBuffer.append("סַ��").append(this.ymAddress).append("\n");
    localStringBuffer.append("ǩ�����أ�").append(this.ymAuthority).append("\n");
    localStringBuffer.append("��Ч���ޣ�").append(this.ymPeriod).append("\n");
    return localStringBuffer.toString();
  }
}