from PIL import Image

# constructs texture files from fragments for the band of arcana
# instead of changing the textures directly, change the fragments and re-run this script
base = Image.open("./base/ring.png")
for m in range(10): # mode
    for c in range(2): # cov
        for l in range(2): # liquid
            for w in range(2): # woft
                ring = base.copy()
                mode = Image.open(f'./base/mode/{m}.png').copy()
                cov = Image.open(f'./base/cov/{c}.png').copy()
                if m == 4:
                    liquid = Image.open(f'./base/liquid/fg/{l}.png').copy()
                else:
                    liquid = Image.open(f'./base/liquid/bg/{l}.png').copy()
                if m == 2:
                    woft = Image.open(f'./base/woft/fg/{w}.png').copy()
                else:
                    woft = Image.open(f'./base/woft/bg/{w}.png').copy()
                name = f'./mode{m}_cov{c}_liquid{l}_woft{w}.png'
                ring.alpha_composite(mode)
                ring.alpha_composite(cov)
                ring.alpha_composite(liquid)
                ring.alpha_composite(woft)
                ring.save(name)