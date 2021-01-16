import xml.etree.ElementTree as ET
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import os
from datetime import datetime
import glob

#######  setting  #######
###############################################################################
#数値実験基本設定
trial_num = 30 #試行回数
gen_num = 5000 #世代数
individual_num = 50 #個体数
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
    fig.savefig(my_path + "/" + filePath + "/" + imageName, transparent=False)     

class XML:
    """xmlファイルを読み込むためのスーパークラス"""
    def __init__(self, filename):
        self.tree = ET.parse(filename)
        self.rootNode = self.tree.getroot()
        nodelist(self.rootNode)
        self.CurrentElement = [] #木構造の現在参照しているノードの位置を保存する
        
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
        nodelist(buf)
            
    def up(self):
        """指定したIDの親要素を参照する"""
        self.CurrentElement.pop(-1)
        buf = self.rootNode
        for i in self.CurrentElement:
            buf = buf[i]
        print("current node:" + buf.tag)
        print(buf.attrib)
        print("\n")
        nodelist(buf)
            
    def root(self):
        """木の根にもどる"""
        self.CurrentElement.clear()
        nodelist(self.rootNode)
            
class detaset_df:
    def __init__(self, datasetName):
        self.datasetName = datasetName
        self.attributeNum = DatasetList[self.datasetName]["Attribute"]
        df_original = pd.read_csv("./dataset/" + self.datasetName + ".csv", header=None)
        df_buf = df_original.iloc[:, 0:self.attributeNum]
        classList_buf = df_original.iloc[:, self.attributeNum]
        self.df = pd.concat([(df_buf - df_buf.min()) / (df_buf.max() - df_buf.min()), classList_buf], axis=1, join='inner') #正規化されたdetaframe
        # self.classDict = 
        
class FuzzyTerm:
    """Fuzzy Termのためのクラス"""
    def __init__(self, fuzzyterm):
        self.name =  fuzzyterm.find("name").text
        self.typeID = int(fuzzyterm.find("Shape_Type_ID").text)
        self.ID = int(fuzzyterm.get("ID"))
        self.Shape_Type = fuzzyterm.find("name").text
        self.parameters = {}
        for buf in fuzzyterm.find('parameters'):
            self.parameters[int(buf.get('id'))] = float(buf.text)
    
    def setAx(self, ax, alpha = 1.0, color = "black"):
        """Ax にメンバシップ関数をプロットする"""
        color_buf = "C{}".format(color) if type(color) is int else color
        
        ax.set_ylim(-0.05, 1.05)
        if self.typeID == 3:
            x = np.array([0, self.parameters[0], self.parameters[1], self.parameters[2], 1])
            y = np.array([0, 0, 1, 0, 0])
            ax.plot(x, y, alpha = alpha, color = color_buf)
            y_bottom = [0, 0, 0, 0, 0]
            ax.fill_between(x, y, y_bottom, facecolor="blue", alpha=0.1)
        if self.typeID == 4:
            mu = self.parameters[0]
            sigma = self.parameters[1]
            x = np.arange(0, 1, 0.01)
            y = 1 * np.exp(-(x - mu)**2 / (2*sigma**2))
            ax.plot(x, y, alpha = alpha, color = color_buf)
            y_bottom = [0] * 100
            ax.fill_between(x, y, y_bottom, facecolor="blue", alpha=0.1)
        if self.typeID == 7:
            x = np.array([0, self.parameters[0], self.parameters[1], self.parameters[2], self.parameters[3], 1])
            y = np.array([0, 0, 1, 1, 0, 0])
            ax.plot(x, y, alpha = alpha, color = color_buf)
            y_bottom = [0, 0, 0, 0, 0, 0]
            ax.fill_between(x, y, y_bottom, facecolor="blue", alpha=0.1)
        if self.typeID == 9:
            x = np.array([0, self.parameters[0], self.parameters[0], self.parameters[1], self.parameters[1], 1])
            y = np.array([0, 0, 1, 1, 0, 0])
            ax.plot(x, y, alpha = alpha, color = color_buf)
            y_bottom = [0, 0, 0, 0, 0, 0]
            ax.fill_between(x, y, y_bottom, facecolor="blue", alpha=0.1)
        if self.typeID == 99:
            x = np.array([0, 0, 1, 1])
            y = np.array([0, 1, 1, 0])
            ax.plot(x, y, alpha = alpha, color = color_buf)
            y_bottom = [0, 0, 0, 0]
            ax.fill_between(x, y, y_bottom, facecolor="blue", alpha=0.1)
        
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
        
    def plot(self, savePath, isSave = True, inOneFig = False, Dataset_df = None):
        if inOneFig:
            for dimension, FuzzySet in self.fuzzySets.items():
                fig = singleFig_set("KnowledgeBase_trial" + str(self.trial) + "_gen" + str(self.gen) + "_Attribute" + str(dimension))
                ax = fig.gca()
                for FuzzyTermID, FuzzyTerm in FuzzySet.items():
                    self.fuzzySets[dimension][FuzzyTermID].setAx(ax)
                if isSave:
                    SaveFig(fig, savePath + "KnowledgeBase/FuzzySetALL/", \
                            self.datasetName + "_KnowledgeBase_trial" + str(self.trial) + "_gen" + str(self.gen) + "_Attribute" + str(dimension))
                elif not isSave:
                    fig.show()
                plt.close("all")
        elif not inOneFig:
            for dimension, FuzzySet in self.fuzzySets.items():
                for FuzzyTermID, FuzzyTerm in FuzzySet.items():
                    fig = singleFig_set("KnowledgeBase_trial" + str(self.trial) + "_gen" + str(self.gen) + "_Attribute" + str(dimension) + "_FuzzyTermID" + str(FuzzyTermID))
                    ax = fig.gca()
                    self.fuzzySets[dimension][FuzzyTermID].setAx(ax)
                    if isSave:
                        SaveFig(fig, savePath + "KnowledgeBase/FuzzySetSingle/Attribute_" + str(dimension) + "/", \
                                self.datasetName + "_KnowledgeBase_trial" + str(self.trial) + "_gen" + str(self.gen) + "_Attribute" + str(dimension) + "_FuzzyTermID" + str(FuzzyTermID))
                    elif not isSave:
                        fig.show()
                    plt.close("all")
        
    def setFuzzyTerm(self, ax, dimension = attri_plot, ID = 0, alpha = 1.0, color = "black"):
        self.fuzzySets[dimension][ID].setAx(ax, alpha = alpha, color = color)
        
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
        self.ruleNum = individual.get("ruleNum")
   
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
        self.ruleset = {}
        for i, trial in enumerate(self.rootNode.findall('trial')):
            populations = {int(population.get("generation")): Population(population, self.datasetName) for population in trial.findall('population')}
            self.ruleset[i] = populations #[trial][generation] = Population
        self.allIndividual = {(i, gen_plot):(j for j in range(individual_num)) for i in range(trial_num)}

    def getPopulation(self, ID_tuple):
        return self.ruleset[ID_tuple[0]][ID_tuple[1]]
    
    def KBplot(self, trial = None, generation = None, inOneFig = False):
        trial_list = [trial] if type(trial) is int else trial
        generation_list = [generation] if type(generation) is int else generation
        if trial is None and generation is None:
            for trialID, populations in self.ruleset.items():
                for generation, population in populations.items():
                    population.kb.plot(self.savePath + "trial_" + str(trialID) + "/generation_" + str(generation) + "/", isSave = True, inOneFig = inOneFig, Dataset_df = None)
        else:
            for trialID in trial_list:
                for generationID in generation_list:
                    self.ruleset[trialID][generationID].kb.plot(self.savePath + "trial_" + str(trialID) + "/generation_" + str(generation) + "/", isSave = True, inOneFig = inOneFig, Dataset_df = None)
           
                
    def IndividualsCoverAllclasses(self):
        buf = {}
        for trialKey, trial in self.ruleset.items():
            population = trial[gen_plot]
            buf_population = (trialKey, gen_plot)
            buf_individual = set()
            for individualID, individual in population.individuals.items():
                if individual.isCoverAllClasses():
                    buf_individual.add(individualID)
            buf[buf_population] = buf_individual
        return buf   
                    
    def CoverAllClasses(self, saveFilePath, isDontCare = False):
        data = {shapeID: [0] * self.attributeNum for shapeID in FuzzyTypeID.keys()}
        for populationID, individualID in self.IndividualsCoverAllclasses().items():
            population = self.getPopulation(populationID)
            for ID in individualID:
                individual = population.getIndividual(ID)
                for singleRule in individual.rules.values():
                    for atrriID, fuzzyTermID in singleRule.rule.items():
                        tmp = population.kb.getShapeID(fuzzyTermID)
                        data[tmp][atrriID] += 1
            
        fig = singleFig_set(self.datasetName + " [used manbership that cover all classes]")
        ax = fig.gca()
        x = [i+1 for i in range(self.attributeNum)]
        buf = [0]*self.attributeNum
        for shapeID, dataList in data.items():
            if isDontCare and shapeID == DontCareID:
                continue
            ax.bar(x, dataList, bottom = buf, label = FuzzyTypeID[shapeID])
            for i, data_i in enumerate(dataList):
                buf[i] += data_i
        ax.legend()
        ax.set_xlabel('Attribute')
        ax.set_ylabel('num of manbership')
        SaveFig(fig, saveFilePath, self.datasetName + "_CoverAllClasses")
        
    def ByConclusion(self, saveFilePath):
        fuzzyTerm_list = {}
        for populationID, individualID in self.allIndividual.items():
            for ID in individualID:
                individual = self.ruleset[populationID[0]][populationID[1]].individuals[ID]
                for singleRule in individual.rules.values():
                    conclusion = singleRule.conclusion
                    if conclusion not in fuzzyTerm_list:
                        fuzzyTerm_list[conclusion] = {attriID:{} for attriID in singleRule.rule.keys()}
                    for ID, fuzzyID in singleRule.rule.items():
                        if fuzzyID not in fuzzyTerm_list[conclusion][ID]:
                            fuzzyTerm_list[conclusion][ID][fuzzyID] = 0
                        fuzzyTerm_list[conclusion][ID][fuzzyID] += 1
        
        for conclusionID, FT_Conclusion in fuzzyTerm_list.items():
            fig = multiFig_set(self.attributeNum, "test")
            axes = fig.axes
            dataNorm = {}
            for attributeID, FT_Attribute in FT_Conclusion.items():
                min_buf,max_buf  = min(FT_Attribute.values()), max(FT_Attribute.values())
                rang = max_buf - min_buf
                dataNorm[attributeID] = {fuzzyTermID: (buf-min_buf)/rang for fuzzyTermID, buf in FT_Attribute.items()}
            IDList = {}
            for attributeID, FT_Attribute in FT_Conclusion.items():
                sum_buf = float(sum(FT_Attribute.values()))
                IDList[attributeID] = {fuzzyTermID: buf/sum_buf for fuzzyTermID, buf in FT_Attribute.items()}
            for ID, ax in enumerate(axes):
                for fuzzyTermID, num in dataNorm[ID].items():
                    self.getPopulation((0, 5000)).kb.setFuzzyTerm(ax, fuzzyTermID, alpha = num * 5, color = conclusionID)
                ax_buf = ax.twinx()
                for i in fuzzyTerm_list.keys():
                    myarray = self.df[self.df[self.attributeNum] == self.conClasses[i]][ID]
                    ax_buf.hist(myarray, alpha = 0.5, bins = 30)
            SaveFig(fig, saveFilePath, self.datasetName + "_Menbership_conclusion_" + str(conclusionID))
                
class RuleSet:
    def __init__(self):
        print("RULESET\n dataset name:")
        self.datasetName = input()
        self.detaset_df = detaset_df(self.datasetName)
        self.FuzzyTypeList = ["trapezoid"]#["rectangular", "trapezoid", "gaussian", "multi"]
        self.folderList = ["entropy_0.7"] #["default", "entropy"]
        self.pathList = []
        self.RuleSetObj = {} #[FuzzyTypeList][folderList] = RuleSetXMLオブジェクト
        for fuzzyType in self.FuzzyTypeList:
            RuleSetObj_buf = {}
            for folderName in self.folderList:
                self.fileName = "*" + fuzzyType + "_ruleset.xml"
                self.pathList = glob.glob(folderName + "/" + self.datasetName + "*/" + self.datasetName + "_" + fuzzyType + "*/" + self.fileName)
                self.savePath = "result/" + self.datasetName + "/" + folderName + "/" + fuzzyType + "/" #変更忘れるな 
                for path in self.pathList:
                    print(path)
                    RuleSetObj_buf[folderName] = RuleSetXML(path, self.savePath, self.datasetName)
            self.RuleSetObj[fuzzyType] = RuleSetObj_buf
            
        self.KBplot()                
            
    def getRuleSetXML(self, fuzzyType = "multi", folderName = "default"):
        return self.RuleSetObj[fuzzyType][folderName]
                
    def RuleSetPlot(self):
        for fuzzyType, RuleSetObj_dict in self.RuleSetObj.items():
            for folderName, RuleSet in RuleSetObj_dict.items():
                if fuzzyType == "multi":
                    RuleSet.CoverAllClasses()
                RuleSet.ByConclusion()
    
    def KBplot(self):
        for fuzzyType, RuleSetObj_dict in self.RuleSetObj.items():
            for folderName, RuleSet in RuleSetObj_dict.items():
                print(RuleSet.savePath)
                RuleSet.KBplot(0, 5000, inOneFig = True)