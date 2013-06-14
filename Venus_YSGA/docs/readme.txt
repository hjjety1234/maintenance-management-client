1.此project是所有基于Android.sln.3.2.0(cnlive分支)的安徽移动项目升级所参考的project。
2.升级步骤
（1）引擎添加好新功能或 bug fix后，会通知项目人员开始Force自构建 Project Report for PJ-3_2_0_640x960_ahyd。
（2）项目人员从\\192.168.1.124\publisher\PJ-3_2_0_640x960_ahyd获取最新project及framework.dat(建议取framework_log.dat,重命名为framework.dat)。
（3）开始比对（2）中的project和项目project。
   合并docs(全部合并过去)、lib（看项目是否需要）、libs（看项目是否需要）、res（看项目是否需要）、src（每个文件都要认真比较，遇到//add pj时以项目为准，其余全部合并过去）、venus（看项目需要哪些.so文件，有更新的全部合并过去）、
   .classpath（看项目是否需要）、AndroidManifest.xml（看项目是否需要）、build.xml（看项目是否需要）、version.txt（全部合并过去）。
（4）手动打包。删除gen文件夹，会自动重新生成新的。比对gen文件夹下的R.java和src文件夹下的R.java。注意package com.wondertek.activity;这句保持不变，其余全部合并过去。
（5）提交project代码，自构建出包。注意：新加的.so文件需通知自构建维护人员在相应自构建上添加。