
% You can this code to build 3-d array from 1-d result from 1-d output


% load the out put 1-d array 
load('res.mat');
% load your mask matrix
load('D:\mycode\ÌåËØ·ÖÎö\MyAp\Mask\mask.mat');
% voxel dimension is same with mask
z = size(mask, 1);
y = size(mask, 2);
x = size(mask, 3);
count = 1;
img = zeros(x, y, z);
for i = 1:x
   for j = 1:y
       for k = 1:z
           img(i, j, k) = meancorelationmat(count);
           count = count + 1;
       end
   end
end
% verify whether the maximum the of the 1-d array is between -1 and 1
sprintf("Max mean correlation value  £º %d" , max(meancorelationmat))
