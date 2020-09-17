import xml.etree.ElementTree as ET
import matplotlib.pyplot as plt
import numpy as np
from sklearn import datasets
from sklearn import preprocessing
import pandas as pd
import os
from datetime import datetime
    
trial_plot = 1 #plotするtriaslのID
gen_plot = 1000 #plotする世代数
dim_plot = 0
trial_num = 30
my_path = os.getcwd()
FuzzyTypeNum = {3:"triangle", 4:"gaussian", 7:"trapezoid", 9:"rectangle"}

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

lim = lambda s, g: int((g-s)/10) if int((g-s)/10) != 0 else 1

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
        
def figSave(fig, filename = None, datesetname = "others"):
    if filename == None:
        print("image file name:")
        filename = input()
    dirname = datesetname + "/"
    os.makedirs(dirname, exist_ok=True)
    now = datetime.now()
    buf = filename + "_{0:%Y%m%d%H%M%S}_{1:%f}.png".format(now, now)
    fig.savefig(os.path.join(my_path + '\\' + datesetname, buf), transparent=False)        


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
            
    def top(self):
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
        for i, ft in self.FSs[dim_plot].items():
            print(ft.parameters)
            ft.show()
            
    def setAx(self, ax):
        for i, ft in self.FSs[dim_plot].items:
            ft.setAx(ax)
            
    def showID(self, dim, i):
        print(self.FSs[dim][i].parameters)
        self.FSs[dim][i].show()
        
    def setID(self, dim, i, ax):
        self.FSs[dim][i].setAx(ax)
        
    def getFuzzyTermID(self, dim, i):
        return self.FSs[dim][i]
        
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
        Num = len(self.rule)
        fig = plt.figure(figsize = (24, ((Num-1)/3+1)*6))
        ax = []
        boxdic = { "facecolor" : "white", "edgecolor" : "black", "linewidth" : 2}
        fig.text(0.5, 0.06, "con:" + str(self.conclusion) + "  cf:" + str(self.cf) + "  fit:" + str(self.fitness), size = 40, bbox = boxdic)
        for dim in range(Num):
            buf = fig.add_subplot((Num+2)/3, 3, dim+1)
            ax.append(buf)
            ax[dim].grid(True)
            ax[dim].set_ylim(-0.17, 1.05)
            ax[dim].set_xlim(-0.05, 1.05)
            ax[dim].set_title("Dim: " + str(dim))
            self.kb.setID(dim, self.rule[dim], ax[dim])
#            irisplot(plt, dim)
        figSave(fig, "singleRule")
        
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
        self.gen = int(individual.get('generation'))
        self.trial = int(individual.get('trial'))
            
    #i = 個体ID
    def show(self):
        for i in self.rules.keys():
            self.rules[i].show()
            
    def showDim(self):
        dimNum = len(self.rules[0].rule)
        fig = plt.figure(figsize = (24, ((dimNum-1)/3+1)*6))
        ax = []
        for dim in range(dimNum):
            buf = fig.add_subplot((dimNum+2)/3, 3, dim+1)
            ax.append(buf)
            ax[dim].grid(True)
            ax[dim].set_ylim(-0.17, 1.05)
            ax[dim].set_xlim(-0.05, 1.05)
            ax[dim].set_title("Rule_Dim" + str(dim))
            
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
        fig = plt.figure(figsize = (24, ((dimNum-1)/3+1)*6))
        ax = []
        for dim in range(dimNum):
            buf = fig.add_subplot((dimNum+2)/3, 3, dim+1)
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
        if self.TypeID == 99:
            x = np.array([0, 0, 1, 1])
            y = np.array([0, 1, 1, 0])
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
        
    def setplotGenAve(self, gen, ax, name = ""):
        ave = {}
        for i, pop in enumerate(self.data_tri_gen.values()):
            data = self.getDataFrame(i, gen)
            df = data[data['f1'] != 1]
            f0_f1 = df[['f0', 'f1']]
            for buf in f0_f1.itertuples():
                try:
                    ave[buf.f1][0] += buf.f0
                    ave[buf.f1][1] += 1                    
                except:
                    ave[buf.f1] = [buf.f0, 1]
        x = [ruleNum for ruleNum, average in ave.items() if average[1] > trial_num/2]
        y = [average[0]/average[1] for average in ave.values() if average[1] > trial_num/2]
        ax.scatter(x, y, label = name)

    def setplotGenAve_tst(self, gen, ax, name = ""):
        ave = {}
        for i, pop in enumerate(self.data_tri_gen.values()):
            data = self.getDataFrame(i, gen)
            df = data[data['f1'] != 1]
            Dtst_f1 = df[['Dtst', 'f1']]
            for buf in Dtst_f1.itertuples():
                try:
                    ave[buf.f1][0] += buf.Dtst
                    ave[buf.f1][1] += 1                    
                except:
                    ave[buf.f1] = [buf.Dtst, 1]
        x = [ruleNum for ruleNum, average in ave.items() if average[1] > trial_num/2]
        y = [average[0]/average[1] for average in ave.values() if average[1] > trial_num/2]
        ax.scatter(x, y, label = name)

    def setplotGenBest(self, gen, ax, name = ""):
        best = {}
        for i, pop in enumerate(self.data_tri_gen.values()):
            data = self.getDataFrame(i, gen)
            df = data[data['f1'] != 1]
            f0_f1 = df[['f0', 'f1']]
            best_pop = {}
            for buf in f0_f1.itertuples():
                try:
                    if best_pop[buf.f1] > buf.f0:
                        best_pop[buf.f1] = buf.f0                        
                except:
                    best_pop[buf.f1] = buf.f0
            for RuleNum, bestValue in best_pop.items():
                try:
                    best[RuleNum][0] += bestValue
                    best[RuleNum][1] += 1                    
                except:
                    best[RuleNum] = [bestValue, 1]
            del best_pop
        x = [ruleNum for ruleNum, average in best.items() if average[1] > trial_num/2]
        y = [average[0]/average[1] for average in best.values() if average[1] > trial_num/2]
        ax.scatter(x, y, label = name)
        
    def setplotGenBest_tst(self, gen, ax, name = ""):
        best = {}
        for i, pop in enumerate(self.data_tri_gen.values()):
            data = self.getDataFrame(i, gen)
            df = data[data['f1'] != 1]
            Dtst_f1 = df[['Dtst', 'f1']]
            best_pop = {}
            for buf in Dtst_f1.itertuples():
                try:
                    if best_pop[buf.f1] > buf.Dtst:
                        best_pop[buf.f1] = buf.Dtst                        
                except:
                    best_pop[buf.f1] = buf.Dtst
            for RuleNum, bestValue in best_pop.items():
                try:
                    best[RuleNum][0] += bestValue
                    best[RuleNum][1] += 1                    
                except:
                    best[RuleNum] = [bestValue, 1]
            del best_pop
        x = [ruleNum for ruleNum, average in best.items() if average[1] > trial_num/2]
        y = [average[0]/average[1] for average in best.values() if average[1] > trial_num/2]
        ax.scatter(x, y, label = name)
        
class dataset():
    def __init__(self):
        print("dataset name:")
        self.datasetname = input()
        triangle_name = self.datasetname + "_triangle_result.xml"
        print(triangle_name)
        self.triangle = result(triangle_name)
        trapezoid_name = self.datasetname + "_trapezoid_result.xml"
        print(trapezoid_name)
        self.trapezoid = result(trapezoid_name)
        rectangle_name = self.datasetname + "_rectangle_result.xml"
        print(rectangle_name)
        self.rectangle = result(rectangle_name)
        gaussian_name = self.datasetname + "_gaussian_result.xml"
        print(gaussian_name)
        self.gaussian = result(gaussian_name)
        multi_name = self.datasetname + "_multi_result.xml"
        print(multi_name)
        self.multi = result(multi_name)
        
        self.plot_ave()
        self.plot_ave_tst()
        self.plot_best()
        self.plot_best_tst()
        self.plot_final_ave()
        self.plot_final_ave_tst()
        self.plot_final_best()
        self.plot_final_best_tst()
        
    def plot(self):
        genNum = len(self.multi.root.find("trial").findall("population"))
        fig = plt.figure(figsize = (24, ((genNum-1)/3+1)*6))
        fig.suptitle(self.datasetname + " result step", size = 24)        
        ax = []
        x_lim = [1000, -1]
        y_lim = [1000, -1]
        for i, pop in enumerate(self.multi.root.find("trial").findall("population")):
            dim = int(pop.get("generation"))
            buf = fig.add_subplot((genNum+2)/3, 3, i+1)
            ax.append(buf)
            ax[i].grid(True)
            self.triangle.setplotTrialGen(trial_plot, dim, ax[i], name = "triangle")
            self.trapezoid.setplotTrialGen(trial_plot, dim, ax[i], name = "trapezoid")
            self.rectangle.setplotTrialGen(trial_plot, dim, ax[i], name = "rectangle")
            self.gaussian.setplotTrialGen(trial_plot, dim, ax[i], name = "gaussian")
            self.multi.setplotTrialGen(trial_plot, dim, ax[i], name = "multi")            
            ax[i].legend(loc='upper right')
            ax[i].set_title("dim:" + str(dim))
            buf_x = ax[i].get_xlim()
            buf_y = ax[i].get_ylim()
            if buf_x[0] < x_lim[0]:
                x_lim[0] = buf_x[0]
            if buf_x[1] > x_lim[1]:
                x_lim[1] = buf_x[1]
            if buf_y[0] < y_lim[0]:
                y_lim[0] = buf_y[0]
            if buf_y[1] > y_lim[1]:
                y_lim[1] = buf_y[1]
        
        for buf in ax:
            buf.set_xlim(x_lim)
            buf.set_ylim(y_lim)
            buf.set_xticks(range(2, 100, int((x_lim[0]-x_lim[1])/10)))
            buf.set_xlabel("number of rule")
            buf.set_ylabel("error rate")
        figSave(fig, "result_step",self.datasetname)

    def plot_ave(self):
        ims = []
        x_lim = [1000, -1]
        y_lim = [1000, -1]
        for i, pop in enumerate(self.multi.root.find("trial").findall("population")):
            fig = plt.figure(figsize = (8, 6))
            fig.suptitle(self.datasetname + " [Dtra's average of all individual of each gen]", size = 24)        
            dim = int(pop.get("generation"))
            ax = fig.add_subplot(1, 1, 1)
            ax.grid(True)
            self.triangle.setplotGenAve(dim, ax, name = "triangle")
            self.trapezoid.setplotGenAve(dim, ax, name = "trapezoid")
            self.rectangle.setplotGenAve(dim, ax, name = "rectangle")
            self.gaussian.setplotGenAve(dim, ax, name = "gaussian")
            self.multi.setplotGenAve(dim, ax, name = "multi")      
            ax.legend(loc='upper right')
            ax.set_title("dim:" + str(dim))
            ims.append(fig)
            buf_x = ax.get_xlim()
            buf_y = ax.get_ylim()
            if buf_x[0] < x_lim[0]:
                x_lim[0] = buf_x[0]
            if buf_x[1] > x_lim[1]:
                x_lim[1] = buf_x[1]
            if buf_y[0] < y_lim[0]:
                y_lim[0] = buf_y[0]
            if buf_y[1] > y_lim[1]:
                y_lim[1] = buf_y[1]
        
        for i, fig in enumerate(ims):
            buf = fig.axes[0]
            buf.set_xlim(x_lim)
            buf.set_ylim(y_lim)
            buf.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
            buf.set_xlabel("number of rule")
            buf.set_ylabel("error rate[%]") 
            figSave(fig, self.datasetname + "_Result_Step_All_Dtra" + str(i), self.datasetname + '/ave')
        
    def plot_ave_tst(self):
        ims = []
        x_lim = [1000, -1]
        y_lim = [1000, -1]
        for i, pop in enumerate(self.multi.root.find("trial").findall("population")):
            fig = plt.figure(figsize = (8, 6))
            fig.suptitle(self.datasetname + " [Dtst's average of all individual of each gen]", size = 24)        
            dim = int(pop.get("generation"))
            ax = fig.add_subplot(1, 1, 1)
            ax.grid(True)
            self.triangle.setplotGenAve_tst(dim, ax, name = "triangle")
            self.trapezoid.setplotGenAve_tst(dim, ax, name = "trapezoid")
            self.rectangle.setplotGenAve_tst(dim, ax, name = "rectangle")
            self.gaussian.setplotGenAve_tst(dim, ax, name = "gaussian")
            self.multi.setplotGenAve_tst(dim, ax, name = "multi")      
            ax.legend(loc='upper right')
            ax.set_title("dim:" + str(dim))
            ims.append(fig)
            buf_x = ax.get_xlim()
            buf_y = ax.get_ylim()
            if buf_x[0] < x_lim[0]:
                x_lim[0] = buf_x[0]
            if buf_x[1] > x_lim[1]:
                x_lim[1] = buf_x[1]
            if buf_y[0] < y_lim[0]:
                y_lim[0] = buf_y[0]
            if buf_y[1] > y_lim[1]:
                y_lim[1] = buf_y[1]
        
        for i, fig in enumerate(ims):
            buf = fig.axes[0]
            buf.set_xlim(x_lim)
            buf.set_ylim(y_lim)
            buf.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
            buf.set_xlabel("number of rule")
            buf.set_ylabel("error rate[%]") 
            figSave(fig, self.datasetname + "_Result_Step_All_Dtst" + str(i), self.datasetname + '/ave_tst')

    def plot_best(self):
        ims = []
        x_lim = [1000, -1]
        y_lim = [1000, -1]
        for i, pop in enumerate(self.multi.root.find("trial").findall("population")):
            fig = plt.figure(figsize = (8, 6))
            fig.suptitle(self.datasetname + " [Dtra's average of best individual of each gen]", size = 24)        
            dim = int(pop.get("generation"))
            ax = fig.add_subplot(1, 1, 1)
            ax.grid(True)
            self.triangle.setplotGenBest(dim, ax, name = "triangle")
            self.trapezoid.setplotGenBest(dim, ax, name = "trapezoid")
            self.rectangle.setplotGenBest(dim, ax, name = "rectangle")
            self.gaussian.setplotGenBest(dim, ax, name = "gaussian")
            self.multi.setplotGenBest(dim, ax, name = "multi")      
            ax.legend(loc='upper right')
            ax.set_title("dim:" + str(dim))
            ims.append(fig)
            buf_x = ax.get_xlim()
            buf_y = ax.get_ylim()
            if buf_x[0] < x_lim[0]:
                x_lim[0] = buf_x[0]
            if buf_x[1] > x_lim[1]:
                x_lim[1] = buf_x[1]
            if buf_y[0] < y_lim[0]:
                y_lim[0] = buf_y[0]
            if buf_y[1] > y_lim[1]:
                y_lim[1] = buf_y[1]
        
        for i, fig in enumerate(ims):
            buf = fig.axes[0]
            buf.set_xlim(x_lim)
            buf.set_ylim(y_lim)
            buf.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
            buf.set_xlabel("number of rule")
            buf.set_ylabel("error rate[%]") 
            figSave(fig, self.datasetname + "_Result_Step_Best_Dtra" + str(i), self.datasetname + '/best')    
        
    def plot_best_tst(self):
        ims = []
        x_lim = [1000, -1]
        y_lim = [1000, -1]
        for i, pop in enumerate(self.multi.root.find("trial").findall("population")):
            fig = plt.figure(figsize = (8, 6))
            fig.suptitle(self.datasetname + " [Dtst's average of best individual of each gen]", size = 24)        
            dim = int(pop.get("generation"))
            ax = fig.add_subplot(1, 1, 1)
            ax.grid(True)
            self.triangle.setplotGenBest(dim, ax, name = "triangle")
            self.trapezoid.setplotGenBest(dim, ax, name = "trapezoid")
            self.rectangle.setplotGenBest(dim, ax, name = "rectangle")
            self.gaussian.setplotGenBest(dim, ax, name = "gaussian")
            self.multi.setplotGenBest(dim, ax, name = "multi")      
            ax.legend(loc='upper right')
            ax.set_title("dim:" + str(dim))
            ims.append(fig)
            buf_x = ax.get_xlim()
            buf_y = ax.get_ylim()
            if buf_x[0] < x_lim[0]:
                x_lim[0] = buf_x[0]
            if buf_x[1] > x_lim[1]:
                x_lim[1] = buf_x[1]
            if buf_y[0] < y_lim[0]:
                y_lim[0] = buf_y[0]
            if buf_y[1] > y_lim[1]:
                y_lim[1] = buf_y[1]
        
        for i, fig in enumerate(ims):
            buf = fig.axes[0]
            buf.set_xlim(x_lim)
            buf.set_ylim(y_lim)
            buf.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
            buf.set_xlabel("number of rule")
            buf.set_ylabel("error rate[%]") 
            figSave(fig, self.datasetname + "_Result_Step_Best_Dtst" + str(i), self.datasetname + '/best_tst')
            
#        genNum = len(self.multi.root.find("trial").findall("population"))
#        fig = plt.figure(figsize = (24, ((genNum-1)/3+1)*6))
#        fig.suptitle(self.datasetname + " [Dtst's average of best individual of each gen]", size = 36)        
#        ax = []
#        x_lim = [1000, -1]
#        y_lim = [1000, -1]
#        for i, pop in enumerate(self.multi.root.find("trial").findall("population")):
#            dim = int(pop.get("generation"))
#            buf = fig.add_subplot((genNum+2)/3, 3, i+1)
#            ax.append(buf)
#            ax[i].grid(True)
#            self.triangle.setplotGenBest_tst(dim, ax[i], name = "triangle")
#            self.trapezoid.setplotGenBest_tst(dim, ax[i], name = "trapezoid")
#            self.rectangle.setplotGenBest_tst(dim, ax[i], name = "rectangle")
#            self.gaussian.setplotGenBest_tst(dim, ax[i], name = "gaussian")
#            self.multi.setplotGenBest_tst(dim, ax[i], name = "multi")      
#            ax[i].legend(loc='upper right')
#            ax[i].set_title("dim:" + str(dim))
#            buf_x = ax[i].get_xlim()
#            buf_y = ax[i].get_ylim()
#            if buf_x[0] < x_lim[0]:
#                x_lim[0] = buf_x[0]
#            if buf_x[1] > x_lim[1]:
#                x_lim[1] = buf_x[1]
#            if buf_y[0] < y_lim[0]:
#                y_lim[0] = buf_y[0]
#            if buf_y[1] > y_lim[1]:
#                y_lim[1] = buf_y[1]        
#        
#        for buf in ax:
#            buf.set_xlim(x_lim)
#            buf.set_ylim(y_lim)
#            buf.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
#            buf.set_xlabel("number of rule")
#            buf.set_ylabel("error rate[%]")
#        figSave(fig, self.datasetname + "_Result_Step_Best_Dtst", self.datasetname)
        
    def plot_final(self):
        dim = gen_plot
        fig = plt.figure(figsize = (24, 18))
        fig.suptitle(self.datasetname + " result final", size = 24)        
        ax = fig.add_subplot(1, 1, 1)
        ax.grid(True)
        ax.set_xticks(range(2, 100, 1))
        self.triangle.setplotTrialGen(trial_plot, dim, ax, name = "triangle")
        self.trapezoid.setplotTrialGen(trial_plot, dim, ax, name = "trapezoid")
        self.rectangle.setplotTrialGen(trial_plot, dim, ax, name = "rectangle")
        self.gaussian.setplotTrialGen(trial_plot, dim, ax, name = "gaussian")
        self.multi.setplotTrialGen(trial_plot, dim, ax, name = "multi")            
        ax.legend(loc='upper right')
        ax.set_title("dim:" + str(dim))
        figSave(fig, "result_final")
        
    def plot_final_ave(self):
        dim = gen_plot
        fig = plt.figure(figsize = (8, 6))
        fig.suptitle(self.datasetname + " [Dtra's average of all individual of final gen]", size = 18)        
        ax = fig.add_subplot(1, 1, 1)
        ax.grid(True)
        self.triangle.setplotGenAve(dim, ax, name = "triangle")
        self.trapezoid.setplotGenAve(dim, ax, name = "trapezoid")
        self.rectangle.setplotGenAve(dim, ax, name = "rectangle")
        self.gaussian.setplotGenAve(dim, ax, name = "gaussian")
        self.multi.setplotGenAve(dim, ax, name = "multi")            
        ax.legend(loc='upper right')
        ax.set_title("dim:" + str(dim))
        x_lim = ax.get_xlim()
        ax.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
        ax.set_xlabel("number of rule")
        ax.set_ylabel("error rate[%]")
        figSave(fig, self.datasetname + "_Result_Final_All_Dtra", self.datasetname)     
        
    def plot_final_ave_tst(self):
        dim = gen_plot
        fig = plt.figure(figsize = (8, 6))
        fig.suptitle(self.datasetname + " [Dtst's average of all individual of final gen]", size = 18)        
        ax = fig.add_subplot(1, 1, 1)
        ax.grid(True)
        ax.set_xticks(range(2, 100, 2))
        self.triangle.setplotGenAve_tst(dim, ax, name = "triangle")
        self.trapezoid.setplotGenAve_tst(dim, ax, name = "trapezoid")
        self.rectangle.setplotGenAve_tst(dim, ax, name = "rectangle")
        self.gaussian.setplotGenAve_tst(dim, ax, name = "gaussian")
        self.multi.setplotGenAve_tst(dim, ax, name = "multi")            
        ax.legend(loc='upper right')
        ax.set_title("dim:" + str(dim))
        x_lim = ax.get_xlim()
        ax.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
        ax.set_xlabel("number of rule")
        ax.set_ylabel("error rate[%]")
        figSave(fig, self.datasetname + "_Result_Final_All_Dtst", self.datasetname)        

    def plot_final_best(self):
        dim = gen_plot
        fig = plt.figure(figsize = (8, 6))
        fig.suptitle(self.datasetname + " [Dtra's average of best individual of final gen]", size = 18)        
        ax = fig.add_subplot(1, 1, 1)
        ax.grid(True)
        ax.set_xticks(range(2, 100, 2))
        self.triangle.setplotGenBest(dim, ax, name = "triangle")
        self.trapezoid.setplotGenBest(dim, ax, name = "trapezoid")
        self.rectangle.setplotGenBest(dim, ax, name = "rectangle")
        self.gaussian.setplotGenBest(dim, ax, name = "gaussian")
        self.multi.setplotGenBest(dim, ax, name = "multi")            
        ax.legend(loc='upper right')
        ax.set_title("dim:" + str(dim))
        x_lim = ax.get_xlim()
        ax.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
        ax.set_xlabel("number of rule")
        ax.set_ylabel("error rate[%]")        
        figSave(fig, self.datasetname + "_Result_Final_Best_Dtra", self.datasetname)     
        
    def plot_final_best_tst(self):
        dim = gen_plot
        fig = plt.figure(figsize = (8, 6))
        fig.suptitle(self.datasetname + " [Dtst's average of best individual of final gen]", size = 18)        
        ax = fig.add_subplot(1, 1, 1)
        ax.grid(True)
        ax.set_xticks(range(2, 100, 2))
        self.triangle.setplotGenBest_tst(dim, ax, name = "triangle")
        self.trapezoid.setplotGenBest_tst(dim, ax, name = "trapezoid")
        self.rectangle.setplotGenBest_tst(dim, ax, name = "rectangle")
        self.gaussian.setplotGenBest_tst(dim, ax, name = "gaussian")
        self.multi.setplotGenBest_tst(dim, ax, name = "multi")            
        ax.legend(loc='upper right')
        ax.set_title("dim:" + str(dim))
        x_lim = ax.get_xlim()
        ax.set_xticks(range(2, int(x_lim[1]), lim(x_lim[0], x_lim[1])))
        ax.set_xlabel("number of rule")
        ax.set_ylabel("error rate[%]")        
        figSave(fig, self.datasetname + "_Result_Final_Best_Dtst", self.datasetname)   
            
class ruleset(XML):
    def __init__(self):
        print("dataset name:")
        self.datasetname = input()
        super(ruleset, self).__init__(self.datasetname + "_multi_ruleset.xml")
        self.rs_tri_gen = {}
        for i, trial in enumerate(self.root.findall('trial')):
            pop = {}
            for population in trial.findall('population'):
                pop[int(population.get('generation'))] = population_ruleset(population)
            self.rs_tri_gen[i] = pop
        self.ManbershipRate_coverAllClass()
        self.ManbershipRateAll_coverAllClass()
            
    def getIndividual(self, trial = 0, gen = 1000, ID = 0):
        return self.rs_tri_gen[trial][gen].individual[ID]
    
            
    def getPopulation(self, trial = 0, gen = 1000):
        return self.rs_tri_gen[trial][gen]
    
    def getKB(self, trial = 0, gen = 1000):
        return self.rs_tri_gen[trial][gen].kb
            
    def Manbership_coverAllClass(self):
        classNum = DatasetList[self.datasetname]["Class"]
        dimNum = DatasetList[self.datasetname]["Attribute"]
            
            
        buf = []
        ruleNum = 100
        for trial in self.rs_tri_gen.values():
            population = trial[gen_plot]
            for individual in population.individual:
                check = {i : False for i in range(classNum)}
                for l, SingleRule in result(individual.rules.values()):
                    check[SingleRule.conclusion] = True
                if(all(check.values())):
                    if len(individual.rules) < ruleNum:
                        ruleNum = len(individual.rules)
                    buf.append(individual)
        
        ruleNumBuf = ruleNum
        while True:
            rulelist = [ind for ind in buf if len(ind.rules) == ruleNumBuf]
            if len(rulelist) == 0:
                break
            #メンバーシップ関数グラフ
            fig = plt.figure(figsize = (24, ((dimNum+2)/3)*6))
            fig.suptitle(self.datasetname + " [used manbership that cover all classes at each num of rule]", size = 36)
            ax = []
            for dim in range(dimNum):
                tmp = fig.add_subplot((dimNum+2)/3, 3, dim+1)
                ax.append(tmp)
                ax[dim].grid(True)
                ax[dim].set_ylim(-0.17, 1.05)
                ax[dim].set_xlim(-0.05, 1.05)
                ax[dim].set_title("RuleDim" + str(dim))
            for tmp in rulelist:
                tmp.setAx(ax)
            figSave(fig, self.datasetname + "_Ruleset_Manbership_coverAllClasses_RuleNum_" + str(ruleNumBuf))
            rulelist.clear()
            del ax
            plt.close(fig)
            ruleNumBuf += 1
            
            
    def ManbershipRate_coverAllClass(self):
        classNum = DatasetList[self.datasetname]["Class"]
        dimNum = DatasetList[self.datasetname]["Attribute"]
            
            
        buf = []
        ruleNum = 100
        for trial in self.rs_tri_gen.values():
            population = trial[gen_plot]
            for individual in population.individual:
                check = {i : False for i in range(classNum)}
                for SingleRule in individual.rules.values():
                    check[SingleRule.conclusion] = True
                if(all(check.values())):
                    if len(individual.rules) < ruleNum:
                        ruleNum = len(individual.rules)
                    buf.append(individual)
           
        i = 0
        hight = []
        dimIndex = range(dimNum)
        ruleNum_tmp = ruleNum
        while True:
            hight.append({ i:{dim:0 for dim in range(dimNum)} for i in FuzzyTypeNum.keys()})
            rulelist = [ind for ind in buf if len(ind.rules) == ruleNum]
            if len(rulelist) == 0:
                break
            for ind in rulelist:
                for singlerule in ind.rules.values():
                    for dim, ID in singlerule.rule.items():
                        if ID == 0:
                            pass
                        else:
                            FuzzyTypeID = self.getKB(ind.trial, ind.gen).getFuzzyTermID(dim, ID).TypeID
                            hight[i][FuzzyTypeID][dim] += 1
            i += 1
            ruleNum += 1

        fig = plt.figure(figsize = (24, ((i+2)/3)*6))
        fig.suptitle(self.datasetname + " [rate of used manbership that cover all classes at each num of rule]", size = 36) 
        for j in range(i):
            ax =  fig.add_subplot((i+2)/3, 3, j+1)
            ax.set_title("RuleNum: " + str(j + ruleNum_tmp))
            bott = [0 for i in range(dimNum)]
            for typeID in FuzzyTypeNum.keys():
                hight_buf = [tmp for tmp in hight[j][typeID].values()]
                ax.bar(dimIndex, hight_buf, label=FuzzyTypeNum[typeID], bottom = bott)
                bott = [hight_buf[dim] + bott[dim] for dim in range(dimNum)]
            ax.grid(True)
            ax.set_xticks(range(0, dimNum, 1))
            ax.set_xlabel("dimension")
            ax.set_ylabel("uesd manbership rate")
        fig.legend(loc = 'lower center', labels = [str for str in FuzzyTypeNum.values()], fontsize = 30, ncol = len(FuzzyTypeNum))
        figSave(fig, self.datasetname + "_Ruleset_Manbership_coverAllClasses_EachRuleNum", self.datasetname)
        
        
    def ManbershipRateAll_coverAllClass(self):
        classNum = DatasetList[self.datasetname]["Class"]
        dimNum = DatasetList[self.datasetname]["Attribute"]
            
            
        buf = []
        ruleNum = 100
        for trial in self.rs_tri_gen.values():
            population = trial[gen_plot]
            for individual in population.individual:
                check = {i : False for i in range(classNum)}
                for SingleRule in individual.rules.values():
                    check[SingleRule.conclusion] = True
                if(all(check.values())):
                    if len(individual.rules) < ruleNum:
                        ruleNum = len(individual.rules)
                    buf.append(individual)
           
        hight = { i:{dim:0 for dim in range(dimNum)} for i in FuzzyTypeNum.keys()}
        for ind in buf:
            for singlerule in ind.rules.values():
                for dim, ID in singlerule.rule.items():
                    if ID == 0:
                        pass
                    else:
                        FuzzyTypeID = self.getKB(ind.trial, ind.gen).getFuzzyTermID(dim, ID).TypeID
                        hight[FuzzyTypeID][dim] += 1
                        
        dimIndex = range(dimNum)
        
        fig = plt.figure(figsize = (8, 6))
        fig.suptitle(self.datasetname + " [rate of used manbership that cover all classes]", size = 18) 
        ax =  fig.add_subplot(1, 1, 1)
        bott = [0 for i in range(dimNum)]
        for typeID in FuzzyTypeNum.keys():
            hight_buf = [tmp for tmp in hight[typeID].values()]
            ax.bar(dimIndex, hight_buf, label=FuzzyTypeNum[typeID], bottom = bott)
            bott = [hight_buf[dim] + bott[dim] for dim in range(dimNum)]
        ax.grid(True)
        ax.set_xticks(range(0, dimNum, 1))
        ax.set_ylabel("uesd manbership rate", size = 12)
        fig.legend(loc = 'lower center', labels = [str for str in FuzzyTypeNum.values()], fontsize = 12, ncol = len(FuzzyTypeNum), borderaxespad=1)
        figSave(fig, self.datasetname + "_Ruleset_Manbership_coverAllClasses_All", self.datasetname)
            
        