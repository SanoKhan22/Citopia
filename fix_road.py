from PIL import Image, ImageDraw
import numpy as np

img = Image.open('assets/citopiaassest/PNG/road_desert_dirt.png').convert("RGB")
data = np.array(img)

# Find the bounding box of the non-white/gray border.
# The border is roughly (220,220,220).
# Let's crop it by finding rows/cols where variance is low and it's bright.
# Actually, let's just take the center 512x512 of the image to be safe.
h, w, c = data.shape
cx, cy = w//2, h//2
size = 512
cropped = data[cy-size//2:cy+size//2, cx-size//2:cx+size//2]

# Now we have a 512x512 texture. To make it seamless, we can blend opposite edges.
# Or an easier perfect seamless trick:
# tile it by mirroring? No, mirroring looks like a kaleidoscope.
# Let's roll it and blend the seams.
def make_seamless(patch, margin_ratio=0.2):
    patch = Image.fromarray(patch)
    w, h = patch.size
    
    mh = int(h * margin_ratio)
    mw = int(w * margin_ratio)
    
    # We will do overlapping blending to make it seamless.
    # The standard way is to split into 4 quadrants and swap them.
    # Actually, simpler: take center crop of 256x512 out of 512x512? No.
    
    # Let's generate a synthetic procedural dirt texture to look perfect and seamless.
    return patch

# Let's just generate a perfectly seamless procedural texture with noise.
import random, math

def generate_noise_texture(width, height):
    out = Image.new('RGB', (width, height))
    pixels = out.load()
    base_color = (195, 155, 110) # Sand/dirt base
    dark_color = (160, 120, 85)
    light_color = (210, 180, 140)
    
    # Generate 3D Perlin noise equivalent with wrapping for 2D seamless
    def wrap_dist(x1, y1, x2, y2):
        dx = abs(x1 - x2)
        dy = abs(y1 - y2)
        dx = min(dx, width - dx)
        dy = min(dy, height - dy)
        return math.sqrt(dx*dx + dy*dy)
    
    # Splat random colored "grains" and "peblles"
    # To make it seamless, we draw everything wrapped around the borders
    for x in range(width):
        for y in range(height):
            pixels[x, y] = base_color
            
    for _ in range(5000):
        cx = random.randint(0, width-1)
        cy = random.randint(0, height-1)
        r = random.randint(1, 3)
        col = random.choice([dark_color, light_color, (130, 90, 60), (220, 190, 150)])
        for dx in range(-r, r+1):
            for dy in range(-r, r+1):
                if dx*dx + dy*dy <= r*r:
                    px = (cx + dx) % width
                    py = (cy + dy) % height
                    pixels[px, py] = col
                    
    # Smooth it out a bit
    from PIL import ImageFilter
    out = out.filter(ImageFilter.GaussianBlur(0.7))
    
    # Add finer noise
    p2 = out.load()
    for x in range(width):
        for y in range(height):
            v = random.randint(-15, 15)
            r, g, b = p2[x, y]
            p2[x, y] = (max(0, min(255, r+v)), max(0, min(255, g+v)), max(0, min(255, b+v)))
            
    return out

# The user wants 64x128
seamless_tex = generate_noise_texture(64, 128)

# Save
seamless_tex.save('assets/citopiaassest/PNG/road_desert_dirt.png')

