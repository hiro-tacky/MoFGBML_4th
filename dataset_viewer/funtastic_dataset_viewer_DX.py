import os
from datetime import datetime
import pandas as pd
import matplotlib.pyplot as plt

print("dataset at UCI, cahnge \"***.data\" -> \"***.csv\" ")
print("input csv file must not have header")
print("class data must be most right column")
print("make 'dataset_view' class Obuject, and call menber func you need")

cmap = plt.get_cmap("tab10") # 色のlist
my_path = os.getcwd()

def figSave(fig, filename = None, datesetname = "others"):
    if filename == None:
        print("image file name:")
        filename = input()
    dirname = datesetname + "/"
    my_path = os.getcwd()
    os.makedirs(dirname, exist_ok=True)
    now = datetime.now()
    buf = filename + "_{0:%Y%m%d%H%M%S}.png".format(now)
    fig.savefig(os.path.join(my_path + '\\' + datesetname, buf), transparent=False)
    
def SaveFig(fig, filePath, filename = None):
    """画像を保存する
    入力:figureオブジェクト, ファイル名, データセット名"""
    if filename == None:
        print("image file name:")
        filename = input()
    os.makedirs(filePath, exist_ok=True)
    now = datetime.now()
    imageName = filename + "_{0:%Y%m%d%H%M%S}_{1:%f}.png".format(now, now)
    fig.savefig(my_path + "/" + filePath + "/" + imageName, transparent=False)     

class dataset_view():
    def __init__(self):
        print('dataset name:')
        self.filename = input()
        df_original = pd.read_csv("./dataset/" + self.filename + ".csv", header=None)
        self.attributeNum = len(df_original.columns) - 1
        self.classname = {}
        for name in df_original[self.attributeNum]:
            try:
                self.classname[name] += 1
            except:
                self.classname[name] = 1
                
        classList_buf = df_original.iloc[:, self.attributeNum]
        df_buf = df_original.iloc[:, 0:self.attributeNum]
        self.df = pd.concat([(df_buf - df_buf.min()) / (df_buf.max() - df_buf.min()), classList_buf], axis=1, join='inner') #正規化されたdetaframe
        
        # self.plot1()
        # self.plot2()
        # self.plot3()
        self.plot4()
        # self.plot5()
                
    #積み上げ棒グラフ
    def plot1(self):
        fig = plt.figure(figsize = (24, ((self.attributeNum+2)/3)*6))
        fig.suptitle(self.filename, size = 24)        
        ax = []
        labels = self.classname.keys()
        #ヒストグラム分割数
        bins_num = 20
        for i in range(self.attributeNum):
            ax = fig.add_subplot((self.attributeNum+2)/3, 3, i+1)
            df_each_class = []
            for name in self.classname.keys():
                df_each_class.append(self.df[self.df[self.attributeNum] == name][i])
            ax.hist(df_each_class, bins = bins_num, stacked=True, label = labels)
            ax.set_title("attribute dim: " + str(i))
            ax.grid(True)
            ax.legend()
            ax.set_xlabel("value")
            ax.set_ylabel("number")            
        figname =  self.filename.replace(".csv", "")
        figSave(fig, filename = figname + "_P1", datesetname = figname)
    
    #重ね棒グラフ
    def plot2(self):
        fig = plt.figure(figsize = (24, ((self.attributeNum+2)/3)*6))
        fig.suptitle(self.filename, size = 24)        
        ax = []
        #ヒストグラム分割数
        bins_num = 15
        for i in range(self.attributeNum):
            ax = fig.add_subplot((self.attributeNum+2)/3, 3, i+1)
            for name in self.classname.keys():
                buf= self.df[self.df[self.attributeNum] == name][i]
                ax.hist(buf, bins = bins_num, label = name, alpha = 0.5, histtype="stepfilled")
            ax.set_title("attribute dim: " + str(i))
            ax.grid(True)
            ax.set_xlabel("value")
            ax.set_ylabel("number")            
            ax.legend()
        figname =  self.filename.replace(".csv", "")
        figSave(fig, filename = figname + "_P2", datesetname = figname )
        
    #各クラスについてグラフ
    def plot3(self):
        #ヒストグラム分割数
        bins_num = 15
        for c, name in enumerate(self.classname.keys()):
            fig = plt.figure(figsize = (24, ((self.attributeNum+2)/3)*6))
            fig.suptitle(self.filename, size = 24)        
            ax = []
            for i in range(self.attributeNum):
                ax = fig.add_subplot((self.attributeNum+2)/3, 3, i+1)
                buf= self.df[self.df[self.attributeNum] == name][i]
                ax.hist(buf, bins = bins_num, label = name, color = cmap(c))
                ax.set_title("class name: " + str(name) + "  attribute dim: " + str(i))
                ax.grid(True)
                ax.set_xlabel("value")
                ax.set_ylabel("number")
            figname =  self.filename.replace(".csv", "")
            figSave(fig, filename = figname + "_" + str(name) + "_P3",  datesetname = figname)
            
        
    #各クラスについてグラフ カーネル密度推定
    def plot4(self):
        for i in range(self.attributeNum):
            fig = plt.figure(figsize = (8,6))
            ax = fig.add_subplot(1, 1, 1)
            fig.suptitle(self.filename, size = 24)        
            ax = []
            ax = fig.add_subplot(1,1,1)
            for c, name in enumerate(self.classname.keys()):
                buf= self.df[self.df[self.attributeNum] == name][i]
                buf.plot(kind='kde', color = cmap(c), label = name)
            ax.set_title("attribute dim: " + str(i), fontsize = 22)
            ax.grid(True)
            ax.set_xlim(-0.05, 1.05)
            ax.set_xlabel("value", fontsize = 22)
            ax.set_ylabel("Density", fontsize = 22)            
            ax.legend(loc='upper right', fontsize='x-large')
            ax.tick_params(axis="x", labelsize=16)
            ax.tick_params(axis="y", labelsize=16)  
            SaveFig(fig, "./" + self.filename + "/dim/",  filename = self.filename + "dim_" + str(i))
            plt.close('all')
        
    def plot5(self):
        fig = plt.figure(figsize = (8, 6))
        fig.suptitle(self.filename, size = 24)        
        ax = fig.add_subplot(1, 1, 1)
        ax.pie(self.classname.values(), labels = self.classname.keys(), autopct="%1.1f%%")
        figname =  self.filename.replace(".csv", "")
        figSave(fig, filename = figname + "_P5",  datesetname = figname)