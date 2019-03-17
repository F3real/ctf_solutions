from functools import reduce
from operator import mul
from math import gcd

#From Rosetta code
def extended_gcd(aa, bb):
    lastremainder, remainder = abs(aa), abs(bb)
    x, lastx, y, lasty = 0, 1, 1, 0
    while remainder:
        lastremainder, (quotient, remainder) = remainder, divmod(lastremainder, remainder)
        x, lastx = lastx - quotient*x, x
        y, lasty = lasty - quotient*y, y
    return lastremainder, lastx * (-1 if aa < 0 else 1), lasty * (-1 if bb < 0 else 1)

def modinv(a, m):
	g, x, y = extended_gcd(a, m)
	if g != 1:
		raise ValueError
	return x % m

    
a  = [213, 229, 25, 83, 135]
m  = [27644437, 10459, 1489, 1046527, 16127]


#Check if all numbers are coprime
for i in range(0, len(m)):
    for j in range(i + 1, len(m)):
        if gcd(m[i], m[j]) != 1:
            print("Numbers are not coprime.")
            quit()

#Calculate solution            
M = reduce(mul, m, 1)
zi = [M//x for x in m];
yi = [modinv(x,y) for x,y in zip(zi,m)]  
wi = [x*y%M for x,y in zip(zi,yi)]
solution = [x*y for x,y in zip(wi,a)]
solution = sum(solution) % M

print(f"Solution: {solution}")