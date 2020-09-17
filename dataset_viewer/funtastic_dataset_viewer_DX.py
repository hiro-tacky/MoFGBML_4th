import os
from datetime import datetime
import pandas as pd
import matplotlib.pyplot as plt

print("dataset at UCI, cahnge \"***.data\" -> \"***.csv\" ")
print("input csv file must not have header")
print("class data must be most right column")
print("make 'dataset_view' class Obuject, and call menber func you need")

cmap = plt.get_cmap("tab10") # 色のlist

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

class dataset_view():
    def __init__(self):
        print('dataset name:')
        self.filename = input()
        self.df = pd.read_csv("./datsename/" + self.filename + ".csv", header=None)
        self.attribute_num = len(self.df.columns) - 1
        self.classname = {}
        for name in self.df[self.attribute_num]:
            try:
                self.classname[name] += 1
            except:
                self.classname[name] = 1
        self.plot1()
        self.plot2()
        self.plot3()
        self.plot4()
        self.plot5()
                
    #積み上げ棒グラフ
    def plot1(self):
        fig = plt.figure(figsize = (24, ((self.attribute_num+2)/3)*6))
        fig.suptitle(self.filename, size = 24)        
        ax = []
        labels = self.classname.keys()
        #ヒストグラム分割数
        bins_num = 20
        for i in range(self.attribute_num):
            ax = fig.add_subplot((self.attribute_num+2)/3, 3, i+1)
            df_each_class = []
            for name in self.classname.keys():
                df_each_class.append(self.df[self.df[self.attribute_num] == name][i])
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
        fig = plt.figure(figsize = (24, ((self.attribute_num+2)/3)*6))
        fig.suptitle(self.filename, size = 24)        
        ax = []
        #ヒストグラム分割数
        bins_num = 15
        for i in range(self.attribute_num):
            ax = fig.add_subplot((self.attribute_num+2)/3, 3, i+1)
            for name in self.classname.keys():
                buf= self.df[self.df[self.attribute_num] == name][i]
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
            fig = plt.figure(figsize = (24, ((self.attribute_num+2)/3)*6))
            fig.suptitle(self.filename, size = 24)        
            ax = []
            for i in range(self.attribute_num):
                ax = fig.add_subplot((self.attribute_num+2)/3, 3, i+1)
                buf= self.df[self.df[self.attribute_num] == name][i]
                ax.hist(buf, bins = bins_num, label = name, color = cmap(c))
                ax.set_title("class name: " + str(name) + "  attribute dim: " + str(i))
                ax.grid(True)
                ax.set_xlabel("value")
                ax.set_ylabel("number")
            figname =  self.filename.replace(".csv", "")
            figSave(fig, filename = figname + "_" + str(name) + "_P3",  datesetname = figname)
            
        
    #各クラスについてグラフ カーネル密度推定
    def plot4(self):
        fig = plt.figure(figsize = (24, ((self.attribute_num+2)/3)*6))
        fig.suptitle(self.filename, size = 24)        
        ax = []
        for i in range(self.attribute_num):
            ax = fig.add_subplot((self.attribute_num+2)/3, 3, i+1)
            for c, name in enumerate(self.classname.keys()):
                buf= self.df[self.df[self.attribute_num] == name][i]
                buf.plot(kind='kde', color = cmap(c), label = name)
            ax.set_title("attribute dim: " + str(i))
            ax.grid(True)
            ax.set_xlabel("value")
            ax.set_ylabel("number")            
            ax.legend()
        figname =  self.filename.replace(".csv", "")
        figSave(fig, filename = figname + "_P4",  datesetname = figname)
        
    def plot5(self):
        fig = plt.figure(figsize = (8, 6))
        fig.suptitle(self.filename, size = 24)        
        ax = fig.add_subplot(1, 1, 1)
        ax.pie(self.classname.values(), labels = self.classname.keys(), autopct="%1.1f%%")
        figname =  self.filename.replace(".csv", "")
        figSave(fig, filename = figname + "_P5",  datesetname = figname)