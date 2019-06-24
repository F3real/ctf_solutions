#Profile:
#python -m cProfile -s cumulative -o p.stats .\julia_fractal.py

import pstats

p = pstats.Stats("p.stats")
p.sort_stats("cumulative")
p.print_stats()
#p.print_callers()
#p.print_callees()