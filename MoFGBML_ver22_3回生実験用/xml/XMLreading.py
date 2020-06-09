import xml.etree.ElementTree as ET
import matplotlib.pyplot as plt
import numpy as np
from sklearn import datasets
from sklearn import preprocessing
import pandas as pd
import os
from datetime import datetime
    
trial_plot = 2 #plotするtriaslのID
gen_plot = 1000 #plotする世代数
my_path = os.getcwd()
    
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
        
def figSave(fig, filename = None):
    if filename == None:
        print("image file name:")
        filename = input()
    now = datetime.now()
    buf = filename + "_{0:%Y%m%d%H%M%S}_{1:%f}.png".format(now, now)
    fig.savefig(os.path.join(my_path + '\png', buf))        


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
        
    def show(self):
        dim_plot = 0
        for i, ft in self.FSs[dim_plot].items():
            print(ft.parameters)
            ft.show()
            
    def setAx(self, ax):
        dim_plot = 0
        for i, ft in self.FSs[dim_plot].items:
            ft.setAx(ax)
            
    def showID(self, dim, i):
        print(self.FSs[dim][i].parameters)
        self.FSs[dim][i].show()
        
    def setID(self, dim, i, ax):
        self.FSs[dim][i].setAx(ax)
        
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
        for dim in range(Num):
            buf = fig.add_subplot(Num/3+1, 3, dim+1)
            ax.append(buf)
            ax[dim].grid(True)
            ax[dim].set_ylim(-0.17, 1.05)
            ax[dim].set_xlim(-0.05, 1.05)
            self.kb.setID(dim, self.rule[dim], ax[dim])
#            irisplot(plt, dim)
        figSave(fig, "Rule")
        
    def setAx(self, ax):
        for dim, ID in enumerate(self.rule.values()):
            self.kb.setID(dim, ID, ax[dim])

class individual_ruleset():
    
    def __init__(self, kb_input, individual):
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
            
    def showDim(self):
        dimNum = len(self.rules[0].rule)
        fig = plt.figure(figsize = (24, (dimNum/3+1)*6))
        ax = []
        for dim in range(dimNum):
            buf = fig.add_subplot(dimNum/3+1, 3, dim+1)
            ax.append(buf)
            ax[dim].grid(True)
            ax[dim].set_ylim(-0.17, 1.05)
            ax[dim].set_xlim(-0.05, 1.05)
            ax[dim].set_title("RuleDim" + str(dim))
            
        for rule in self.rules.values():
            rule.setAx(ax)
        
        figSave(fig, "individual_Dim")
        plt.close(fig)
        del ax
            
    def setAx(self, ax):            
        for rule in self.rules.values():
            rule.setAx(ax)       
            
class population_ruleset():
    def __init__(self, population):
        #KB
        self.kb = KB(population.find('KnowledgeBase'))
        #XML_ruleクラスの配列(len:個体数)
        self.individual = [individual_ruleset(self.kb, buf) for buf in population.findall('individual')]
        self.gen = int(population.get('generation'))
        self.trial = int(population.get('trial'))
        
    def show(self):
        dimNum = len(self.individual[0].rules[0].rule)
        fig = plt.figure(figsize = (24, (dimNum/3+1)*6))
        ax = []
        for dim in range(dimNum):
            buf = fig.add_subplot(dimNum/3+1, 3, dim+1)
            ax.append(buf)
            ax[dim].grid(True)
            ax[dim].set_ylim(-0.17, 1.05)
            ax[dim].set_xlim(-0.05, 1.05)
            for i in range(len(self.individual)):
                for j in range(len(self.individual[i].rules)):
                    fuzyytermID = self.individual[i].rules[j].rule[dim]
                    self.kb.setID(dim, fuzyytermID, ax[dim])
            ax[dim].set_title("RuleDim" + str(dim))
#            irisplot(plt, dim)
        figSave(fig, "Population_" + str(self.trial) + "_" + str(self.gen))
    
class fuzzyterm:
    def __init__(self, fuzzyterm):
        self.name =  fuzzyterm.find("name").text
        self.TypeID = int(fuzzyterm.find("Shape_Type_ID").text)
        self.parameters = {}
        for buf in fuzzyterm.find('parameters'):
            self.parameters[int(buf.get('id'))] = float(buf.text)
    
    def setAx(self, ax):
#        ax.set_title(self.name)
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
            
    def show(self):
        fig = plt.figure()
        ax = fig.add_subplot(1, 1, 1)
        ax.grid(True)
        ax.set_ylim(-0.05, 1.05)
        ax.set_xlim(-0.05, 1.05)
        self.setAx(plt)
        fig.show()
        
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
        fig = plt.figure(figsize = (24, (10/3+1)*6))
        ax = []
        for i, dim in enumerate(range(100, 1100, 100)):
            buf = fig.add_subplot(10/3+1, 3, i+1)
            ax.append(buf)
            ax[i].grid(True)
            ax[i].set_xlim(1.5, 10)
            ax[i].set_ylim(25, 60)
            self.single.setplotTrialGen(trial_plot, dim, ax[i], name = "single")
            self.multi.setplotTrialGen(trial_plot, dim, ax[i], name = "multi")
            ax[i].legend(loc='upper right')
            ax[i].set_title("dim:" + str(dim))
        fig.show()
        
    def plot_2(self):
        fig = plt.figure(figsize = (24, (10/3+1)*6))
        ax = []
        i = 0
        dim = 1000
        buf = fig.add_subplot(10/3+1, 3, i+1)
        ax.append(buf)
        ax[i].grid(True)
        ax[i].set_xlim(1.5, 10)
        ax[i].set_ylim(25, 60)
        self.single.setplotTrialGen(trial_plot, dim, ax[i], name = "single")
        self.multi.setplotTrialGen(trial_plot, dim, ax[i], name = "multi")
        ax[i].legend(loc='upper right')
        ax[i].set_title("dim:" + str(dim))
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
            
    def getIndividual(self, trial = 0, gen = 1000, ID = 0):
        return self.rs_tri_gen[trial][gen].individual[ID]
    
    def getKB(self, trial = 0, gen = 1000):
        return self.rs_tri_gen[trial][gen].kb
    
    def plot(self, trial = 0, gen = 1000, ruleID = 0):
        self.rs_tri_gen[trial][gen].show()
        
    def selectedRuleset(self):
        print("class num")
        classNum = int(input())
        buf = []
        ruleNum = 100
        for trial in self.rs_tri_gen.values():
            population = trial[1000]
            for individual in population.individual:
                check = {i : False for i in range(classNum)}
                for l, SingleRule in enumerate(individual.rules.values()):
                    check[SingleRule.conclusion] = True
                if(all(check.values())):
                    if len(individual.rules) < ruleNum:
                        ruleNum = len(individual.rules)
                    buf.append(individual)

        rulelist = [ind for ind in buf if len(ind.rules) == ruleNum]
        for tmp in rulelist:
            tmp.showDim()
        
    def selectedTrial(self):
        print("class num")
        classNum = int(input())
        dimNum = len(self.getIndividual().rules[0].rule)
            
        for trial in self.rs_tri_gen.values():
            fig = plt.figure(figsize = (24, (dimNum/3+1)*6))
            ax = []
            for dim in range(dimNum):
                buf = fig.add_subplot(dimNum/3+1, 3, dim+1)
                ax.append(buf)
                ax[dim].grid(True)
                ax[dim].set_ylim(-0.17, 1.05)
                ax[dim].set_xlim(-0.05, 1.05)
                ax[dim].set_title("RuleDim" + str(dim))
                
            buf = []
            ruleNum = 100
            population = trial[1000]
            for individual in population.individual:
                check = {i : False for i in range(classNum)}
                for l, SingleRule in enumerate(individual.rules.values()):
                    check[SingleRule.conclusion] = True
                if(all(check.values())):
                    if len(individual.rules) < ruleNum:
                        ruleNum = len(individual.rules)
                    buf.append(individual)
            rulelist = [ind for ind in buf if len(ind.rules) == ruleNum]
            for tmp in rulelist:
                tmp.setAx(ax)
            figSave(fig, "RuleCoverAll_buf")
            plt.close(fig)
            del ax, buf
            
    def selectedAll(self):
        print("class num")
        classNum = int(input())
        dimNum = len(self.getIndividual().rules[0].rule)
            
        fig = plt.figure(figsize = (24, (dimNum/3+1)*6))
        ax = []
        for dim in range(dimNum):
            buf = fig.add_subplot(dimNum/3+1, 3, dim+1)
            ax.append(buf)
            ax[dim].grid(True)
            ax[dim].set_ylim(-0.17, 1.05)
            ax[dim].set_xlim(-0.05, 1.05)
            ax[dim].set_title("RuleDim" + str(dim))
            
        for trial in self.rs_tri_gen.values():
            buf = []
            ruleNum = 100
            population = trial[1000]
            for individual in population.individual:
                check = {i : False for i in range(classNum)}
                for l, SingleRule in enumerate(individual.rules.values()):
                    check[SingleRule.conclusion] = True
                if(all(check.values())):
                    if len(individual.rules) < ruleNum:
                        ruleNum = len(individual.rules)
                    buf.append(individual)
                    
        rulelist = [ind for ind in buf if len(ind.rules) == ruleNum]
        print(len(rulelist))
        for tmp in buf:
            tmp.setAx(ax)
        figSave(fig, "RuleCoverAll_All")
        plt.close(fig)
        del ax, buf