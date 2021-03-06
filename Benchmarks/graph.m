t=0:20*3;
ideal=sin(t*pi/(10));
start=[-0.3 -0.2];
newpoint=start;
depoint=newpoint;
for i=3:length(t)
    newpoint(i)=depoint(i-1)+(depoint(i-1)-depoint(i-2));
    if newpoint(i)<= ideal(i)
        depoint(i)=newpoint(i)+0.2*abs(newpoint(i)-ideal(i));
    elseif newpoint(i)> ideal(i)
        depoint(i)=newpoint(i)-0.2*abs(newpoint(i)-ideal(i));
    end
end

newpoint1=[-0.3 -0.2 0.02 ];
depoint1=newpoint1;
for i=4:length(t)
    x=abs(depoint1(i-1)-depoint1(i-2))-abs(depoint1(i-2)-depoint1(i-3));
    if rand <0.5
          epsilon=1+tanh(x);%+abs(randn);
    else
          epsilon=1+(x)*abs(randn);
    end
    newpoint1(i)=depoint1(i-1)+epsilon*(depoint1(i-1)-depoint1(i-2));
    if newpoint1(i)<= ideal(i)
        depoint1(i)=newpoint1(i)+0.2*abs(newpoint1(i)-ideal(i));
    elseif newpoint1(i)> ideal(i)
        depoint1(i)=newpoint1(i)-0.2*abs(newpoint1(i)-ideal(i));
    end
end

newpoint2=[-0.3] ;
depoint2=newpoint2;
for i=2:length(t)

    if depoint2(i-1)<= ideal(i-1)
        depoint2(i)=depoint2(i-1)+0.2*abs(depoint1(i-1)-ideal(i-1));
    elseif depoint2(i-1)> ideal(i-1)
        depoint2(i)=depoint2(i-1)-0.2*abs(depoint1(i-1)-ideal(i-1));
    end
end

plot(t,ideal,'y.-',t,depoint,'r*-',t,depoint1,'g^-',t,depoint2,'o',t,newpoint,'bo',t,newpoint1,'b+');

error=sum((ideal-depoint).^2)
error=sum((ideal-depoint1).^2)
error=sum((ideal-depoint2).^2)
