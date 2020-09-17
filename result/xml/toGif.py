from PIL import Image
import glob

filenameList = ['AveDtra', 'AveDtst', 'BestDtra', 'BestDtst']
print("dataset name:")
datasetName = input()
folderList = glob.glob('.\\result\\' + datasetName + '\\*')
for folder in folderList:
    for filename in filenameList:
        files = sorted(glob.glob(folder + "\\" + filename + '/*.png'))
        images = list(map(lambda file: Image.open(file), files))
        images[0].save(folder + '\\' + datasetName + '_' + filename + '.gif', save_all=True, append_images=images[1:], duration=400, loop=0)