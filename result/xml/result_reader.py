
import xml.etree.ElementTree as ET
import matplotlib.pyplot as plt
import pandas as pd
import os
from datetime import datetime
import glob


#######  setting  #######
###############################################################################
#数値実験基本設定
trial_num = 30 #試行回数
gen_num = 5000 #世代数
my_path = os.getcwd()
FuzzyTypeID = {9:"rectangle", 7:"trapezoid", 3:"triangle", 4:"gaussian", 99:"multi"}
gen_list = range(100, 5000+1, 100)
trial_list = range(30)

DatasetList = {
    'iris': {'Patterns': 150, 'Attribute':4, 'Class':3},
    'wine': {'Patterns': 178, 'Attribute':13, 'Class':3},
    'phoneme': {'Patterns': 5404, 'Attribute':5, 'Class':2},
    'yeast': {'Patterns': 1484, 'Attribute':8, 'Class':10},
    'sonar': {'Patterns': 208, 'Attribute':60, 'Class':2},
    'pima': {'Patterns': 760, 'Attribute':8, 'Class':2},
    'vehicle': {'Patterns': 946, 'Attribute':18, 'Class':4},
    'bupa': {'Patterns': 345, 'Attribute':6, 'Class':2},
    'satimage': {'Patterns': 6435, 'Attribute':36, 'Class':6},
    'bal': {'Patterns': 630, 'Attribute':4, 'Class':3},
    'australian': {'Patterns': 690, 'Attribute':14, 'Class':2}
}
#plotするデフォルト設定
trial_plot = 1 #plotするtriaslのID
gen_plot = 5000 #plotする世代数
attri_plot = 0
#scatterの基本設定
default_size = 50
default_alpha = 0.01
#figureの基本設定
default_figsize = (8, 6)
default_titlesize = 18
###############################################################################

lim = lambda s, g: int((g-s)/10) if int((g-s)/10) != 0 else 1


def nodelist(root):
    for i, child in enumerate(root):
        if i<5 or i>len(list(root))-3:
            txt = "node" if child.text == None else str(child.text)
            print("{:3d}: {:20}{:10}{}".format(i, child.tag, str(txt), child.attrib))
        elif i>9 and i<13:
            print("         .")
        
def singleFig_set(title = None):
        """保存する画像(グラフ1つ)の基本設定
        入力:ファイル名
        返り値:figureオブジェクト"""
        fig = plt.figure(figsize = default_figsize)
        ax = fig.add_subplot(1, 1, 1)
        if title is not None:
            fig.suptitle(title, size = default_titlesize)        
        ax.grid(True)
        return fig
    
def multiFig_set(title = None):
        """保存する画像(グラフ複数)の基本設定
        入力:ファイル名
        返り値:figureオブジェクト"""
        fig = plt.figure(figsize = default_figsize)
        if title is not None:
            fig.suptitle(title, size = default_titlesize)    
        return fig        
    
def SaveFig(fig, filePath, filename = None):
    """画像を保存する
    入力:figureオブジェクト, ファイル名, データセット名"""
    if filename == None:
        print("image file name:")
        filename = input()
    os.makedirs(filePath, exist_ok=True)
    now = datetime.now()
    imageName = filename + "_{0:%Y%m%d%H%M%S}_{1:%f}.png".format(now, now)
    fig.savefig(my_path + '\\' + filePath + "\\" + imageName, transparent=False)     


class XML:
    """xmlファイルを読み込むためのスーパークラス"""
    def __init__(self, filename):
        self.tree = ET.parse(filename)
        self.root = self.tree.getroot()
        nodelist(self.root)
        self.CurrentElement = [] #木構造の現在参照しているノードの位置を保存する
        
    def down(self, id):
        """指定したIDの子要素を参照する"""
        self.CurrentElement.append(id)
        buf = self.root
        for i in self.CurrentElement:
            if not list(buf):
                print("NULL")
                self.up()
                return
            buf = buf[i]
        print("current node:" + buf.tag + "\n")
        nodelist(buf)
            
    def up(self):
        """指定したIDの親要素を参照する"""
        self.CurrentElement.pop(-1)
        buf = self.root
        for i in self.CurrentElement:
            buf = buf[i]
        print("current node:" + buf.tag + "\n")
        nodelist(buf)
            
    def root(self):
        """木の根にもどる"""
        self.CurrentElement.clear()
        nodelist(self.root)
        
        
class result(XML):
    """1つのxmlファイル(実験結果)用クラス"""
    def __init__(self, filename):
        super(result, self).__init__(filename)
        self.data = {}
        for i, trial in enumerate(self.root.findall('trial')):
            trial_buf = {}
            for population in trial:
                df = []
                for individual in population.findall('individual'):
                    buf = {}
                    for element in individual:
                        try:
                            tmp = float(element.text)
                        except:
                            tmp =  element.text
                        buf[element.tag] = tmp
                    df.append(buf)
                trial_buf[int(population.get('generation'))] = pd.DataFrame(df)
            self.data[i] = trial_buf
            
    def getDataFrame(self, trial, gen):
        return self.data[trial][gen]

    def setPopulation(self, trial, gen, ax, label_name, title = None, isDtst = True):
        """指定した世代，試行の結果をaxesオブジェクトにセット
        trial, gen:list型あるいはint型, isDtst:DtraかDtstの選択
        """
        gen_buf = [gen] if type(gen) is int else gen
        trial_buf = [trial] if type(trial) is int else trial
        y_ax = 'Dtst' if isDtst else 'Dtra'
        for gen_now in gen_buf:
            x, y = [], []
            for trial_now in trial_buf:
                data_buf = self.getDataFrame(trial_now, gen_now)
                data = data_buf[data_buf['f1'] != 1]
                x += list(data['f1'])
                y += list(data[y_ax])
            ax.scatter(x, y, label = label_name, s=default_size, alpha=default_alpha)
        if title is not None:
            ax.set_title(title)
        
    def plotPopulation(self, trial, gen, title = None, filename = "Population", isSave = True):
        """指定した世代，試行の結果を画像として出力
        trial, gen:list型あるいはint型
        """
        fig = singleFig_set(title)
        ax = fig.gca()
        self.setPopulation(trial, gen, ax, "Population", True)
        if isSave:
            SaveFig(fig, filename)
        else:
            plt.show()
            
        
    def setplotGenAve(self, gen, ax, label_name, title = None, isDtst = True):
        """指定した世代の平均の結果をaxesオブジェクトにセット
        gen:list型あるいはint型, isDtst:DtraかDtstの選択
        """
        y_ax = 'Dtst' if isDtst else 'Dtra'
        ave = {}
        for i, trial in enumerate(self.data.values()):
            data = trial[gen]
            df = data[data['f1'] != 1]
            for i, buf in df.iterrows():
                try:
                    ave[buf['f1']][0] += buf[y_ax]
                    ave[buf['f1']][1] += 1                    
                except:
                    ave[buf['f1']] = [buf[y_ax], 1]
        x = [ruleNum for ruleNum, average in ave.items() if average[1] > trial_num/2]
        y = [average[0]/average[1] for average in ave.values() if average[1] > trial_num/2]
        ax.scatter(x, y, label = label_name)
        ax.grid(True)
        if title is not None:
            ax.set_title(title)

    def setplotGenBest(self, gen, ax, label_name, title = None, isDtst = True):
        """指定した世代の最良個体群の結果をaxesオブジェクトにセット
        genはlist型あるいはint型, isDtst:DtraかDtstの選択
        """
        y_ax = 'Dtst' if isDtst else 'Dtra'
        best = {}
        for i, trial in enumerate(self.data.values()):
            data = trial[gen]
            df = data[data['f1'] != 1]
            best_individuals = {}
            for i, buf in df.iterrows():
                try:
                    if best_individuals[buf['f1']] > buf[y_ax]:
                        best_individuals[buf['f1']] = buf[y_ax]                        
                except:
                    best_individuals[buf['f1']] = buf[y_ax]
            for RuleNum, bestValue in best_individuals.items():
                try:
                    best[RuleNum][0] += bestValue
                    best[RuleNum][1] += 1                    
                except:
                    best[RuleNum] = [bestValue, 1]
            del best_individuals
        x = [ruleNum for ruleNum, average in best.items() if average[1] > trial_num/2]
        y = [average[0]/average[1] for average in best.values() if average[1] > trial_num/2]
        ax.scatter(x, y, label = label_name)
        ax.grid(True)
        if title is not None:
            ax.set_title(title)
        
        
###################################################################################################
        
        
class dataset():
    """データセットに対する全タイプの識別器の結果用クラス"""
    def __init__(self):
        print("dataset name:")
        self.datasetname = input()
        self.filename_set = set()
        self.resultObj_set = {} #オブジェクト用辞書 key:ファジィセット名 value:resultオブジェクト
        self.folderList = ["5000_15"]
        self.FuzzyTypeList = ["rectangle", "trapezoid", "triangle", "gaussian", "multi"]
        for folderName in self.folderList:
            for fuzzyType in self.FuzzyTypeList:
                self.fileName = "*" + fuzzyType + "_result.xml"
                self.pathList = glob.glob(folderName + "/" + self.datasetname + "*/" + self.datasetname + "*/" + self.fileName)
                for path in self.pathList:
                    
                    ############################################
                    label_name = fuzzyType #ラベルネーム変更忘れるな
                    ############################################
                    
                    self.resultObj_set[label_name] = result(path)
        
        #####################################################################
        self.savePath = "result/" + self.datasetname + "/5000_15/" #変更忘れるな 
        #####################################################################
        
        #(Dtst or Dtrs) or (Ave or Best)の4つの場合でメソッドを実行
        for tmp in [[True, True], [False, True], [True, False], [False, False]]:
            self.plot_result(isDtst = tmp[0], isAve = tmp[1])
        
    def plot_individuals(self, gen = gen_list, trial = trial_list, title = "Result_individuals", filename = None):
        """特定の個体の結果を出力する"""
        if filename is None:
            filename = self.datasetname + "_Result_individuals"

        gen_tmp = [gen] if type(gen) is int else gen
        trial_tmp = [trial] if type(trial) is int else trial
        x_lim,y_lim  = [1000, -1], [1000, -1]
        for i, gen_buf in enumerate(gen_tmp):
            fig =  singleFig_set(title)
            ax = fig.gca()
            for FuzzySet_name, resultObj in self.resultObj_set.items():
                self.setPopulation(trial_tmp, gen_tmp, ax, title, label_name = FuzzySet_name)
            ax.legend(loc='upper right')
            buf_x, buf_y = ax.get_xlim(), ax.get_ylim()
            if buf_x[0] < x_lim[0]: x_lim[0] = buf_x[0] 
            if buf_x[1] > x_lim[1]: x_lim[1] = buf_x[1]
            if buf_y[0] < y_lim[0]: y_lim[0] = buf_y[0]
            if buf_y[1] > y_lim[1]: y_lim[1] = buf_y[1]
        
        fignums = plt.get_fignums()
        for i, fignum in enumerate(fignums):
            plt.figure(fignum)
            fig = plt.gcf()
            ax = fig.gca()
            ax.set_xlim(x_lim)
            ax.set_ylim(y_lim)
            ax.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
            ax.set_xlabel("number of rule")
            ax.set_ylabel("error rate[%]") 
            SaveFig(fig, self.savePath, filename + str(i).zfill(3), self.datasetname + '/ave')
        plt.close()


    def plot_result(self, gen = gen_list, isDtst = True, isAve = True, title = None, filename = None):
        if title is None:
            if isDtst and isAve:
                fig_title = self.datasetname + " [Dtst's average of all individual of each gen]"
                filename = self.datasetname + "_Result_Step_All_Dtst"
                savepath = self.savePath + "AveDtst/"
            elif not isDtst and isAve:
                fig_title = self.datasetname + " [Dtra's average of all individual of each gen]"
                filename = self.datasetname + "_Result_Step_All_Dtra"
                savepath = self.savePath + "AveDtra/"
            elif isDtst and not isAve:
                fig_title = self.datasetname + " [Dtst's average of best individual of each gen]"
                filename = self.datasetname + "_Result_Step_Best_Dtst"
                savepath = self.savePath + "BestDtst/"
            elif not isDtst and not isAve:
                fig_title = self.datasetname + " [Dtra's average of best individual of each gen]"
                filename = self.datasetname + "_Result_Step_Best_Dtra"   
                savepath = self.savePath + "BestDtra/"
        print(savepath)
        
        gen_tmp = [gen] if type(gen) is int else gen

        x_lim,y_lim  = [1000, -1], [1000, -1]
        for i, gen_buf in enumerate(gen_tmp):
            fig =  singleFig_set(fig_title)
            ax = fig.gca()
            if isAve:
                for FuzzySet_name, resultObj in self.resultObj_set.items():
                    resultObj.setplotGenAve(gen_buf, ax, label_name = FuzzySet_name, title = "gen:" + str(gen_buf), isDtst = isDtst) 
            elif not isAve:
                for FuzzySet_name, resultObj in self.resultObj_set.items():
                    resultObj.setplotGenBest(gen_buf, ax, label_name = FuzzySet_name, title = "gen:" + str(gen_buf), isDtst = isDtst)                  
            ax.legend(loc='upper right')
            buf_x, buf_y = ax.get_xlim(), ax.get_ylim()
            if buf_x[0] < x_lim[0]: x_lim[0] = buf_x[0] 
            if buf_x[1] > x_lim[1]: x_lim[1] = buf_x[1]
            if buf_y[0] < y_lim[0]: y_lim[0] = buf_y[0]
            if buf_y[1] > y_lim[1]: y_lim[1] = buf_y[1]
            
        fignums = plt.get_fignums()
        for i, fignum in enumerate(fignums):
            plt.figure(fignum)
            fig = plt.gcf()
            ax = fig.gca()
            ax.set_xlim(x_lim)
            ax.set_ylim(y_lim)
            ax.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
            ax.set_xlabel("number of rule")
            ax.set_ylabel("error rate[%]")
            SaveFig(fig, savepath, filename + str(i).zfill(3))
        plt.close('all')
        print("fin")
