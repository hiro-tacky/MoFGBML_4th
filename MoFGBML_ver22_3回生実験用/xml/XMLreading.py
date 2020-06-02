import xml.etree.ElementTree as ET
import matplotlib.pyplot as plt
import numpy as np
from sklearn import datasets
from sklearn import preprocessing
import pandas as pd
import os
    

def irisplot(plt, j):
    iris = datasets.load_iris()
    mm = preprocessing.MinMaxScaler()
    iris_data = mm.fit_transform(iris.data)
    #irisデータ追加    
    iris_element = [[] for i in range(len(iris.feature_names)-1)]
    for i in range(len(iris_data)):
        iris_element[iris.target[i]].append(iris_data[i][j])
        
    for i in range(len(iris_element)):
        y = [-(0.03+i*0.05) for j in range(len(iris_element[i]))]
        plt.scatter(iris_element[i], y, marker='s', alpha=0.1)

def nodelist(root):
    i = 0
    for child in root:
        txt = "node"
        if child.text != None:
            txt = str(child.text)
        print("{:3d}: {:20}{:10}{}".format(i, child.tag, str(txt), child.attrib))
        i += 1;
        
def figSave(fig):
        print("image file name:")
        filename = input()
        my_path = os.getcwd()
        fig.savefig(os.path.join(my_path + '\png', filename))        


#xmlの基本的な操作スーパークラス
class XML:
    def __init__(self, filename):
        self.tree = ET.parse(filename)
        self.root = self.tree.getroot()
        nodelist(self.root)
        self.CurrentElement = []
    
        
    def down(self, id):
        self.CurrentElement.append(id)
        buf = self.root
        for i in self.CurrentElement:
            if len(list(buf)) == 0:
                self.CurrentElement.pop(-1)
                print("error:null")
                return;
            buf = buf[i]
        print("current node:" + buf.tag)
        print()
        i=0
        for child in buf:
            txt = "node"
            if child.text != None:
                txt = str(child.text)
            print("{:3d}: {:20}{:10}{}".format(i, child.tag, str(txt), child.attrib))
            i += 1;
            
    def up(self):
        self.CurrentElement.pop(-1)
        buf = self.root
        for i in self.CurrentElement:
            buf = buf[i]
        print()
        i=0
        for child in buf:
            txt = "node"
            if child.text != None:
                txt = str(child.text)
            print("{:3d}: {:20}{:10}{}".format(i, child.tag, str(txt), child.attrib))
            i += 1;
            
    def up_root(self):
        self.CurrentElement.clear()
        nodelist(self.root)
        
#KB用サブクラス
class KB():
    
    def __init__(self, KB):
        #FSs[次元][FuzzyTerm ID]
        self.FSs = {}
        self.gen = int(KB.get('generation'))
        self.trial = int(KB.get('trial'))
        for dim, fs in enumerate(KB.findall('FuzzySet')):
            FS = {}
            for i, ft in enumerate(fs.findall('FuzzyTerm')):
                FS[i] = fuzzyterm(ft)
            self.FSs[dim] = FS
        
    def Show(self):
        for i, ft in self.FSs[0].items:
            print(ft.parameters)
            ft.Show()
            
    def Set(self, ax):
        for i, ft in self.FSs[0].items:
            ft.Set(ax)
            
    def ShowID(self, dim, i):
        print(self.FSs[dim][i].parameters)
        self.FSs[dim][i].Show()
        
    def SetID(self, dim, i, ax):
        self.FSs[dim][i].Set(ax)
        
class singleRule_ruleset():
    
    def __init__(self, SingleRule, kb_input):
        #前件部ので用いられるファジィ変数(len:次元)
        self.rule = {}
        for i, element in enumerate(SingleRule.find('rule')):
            self.rule[i] = int(element.text)
        self.conclusion = int(SingleRule.find('conclusion').text)
        self.cf = float(SingleRule.find('cf').text)
        self.fitness = float(SingleRule.find('fitness').text)
        self.kb = kb_input
        
    def show(self):
        print("conclusion:" + str(self.conclusion))
        print("cf:" + str(self.cf))
        print("fitness:" + str(self.fitness))
        Num = len(self.rule)
        fig = plt.figure(figsize = (24, (Num/3+1)*6))
        ax = []
        for dim in range(len(self.rule)):
            buf = fig.add_subplot(Num/3+1, 3, dim+1)
            ax.append(buf)
            ax[dim].grid(True)
            ax[dim].set_ylim(-0.17, 1.05)
            ax[dim].set_xlim(-0.05, 1.05)
            self.kb.setID(dim, self.rule[dim], ax[dim])
#            irisplot(plt, dim)
        figSave(fig)

class individual_ruleset():
    
    def __init__(self, kb_input, individual):
        self.gen = int(individual.get('generation'))
        self.trial = int(individual.get('trial'))
        self.f0 = float(individual.find('f0').text)
        self.f1 = float(individual.find('f1').text)
        self.rank = int(individual.find('rank').text)
        self.crowding = float(individual.find('crowding').text)
        #rules [singlerule]
        self.rules = {}
        for i, SingleRule in enumerate(individual.find('ruleSet')):
            self.rules[i] = singleRule_ruleset(SingleRule, kb_input)
            
    #i = 個体ID
    def show(self):
        for i in self.rules.keys():
            print("============rule ID:"+str(i)+ "================")
            self.rules[i].show()
            
class population_ruleset():
    def __init__(self, population):
        #KB
        self.kb = KB(population.find('KnowledgeBase'))
        #XML_ruleクラスの配列(len:個体数)
        self.individual = [individual_ruleset(self.kb, buf) for buf in population.findall('individual')]
        self.gen = int(population.get('generation'))
        self.trail = int(population.get('trial'))
        
    def show(self):
        dinNum = len(self.individual[0].rules[0].rule)
        fig = plt.figure(figsize = (24, (dinNum/3+1)*6))
        ax = []
        for dim in range(dinNum):
            buf = fig.add_subplot(dinNum/3+1, 3, dim+1)
            ax.append(buf)
            ax[dim].grid(True)
            ax[dim].set_ylim(-0.17, 1.05)
            ax[dim].set_xlim(-0.05, 1.05)
            for i in range(len(self.individual)):
                for j in range(len(self.individual[i].rules)):
                    fuzyytermID = self.individual[i].rules[j].rule[dim]
                    self.kb.SetID(dim, fuzyytermID, ax[dim])
            ax[dim].set_title("RuleDim" + str(dim))
#            irisplot(plt, dim)
        figSave(fig)
    
class fuzzyterm:
    def __init__(self, fuzzyterm):
        self.name =  fuzzyterm.find("name").text
        self.TypeID = int(fuzzyterm.find("Shape_Type_ID").text)
        self.parameters = {}
        for buf in fuzzyterm.find('parameters'):
            self.parameters[int(buf.get('id'))] = float(buf.text)
    
    def Set(self, ax):
        ax.set_title(self.name)
        if self.TypeID == 3:
            x = np.array([0, self.parameters[0], self.parameters[1], self.parameters[2], 1])
            y = np.array([0, 0, 1, 0, 0])
            ax.plot(x, y)
        if self.TypeID == 4:
            mu = self.parameters[0]
            sigma = self.parameters[1]
            x = np.arange(0, 1, 0.01)
            y = 1 * np.exp(-(x - mu)**2 / (2*sigma**2))
            ax.plot(x, y)
        if self.TypeID == 7:
            x = np.array([0, self.parameters[0], self.parameters[1], self.parameters[2], self.parameters[3], 1])
            y = np.array([0, 0, 1, 1, 0, 0])
            ax.plot(x, y)
        if self.TypeID == 9:
            x = np.array([0, self.parameters[0], self.parameters[0], self.parameters[1], self.parameters[1], 1])
            y = np.array([0, 0, 1, 1, 0, 0])
            ax.plot(x, y)
            
    def Show(self):
        plt.title(self.name)
        plt.grid(True)
        plt.ylim(-0.05, 1.05)
        plt.xlim(-0.05, 1.05)
        self.Set(plt)
        plt.show()
        
class result(XML):
    def __init__(self, filename):
        super(result, self).__init__(filename)
        #data_tri_gen[trial][gen] = population(dataframe)
        self.data_tri_gen = {}
        for i, trial in enumerate(self.root.findall('trial')):
            pop = {}
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
                pop[int(population.get('generation'))] = pd.DataFrame(df)
            self.data_tri_gen[i] = pop
            
    def getDataFrame(self, trial, gen):
        return self.data_tri_gen[trial][gen]
    
    def plotGen(self, gen):
        fig = plt.figure()
        ax = fig.add_subplot(1, 1, 1)
        self.setplotGen(gen, ax)
        ax.show()
        
    def plotTrialGen(self, trial, gen):
        fig = plt.figure()
        ax = fig.add_subplot(1, 1, 1)
        self.setplotTrialGen(trial, gen, ax)
        ax.show()
            
    def setplotGen(self, gen, ax):
        x = []
        y = []
        for buf in self.data_tri_gen.values():
            data = buf[gen]
            x += list(data['f1'])
            y += list(data['f0'])
        ax.scatter(x, y, alpha = 0.01)
        
    def setplotTrialGen(self, trial, gen, ax, name = ""):
        data = self.getDataFrame(trial, gen)
        df = data[data['f1'] != 1] 
        x = list(df['f1'])
        y = list(df['f0'])
        ax.scatter(x, y, label = name) 
        
class dataset():
    def __init__(self):
        print("dataset name:")
        self.filename = input()
        single_name = self.filename + "_result_single.xml"
        print(single_name)
        self.single = result(single_name)
        multi_name = self.filename + "_result.xml"
        print(multi_name)
        self.multi = result(multi_name)
        
    def plot(self):
        fig = plt.figure()
        ax = fig.add_subplot(1, 1, 1)
        ax.grid(True)
        self.single.setplotTrialGen(0, 1000, ax, name = "single")
        self.multi.setplotTrialGen(0, 1000, ax, name = "multi")
        ax.legend(loc='upper right')
        fig.show()
            
class ruleset(XML):
    def __init__(self, filename):
        super(ruleset, self).__init__(filename)
        self.rs_tri_gen = {}
        for i, trial in enumerate(self.root.findall('trial')):
            pop = {}
            for population in trial.findall('population'):
                pop[int(population.get('generation'))] = population_ruleset(population)
            self.rs_tri_gen[i] = pop
            
    def getRuleset(self, trial, gen):
        return self.rs_tri_gen[0][1000]
    
    def plot(self, trial = 0, gen = 1000, ruleID = 0):
        self.rs_tri_gen[trial][gen].show()