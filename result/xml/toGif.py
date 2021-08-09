from PIL import Image
import glob
import os

# filenameList = ['AveDtra', 'AveDtst', 'BestDtra', 'BestDtst']
# print("dataset name:")
# datasetName = input()
# print("folder name:")
# folderName = input()
# folderList = glob.glob('.\\result\\' + datasetName + '\\result\\*\\')
# for folder in folderList:
#     for filename in filenameList:
#         files = sorted(glob.glob(folder + "\\" + filename + '\\' + folderName + '\\*.png'))
#         images = list(map(lambda file: Image.open(file), files))
#         os.makedirs(folder + folderName + '\\', exist_ok=True)
#         images[0].save(folder + folderName + '\\' + datasetName + '_' + filename + '.gif', save_all=True, append_images=images[1:], duration=400, loop=0)
        
print("dataset name:")
datasetName = input()
folderList = glob.glob('gen_max\\result\\' + datasetName + '\\trial_*')
savePath = "gen_max\\result\\gif\\" + datasetName
for folder in folderList:
    fileList = glob.glob(folder + '\\gen_*\\UsedMenbershipRate\\AllIndividuals\\dim_*\\*.png')
    files_buf = {"dim_" + str(i): [] for i in range(5)} #属性数によって変更しろ
    for filename in fileList:
        files_buf[filename.split('\\')[-2]].append(filename)
    for dim, files in files_buf.items():
        images = list(map(lambda file: Image.open(file), files))
        os.makedirs(savePath + '\\' + dim, exist_ok=True)
        images[0].save(savePath + '\\' + dim + '\\' + 'gen_max.gif', save_all=True, append_images=images[1:], duration=50, loop=0)