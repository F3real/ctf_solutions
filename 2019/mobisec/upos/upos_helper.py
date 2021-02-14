import re

with open("FC.smali") as file:

    data = file.read()
   
    #r means it's a raw string
    #Adding a ? on a quantifier (?, * or +) makes it non-greedy
    methods = re.findall(r"^\.method(.*?)\.end method", data, re.MULTILINE | re.DOTALL)
   
    for idx, method in enumerate(methods):
        m = [s.strip() for s in method.splitlines()]
        methods[idx] = m
    
    
    print(f"Number of methods: {len(methods)}")
    print(f"Length of checkFlag: {len(methods[2])}")
    
    try_start = []
    try_end   = []
    
    open_try = 0
    
    for idx, line in enumerate(methods[2]):
        if line.startswith(":try_start"):
            try_start.append(idx)
            open_try += 1
            if open_try != 1:
                print("Nested try catch, detected!!!")
        if line.startswith(":try_end"):
            try_end.append(idx)
            open_try -= 1
    
    print(f"Number of try/catch blocks: {len(try_start)}")
    
    for i in range(len(try_start)):
        #print(f"Try/catch: {try_start[i]} {try_end[i]}")
        
        for i in range(try_start[i], try_end[i]+1):
            print(methods[2][i])
        input("paused...")
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            