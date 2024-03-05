clear; 

tol=1e-8; 
nd=3; 
rng(0); 
err=zeros(1, nd+2);
ndim=[2,3,4]; 
Atemp=randi(5,ndim); 
Btemp=randi(4,ndim);
X=randi([-1,1], 5, max(ndim)); 
A=tensor(Atemp); 
B=tensor(Btemp);

try
    for k=1:nd
        % frobenius norm of absolute differences for error
        %err(k) = norm(ttm_1084567(Atemp,X(5, 1:ndim(k)), k) - double(ttm(A,X(5, 1:ndim(k)), k)));
        err(k) = norm(abs(ttm_1084567(Atemp,X(5, 1:ndim(k)), k) - double(ttm(A,X(5, 1:ndim(k)), k))), 'fro');
    end
    assert(max(err<tol), 'ttm modal multiplication fails')
    catch ME1
end

try
    err(nd+1) = norm(tensor(ttt_1084567(Atemp, Btemp)) - ttt(A,B));
    catch ME2
end

try
    err(nd+2) = abs(ttt_1084567(Atemp, Btemp, 'all') - double(ttt(A,B,[1:nd])));
    catch ME3
end

if exist('ME1')
    ME1.message
end

if exist('ME2')
    ME2.message
end

if exist('ME3')
    ME3.message
end