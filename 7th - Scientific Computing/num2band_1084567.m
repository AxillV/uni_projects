function band = num2band(array, kl, ku) % only works for nxn matrices
    N = size(array,1);
    band = zeros(kl+ku+1, N);
    
    % diagonal
    band(ku+1, :) = diag(array);
    
    %upper triagonal
    for n=1:ku
        band(ku + 1 - n, 1+n:N) = diag(array, n);
    end

    %lower triagonal
    for n=1:kl
        band(ku + 1 + n, 1:N-n) = diag(array, -n);
    end

end