import click
import numpy
import math

from PIL import Image, ImageFilter, ImageOps
from tkinter import Canvas, font, Tk


# CCIR 601
# Y = 0.299 R + 0.587 G + 0.114 B
# assume we are getting RGB input
#
# Alternatively, we can just call image.convert('L')
def gonvert_to_greyscale(input_image):
    image = numpy.array(input_image)
    #dot(a,b) 
    #If a is an N-D array and b is a 1-D array, it is a sum product over the last axis of a and b.
    image = numpy.dot(image, [0.299, 0.587, 0.114])
    #Normalize values
    image *= (255.0/image.max())
    return Image.fromarray(image.astype(numpy.uint8))


def convert_to_ascii(input_image):
    ascii_table = '$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/|()1{}[]?-_+~<>i!lI;:,"^`\'. '
    #ascii_table = ' .,:;ox%#@'
    table_len = len(ascii_table)

    res = []
    width, height = input_image.size
    for i in range(0, height):
       for j in range(0, width):
            res.append(ascii_table[math.ceil((table_len - 1) * input_image.getpixel((j, i)) / 255)])  
       res.append('\n')
    return ''.join(res)
  

def find_edges(input_image):
    #imageWithEdges = input_image.filter(ImageFilter.FIND_EDGES)
    #imageWithEdges = ImageOps.invert(imageWithEdges)
    
    # or
    
    imageWithEdges = input_image.filter(ImageFilter.SMOOTH)
    imageWithEdges = imageWithEdges.filter(ImageFilter.CONTOUR)
    return imageWithEdges
    

def canvas_write(input_image, canvas_width=800, canvas_height=600):
   
    def get_char_dimensions(canvas, selected_font):
        oneChar = canvas.create_text(20, 20, text=" ", font=selected_font)
        bounds = canvas.bbox(oneChar)  # returns a tuple like (x1, y1, x2, y2)
        char_width = bounds[2] - bounds[0]
        char_height = bounds[3] - bounds[1]
        print(f'Char width: {char_width} Char height: {char_height}')
        canvas.delete(oneChar)
        return (char_width, char_height)
    
    def resize_to_ratio(input_image, char_dimensions):
        width, height = input_image.size
        image_ratio = width  / height
        adjusted_width, adjusted_height = width, height
           
        if width * char_dimensions[0] > canvas_width:
            adjusted_width = math.floor(canvas_width / char_dimensions[0])
            adjusted_height = math.floor(adjusted_width / image_ratio)
         
        while adjusted_height * char_dimensions[1] > canvas_height:   
            adjusted_width = adjusted_width - 1
            adjusted_height = math.floor(adjusted_width / image_ratio)
        
        # Font has usually has higher height, so we increase width accordinly to avoid elongated images
        font_ratio = char_dimensions[0]/char_dimensions[1]
        adjusted_width = math.floor(adjusted_width / font_ratio)
        
        print(f'Width: {adjusted_width} Height: {adjusted_height}')
        resized_image = input_image.resize((adjusted_width, adjusted_height), Image.ANTIALIAS)
        return resized_image
          
    
    root = Tk()
    canvas = Canvas(root,  width=canvas_width, height=canvas_height)
    
    #Smaller font gives more details
    font_size = 4
    monospace_font = font.Font(family='Consolas', size=font_size)
    char_dimensions = get_char_dimensions(canvas, monospace_font)
    
    resized_input = resize_to_ratio(input_image, char_dimensions) 
    resized_input = find_edges(resized_input)    

    ascii_image = convert_to_ascii(resized_input)
    
    text_file = open("res.txt", "w")
    text_file.write(ascii_image)
    text_file.close() 

    # Draw image to canvas  
    drawn_text = canvas.create_text(canvas_width/2, canvas_height/2, text=ascii_image, font=monospace_font, anchor="c")
    
    text_bbox = canvas.bbox(drawn_text)
    rect_item = canvas.create_rectangle(text_bbox, outline="red", fill="white")  
    canvas.tag_raise(drawn_text,rect_item)

    canvas.pack()
    root.mainloop()


@click.command()
@click.argument('image_path')
@click.option('--ps', 'print_size', is_flag=True, default=False, help="Don't output original size")
def main(image_path, print_size):   
    data = Image.open(image_path).convert('RGB')
    
    if print_size:
        width, height = data.size
        print(f'Original Width: {width} Height: {height}')
    
    grayscale_data = gonvert_to_greyscale(data)
    canvas_write(grayscale_data)


if __name__ == "__main__":
    main()

