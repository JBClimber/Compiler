//Program

main()
var x,y,z,k,port;
proc display, delay;

x:=16;
z:=(-16);
y := 16-(16+z);
// z:=y;
z:=+x-(y-16)+((-z)-(-16))+(x+16+z);//assign stmt
if y==0 then x:=16;//if stmt
if x>=(16-z)then { if z==16 then x:=16+16;}
while x>=-16 do {x:=x-16; y:=+16;}//composite stmt
while z==16 do {if x==16 then { while y==16 do { if x==15 then{z:=16; x:=16;}}}}
call display
end

display ()
x := 16;
end