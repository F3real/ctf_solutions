import cmath
import julia_fractal


from line_profiler import LineProfiler

# Set up arguments
width = 200
height = 200 
c = complex(-0.7, 0.27015)

lp = LineProfiler()
# Function which we are profiling
lp_wrapper = lp(julia_fractal.generate_julia_fractal)
# Arguments to function
lp_wrapper(width, height, c)
lp.print_stats()

"""
DOESN'T WORK WITH NUMBA

pipenv run pip install cython
pipenv run pip install git+https://github.com/rkern/line_profiler#egg=line_profiler

To profile additional functions, add before lp(func_name):
lp.add_function(func_name)
"""