function angle_knn
xdata = load('X_train.csv');
ydata = importdata('y16.mat');
xfeature = importdata('y16_feature.mat');
newxdata = xdata(:, xfeature);

b = randperm(1953);
xtrain = newxdata(b(1:1560), :);
ytrain = ydata(b(1:1560), :);
xnorm = (xtrain - min(min(xtrain))) / (max(max(xtrain)) - min(min(xtrain)));
accuracy = zeros(100, 1);

for k = 1:100    
    Mdlk = fitcknn(xnorm, ytrain, 'NumNeighbors', k, 'Distance', 'cosine');
    cvmdl = crossval(Mdlk, 'KFold', 5);
    kloss = kfoldLoss(cvmdl);
    accuracy(k) = 1 - kloss;
    disp(k);
    disp(kloss);
    %plot(k, kloss);
    hold on;
end

plot([1:100], accuracy);