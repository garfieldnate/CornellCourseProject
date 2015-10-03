function [newy] = ylabel
y = load('y_train.csv');
newy = zeros(1953, 2);
% for i = 1: 1953
%     if y(i, 1) > 0 && y(i, 1) <=90 && y(i, 2) >= -90 && y(i, 2) <= 0
%         newy(i) = 1;
%     else if y(i, 1) >= 0 && y(i, 1) <= 90 && y(i, 2) > 0 && y(i, 2) <= 90
%         newy(i) = 2;
%         else if y(i, 1) >= -90 && y(i, 1) < 0 && y(i, 2) >= 0 && y(i, 2) <= 90
%             newy(i) = 3;
%             else if y(i, 1) >= -90 && y(i, 1) <= 0 && y(i, 2) >= -90 && y(i, 2) < 0
%                 newy(i) = 4;
%                 else
%                 newy(i) = 5;
%                 end
%             end
%         end
%     end
% end

for i = 1:1953
    switch y(i, 1)
        case {90, 75, 60}
            switch y(i, 2)
                case {-90, -75, -60, -45}
                    newy(i, 1) = 1;
                    newy(i, 2) = 1;
                case {-30, -15, 0}
                    newy(i, 1) = 1;
                    newy(i, 2) = 2;
                case {15, 30, 45}
                    newy(i, 1) = 2;
                    newy(i, 2) = 5;
                case {60, 75, 90}
                    newy(i, 1) = 2;
                    newy(i, 2) = 6;
            end
        case {15, 30, 45}
            switch y(i, 2)
                case {-90, -75, -60, -45}
                    newy(i, 1) = 1;
                    newy(i, 2) = 4;
                case {-30, -15, 0}
                    newy(i, 1) = 1;
                    newy(i, 2) = 3;
                case {15, 30, 45}
                    newy(i, 1) = 2;
                    newy(i, 2) = 8;
                case {60, 75, 90}
                    newy(i, 1) = 2;
                    newy(i, 2) = 7;
            end
        case {-30, -15, 0}
            switch y(i, 2)
                case {-90, -75, -60, -45}
                    newy(i, 1) = 3;
                    newy(i, 2) = 9;
                case {-30, -15, 0}
                    newy(i, 1) = 3;
                    newy(i, 2) = 10;
                case {15, 30, 45}
                    newy(i, 1) = 4;
                    newy(i, 2) = 13;
                case {60, 75, 90}
                    newy(i, 1) = 4;
                    newy(i, 2) = 14;
            end
        case {-45, -60, -75, -90}
            switch y(i, 2)
                case {-90, -75, -60, -45}
                    newy(i, 1) = 3;
                    newy(i, 2) = 12;
                case {-30, -15, 0}
                    newy(i, 1) = 3;
                    newy(i, 2) = 11;
                case {15, 30, 45}
                    newy(i, 1) = 4;
                    newy(i, 2) = 16;
                case {60, 75, 90}
                    newy(i, 1) = 4;
                    newy(i, 2) = 15;
            end
    end
end

for i = 1:1953
    if y(i, 1) == 0 && y(i, 2) == 0
        newy(i, 1) = 5;
        newy(i, 2) = 17;
    end
end

save('ylabel.mat', 'newy');