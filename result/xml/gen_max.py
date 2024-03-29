import xml.etree.ElementTree as ET
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import os
from datetime import datetime
import glob
from scipy.stats import gaussian_kde
from sklearn import preprocessing
import csv

#######  setting  #######
###############################################################################
#数値実験基本設定
trial_num = 30 #試行回数
gen_num = 1000 #世代数
individual_num = 50 #個体数
partiton_num_set = (2, 3, 4, 5) #分割数のリスト
my_path = os.getcwd()
DontCareID = 99
FuzzyTypeID = {9:"rectangle", 7:"trapezoid", 3:"triangle", 4:"gaussian", DontCareID:"DontCare"}
gen_list = range(100, gen_num, 100)
trial_list = range(trial_num)
cmap = plt.get_cmap("tab10")

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
trial_plot = 0 #plotするtriaslのID
gen_plot = 10000 #plotする世代数
attri_plot = 0
#scatterの基本設定
default_size = 50
default_alpha = 0.01
#figureの基本設定
default_figsize = (16, 16) ##
default_titlesize = 18
###############################################################################

lim = lambda s, g: int((g-s)/10) if int((g-s)/10) != 0 else 1

        
def singleFig_set(title = None):
        """保存する画像(グラフ1つ)の基本設定
        入力:ファイル名
        返り値:figureオブジェクト"""
        fig = plt.figure(figsize = default_figsize)
        plt.rcParams["font.family"] = "MS Gothic"
        # fig.subplots_adjust(left=0.06, right=0.94, bottom=0.06, top=0.92)
        fig.subplots_adjust(left=0, right=1, bottom=0, top=0.95)
        ax = fig.add_subplot(1, 1, 1)
        if title is not None:
            fig.suptitle(title, size = default_titlesize)        
        ax.grid(False)
        return fig
    
def multiFig_set(axNum, title = None):
        """保存する画像(グラフ複数)の基本設定
        入力:ファイル名 axNum:グラフの数
        返り値:figureオブジェクト"""
        fig = plt.figure(figsize = (24, ((axNum-1)/3+1)*6))
        for i in range(axNum):
            fig.add_subplot((axNum+2)/3, 3, i+1)
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
    fig.savefig(my_path + "\\" + filePath + "\\" + imageName, transparent=True)     

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
        
def ls(current_path = my_path):
    pathList = glob.glob(current_path + "/*")
    for i, path in enumerate(pathList):
        print("{:3d}: {}".format(i, path))
    print("type id(back = -1):")
    tmp = int(input())
    if tmp < 0:
        ls(my_path)
    else:
        ls(pathList[tmp])

class detaset_df:
    def __init__(self, datasetName):
        self.datasetName = datasetName
        self.attributeNum = DatasetList[self.datasetName]["Attribute"]
        try:
            df_original = pd.read_csv("./dataset/" + self.datasetName + ".csv", header=None)
        except:
            return
        classList_buf = df_original.iloc[:, self.attributeNum]
        self.classList = set() #クラス名のリスト
        for className in classList_buf:
            self.classList.add(className)
        df_buf = df_original.iloc[:, 0:self.attributeNum]
        self.df = pd.concat([(df_buf - df_buf.min()) / (df_buf.max() - df_buf.min()), classList_buf], axis=1, join='inner') #正規化されたdetaframe
        
        self.dfByClass = {} #クラス別の正規化されたdataframe
        for className in self.classList:
            df_buf = self.df[self.df[self.attributeNum] == className]
            self.dfByClass[className] = df_buf

    def setAx(self, dim, ax):
        ax2 = ax.twinx()
        for className in self.classList:
            kde_model = gaussian_kde(self.dfByClass[className][dim])
            x_grid = np.linspace(0, max(self.dfByClass[className][dim]), num=100)
            y = kde_model(x_grid)
            ax2.plot(x_grid, y, linestyle = "--", label = className)
            ax2.tick_params(axis="y", labelsize=24)
            ax2.legend(bbox_to_anchor=(1.12, 1), loc='upper left', fontsize=18)
    
    def setAxHist(self, dim, ax):
        ax2 = ax.twinx()
        for c, className in enumerate(self.classList):
            ax2.hist(self.dfByClass[className][dim], bins = 15, range = (0.0, 1.0), alpha = 0.5-0.07*c, color = cmap(c))
            ax2.hist(self.dfByClass[className][dim], bins = 15, range = (0.0, 1.0), histtype="step", color = cmap(c))
            ylim = ax2.get_ylim();
            ax2.set_ylim(-0.05*ylim[1], ylim[1]*1.05)
            ax2.tick_params(axis="y", labelsize=24)
        
class FuzzyTerm:
    """Fuzzy Termのためのクラス"""
    def __init__(self, fuzzyterm):
        self.name =  fuzzyterm.find("name").text
        self.typeID = int(fuzzyterm.find("Shape_Type_ID").text)
        self.ID = int(fuzzyterm.get("ID"))
        self.Shape_Type = fuzzyterm.find("Shape_Type").text
        self.parameters = {}
        for buf in fuzzyterm.find('parameters'):
            self.parameters[int(buf.get('id'))] = float(buf.text)
        self.PartitionNum = int(fuzzyterm.find("PartitionNum").text)
    
    def setAx(self, ax, alpha = 1.0, alpha_between = 0.1, color = "black", color_between = "blue"):
        """Ax にメンバシップ関数をプロットする"""
        color_buf = "C{}".format(color) if type(color) is int else color
        
        ax.set_ylim(-0.05, 1.05)
        if self.typeID == 3:
            x = np.array([0, self.parameters[0], self.parameters[1], self.parameters[2], 1])
            y = np.array([0, 0, 1, 0, 0])
            ax.plot(x, y, alpha = alpha, color = color_buf)
            y_bottom = [0, 0, 0, 0, 0]
            ax.fill_between(x, y, y_bottom, facecolor=color_between, alpha = alpha_between)
        if self.typeID == 4:
            mu = self.parameters[0]
            sigma = self.parameters[1]
            x = np.arange(0, 1, 0.01)
            y = 1 * np.exp(-(x - mu)**2 / (2*sigma**2))
            ax.plot(x, y, alpha = alpha, color = color_buf)
            y_bottom = [0] * 100
            ax.fill_between(x, y, y_bottom, facecolor = color_between, alpha = alpha_between)
        if self.typeID == 7:
            x = np.array([0, self.parameters[0], self.parameters[1], self.parameters[2], self.parameters[3], 1])
            y = np.array([0, 0, 1, 1, 0, 0])
            ax.plot(x, y, alpha = alpha, color = color_buf)
            y_bottom = [0, 0, 0, 0, 0, 0]
            ax.fill_between(x, y, y_bottom, facecolor = color_between, alpha = alpha_between)
        if self.typeID == 9:
            x = np.array([0, self.parameters[0], self.parameters[0], self.parameters[1], self.parameters[1], 1])
            y = np.array([0, 0, 1, 1, 0, 0])
            ax.plot(x, y, alpha = alpha, color = color_buf)
            y_bottom = [0, 0, 0, 0, 0, 0]
            ax.fill_between(x, y, y_bottom, facecolor = color_between, alpha = alpha_between)
        if self.typeID == 99:
            x = np.array([0, 0, 1, 1])
            y = np.array([0, 1, 1, 0])
            ax.plot(x, y, alpha = alpha, color = color_buf)
            y_bottom = [0, 0, 0, 0]
            ax.fill_between(x, y, y_bottom, facecolor = color_between, alpha = alpha_between)
        
class KB:
    """Knowledge bas用のクラス"""
    def __init__(self, kb, dataseName):
        self.datasetName = dataseName
        self.fuzzySets = {}
        self.gen = int(kb.get('generation'))
        self.trial = int(kb.get('trial'))
        for fuzzySet in kb.findall("FuzzySet"):
            FuzzyTerms_ByAttribute = {} #[AttributeID][FuzzySetID] = FuzzyTermオブジェクト
            for fuzzyTermNode in fuzzySet.findall('FuzzyTerm'):
                FuzzyTerms_ByAttribute[int(fuzzyTermNode.get("ID"))] = FuzzyTerm(fuzzyTermNode)
            self.fuzzySets[int(fuzzySet.get("dimension"))] = FuzzyTerms_ByAttribute
        
    def plot(self, savePath, isSave = True, inOneFig = False, Dataset_df = None, ByPartitoinNum = True, df = None, ClassifyResult = None):
        print(savePath)
        if inOneFig and ByPartitoinNum: #FuzzySetByPartition
            for dimension, FuzzySet in self.fuzzySets.items():
                CurrentFuzzySetID = 1
                while len(FuzzySet) > CurrentFuzzySetID:
                    partiton_num = FuzzySet[CurrentFuzzySetID].PartitionNum
                    fig = singleFig_set("KB_trial" + str(self.trial) + "_gen" + str(self.gen) + "_Attribute" + str(dimension) + "_Partition" + str(partiton_num))
                    ax = fig.gca()
                    ax.tick_params(axis="x", labelsize=24)
                    ax.tick_params(axis="y", labelsize=24)
                    if ClassifyResult is not None:
                        ClassifyResult.plot(ax, dimension)
                    if df is not None:
                        df.setAx(dimension, ax, alpha_between = 0)
                    for i in range(partiton_num):
                        FuzzySet[CurrentFuzzySetID + i].setAx(ax, alpha_between = 0)
                    if isSave:
                        SaveFig(fig, savePath + "KnowledgeBase/FuzzySetByPartition/Attribute_" + str(dimension) + "/", \
                                self.datasetName + "_KB_trial" + str(self.trial) + "_gen" + str(self.gen) + "_Attribute" + str(dimension) + "_partition" + str(CurrentFuzzySetID) + "-" + str(CurrentFuzzySetID + partiton_num - 1))
                    elif not isSave:
                        fig.show()
                    plt.close("all")
                    CurrentFuzzySetID += partiton_num
        elif inOneFig and not ByPartitoinNum: #FuzzySetALL
            for dimension, FuzzySet in self.fuzzySets.items():
                fig = singleFig_set("KnowledgeBase_trial" + str(self.trial) + "_gen" + str(self.gen) + "_Attribute" + str(dimension))
                ax = fig.gca()
                ax.tick_params(axis="x", labelsize=24)
                ax.tick_params(axis="y", labelsize=24)                
                if ClassifyResult is not None:
                    ClassifyResult.plot(ax, dimension)
                if df is not None:
                    df.setAx(dimension, ax)
                for FuzzyTermID, FuzzyTerm in FuzzySet.items():
                    FuzzyTerm.setAx(ax)
                if isSave:
                    SaveFig(fig, savePath + "KnowledgeBase/FuzzySetALL/", \
                            self.datasetName + "_KnowledgeBase_trial" + str(self.trial) + "_gen" + str(self.gen) + "_Attribute" + str(dimension))
                elif not isSave:
                    fig.show()
                plt.close("all")
        elif not inOneFig: #FuzzySetSingle
            for dimension, FuzzySet in self.fuzzySets.items():
                for FuzzyTermID, FuzzyTerm in FuzzySet.items():
                    fig = singleFig_set("KnowledgeBase_trial" + str(self.trial) + "_gen" + str(self.gen) + "_Attribute" + str(dimension) + "_FuzzyTermID" + str(FuzzyTermID))
                    ax = fig.gca()
                    ax.tick_params(axis="x", labelsize=24)
                    ax.tick_params(axis="y", labelsize=24)
                    if ClassifyResult is not None:
                        ClassifyResult.plot(ax, dimension)
                    if df is not None:
                        df.setAx(dimension, ax)
                    self.fuzzySets[dimension][FuzzyTermID].setAx(ax)
                    if isSave:
                        SaveFig(fig, savePath + "KnowledgeBase/FuzzySetSingle/Attribute_" + str(dimension) + "/", \
                                self.datasetName + "_KnowledgeBase_trial" + str(self.trial) + "_gen" + str(self.gen) + "_Attribute" + str(dimension) + "_FuzzyTermID" + str(FuzzyTermID))
                    elif not isSave:
                        fig.show()
                    plt.close("all")
        
    def setFuzzyTerm(self, ax, dimension = attri_plot, ID = 0, alpha = 1.0, alpha_between = 0.1, color = "black", color_between = "blue"):
        self.fuzzySets[dimension][ID].setAx(ax, alpha = alpha, color = color, alpha_between = alpha_between, color_between = color_between)
        
    def getFuzzyTermID(self, dimension = attri_plot, ID = 0):
        return self.fuzzySets[dimension][ID]
        
class RuleSetInfo:
    def __init__(self, population, datasetName):
        self.gen = int(population.get('generation'))
        self.trial = int(population.get('trial'))
        self.datasetName = datasetName
        self.population = population
        self.attributeNum = DatasetList[datasetName]["Attribute"]
        self.classNum = DatasetList[datasetName]["Class"]

class SingleRule(RuleSetInfo):
    def __init__(self, singleRule, population, datasetName, i):
        super().__init__(population, datasetName)
        self.ID = i
        self.rule = {}
        for element in singleRule.find('rule'):
            self.rule[int(element.get("ID"))] = int(element.text)
        self.conclusion = int(singleRule.find('conclusion').text)
        self.cf = float(singleRule.find('cf').text)
        self.fitness = float(singleRule.find('fitness').text)
        
        
class Individual(RuleSetInfo):
    def __init__(self, individual, population, datasetName, i):
        super().__init__(population, datasetName)
        self.ID = i
        self.rules = {i: SingleRule(singleRule, population, datasetName, i) for i, singleRule in enumerate(individual.find('ruleSet'))}
        self.ruleNum = int(individual.get("ruleNum"))
   
    def isCoverAllClasses(self):
        """この個体について，この個体が持つ各ルールの結論部が全てのクラスを持つか確認する"""
        check = set()
        for rule in self.rules.values():
            check.add(rule.conclusion)
        return True if len(check) == self.classNum else False
        
class Population(RuleSetInfo):
    def __init__(self, population, datasetName):
        super().__init__(population, datasetName)
        self.kb = KB(population.find('KnowledgeBase'), datasetName) 
        self.individuals = {i: Individual(individual, population, datasetName, i) for i, individual in enumerate(population.findall('individual'))}
      
    def setIndividual(self, fig, individualID):
        if len(fig.axes) != self.attributeNum:
            print("axesオブジェクトが属性値と同数でない")
            return        
        axes = fig.axes
        for rule_buf in self.individuals[individualID].rules.values():
            for i, ax in enumerate(axes):
                fuzzyTermID = rule_buf.rule[i]
                self.kb.setFuzzyTerm(fuzzyTermID, ax)
            
    def setSingleRule(self, fig, individualID, ruleID):
        if len(fig.axes) != self.attributeNum:
            print("axesオブジェクトが属性値と同数でない")
            return
        axes = fig.axes
        for i, ax in enumerate(axes):
            fuzzyTermID = self.individuals[individualID].rules[ruleID].rule[i]
            self.kb.setFuzzyTerm(fuzzyTermID, ax)
            
    def getIndividual(self, ID):
        return self.individuals[ID]
    
    

class RuleSetXML(XML):
    def __init__(self, path, savePath, datasetName):
        """一つのルールセット用のクラス
        入力: path=xmlファイルのパス, datasetName = データセットの名前, df = データセットのcsvファイル(detasframe)"""
        self.datasetName = datasetName
        self.attributeNum = DatasetList[self.datasetName]["Attribute"]
        self.classNum = DatasetList[self.datasetName]["Class"]
        self.savePath = savePath
        super().__init__(path)
        self.ruleset = {int(population.get("generation")): Population(population, self.datasetName) for population in self.rootNode.findall('population')}
        self.allIndividual = {(i, gen_plot):(j for j in range(individual_num)) for i in range(trial_num)}
        self.gen = int(self.rootNode.find('population').get("generation"))
        
    def getPopulation(self, ID_tuple):
        return self.ruleset[ID_tuple[0]][ID_tuple[1]]
    
    def KBplot(self, generation = None, inOneFig = False, ByPartitoinNum = True, df = None, ClassifyResult = None):
        generation_list = [generation] if type(generation) is int else generation
        if generation is None:
            for generation, population in self.ruleset.items():
                population.kb.plot(self.savePath + "generation_" + str(generation) + "/", isSave = True, inOneFig = inOneFig, Dataset_df = None, ByPartitoinNum = ByPartitoinNum, df = df, ClassifyResult = ClassifyResult)
        else:
            for generationID in generation_list:
                self.ruleset[generationID].kb.plot(self.savePath + "generation_" + str(generation) + "/", isSave = True, inOneFig = inOneFig, Dataset_df = None, ByPartitoinNum = ByPartitoinNum, df = df, ClassifyResult = ClassifyResult) 
                    
    def UsedMenbership(self, gen = gen_plot, isDontCare = False, df = None, isCoverAllClasses = True):
        savePath = self.savePath + "/usedMenbership/CoverAllClasses/" if isCoverAllClasses else self.savePath + "/usedMenbership/AllIndividuals/"
        print(savePath)
        population = self.ruleset[gen]
        kb = population.kb
        uesdFuzzyTerms = [[0]*len(kb.fuzzySets[dimension]) for dimension in kb.fuzzySets.keys()]
        for individualID, individual in population.individuals.items():
            if not isCoverAllClasses or (individual.ruleNum >= DatasetList[self.datasetName]["Class"] and individual.isCoverAllClasses()):
                for ruleID, SingleRule in individual.rules.items():
                    for dim, FuzzyTermID in SingleRule.rule.items():
                        if FuzzyTermID != 0 or (FuzzyTermID == 0 and isDontCare):
                            uesdFuzzyTerms[dim][FuzzyTermID] += 1
        for dim in range(self.attributeNum):
            buf = preprocessing.minmax_scale(uesdFuzzyTerms, axis=1)
            for fuzzyType_i in range(6):
                fig = singleFig_set(self.datasetName + " dim:" + str(dim) + " used menbership (cover all classes)")
                ax = fig.gca()
                ax.set_ylim(-0.05, 1.05)
                ax.set_xlim(-0.05, 1.05)
                if df is not None:
                    df.setAx(dim, ax)
                for fuzzyTerm_i, alpha in enumerate(buf[dim][1+fuzzyType_i*14:1+(fuzzyType_i+1)*14]):
                    fuzzyTermID = 1+fuzzyType_i*14 + fuzzyTerm_i
                    if alpha > 0:
                        color = "navy" if fuzzyTermID > 42 else "red"                               #######要修正(ファジィ集合変更時)######
                        color_between = "cyan" if fuzzyTermID > 42 else "orange"                    #######要修正(ファジィ集合変更時)######
                        kb.setFuzzyTerm(ax, dim, fuzzyTermID, alpha = alpha, alpha_between = alpha*0.1, color = color, color_between = color_between)
                SaveFig(fig, savePath + "dim_" + str(dim), self.datasetName + "_dim" + str(dim) + "_usedMenbership")
                plt.close("all")

    def UsedMenbershipRate(self, isDontCare = False, df = None, isCoverAllClasses = False):
        savePath = self.savePath + "\\UsedMenbershipRate\\CoverAllClasses\\" if isCoverAllClasses else self.savePath + "\\UsedMenbershipRate\\AllIndividuals\\"
        print(savePath)
        uesdFuzzyTerms = [[0]*len(self.ruleset[self.gen].kb.fuzzySets[i]) for i in range(self.attributeNum)]#0番目試行のKを使用
        for individualID, individual in self.ruleset[self.gen].individuals.items():
            if not isCoverAllClasses or (individual.ruleNum < DatasetList[self.datasetName]["Class"] or not individual.isCoverAllClasses()):
                for SingleRule in individual.rules.values():
                    for dim, FuzzyTermID in SingleRule.rule.items():
                        uesdFuzzyTerms[dim][FuzzyTermID] += 1
                        
        # print(uesdFuzzyTerms)
        cm = plt.get_cmap('tab10')
        label_sample = {"DontCare":["Don't Care", cm(0)], "InhomoGaussian":["不均等分割ガウシアン集合", cm(1)], "InhomoInterval":["不均等分割区間集合", cm(2)], \
                        "InhomoFuzzy":["不均等分割線形型ファジィ集合", cm(3)], "HomoGaussian":["均等分割ガウシアン集合", cm(4)], "HomoInterval":["均等分割区間集合", cm(5)], "HomoFuzzy":["均等分割線形型ファジィ集合", cm(6)]}
        for dim in range(self.attributeNum):
            kb = self.ruleset[self.gen].kb
            # fig = singleFig_set(self.datasetName + " gen:" + str(self.gen) + " dim:" + str(dim) + " used menbership rate (cover all classes)")
            fig = singleFig_set()
            plt.subplots_adjust(left=-0.1, right=1.1, bottom=-0.1, top=1.1)
            ax = fig.gca()
            fuzzyTypeData = {}
            FuzzySet = kb.fuzzySets[dim]
            CurrentFuzzySetID = 0 if isDontCare else 1
            while len(FuzzySet) > CurrentFuzzySetID:
                partiton_num = FuzzySet[CurrentFuzzySetID].PartitionNum
                fuzzySetName = FuzzySet[CurrentFuzzySetID].name.split('_')[0]
                # print(fuzzySetName, partiton_num)
                if not fuzzySetName in fuzzyTypeData:
                    fuzzyTypeData[fuzzySetName] = {}
                if not partiton_num in fuzzyTypeData[fuzzySetName]:
                    fuzzyTypeData[fuzzySetName][partiton_num] = 0
                for i in range(partiton_num):
                    fuzzyTypeData[fuzzySetName][partiton_num] += uesdFuzzyTerms[dim][CurrentFuzzySetID + i]
                if not fuzzySetName == "DontCare":
                    CurrentFuzzySetID += partiton_num
                else :
                    CurrentFuzzySetID += 1
            # print(fuzzyTypeData, len(FuzzySet), CurrentFuzzySetID)
            label_2 = [label_sample[tmp][0] for tmp in fuzzyTypeData.keys()]
            colorList_2 = [label_sample[tmp][1] for tmp in fuzzyTypeData.keys()]
            fuzzyTypePartitionData, fuzzyTypeNumData, label_1, colorList_1 = [], [], [], []
            for name, buf in fuzzyTypeData.items():
                x = 0
                for num, tmp in buf.items():
                    x += tmp
                    fuzzyTypeNumData.append(tmp)
                    label_1.append(str(num))
                    color_buf = label_sample[name][1]
                    colorList_1.append((color_buf[0], color_buf[1], color_buf[2], 1/num))
                fuzzyTypePartitionData.append(x)
            patches, texts = ax.pie(fuzzyTypeNumData, labels = label_1, startangle=90, colors = colorList_1, counterclock = False, labeldistance=0.875, wedgeprops={'linewidth': 2, 'edgecolor':"black"})
            for t in texts:
                t.set_size(24)
            # patches, texts = ax.pie(fuzzyTypePartitionData, labels = label_2, startangle=90, colors = colorList_2, counterclock = False, labeldistance=1.4, radius=0.75, wedgeprops={'linewidth': 5, 'edgecolor':"black"})
            patches, texts = ax.pie(fuzzyTypePartitionData, startangle=90, colors = colorList_2, counterclock = False, labeldistance=1.4, radius=0.75, wedgeprops={'linewidth': 5, 'edgecolor':"black"})
            for t in texts:
                t.set_size(36)
            SaveFig(fig, savePath + "dim_" + str(dim), self.datasetName + "_dim" + str(dim) + "_usedMenbershipRate")
            plt.close("all")
            # print(fuzzyTypeData)
            # with open(savePath +'/result_dim_' + str(dim) + '.csv', 'a', newline="") as f:
                # writer = csv.writer(f)
                # header = ["trial"]
                # header_2 = [""]
                # for name, data in fuzzyTypeData.items():
                #     header.append(name)
                #     for i in range(len(data) - 1):
                #         header.append("")
                #     for label, num in data.items():
                #         header_2.append(label)
                #         conte.append(num)
                # writer.writerow(conte)
                
class ClassifyResult(XML):
    # xml = XML("xml/iris/test/test\iris\iris_ClassifyResult.xml")
    def __init__(self, path, savePath, datasetName):
        """識別結果用のクラス
        入力: path=xmlファイルのパス, datasetName = データセットの名前, df = データセットのcsvファイル(detasframe)"""
        self.datasetName = datasetName
        self.attributeNum = DatasetList[self.datasetName]["Attribute"]
        self.classNum = DatasetList[self.datasetName]["Class"]
        self.savePath = savePath
        super().__init__(path)
        self.dataseet = {} #[trial番号][パターンID][属性（次元）]
        self.classifyResult = {}
        self.sum = 0
        for i, trial in enumerate(self.rootNode.findall('trial')):
            buf_1 = {}
            for pattern in trial.find('dataseet'):
                buf_2 = {}
                for attribute in pattern.findall('attribute'):
                    buf_2[int(attribute.get('dim'))] = float(attribute.text)
                buf_1[int(pattern.get('ID'))] = buf_2
            self.dataseet[int(trial.get('trial'))] = buf_1 
        for trial in self.rootNode.findall('trial'):
            buf_1 = {}
            for individual in trial.find('classifyResult'):
                buf_2 = {}
                for pattern in individual.findall('pattern'):
                    tmp = []
                    tmp.append(int(pattern.find('classifiedClass').text))
                    tmp.append(True if int(pattern.find('classifiedResult').text) == 1 else False)
                    buf_2[int(pattern.get('patternID'))] = tmp
                    self.sum += 1
                buf_1[int(individual.get('ID'))] = buf_2
            self.classifyResult[int(trial.get('trial'))] = buf_1
        
    def plot(self, ax, dim):
        ClassifyResult = []
        for trial_i in range(len(self.classifyResult)):
            buf = {patternID:0 for patternID in self.dataseet[trial_i].keys()}
            for individual in self.classifyResult[trial_i].values():
                for patternID, pattern in individual.items():
                    if pattern[1]:
                        buf[patternID] += 1
            for patternID, pattern in self.dataseet[trial_i].items():
                ClassifyResult.append([pattern, buf[patternID]])
        buf = [float(pattern[1]) for pattern in ClassifyResult]
        minValues = min(buf)
        between = max(buf) - minValues
        y = [float(pattern[1] - minValues)/between for pattern in ClassifyResult]
        data = [[[], []] for i in range(self.classNum)]
        for patternID, pattern in enumerate(ClassifyResult):
            classID = int(pattern[0][len(pattern[0])-1])
            data[classID][0].append(pattern[0][dim])
            data[classID][1].append(y[patternID])
        for class_i in range(self.classNum):
            # print(class_i, data[class_i][0], data[class_i][1])
            ax.scatter(data[class_i][0], data[class_i][1], s = 200, color=cmap(class_i))     
        
class RuleSet:
    def __init__(self):
        print("RULESET\n dataset name:")
        self.datasetName = input()
        self.detaset_df = detaset_df(self.datasetName)
        self.fileName = self.datasetName + "_ruleset.xml"
        self.fileName_ClassifyResult = self.datasetName + "_ClassifyResult.xml"
        self.savePath = "gen_max\\result\\" + self.datasetName
        # pathList = glob.glob("gen_max\\" + self.datasetName + "\trial_*")
        pathList = glob.glob("gen_max\\" + self.datasetName + "\\*")
        for path_buf1 in pathList:
            savePath = self.savePath + "\\" + path_buf1.split("\\")[-1]
            path_bnuf2 =  glob.glob(path_buf1 + "\\trial_0\gen_1000000")
            for path in path_bnuf2:
                print(path)
                savePath_buf = savePath + "\\gen_{:0>6}".format(path.split("\\")[-1].split('_')[1])
                print(savePath_buf)
                buf1 = RuleSetXML(path + "\\" + self.datasetName + "_ruleset.xml", savePath_buf, self.datasetName)
                buf1.UsedMenbershipRate(isCoverAllClasses = False)
                ClassifyResult(path + "\\" + self.datasetName + "_classifyResult.xml", savePath_buf, self.datasetName)
