%%
clear; clc;

% Calculate Cholesky's run time
time_taken_1 = chol_timeit(100:100:2000);
time_taken_2 = chol_timeit(150:100:1550);

for i=2:4
    
    % Interpolate results
    [p, S, mu] = polyfit(100:100:2000, time_taken_1, i);
    
    % Create figure of results
    figure
    plot(100:100:2000, time_taken_1, 'o')
    hold on
    plot(100:100:2000, polyval(p, 100:100:2000, S, mu))
    title(((i-1)*2 - 1) + ". Cholesky run time estimations from points 100:100:2000, polyonym degree " + i)
    legend("Actual time taken", "Interpolated time taken")
    hold off
    
    % Interpolate new results based on old interpolations
    figure
    plot(150:100:1550, time_taken_2, 'o')
    hold on
    plot(150:100:1550, polyval(p, 150:100:1550, S, mu))
    title((i-1)*2 + ". Cholesky run time estimations from points 100:100:2000, polyonym degree " + i)
    legend("Actual time taken", "Interpolated time taken")
    hold off
end