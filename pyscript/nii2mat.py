'''

This script help you transfer NIFTI file to .mat file.
See https://brainder.org/2012/09/23/the-nifti-file-format/ about NIFTI file.

'''

import os
import numpy as np
import nibabel as nib
import scipy.io as io
path = "nii\\Filtered_4DVolume.nii"
img = nib.load(path)
npdata = np.asanyarray(img.dataobj)
target_path = "nii\\Filtered_4DVolume.mat"
io.savemat(target_path, {'myniidata': npdata})
