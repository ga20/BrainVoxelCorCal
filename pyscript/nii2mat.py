import os
import numpy as np
import nibabel as nib
import scipy.io as io
path = "D:\mycode\体素分析\MyAp\\nii\Filtered_4DVolume.nii"
img = nib.load(path)
npdata = np.asanyarray(img.dataobj)
target_path = "D:\mycode\体素分析\MyAp\\nii\\Filtered_4DVolume.mat"
io.savemat(target_path, {'myniidata': npdata})
