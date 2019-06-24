import cmath
import matplotlib.pyplot as plt
import numpy as np

from numba import jit, prange
from timeit import default_timer as timer


LIMIT = 2

# Numba translates Python functions to optimized machine code at runtime using the LLVM compiler 
# @jit decorator specifies function to optimize
# nopython - compilation mode that generates code that does not access the Python C API (best performance)
# parallel=True works together with prange and enables multiple threads
@jit(nopython=True, parallel=True)
def generate_julia_fractal(img_width, img_height, c, max_iterations=256, zoom=1):
  
    pixels = np.empty((img_height, img_width), dtype=np.uint16)
    
    # area of complex space to investigate
    x_min, x_max, y_min, y_max = -2.5, 2.5, -2.5, 2.5
    x_width = x_max - x_min
    y_width = y_min - y_max
    
    # Only used for option 3
    r = np.sqrt((img_height/2)**2  + (img_width/2)**2)
    
    for i in prange(img_width):
        for j in range(img_height):

            # translate pixel (x,y) to z       
            #z = complex(x_max*(i - img_width/2)/(0.5*zoom*img_width), y_min*(j - img_height/2)/(0.5*zoom*img_height))
            #z = complex((i / img_width * x_width + x_min) * 1/zoom, (j / img_height * y_width - y_min) * 1/zoom)
            z = complex(x_max*(i - img_width/2)/(r*zoom), y_min*(j - img_height/2)/(r*zoom))
            
            # Julia quadratic iteration
            # f(z) = z^2 + c
            iteration = 0
            while iteration < max_iterations and abs(z) < LIMIT:
                z = z**2 + c
                iteration += 1
                
            # Determine color based on number of iterations    
            pixels[j, i] = iteration
    
    return pixels

def display_fractal(pixels, save_fractal=0):
    print(f'Backend {plt.get_backend()}')
    
    # Start fullscreen on TkAgg backend
    figManager = plt.get_current_fig_manager()
    figManager.window.state('zoomed')
    
    
    plt.axis('off')
    plt.imshow(pixels,
               cmap='twilight_shifted', #nipy_spectral_r
               origin='lower')
    
    # Saving fractal takes a while
    if save_fractal:
        fig = plt.gcf()
        fig.set_size_inches(19.20, 10.80, forward=False)
        fig.savefig('res.svg', dpi=1200)
    plt.show()
    

    

if __name__ == "__main__":
    width = 1024 * 8
    height = 1024 * 8
    #c = complex(-0.1, 0.65)
    c = complex(-0.7, 0.27015)
    #c = complex(0.355, 0.355)
    #c = complex(-0.35, 0.65)
    #c = complex(-0.54, 0.54)
    #c = complex(0.34, -0.05)
    
    start = timer()
    pixels = generate_julia_fractal(width, height, c)
    end = timer()   
    
    display_fractal(pixels) 
    print(f'Generating Julia fractal took: {end - start}')
 
""" 
    Each pixel is translated to complex space based on coordinates:
    
    Option 1
    pixel(x,y)   ->  x/image_width                        scale x between (0, 1)
                     * (complex space len min to max)     scales x between (0, len_complex)
                     + (min)                              translate value to point in complex space (min, max)
                 ->  same thing for y except, since coordinates of picture start at (0,0) top left corner, we have
                     to translate 0 to max positive value.
                     We calculate complete space length as (min - max) and we subtract min to get top left corner
                     when translated.
                     
                 
                 We can change zoom level by modifying above formula to:
                 (x/image_width  * (complex space len min to max)  - abs(min)) * 1/zoom
    
    Option 2
    pixel(x,y)   ->  (x -  (image_width / 2)              center value of x around 0 (-img_width/2, img_width/2)
                     / (0.5 * img_width))                 scale value between (-1, 1)
                     * (half len of complex space)        translate value to point in complex space (min, max)               
                 ->  same thing for y, except for same reason as in Option 1, we multiply by
                     * -(half len of complex space)

                 We can change zoom level by modifying above formula to:
                 ((x -  (image_width / 2))  / (0.5 * img_width * zoom)) * (half width of complex space)
                 
    
    Option 3:
    pixel(x,y)   ->  (x -  (image_width / 2)  / r
                 ->  same thing for y 
                 
    r = sqrt((image_width / 2) ^2 + (img_height/2)^2 )
    Translates pixel coordinates to coordinates inside circle with radius r.
    
     
    Zoom is achieved by modifying value x after translation to complex space to x/zoom
    
    
    The complex number belongs to the Mandelbrot set if the sequence stays within a certain radius from
    the origin (usually cutoff is 2) and doesn't escape to infinity. 
    This mean that entire width of complex space should be 2 * cutoff.
    We can check if number belongs to the set by iterating the formula. Since the iteration may never end we set a maximum.
    
    Real space options seen:
    -1.8 to 1.8, same for y
    -2 to 2, same for y
    -1.5 to 1.5, same for y
    -1.5 to 1.5, -1 to 1
"""    