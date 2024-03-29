
import xml.etree.ElementTree as ET
import matplotlib.pyplot as plt
import pandas as pd
import os
import datetime
import glob


#######  setting  #######
###############################################################################
#数値実験基本設定
trial_num = 30 #試行回数
gen_num = 10000 #世代数
my_path = os.getcwd()
FuzzyTypeID = {9:"rectangle", 7:"trapezoid", 3:"triangle", 4:"gaussian", 99:"multi"}
gen_list = range(1000, 10000+1, 1000)
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
default_figsize = (16, 9)
default_titlesize = 18
colorList = ["r", "b", "m", "g"]
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
        fig.subplots_adjust(left=0.11, right=0.95, bottom=0.15, top=0.92)
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
    now = datetime.datetime.now()
    imageName = filename + "_{0:%Y%m%d%H%M%S}_{1:%f}.png".format(now, now)
    fig.savefig(my_path + '\\' + filePath + "\\" + imageName, transparent=False)
    
def outputFigList(figList):
    for fig_i in figList:
        fig_tmp = settingFig(fig_i[0])
        SaveFig(fig_tmp, fig_i[1], fig_i[2])
    
def settingFig(fig):
    ax = fig.gca()
    ax_xlim, ax_ylim = ax.get_xlim(), ax.get_ylim()
    ax.set_xticks(range(2, int(ax_xlim[1])+1, lim(ax_xlim[0], ax_xlim[1]+1)))
    ax.tick_params(axis="x", labelsize=30)
    ax.tick_params(axis="y", labelsize=30)
    ax.set_xlabel("ルール数", fontsize = 40,  fontname="MS Gothic")
    ax.set_ylabel("誤識別率[%]", fontsize = 40,  fontname="MS Gothic")
    ax.legend(loc='upper right', fontsize='xx-large')
    return fig

class XML:
    """xmlファイルを読み込むためのスーパークラス"""
    def __init__(self, filename):
        self.tree = ET.parse(filename)
        self.rootNode = self.tree.getroot()
        self.nodelist(self.rootNode)
        self.CurrentElement = [] #木構造の現在参照しているノードの位置を保存する

    def nodelist(self, root):
        for i, child in enumerate(root):
            if i<5 or i>len(list(root))-3:
                txt = "node" if child.text == None else str(child.text)
                print("{:3d}: {:20}{:10}{}".format(i, child.tag, str(txt), child.attrib))
            elif i>9 and i<13:
                print("         .")
        
    def down(self, id):
        """指定したIDの子要素を参照する"""
        self.CurrentElement.append(id)
        buf = self.rootNode
        for i in self.CurrentElement:
            if not list(buf):
                print("NULL")
                self.up()
                return
            buf = buf[i]
        print("current node:" + buf.tag)
        print(buf.attrib)
        print("\n")
        self.nodelist(buf)
            
    def up(self):
        """指定したIDの親要素を参照する"""
        self.CurrentElement.pop(-1)
        buf = self.rootNode
        for i in self.CurrentElement:
            buf = buf[i]
        print("current node:" + buf.tag)
        print(buf.attrib)
        print("\n")
        self.nodelist(buf)
            
    def root(self):
        """木の根にもどる"""
        self.CurrentElement.clear()
        self.nodelist(self.rootNode)
        
    def showAll(self):
        buf = self.rootNode
        for i in self.CurrentElement:
            buf = buf[i]
        for i, child in enumerate(buf):
            txt = "node" if child.text == None else str(child.text)
            print("{:3d}: {:20}{:10}{}".format(i, child.tag, str(txt), child.attrib))
            
    def show(self):
        buf = self.rootNode
        for i in self.CurrentElement:
            buf = buf[i]
        for i, child in enumerate(buf):
            if i<5 or i>len(list(buf))-3:
                txt = "node" if child.text == None else str(child.text)
                print("{:3d}: {:20}{:10}{}".format(i, child.tag, str(txt), child.attrib))
            elif i>9 and i<13:
                print("         .")
        
        
class resultXML(XML):
    """1つのxmlファイル(実験結果)用クラス"""
    def __init__(self, filePath, savePath):
        super(resultXML, self).__init__(filePath)
        self.data = {}
        self.savePath = savePath
        for i, trial in enumerate(self.rootNode.findall('trial')):
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
            SaveFig(fig, self.savePath, filename)
        else:
            plt.show()
            
        
    def setplotGenAve(self, gen, ax, label_name, title = None, isDtst = True, marker = "o"):
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
        ax.scatter(x, y, label = label_name, marker = marker, s=60)
        ax.grid(True)
        if title is not None:
            ax.set_title(title)

    def setplotGenBest(self, gen, ax, label_name, title = None, isDtst = True, marker = "o"):
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
        ax.scatter(x, y, label = label_name, marker = marker, s=100)
        ax.grid(True)
        if title is not None:
            ax.set_title(title)
            
    def setPlotDtraBest(self, gen, ax, label_name, title = None, isDtst = True, marker = "o"):
        best = {}
        for i, trial in enumerate(self.data.values()):
            data = trial[gen]
            df = data[data['f1'] != 1]
            best_individuals_Dtra = {}
            best_individuals_Dtst = {}
            for i, buf in df.iterrows():
                try:
                    if best_individuals_Dtra[buf['f1']] > buf['Dtra'] or (best_individuals_Dtra[buf['f1']] == buf['Dtra'] and best_individuals_Dtst[buf['f1']] > buf['Dtst']):
                        best_individuals_Dtra[buf['f1']] = buf['Dtra']                      
                        best_individuals_Dtst[buf['f1']] = buf['Dtst']
                except:
                    best_individuals_Dtra[buf['f1']] = buf['Dtra']
                    best_individuals_Dtst[buf['f1']] = buf['Dtst']
            if isDtst:
                for RuleNum, bestValue in best_individuals_Dtst.items():
                    try:
                        best[RuleNum][0] += bestValue
                        best[RuleNum][1] += 1                    
                    except:
                        best[RuleNum] = [bestValue, 1]
            else:
                for RuleNum, bestValue in best_individuals_Dtra.items():
                    try:
                        best[RuleNum][0] += bestValue
                        best[RuleNum][1] += 1                    
                    except:
                        best[RuleNum] = [bestValue, 1]
            del best_individuals_Dtra, best_individuals_Dtst
        x = [ruleNum for ruleNum, average in best.items() if average[1] > trial_num/2]
        y = [average[0]/average[1] for average in best.values() if average[1] > trial_num/2]
        ax.scatter(x, y, label = label_name, marker = marker, s=100)
        
        ax.grid(True)
        if title is not None:
            ax.set_title(title)
        
        
        
###################################################################################################
        
        
class Result():
    """データセットに対する全タイプの識別器の結果用クラス"""
    def __init__(self):
        print("RESULT\n dataset name:")
        self.datasetName = input()
        self.resultObj_set = {} #[FuzzyTypeList][folderList] = RuleSetXMLオブジェクト
        self.experimentTittle = "PartitionNumRank"#["samePartitionNum", "diffPartitionNum"]#["rectangular", "trapezoid", "gaussian", "triangle", "multi"]
        self.experimentList = [("default", "default"), ("default_entropy_mixed/multi", "default_entropy_mixed"), ("PartitionNumRank3", "PartitionNumRank3"), ("PartitionNumRank5", "PartitionNumRank5")]#["default", "default_entropy_mixed"]
        self.savePath = "result/" + self.experimentTittle + "/" + self.datasetName #変更忘れるな
        dirPath = "xml/" + self.experimentTittle + "/" + self.datasetName + "/"
        # label_name = "             "
        for experimentFolder in self.experimentList:
            self.fileName = self.datasetName + "_result.xml"
            self.pathList = glob.glob(dirPath + experimentFolder[0] + "/" + self.datasetName + "*/" + self.fileName)
            for path in self.pathList:
                print(path)
                print(experimentFolder[0], experimentFolder[1])
                self.resultObj_set[experimentFolder[1]] = resultXML(path, self.savePath + "/" + experimentFolder[0])
        
        # (Dtst or Dtrs) or (Ave or Best)の4つの場合でメソッドを実行
        for tmp in [[True, False], [False, False]]:
            self.plot_result(gen = 10000, isDtst = tmp[0], isAve = tmp[1])
        
    def down(self, x):
        i = 0
        for ExperimentName, resultObj_set_Experiment in self.resultObj_set.items():
            for label_name, resultObj in resultObj_set_Experiment.items():
                if i == x:
                    return resultObj
                i += 1
    
    def getRuleSetXML(self):
        i = 0
        for folderName, ResultObj_buf1 in self.resultObj_set.items():
            for fuzzyType, ResultObj_buf2 in ResultObj_buf1.items():
                print("ID:" + str(i) + " | fuzzyType = " + fuzzyType, "| folderName = " + folderName)
                i += 1
        print("type ID")
        ID = int(input())
        i = 0
        for folderName, ResultObj_buf1 in self.resultObj_set.items():
            for fuzzyType, ResultObj_buf2 in ResultObj_buf1.items():
                if i == ID:
                    ResultObj_buf2.show()
                    return ResultObj_buf2
                i += 1

    def plot_result(self, gen = gen_list, isDtst = True, isAve = True, title = None, filename = None, isDtraBest = False):
        gen_tmp = [gen] if type(gen) is int else gen
        d_today = datetime.date.today()
        savepath = self.savePath + "/{0:%Y%m%d}".format(d_today)+ "/"
        returnBuf = []
        if not isDtraBest:
            if isDtst and isAve:
                fig_title = self.datasetName + ": " + self.experimentTittle + " Dtst's average"
                filename = self.datasetName + "_" + self.experimentTittle + "_Result_Ave_Dtst"
                savepath = savepath + "/AveDtst"
            elif not isDtst and isAve:
                fig_title = self.datasetName + ": " + self.experimentTittle + " Dtra's average"
                filename = self.datasetName + "_" + self.experimentTittle + "_Result_Ave_Dtra"
                savepath = savepath + "/AveDtra"
            elif isDtst and not isAve:
                fig_title = self.datasetName + ": " + self.experimentTittle + " Dtst's average of PF"
                filename = self.datasetName + "_" + self.experimentTittle + "_Result_Best_Dtst"
                savepath = savepath + "/BestDtst"
            elif not isDtst and not isAve:
                fig_title = self.datasetName + ": " + self.experimentTittle + " Dtra's average of PF"
                filename = self.datasetName + "_" + self.experimentTittle + "_Result_Best_Dtra"   
                savepath = savepath + "/BestDtra"
        elif isDtraBest:
            if isDtst:
                fig_title = self.datasetName + ": " + self.experimentTittle + " Dtst's average of Dtra Best"
                filename = self.datasetName + "_" + self.experimentTittle + "_Result_DtraBest_Dtst"
                savepath = savepath + "/BestDtst"
            elif not isDtst:
                fig_title = self.datasetName + ": " + self.experimentTittle + " Dtra's average of Dtra vest"
                filename = self.datasetName + "_" + self.experimentTittle + "_Result_DtraBest_Dtra"   
                savepath = savepath + "/BestDtra"
        os.makedirs(savepath, exist_ok=True)
        
        for gen_i in gen_tmp:
            x_lim,y_lim  = [1000, -1], [1000, -1]
            fig = singleFig_set(fig_title)
            returnBuf.append([fig, savepath, filename + "dim_" + str(gen_i).zfill(3)])
            ax = fig.gca()
            for ExperimentName, resultObj in self.resultObj_set.items():
                if not isDtraBest:
                    if isAve:
                        resultObj.setplotGenAve(gen_i, ax, label_name = ExperimentName, title = "gen:" + str(gen_i), isDtst = isDtst) 
                    elif not isAve:
                        resultObj.setplotGenBest(gen_i, ax, label_name = ExperimentName, title = "gen:" + str(gen_i), isDtst = isDtst)                  
                elif isDtraBest:
                        resultObj.setPlotDtraBest(gen_i, ax, label_name = ExperimentName, title = "gen:" + str(gen_i), isDtst = isDtst)                  

            buf_x, buf_y = ax.get_xlim(), ax.get_ylim()
            if buf_x[0] < x_lim[0]: x_lim[0] = buf_x[0] 
            if buf_x[1] > x_lim[1]: x_lim[1] = buf_x[1]
            if buf_y[0] < y_lim[0]: y_lim[0] = buf_y[0]
            if buf_y[1] > y_lim[1]: y_lim[1] = buf_y[1]
        fignums = plt.get_fignums()
        for i, fignum in enumerate(fignums):
            plt.figure(fignum)
            ax = plt.gca()
            ax.set_xlim(x_lim)
            ax.set_ylim(y_lim)
        return returnBuf
        #         ax.set_xticks(range(2, int(x_lim[1])+1, lim(x_lim[0], x_lim[1]+1)))
        #         ax.tick_params(axis="x", labelsize=30)
        #         ax.tick_params(axis="y", labelsize=30)
        #         ax.set_xlabel("ルール数", fontsize = 40,  fontname="MS Gothic")
        #         ax.set_ylabel("誤識別率[%]", fontsize = 40,  fontname="MS Gothic")
        #         print(savepath + "/" + ExperimentName + "/")
        #         print(filename + str(i).zfill(3))
        #         SaveFig(fig, savepath + "/" + ExperimentName + "/", filename + str(i).zfill(3))
        #     print(savepath + ExperimentName)
        #     plt.close('all')
        # print("fin")

    def plot_resultDtraBest(self, gen = gen_list, title = None, filename = None):
        gen_tmp = [gen] if type(gen) is int else gen
        d_today = datetime.date.today()
        
        for ExperimentName, resultObj_dict in self.resultObj_set.items():
            
            savepath = self.savePath + "{0:%Y%m%d}".format(d_today)+ "/DtarBest/"
            fig_title = self.datasetName + ": " + ExperimentName + " Dtra Best's Dtst"
            filename = self.datasetName + "_" + ExperimentName + "_Result_DtraBest"
            print(savepath)
            
            x_lim,y_lim  = [1000, -1], [1000, -1]
            for gen_buf in gen_tmp:
                fig =  singleFig_set(fig_title)
                ax = fig.gca()
                for label_name, resultObj in resultObj_dict.items():
                    resultObj.setPlotDtraBest(gen_buf, ax, label_name = label_name, title = "gen:" + str(gen_buf)) 
                ax.legend(loc='upper right', fontsize='xx-large')
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
                ax.legend(loc='upper right', fontsize='xx-large')
                ax.set_xlim(x_lim)
                ax.set_ylim(y_lim)
                ax.set_xticks(range(2, int(x_lim[1])+1, lim(x_lim[0], x_lim[1]+1)))
                ax.tick_params(axis="x", labelsize=24)
                ax.tick_params(axis="y", labelsize=24)
                ax.set_xlabel("number of rule", fontsize = 36)
                ax.set_ylabel("error rate[％]", fontsize = 36)
                SaveFig(fig, savepath + ExperimentName + "/", filename + str(i).zfill(3))
            plt.close('all')
        print("fin")
        
        

