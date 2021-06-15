from PIL import Image
import glob
import os

filenameList = ['AveDtra', 'AveDtst', 'BestDtra', 'BestDtst']
print("dataset name:")
datasetName = input()
print("folder name:")
folderName = input()
folderList = glob.glob('./result/' + datasetName + '/result/*/')
for folder in folderList:
    for filename in filenameList:
        files = sorted(glob.glob(folder + "/" + filename + '/' + folderName + '/*.png'))
        images = list(map(lambda file: Image.open(file), files))
        os.makedirs(folder + folderName + '/', exist_ok=True)
        images[0].save(folder + folderName + '/' + datasetName + '_' + filename + '.gif', save_all=True, append_images=images[1:], duration=400, loop=0)