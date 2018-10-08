// PROGRAM3.SL

main()
var x, y, inport, outport;
proc up, display, delay;
while x>=0 do {call up call display}
end

up()
if inport==1 then x:=x+1;
if x>=10 then x:=0;
end

display()
outport:=x;
call delay
end

delay()
y:=20;
end