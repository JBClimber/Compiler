// Program.sl 2nd

main()
var x, y, inport, outport;
proc up,down,delay,display;

x:=0;
call up
call down
call display
end

up()
if inport==1 then x:=x+1;
if x>=10 then x:=0;
end

down()
if inport==2 then x:=x-1;
if x>=10 then x:=10;
end

display()
outport:=x;
call delay
end

delay()
y:=50;
while y>=0 do y:=y-1;
end