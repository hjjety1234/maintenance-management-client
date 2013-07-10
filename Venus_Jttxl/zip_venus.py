#-*-coding:utf-8 -*-

import os
import zipfile

def zipdir(path, zip):
    for root, dirs, files in os.walk(path):
        for file in files:
            arcpath = root.replace(os.getcwd()+ "\\venus\\module\\", "")
            zip.write(os.path.join(root, file), os.path.join(arcpath, file))

if __name__ == "__main__":
    # 解析boot.lua文件，获取业务模块名称
    bootfile = os.getcwd() + "\\venus\\module\\boot.lua"
    filecontent = open(bootfile, "r").readline().replace("'", "").strip()
    modulename = filecontent.split("=")[1].strip()

    # 压缩业务代码
    modulepath = os.getcwd() + "\\venus\\module\\" + modulename
    print(modulepath)
    zipmodule = zipfile.ZipFile(modulepath + ".zip", "w", zipfile.ZIP_STORED)
    zipdir(modulepath, zipmodule)
    zipmodule.close()

    # 创建venus.zip压缩文件
    venusZipPath = os.getcwd() + "\\assets\\venus.zip"
    venusZip = zipfile.ZipFile(venusZipPath, "w", zipfile.ZIP_DEFLATED)

    # 添加lib2文件夹
    lib2path = os.getcwd() + "\\venus\\lib2_debug\\"
    for root, dirs, files in os.walk(lib2path):
        for file in files:
            arcpath = root.replace(os.getcwd()+ "\\venus\\lib2_debug\\", "lib2\\")
            venusZip.write(os.path.join(root, file), os.path.join(arcpath, file))

    # 添加framework.dat文件
    framework = os.getcwd() + "\\venus\\framework_log.dat"
    venusZip.write(framework, "framework.dat")

    # 添加module文件夹
    module = os.getcwd() + "\\venus\\module\\"
    for root, dirs, files in os.walk(module):
        for file in files:
            arcpath = root.replace(os.getcwd()+ "\\venus\\module\\", "module\\")
            if (arcpath.startswith("module\\" + modulename) == False):
                venusZip.write(os.path.join(root, file), os.path.join(arcpath, file))
    venusZip.write(module + "\\" + modulename, "module\\" + modulename)
    venusZip.close()
