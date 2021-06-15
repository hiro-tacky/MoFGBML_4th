import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from pandas import Series, DataFrame
import sklearn.datasets as datasets
import math

print('make a Dataset object and type a dataset name. ex) iris = Dataset("iris")')
print('dataset name list: "iris", "boston", "diabetes", "digits", "linnerud", "wine", "breast_cancer"')

class Dataset:
    def __init__(self, dataset_name):
        if dataset_name == 'iris': data = datasets.load_iris()
        if dataset_name == 'boston': data = datasets.load_boston()
        if dataset_name == 'diabetes': data = datasets.load_diabetes()
        if dataset_name == 'digits': data = datasets.load_digits()
        if dataset_name == 'linnerud': data = datasets.load_linnerud()
        if dataset_name == 'wine': data = datasets.load_wine()
        if dataset_name == 'breast_cancer': data = datasets.load_breast_cancer()
        self.df = DataFrame(data.data, columns = data.feature_names) #datasetのDataFrame
        print(self.df)
        print('columns: ' + str(list(self.df.columns)))
        self.frequency_distribution('sepal length (cm)')
        
    def frequency_distribution(self, column_input):
        print('\nfrequency_distribution')
        if type(column_input) is int: series = self.df.iloc[:, column_input]
        if type(column_input) is str: series = self.df.loc[:, column_input]
        n = series.size #観測値の数
        k = round(1 + math.log(n, 2)) #Sturges' rule: 階級の数を決定する関数
        print("Sturges' rule: 階級の数を決定する関数 (k:階級数, n:標本数)")
        print("k = 1 + log2(n)")
        print("k(=" + str(k) + ") = 1 + log2(n(=" + str(n) + "))")
        class_data = [0]*k
        min_value = series.min()
        class_width = (series.max() - min_value) / k
        for tmp in series - min_value:
            index_tmp = int(tmp // class_width)
            if index_tmp == k :class_data[k-1] += 1
            else: class_data[index_tmp] += 1
        cumulative_frequency, cumulative_relative_frequency = 0, 0
        df_class_data = DataFrame(columns = ['min', 'max', 'class', 'frequency', 'relative frequency', 'cumulative_frequency', 'cumulative_relative_frequency'])
        for i in range(k):
            buf = [series.min() + class_width*i, series.min() + class_width*(i+1)]
            buf.append(series.min() + class_width*(i+0.5))
            buf.append(class_data[i])
            buf.append(class_data[i]/n)
            cumulative_frequency += class_data[i]
            buf.append(cumulative_frequency)
            cumulative_relative_frequency += class_data[i]/n
            buf.append(cumulative_relative_frequency)
            buf_series = Series(buf, index = df_class_data.columns)
            df_class_data = df_class_data.append(buf_series, ignore_index=True)
        print(df_class_data)
            
    def histogram(self):
        plt = 
        