import json
import logging

from Queue import Queue
from PIL import Image

logging.basicConfig(level=logging.DEBUG)

# Color defines
BLACK = (0, 0, 0)
DODGER_BLUE = (30, 144, 255)
GRAY = (128, 128, 128)
GREEN_YELLOW = (173, 255, 47)
MOCCASIN = (255, 228, 181)
ORANGE = (255, 165, 0)
WHITE = (255, 255, 255)
YELLOW = (255, 255, 0)
YELLOW_GREEN = (154, 205, 50)
LIME = (0,255,0)

# Tile type defines
GRASS = 1
WATER = 2
CAVE = 3
SAND = 4
FOREST = 5
WALKABLE_INDOORS = 6
WALKABLE_OUTDOORS = 7
WALLS = 8

COLOR_MAPINGS = {
    WATER: DODGER_BLUE,
    WALKABLE_INDOORS: MOCCASIN,
    GRASS: GREEN_YELLOW,
    SAND: YELLOW,
    WALLS: BLACK,
    FOREST: YELLOW_GREEN,
    WALKABLE_OUTDOORS: ORANGE,
    CAVE: GRAY
}

MOVES = [ 
    (0,1),  #right
    (0,-1), #left
    (-1,0), #north
    (1,0)   #south
]

# Location of start and end pixel
START = (11, 523)
END = (309, 597)

# Map size defines
MAZE_WIDTH = 512
MAZE_HEIGHT = 768
MAZE_SIZE = (MAZE_WIDTH * MAZE_HEIGHT)


def idx(x, y):
    return (x + y * 512)


def load_map():
    with open('map.json') as f:
        maze = json.load(f)['terrain']
        logging.debug(maze)
    return bytearray(maze.decode('base64').decode('zlib'))


def get_map(maze):
    img = Image.new('RGB', (MAZE_WIDTH, MAZE_HEIGHT), "white")
    data = img.load()  # Get pixel acess object
    for x in range(MAZE_WIDTH):
        for y in range(MAZE_HEIGHT):
            data[x, y] = COLOR_MAPINGS.get(maze[idx(x, y)], WHITE)
    return img

# maze is Image object
def mark_solution(maze, solution):
    data = maze.load()  # Get pixel acess object
    for x, y in solution:
            data[x, y] = LIME
    return maze

def solve_maze_recursive(maze, pos):
    i = idx(*pos)

    if maze[i] != WALKABLE_INDOORS:
        return None
    
    # Mark tile as visited
    maze[i] = 0

    for move in MOVES:
        new_pos = (pos[0] + move[0], pos[1] + move[1])

        if new_pos == END:
            return [new_pos]

        ret = solve_maze_recursive(maze, new_pos)
        if ret is not None:
            ret.append(pos)
            return ret


# BFS
def solve_maze(maze):
    
    queue = Queue()
    queue.put([START])

    while not queue.empty():

        path = queue.get() 
        pos = path[-1]

        if pos == END:
            return path

        for move in MOVES:
            new_pos = (pos[0] + move[0], pos[1] + move[1])
            i = idx(*new_pos)
            if maze[i] == WALKABLE_INDOORS:
                # Mark tile as visited
                maze[i] = 0 
                new_path = list(path)
                new_path.append(new_pos)
                queue.put(new_path)


maze = load_map()
logging.debug('Maze size %d' % len(maze))
logging.debug('Assumed maze size %d' % MAZE_SIZE)
img = get_map(maze)
res = solve_maze(maze)
logging.debug('Number of steps in solution %d' % len(res))

img.save('start.png')
last = 0
for x in range(0, len(res), 500):
    img = mark_solution(img, res[0:x])
    area = (6, 520, 312, 675)
    cropped_img = img.crop(area)
    cropped_img.save('%d.png' % x)
    last = x
img = mark_solution(img, res[last:])
cropped_img = img.crop(area)
cropped_img.save('%d.png' % x)
img.save('final.png')

